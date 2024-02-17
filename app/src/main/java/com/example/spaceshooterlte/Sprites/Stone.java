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
    public boolean isStoneHit = true;
    public Bitmap stone1, stone2, stone3;

    public Stone(Resources resources) {
        stone1 = BitmapFactory.decodeResource(resources, R.drawable.stone1);
        stone2 = BitmapFactory.decodeResource(resources, R.drawable.stone2);
        stone3 = BitmapFactory.decodeResource(resources, R.drawable.stone3);

        // resizing the stones
        width = stone1.getWidth();
        height = stone2.getHeight();

        width /= 1.5;
        height /= 1.5;

        width = (int) (width * screenRatioX);
        height = (int) (height * screenRatioY);

        stone1 = Bitmap.createScaledBitmap(stone1, width, height, false);
        stone2 = Bitmap.createScaledBitmap(stone2, width, height, false);
        stone3 = Bitmap.createScaledBitmap(stone3, width, height, false);

        y = -height;
    }

    public Bitmap getStone() {
        if (stoneCounter == 0) {
            stoneCounter += 1;
            return stone1;
        }
        if (stoneCounter == 1) {
            stoneCounter += 1;
            return stone2;
        }
        stoneCounter = 0;
        return stone3;
    }

    public Rect getCollisionBounds() {
        return new Rect(x, y, x + width, y + height);
    }
}
