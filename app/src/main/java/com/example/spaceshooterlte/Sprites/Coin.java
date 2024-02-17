package com.example.spaceshooterlte.Sprites;

import static com.example.spaceshooterlte.View.GameView.screenRatioX;
import static com.example.spaceshooterlte.View.GameView.screenRatioY;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;

import com.example.spaceshooterlte.R;

public class Coin {
    public int x, y, width, height, coinSpeed = 20, coinCounter = 0;
    public boolean isCoinCollected = true;
    public Bitmap coin1, coin2, coin3;

    public Coin(Resources resources) {
        coin1 = BitmapFactory.decodeResource(resources, R.drawable.coin1);
        coin2 = BitmapFactory.decodeResource(resources, R.drawable.coin2);
        coin3 = BitmapFactory.decodeResource(resources, R.drawable.coin3);

        // resize the coin
        width = coin1.getWidth();
        height = coin1.getHeight();

        width /= 10;
        height /= 10;

        width = (int) (width * screenRatioX);
        height = (int) (height * screenRatioY);

        coin1 = Bitmap.createScaledBitmap(coin1, width, height, false);
        coin2 = Bitmap.createScaledBitmap(coin2, width, height, false);
        coin3 = Bitmap.createScaledBitmap(coin3, width, height, false);

        y = -height;
    }

    public Bitmap getCoin() {
        if (coinCounter == 0) {
            coinCounter++;
            return coin1;
        }
        if (coinCounter == 1) {
            coinCounter++;
            return coin2;
        }
        coinCounter = 0;
        return coin3;
    }

    public Rect getCollisionBounds() {
        return new Rect(x, y, x + width, y + height);
    }
}
