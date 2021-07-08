package edu.uchicago.fullstack.androidretro.AA_view.views;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import edu.uchicago.fullstack.androidretro.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    //.replace(R.id.container, ResultListFragment.newInstance())
                    .commitNow();
        }
    }
}