package com.hsbook.fragment;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hsbook.MainActivity;
import com.hsbook.R;
import com.hsbook.SwipeCallBack.RecyclerItemTouchHelper;
import com.hsbook.adpater.HistoryAdapter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@SuppressWarnings("FieldCanBeLocal")
public class HistoryFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener{
    // Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private RecyclerView historyRecycleView;
    private RecyclerView.Adapter historyAdapter;
    private RecyclerView.LayoutManager historyLayoutManager;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private OnFragmentInteractionListener mListener;

    public HistoryFragment() {
        // Required empty public constructor
    }

    // Rename and change types and number of parameters
    public static HistoryFragment newInstance(String param1, String param2) {
        HistoryFragment fragment = new HistoryFragment();
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
        return inflater.inflate(R.layout.fragment_history, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        historyRecycleView = Objects.requireNonNull(getView()).findViewById(R.id.frm_history_recyler_book);
        mSwipeRefreshLayout = getView().findViewById(R.id.frm_history_swipe_container);
        historyLayoutManager = new LinearLayoutManager(getContext());
        historyRecycleView.setLayoutManager(historyLayoutManager);
        historyRecycleView.setItemAnimator(new DefaultItemAnimator());
        historyRecycleView.setHasFixedSize(true);

        historyAdapter = new HistoryAdapter(MainActivity.histories, getActivity());
        historyRecycleView.setAdapter(historyAdapter);
        RecyclerItemTouchHelper recyclerItemTouchHelper = new RecyclerItemTouchHelper(getActivity(), historyRecycleView) {
            @Override
            public void instantiateUnderlayButton(final RecyclerView.ViewHolder viewHolder, List<UnderlayButton> underlayButtons) {
                underlayButtons.add(new RecyclerItemTouchHelper.UnderlayButton(
                        "Delete",
                        0,
                        Color.parseColor("#FF3C30"),
                        new RecyclerItemTouchHelper.UnderlayButtonClickListener() {
                            @Override
                            public void onClick(int pos) {
                                // TODO: onDelete
                                Log.d("onClick::", "onClick" + MainActivity.histories.get(pos));
                                File pdfFile = new File(Environment.getExternalStorageDirectory() + "/" + "HSBook Download", MainActivity.histories.get(pos));
                                if (pdfFile.exists()) //Checking for the file is exist or not
                                {
                                    pdfFile.delete();
                                    MainActivity.histories.remove(pos);
                                    getFragmentManager()
                                            .beginTransaction()
                                            .detach(HistoryFragment.this)
                                            .attach(HistoryFragment.this)
                                            .commit();
                                }


                            }
                        }
                ));
            }
        };

        new  ItemTouchHelper(recyclerItemTouchHelper).attachToRecyclerView(historyRecycleView);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
//                mSwipeRefreshLayout.setRefreshing(true);
                getHistoryList();
            }
        });
    }

    //nRename method, update argument and hook method into UI event
//    public void onButtonPressed(Uri uri) {
//        if (mListener != null) {
//            mListener.onFragmentInteraction(uri);
//        }
//    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onRefresh() {
        getHistoryList();
    }

    public interface OnFragmentInteractionListener {
        // Update argument type and name
        //void onFragmentInteraction(Uri uri);
    }

    public void getHistoryList(){
        MainActivity.histories = new ArrayList<>();
        File pdfFile = new File(Environment.getExternalStorageDirectory() + "/" + "HSBook Download");
        if (pdfFile.exists()) {
            //Checking for the file is exist or not
            String path = Environment.getExternalStorageDirectory() + "/" + "HSBook Download";
            File directory = new File(path);
            File[] files = directory.listFiles();
            if(files != null) {
                Log.d("Files", "Size: " + files.length);
                for (File file : files) {
                    MainActivity.histories.add(file.getName());
                    Log.d("Files", "FileName:" + file.getName());
                }
                mSwipeRefreshLayout.setRefreshing(false);
            }
        }
        mSwipeRefreshLayout.setRefreshing(false);
    }
}
