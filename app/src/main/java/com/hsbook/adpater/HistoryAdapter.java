package com.hsbook.adpater;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.barteksc.pdfviewer.PDFView;
import com.hsbook.HistoryDetailActivity;
import com.hsbook.R;

import java.io.File;
import java.util.ArrayList;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.HistoryHolder> {

    private ArrayList<String> historyList;
    private Context mContext;

    public HistoryAdapter(ArrayList<String> list, Context context) {
        historyList = list;
        mContext = context;
    }

    @NonNull
    @Override
    public HistoryAdapter.HistoryHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater mInflater = LayoutInflater.from(parent.getContext());
        View itemView = mInflater.inflate(R.layout.history_list_item, parent, false);
        return new HistoryAdapter.HistoryHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final HistoryAdapter.HistoryHolder holder, int position) {
        final String book = historyList.get(position);
        String[] titleSeparated = book.split("\\.");
        holder.bookTitle.setText(titleSeparated[0]);

        File pdfFile = new File(Environment.getExternalStorageDirectory() + "/" + "HSBook Download", book);
        if (pdfFile.exists()) //Checking for the file is exist or not
        {
            Uri path = Uri.fromFile(pdfFile);
            holder.pdfView.fromUri(path).enableSwipe(false).load();
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //onClickSubject.onNext(element);
                Intent intent = new Intent(mContext, HistoryDetailActivity.class);
                intent.putExtra("path", book);
                mContext.startActivity(intent);
            }
        });
    }

    public void removeAt(int position){

        File pdfFile = new File(Environment.getExternalStorageDirectory() + "/" + "HSBook Download", historyList.get(position));
        if (pdfFile.exists()) //Checking for the file is exist or not
        {
            historyList.remove(position);
            pdfFile.delete();
        }
    }

    @Override
    public int getItemCount() {
        return historyList.size();
    }

    public class HistoryHolder extends RecyclerView.ViewHolder {
//        ImageView bookImg;
        TextView bookTitle;
        PDFView pdfView;
        public RelativeLayout viewForeground;
        HistoryHolder(View itemView) {
            super(itemView);

            pdfView = itemView.findViewById(R.id.act_history_book_image);
            bookTitle = itemView.findViewById(R.id.act_history_book_title_label);
            viewForeground = itemView.findViewById(R.id.view_foreground);
        }
    }

    public void pdf2Img(){

    }
}