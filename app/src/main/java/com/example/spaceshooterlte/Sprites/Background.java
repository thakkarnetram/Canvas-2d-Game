package com.example.spaceshooterlte.Sprites;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.example.spaceshooterlte.R;

public class Background {
    public int x=0,y=0;
    public Bitmap background;

    public Background(int screenX, int screenY, Resources resources) {
        background = BitmapFactory.decodeResource(resources, R.drawable.background);
        background = Bitmap.createScaledBitmap(background,screenX,screenY,false);
    }
}
