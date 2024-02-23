package com.example.spaceshooterlte.Constants;

import android.graphics.Bitmap;
import android.graphics.Matrix;

import com.example.spaceshooterlte.Sprites.EnemyBird;
import com.example.spaceshooterlte.View.GameView;

public class AppConstantMethods {

    public static GameView gameView;

    public AppConstantMethods() {
        gameView = new GameView(gameView.getContext(), (int) AppConstants.SCREEN_WIDTH, (int) AppConstants.SCREEN_HEIGHT);
    }

    public static void gameOverLogic() {
        if (AppConstants.GAME_MODE_ID == 2131230968) {
            GameView.isGameOver = true;
        } else if (AppConstants.GAME_MODE_ID == 2131230967) {
            GameView.isGameOver = false;
        }
    }

    public static void gameLevelObjects() {
        switch (AppConstants.GAME_LEVEL) {
            case 1:
                for (EnemyBird enemyBird : GameView.enemyBird) {
                    enemyBird.isBirdOn = true;
                }
                GameView.coin.isCoinOn = true;
                GameView.stone.isStoneOn = true;
                break;
            case 2:
                GameView.stone.isStoneOn = true;
                GameView.nitro.isNitroOn = true;
                GameView.coin.isCoinOn = true;
                GameView.enemyFlight.isEnemyFlightOn = true;
                break;
            case 3:
                GameView.brickObject.isBrickOn = true;
                GameView.coin.isCoinOn = true;
                GameView.stone.isStoneOn = true;
                GameView.nitro.isNitroOn = true;
                break;
        }
    }

    public static void setTargetScore(int level) {
        switch (level) {
            case 1:
                GameView.targetScore = 100;
                break;
            case 2:
                GameView.targetScore = 150;
                break;
            case 3:
                GameView.targetScore = 250;
                break;
            default:
                GameView.targetScore = 50;
                break;
        }
    }


    public static void scoreIncreaseLogic(int level) {
        switch (level) {
            case 1:
                GameView.score += 4;
                break;
            case 2:
                GameView.score += 8;
                break;
            case 3:
                GameView.score += 12;
                break;
            default:
                GameView.score++;
                break;
        }
    }


    public static void scoreDecreaseLogic(int level) {
        if (AppConstants.GAME_MODE_ID == 2131230967) {
            switch (level) {
                case 1:
                    GameView.score -= 2;
                    break;
                case 2:
                    GameView.score -= 4;
                    break;
                case 3:
                    GameView.score -= 6;
                    break;
                default:
                    GameView.score--;
                    break;
            }
        } else if (AppConstants.GAME_MODE_ID == 2131230968) {
            GameView.isGameOver = true;
        }
    }


    public static Bitmap getResizedBitmap(Bitmap bm, int newWidth, int newHeight) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // CREATE A MATRIX FOR THE MANIPULATION
        Matrix matrix = new Matrix();
        // RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight);
        // "RECREATE" THE NEW BITMAP
        Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, false);
        bm.recycle();
        return resizedBitmap;
    }
}
