package com.hsbook;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.barteksc.pdfviewer.PDFView;
//import com.hsbook.Utils.DownloadTask;
import com.hsbook.Retrofit.Interface.HomeProfileService;
import com.hsbook.Retrofit.ServiceManager;
import com.hsbook.Retrofit.ServiceMangerCallback;
import com.hsbook.Utils.DownloadTask;
import com.hsbook.adpater.TextDetailListAdapter;
import com.hsbook.api.ApiUrl;
import com.hsbook.model.BookModel;
import com.hsbook.model.BookResponse;
import com.hsbook.model.TextDetailModel;

import java.io.File;
import java.io.FileOutputStream;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailActivity extends AppCompatActivity{
    private String PDF_VIEWER = "PDF_VIEWER";

    private ImageView imgBook;
    private RecyclerView textListView;
    private LinearLayoutManager linearLayoutManager;
    private WebView webView;
    private TextDetailListAdapter textListAdapter;
    private List<TextDetailModel> textList = new ArrayList<>();
    private BookModel book;
    private Toolbar actionBar;
    private Button downloadBtn;
    PDFView pdfView;
    String url = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

//        imgBook = findViewById(R.id.act_detail_img_book);
        textListView = findViewById(R.id.act_detail_list);
        actionBar = findViewById(R.id.act_detail_app_bar);
        webView = findViewById(R.id.act_detail_webview);
        downloadBtn = findViewById(R.id.act_detail_download_btn);
        setUpActionBar(actionBar);

        linearLayoutManager = new LinearLayoutManager(this);
        textListView.setLayoutManager(linearLayoutManager);
        textListView.setHasFixedSize(true);
        book = (BookModel) getIntent().getSerializableExtra("BOOK");
        downloadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                        //File write logic here
                        final String bookTitle = MessageFormat.format("{0}-{1}", book.getTypeKh(), book.getTypeEn()) + ".pdf";
                        Log.d("DownloadTask =", "" + url);
                        new DownloadTask(DetailActivity.this, url, bookTitle);

//                        HomeProfileService api = ServiceManager.createService(HomeProfileService.class);
//                        Call<ResponseBody> responseCall = api.downlload(url);
//                        ServiceManager.enqueueDownload(responseCall, new ServiceMangerCallback<Response<ResponseBody>>() {
//                            @Override
//                            public void onSuccess(Response<ResponseBody> data) {
//
//                                try {
//                                    File path = Environment.getExternalStorageDirectory();
//                                    File file = new File(path, bookTitle);
//                                    FileOutputStream fileOutputStream = new FileOutputStream(file);
//
//                                    assert data.body() != null;
//                                    IOUtils.write(data.body().bytes(), fileOutputStream);
//                                }
//                                catch (Exception ex){
//                                    Log.d("Exception =", "" + ex.getMessage());
//                                    Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT)
//                                }
//
//                            }
//
//                            @Override
//                            public void onError(String message) {
//
//                            }
//                        });

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
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    //Do something after 100ms
                    setUpDetailInfo(book);
                }
            }, 500);
        }
    }

    private void setUpDetailInfo(BookModel book) {
//        String coverUrl = new ApiUrl().getCoverApi();
//                if (book.getCover() != null){
//            Glide.with(getApplicationContext())
//                    .load(coverUrl + book.getCover())
//                    .into(imgBook);
//        }
        String pdfFileUrl = new ApiUrl().getSoftFile();
        url = pdfFileUrl + book.getSoftFile();
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl("https://docs.google.com/viewer?url=" + url);
        webView.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                Log.d("webView", "" + url);
            }

            public void onPageFinished(WebView view, String url) {
                Log.d("webView", "finish" );
            }
        });

        textList.add(new TextDetailModel("Type of document", book.getBookType()));
        Log.d("book.", "" + book.getTypeEn());
        Log.d("book.", "" + book.getTypeKh());
        textList.add(new TextDetailModel("Search keyword EN", book.getTypeEn()));
        textList.add(new TextDetailModel("Search keyword KH", book.getTypeKh()));

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
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back_arrow_24dp);
        }
    }

//    @SuppressLint("SetJavaScriptEnabled")
//    public void getPdfViewer(){
//        String gPdfVewUrl = "http://docs.google.com/gview?embedded=true&url=";
//        String pdfFileUrl = new ApiUrl().getSoftFile();
//
//        if (book.getSoftFile() != null) {
//            WebView pdfViewer = new WebView(this);
//            pdfViewer.getSettings().setJavaScriptEnabled(true);
//            pdfViewer.loadUrl(gPdfVewUrl + pdfFileUrl + book.getSoftFile());
//            Log.d(PDF_VIEWER, pdfFileUrl+book.getSoftFile());
//        }
//    }
}