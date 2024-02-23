package com.example.spaceshooterlte.View;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.view.MotionEvent;
import android.view.SurfaceView;

import com.example.spaceshooterlte.Activities.GameOver;
import com.example.spaceshooterlte.Constants.AppConstantMethods;
import com.example.spaceshooterlte.Constants.AppConstants;
import com.example.spaceshooterlte.Constants.CollisionDetection;
import com.example.spaceshooterlte.R;
import com.example.spaceshooterlte.Sprites.Background;
import com.example.spaceshooterlte.Sprites.BrickObject;
import com.example.spaceshooterlte.Sprites.Bullet;
import com.example.spaceshooterlte.Sprites.Coin;
import com.example.spaceshooterlte.Sprites.EnemyBird;
import com.example.spaceshooterlte.Sprites.EnemyFlight;
import com.example.spaceshooterlte.Sprites.Flight;
import com.example.spaceshooterlte.Sprites.Nitro;
import com.example.spaceshooterlte.Sprites.Stone;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class GameView extends SurfaceView implements Runnable {
    private Thread thread;
    public boolean isPlaying;
    public static boolean isGameOver = false;
    private float screenX, screenY;
    public static int score;
    public static int targetScore;
    public static boolean isTargetReached = false;
    public static float screenRatioX, screenRatioY;
    private Paint paint, nitro1, nitro2;
    public Background background1;
    public Flight flight;
    public static BrickObject brickObject;
    public static Stone stone;
    public static Coin coin;
    public static Nitro nitro;
    public static EnemyFlight enemyFlight;
    public List<Bullet> bulletList;
    public static EnemyBird[] enemyBird;
    private Random random;
    private Timer timer;
    private SoundPool soundPool;
    private int sound;
    public int bound;
    SharedPreferences sharedPreferences;
    private int flightPointerId = -1;
    private CollisionDetection collisionDetection;
    private boolean gameStarted = false;
    private boolean isCollided;
    public int boostSpeed = 30;
    public static int gameSpeed = 6;
    public int nitroCounter = -1;
    public int maxNitroSpeed = 900;

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

        background1 = new Background(AppConstantMethods.getResizedBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.background), (int) AppConstants.SCREEN_WIDTH, (int) (AppConstants.SCREEN_HEIGHT * 4)));
        background1.setVector(gameSpeed);

        bulletList = new ArrayList<>();

        flight = new Flight(this, screenY, getResources());

        brickObject = new BrickObject(getResources());

        enemyBird = new EnemyBird[4];

        coin = new Coin(getResources());

        stone = new Stone(getResources());

        nitro = new Nitro(getResources());

        enemyFlight = new EnemyFlight(getResources());

        timer = new Timer();

        collisionDetection = new CollisionDetection();

        paint = new Paint();
        nitro1 = new Paint();
        nitro2 = new Paint();
        // Adding Score using Paint
        paint.setTextSize(60f);
        paint.setColor(Color.BLACK);

        // random int bound
        bound = (int) (30 * screenRatioX);

        // init Birds
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

    public void resumeGame() {
        gameStarted = true;
    }

    private void update() {
        resumeGame();
        updateNitro();
        if (gameStarted) {
            gameLogic();
            AppConstantMethods.gameLevelObjects();
        }
    }

    private void gameLogic() {
        backgroundLogic();
        flightLogic();
        bulletLogic();
        coinLogic(); // +
        stoneLogic(); // -
        enemyBirdLogic(); // +
        brickObjectLogic(); // -
        nitroLogic(); //  +
        enemyFlightLogic(); // -
        targetReached();

    }

    private void targetReached() {
        if (score != 0) {
            if (score == targetScore || score > targetScore) {
                isGameOver = true;
            } else {
                isGameOver = false;
            }
        }
    }

    private void backgroundLogic() {
        background1.update();
    }

    private void flightLogic() {
        // if the flight is going up we have to bring it down at some point
        if (flight.isGoingUp) {
            flight.x -= 30 * screenRatioX;
        } else {
            flight.x += 30 * screenRatioX;
        }
        if (flight.x < 0) {
            // sets the Y of the flight to 0 if it goes out screen
            flight.x = 0;
        }
        if (flight.x > screenX - flight.width) {
            // if the flight is going off the screen from the bottom
            flight.x = (int) (screenX - flight.width);
        }
    }

    private void bulletLogic() {
        // Bullet logic
        List<Bullet> trashList = new ArrayList<>();

        // iteration of bullet in the BulletList
        for (Bullet bullet : bulletList) {

            // if the bullet is off the screen adding it to the trash list
            if (bullet.y > screenY) {
                trashList.add(bullet);
            }
            // bullet moving speed on X axis ( multiplying with RatioX in order to make it compatible )
            bullet.y -= 150 * screenRatioY;
            // checking if the Bullets hit the Enemy Bird
            for (EnemyBird bird : enemyBird) {
                // if bullets hit the Bird ( bird would be Dead & score would increase )
                isCollided = collisionDetection.isCollisionDetected(bullet.bullet, bullet.x, bullet.y, bird.getBird(), bird.x, bird.y);
                if (isCollided) {
                    // increase the score
                    targetReached();
                    AppConstantMethods.scoreIncreaseLogic(AppConstants.GAME_LEVEL);
                    if (score < 0) {
                        score = 0;
                    }
                    // if they collide Set the bird position of the screen
                    // and also when the Bird goes of the screen it re-spawns again
                    bird.x = -500;
                    // if the bullet is off the screen it goes in the condition of trashBullet array
                    bullet.y = (int) (screenY + 500);
                    // set birdShot to true
                    bird.birdShot = true;
                }
            }

            // if coins hit the bullet increase the score
            isCollided = collisionDetection.isCollisionDetected(bullet.bullet, bullet.x, bullet.y, coin.coin, coin.x, coin.y);
            if (isCollided) {
                // increase the score
                targetReached();
                AppConstantMethods.scoreIncreaseLogic(AppConstants.GAME_LEVEL);
                if (score < 0) {
                    score = 0;
                }
                // when the coins are hit move em of the screen to respawn them again
                coin.x = -400;
                // set coin collected true
                coin.isCoinCollected = true;
            }

            // nitro and bullet logic
            isCollided = collisionDetection.isCollisionDetected(bullet.bullet, bullet.x, bullet.y, nitro.nitro, nitro.x, nitro.y);
            if (isCollided) {
                if (nitroCounter == -1) {
                    activateNitro();
                } else if (nitroCounter > 0) {
                    if (nitroCounter > 1000) {
                        nitroCounter = 1000;
                    } else {
                        nitroCounter += 100;
                    }
                }
                score += 5;
                AppConstantMethods.gameOverLogic();
                nitro.x = -500;
                nitro.isNitroCollected = true;
            }
        }
        // remove bullets from the Trash List too
        for (Bullet bullet : trashList) {
            bulletList.remove(bullet);
        }
    }

    private void coinLogic() {
        // spawn the coins
        // move coins towards the flight
        coin.y += coin.coinSpeed;

        // checking if the coin goes out of screen
        if (coin.y > screenY) {

            // if coin is missed Game Over
            if (!coin.isCoinCollected) {
                AppConstantMethods.gameOverLogic();
                return;
            }

            // increase spawn speed of coin
            coin.coinSpeed = random.nextInt(bound);

            // ensuring the random number does not return 0
            if (coin.coinSpeed < 10 * screenRatioX) {
                coin.coinSpeed = (int) (10 * screenRatioX);
            }

            // place the coin at a random position at the top of the screen
            coin.y = -coin.height;
            coin.x = random.nextInt((int) (screenX - coin.width));
        }
    }


    private void stoneLogic() {
        // Stone Logic
        stone.y += stone.stoneSpeed;

        // handle out of screen case
        if (stone.y > screenY) {

            if (!stone.isStoneHit) {
                AppConstantMethods.gameOverLogic();
                return;
            }

            // increase the speed of stone
            stone.stoneSpeed = random.nextInt(bound);

            // if random value returns 0
            if (stone.stoneSpeed < 10 * screenRatioX) {
                stone.stoneSpeed = (int) (10 * screenRatioX);
            }

            // place the stone at a random position at the top of the screen
            stone.y = -stone.height;
            stone.x = random.nextInt((int) (screenX - stone.width));

        }
        isCollided = collisionDetection.isCollisionDetected(stone.stone1, stone.x, stone.y, flight.getFlight(), flight.x, flight.y);
        if (isCollided) {
            AppConstantMethods.scoreDecreaseLogic(AppConstants.GAME_LEVEL);
            AppConstantMethods.gameOverLogic();
            stone.x = -500;
            stone.isStoneHit = true;
        }
    }

    private void enemyBirdLogic() {
        // Enemy Bird logic
        // bird object in the Bird Array
        for (EnemyBird bird : enemyBird) {
            // moving the bird towards the Flight
            bird.y += bird.birdSpeed;

            // if bird goes out of the Screen from the Left Side
            // negative numbers < 0
            if (bird.y > screenY) {

                if (!bird.birdShot) {
                    AppConstantMethods.gameOverLogic();
                    return;
                }

                // increase the speed of bird so next time it goes more faster
                if (AppConstants.GAME_LEVEL == 1) {
                    bird.birdSpeed = random.nextInt(bound + 5);
                } else if (AppConstants.GAME_LEVEL == 2) {
                    bird.birdSpeed = random.nextInt(bound + 10);
                } else if (AppConstants.GAME_LEVEL == 3) {
                    bird.birdSpeed = random.nextInt(bound + 15);
                }

                // as we are taking random values , edge case might be where it returns 0
                if (bird.birdSpeed < 10 * screenRatioX) {
                    bird.birdSpeed = (int) (10 * screenRatioX);
                }

                // placing the bird to end of the right side of screen
                bird.y = -bird.height;

                // placing the bird randomly along the X axis
                bird.x = random.nextInt((int) (screenX - bird.width));

                if (AppConstants.GAME_MODE_ID == 2131230968) {
                    bird.birdShot = false;
                } else if (AppConstants.GAME_MODE_ID == 2131230967) {
                    bird.birdShot = true;
                }
            }

            // Collision detection and after logic
            isCollided = collisionDetection.isCollisionDetected(bird.getBird(), bird.x, bird.y, flight.getFlight(), flight.x, flight.y);
            if (isCollided) {
                AppConstantMethods.gameOverLogic();
                bird.x = -500;
                bird.birdShot = true;
            }
        }
    }

    private void brickObjectLogic() {
        // Brick spawning
        brickObject.y += brickObject.objectSpeed;

        // out of screen check
        if (brickObject.y > screenY) {
            if (!brickObject.isObjectCollected) {
                AppConstantMethods.gameOverLogic();
                return;
            }
            brickObject.objectSpeed = random.nextInt(bound);

            // random value is 0
            if (brickObject.objectSpeed < 10 * screenRatioX) {
                brickObject.objectSpeed = (int) (10 * screenRatioX);
            }
            // placing the brick to end of the right side of screen
            brickObject.y = -brickObject.height;
            brickObject.x = random.nextInt((int) (screenX - brickObject.width));

            // collision
            isCollided = collisionDetection.isCollisionDetected(brickObject.brick, brickObject.x, brickObject.y, flight.getFlight(), flight.x, flight.y);
            if (isCollided) {
                AppConstantMethods.scoreDecreaseLogic(AppConstants.GAME_LEVEL);
                AppConstantMethods.gameOverLogic();
                brickObject.x = -500;
                brickObject.isObjectCollected = true;
            }
        }
    }

    private void nitroLogic() {
        // nitro movement
        nitro.y += nitro.nitroSpeed;

        // handling out of screen
        if (nitro.y > screenY) {
            if (!nitro.isNitroCollected) {
                AppConstantMethods.gameOverLogic();
                return;
            }
            nitro.nitroSpeed = random.nextInt(bound);

            // random val is 0
            if (nitro.nitroSpeed < 10 * screenRatioX) {
                nitro.nitroSpeed = (int) (10 * screenRatioX);
            }

            // place the nitro at a random position at the top of the screen
            nitro.y = -nitro.height;
            nitro.x = random.nextInt((int) (screenX - nitro.width));
        }
        isCollided = collisionDetection.isCollisionDetected(nitro.nitro, nitro.x, nitro.y, flight.getFlight(), flight.x, flight.y);
        if (isCollided) {
            if (nitroCounter == -1) {
                activateNitro();
            } else if (nitroCounter > 0) {
                if (nitroCounter > 1000) {
                    nitroCounter = 1000;
                } else {
                    nitroCounter += 100;
                }
            }
            score += 5;
            AppConstantMethods.gameOverLogic();
            nitro.x = -500;
            nitro.isNitroCollected = true;
        }
    }

    private void updateNitro() {
        if (nitroCounter > 0) {
            nitroCounter--;
        } else if (nitroCounter < 0) {
        } else {
            deactivateNitro();
        }
    }

    public void activateNitro() {
        nitroCounter = maxNitroSpeed;
        gameSpeed += boostSpeed;
        background1.setVector(gameSpeed);
    }


    public void deactivateNitro() {
        gameSpeed -= boostSpeed;
        background1.setVector(gameSpeed);
        nitroCounter--;
    }

    private void drawNitro(Canvas canvas){
        float offsetX = AppConstants.SCREEN_WIDTH * 0.101f;
        float offsetY = AppConstants.SCREEN_HEIGHT * 0.079f;
        float height = AppConstants.SCREEN_HEIGHT * 0.06f;
        float width =  AppConstants.SCREEN_WIDTH * 0.50f;
        RectF dstRect1 = new RectF(offsetX, offsetY,
                (offsetX + width + (AppConstants.SCREEN_WIDTH  * 0.05f)),
                (offsetY + height));
        nitro1.setStrokeWidth(10);
        nitro1.setColor(Color.TRANSPARENT);
        nitro1.setStyle(Paint.Style.STROKE);
        nitro1.setColor(Color.WHITE);
        canvas.drawRoundRect(dstRect1, 200, 200, nitro1);
        nitro1.setStrokeWidth(10);
        nitro2.setColor(Color.WHITE);
        RectF dstRect2 = new RectF(offsetX, offsetY,
                (offsetX + nitroCounter + (AppConstants.SCREEN_WIDTH  * 0.05f)),
                (offsetY + height));
        canvas.drawRoundRect(dstRect2, 200, 200, nitro2);
    }

    private void enemyFlightLogic() {
        // move the flight towards our player
        enemyFlight.y += enemyFlight.enemyFlightSpeed;

        // out of screen handle
        if (enemyFlight.y > +screenY) {
            if (!enemyFlight.isEnemyFlightHit) {
                AppConstantMethods.gameOverLogic();
                return;
            }
            enemyFlight.enemyFlightSpeed = random.nextInt(bound);

            if (enemyFlight.enemyFlightSpeed < 10 * screenRatioX) {
                enemyFlight.enemyFlightSpeed = (int) (10 * screenRatioX);
            }
            // place the enemy flight at a random position at the top of the screen
            enemyFlight.y = -enemyFlight.height;
            enemyFlight.x = random.nextInt((int) (screenX - enemyFlight.width));

        }
        isCollided = collisionDetection.isCollisionDetected(enemyFlight.getEnemyFlight(), enemyFlight.x, enemyFlight.y, flight.getFlight(), flight.x, flight.y);
        if (isCollided) {
            AppConstantMethods.scoreDecreaseLogic(AppConstants.GAME_LEVEL);
            AppConstantMethods.gameOverLogic();
            enemyFlight.x = -500;
            enemyFlight.isEnemyFlightHit = true;
        }
    }

    private void draw() {
        // checking if surface view has been initialized
        if (getHolder().getSurface().isValid()) {

            // returns the current canvas which is being displayed on screen
            Canvas canvas = getHolder().lockCanvas();

            if (canvas != null) {

                background1.draw(canvas);
                if (stone.isStoneOn) {
                    canvas.drawBitmap(stone.stone1, stone.x, stone.y, paint);
                }

                if (brickObject.isBrickOn) {
                    canvas.drawBitmap(brickObject.brick, brickObject.x, brickObject.y, paint);
                }

                if (enemyFlight.isEnemyFlightOn) {
                    canvas.drawBitmap(enemyFlight.enemyFlight, enemyFlight.x, enemyFlight.y, paint);
                }

                for (EnemyBird bird : enemyBird) {
                    if (bird.isBirdOn) {
                        canvas.drawBitmap(bird.getBird(), bird.x, bird.y, paint);
                    }
                }
                if (coin.isCoinOn) {
                    canvas.drawBitmap(coin.coin, coin.x, coin.y, paint);
                }
                if (nitro.isNitroOn) {
                    canvas.drawBitmap(nitro.nitro, nitro.x, nitro.y, paint);
                }
                if (score < 1) {
                    canvas.drawText("Score : " + -1 + "", (int) (screenX/1.2), 100, paint);
                } else {
                    canvas.drawText("Score : " + score + "", (int) (screenX / 1.2), 100, paint);
                }
                AppConstantMethods.setTargetScore(AppConstants.GAME_LEVEL);
                canvas.drawText("Target Score " + targetScore + "", (int) (screenX / 1.35), 185, paint);
                drawNitro(canvas);
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
                        if (positionX < screenY / 2) {
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
        // Calculate the initial x-coordinate of the bullet to center it horizontally relative to the flight
        bullet.x = flight.x + (flight.width / 2) - (bullet.width / 2); // Centered horizontally
        bullet.y = flight.y; // Above the flight

        // Add bullet to BulletList
        bulletList.add(bullet);
    }
}
