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
import android.util.Log;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.Map;


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
                stopBackgroundMusic();
                Intent myIntent = new Intent(MainActivity.this, SelectWorld.class);
                MainActivity.this.startActivity(myIntent);
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



                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference myRef = database.getReference(auth.getCurrentUser().getUid());

                myRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // This method is called once with the initial value and again
                        // whenever data at this location is updated.
                        try{
                            String value = dataSnapshot.getValue(String.class);
                            if(value == null) {
                                initializeUser();
                            }
                        } catch (Exception e) {
                            //initializeUser();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        // Failed to read value
                        Toast.makeText(MainActivity.this,error+ "",Toast.LENGTH_SHORT).show();
                    }
                });

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

    public void stopBackgroundMusic() {
        try {
            mediaPlayer.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void initializeUser() {

        try {
            FirebaseDatabase database = FirebaseDatabase.getInstance();


            DatabaseReference myRef41 = database.getReference(auth.getCurrentUser().getUid() + "/totalworld1");
            myRef41.setValue("0%");

            DatabaseReference myRef42 = database.getReference(auth.getCurrentUser().getUid() + "/totalworld2");
            myRef42.setValue("0%");


            DatabaseReference myRef2 = database.getReference(auth.getCurrentUser().getUid() + "/world1");
            myRef2.setValue("0");
            DatabaseReference myRef3 = database.getReference(auth.getCurrentUser().getUid() + "/world1/level1");
            myRef3.setValue("0");
            DatabaseReference myRef4 = database.getReference(auth.getCurrentUser().getUid() + "/world1/level2");
            myRef4.setValue("0");
            DatabaseReference myRef5 = database.getReference(auth.getCurrentUser().getUid() + "/world1/level3");
            myRef5.setValue("0");
            DatabaseReference myRef21 = database.getReference(auth.getCurrentUser().getUid() + "/world1/level4");
            myRef21.setValue("0");
            DatabaseReference myRef22 = database.getReference(auth.getCurrentUser().getUid() + "/world1/level5");
            myRef22.setValue("0");
            DatabaseReference myRef23 = database.getReference(auth.getCurrentUser().getUid() + "/world1/level6");
            myRef23.setValue("0");
            DatabaseReference myRef24 = database.getReference(auth.getCurrentUser().getUid() + "/world1/level7");
            myRef24.setValue("0");
            DatabaseReference myRef25 = database.getReference(auth.getCurrentUser().getUid() + "/world1/level8");
            myRef25.setValue("0");
            DatabaseReference myRef26 = database.getReference(auth.getCurrentUser().getUid() + "/world1/level9");
            myRef26.setValue("0");
            DatabaseReference myRef6 = database.getReference(auth.getCurrentUser().getUid() + "/world2");
            myRef6.setValue("0");
            DatabaseReference myRef7 = database.getReference(auth.getCurrentUser().getUid() + "/world2/level1");
            myRef7.setValue("0");
            DatabaseReference myRef8 = database.getReference(auth.getCurrentUser().getUid() + "/world2/level2");
            myRef8.setValue("0");
            DatabaseReference myRef9 = database.getReference(auth.getCurrentUser().getUid() + "/world2/level3");
            myRef9.setValue("0");
            DatabaseReference myRef13 = database.getReference(auth.getCurrentUser().getUid() + "/world2/level3");
            myRef13.setValue("0");
            DatabaseReference myRef31 = database.getReference(auth.getCurrentUser().getUid() + "/world2/level4");
            myRef31.setValue("0");
            DatabaseReference myRef33 = database.getReference(auth.getCurrentUser().getUid() + "/world2/level5");
            myRef33.setValue("0");
            DatabaseReference myRef34 = database.getReference(auth.getCurrentUser().getUid() + "/world2/level6");
            myRef34.setValue("0");
            DatabaseReference myRef35 = database.getReference(auth.getCurrentUser().getUid() + "/world2/level7");
            myRef35.setValue("0");
            DatabaseReference myRef36 = database.getReference(auth.getCurrentUser().getUid() + "/world2/level8");
            myRef36.setValue("0");
            DatabaseReference myRef37 = database.getReference(auth.getCurrentUser().getUid() + "/world2/level9");
            myRef37.setValue("0");
            DatabaseReference myRef10 = database.getReference(auth.getCurrentUser().getUid() + "/lifes");
            myRef10.setValue("5");
            DatabaseReference myRef11 = database.getReference(auth.getCurrentUser().getUid() + "/coins");
            myRef11.setValue("100");
            DatabaseReference myRef12 = database.getReference(auth.getCurrentUser().getUid() + "/progress");
            myRef12.setValue("0%");

            DatabaseReference myRef50 = database.getReference(auth.getCurrentUser().getUid() + "/world1/level1/icon");
            myRef50.setValue("2130837707");
            myRef50 = database.getReference(auth.getCurrentUser().getUid() + "/world1/level1/stars");
            myRef50.setValue("2130837768");
            myRef50 = database.getReference(auth.getCurrentUser().getUid() + "/world1/level1/points");
            myRef50.setValue("0");

            DatabaseReference myRef51 = database.getReference(auth.getCurrentUser().getUid() + "/world1/level2/icon");
            myRef51.setValue("2130837709");
            myRef51 = database.getReference(auth.getCurrentUser().getUid() + "/world1/level2/stars");
            myRef51.setValue("69");
            myRef51 = database.getReference(auth.getCurrentUser().getUid() + "/world1/level2/points");
            myRef51.setValue("0");

            DatabaseReference myRef52 = database.getReference(auth.getCurrentUser().getUid() + "/world1/level3/icon");
            myRef52.setValue("2130837711");
            myRef52 = database.getReference(auth.getCurrentUser().getUid() + "/world1/level3/stars");
            myRef52.setValue("69");
            myRef52 = database.getReference(auth.getCurrentUser().getUid() + "/world1/level3/points");
            myRef52.setValue("0");

            DatabaseReference myRef53 = database.getReference(auth.getCurrentUser().getUid() + "/world1/level4/icon");
            myRef53.setValue("2130837713");
            myRef53 = database.getReference(auth.getCurrentUser().getUid() + "/world1/level4/stars");
            myRef53.setValue("69");
            myRef53 = database.getReference(auth.getCurrentUser().getUid() + "/world1/level4/points");
            myRef53.setValue("0");

            DatabaseReference myRef54 = database.getReference(auth.getCurrentUser().getUid() + "/world1/level5/icon");
            myRef54.setValue("2130837715");
            myRef54 = database.getReference(auth.getCurrentUser().getUid() + "/world1/level5/stars");
            myRef54.setValue("69");
            myRef54 = database.getReference(auth.getCurrentUser().getUid() + "/world1/level5/points");
            myRef54.setValue("0");

            DatabaseReference myRef55 = database.getReference(auth.getCurrentUser().getUid() + "/world1/level6/icon");
            myRef55.setValue("2130837717");
            myRef55 = database.getReference(auth.getCurrentUser().getUid() + "/world1/level6/stars");
            myRef55.setValue("69");
            myRef55 = database.getReference(auth.getCurrentUser().getUid() + "/world1/level6/points");
            myRef55.setValue("0");

            DatabaseReference myRef56 = database.getReference(auth.getCurrentUser().getUid() + "/world1/level7/icon");
            myRef56.setValue("2130837719");
            myRef56 = database.getReference(auth.getCurrentUser().getUid() + "/world1/level7/stars");
            myRef56.setValue("69");
            myRef56 = database.getReference(auth.getCurrentUser().getUid() + "/world1/level7/points");
            myRef56.setValue("0");


            DatabaseReference myRef57 = database.getReference(auth.getCurrentUser().getUid() + "/world1/level8/icon");
            myRef57.setValue("2130837721");
            myRef57 = database.getReference(auth.getCurrentUser().getUid() + "/world1/level8/stars");
            myRef57.setValue("69");
            myRef57 = database.getReference(auth.getCurrentUser().getUid() + "/world1/level8/points");
            myRef57.setValue("0");

            DatabaseReference myRef58 = database.getReference(auth.getCurrentUser().getUid() + "/world1/level9/icon");
            myRef58.setValue("2130837723");
            myRef58 = database.getReference(auth.getCurrentUser().getUid() + "/world1/level9/stars");
            myRef58.setValue("69");
            myRef58 = database.getReference(auth.getCurrentUser().getUid() + "/world1/level9/points");
            myRef58.setValue("0");


        } catch (Exception e) {

        }

    }
}
