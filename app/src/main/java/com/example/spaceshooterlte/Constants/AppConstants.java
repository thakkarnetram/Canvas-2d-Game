package com.example.spaceshooterlte.Constants;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

public class AppConstants {

    public static float SCREEN_WIDTH, SCREEN_HEIGHT;
    public static int GAME_MODE_ID;
    public static int GAME_LEVEL;
    public static int EXITED = 0;
    public static boolean IS_MODE_SELECTED = false;
    public static boolean IS_LEVEL_SELECTED = false;

    public static void initialization(Context context) {
        setScreenSize(context);
    }

    private static void setScreenSize(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        display.getMetrics(displayMetrics);
        float width = displayMetrics.widthPixels;
        float height = displayMetrics.heightPixels;
        AppConstants.SCREEN_WIDTH = width;
        AppConstants.SCREEN_HEIGHT = height;
    }
}
