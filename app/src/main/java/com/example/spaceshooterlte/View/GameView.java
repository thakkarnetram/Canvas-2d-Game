package com.example.spaceshooterlte.View;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.view.MotionEvent;
import android.view.SurfaceView;

import com.example.spaceshooterlte.Activities.GameOver;
import com.example.spaceshooterlte.Constants.AppConstants;
import com.example.spaceshooterlte.R;
import com.example.spaceshooterlte.Sprites.Background;
import com.example.spaceshooterlte.Sprites.Bullet;
import com.example.spaceshooterlte.Sprites.Coin;
import com.example.spaceshooterlte.Sprites.EnemyBird;
import com.example.spaceshooterlte.Sprites.Flight;
import com.example.spaceshooterlte.Sprites.Stone;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class GameView extends SurfaceView implements Runnable {
    private Thread thread;
    public boolean isPlaying, isGameOver = false;
    private float screenX, screenY;
    private int score;
    public static float screenRatioX, screenRatioY;
    private Paint paint;
    Background background1, background2;
    Flight flight;
    private List<Bullet> bulletList;
    private EnemyBird[] enemyBird;
    private Coin[] coins;
    private Stone[] stones;
    private Random random;
    private Timer timer;
    private SoundPool soundPool;
    private int sound;
    private int isScoreDeducted=0;
    SharedPreferences sharedPreferences;
    private int flightPointerId = -1;
    private int targetScore; // todo add target in diff levels
    private boolean isTargetReached = false;

    public GameView(Context context, int screenX, int screenY) {
        super(context);

        sharedPreferences = context.getSharedPreferences("game", Context.MODE_PRIVATE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .setUsage(AudioAttributes.USAGE_GAME)
                    .build();

            soundPool = new SoundPool.Builder()
                    .setAudioAttributes(audioAttributes)
                    .build();

        } else {
            soundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
        }

        sound = soundPool.load(getContext(), R.raw.shoot, 1);

        this.screenX = screenX;
        this.screenY = screenY;

        screenRatioX = AppConstants.SCREEN_WIDTH / screenX;
        screenRatioY = AppConstants.SCREEN_HEIGHT / screenY;

        background1 = new Background(screenX, screenY, getResources());
        background2 = new Background(screenX, screenY, getResources());
        background2.x = screenX;

        bulletList = new ArrayList<>();

        flight = new Flight(this, screenY, getResources());
        enemyBird = new EnemyBird[4];

        coins = new Coin[3];

        stones = new Stone[3];

        timer = new Timer();

        paint = new Paint();
        // Adding Score using Paint
        paint.setTextSize(128f);
        paint.setColor(Color.BLACK);

        // init Birds
        for (int i = 0; i < 4; i++) {
            EnemyBird bird = new EnemyBird(getResources());
            // the ith of the EnemyBird array is = to the Bird Instance
            enemyBird[i] = bird;
        }

        // init Coins
        for (int i = 0; i < 3; i++) {
            Coin coin = new Coin(getResources());
            coins[i] = coin;
        }

        // init Stones
        for (int i = 0; i < 3; i++) {
            Stone stone = new Stone(getResources());
            stones[i] = stone;
        }

        random = new Random();
    }


    @Override
    public void run() {
        while (isPlaying) {
            update();
            draw();
            sleep();
        }
    }

    private void update() {
        // -= to move the background towards the left
        if (AppConstants.GAME_LEVEL == 1) {
            background1.x -= 10 * screenRatioX;
            background2.x -= 10 * screenRatioX;
        } else if (AppConstants.GAME_LEVEL == 2) {
            background1.x -= 20 * screenRatioX;
            background2.x -= 20 * screenRatioX;
        } else if (AppConstants.GAME_LEVEL == 3) {
            background1.x -= 30 * screenRatioX;
            background2.x -= 30 * screenRatioX;
        }

        // width < 0 means if the background is completely off the screen
        // we need to reset it to normal position
        if (background1.x + background1.background.getWidth() < 0) {
            background1.x = (int) screenX;
        }
        if (background2.x + background2.background.getWidth() < 0) {
            background2.x = (int) screenX;
        }

        // if the flight is going up we have to bring it down at some point
        if (flight.isGoingUp) {
            flight.y -= 30 * screenRatioY;
        } else {
            flight.y += 30 * screenRatioY;
        }
        if (flight.y < 0) {
            // sets the Y of the flight to 0 if it goes out screen
            flight.y = 0;
        }
        if (flight.y > screenY - flight.height) {
            // if the flight is going off the screen from the bottom
            flight.y = (int) (screenY - flight.height);
        }

        // Bullet logic
        List<Bullet> trashList = new ArrayList<>();

        // iteration of bullet in the BulletList
        for (Bullet bullet : bulletList) {

            // if the bullet is off the screen adding it to the trash list
            if (bullet.x > screenX) {
                trashList.add(bullet);
            }
            // bullet moving speed on X axis ( multiplying with RatioX in order to make it compatible )
            if (AppConstants.GAME_LEVEL == 1) {
                bullet.x += 150 * screenRatioX;
            } else if (AppConstants.GAME_LEVEL == 2) {
                bullet.x += 250 * screenRatioX;
            } else if (AppConstants.GAME_LEVEL == 3) {
                bullet.x += 350 * screenRatioX;
            }

            // checking if the Bullets hit the Enemy Bird
            for (EnemyBird bird : enemyBird) {
                // if bullets hit the Bird ( bird would be Dead & score would increase )
                if (Rect.intersects(bird.getCollisionBounds(), bullet.getCollisionBounds())) {
                    // increase the score
                    if (AppConstants.GAME_LEVEL == 1) {
                        score += 1;
                    } else if (AppConstants.GAME_LEVEL == 2) {
                        score += 2;
                    } else if (AppConstants.GAME_LEVEL == 3) {
                        score += 3;
                    }
                    // if they collide Set the bird position of the screen
                    // and also when the Bird goes of the screen it re-spawns again
                    bird.x = -500;
                    // if the bullet is off the screen it goes in the condition of trashBullet array
                    bullet.x = (int) (screenX + 500);
                    // set birdShot to true
                    bird.birdShot = true;
                }
            }

            for (Coin coin : coins) {
                // if coins hit the bullet increase the score
                if (Rect.intersects(coin.getCollisionBounds(), bullet.getCollisionBounds())) {
                    // increase the score
                    if (AppConstants.GAME_LEVEL == 1) {
                        score += 2;
                    } else if (AppConstants.GAME_LEVEL == 2) {
                        score += 4;
                    } else if (AppConstants.GAME_LEVEL == 3) {
                        score += 6;
                    }
                    // when the coins are hit move em of the screen to respawn them again
                    coin.x = -400;
                    // set coin collected true
                    coin.isCoinCollected = true;
                }
            }
        }

        // remove bullets from the Trash List too
        for (Bullet bullet : trashList) {
            bulletList.remove(bullet);
        }

        // spawn the coins
        for (Coin coin : coins) {
            // move coins towards the flight
            coin.x -= coin.coinSpeed;

            // checking if the coin goes out of screen
            if (coin.x + coin.width < 0) {

                // if coin is missed Game Over
                if (!coin.isCoinCollected) {
                    if (AppConstants.GAME_MODE_ID == 2131230968) {
                        isGameOver = true;
                    } else if (AppConstants.GAME_MODE_ID == 2131230967) {
                        isGameOver = false;
                    }
                    return;
                }

                // increase spawn speed of bird
                int bound = (int) (30 * screenRatioX);
                coin.coinSpeed = random.nextInt(bound);

                // ensuring the random number does not return 0
                if (coin.coinSpeed < 10 * screenRatioX) {
                    coin.coinSpeed = (int) (10 * screenRatioX);
                }
                // placing the bird to end of the right side of screen
                coin.x = (int) screenX;
                // screenY - height bound cause it might place the bird on Y axis out of the screen
                coin.y = random.nextInt((int) (screenY - coin.height));

//                 collision detection of coin & flight
                if (Rect.intersects(coin.getCollisionBounds(), flight.getCollisionBounds())) {
                    if (AppConstants.GAME_MODE_ID == 2131230968) {
                        coin.isCoinCollected = false;
                    } else if (AppConstants.GAME_MODE_ID == 2131230967) {
                        coin.isCoinCollected = true;
                    }
                    return;
                }

            }
        }

        // Stone Logic
        for (Stone stone : stones) {
            stone.x -= stone.stoneSpeed;

            // handle out of screen case
            if (stone.x + stone.width < 0) {

                if (!stone.isStoneHit) {
                    if (AppConstants.GAME_MODE_ID == 2131230968) {
                        isGameOver = true;
                    } else if (AppConstants.GAME_MODE_ID == 2131230967) {
                        isGameOver = false;
                    }
                    return;
                }

                // increase the speed of stone
                int bound = (int) (25 * screenRatioX);
                stone.stoneSpeed = random.nextInt(bound);

                // if random value returns 0
                if (stone.stoneSpeed < 10 * screenRatioX) {
                    stone.stoneSpeed = (int) (10 * screenRatioX);
                }

                // placing the stone
                stone.x = (int) screenX;

                // screenY - height bound cause it might place the bird on Y axis out of the screen
                stone.y = random.nextInt((int) (screenY - stone.height));

            }
            if (Rect.intersects(stone.getCollisionBounds(), flight.getCollisionBounds())) {
                // todo minus logic
//                if (AppConstants.GAME_LEVEL == 1) {
//                    if(isScoreDeducted == 0 ) {
//                        score-=3;
//                    }
//                    isScoreDeducted=0;
//                } else if (AppConstants.GAME_LEVEL == 2) {
//                    score = score - 3;
//                } else if (AppConstants.GAME_LEVEL == 3) {
//                    score = score - 4;
//                }
                if(isScoreDeducted == 0  ) {
                    if(AppConstants.GAME_LEVEL==1) {
                        isScoreDeducted++;
                        score -= 3;
                    }
                }
            }
        }

        // Enemy Bird logic
        // bird object in the Bird Array
        for (EnemyBird bird : enemyBird) {
            // moving the bird towards the Flight
            bird.x -= bird.birdSpeed;

            // if bird goes out of the Screen from the Left Side
            // negative numbers < 0
            if (bird.x + bird.width < 0) {

                if (!bird.birdShot) {
                    // bird was not shot and still off the screen
                    isGameOver = true;
                    return;
                }

                // increase the speed of bird so next time it goes more faster
                if (AppConstants.GAME_LEVEL == 1) {
                    int bound = (int) (20 * screenRatioX);
                    bird.birdSpeed = random.nextInt(bound);
                } else if (AppConstants.GAME_LEVEL == 2) {
                    int bound = (int) (30 * screenRatioX);
                    bird.birdSpeed = random.nextInt(bound);
                } else if (AppConstants.GAME_LEVEL == 3) {
                    int bound = (int) (40 * screenRatioX);
                    bird.birdSpeed = random.nextInt(bound);
                }

                // as we are taking random values , edge case might be where it returns 0
                if (bird.birdSpeed < 10 * screenRatioX) {
                    bird.birdSpeed = (int) (10 * screenRatioX);
                }

                // placing the bird to end of the right side of screen
                bird.x = (int) screenX;
                // screenY - height bound cause it might place the bird on Y axis out of the screen
                bird.y = random.nextInt((int) (screenY - bird.height));
                if (AppConstants.GAME_MODE_ID == 2131230968) {
                    bird.birdShot = false;
                } else if (AppConstants.GAME_MODE_ID == 2131230967) {
                    bird.birdShot = true;
                }
            }

            // Collision detection and after logic
            if (Rect.intersects(bird.getCollisionBounds(), flight.getCollisionBounds())) {
                if (AppConstants.GAME_MODE_ID == 2131230968) {
                    isGameOver = true;
                } else if (AppConstants.GAME_MODE_ID == 2131230967) {
                    isGameOver = false;
                    score -= 1000;
                }
                return;
            }

        }
    }

    private void draw() {
        // checking if surface view has been initialized
        if (getHolder().getSurface().isValid()) {
            // returns the current canvas which is being displayed on screen
            Canvas canvas = getHolder().lockCanvas();
            if (canvas != null) {

                canvas.drawBitmap(background1.background, background1.x, background1.y, paint);
                canvas.drawBitmap(background2.background, background2.x, background2.y, paint);

                if (AppConstants.GAME_LEVEL == 1) {
                    for (EnemyBird bird : enemyBird) {
                        canvas.drawBitmap(bird.getBird(), bird.x, bird.y, paint);
                    }
                    for (Stone stone : stones) {
                        canvas.drawBitmap(stone.getStone(), stone.x, stone.y, paint);
                    }
                }
                if (AppConstants.GAME_LEVEL == 2) {
                    for (Coin coin : coins) {
                        canvas.drawBitmap(coin.getCoin(), coin.x, coin.y, paint);
                    }
                    for (Stone stone : stones) {
                        canvas.drawBitmap(stone.getStone(), stone.x, stone.y, paint);
                    }
                }
                if (AppConstants.GAME_LEVEL == 3) {
                    for (EnemyBird bird : enemyBird) {
                        canvas.drawBitmap(bird.getBird(), bird.x, bird.y, paint);
                    }
                    for (Coin coin : coins) {
                        canvas.drawBitmap(coin.getCoin(), coin.x, coin.y, paint);
                    }
                }


                // drawing the score
                // do not display score in negative
                if (score < 0) {
                    score = 0;
                    canvas.drawText(score + "", screenX / 2, 200, paint);
                } else if (score > 0) {
                    canvas.drawText(score + "", screenX / 2, 200, paint);
                }


                // if game is over Stop the game
                if (isGameOver) {
                    // this would exit the thread
                    isPlaying = false;
                    // draw a dead Flight
                    canvas.drawBitmap(flight.getDeadFlight(), flight.x, flight.y, paint);
                    getHolder().unlockCanvasAndPost(canvas);
                    saveIfHighScore();
                    gameExit();
                    return;
                }


                canvas.drawBitmap(flight.getFlight(), flight.x, flight.y, paint);

                for (Bullet bullet : bulletList) {
                    canvas.drawBitmap(bullet.bullet, bullet.x, bullet.y, paint);
                }

                getHolder().unlockCanvasAndPost(canvas);
            }
        }
    }

    private void gameExit() {
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Intent intent = new Intent(getContext(), GameOver.class);
                getContext().startActivity(intent);
            }
        }, 2500);
    }

    private void saveIfHighScore() {
        if (sharedPreferences.getInt("score", 0) < score) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt("score", score);
            editor.apply();
        }
    }

    private void sleep() {
        try {
            Thread.sleep(17);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void resume() {
        isPlaying = true;
        thread = new Thread(this);
        thread.start();
    }

    public void pause() {
        try {
            isPlaying = false;
            thread.join(); // stops the thread
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int pointerCount = event.getPointerCount(); // gets the no of fingers on the screen
        // checking if there are any active pointers
        boolean isPointerActive = pointerCount > 0;
        // getting all the indexes
        for (int i = 0; i < pointerCount; i++) {

            // getting the Pointer Id
            int pointerId = event.getPointerId(i);

            // getting co-ordinates of the pointer
            float positionX = event.getX(i);
            float positionY = event.getY(i);

            switch (event.getActionMasked()) {
                case MotionEvent.ACTION_DOWN:
                case MotionEvent.ACTION_POINTER_DOWN:
                    // If user taps in the left side of the screen Flight Starts Going Up and Down
                    if (isPointerActive) {
                        if (positionX < screenX / 2) {
                            flight.isGoingUp = true;
                            flightPointerId = pointerId;
                        } else {
                            if (isPointerActive) { // Only shoot if there are active pointers
                                flight.toShoot += 1;
                            } else {
                                flight.toShoot = 0;
                            }
                        }
                    }
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_POINTER_UP:
                    // pointer that went up corresponds to the left side touch, stop going up and down
                    if (pointerId == flightPointerId) {
                        flight.isGoingUp = false;
                        flightPointerId = -1;
                    }
                    // If user taps in the right side of the screen Start Shooting
                    else {
                        if (isPointerActive) {
                            flight.toShoot += 1;
                        } else {
                            flight.toShoot = 0;
                        }
                    }
                    break;
            }
        }
        return true;
    }

    public void newBullet() {

        // Playing Sound
        soundPool.play(sound, 1, 1, 0, 0, 1);

        Bullet bullet = new Bullet(getResources());
        // bullet would be placed next to the fans of the Flight
        bullet.x = flight.x + flight.width;
        bullet.y = flight.y + (flight.height / 2);
        // add bullet to BulletList
        bulletList.add(bullet);
    }
}
