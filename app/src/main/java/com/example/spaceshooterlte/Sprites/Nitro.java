package com.example.spaceshooterlte.Sprites;

import static com.example.spaceshooterlte.View.GameView.screenRatioX;
import static com.example.spaceshooterlte.View.GameView.screenRatioY;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.example.spaceshooterlte.R;

public class Nitro {
    public int x, y, width, height, nitroSpeed = 20;
    public Bitmap nitro;
    public boolean isNitroOn = false, isNitroCollected = true;

    public Nitro(Resources resources) {
        nitro = BitmapFactory.decodeResource(resources, R.drawable.nitro);
        // resize
        width = nitro.getWidth();
        height = nitro.getHeight();

        width /= 6;
        height /= 6;

        width = (int) (width * screenRatioX);
        height = (int) (height * screenRatioY);

        nitro = Bitmap.createScaledBitmap(nitro, width, height, false);

        // initial spawn position
        x = (int) (screenRatioX - 150);
    }
}
