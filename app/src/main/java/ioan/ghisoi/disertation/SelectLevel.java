package ioan.ghisoi.disertation;

import android.animation.ValueAnimator;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.widget.ProfilePictureView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

/**
 * Created by jarvis on 11/04/2017.
 */

public class SelectLevel extends AppCompatActivity {

    GridView androidGridView;
    ImageButton backButton;
    TextView mUsername, mProgresWorld, mLifes, mCoins;
    ImageView mProfilePicture;
    FirebaseAuth auth = FirebaseAuth.getInstance();

    int[] gridViewString = {
            R.drawable.star3, R.drawable.star2, R.drawable.star3, R.drawable.star3, R.drawable.star2, R.drawable.star0,
            R.drawable.star0, R.drawable.star0, R.drawable.star0,

    } ;
    int[] gridViewImageId = {
            R.drawable.lvl1, R.drawable.lvl2block, R.drawable.lvl3block, R.drawable.lvl4, R.drawable.lvl5,
            R.drawable.lvl6, R.drawable.lvl7, R.drawable.lvl8, R.drawable.lvl9,
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_level);

        mProfilePicture = (ImageView) findViewById(R.id.user_picture);
        mUsername = (TextView) findViewById(R.id.user_name);
        mProgresWorld = (TextView) findViewById(R.id.worldProgress);
        mLifes = (TextView) findViewById(R.id.user_lifes);
        mCoins = (TextView) findViewById(R.id.user_coins);

        try {
            if (auth.getCurrentUser() != null) {
                mProfilePicture.setVisibility(View.VISIBLE);
                mUsername.setVisibility(View.VISIBLE);
                mUsername.setText(auth.getCurrentUser().getDisplayName());
                setImage(mProfilePicture, String.valueOf(FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl()));
            }
        } catch (Exception e) {

        }



        CustomGridViewActivity adapterViewAndroid = new CustomGridViewActivity(SelectLevel.this, gridViewString, gridViewImageId);
        androidGridView = (GridView) findViewById(R.id.grid_view_image_text);
        androidGridView.setVerticalScrollBarEnabled(false);
        androidGridView.setAdapter(adapterViewAndroid);

        scrollingBackground();



        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(auth.getCurrentUser().getUid() + "/progress");

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String value = dataSnapshot.getValue(String.class);
                try{
                    mProgresWorld.setText(value);
                    mProfilePicture.setVisibility(View.VISIBLE);
                    mUsername.setVisibility(View.VISIBLE);
                    mUsername.setText(auth.getCurrentUser().getDisplayName());
                    setImage(mProfilePicture, String.valueOf(FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl()));

                } catch (Exception e) {
                    System.out.println();
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value

            }
        });

        DatabaseReference lifeWatcher = database.getReference(auth.getCurrentUser().getUid() + "/lifes");

        lifeWatcher.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String value = dataSnapshot.getValue(String.class);
                try{
                    mLifes.setText(value);
                    try {
                        Typeface mFont = Typeface.createFromAsset(getAssets(), "fonts/johnny.ttf");
                        mProgresWorld.setTypeface(mFont);
                    } catch (Exception e) {

                    }
                } catch (Exception e) {
                    System.out.println();
                }
            }
            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
            }
        });

        DatabaseReference coinWatcher = database.getReference(auth.getCurrentUser().getUid() + "/coins");

        coinWatcher.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String value = dataSnapshot.getValue(String.class);
                try{
                    mCoins.setText(value);
                } catch (Exception e) {
                    System.out.println();
                }
            }
            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
            }
        });



        androidGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int i, long id) {
                Intent myIntent = new Intent(SelectLevel.this, Game.class);
                SelectLevel.this.startActivity(myIntent);
                Toast.makeText(SelectLevel.this, "GridView Item: " + gridViewString[i] + " level " + i , Toast.LENGTH_LONG).show();
            }
        });

        backButton = (ImageButton) findViewById(R.id.backButton);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(SelectLevel.this, SelectWorld.class);
                SelectLevel.this.startActivity(myIntent);
            }
        });

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
    public void setImage(ImageView im, String url) {
        try {

            Picasso.with(SelectLevel.this)
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
