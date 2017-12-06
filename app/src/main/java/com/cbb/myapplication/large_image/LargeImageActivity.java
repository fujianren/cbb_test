package com.cbb.myapplication.large_image;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapRegionDecoder;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.cbb.myapplication.R;

import java.io.IOException;
import java.io.InputStream;


public class LargeImageActivity extends AppCompatActivity {

    private ImageView mImageView;
    private LargeImageView mLargeImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_large_image);

        mImageView = (ImageView) findViewById(R.id.imageview);
//        setImage();

        mLargeImageView = (LargeImageView) findViewById(R.id.largeImageView);
        try {
            InputStream inputStream = getAssets().open("qm.jpg");
            mLargeImageView.setInputStream(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setImage() {
        try {
            // 输入流
            InputStream inputStream = getAssets().open("tangyan.jpg");

            BitmapFactory.Options tmpOptions = new BitmapFactory.Options(); // bitmap的选项
            tmpOptions.inJustDecodeBounds = true;   // true表示，不分配内存就直接访问bitmap
            BitmapFactory.decodeStream(inputStream, null, tmpOptions);  // Bitmap解码输入流，存储在tmpOptions中
            int width = tmpOptions.outWidth;        // 从选项中获取bitmap的宽
            int height = tmpOptions.outHeight;      // 从选项中获取bitmap的高

            // bitmap的区域解码器（对象）
            BitmapRegionDecoder bitmapRegionDecoder = BitmapRegionDecoder.newInstance(inputStream, false);
            // 对应的bitmap选项
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.RGB_565;  // 色彩标准配置，默认ARGB_8888
            // 区域解码器根据指定区域，bitmap选项，解码出一个新的bitmap
            Bitmap bitmap = bitmapRegionDecoder.decodeRegion(new Rect(
                    width / 2 - 100, height / 2 - 100, width / 2 + 100, height / 2 + 100
            ), options);
            // 显示出解码出的bitmap
            mImageView.setImageBitmap(bitmap);

            inputStream.close();

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
        }
    }
}
