package com.hsbook;

import android.net.Uri;
import android.os.Environment;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.github.barteksc.pdfviewer.PDFView;

import java.io.File;
import java.util.Objects;

@SuppressWarnings("FieldCanBeLocal")
public class HistoryDetailActivity extends AppCompatActivity {

    private PDFView pdfView;
    private String path;
    private Toolbar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_detail);
        pdfView = findViewById(R.id.act_history_pdf_view);
        actionBar = findViewById(R.id.act_history_app_bar);

        setUpActionBar(actionBar);
        Bundle bundle = getIntent().getExtras();
        assert bundle != null;
        path = bundle.getString("path");

        File pdfFile = new File(Environment.getExternalStorageDirectory() + "/" + "HSBook Download", path);
        if (pdfFile.exists())  {
            //Checking for the file is exist or not
            Log.d("pdfFile = ", "" + pdfFile.getName());
            Uri path = Uri.fromFile(pdfFile);
            pdfView.fromUri(path).load();
        }
    }

    private void setUpActionBar(Toolbar toolbar) {
        TextView mActionBarTitle;
        mActionBarTitle = findViewById(R.id.app_bar_title);

        if (toolbar != null) {
            setSupportActionBar(toolbar);
            mActionBarTitle.setText(R.string.download_history);
        }
    }
}
