package com.example.spaceshooterlte.Sprites;

import static com.example.spaceshooterlte.View.GameView.screenRatioX;
import static com.example.spaceshooterlte.View.GameView.screenRatioY;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;

import com.example.spaceshooterlte.R;

public class Stone {
    public int x, y, width, height, stoneCounter = 0, stoneSpeed = 25;
    public boolean isStoneHit = true,isStoneOn=false;
    public Bitmap stone1;

    public Stone(Resources resources) {
        stone1 = BitmapFactory.decodeResource(resources, R.drawable.stone1);

        // resizing the stones
        width = stone1.getWidth();
        height = stone1.getHeight();

        width /= 1.5;
        height /= 1.5;

        width = (int) (width * screenRatioX);
        height = (int) (height * screenRatioY);

        stone1 = Bitmap.createScaledBitmap(stone1, width, height, false);

        y = -height;
    }

}
