package ioan.ghisoi.disertation;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;


public class MainActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 0;
    private boolean isLoggedIn = false;

    ImageView mLoginButton, mLogoutButton, mUserPicture;
    FirebaseAuth auth = FirebaseAuth.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mLoginButton = (ImageView) findViewById(R.id.login_button);
        mLogoutButton = (ImageView) findViewById(R.id.refresh_button);
        mUserPicture = (ImageView) findViewById(R.id.user_picture);

        try {
            if(auth.getCurrentUser() != null) {
                mLoginButton.setImageResource(R.drawable.logoutbutton);
                mUserPicture.setVisibility(View.VISIBLE);
                setImage(mUserPicture, String.valueOf(FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl()));
            }
        } catch (Exception e) {

        }


        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(auth.getCurrentUser() != null) {
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
                        Intent intent = getIntent();
                        finish();
                        startActivity(intent);
                        Toast.makeText(MainActivity.this,"Log out succesfully",Toast.LENGTH_SHORT);
                    }
                });

            }
        });

        //johnny
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == RC_SIGN_IN) {
            if(resultCode == RESULT_OK) {
                isLoggedIn = true;
                mLoginButton.setImageResource(R.drawable.logoutbutton);
                mUserPicture.setVisibility(View.VISIBLE);

                setImage(mUserPicture, String.valueOf(FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl()));


                Toast.makeText(MainActivity.this,"Welcome " + auth.getCurrentUser().getDisplayName(),Toast.LENGTH_SHORT);
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
}
