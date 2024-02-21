package com.example.spaceshooterlte.Sprites;

import static com.example.spaceshooterlte.View.GameView.screenRatioX;
import static com.example.spaceshooterlte.View.GameView.screenRatioY;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.example.spaceshooterlte.Constants.AppConstants;
import com.example.spaceshooterlte.R;

public class EnemyFlight {
    public int x, y, width, height, enemyFlightSpeed = 20;
    public boolean isEnemyFlightHit = true, isEnemyFlightOn = false;
    public Bitmap enemyFlight;

    public EnemyFlight(Resources resources) {
        enemyFlight = BitmapFactory.decodeResource(resources, R.drawable.enemyplane);

        //resize
        width = enemyFlight.getWidth();
        height = enemyFlight.getHeight();

        width /= 3;
        height /= 3;

        width = (int) (width * screenRatioX);
        height = (int) (height * screenRatioY);

        enemyFlight = Bitmap.createScaledBitmap(enemyFlight, width, height, false);

        x = (int) (AppConstants.SCREEN_WIDTH + 250);
    }

}
