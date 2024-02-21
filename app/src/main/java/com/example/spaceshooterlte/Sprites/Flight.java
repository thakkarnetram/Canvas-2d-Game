package com.example.spaceshooterlte.Sprites;

import static com.example.spaceshooterlte.View.GameView.screenRatioX;
import static com.example.spaceshooterlte.View.GameView.screenRatioY;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;

import com.example.spaceshooterlte.Constants.AppConstants;
import com.example.spaceshooterlte.View.GameView;
import com.example.spaceshooterlte.R;

public class Flight {
    public int toShoot;
    public Boolean isGoingUp = false;
    public int x, y, width, height, wingCounter = 0, shootCounter = 1;
    public Bitmap flight1, flight2, shoot1, shoot2, shoot3, shoot4, shoot5, deadFlight;
    private GameView gameView;

    public Flight(GameView gameView, int screenY, Resources resources) {
        // gameView ref
        this.gameView = gameView;
        // Inserting the images into the Bitmap
        flight1 = BitmapFactory.decodeResource(resources, R.drawable.fly1);
        flight2 = BitmapFactory.decodeResource(resources, R.drawable.fly2);

        // getting the width
        width = flight1.getWidth();
        height = flight1.getHeight();

        // reducing the size
        width /= 3;
        height /= 3;

        width = (int) (width * screenRatioX);
        height = (int) (height * screenRatioY);

        // resize the flight
        flight1 = Bitmap.createScaledBitmap(flight1, width, height, false);
        flight2 = Bitmap.createScaledBitmap(flight2, width, height, false);

        // insert the shooting bullets bitmaps
        shoot1 = BitmapFactory.decodeResource(resources, R.drawable.shoot1);
        shoot2 = BitmapFactory.decodeResource(resources, R.drawable.shoot2);
        shoot3 = BitmapFactory.decodeResource(resources, R.drawable.shoot3);
        shoot4 = BitmapFactory.decodeResource(resources, R.drawable.shoot4);
        shoot5 = BitmapFactory.decodeResource(resources, R.drawable.shoot5);

        // resize the bullets
        shoot1 = Bitmap.createScaledBitmap(shoot1, width, height, false);
        shoot2 = Bitmap.createScaledBitmap(shoot2, width, height, false);
        shoot3 = Bitmap.createScaledBitmap(shoot3, width, height, false);
        shoot4 = Bitmap.createScaledBitmap(shoot4, width, height, false);
        shoot5 = Bitmap.createScaledBitmap(shoot5, width, height, false);

        // dead flight
        deadFlight = BitmapFactory.decodeResource(resources, R.drawable.dead);

        // resize the deadFlight
        deadFlight = Bitmap.createScaledBitmap(deadFlight, width, height, false);

        y = (int) (AppConstants.SCREEN_HEIGHT  -  100);
    }

    public Bitmap getFlight() {

        // shooting logic
        if (toShoot != 0) {
            if (shootCounter == 1) {
                shootCounter++;
                return shoot1;
            }
            if (shootCounter == 2) {
                shootCounter++;
                return shoot2;
            }
            if (shootCounter == 3) {
                shootCounter++;
                return shoot3;
            }
            if (shootCounter == 4) {
                shootCounter++;
                return shoot4;
            }

            shootCounter = 1;
            toShoot -= 1;
            gameView.newBullet();

            return shoot5;
        }

        // basically sort of animation in which flight1 and flight2 is called one after other again and again
        if (wingCounter == 0) {
            wingCounter += 1;
            return flight1;
        }
        wingCounter -= 1;
        return flight2;
    }

    // Collision detection
    public Rect getCollisionBounds() {
        return new Rect(x, y, x + width, y + height);
    }

    // Get the DeadFlight
    public Bitmap getDeadFlight() {
        return deadFlight;
    }
}
