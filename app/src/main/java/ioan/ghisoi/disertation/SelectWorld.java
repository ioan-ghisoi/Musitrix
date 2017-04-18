package ioan.ghisoi.disertation;

import android.animation.ValueAnimator;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.DefaultSliderView;
import com.daimajia.slider.library.Tricks.ViewPagerEx;
import com.facebook.login.widget.ProfilePictureView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.sql.SQLOutput;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

public class SelectWorld extends AppCompatActivity implements BaseSliderView.OnSliderClickListener, ViewPagerEx.OnPageChangeListener{

    private SliderLayout mWorldSlider;
    ImageView mProfilePicture;
    TextView mUsername, mProgresWorld, mLifes, mCoins;
    ImageButton backButton;
    FirebaseAuth auth = FirebaseAuth.getInstance();
    HashMap<String,Integer> file_maps;
    ProgressDialog pd;
    ImageView lifeBoost, timeBoost, replayBoost, closeDialog;
    ImageButton mShop;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    int currentCoins, currentTime, currentReply, currentLife;
    String globalId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_world);

        pd = new ProgressDialog(SelectWorld.this, R.style.TransparentProgressDialog);
        pd.setMessage("Loading...");
        pd.show();

        globalId = auth.getCurrentUser().getUid();

        mProfilePicture = (ImageView) findViewById(R.id.user_picture);
        mUsername = (TextView) findViewById(R.id.user_name);
        mProgresWorld = (TextView) findViewById(R.id.worldProgress);
        mLifes = (TextView) findViewById(R.id.user_lifes);
        mCoins = (TextView) findViewById(R.id.user_coins);
        mShop = (ImageButton) findViewById(R.id.shoppingButton);


        //String uid, int lifes, int time, int replay

        DatabaseReference myRef = database.getReference(auth.getCurrentUser().getUid() + "/lifes");

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });




        mShop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openShop(auth.getCurrentUser().getUid(), currentLife, currentTime, currentReply, currentCoins);
            }
        });


//        lifeBoost = (ImageView) findViewById(R.id.addLife);
//        timeBoost = (ImageView) findViewById(R.id.addTime);
//        replayBoost = (ImageView) findViewById(R.id.addListen);

        scrollingBackground();

        try {
            if (auth.getCurrentUser() != null) {
                mProfilePicture.setVisibility(View.VISIBLE);
                mUsername.setVisibility(View.VISIBLE);
                mUsername.setText(auth.getCurrentUser().getDisplayName());
                setImage(mProfilePicture, String.valueOf(FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl()));
            }
        } catch (Exception e) {
//                Toast.makeText(SelectWorld.this,"" + e,Toast.LENGTH_LONG).show();
        }




        DatabaseReference coins = database.getReference(auth.getCurrentUser().getUid() + "/time");
        coins.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try{
                    String value = dataSnapshot.getValue().toString();
                    currentTime = Integer.parseInt(value);
                    System.out.println("PPPPPPPPPPPPPPPPPPPPPPcurrentCoins " + currentTime);
                }catch (Exception e) {
                    System.out.println(e);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        DatabaseReference replay = database.getReference(auth.getCurrentUser().getUid() + "/replay");
        replay.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try{
                    String value = dataSnapshot.getValue().toString();
                    currentReply = Integer.parseInt(value);
                    System.out.println("PPPPPPPPPPPPPPPPPPPPPPcurrentReply " + currentReply);
                }catch (Exception e) {
                    System.out.println(e);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        DatabaseReference coinsWatch = database.getReference(auth.getCurrentUser().getUid() + "/coins");
        coinsWatch.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try{
                    String value = dataSnapshot.getValue().toString();
                    currentCoins = Integer.parseInt(value);
                    System.out.println("PPPPPPPPPPPPPPPPPPPPPPcurrentCoins " + currentCoins);
                }catch (Exception e) {
                    System.out.println(e);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        DatabaseReference lifeWatch = database.getReference(auth.getCurrentUser().getUid() + "/lifes");
        lifeWatch.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try{
                    String value = dataSnapshot.getValue().toString();
                    currentLife = Integer.parseInt(value);
                    System.out.println("PPPPPPPPPPPPPPPPPPPPPPlifes " + currentLife);
                }catch (Exception e) {
                    System.out.println(e);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });












        DatabaseReference lifeWatcher = database.getReference(auth.getCurrentUser().getUid() + "/lifes");

        lifeWatcher.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String value = dataSnapshot.getValue().toString();
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
                String value = dataSnapshot.getValue().toString();
                try{
                    mCoins.setText(value);
                    pd.dismiss();
                } catch (Exception e) {
                    System.out.println();
                }
            }
            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
            }
        });

        mWorldSlider = (SliderLayout)findViewById(R.id.slider);
        backButton = (ImageButton)findViewById(R.id.backButton);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(SelectWorld.this, MainActivity.class);
                SelectWorld.this.startActivity(myIntent);
            }
        });


        file_maps = new HashMap<String, Integer>();
        file_maps.put("world1",R.drawable.rockslider);
        file_maps.put("world2",R.drawable.rapslider);
        file_maps.put("world3",R.drawable.popslider);


        createSlider(file_maps);

    }

    public void createSlider(HashMap<String,Integer> file_maps) {

        for(String name : file_maps.keySet()){
            DefaultSliderView textSliderView = new DefaultSliderView(SelectWorld.this);
            // initialize a SliderLayout
            textSliderView
                    .description(name)
                    .image(file_maps.get(name))
                    .setScaleType(BaseSliderView.ScaleType.CenterInside)
                    .setOnSliderClickListener(SelectWorld.this);

            //add your extra information
            textSliderView.bundle(new Bundle());
            textSliderView.getBundle()
                    .putString("extra",name);
            textSliderView.getBundle()
                    .putString("jh","johnny");

            mWorldSlider.addSlider(textSliderView);
        }
        mWorldSlider.setPresetTransformer(SliderLayout.Transformer.ZoomOutSlide);
        mWorldSlider.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
        mWorldSlider.setCustomAnimation(new DescriptionAnimation());
        mWorldSlider.setDuration(0);
        mWorldSlider.stopAutoCycle();
        mWorldSlider.addOnPageChangeListener(SelectWorld.this);
    }

    @Override
    protected void onStop() {
        // To prevent a memory leak on rotation, make sure to call stopAutoCycle() on the slider before activity or fragment is destroyed
        mWorldSlider.stopAutoCycle();
        super.onStop();
    }

    @Override
    public void onSliderClick(BaseSliderView slider) {

        Intent myIntent = new Intent(SelectWorld.this, SelectLevel.class);
        myIntent.putExtra("world",""+slider.getBundle().get("extra"));
        SelectWorld.this.startActivity(myIntent);
    }



    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

    @Override
    public void onPageSelected(int position) {
        Log.d("Slider Demo", "Page Changed: " + position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {}

    public void setImage(ImageView im, String url) {
        try {

            Picasso.with(SelectWorld.this)
                    .load(url)
                    .config(Bitmap.Config.RGB_565)
                    .fit()
                    .centerInside()
                    .into(im);
        } catch (Exception e) {
            e.printStackTrace();
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

    void openShop( String uid, int lifes, int time, int replaym,  int coins){
        AlertDialog.Builder customDialog
                = new AlertDialog.Builder(SelectWorld.this,R.style.CustomDialog);
        customDialog.setTitle("");

        LayoutInflater layoutInflater
                = (LayoutInflater)getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view=layoutInflater.inflate(R.layout.shop,null);

        lifeBoost = (ImageView) view.findViewById(R.id.addLife);
        timeBoost = (ImageView) view.findViewById(R.id.addTime);
        replayBoost = (ImageView) view.findViewById(R.id.addListen);
        closeDialog = (ImageView) view.findViewById(R.id.exit);

        lifeBoost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {

                    if(currentCoins > 99) {
                        DatabaseReference updaterTime = database.getReference(globalId + "/" + "lifes");
                        int updatedLife = currentLife + 1;
                        updaterTime.setValue(updatedLife);

                        DatabaseReference updaterCoins = database.getReference(globalId + "/" + "coins");
                        int updatedCoin = currentCoins - 100;
                        updaterCoins.setValue(updatedCoin);
                        Toast.makeText(SelectWorld.this, "Purchase completed", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(SelectWorld.this, "Not enough coins", Toast.LENGTH_LONG).show();
                    }

                }catch (Exception e) {
                    System.out.println(e);
                }

            }
        });


        timeBoost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {

                    if(currentCoins > 99) {
                        DatabaseReference updaterTime = database.getReference(globalId + "/" + "time");
                        int updatedTime = currentTime + 1;
                        updaterTime.setValue(updatedTime);
                        DatabaseReference updaterCoins = database.getReference(globalId + "/" + "coins");
                        int updatedCoin = currentCoins - 100;
                        updaterCoins.setValue(updatedCoin);
                        Toast.makeText(SelectWorld.this, "Purchase completed", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(SelectWorld.this, "Not enough coins", Toast.LENGTH_LONG).show();
                    }

                }catch (Exception e) {
                    System.out.println(e);
                }
            }
        });

        replayBoost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {

                    if(currentCoins > 99) {
                        DatabaseReference updaterTime = database.getReference(globalId + "/" + "replay");
                        int updatedReplay = currentReply + 1;
                        updaterTime.setValue(updatedReplay);

                        DatabaseReference updaterCoins = database.getReference(globalId + "/" + "coins");
                        int updatedCoin = currentCoins - 100;
                        updaterCoins.setValue(updatedCoin);
                        Toast.makeText(SelectWorld.this, "Purchase completed", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(SelectWorld.this, "Not enough coins", Toast.LENGTH_LONG).show();
                    }

                }catch (Exception e) {
                    System.out.println(e);
                }
            }
        });


        customDialog.setView(view);
        final AlertDialog show = customDialog.show();

        closeDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                show.dismiss();
            }
        });

    }


}
