package com.wzy.pintu;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private static final int PANEL_ROW = 3;
    private static final int PANEL_COLUMN = 5;
    private static final int DEFAULT_PADDING = 2;
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final long DEFAULT_ANIM_DURATION = 1000;

    private ImageView[][] mGamePics;

    private GridLayout mGridLayout;
    private ImageView mBlankImageView;

    private enum DIRECTION {
        LEFT, RIGHT, TOP, BOTTOM
    };
    private GestureDetector mDetector;

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
        int x, y;
        for (int i = 0; i < PANEL_ROW; i++) {
            for (int j = 0; j < PANEL_COLUMN; j++) {
                x = j * squareWidth;
                y = i * squareHeight;
                Bitmap square = Bitmap.createBitmap(srcBitmap, x, y, squareWidth, squareHeight);
                final ImageView imageView = new ImageView(this);
                imageView.setImageBitmap(square);
                imageView.setPadding(DEFAULT_PADDING, DEFAULT_PADDING, DEFAULT_PADDING,
                        DEFAULT_PADDING);
                GameInfo gameInfo = new GameInfo(x, x + squareWidth, y, y + squareHeight, square, i, j);
                imageView.setTag(gameInfo);
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        boolean isAvail = isAvailableBlankImageView(imageView);
                        Toast.makeText(MainActivity.this, "isAvail=" + isAvail, Toast.LENGTH_SHORT).show();
                        if (isAvail) {
                            animTranslation(imageView);
                        }
                    }
                });
                mGamePics[i][j] = imageView;
            }
        }
        srcBitmap.recycle();

        mDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                DIRECTION direction = getDirection(e1.getX(), e1.getY(), e2.getX(), e2.getY());
                swapImgsByDirection(direction);
                return true;
            }
        });
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
        mBlankImageView = iv;
    }

    private boolean isAvailableBlankImageView(ImageView iv) {
        GameInfo ivInfo = (GameInfo) iv.getTag();
        GameInfo blankInfo = (GameInfo) mBlankImageView.getTag();

        Log.e(TAG, "isAvailableBlankImageView ivInfo: leftX=" + ivInfo.leftX + ", rightX=" + ivInfo.rightX + ", topY=" + ivInfo.topY + ", bottomY=" + ivInfo.bottomY);
        Log.e(TAG, "isAvailableBlankImageView blankInfo: leftX=" + blankInfo.leftX + ", rightX=" + blankInfo.rightX + ", topY=" + blankInfo.topY + ", bottomY=" + blankInfo.bottomY);

        if (ivInfo.bottomY == blankInfo.bottomY && ivInfo.rightX == blankInfo.leftX) {
            // 左边
            return true;
        } else if (ivInfo.bottomY == blankInfo.bottomY && ivInfo.leftX == blankInfo.rightX) {
            // 右边
            return true;
        } else if (ivInfo.leftX == blankInfo.leftX && ivInfo.bottomY == blankInfo.topY) {
            // 上边
            return true;
        } else if (ivInfo.leftX == blankInfo.leftX && ivInfo.topY == blankInfo.bottomY) {
            // 下边
            return true;
        }

        return false;
    }

    private void animTranslation(final ImageView iv) {
        final GameInfo ivInfo = (GameInfo) iv.getTag();
        final GameInfo blankInfo = (GameInfo) mBlankImageView.getTag();
        TranslateAnimation anim = null;

        if (ivInfo.bottomY == blankInfo.bottomY) {
            // 左边or右边
            anim = new TranslateAnimation(0, blankInfo.leftX - ivInfo.leftX, 0, 0);
        } else if (ivInfo.leftX == blankInfo.leftX) {
            // 上边or下边
            anim = new TranslateAnimation(0, 0, 0, blankInfo.topY - ivInfo.topY);
        }

        assert anim != null;
        anim.setDuration(DEFAULT_ANIM_DURATION);
        anim.setFillAfter(true);
        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                iv.clearAnimation();
                mBlankImageView.setImageBitmap(ivInfo.getBitmap());
                blankInfo.setBitmap(ivInfo.getBitmap());
                setBlankImageView(iv);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        iv.startAnimation(anim);

    }

    private DIRECTION getDirection(float x1, float y1, float x2, float y2) {
        boolean isLeftOrRight = Math.abs(x1 - x2) > Math.abs(y1 - y2);
        if (isLeftOrRight) {
            return x1 - x2 > 0 ? DIRECTION.LEFT : DIRECTION.RIGHT;
        } else {
            return y1 - y2 > 0 ? DIRECTION.TOP : DIRECTION.BOTTOM;
        }
    }

    private void swapImgsByDirection(DIRECTION direction) {
        GameInfo blankGameInfo = (GameInfo) mBlankImageView.getTag();
        int locy = blankGameInfo.locRow, locx = blankGameInfo.locCol;
        switch (direction) {
            case LEFT:
                locx = blankGameInfo.locCol + 1;
                break;
            case RIGHT:
                locx = blankGameInfo.locCol - 1;
                break;
            case TOP:
                locy = blankGameInfo.locRow + 1;
                break;
            case BOTTOM:
                locy = blankGameInfo.locRow - 1;
                break;
        }

        if (locx >= 0 && locx < PANEL_COLUMN && locy >= 0 && locy < PANEL_ROW) {
            animTranslation(mGamePics[locy][locx]);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return mDetector.onTouchEvent(event);
    }

    private static class GameInfo {
        private int leftX;
        private int topY;
        private int rightX;
        private int bottomY;
        private Bitmap mBitmap;
        private int locRow;
        private int locCol;

        public GameInfo(int leftX, int rightX, int topY, int bottomY, Bitmap bitmap, int locRow, int locCol) {
            this.leftX = leftX;
            this.rightX = rightX;
            this.topY = topY;
            this.bottomY = bottomY;
            this.mBitmap = bitmap;
            this.locRow = locRow;
            this.locCol = locCol;
        }

        public Bitmap getBitmap() {
            return mBitmap;
        }

        public void setBitmap(Bitmap mBitmap) {
            this.mBitmap = mBitmap;
        }
    }
}
