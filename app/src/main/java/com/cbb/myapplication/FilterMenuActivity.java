package com.cbb.myapplication;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.View;
import android.widget.Toast;

import com.cbb.myapplication.filterMenu.FilterMenu;
import com.cbb.myapplication.filterMenu.FilterMenuLayout;

public class FilterMenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter_menu);
        FilterMenuLayout layout1 = (FilterMenuLayout) findViewById(R.id.filter_menu1);
        attachMenu1(layout1);

        FilterMenuLayout layout2 = (FilterMenuLayout) findViewById(R.id.filter_menu2);
        attachMenu2(layout2);

        FilterMenuLayout layout3 = (FilterMenuLayout) findViewById(R.id.filter_menu3);
        attachMenu3(layout3);

        FilterMenuLayout layout4 = (FilterMenuLayout) findViewById(R.id.filter_menu4);
        attachMenu4(layout4);
    }
    private FilterMenu attachMenu1(FilterMenuLayout layout){
        return new FilterMenu.Builder(this)
                .addItem(R.drawable.ic_action_add)
                .addItem(R.drawable.ic_action_clock)
                .addItem(R.drawable.ic_action_info)
                .addItem(R.drawable.ic_action_io)
                .addItem(R.drawable.ic_action_location_2)
                .attach(layout)
                .withListener(listener)
                .build();
    }
    private FilterMenu attachMenu2(FilterMenuLayout layout){
        return new FilterMenu.Builder(this)
                .addItem(R.drawable.ic_action_add)
                .addItem(R.drawable.ic_action_clock)
                .addItem(R.drawable.ic_action_info)
                .addItem(R.drawable.ic_action_location_2)
                .attach(layout)
                .withListener(listener)
                .build();
    }
    private FilterMenu attachMenu3(FilterMenuLayout layout){
        return new FilterMenu.Builder(this)
                .addItem(R.drawable.ic_action_add)
                .addItem(R.drawable.ic_action_clock)
                .addItem(R.drawable.ic_action_location_2)
                .attach(layout)
                .withListener(listener)
                .build();
    }
    private FilterMenu attachMenu4(FilterMenuLayout layout){
        return new FilterMenu.Builder(this)
                .inflate(R.menu.menu_filter)
                .attach(layout)
                .withListener(listener)
                .build();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    FilterMenu.OnMenuChangeListener listener = new FilterMenu.OnMenuChangeListener() {
        @Override
        public void onMenuItemClick(View view, int position) {
            Toast.makeText(FilterMenuActivity.this, "Touched position " + position, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onMenuCollapse() {

        }


        @Override
        public void onMenuExpand() {

        }
    };
}
