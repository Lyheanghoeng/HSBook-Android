package com.hsbook.fragment;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.hsbook.MainActivity;
import com.hsbook.R;
import com.hsbook.Retrofit.Interface.HomeProfileService;
import com.hsbook.Retrofit.ServiceManager;
import com.hsbook.Retrofit.ServiceMangerCallback;
import com.hsbook.adpater.BookListAdapter;
import com.hsbook.model.BookModel;
import com.hsbook.model.BookResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Response;

public class HomeFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private SearchView searchView = null;
    private SearchView.OnQueryTextListener queryTextListener;

    private ProgressBar progressBar;
    private TextView txtNotFound;
    private RecyclerView bookRecycleView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView.Adapter bookAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private List<BookModel> searchList = new ArrayList<>();

    private OnFragmentInteractionListener mListener;

    public HomeFragment() {
        // Required empty public constructor
    }

    // Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        bookRecycleView = Objects.requireNonNull(getView()).findViewById(R.id.frm_home_recycler_book);
        mSwipeRefreshLayout = getView().findViewById(R.id.frm_home_swipe_container);
        progressBar = (getView()).findViewById(R.id.frm_home_progress);
        txtNotFound = (getView()).findViewById(R.id.frm_home_txtNotFound);

        layoutManager = new LinearLayoutManager(getContext());
        bookRecycleView.setLayoutManager(layoutManager);
        bookRecycleView.setHasFixedSize(true);
        bookAdapter = new BookListAdapter(MainActivity.bookListMain, getActivity());
        bookRecycleView.setAdapter(bookAdapter);
        setHasOptionsMenu(true);

        if (MainActivity.bookListMain.size() < 1) {
            progressBar.setVisibility(View.VISIBLE);
            if (isConnected()) {
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //Do something after 100ms
                        getBookList();
                    }
                }, 1000);
            } else {
                Toast.makeText(getContext(), "No Internet connection", Toast.LENGTH_LONG).show();
            }
        }

        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {

            }
        });
    }

    // Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.search_menu, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchManager searchManager = (SearchManager) Objects.requireNonNull(getActivity()).getSystemService(Context.SEARCH_SERVICE);

        if (searchItem != null) {
            searchView = (SearchView) searchItem.getActionView();
        }
        if (searchView != null) {
            assert searchManager != null;
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));

            queryTextListener = new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextChange(String newText) {
                    Log.i("onQueryTextChange", newText);

                    return true;
                }

                @Override
                public boolean onQueryTextSubmit(final String query) {
                    Log.i("onQueryTextSubmit", query);

                    if (isConnected()) {
                        final Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                hideKeyboard(getActivity());
                                searchList.clear();
                                bookAdapter = new BookListAdapter(searchList, getActivity());
                                bookRecycleView.setAdapter(bookAdapter);
                                bookAdapter.notifyDataSetChanged();

                                txtNotFound.setVisibility(View.INVISIBLE);
                                progressBar.setVisibility(View.VISIBLE);

                                getSearchBook(query);
                            }
                        }, 1000);
                    } else {
                        Toast.makeText(getContext(), "No Internet connection", Toast.LENGTH_LONG).show();
                    }

                    return true;
                }
            };
            searchView.setOnQueryTextListener(queryTextListener);

            assert searchItem != null;
            searchItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
                @Override
                public boolean onMenuItemActionExpand(MenuItem menuItem) {
                    Log.d("CLOSE", "Expand");
                    return true;
                }

                @Override
                public boolean onMenuItemActionCollapse(MenuItem menuItem) {
                    Log.d("CLOSE", "Collapse");
                    txtNotFound.setVisibility(View.INVISIBLE);
                    bookAdapter = new BookListAdapter(MainActivity.bookListMain, getActivity());
                    bookRecycleView.setAdapter(bookAdapter);
                    bookAdapter.notifyDataSetChanged();
                    return true;
                }
            });
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search:
                // not implement event here
                return false;
            default:
                break;
        }
        searchView.setOnQueryTextListener(queryTextListener);
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRefresh() {
        if (isConnected()) {
            MainActivity.bookListMain = new ArrayList<>();
            getBookList();
        } else {
            mSwipeRefreshLayout.setRefreshing(false);
            Toast.makeText(getContext(), "No Internet connection", Toast.LENGTH_LONG).show();
        }
    }

    public interface OnFragmentInteractionListener {
        // Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    public boolean isConnected() {
        ConnectivityManager connMgr = (ConnectivityManager) Objects.requireNonNull(getActivity()).getSystemService(Activity.CONNECTIVITY_SERVICE);
        assert connMgr != null;
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    // query book list
    private void getBookList() {
        HomeProfileService service = ServiceManager.createService(HomeProfileService.class);
        Call<List<BookResponse>> responseCall = service.getBookList();
        final ArrayList<BookModel> tempList = new ArrayList<>();
        ServiceManager.enqueueWithRetry(responseCall, new ServiceMangerCallback<retrofit2.Response<List<BookResponse>>>() {
            @Override
            public void onSuccess(retrofit2.Response<List<BookResponse>> data) {
                progressBar.setVisibility(View.INVISIBLE);
                for (int i = 0; i < data.body().size(); i++) {
                    BookModel tempBook = new BookModel();
                    BookResponse bookResponse = data.body().get(i);
                    tempBook.setBookId(bookResponse.getBookId());
                    tempBook.setSearch(bookResponse.getSearch());
                    tempBook.setBookType(bookResponse.getBookType());
                    tempBook.setBookLanguage(bookResponse.getBookLanguage());
                    tempBook.setReleaseYear(bookResponse.getReleaseYear());
                    tempBook.setAuthor(bookResponse.getAuthor());
                    tempBook.setIsbn(bookResponse.getIsbn());
                    tempBook.setSoftFile(bookResponse.getSoftFile());
                    tempBook.setCover(bookResponse.getCover());
                    tempBook.setEntryBy(bookResponse.getEntryBy());
                    tempBook.setEntryDate(bookResponse.getEntryDate());
                    tempBook.setShowPub(bookResponse.getShowPub());
                    tempBook.setLang(bookResponse.getLang());
                    tempBook.setTypeKh(bookResponse.getTypeKh());
                    tempBook.setTypeEn(bookResponse.getTypeEn());

                    tempList.add(tempBook);
                }

                if (MainActivity.bookListMain.size() < tempList.size()) {
                    MainActivity.bookListMain = tempList;
                    bookAdapter = new BookListAdapter(MainActivity.bookListMain, getActivity());
                    bookRecycleView.setAdapter(bookAdapter);
                    bookAdapter.notifyDataSetChanged();
                }

            }

            @Override
            public void onError(String message) {
                // Is thrown if there's no network connection or server is down
                progressBar.setVisibility(View.INVISIBLE);
                Log.d("Error =", "" + message);
                Toast.makeText(getContext(), "Server problem, check internet connection and restart",
                        Toast.LENGTH_LONG).show();

                // We return to the last fragment
                assert getFragmentManager() != null;
                if (getFragmentManager().getBackStackEntryCount() != 0) {
                    getFragmentManager().popBackStack();
                }
            }
        });
    }

    // Search Book by keyword
    private void getSearchBook(String query) {

        HomeProfileService service = ServiceManager.createService(HomeProfileService.class);
        Call<List<BookResponse>> responseCall = service.getSearchBook(query);
        ServiceManager.enqueueWithRetry(responseCall, new ServiceMangerCallback<retrofit2.Response<List<BookResponse>>>() {
            @Override
            public void onSuccess(Response<List<BookResponse>> data) {
                if (data.body() != null) {
                    for (int i = 0; i < data.body().size(); i++) {
                        BookModel tempBook = new BookModel();
                        BookResponse bookResponse = data.body().get(i);
                        tempBook.setBookId(bookResponse.getBookId());
                        tempBook.setSearch(bookResponse.getSearch());
                        tempBook.setBookType(bookResponse.getBookType());
                        tempBook.setBookLanguage(bookResponse.getBookLanguage());
                        tempBook.setReleaseYear(bookResponse.getReleaseYear());
                        tempBook.setAuthor(bookResponse.getAuthor());
                        tempBook.setIsbn(bookResponse.getIsbn());
                        tempBook.setSoftFile(bookResponse.getSoftFile());
                        tempBook.setCover(bookResponse.getCover());
                        tempBook.setEntryBy(bookResponse.getEntryBy());
                        tempBook.setEntryDate(bookResponse.getEntryDate());
                        tempBook.setShowPub(bookResponse.getShowPub());
                        tempBook.setLang(bookResponse.getLang());
                        tempBook.setTypeKh(bookResponse.getTypeKh());
                        tempBook.setTypeEn(bookResponse.getTypeEn());

                        searchList.add(tempBook);
                    }

                    progressBar.setVisibility(View.INVISIBLE);
                    bookAdapter = new BookListAdapter(searchList, getActivity());
                    bookRecycleView.setAdapter(bookAdapter);
                    bookAdapter.notifyDataSetChanged();

                } else {
                    searchList.clear();
                    bookAdapter = new BookListAdapter(searchList, getActivity());
                    bookRecycleView.setAdapter(bookAdapter);
                    bookAdapter.notifyDataSetChanged();
                    progressBar.setVisibility(View.INVISIBLE);
                    txtNotFound.setVisibility(View.VISIBLE);

                }
            }

            @Override
            public void onError(String message) {
                // Is thrown if there's no network connection or server is down
                progressBar.setVisibility(View.INVISIBLE);
                Log.d("Error =", "" + message);
                Toast.makeText(getContext(), "Server problem, check internet connection and restart",
                        Toast.LENGTH_LONG).show();

                // We return to the last fragment
                assert getFragmentManager() != null;
                if (getFragmentManager().getBackStackEntryCount() != 0) {
                    getFragmentManager().popBackStack();
                }
            }
        });
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
