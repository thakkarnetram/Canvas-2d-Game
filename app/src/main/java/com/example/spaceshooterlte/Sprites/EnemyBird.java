package com.example.spaceshooterlte.Sprites;

import static com.example.spaceshooterlte.View.GameView.screenRatioX;
import static com.example.spaceshooterlte.View.GameView.screenRatioY;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;

import com.example.spaceshooterlte.Constants.AppConstants;
import com.example.spaceshooterlte.R;

public class EnemyBird {
    public int birdSpeed = 20;
    public int x = 0, y, width, height, birdCounter = 1;
    public boolean birdShot = true,isBirdOn=false;
    public Bitmap bird1, bird2, bird3, bird4;

    public EnemyBird(Resources resources) {
        bird1 = BitmapFactory.decodeResource(resources, R.drawable.bird1);
        bird2 = BitmapFactory.decodeResource(resources, R.drawable.bird2);
        bird3 = BitmapFactory.decodeResource(resources, R.drawable.bird3);
        bird4 = BitmapFactory.decodeResource(resources, R.drawable.bird4);

        // resizing the birds
        width = bird1.getWidth();
        height = bird1.getHeight();

        width /= 8;
        height /= 8;

        width = (int) (width * screenRatioX);
        height = (int) (height * screenRatioY);

        bird1 = Bitmap.createScaledBitmap(bird1, width, height, false);
        bird2 = Bitmap.createScaledBitmap(bird2, width, height, false);
        bird3 = Bitmap.createScaledBitmap(bird3, width, height, false);
        bird4 = Bitmap.createScaledBitmap(bird4, width, height, false);

        x = (int) (AppConstants.SCREEN_WIDTH + 100);
    }

    public Bitmap getBird() {
        if (birdCounter == 1) {
            birdCounter++;
            return bird1;
        }
        if (birdCounter == 2) {
            birdCounter++;
            return bird2;
        }
        if (birdCounter == 3) {
            birdCounter++;
            return bird3;
        }
        birdCounter = 1;
        return bird4;
    }

    // Rect method to detect collision
    public Rect getCollisionBounds() {
        return new Rect(x, y, x + width, y + height);
    }
}
