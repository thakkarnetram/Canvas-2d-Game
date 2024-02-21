package com.example.spaceshooterlte.Sprites;

import static com.example.spaceshooterlte.View.GameView.screenRatioX;
import static com.example.spaceshooterlte.View.GameView.screenRatioY;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.ActionProvider;

import com.example.spaceshooterlte.Constants.AppConstants;
import com.example.spaceshooterlte.R;

public class BrickObject {
    public int x, y, width, height, objectSpeed = 20;
    public boolean isObjectCollected = true, isBrickOn = false;
    public Bitmap brick;

    public BrickObject(Resources resources) {
        brick = BitmapFactory.decodeResource(resources, R.drawable.object);

        // resize
        width = brick.getWidth();
        height = brick.getHeight();

        width /= 10;
        height /= 10;

        width = (int) (width * screenRatioX);
        height = (int) (height * screenRatioY);

        brick = Bitmap.createScaledBitmap(brick, width, height, false);

        x = (int) (AppConstants.SCREEN_WIDTH + 100);
    }

}
