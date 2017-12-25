package com.cbb.myapplication.viewpager;

import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.cbb.myapplication.R;

public class ViewPagerActivity extends AppCompatActivity {

    int[] imgRes = {R.mipmap.a, R.mipmap.b, R.mipmap.c, R.mipmap.d,
            R.mipmap.e, R.mipmap.f, R.mipmap.g, R.mipmap.h, R.mipmap.i
    };
    private PagerAdapter mPagerAdapter;
    private ViewPager mViewPager;
    private ViewPager mViewPager2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_pager);
        mViewPager = (ViewPager) findViewById(R.id.id_viewpager);
        mViewPager.setPageMargin(20);
        mViewPager.setOffscreenPageLimit(3);

        mViewPager2 = (ViewPager) findViewById(R.id.id_viewpager2);
        mViewPager2.setPageMargin(40);

        mPagerAdapter = new PagerAdapter() {

            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                ImageView imageView = new ImageView(ViewPagerActivity.this);
                imageView.setImageResource(imgRes[position]);
                container.addView(imageView);
                return imageView;
            }

            @Override
            public int getCount() {
                return imgRes.length;
            }

            @Override
            public boolean isViewFromObject(View view, Object object) {
                return view == object;
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                container.removeView((View) object);
            }
        };

        mViewPager.setAdapter(mPagerAdapter);
        mViewPager.setPageTransformer(true, new AlphaPageTransformer());
        mViewPager2.setAdapter(mPagerAdapter);
        mViewPager2.setPageTransformer(true, new RotateYTransformer());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        String[] effects = this.getResources().getStringArray(R.array.effect);
        for (String effect : effects){
            menu.add(effect);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        String title = item.getTitle().toString();
        mViewPager.setAdapter(mPagerAdapter);
        switch (title){
            case "RotateDown":
                mViewPager.setPageTransformer(true, new RotateDownPageTransformer());
                break;
            case "RotateUp":
                mViewPager.setPageTransformer(true, new RotateUpPageTransformer());
                break;
            case "RotateUpPageTransformer":
                mViewPager.setPageTransformer(true, new RotateYTransformer());
                break;
            case "Standard":
                mViewPager.setPageTransformer(true, NonPageTransformer.INSTANCE);
                break;
            case "Alpha":
                mViewPager.setPageTransformer(true, new AlphaPageTransformer());
                break;
            case "ScaleIn":
                mViewPager.setPageTransformer(true, new ScaleInTransformer());
                break;
            case "RotateDown and Alpha":
                mViewPager.setPageTransformer(true, new RotateDownPageTransformer(new AlphaPageTransformer()));
                break;
            case "RotateDown and Alpha And ScaleIn":
                mViewPager.setPageTransformer(true, new RotateDownPageTransformer(new AlphaPageTransformer(new ScaleInTransformer())));
                break;
        }
        setTitle(title);
        return true;
    }
}
