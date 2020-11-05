package com.hsbook;

import android.net.Uri;
import android.os.Environment;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.github.barteksc.pdfviewer.PDFView;

import java.io.File;

public class HistoryDetailActivity extends AppCompatActivity {

    PDFView pdfView;
    String path;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_detail);
        pdfView = findViewById(R.id.act_history_pdf_view);

        Bundle bundle = getIntent().getExtras();
        path = bundle.getString("path");

        File pdfFile = new File(Environment.getExternalStorageDirectory() + "/" + "HSBook Download", path);
        if (pdfFile.exists())  {
            //Checking for the file is exist or not
            Log.d("pdfFile = ", "" + pdfFile.getName());
            Uri path = Uri.fromFile(pdfFile);
            pdfView.fromUri(path).load();
        }
    }
}
