package com.example.spaceshooterlte.Sprites;

import android.graphics.Bitmap;
import android.graphics.Canvas;

import com.example.spaceshooterlte.Constants.AppConstants;
import com.example.spaceshooterlte.View.GameView;

public class Background {
    public int x = 0, y,dy;
    public Bitmap background;
    private int imageHeight;

    public Background(Bitmap res) {
        background = res;
        imageHeight = res.getHeight();
        y = (int) -(imageHeight - (AppConstants.SCREEN_HEIGHT));
    }

    public void update() {
        // Log.d("bgHeight", Integer.toString(CarSprite.imageHeight));// 2539
        y += dy;
    }
    public void draw(Canvas canvas) {
        canvas.drawBitmap(background, x, y, null);
        if ((y + GameView.gameSpeed + 8) > -1) {
            y = (int) (-(imageHeight - AppConstants.SCREEN_HEIGHT) + AppConstants.SCREEN_HEIGHT / 3);
            canvas.drawBitmap(background, x, y, null);
        }
    }

    public void setVector(int dy) {
        this.dy = dy;
    }
}
