package com.example.spaceshooterlte.View;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceView;

import com.example.spaceshooterlte.Activities.GameOver;
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
    public boolean isPlaying, isGameOver = false;
    private float screenX, screenY;
    private int score;
    public static float screenRatioX, screenRatioY;
    private Paint paint;
    private Background background1, background2;
    private Flight flight;
    private BrickObject brickObject;
    private Stone stone;
    private Coin coin;
    private Nitro nitro;
    private EnemyFlight enemyFlight;
    private List<Bullet> bulletList;
    private EnemyBird[] enemyBird;
    private Random random;
    private Timer timer;
    private SoundPool soundPool;
    private int sound;
    public int bound;
    SharedPreferences sharedPreferences;
    private int flightPointerId = -1;
    private CollisionDetection collisionDetection;
    private int targetScore; // todo add target in diff levels
    private boolean isTargetReached = false;
    private boolean gameStarted = false;
    private boolean isCollided;
    public int boostSpeed = 30;
    public static int gameSpeed = 6;
    private int prevCollision = -1; //  0 means not yet collided

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

        background1 = new Background(getResizedBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.background), (int) AppConstants.SCREEN_WIDTH, (int) (AppConstants.SCREEN_HEIGHT * 4)));
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
        // Adding Score using Paint
        paint.setTextSize(128f);
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
        Log.e("ok", "update: Prev - Collision " + prevCollision + " boolean " + isCollided);
        resumeGame();
        if (gameStarted) {
            gameLogic();
            gameLevelObjects();
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

    private void gameOverLogic() {
        if (AppConstants.GAME_MODE_ID == 2131230968) {
            isGameOver = true;
        } else if (AppConstants.GAME_MODE_ID == 2131230967) {
            isGameOver = false;
        }
    }


    private void gameLevelObjects() {
        switch (AppConstants.GAME_LEVEL) {
            case 1:
                for (EnemyBird enemyBird : enemyBird) {
                    enemyBird.isBirdOn = false;
                }
                coin.isCoinOn = true;
                stone.isStoneOn = true;
                break;
            case 2:
                stone.isStoneOn = true;
                nitro.isNitroOn = true;
                enemyFlight.isEnemyFlightOn = true;
                break;
            case 3:
                brickObject.isBrickOn = true;
                coin.isCoinOn = true;
                stone.isStoneOn = true;
                break;
        }
    }

    private void backgroundLogic() {
        // todo game speed change for levels
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
            if (bullet.x > screenY) {
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
                isCollided = collisionDetection.isCollisionDetected(bullet.bullet, bullet.x, bullet.y, bird.getBird(), bird.x, bird.y);
                if (isCollided) {
                    // increase the score
                    if (AppConstants.GAME_LEVEL == 1) {
                        score += 3;
                    } else if (AppConstants.GAME_LEVEL == 2) {
                        score += 5;
                    } else if (AppConstants.GAME_LEVEL == 3) {
                        score += 7;
                    }
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
                if (AppConstants.GAME_LEVEL == 1) {
                    score += 2;
                } else if (AppConstants.GAME_LEVEL == 2) {
                    score += 4;
                } else if (AppConstants.GAME_LEVEL == 3) {
                    score += 6;
                }
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
                if (AppConstants.GAME_LEVEL == 1) {
                    background1.y -= boostSpeed * screenRatioY;
                } else if (AppConstants.GAME_LEVEL == 2) {
                    background1.x -= boostSpeed + 10 * screenRatioX;
                    background2.x -= boostSpeed + 10 * screenRatioX;
                } else if (AppConstants.GAME_LEVEL == 3) {
                    background1.x -= boostSpeed + 10 * screenRatioX;
                    background2.x -= boostSpeed + 10 * screenRatioX;
                }
                nitro.x = -500;
                nitro.x = (int) (screenX + 700);
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
                if (AppConstants.GAME_MODE_ID == 2131230968) {
                    isGameOver = true;
                } else if (AppConstants.GAME_MODE_ID == 2131230967) {
                    isGameOver = false;
                }
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
                gameOverLogic();
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
        if (isCollided && prevCollision != 1) {
            score -= 1; // Reduce score by 1 only when a collision occurs for the first time
            gameOverLogic();
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
                    // bird was not shot and still off the screen
                    isGameOver = true;
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
                gameOverLogic();
                prevCollision = 1;
            }
        }
    }

    private void brickObjectLogic() {
        // Brick spawning
        brickObject.y += brickObject.objectSpeed;

        // out of screen check
        if (brickObject.y > screenY) {
            if (!brickObject.isObjectCollected) {
                gameOverLogic();
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
            isCollided = CollisionDetection.isCollisionDetected(brickObject.brick, brickObject.x, brickObject.y, flight.getFlight(), flight.x, flight.y);
            if (isCollided) {
                gameOverLogic();
                prevCollision = 1;
            }
        }
    }

    private void nitroLogic() {
        // nitro movement
        nitro.y += nitro.nitroSpeed;

        // handling out of screen
        if (nitro.y > screenY) {
            if (!nitro.isNitroCollected) {
                gameOverLogic();
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

            // todo collision of nitro & flight

        }
    }

    private void enemyFlightLogic() {
        // move the flight towards our player
        enemyFlight.y += enemyFlight.enemyFlightSpeed;

        // out of screen handle
        if (enemyFlight.y > +screenY) {
            if (!enemyFlight.isEnemyFlightHit) {
                if (AppConstants.GAME_MODE_ID == 2131230968) {
                    isGameOver = true;
                } else if (AppConstants.GAME_MODE_ID == 2131230967) {
                    isGameOver = false;
                }
                return;
            }
            enemyFlight.enemyFlightSpeed = random.nextInt(bound);

            if (enemyFlight.enemyFlightSpeed < 10 * screenRatioX) {
                enemyFlight.enemyFlightSpeed = (int) (10 * screenRatioX);
            }
            // place the enemy flight at a random position at the top of the screen
            enemyFlight.y = -enemyFlight.height;
            enemyFlight.x = random.nextInt((int) (screenX - enemyFlight.width));

            isCollided = CollisionDetection.isCollisionDetected(flight.getFlight(), flight.x, flight.y, enemyFlight.enemyFlight, enemyFlight.x, enemyFlight.y);
            prevCollision = 0;
            if (isCollided) {
                gameOverLogic();
                prevCollision = 1;
            }
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
                } else {
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
