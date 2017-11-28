package com.cbb.myapplication.taiji;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.cbb.myapplication.R;

import java.util.ArrayList;

public class EasyCircleActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_easy_circle);

        PieView pieView = new PieView(this);
        setContentView(pieView);

        ArrayList<PieData> datas = new ArrayList<>();
        datas.add(new PieData("小明" , 10));
        datas.add(new PieData("小名" , 100));
        datas.add(new PieData("小明" , 30));
        datas.add(new PieData("小明" , 40));
        datas.add(new PieData("小明" , 80));
        pieView.setDatas(datas);
    }
}
