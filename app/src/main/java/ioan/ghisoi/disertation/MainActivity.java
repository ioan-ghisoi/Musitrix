package ioan.ghisoi.disertation;

import android.animation.ValueAnimator;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

import java.io.IOException;


public class MainActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 0;

    ImageView mLoginButton, mLogoutButton, mUserPicture, mLogo;
    Button mNewGame, mContinue, mRank;
    TextView mUsername, tittle;
    MediaPlayer mediaPlayer;
    FirebaseAuth auth = FirebaseAuth.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mNewGame = (Button) findViewById(R.id.main_screen_new_game_button);
        mContinue = (Button) findViewById(R.id.main_screen_continue_button);
        mRank = (Button) findViewById(R.id.main_screen_rank_button);
        tittle = (TextView) findViewById(R.id.main_screen_title);
        mLoginButton = (ImageView) findViewById(R.id.login_button);
        mLogoutButton = (ImageView) findViewById(R.id.refresh_button);
        mUserPicture = (ImageView) findViewById(R.id.user_picture);
        mLogo = (ImageView) findViewById(R.id.main_screen_logo);
        mUsername = (TextView) findViewById(R.id.user_name);

        Typeface mFont = Typeface.createFromAsset(getAssets(), "fonts/johnny.ttf");
        tittle.setTypeface(mFont);
        mNewGame.setTypeface(mFont);
        mContinue.setTypeface(mFont);
        mRank.setTypeface(mFont);

        rotateImage(mLogo);

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                //magic
                Uri audioFileUri = Uri.parse(
                        "android.resource://" + MainActivity.this.getPackageName() + "/" + R.raw.backgroundmusic);
                mediaPlayer = getMediaPlayerInstance();
                try {
                    mediaPlayer.reset();
                    mediaPlayer.setDataSource(MainActivity.this, audioFileUri);
                    mediaPlayer.prepare();
                    mediaPlayer.getDuration();
                    mediaPlayer.start();
                    mediaPlayer.setLooping(true);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        try {
            if (auth.getCurrentUser() != null) {
                mLoginButton.setImageResource(R.drawable.logoutbutton);
                mUserPicture.setVisibility(View.VISIBLE);
                mUsername.setVisibility(View.VISIBLE);
                mUsername.setText(auth.getCurrentUser().getDisplayName());
                setImage(mUserPicture, String.valueOf(FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl()));
            }
        } catch (Exception e) {

        }

        mNewGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        mContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        mRank.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });


        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (auth.getCurrentUser() != null) {
                    mLogoutButton.performClick();
                    mLoginButton.setImageResource(R.drawable.logoutbutton);
                } else {
                    startActivityForResult(AuthUI.getInstance()
                            .createSignInIntentBuilder().setProviders(
                                    AuthUI.FACEBOOK_PROVIDER,
                                    AuthUI.GOOGLE_PROVIDER,
                                    AuthUI.EMAIL_PROVIDER
                            )
                            .setTheme(R.style.GreenTheme).build(), RC_SIGN_IN);
                    mLoginButton.setImageResource(R.drawable.loginbutton);
                }
            }
        });


        mLogoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AuthUI.getInstance().signOut(MainActivity.this).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        mUserPicture.setVisibility(View.INVISIBLE);
                        mUsername.setVisibility(View.INVISIBLE);
                        try {
                            mediaPlayer.stop();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        Intent intent = getIntent();
                        finish();
                        startActivity(intent);
                        Toast.makeText(MainActivity.this, "Log out succesfully", Toast.LENGTH_SHORT).show();
                        mediaPlayer.start();
                    }
                });

            }
        });
        scrollingBackground();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK) {
                mLoginButton.setImageResource(R.drawable.logoutbutton);
                mUsername.setVisibility(View.VISIBLE);
                mUsername.setText(auth.getCurrentUser().getDisplayName());
                mUserPicture.setVisibility(View.VISIBLE);
                setImage(mUserPicture, String.valueOf(FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl()));
                rotateImage(mUserPicture);

                Toast.makeText(MainActivity.this, "Welcome " + auth.getCurrentUser().getDisplayName(), Toast.LENGTH_SHORT).show();
            } else {

            }
        }
    }

    public void setImage(ImageView im, String url) {
        try {

            Picasso.with(MainActivity.this)
                    .load(url)
                    .config(Bitmap.Config.RGB_565)
                    .fit()
                    .centerInside()
                    .into(im);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void rotateImage(ImageView image) {
        RotateAnimation rotate = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotate.setDuration(1000);
        rotate.setInterpolator(new LinearInterpolator());
        image.startAnimation(rotate);
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

    public MediaPlayer getMediaPlayerInstance() {
        MediaPlayer mMediaPlayer;
        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        return mMediaPlayer;
    }
}
