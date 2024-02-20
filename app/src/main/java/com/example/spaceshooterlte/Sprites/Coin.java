package com.example.spaceshooterlte.Sprites;

import static com.example.spaceshooterlte.View.GameView.screenRatioX;
import static com.example.spaceshooterlte.View.GameView.screenRatioY;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;

import com.example.spaceshooterlte.R;

public class Coin {
    public int x, y, width, height, coinSpeed = 25, coinCounter = 0;
    public boolean isCoinCollected = true;
    public Bitmap coin;

    public boolean isCoinOn = false;

    public Coin(Resources resources) {
        coin = BitmapFactory.decodeResource(resources, R.drawable.coin1);

        // resize the coin
        width = coin.getWidth();
        height = coin.getHeight();

        width /= 10;
        height /= 10;

        width = (int) (width * screenRatioX);
        height = (int) (height * screenRatioY);

        coin= Bitmap.createScaledBitmap(coin, width, height, false);

        // initial spawn position
        x = (int) (screenRatioY -  200);
    }

}
