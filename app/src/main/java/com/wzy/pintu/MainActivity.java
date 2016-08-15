package com.wzy.pintu;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.GridLayout;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final int PANEL_ROW = 3;
    private static final int PANEL_COLUMN = 5;
    private static final String TAG = MainActivity.class.getSimpleName();

    private ImageView[][] mGamePics;

    private GridLayout mGridLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initData();
        initView();
    }

    private void initData() {
        mGamePics = new ImageView[PANEL_ROW][PANEL_COLUMN];
        Bitmap srcBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.huoying);
        Log.e(TAG, "initData: width=" + srcBitmap.getWidth() + ", height=" + srcBitmap.getHeight());
        Log.e(TAG, "initData: screenWidth=" + getResources().getDisplayMetrics().widthPixels);
        Log.e(TAG, "initData: screenHeight=" + getResources().getDisplayMetrics().heightPixels);
        int squareWidth = srcBitmap.getWidth() / PANEL_COLUMN;
        int squareHeight = srcBitmap.getHeight() / PANEL_ROW;
        int x = 0, y = 0;
        for (int i = 0; i < PANEL_ROW; i ++) {
            for (int j = 0; j < PANEL_COLUMN; j ++) {
                x = j * squareWidth;
                y = i * squareHeight;
                Bitmap square = Bitmap.createBitmap(srcBitmap, x, y, squareWidth, squareHeight);
                ImageView imageView = new ImageView(this);
                imageView.setImageBitmap(square);
                imageView.setPadding(2, 2, 2, 2);
                mGamePics[i][j] = imageView;
            }
        }
        srcBitmap.recycle();
    }

    private void initView() {
        mGridLayout = (GridLayout) findViewById(R.id.id_gridlayout);
        if (mGridLayout != null) {
            for (int i = 0; i < mGamePics.length; i++) {
                for (ImageView iv : mGamePics[i]) {
                    mGridLayout.addView(iv);
                }
            }
        }
        setBlankImageView(mGamePics[PANEL_ROW - 1][PANEL_COLUMN - 1]);
    }

    private void setBlankImageView(ImageView iv) {
        iv.setImageBitmap(null);
    }
}
