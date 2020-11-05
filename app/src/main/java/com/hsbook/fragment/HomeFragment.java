package com.hsbook.fragment;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
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

@SuppressWarnings({"FieldCanBeLocal", "rawtypes"})
public class HomeFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private SearchView searchView = null;
    private SearchView.OnQueryTextListener queryTextListener;

    private ProgressBar progressBar;
    private TextView txtNotFound;
    private RecyclerView bookRecycleView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView.Adapter bookAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private List<BookModel> searchList = new ArrayList<>();

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // Your Code
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

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            // do nothing
            Log.d("context", "context");
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, MenuInflater inflater) {
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
                        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                hideKeyboard(Objects.requireNonNull(getActivity()));
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
        if (item.getItemId() == R.id.action_search) {
            // not implement event here
            return false;
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
    }

    public boolean isConnected() {
        // check internet connection
        ConnectivityManager connectivityManager = (ConnectivityManager) Objects.requireNonNull(getActivity()).getSystemService(Context.CONNECTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // handle new version code
            Network nw = connectivityManager.getActiveNetwork();
            if (nw == null) return false;
            NetworkCapabilities actNw = connectivityManager.getNetworkCapabilities(nw);
            return actNw != null && (actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) || actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) || actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) || actNw.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH));
        } else {
            // handle old version code
            NetworkInfo nwInfo = connectivityManager.getActiveNetworkInfo();
            return nwInfo != null && nwInfo.isConnected();
        }
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
                assert data.body() != null;
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
