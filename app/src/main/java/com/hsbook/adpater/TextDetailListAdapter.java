package com.hsbook.adpater;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hsbook.R;
import com.hsbook.model.TextDetailModel;

import java.util.List;

public class TextDetailListAdapter extends RecyclerView.Adapter<TextDetailListAdapter.TextDetailHolder> {
    private List<TextDetailModel> detailList;

    public TextDetailListAdapter(List<TextDetailModel> list) {
        detailList = list;
    }


    @NonNull
    @Override
    public TextDetailListAdapter.TextDetailHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater mInflater = LayoutInflater.from(parent.getContext());
        View itemView = mInflater.inflate(R.layout.book_detail_text_list_item, parent, false);
        return new TextDetailListAdapter.TextDetailHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull TextDetailHolder holder, int position) {
        TextDetailModel detail = detailList.get(position);

        holder.txt_title.setText(detail.getText_title());
        holder.txt_desc.setText(detail.getText_desc());
    }

    @Override
    public int getItemCount() {
        return detailList.size();
    }

    class TextDetailHolder extends RecyclerView.ViewHolder {
        TextView txt_title;
        TextView txt_desc;

        TextDetailHolder(View itemView) {
            super(itemView);

            txt_title = itemView.findViewById(R.id.detail_txt_title);
            txt_desc = itemView.findViewById(R.id.detail_txt_desc);
        }
    }
}
