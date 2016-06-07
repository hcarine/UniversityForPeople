package com.example.cari.universit;

import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

/**
 * Created by cari on 06/06/16.
 */
public class GradeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{


    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        return false;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.table_notas);

    }
}
