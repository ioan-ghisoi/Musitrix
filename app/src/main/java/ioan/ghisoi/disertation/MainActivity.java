package ioan.ghisoi.disertation;

import android.animation.ValueAnimator;
import android.app.ActivityOptions;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;


public class MainActivity extends AppCompatActivity {

    private static final int SIGN_IN_CODE = 0;
    private FirebaseAuth mAuth;
    private Button mPlayButton, mCommunityButton, mRankButton;
    private ImageView mUserPicture, mLoginFacebook, mLoginGoogle, mLogout, mScroll1, mScroll2;
    private TextView mUserName, mAppTitle;
    FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();

        mPlayButton = (Button) findViewById(R.id.play_button);
        mCommunityButton = (Button) findViewById(R.id.community_button);
        mRankButton = (Button) findViewById(R.id.rank_button);
        mUserPicture = (ImageView) findViewById(R.id.user_picture);
        mLoginFacebook = (ImageView) findViewById(R.id.facebook_login);
        mLoginGoogle = (ImageView) findViewById(R.id.google_login);
        mLogout = (ImageView) findViewById(R.id.logout_button);
        mScroll1 = (ImageView) findViewById(R.id.scrolling_1);
        mScroll2 = (ImageView) findViewById(R.id.scrolling_2);
        mUserName = (TextView) findViewById(R.id.user_name);
        mAppTitle = (TextView) findViewById(R.id.app_title);

        mPlayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mAuth.getCurrentUser() == null) {
                    registerAnonymus();
                } else {
                    Intent myIntent = new Intent(MainActivity.this, SelectWorld.class);
                    MainActivity.this.startActivity(myIntent);
                }
            }
        });

        mCommunityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mAuth.getCurrentUser() == null) {
                    registerAnonymus();
                } else {
                    Intent myIntent = new Intent(MainActivity.this, ComunityChose.class);
                    MainActivity.this.startActivity(myIntent);
                }
            }
        });

        mRankButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mAuth.getCurrentUser() == null) {
                    registerAnonymus();
                } else {
                    Intent myIntent = new Intent(MainActivity.this, Rank.class);
                    MainActivity.this.startActivity(myIntent);
                }
            }
        });


        scrollingBackground();

        setCostumeFont();

        setUserDetails();

        handleLogin();

        handleLogout();


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        try {
            if (requestCode == SIGN_IN_CODE) {
                if (resultCode == RESULT_OK) {
                    mUserPicture.setVisibility(View.VISIBLE);
                    mUserName.setVisibility(View.VISIBLE);
                    Toast.makeText(MainActivity.this, "Welcome " + mAuth.getCurrentUser().getDisplayName(), Toast.LENGTH_SHORT).show();

                    DatabaseReference myRef = mDatabase.getReference(mAuth.getCurrentUser().getUid() + "/coins");

                    myRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            try{
                                String test = dataSnapshot.getValue().toString();
                            } catch (Exception e) {
                                instantiateDatabase();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError error) {
                            // Failed to read value
                            Log.w("ERROR ", "Failed to read value.", error.toException());
                        }
                    });


                    setUserDetails();
                } else {

                }
            }
        }catch (Exception e) {
            System.out.println(e);
        }
    }

    public void setImage(ImageView imageview, String url) {
        try {

            Picasso.with(MainActivity.this)
                    .load(url)
                    .config(Bitmap.Config.RGB_565)
                    .fit()
                    .centerInside()
                    .into(imageview);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setUserDetails() {
        if(mAuth.getCurrentUser() != null) {
            mUserPicture.setVisibility(View.VISIBLE);
            mUserName.setVisibility(View.VISIBLE);
            setImage(mUserPicture, String.valueOf(mAuth.getCurrentUser().getPhotoUrl()));
            mUserName.setText(mAuth.getCurrentUser().getDisplayName());
        } else {
            mUserPicture.setVisibility(View.INVISIBLE);
            mUserName.setVisibility(View.INVISIBLE);
        }
    }

    public void handleLogin() {
        mLoginFacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if(mAuth.getCurrentUser() == null) {
                        startActivityForResult(AuthUI.getInstance()
                                .createSignInIntentBuilder().setProviders(
                                        AuthUI.FACEBOOK_PROVIDER,
                                        AuthUI.GOOGLE_PROVIDER,
                                        AuthUI.EMAIL_PROVIDER
                                )
                                .setTheme(R.style.GreenTheme).build(), SIGN_IN_CODE);
                    } else {
                        mLogout.performClick();
                    }
                } catch (Exception e) {
                    System.out.println(e);
                }
            }
        });

        mLoginGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if(mAuth.getCurrentUser() == null) {
                        startActivityForResult(AuthUI.getInstance()
                                .createSignInIntentBuilder().setProviders(
                                        AuthUI.FACEBOOK_PROVIDER,
                                        AuthUI.GOOGLE_PROVIDER,
                                        AuthUI.EMAIL_PROVIDER
                                )
                                .setTheme(R.style.GreenTheme).build(), SIGN_IN_CODE);
                    } else {
                        mLogout.performClick();
                    }
                } catch (Exception e) {
                    System.out.println(e);
                }
            }
        });
    }

    public void handleLogout() {

        mLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AuthUI.getInstance().signOut(MainActivity.this).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        mUserPicture.setVisibility(View.INVISIBLE);
                        mUserName.setVisibility(View.INVISIBLE);
                        Toast.makeText(MainActivity.this, "Log out succesfully", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    public void setCostumeFont() {
        Typeface mFont = Typeface.createFromAsset(getAssets(), "fonts/johnny.ttf");
        mPlayButton.setTypeface(mFont);
        mCommunityButton.setTypeface(mFont);
        mRankButton.setTypeface(mFont);
        mPlayButton.setTypeface(mFont);
        mAppTitle.setTypeface(mFont);
    }

    public void scrollingBackground() {

        ValueAnimator animator = ValueAnimator.ofFloat(0.0f, 1.0f);
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.setInterpolator(new LinearInterpolator());
        animator.setDuration(10000L);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                final float progress = (float) animation.getAnimatedValue();
                final float width = mScroll1.getWidth();
                final float translationX = width * progress;
                mScroll1.setTranslationX(translationX);
                mScroll2.setTranslationX(translationX - width);
            }
        });
        animator.start();
    }

    public void instantiateDatabase() {
        try {
            DatabaseReference myReference = mDatabase.getReference(mAuth.getCurrentUser().getUid() + "/coins");
            myReference.setValue("1000");

            myReference = mDatabase.getReference(mAuth.getCurrentUser().getUid() + "/lifes");
            myReference.setValue("20");

            myReference = mDatabase.getReference(mAuth.getCurrentUser().getUid() + "/playername");
            myReference.setValue(mAuth.getCurrentUser().getDisplayName());

            myReference = mDatabase.getReference(mAuth.getCurrentUser().getUid() + "/progress");
            myReference.setValue("0");

            myReference = mDatabase.getReference(mAuth.getCurrentUser().getUid() + "/replay");
            myReference.setValue("5");

            myReference = mDatabase.getReference(mAuth.getCurrentUser().getUid() + "/time");
            myReference.setValue("5");

            myReference = mDatabase.getReference(mAuth.getCurrentUser().getUid() + "/time");
            myReference.setValue("5");

            myReference = mDatabase.getReference(mAuth.getCurrentUser().getUid() + "/world1/level1/icon");
            myReference.setValue(R.drawable.lvl1);
            myReference = mDatabase.getReference(mAuth.getCurrentUser().getUid() + "/world1/level1/points");
            myReference.setValue("0");
            myReference = mDatabase.getReference(mAuth.getCurrentUser().getUid() + "/world1/level1/stars");
            myReference.setValue(R.drawable.star0);
            myReference = mDatabase.getReference(mAuth.getCurrentUser().getUid() + "/world1/level1/status");
            myReference.setValue("unlocked");

            myReference = mDatabase.getReference(mAuth.getCurrentUser().getUid() + "/world1/level2/icon");
            myReference.setValue(R.drawable.lvl2block);
            myReference = mDatabase.getReference(mAuth.getCurrentUser().getUid() + "/world1/level2/points");
            myReference.setValue("0");
            myReference = mDatabase.getReference(mAuth.getCurrentUser().getUid() + "/world1/level2/stars");
            myReference.setValue(69);
            myReference = mDatabase.getReference(mAuth.getCurrentUser().getUid() + "/world1/level2/status");
            myReference.setValue("locked");

            myReference = mDatabase.getReference(mAuth.getCurrentUser().getUid() + "/world1/level3/icon");
            myReference.setValue(R.drawable.lvl3block);
            myReference = mDatabase.getReference(mAuth.getCurrentUser().getUid() + "/world1/level3/points");
            myReference.setValue("0");
            myReference = mDatabase.getReference(mAuth.getCurrentUser().getUid() + "/world1/level3/stars");
            myReference.setValue(69);
            myReference = mDatabase.getReference(mAuth.getCurrentUser().getUid() + "/world1/level3/status");
            myReference.setValue("locked");

            myReference = mDatabase.getReference(mAuth.getCurrentUser().getUid() + "/world1/level4/icon");
            myReference.setValue(R.drawable.lvl4block);
            myReference = mDatabase.getReference(mAuth.getCurrentUser().getUid() + "/world1/level4/points");
            myReference.setValue("0");
            myReference = mDatabase.getReference(mAuth.getCurrentUser().getUid() + "/world1/level4/stars");
            myReference.setValue(69);
            myReference = mDatabase.getReference(mAuth.getCurrentUser().getUid() + "/world1/level4/status");
            myReference.setValue("locked");

            myReference = mDatabase.getReference(mAuth.getCurrentUser().getUid() + "/world1/level5/icon");
            myReference.setValue(R.drawable.lvl5block);
            myReference = mDatabase.getReference(mAuth.getCurrentUser().getUid() + "/world1/level5/points");
            myReference.setValue("0");
            myReference = mDatabase.getReference(mAuth.getCurrentUser().getUid() + "/world1/level5/stars");
            myReference.setValue(69);
            myReference = mDatabase.getReference(mAuth.getCurrentUser().getUid() + "/world1/level5/status");
            myReference.setValue("locked");

            myReference = mDatabase.getReference(mAuth.getCurrentUser().getUid() + "/world1/level6/icon");
            myReference.setValue(R.drawable.lvl6block);
            myReference = mDatabase.getReference(mAuth.getCurrentUser().getUid() + "/world1/level6/points");
            myReference.setValue("0");
            myReference = mDatabase.getReference(mAuth.getCurrentUser().getUid() + "/world1/level6/stars");
            myReference.setValue(69);
            myReference = mDatabase.getReference(mAuth.getCurrentUser().getUid() + "/world1/level6/status");
            myReference.setValue("locked");

            myReference = mDatabase.getReference(mAuth.getCurrentUser().getUid() + "/world1/level7/icon");
            myReference.setValue(R.drawable.lvl7block);
            myReference = mDatabase.getReference(mAuth.getCurrentUser().getUid() + "/world1/level7/points");
            myReference.setValue("0");
            myReference = mDatabase.getReference(mAuth.getCurrentUser().getUid() + "/world1/level7/stars");
            myReference.setValue(69);
            myReference = mDatabase.getReference(mAuth.getCurrentUser().getUid() + "/world1/level7/status");
            myReference.setValue("locked");

            myReference = mDatabase.getReference(mAuth.getCurrentUser().getUid() + "/world1/level8/icon");
            myReference.setValue(R.drawable.lvl8block);
            myReference = mDatabase.getReference(mAuth.getCurrentUser().getUid() + "/world1/level8/points");
            myReference.setValue("0");
            myReference = mDatabase.getReference(mAuth.getCurrentUser().getUid() + "/world1/level8/stars");
            myReference.setValue(69);
            myReference = mDatabase.getReference(mAuth.getCurrentUser().getUid() + "/world1/level8/status");
            myReference.setValue("locked");

            myReference = mDatabase.getReference(mAuth.getCurrentUser().getUid() + "/world1/level9/icon");
            myReference.setValue(R.drawable.lvl9block);
            myReference = mDatabase.getReference(mAuth.getCurrentUser().getUid() + "/world1/level9/points");
            myReference.setValue("0");
            myReference = mDatabase.getReference(mAuth.getCurrentUser().getUid() + "/world1/level9/stars");
            myReference.setValue(69);
            myReference = mDatabase.getReference(mAuth.getCurrentUser().getUid() + "/world1/level9/status");
            myReference.setValue("locked");

            ////////

            myReference = mDatabase.getReference(mAuth.getCurrentUser().getUid() + "/world2/level1/icon");
            myReference.setValue(R.drawable.lvl1);
            myReference = mDatabase.getReference(mAuth.getCurrentUser().getUid() + "/world2/level1/points");
            myReference.setValue("0");
            myReference = mDatabase.getReference(mAuth.getCurrentUser().getUid() + "/world2/level1/stars");
            myReference.setValue(R.drawable.star0);
            myReference = mDatabase.getReference(mAuth.getCurrentUser().getUid() + "/world2/level1/status");
            myReference.setValue("unlocked");

            myReference = mDatabase.getReference(mAuth.getCurrentUser().getUid() + "/world2/level2/icon");
            myReference.setValue(R.drawable.lvl2block);
            myReference = mDatabase.getReference(mAuth.getCurrentUser().getUid() + "/world2/level2/points");
            myReference.setValue("0");
            myReference = mDatabase.getReference(mAuth.getCurrentUser().getUid() + "/world2/level2/stars");
            myReference.setValue(69);
            myReference = mDatabase.getReference(mAuth.getCurrentUser().getUid() + "/world2/level2/status");
            myReference.setValue("locked");

            myReference = mDatabase.getReference(mAuth.getCurrentUser().getUid() + "/world2/level3/icon");
            myReference.setValue(R.drawable.lvl3block);
            myReference = mDatabase.getReference(mAuth.getCurrentUser().getUid() + "/world2/level3/points");
            myReference.setValue("0");
            myReference = mDatabase.getReference(mAuth.getCurrentUser().getUid() + "/world2/level3/stars");
            myReference.setValue(69);
            myReference = mDatabase.getReference(mAuth.getCurrentUser().getUid() + "/world2/level3/status");
            myReference.setValue("locked");

            myReference = mDatabase.getReference(mAuth.getCurrentUser().getUid() + "/world2/level4/icon");
            myReference.setValue(R.drawable.lvl4block);
            myReference = mDatabase.getReference(mAuth.getCurrentUser().getUid() + "/world2/level4/points");
            myReference.setValue("0");
            myReference = mDatabase.getReference(mAuth.getCurrentUser().getUid() + "/world2/level4/stars");
            myReference.setValue(69);
            myReference = mDatabase.getReference(mAuth.getCurrentUser().getUid() + "/world2/level4/status");
            myReference.setValue("locked");

            myReference = mDatabase.getReference(mAuth.getCurrentUser().getUid() + "/world2/level5/icon");
            myReference.setValue(R.drawable.lvl5block);
            myReference = mDatabase.getReference(mAuth.getCurrentUser().getUid() + "/world2/level5/points");
            myReference.setValue("0");
            myReference = mDatabase.getReference(mAuth.getCurrentUser().getUid() + "/world2/level5/stars");
            myReference.setValue(69);
            myReference = mDatabase.getReference(mAuth.getCurrentUser().getUid() + "/world2/level5/status");
            myReference.setValue("locked");

            myReference = mDatabase.getReference(mAuth.getCurrentUser().getUid() + "/world2/level6/icon");
            myReference.setValue(R.drawable.lvl6block);
            myReference = mDatabase.getReference(mAuth.getCurrentUser().getUid() + "/world2/level6/points");
            myReference.setValue("0");
            myReference = mDatabase.getReference(mAuth.getCurrentUser().getUid() + "/world2/level6/stars");
            myReference.setValue(69);
            myReference = mDatabase.getReference(mAuth.getCurrentUser().getUid() + "/world2/level6/status");
            myReference.setValue("locked");

            myReference = mDatabase.getReference(mAuth.getCurrentUser().getUid() + "/world2/level7/icon");
            myReference.setValue(R.drawable.lvl7block);
            myReference = mDatabase.getReference(mAuth.getCurrentUser().getUid() + "/world2/level7/points");
            myReference.setValue("0");
            myReference = mDatabase.getReference(mAuth.getCurrentUser().getUid() + "/world2/level7/stars");
            myReference.setValue(69);
            myReference = mDatabase.getReference(mAuth.getCurrentUser().getUid() + "/world2/level7/status");
            myReference.setValue("locked");

            myReference = mDatabase.getReference(mAuth.getCurrentUser().getUid() + "/world2/level8/icon");
            myReference.setValue(R.drawable.lvl8block);
            myReference = mDatabase.getReference(mAuth.getCurrentUser().getUid() + "/world2/level8/points");
            myReference.setValue("0");
            myReference = mDatabase.getReference(mAuth.getCurrentUser().getUid() + "/world2/level8/stars");
            myReference.setValue(69);
            myReference = mDatabase.getReference(mAuth.getCurrentUser().getUid() + "/world2/level8/status");
            myReference.setValue("locked");

            myReference = mDatabase.getReference(mAuth.getCurrentUser().getUid() + "/world2/level9/icon");
            myReference.setValue(R.drawable.lvl9block);
            myReference = mDatabase.getReference(mAuth.getCurrentUser().getUid() + "/world2/level9/points");
            myReference.setValue("0");
            myReference = mDatabase.getReference(mAuth.getCurrentUser().getUid() + "/world2/level9/stars");
            myReference.setValue(69);
            myReference = mDatabase.getReference(mAuth.getCurrentUser().getUid() + "/world2/level9/status");
            myReference.setValue("locked");


            ///////


            myReference = mDatabase.getReference(mAuth.getCurrentUser().getUid() + "/world3/level1/icon");
            myReference.setValue(R.drawable.lvl1);
            myReference = mDatabase.getReference(mAuth.getCurrentUser().getUid() + "/world3/level1/points");
            myReference.setValue("0");
            myReference = mDatabase.getReference(mAuth.getCurrentUser().getUid() + "/world3/level1/stars");
            myReference.setValue(R.drawable.star0);
            myReference = mDatabase.getReference(mAuth.getCurrentUser().getUid() + "/world3/level1/status");
            myReference.setValue("unlocked");

            myReference = mDatabase.getReference(mAuth.getCurrentUser().getUid() + "/world3/level2/icon");
            myReference.setValue(R.drawable.lvl2block);
            myReference = mDatabase.getReference(mAuth.getCurrentUser().getUid() + "/world3/level2/points");
            myReference.setValue("0");
            myReference = mDatabase.getReference(mAuth.getCurrentUser().getUid() + "/world3/level2/stars");
            myReference.setValue(69);
            myReference = mDatabase.getReference(mAuth.getCurrentUser().getUid() + "/world3/level2/status");
            myReference.setValue("locked");

            myReference = mDatabase.getReference(mAuth.getCurrentUser().getUid() + "/world3/level3/icon");
            myReference.setValue(R.drawable.lvl3block);
            myReference = mDatabase.getReference(mAuth.getCurrentUser().getUid() + "/world3/level3/points");
            myReference.setValue("0");
            myReference = mDatabase.getReference(mAuth.getCurrentUser().getUid() + "/world3/level3/stars");
            myReference.setValue(69);
            myReference = mDatabase.getReference(mAuth.getCurrentUser().getUid() + "/world3/level3/status");
            myReference.setValue("locked");

            myReference = mDatabase.getReference(mAuth.getCurrentUser().getUid() + "/world3/level4/icon");
            myReference.setValue(R.drawable.lvl4block);
            myReference = mDatabase.getReference(mAuth.getCurrentUser().getUid() + "/world3/level4/points");
            myReference.setValue("0");
            myReference = mDatabase.getReference(mAuth.getCurrentUser().getUid() + "/world3/level4/stars");
            myReference.setValue(69);
            myReference = mDatabase.getReference(mAuth.getCurrentUser().getUid() + "/world3/level4/status");
            myReference.setValue("locked");

            myReference = mDatabase.getReference(mAuth.getCurrentUser().getUid() + "/world3/level5/icon");
            myReference.setValue(R.drawable.lvl5block);
            myReference = mDatabase.getReference(mAuth.getCurrentUser().getUid() + "/world3/level5/points");
            myReference.setValue("0");
            myReference = mDatabase.getReference(mAuth.getCurrentUser().getUid() + "/world3/level5/stars");
            myReference.setValue(69);
            myReference = mDatabase.getReference(mAuth.getCurrentUser().getUid() + "/world3/level5/status");
            myReference.setValue("locked");

            myReference = mDatabase.getReference(mAuth.getCurrentUser().getUid() + "/world3/level6/icon");
            myReference.setValue(R.drawable.lvl6block);
            myReference = mDatabase.getReference(mAuth.getCurrentUser().getUid() + "/world3/level6/points");
            myReference.setValue("0");
            myReference = mDatabase.getReference(mAuth.getCurrentUser().getUid() + "/world3/level6/stars");
            myReference.setValue(69);
            myReference = mDatabase.getReference(mAuth.getCurrentUser().getUid() + "/world3/level6/status");
            myReference.setValue("locked");

            myReference = mDatabase.getReference(mAuth.getCurrentUser().getUid() + "/world3/level7/icon");
            myReference.setValue(R.drawable.lvl7block);
            myReference = mDatabase.getReference(mAuth.getCurrentUser().getUid() + "/world3/level7/points");
            myReference.setValue("0");
            myReference = mDatabase.getReference(mAuth.getCurrentUser().getUid() + "/world3/level7/stars");
            myReference.setValue(69);
            myReference = mDatabase.getReference(mAuth.getCurrentUser().getUid() + "/world3/level7/status");
            myReference.setValue("locked");

            myReference = mDatabase.getReference(mAuth.getCurrentUser().getUid() + "/world3/level8/icon");
            myReference.setValue(R.drawable.lvl8block);
            myReference = mDatabase.getReference(mAuth.getCurrentUser().getUid() + "/world3/level8/points");
            myReference.setValue("0");
            myReference = mDatabase.getReference(mAuth.getCurrentUser().getUid() + "/world3/level8/stars");
            myReference.setValue(69);
            myReference = mDatabase.getReference(mAuth.getCurrentUser().getUid() + "/world3/level8/status");
            myReference.setValue("locked");

            myReference = mDatabase.getReference(mAuth.getCurrentUser().getUid() + "/world3/level9/icon");
            myReference.setValue(R.drawable.lvl9block);
            myReference = mDatabase.getReference(mAuth.getCurrentUser().getUid() + "/world3/level9/points");
            myReference.setValue("0");
            myReference = mDatabase.getReference(mAuth.getCurrentUser().getUid() + "/world3/level9/stars");
            myReference.setValue(69);
            myReference = mDatabase.getReference(mAuth.getCurrentUser().getUid() + "/world3/level9/status");
            myReference.setValue("locked");


        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public void registerAnonymus() {
        try{
            mAuth.signInAnonymously().addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    instantiateDatabase();
                }
            });
        }catch (Exception e){
            System.out.println(e);
        }
    }
}