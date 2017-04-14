package ioan.ghisoi.disertation;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.test.suitebuilder.TestMethod;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class Game extends AppCompatActivity {
    private ImageView mPlayFullTrack;
    private boolean mMainPlayerFinished = false;
    private boolean mShoulUseAsPlay = false;
    private List<Step> mPieceArray = new ArrayList<>();
    private List<Placeholder> mPlaceholderArray = new ArrayList<>();
    private String[] mDefaultColors = {"musired","musigreen","musiblue","musiyellow","musipink","musiblack"};
    private List<Step> mPieceOrder = new ArrayList<>();


    private FrameLayout mContainerView;
    private LinearLayout mSolutionView;
    CountDownTimer timer;

    private MediaPlayer mp;
    private Handler mHandler;
    private Runnable mRunnable;
    private TextView mTimer;

    void openCustomDialog(String text, int starsId){
        AlertDialog.Builder customDialog
                = new AlertDialog.Builder(Game.this,R.style.CustomDialog);
        customDialog.setTitle("");

        LayoutInflater layoutInflater
                = (LayoutInflater)getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view=layoutInflater.inflate(R.layout.game_popup,null);


        ImageView  back, forward;
        TextView score;
        ImageView stars;

        back = (ImageView) view.findViewById(R.id.level_back);
        forward= (ImageView) view.findViewById(R.id.level_next);

        score = (TextView) view.findViewById(R.id.level_score);
        stars= (ImageView) view.findViewById(R.id.level_stars);

        score.setText(text);
        setImage(starsId, stars);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(Game.this, SelectLevel.class);
                Game.this.startActivity(myIntent);
            }
        });

        forward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(Game.this, SelectLevel.class);
                Game.this.startActivity(myIntent);
            }
        });

        customDialog.setView(view);
        customDialog.show();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game);

        mHandler = new Handler();

        scrollingBackground();

        mPlayFullTrack = (ImageView) findViewById(R.id.play_full_track_button);
        mContainerView = (FrameLayout) findViewById(R.id.container_view);
        mSolutionView = (LinearLayout) findViewById(R.id.solution_view);
        mTimer = (TextView) findViewById(R.id.level_timer);


                mp = MediaPlayer.create(this, R.raw.tut1);
        mPlayFullTrack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mShoulUseAsPlay) {
                    playSequence();
                    checkIfCorrectSequance();
                } else {
                    if(mMainPlayerFinished || mp.isPlaying()) {
                        return;
                    }
                    mp.seekTo(0);
                    mp.start();
                }
            }
        });

        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mPlayFullTrack.setImageResource(R.drawable.bigplay);
                mMainPlayerFinished = true;
                mShoulUseAsPlay = true;

                 timer = new CountDownTimer(30000, 1000) {

                    public void onTick(long millisUntilFinished) {
                        mTimer.setText(""+millisUntilFinished / 1000);
                        //here you can have your logic to set text to edittext
                    }

                    public void onFinish() {
                        finishLevel(1);
                    }

                }.start();
            }
        });

        mPlayFullTrack.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                    @Override
                    public void onGlobalLayout() {
                        mPlayFullTrack.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        addPuzzlePieces(3);
                        addPuzzlePiecesPlaceholders(3);
                    }
                });

    }

    private void playSequence() {
        int pieceSizeMs = mp.getDuration() / mPieceArray.size();
        int played = 0;
        for (Placeholder placeholder : mPlaceholderArray) {
            for (Step step : mPieceArray) {
                final Step currentStep = step;
                if (placeholder.getStepView() == currentStep.getStepView()) {
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            play(currentStep.getSongStartMs(), currentStep.getSongEndMs());
                        }
                    }, played * pieceSizeMs);
                    played++;
                    break;
                }
            }
        }
    }

    private void checkIfCorrectSequance() {
        boolean isCorrect = true;

        for (int i = 0; i < mPlaceholderArray.size(); i++) {
            if (mPlaceholderArray.get(i).getStepView() != mPieceArray.get(i).getStepView()) {
                isCorrect = false;
                break;
            }
        }

        if(isCorrect) {
            for (int i = 0; i < mPlaceholderArray.size(); i++) {
                rotateImage(mPlaceholderArray.get(i).getStepView());
            }
            rotateImage(mPlayFullTrack);
            finishLevel(0);
        } else {
            Toast.makeText(Game.this, "Not Correct",
                    Toast.LENGTH_LONG).show();
        }
    }

    private void addPuzzlePiecesPlaceholders(int number) {
        if (number < 3 || number > 6) {
            return;
        }

        int size = getResources().getDimensionPixelSize(R.dimen.placeholder_size);

        for(int i = 0; i < number; i ++) {
            ImageView placeholder = new ImageView(this);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(size, size);
            lp.gravity = Gravity.CENTER;
            lp.weight = 1;
            placeholder.setLayoutParams(lp);
            placeholder.setImageResource(R.drawable.musiempty);//johnny
            mSolutionView.addView(placeholder);
            mPlaceholderArray.add(new Placeholder(placeholder));
        }

    }

    private void addPuzzlePieces(int number) {
        if (number < 3 || number > 6) {
            return;
        }

        final int step = 360/number;
        int r = getResources().getDimensionPixelSize(R.dimen.radius);
        float x = mPlayFullTrack.getX() + mPlayFullTrack.getWidth()  / 2;
        float y = mPlayFullTrack.getY() + mPlayFullTrack.getHeight()  / 2;
        final int size = getResources().getDimensionPixelSize(R.dimen.piece_size);
        int counter = 0;

        for(int i = 0; i < 360; i += step) {
            final ImageView pieceInitialPlacePlaceholder = new ImageView(this);
            pieceInitialPlacePlaceholder.setImageResource(R.drawable.inactivepiece);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(size, size);
            pieceInitialPlacePlaceholder.setLayoutParams(lp);
            pieceInitialPlacePlaceholder.setX(x + r * (float)Math.cos(Math.toRadians(i)) - size/2);
            pieceInitialPlacePlaceholder.setY(y + r * (float)Math.sin(Math.toRadians(i)) - size/2 +
                    getResources().getDimensionPixelSize(R.dimen.solver_height));
            mContainerView.addView(pieceInitialPlacePlaceholder);
        }

        int pieceSizeMs = mp.getDuration() / number;

        int positions[] = new int[number + 1];
        for (int i = 0; i < 360; i+=step) {
            positions[++positions[0]] = i;
        }

        for(int i = 0; i < 360; i += step) {
            final ImageView stepView = new ImageView(this);

            int resourceId = this.getResources().getIdentifier(mDefaultColors[counter],
                    "drawable", "ioan.ghisoi.disertation");
            counter++;

            stepView.setImageResource(resourceId);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(size, size);
            stepView.setLayoutParams(lp);

            // get a valid random i
            int value = -1;
            while (value == -1) {
                int position = (int) (Math.random() * number + 1);
                value = positions[position];
                positions[position] = -1;
            }

            stepView.setX(x + r * (float)Math.cos(Math.toRadians(value)) - size/2);
            stepView.setY(y + r * (float)Math.sin(Math.toRadians(value)) - size/2 +
                    getResources().getDimensionPixelSize(R.dimen.solver_height));
            mContainerView.addView(stepView);

            stepView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (mp.isPlaying()) {
                        mp.pause();
                    }
                    return false;
                }
            });

            stepView.setOnTouchListener(new View.OnTouchListener() {
                PointF DownPT = new PointF(); // Record Mouse Position When Pressed Down
                PointF StartPT = new PointF(); // Record Start Position of 'img'
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_MOVE :
                            PointF mv = new PointF( event.getX() - DownPT.x, event.getY() - DownPT.y);
                            int x = (int)(StartPT.x+mv.x);
                            int y = (int)(StartPT.y+mv.y);
                            stepView.setX(x);
                            stepView.setY(y);

                            if (y > mSolutionView.getHeight()) {
                                // search in placeholders
                                for (Placeholder placeholder : mPlaceholderArray) {
                                    if (placeholder.getStepView() == stepView) {
                                        placeholder.setStepView(null);
                                        break;
                                    }
                                }
                            }

                            StartPT = new PointF( stepView.getX(), stepView.getY() );
                            break;
                        case MotionEvent.ACTION_DOWN :
                            DownPT.x = event.getX();
                            DownPT.y = event.getY();
                            StartPT = new PointF( stepView.getX(), stepView.getY());

                            for (Step step : mPieceArray) {
                                if (step.getStepView() == stepView) {
                                    play(step.getSongStartMs(), step.getSongEndMs());
                                    break;
                                }
                            }

                            break;
                        case MotionEvent.ACTION_UP :
                            // if crossing the placeholder line
                            if (stepView.getY() < mSolutionView.getHeight()) {
                                // find closest placeholder and center inside

                                double minDistance = 9999;
                                Placeholder closestPlaceholder = null;

                                for (Placeholder placeholder : mPlaceholderArray) {
                                    ImageView placeholderView = placeholder.getPlaceholderView();
                                    float x1 = placeholderView.getX();
                                    float x2 = stepView.getX();
                                    float y1 = placeholderView.getY();
                                    float y2 = stepView.getY();
                                    double distance = Math.sqrt(
                                            (x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));
                                    if (distance < minDistance) {
                                        closestPlaceholder = placeholder;
                                        minDistance = distance;
                                    }
                                }

                                if (closestPlaceholder != null && closestPlaceholder.getStepView() == null) {
                                    ImageView placeholderView = closestPlaceholder.getPlaceholderView();
                                    float placeholderCenterX = placeholderView.getX() + placeholderView.getWidth() / 2;
                                    float placeholderCenterY = placeholderView.getY() + placeholderView.getHeight() / 2;
                                    stepView.setX(placeholderCenterX - stepView.getWidth() / 2);
                                    stepView.setY(placeholderCenterY - stepView.getHeight() / 2);
                                    closestPlaceholder.setStepView(stepView);
                                    return true;
                                }
                            }

                            // reset
                            for (Step step : mPieceArray) {
                                if (step.getStepView() == stepView) {
                                    step.reset();
                                    break;
                                }
                            }

                            break;
                        default :
                            break;
                    }
                    return false;
                }
            });

            mPieceArray.add(new Step(stepView, (counter - 1) * pieceSizeMs, counter * pieceSizeMs));
        }

    }

    private void play(int fromMs, int toMs) {
        mp.seekTo(fromMs);
        mp.start();
        if (mRunnable != null) {
            mHandler.removeCallbacks(mRunnable);
        }
        mRunnable = new Runnable() {
            @Override
            public void run() {
                mp.pause();
            }
        };
        mHandler.postDelayed(mRunnable, toMs - fromMs);
    }

    private class Placeholder {
        private ImageView placeholderView;
        private ImageView stepView;

        private Placeholder(ImageView placeholderView) {
            this.placeholderView = placeholderView;
        }

        public ImageView getPlaceholderView() {
            return placeholderView;
        }

        public ImageView getStepView() {
            return stepView;
        }

        public void setStepView(ImageView stepView) {
            this.stepView = stepView;
        }
    }

    private class Step {
        private ImageView stepView;
        private float initialX;
        private float initialY;

        int songStartMs;
        int songEndMs;

        private Step(ImageView stepView, int songStartMs, int songEndMs) {
            this.stepView = stepView;
            this.initialX = stepView.getX();
            this.initialY = stepView.getY();
            this.songStartMs = songStartMs;
            this.songEndMs = songEndMs;
        }

        public void reset() {
            stepView.setX(initialX);
            stepView.setY(initialY);
        }

        public ImageView getStepView() {
            return stepView;
        }

        public int getSongStartMs() {
            return songStartMs;
        }

        public int getSongEndMs() {
            return songEndMs;
        }

    }
    public void rotateImage(ImageView image) {
        RotateAnimation rotate = new RotateAnimation(0, 360, Animation.RELATIVE_TO_PARENT, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotate.setDuration(1000);
        rotate.setInterpolator(new LinearInterpolator());
        image.startAnimation(rotate);
    }

    public void setImage(int resource, ImageView target){

        if(resource == 69) {
            target.setVisibility(View.INVISIBLE);
        } else {
            target.setVisibility(View.VISIBLE);
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                Drawable icon1 = getResources().getDrawable(resource, getTheme());
                target.setImageDrawable(icon1);
            }
        }
    }
    public void scrollingBackground() {
        final ImageView backgroundOne = (ImageView) findViewById(R.id.background_one);
        final ImageView backgroundTwo = (ImageView) findViewById(R.id.background_two);

        final ValueAnimator animator = ValueAnimator.ofFloat(0.0f, 1.0f);
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.setInterpolator(new LinearInterpolator());
        animator.setDuration(10000L);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                final float progress = (float) animation.getAnimatedValue();
                final float width = backgroundOne.getWidth();
                final float translationX = width * progress;
                backgroundOne.setTranslationX(translationX);
                backgroundTwo.setTranslationX(translationX - width);
            }
        });
        animator.start();
    }

    public void finishLevel(int type){

        if(type == 0) {
            try {
                mTimer.setVisibility(View.INVISIBLE);
                timer.cancel();
                timer = null;
                int score = 1000 * Integer.parseInt(mTimer.getText().toString());

                openCustomDialog(String.valueOf(score), R.drawable.star1);
            } catch (Exception e) {
                System.out.println(e);
            }
        } else {
            try {
                mTimer.setVisibility(View.INVISIBLE);
                int score = 0;
                openCustomDialog(String.valueOf(score), R.drawable.star0);
            } catch (Exception e) {
                System.out.println(e);
            }
        }
    }
}
