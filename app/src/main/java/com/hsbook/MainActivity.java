package com.hsbook;

import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import androidx.annotation.NonNull;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import androidx.fragment.app.Fragment;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.hsbook.fragment.AboutFragment;
import com.hsbook.fragment.HistoryFragment;
import com.hsbook.fragment.HomeFragment;
import com.hsbook.model.BookModel;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@SuppressWarnings("FieldCanBeLocal")
public class MainActivity extends BaseActivity implements HomeFragment.OnFragmentInteractionListener,
        HistoryFragment.OnFragmentInteractionListener, AboutFragment.OnFragmentInteractionListener {

    private TextView mActionBarTitle;
    private Toolbar actionBar;
    private Fragment fragment;
    public static ArrayList<String> histories = new ArrayList<>();
    public static List<BookModel> bookListMain = new ArrayList<>();

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    mActionBarTitle.setText("DHS");
                    fragment = new HomeFragment();
                    break;
                case R.id.navigation_history:
                    mActionBarTitle.setText(R.string.download_history);
                    fragment = new HistoryFragment();
                    break;
                case R.id.navigation_about:
                    mActionBarTitle.setText(R.string.title_about);
                    fragment = new AboutFragment();
                default:
                    break;
            }
            return loadFragment(fragment);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mActionBarTitle = findViewById(R.id.app_bar_title);
        actionBar = findViewById(R.id.act_home_app_bar);
        getHistoryList();
        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        if (findViewById(R.id.frm_container) != null) {

            if (savedInstanceState != null) {
                return;
            }

            // Create a new Fragment to be placed in the activity layout
            fragment = new HomeFragment();

            // In case this activity was started with special instructions from an
            // Intent, pass the Intent's extras to the fragment as arguments
            fragment.setArguments(getIntent().getExtras());

            // Add the fragment to the 'fragment_container' FrameLayout
            getSupportFragmentManager().beginTransaction().add(R.id.frm_container, fragment).commit();
        }

        setUpActionBar(actionBar);

    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //getMenuInflater().inflate(R.menu.search_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public void onFragmentInteraction(Uri uri) {

    }

    protected boolean loadFragment(Fragment frm) {
        if (frm != null){
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.frm_container, frm)
                    .commit();
            return true;
        }
        return  false;
    }

    private void setUpActionBar(Toolbar toolbar){
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            mActionBarTitle.setText("DHS");
            Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(false);
        }
    }

    public void getHistoryList(){
        histories = new ArrayList<>();
        File pdfFile = new File(Environment.getExternalStorageDirectory() + "/" + "HSBook Download");
        if (pdfFile.exists()) {
            //Checking for the file is exist or not
            String path = Environment.getExternalStorageDirectory() + "/" + "HSBook Download";
            File directory = new File(path);
            File[] files = directory.listFiles();
            if(files != null) {
                Log.d("Files", "Size: " + files.length);
                for (File file : files) {
                    histories.add(file.getName());
                    Log.d("Files", "FileName:" + file.getName());
                }
            }
        }
    }
}
