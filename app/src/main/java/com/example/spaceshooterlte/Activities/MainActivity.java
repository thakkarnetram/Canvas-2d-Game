package com.example.spaceshooterlte.Activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import com.example.spaceshooterlte.Constants.AppConstants;
import com.example.spaceshooterlte.R;
import com.nex3z.togglebuttongroup.SingleSelectToggleGroup;
import com.nex3z.togglebuttongroup.button.LabelToggle;

public class MainActivity extends AppCompatActivity {

    SingleSelectToggleGroup toggleMode, toggleLevel;
    Button btnStart;
    ImageButton btnExitGame;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        hideSystemUI();
        setContentView(R.layout.activity_main);
        AppConstants.initialization(this.getApplicationContext());
        configureGameSetting();
        playGame();
        exitGame();
    }

    private void playGame() {
        btnStart = (Button) findViewById(R.id.btnStart);
        btnStart.setEnabled(false);
        btnStart.setBackgroundColor(Color.GRAY);
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, GameActivity.class);
                startActivity(i);
                finish();
            }
        });
    }

    private void configureGameSetting() {
        // Mode Select
        toggleMode = (SingleSelectToggleGroup) findViewById(R.id.ltModes);
        toggleMode.setOnCheckedChangeListener(new SingleSelectToggleGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(SingleSelectToggleGroup group, int checkedId) {
                if (AppConstants.EXITED == 1) {
                    btnStart.setEnabled(false);
                    btnStart.setBackgroundColor(Color.GRAY);
                    AppConstants.EXITED = 0;
                }
                if (AppConstants.EXITED == 0) {
                    AppConstants.GAME_MODE_ID = checkedId;
                    AppConstants.IS_MODE_SELECTED = true;
                    if (AppConstants.IS_LEVEL_SELECTED) {
                        btnStart.setEnabled(true);
                        btnStart.setBackgroundColor(Color.BLACK);
                    }
                }
            }
        });

        // Level Select
        toggleLevel = (SingleSelectToggleGroup) findViewById(R.id.ltSessionSetting);
        toggleLevel.setOnCheckedChangeListener(new SingleSelectToggleGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(SingleSelectToggleGroup group, int checkedId) {
                if (!AppConstants.IS_LEVEL_SELECTED && !AppConstants.IS_MODE_SELECTED) {
                    btnStart.setEnabled(false);
                    btnStart.setBackgroundColor(Color.GRAY);
                    AppConstants.EXITED = 0;
                    AppConstants.IS_LEVEL_SELECTED = true;
                }
                if (checkedId == 2131231229 && AppConstants.IS_MODE_SELECTED) {
                    AppConstants.GAME_LEVEL = 1;
                    btnStart.setEnabled(true);
                    btnStart.setBackgroundColor(Color.BLACK);
                } else if (checkedId == 2131231230 && AppConstants.IS_MODE_SELECTED) {
                    AppConstants.GAME_LEVEL = 2;
                    btnStart.setEnabled(true);
                    btnStart.setBackgroundColor(Color.BLACK);
                } else if (checkedId == 2131231231 && AppConstants.IS_MODE_SELECTED) {
                    AppConstants.GAME_LEVEL = 3;
                    btnStart.setEnabled(true);
                    btnStart.setBackgroundColor(Color.BLACK);
                }
            }
        });
    }

    private void exitGame() {
        btnExitGame = (ImageButton) findViewById(R.id.ibExitBtn);
        btnExitGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                System.exit(0);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        hideSystemUI();
    }


    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            hideSystemUI();
        }
    }

    public void hideSystemUI() {
        // Enables regular immersive mode.
        // For "lean back" mode, remove SYSTEM_UI_FLAG_IMMERSIVE.
        // Or for "sticky immersive," replace it with SYSTEM_UI_FLAG_IMMERSIVE_STICKY

        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE
                        // Set the content to appear under the system bars so that the
                        // content doesn't resize when the system bars hide and show.
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        // Hide the nav bar and status bar
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN);
    }
}