package com.hsbook.adpater;

import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.hsbook.DetailActivity;
import com.hsbook.R;
import com.hsbook.api.ApiUrl;
import com.hsbook.model.BookModel;

import java.text.MessageFormat;
import java.util.List;

public class BookListAdapter extends RecyclerView.Adapter<BookListAdapter.BookViewHolder> {

    private List<BookModel> bookList;
    private Context mContext;

    public BookListAdapter(List<BookModel> list, Context context) {
        bookList = list;
        mContext = context;
    }


    @NonNull
    @Override
    public BookListAdapter.BookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater mInflater = LayoutInflater.from(parent.getContext());
        View itemView = mInflater.inflate(R.layout.book_list_item, parent, false);
        return new BookViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final BookListAdapter.BookViewHolder holder, int position) {
        final BookModel book = bookList.get(position);
        String coverUrl = new ApiUrl().getCoverApi();
        holder.bookTitle.setText(MessageFormat.format("{0}-{1}", book.getTypeKh(), book.getTypeEn()));
        holder.bookAuthor.setText(book.getAuthor());
        holder.bookLang.setText(book.getLang());

        if (book.getCover() != null) {
            Glide.with(holder.bookImg)
                    .load(coverUrl + book.getCover())
                    .into(holder.bookImg);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //onClickSubject.onNext(element);
                Intent detailActivity = new Intent(mContext, DetailActivity.class);
                detailActivity.putExtra("BOOK", book);
                mContext.startActivity(detailActivity);
            }
        });
    }

    @Override
    public int getItemCount() {
        return bookList.size();
    }

    class BookViewHolder extends RecyclerView.ViewHolder {
        ImageView bookImg;
        TextView bookTitle;
        TextView bookLang;
        TextView bookAuthor;

        BookViewHolder(View itemView) {
            super(itemView);

            bookImg = itemView.findViewById(R.id.book_img);
            bookTitle = itemView.findViewById(R.id.book_title);
            bookLang = itemView.findViewById(R.id.book_lang);
            bookAuthor = itemView.findViewById(R.id.book_author);
        }
    }
}
