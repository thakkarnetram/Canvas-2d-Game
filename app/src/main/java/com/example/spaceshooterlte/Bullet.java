package com.example.spaceshooterlte;

import static com.example.spaceshooterlte.GameView.screenRatioX;
import static com.example.spaceshooterlte.GameView.screenRatioY;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;

public class Bullet {
    int x, y, width, height;
    Bitmap bullet;

    Bullet(Resources resources) {
        bullet = BitmapFactory.decodeResource(resources, R.drawable.bullet);

        // resize the bullet
        width = bullet.getWidth();
        height = bullet.getHeight();

        width /= 4;
        height /= 4;

        width = (int) (width * screenRatioX);
        height = (int) (height * screenRatioY);

        bullet = Bitmap.createScaledBitmap(bullet, width, height, false);
    }

    // Collision detection
    Rect getCollisionBounds() {
        return new Rect(x, y, x + width, y + height);
    }
}
