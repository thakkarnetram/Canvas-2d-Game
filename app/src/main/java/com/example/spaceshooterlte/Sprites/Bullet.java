package com.example.spaceshooterlte.Sprites;

import static com.example.spaceshooterlte.View.GameView.screenRatioX;
import static com.example.spaceshooterlte.View.GameView.screenRatioY;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;

import com.example.spaceshooterlte.R;

public class Bullet {
    public int x, y, width, height;
    public Bitmap bullet;

    public Bullet(Resources resources) {
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
    public Rect getCollisionBounds() {
        return new Rect(x, y, x + width, y + height);
    }
}
