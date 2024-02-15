package com.example.spaceshooterlte;

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
    private Random random;
    private Timer timer;
    private SoundPool soundPool;
    private int sound;
    SharedPreferences sharedPreferences;
    private int flightPointerId = -1;

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

        timer = new Timer();

        paint = new Paint();
        // Adding Score using Paint
        paint.setTextSize(128f);
        paint.setColor(Color.WHITE);

        for (int i = 0; i < 4; i++) {
            EnemyBird bird = new EnemyBird(getResources());
            // the ith of the EnemyBird array is = to the Bird Instance
            enemyBird[i] = bird;
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
        background1.x -= 15 * screenRatioX;
        background2.x -= 15 * screenRatioX;

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
            bullet.x += 150 * screenRatioX;

            // checking if the Bullets hit the Enemy Bird
            for (EnemyBird bird : enemyBird) {
                // if bullets hit the Bird ( bird would be Dead & score would increase )
                if (Rect.intersects(bird.getCollisionBounds(), bullet.getCollisionBounds())) {
                    // increase the score
                    score += 1;
                    // if they collide Set the bird position of the screen
                    // and also when the Bird goes of the screen it re-spawns again
                    bird.x = -500;
                    // if the bullet is off the screen it goes in the condition of trashBullet array
                    bullet.x = (int) (screenX + 500);
                    // set birdShot to true
                    bird.birdShot = true;
                }
            }
        }

        // remove bullets from the Trash List too
        for (Bullet bullet : trashList) {
            bulletList.remove(bullet);
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
                int bound = (int) (30 * screenRatioX);
                bird.birdSpeed = random.nextInt(bound);

                // as we are taking random values , edge case might be where it returns 0
                if (bird.birdSpeed < 10 * screenRatioX) {
                    bird.birdSpeed = (int) (10 * screenRatioX);
                }

                // placing the bird to end of the right side of screen
                bird.x = (int) screenX;
                // screenY - height bound cause it might place the bird on Y axis out of the screen
                bird.y = random.nextInt((int) (screenY - bird.height));
                bird.birdShot = false;
            }

            // Collision detection and after logic
            if (Rect.intersects(bird.getCollisionBounds(), flight.getCollisionBounds())) {
                isGameOver = true;
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

                for (EnemyBird bird : enemyBird) {
                    canvas.drawBitmap(bird.getBird(), bird.x, bird.y, paint);
                }

                // drawing the score
                canvas.drawText(score + "", screenX / 2, 200, paint);

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
//        switch (event.getAction()) {
//            case MotionEvent.ACTION_DOWN:
//                // If user taps in the left side of the screen Flight Starts Going Up and Down
//                if (event.getX() < screenX / 2) {
//                    flight.isGoingUp = true;
//                }
//                break;
//            case MotionEvent.ACTION_UP:
//                flight.isGoingUp = false;
//                // If user taps in the right side of the screen Start Shooting
//                if (event.getX() > screenX / 2) {
//                    flight.toShoot += 1;
//                }
//                break;
//
//        }
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
                    if (pointerId == flightPointerId && isPointerActive) {
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
