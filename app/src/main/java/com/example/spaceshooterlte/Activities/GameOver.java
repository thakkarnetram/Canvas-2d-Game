package com.example.spaceshooterlte.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.spaceshooterlte.Constants.AppConstants;
import com.example.spaceshooterlte.R;
import com.example.spaceshooterlte.View.GameView;

public class GameOver extends AppCompatActivity {

    Button btnRestartGame, btnStart;
    TextView tvHighScore;
    ImageButton ibHomeBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        hideSystemUI();
        setContentView(R.layout.activity_game_over);
        restartGame();
        setHighScore();
        returnHome();
    }

    private void setHighScore() {
        tvHighScore = (TextView) findViewById(R.id.tvHighScore);
        final SharedPreferences sharedPreferences = getSharedPreferences("game", MODE_PRIVATE);
        tvHighScore.setText("HighScore : " + sharedPreferences.getInt("score", 0));
    }

    private void restartGame() {
        btnRestartGame = (Button) findViewById(R.id.btnRestart);
        btnRestartGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GameOver.this, GameActivity.class);
                GameView.score= -1;
                startActivity(intent);
                finish();
            }
        });
    }

    private void returnHome() {
        ibHomeBtn = (ImageButton) findViewById(R.id.ibHomeBtn);
        btnStart = (Button) findViewById(R.id.btnStart);
        ibHomeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GameOver.this, MainActivity.class);
                startActivity(intent);
                AppConstants.EXITED = 1;
                AppConstants.IS_LEVEL_SELECTED = false;
                GameView.score= -1;
                finish();
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