package ioan.ghisoi.disertation;

import android.animation.ValueAnimator;
import android.app.ProgressDialog;
import android.content.Context;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;


public class SelectWorld extends AppCompatActivity implements BaseSliderView.OnSliderClickListener, ViewPagerEx.OnPageChangeListener{

    private ImageView mBack, mShop, mUserPicture;
    private SliderLayout mWorldSlider;
    private TextView mUserName, mUserLifes, mUserCoins, mWorldProgress;
    private FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private int mCurrentCoins, mCurrentLifes, mCurrentTime, mCurrentReplay;
    public ImageView lifeBoost, timeBoost, replayBoost, closeDialog;
    HashMap<String,Integer> file_maps;
    private ProgressDialog pd;
    public String mGlobalId;
    private ImageView mScroll1, mScroll2;;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_world);

        mBack = (ImageView) findViewById(R.id.back);
        mShop = (ImageView) findViewById(R.id.shop);
        mUserPicture = (ImageView) findViewById(R.id.user_picture);
        mUserName = (TextView) findViewById(R.id.user_name);
        mUserLifes = (TextView) findViewById(R.id.user_lifes);
        mUserCoins = (TextView) findViewById(R.id.user_coins);
        mWorldProgress = (TextView) findViewById(R.id.world_progress);
        mWorldSlider = (SliderLayout)findViewById(R.id.slider);
        mScroll1 = (ImageView) findViewById(R.id.scrolling_1);
        mScroll2 = (ImageView) findViewById(R.id.scrolling_2);


        scrollingBackground();

        loadUserData();

        populateSlider();

        setCostumeFont();

        handleButtons();

        mGlobalId = mAuth.getCurrentUser().getUid();

    }

    @Override
    protected void onStop() {
        // To prevent a memory leak on rotation, make sure to call stopAutoCycle() on the slider before activity or fragment is destroyed
        mWorldSlider.stopAutoCycle();
        super.onStop();
    }

    @Override
    public void onSliderClick(BaseSliderView slider) {

        Toast.makeText(SelectWorld.this, "" + slider.getBundle().get("extra"), Toast.LENGTH_LONG).show();
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

    public void setImage(ImageView imageview, String url) {
        try {

            Picasso.with(SelectWorld.this)
                    .load(url)
                    .config(Bitmap.Config.RGB_565)
                    .fit()
                    .centerInside()
                    .into(imageview);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadUserData() {

        pd = new ProgressDialog(SelectWorld.this, R.style.TransparentProgressDialog);
        pd.setMessage("Loading...");
        pd.show();

        if(mAuth.getCurrentUser() != null) {

            setImage(mUserPicture, String.valueOf(mAuth.getCurrentUser().getPhotoUrl()));
            mUserName.setText(mAuth.getCurrentUser().getDisplayName());

            DatabaseReference coinsWatcher = mDatabase.getReference(mAuth.getCurrentUser().getUid() + "/coins");
            coinsWatcher.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    mUserCoins.setText(dataSnapshot.getValue().toString());
                    mCurrentCoins = Integer.parseInt(dataSnapshot.getValue().toString());
                    pd.dismiss();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            DatabaseReference lifeWatcher = mDatabase.getReference(mAuth.getCurrentUser().getUid() + "/lifes");
            lifeWatcher.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    mUserLifes.setText(dataSnapshot.getValue().toString());
                    mCurrentLifes = Integer.parseInt(dataSnapshot.getValue().toString());
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            DatabaseReference progressWatcher = mDatabase.getReference(mAuth.getCurrentUser().getUid() + "/progress");
            progressWatcher.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    mWorldProgress.setText("Total Score: " + dataSnapshot.getValue().toString());
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            DatabaseReference replayWatcher = mDatabase.getReference(mAuth.getCurrentUser().getUid() + "/replay");
            replayWatcher.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    mCurrentReplay = Integer.parseInt(dataSnapshot.getValue().toString());
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            DatabaseReference timeWatcher = mDatabase.getReference(mAuth.getCurrentUser().getUid() + "/time");
            timeWatcher.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    mCurrentTime = Integer.parseInt(dataSnapshot.getValue().toString());
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }
    }

    public void populateSlider() {
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

            textSliderView.bundle(new Bundle());
            textSliderView.getBundle()
                    .putString("extra",name);

            mWorldSlider.addSlider(textSliderView);
        }
        mWorldSlider.setPresetTransformer(SliderLayout.Transformer.ZoomOutSlide);
        mWorldSlider.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
        mWorldSlider.setCustomAnimation(new DescriptionAnimation());
        mWorldSlider.setDuration(0);
        mWorldSlider.stopAutoCycle();
        mWorldSlider.addOnPageChangeListener(SelectWorld.this);
    }

    public void handleButtons() {

        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(SelectWorld.this, MainActivity.class);
                SelectWorld.this.startActivity(myIntent);
            }
        });

        mShop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openShop();
            }
        });
    }

    public void setCostumeFont() {
        Typeface mFont = Typeface.createFromAsset(getAssets(), "fonts/johnny.ttf");
        mWorldProgress.setTypeface(mFont);
        mUserCoins.setTypeface(mFont);
        mUserLifes.setTypeface(mFont);
        mUserName.setTypeface(mFont);
    }

    void openShop(){
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

                    if(mCurrentCoins > 99) {
                        DatabaseReference updaterTime = mDatabase.getReference(mGlobalId + "/" + "lifes");
                        int updatedLife = mCurrentLifes + 1;
                        updaterTime.setValue(updatedLife);

                        DatabaseReference updaterCoins = mDatabase.getReference(mGlobalId + "/" + "coins");
                        int updatedCoin = mCurrentCoins - 100;
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

                    if(mCurrentCoins > 99) {
                        DatabaseReference updaterTime = mDatabase.getReference(mGlobalId + "/" + "time");
                        int updatedTime = mCurrentTime + 1;
                        updaterTime.setValue(updatedTime);
                        DatabaseReference updaterCoins = mDatabase.getReference(mGlobalId + "/" + "coins");
                        int updatedCoin = mCurrentCoins - 100;
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

                    if(mCurrentCoins > 99) {
                        DatabaseReference updaterTime = mDatabase.getReference(mGlobalId + "/" + "replay");
                        int updatedReplay = mCurrentReplay + 1;
                        updaterTime.setValue(updatedReplay);

                        DatabaseReference updaterCoins = mDatabase.getReference(mGlobalId + "/" + "coins");
                        int updatedCoin = mCurrentCoins - 100;
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

}
