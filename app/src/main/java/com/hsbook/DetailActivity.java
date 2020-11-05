package com.hsbook;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hsbook.Utils.DownloadTask;
import com.hsbook.adpater.TextDetailListAdapter;
import com.hsbook.api.ApiUrl;
import com.hsbook.model.BookModel;
import com.hsbook.model.TextDetailModel;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@SuppressWarnings("FieldCanBeLocal")
public class DetailActivity extends AppCompatActivity {

    private RecyclerView textListView;
    private LinearLayoutManager linearLayoutManager;
    private WebView webView;
    private TextDetailListAdapter textListAdapter;
    private List<TextDetailModel> textList = new ArrayList<>();
    private BookModel book;
    private Toolbar actionBar;
    private Button downloadBtn;
    private ProgressBar progressBar;

    private String url = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        textListView = findViewById(R.id.act_detail_list);
        actionBar = findViewById(R.id.act_detail_app_bar);
        webView = findViewById(R.id.act_detail_webview);
        downloadBtn = findViewById(R.id.act_detail_download_btn);
        progressBar = findViewById(R.id.act_detail_progress);
        setUpActionBar(actionBar);

        linearLayoutManager = new LinearLayoutManager(this);
        textListView.setLayoutManager(linearLayoutManager);
        textListView.setHasFixedSize(true);
        book = (BookModel) getIntent().getSerializableExtra("BOOK");

        // Download Action
        downloadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                        //File write logic here
                        final String bookTitle = MessageFormat.format("{0}-{1}", book.getTypeKh(), book.getTypeEn()) + ".pdf";
                        if (url.equals("")) {
                            Toast.makeText(getApplication(), "Something went wrong", Toast.LENGTH_SHORT).show();
                        } else {
                            new DownloadTask(DetailActivity.this, url, bookTitle);
                        }
                    } else {
                        ActivityCompat.requestPermissions(DetailActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                    }
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (book != null) {
            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    setUpDetailInfo(book);
                }
            }, 500);
        }
    }

    private void setUpDetailInfo(BookModel book) {
        String pdfFileUrl = new ApiUrl().getSoftFile();
        url = pdfFileUrl + book.getSoftFile();
        webView.getSettings().setJavaScriptEnabled(true);
        webView.clearCache(true);
        webView.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                progressBar.setVisibility(View.VISIBLE);
            }

            public void onPageFinished(WebView view, String url) {
                if (view.getContentHeight() == 0) view.loadUrl(url);
                progressBar.setVisibility(View.INVISIBLE);
            }
        });

        Log.d("onPageFinished", "" + url);
        webView.loadUrl("https://docs.google.com/viewer?url=" + url);
//        textList.add(new TextDetailModel("Type of document", book.getBookType()));
//        textList.add(new TextDetailModel("Search keyword EN", book.getTypeEn()));
//        textList.add(new TextDetailModel("Search keyword KH", book.getTypeKh()));

        textList.add(new TextDetailModel("Type of document", book.getTypeEn()));

        textListAdapter = new TextDetailListAdapter(textList);
        textListAdapter.notifyDataSetChanged();
        textListView.setAdapter(textListAdapter);
    }

    private void setUpActionBar(Toolbar toolbar) {
        TextView mActionBarTitle;
        mActionBarTitle = findViewById(R.id.app_bar_title);

        if (toolbar != null) {
            setSupportActionBar(toolbar);
            mActionBarTitle.setText(R.string.detail);
            Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back_arrow_24dp);
        }
    }
}