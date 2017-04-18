package ioan.ghisoi.disertation;

import android.animation.ValueAnimator;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.Map;

/**
 * Created by jarvis on 11/04/2017.
 */

public class SelectLevel extends AppCompatActivity {


    ImageView mUserPicture;
    ImageView lv1, lv2, lv3, lv4, lv5, lv6, lv7, lv8, lv9;
    ImageView lv1_star, lv2_star, lv3_star, lv4_star, lv5_star, lv6_star, lv7_star, lv8_star, lv9_star;
    TextView mProgress, mLifes, mCoins, mUserName;
    ImageButton back;
    String myWorld = "world1";
    ProgressDialog pd;
    boolean isAccesable = false;

    FirebaseAuth auth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_level);

        pd = new ProgressDialog(SelectLevel.this, R.style.TransparentProgressDialog);
        pd.setMessage("Loading...");
        pd.show();

        Intent myIntent= getIntent();
        Bundle myBundle = myIntent.getExtras();

        scrollingBackground();

        lv1 = (ImageView) findViewById(R.id.lvl1_image);
        lv2 = (ImageView) findViewById(R.id.lvl2_image);
        lv3 = (ImageView) findViewById(R.id.lvl3_image);
        lv4 = (ImageView) findViewById(R.id.lvl4_image);
        lv5 = (ImageView) findViewById(R.id.lvl5_image);
        lv6 = (ImageView) findViewById(R.id.lvl6_image);
        lv7 = (ImageView) findViewById(R.id.lvl7_image);
        lv8 = (ImageView) findViewById(R.id.lvl8_image);
        lv9 = (ImageView) findViewById(R.id.lvl9_image);
        lv1_star = (ImageView) findViewById(R.id.lvl1_stars);
        lv2_star = (ImageView) findViewById(R.id.lvl2_stars);
        lv3_star = (ImageView) findViewById(R.id.lvl3_stars);
        lv4_star = (ImageView) findViewById(R.id.lvl4_stars);
        lv5_star = (ImageView) findViewById(R.id.lvl5_stars);
        lv6_star = (ImageView) findViewById(R.id.lvl6_stars);
        lv7_star = (ImageView) findViewById(R.id.lvl7_stars);
        lv8_star = (ImageView) findViewById(R.id.lvl8_stars);
        lv9_star = (ImageView) findViewById(R.id.lvl9_stars);
        //mProgress = (TextView) findViewById(R.id.level_progress);
        mLifes = (TextView) findViewById(R.id.user_lifes);
        mCoins = (TextView) findViewById(R.id.user_coins);
        mUserPicture = (ImageView) findViewById(R.id.user_picture);
        mUserName = (TextView) findViewById(R.id.user_name);
        back = (ImageButton) findViewById(R.id.backButton);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(SelectLevel.this, SelectWorld.class);
                SelectLevel.this.startActivity(myIntent);
            }
        });

        if(myBundle!=null)
        {
            String myWorld =(String) myBundle.get("world");
            //Toast.makeText(SelectLevel.this, "" + myWorld, Toast.LENGTH_LONG).show();
        }

        try{
            if(auth.getCurrentUser() != null){
                setImage(mUserPicture, String.valueOf(FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl()));
                mUserName.setText(auth.getCurrentUser().getDisplayName());
                //Toast.makeText(SelectLevel.this, "" + myWorld.equals("world1"), Toast.LENGTH_LONG).show();
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference myRef = database.getReference(auth.getCurrentUser().getUid() + "/" + myWorld);

                myRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        try{
                            Map<String, Object> newPost = (Map<String, Object>) dataSnapshot.getValue();
                            System.out.println("TTTTTTTTTTTTTTTTTTTTTTTTT: " + R.drawable.star0 +" "+ R.drawable.star3);
                            System.out.println("AAAAAAAAAAAAAAAAAAAAAAAAAA: " +Integer.parseInt(String.valueOf(((Map)newPost.get( "level1" )).get( "stars" ))));



                            int n1 = Integer.parseInt(String.valueOf(((Map)newPost.get( "level1" )).get( "icon" )));
                            int n2 = Integer.parseInt(String.valueOf(((Map)newPost.get( "level2" )).get( "icon" )));
                            int n3 = Integer.parseInt(String.valueOf(((Map)newPost.get( "level3" )).get( "icon" )));
                            int n4 = Integer.parseInt(String.valueOf(((Map)newPost.get( "level4" )).get( "icon" )));
                            int n5 = Integer.parseInt(String.valueOf(((Map)newPost.get( "level5" )).get( "icon" )));
                            int n6 = Integer.parseInt(String.valueOf(((Map)newPost.get( "level6" )).get( "icon" )));
                            int n7 = Integer.parseInt(String.valueOf(((Map)newPost.get( "level7" )).get( "icon" )));
                            int n8 = Integer.parseInt(String.valueOf(((Map)newPost.get( "level8" )).get( "icon" )));
                            int n9 = Integer.parseInt(String.valueOf(((Map)newPost.get( "level9" )).get( "icon" )));

                            setImage(n1, lv1);
                            setImage(n2, lv2);
                            setImage(n3, lv3);
                            setImage(n4, lv4);
                            setImage(n5, lv5);
                            setImage(n6, lv6);
                            setImage(n7, lv7);
                            setImage(n8, lv8);
                            setImage(n9, lv9);


                            int l1 = Integer.parseInt(String.valueOf(((Map)newPost.get( "level1" )).get( "stars" )));
                            int l2 = Integer.parseInt(String.valueOf(((Map)newPost.get( "level2" )).get( "stars" )));
                            int l3 = Integer.parseInt(String.valueOf(((Map)newPost.get( "level3" )).get( "stars" )));
                            int l4 = Integer.parseInt(String.valueOf(((Map)newPost.get( "level4" )).get( "stars" )));
                            int l5 = Integer.parseInt(String.valueOf(((Map)newPost.get( "level5" )).get( "stars" )));
                            int l6 = Integer.parseInt(String.valueOf(((Map)newPost.get( "level6" )).get( "stars" )));
                            int l7 = Integer.parseInt(String.valueOf(((Map)newPost.get( "level7" )).get( "stars" )));
                            int l8 = Integer.parseInt(String.valueOf(((Map)newPost.get( "level8" )).get( "stars" )));
                            int l9 = Integer.parseInt(String.valueOf(((Map)newPost.get( "level9" )).get( "stars" )));


                            setImage(l1, lv1_star);
                            setImage(l2, lv2_star);
                            setImage(l3, lv3_star);
                            setImage(l4, lv4_star);
                            setImage(l5, lv5_star);
                            setImage(l6, lv6_star);
                            setImage(l7, lv7_star);
                            setImage(l8, lv8_star);
                            setImage(l9, lv9_star);

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

            }
        }catch (Exception e) {
            System.out.println(e);
        }
        FirebaseDatabase database = FirebaseDatabase.getInstance();

        DatabaseReference lifeWatcher = database.getReference(auth.getCurrentUser().getUid() + "/lifes");

        lifeWatcher.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String value = dataSnapshot.getValue().toString();
                try{
                    mLifes.setText(value);
                    try {
                        Typeface mFont = Typeface.createFromAsset(getAssets(), "fonts/johnny.ttf");
                        mProgress.setTypeface(mFont);
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


        DatabaseReference progressWatcher = database.getReference(auth.getCurrentUser().getUid() + "/total" + myWorld);

        progressWatcher.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String value = dataSnapshot.getValue().toString();
                try{
                    mProgress.setText(value);
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
                } catch (Exception e) {
                    System.out.println();
                }
            }
            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
            }
        });



        lv1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(SelectLevel.this, Game.class);
                myIntent.putExtra("level",""+ "1");
                myIntent.putExtra("world",""+ myWorld);
                myIntent.putExtra("icon",""+ R.drawable.lvl1);
                myIntent.putExtra("next",""+ R.drawable.lvl2);
                myIntent.putExtra("background",""+ "https://firebasestorage.googleapis.com/v0/b/musitrix-disertation.appspot.com/o/paralex2.jpg?alt=media&token=0b66eec2-2496-4023-a653-67d9acbd334d");
                SelectLevel.this.startActivity(myIntent);
            }
        });

        lv2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(SelectLevel.this, Game.class);
                if(isPlayable("level2")) {
                    myIntent.putExtra("level",""+ "2");
                    myIntent.putExtra("world",""+ myWorld);
                    myIntent.putExtra("icon",""+ R.drawable.lvl2);
                    myIntent.putExtra("next",""+ R.drawable.lvl3);
                    SelectLevel.this.startActivity(myIntent);
                }
            }
        });

        lv3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(SelectLevel.this, Game.class);
                myIntent.putExtra("level",""+ "3");
                myIntent.putExtra("world",""+ myWorld);
                myIntent.putExtra("icon",""+ R.drawable.lvl3);
                myIntent.putExtra("next",""+ R.drawable.lvl4);
                SelectLevel.this.startActivity(myIntent);
            }
        });

        lv4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(SelectLevel.this, Game.class);
                myIntent.putExtra("level",""+ "4");
                myIntent.putExtra("world",""+ myWorld);
                myIntent.putExtra("icon",""+ R.drawable.lvl4);
                myIntent.putExtra("next",""+ R.drawable.lvl5);
                SelectLevel.this.startActivity(myIntent);
            }
        });

        lv5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(SelectLevel.this, Game.class);
                myIntent.putExtra("level",""+ "5");
                myIntent.putExtra("world",""+ myWorld);
                myIntent.putExtra("icon",""+ R.drawable.lvl5);
                myIntent.putExtra("next",""+ R.drawable.lvl6);
                SelectLevel.this.startActivity(myIntent);
            }
        });

        lv6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(SelectLevel.this, Game.class);
                myIntent.putExtra("level",""+ "6");
                myIntent.putExtra("world",""+ myWorld);
                myIntent.putExtra("icon",""+ R.drawable.lvl6);
                myIntent.putExtra("next",""+ R.drawable.lvl7);
                SelectLevel.this.startActivity(myIntent);
            }
        });

        lv7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(SelectLevel.this, Game.class);
                myIntent.putExtra("level",""+ "7");
                myIntent.putExtra("world",""+ myWorld);
                myIntent.putExtra("icon",""+ R.drawable.lvl7);
                myIntent.putExtra("next",""+ R.drawable.lvl8);
                SelectLevel.this.startActivity(myIntent);
            }
        });

        lv8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(SelectLevel.this, Game.class);
                myIntent.putExtra("level",""+ "8");
                myIntent.putExtra("world",""+ myWorld);
                myIntent.putExtra("icon",""+ R.drawable.lvl8);
                myIntent.putExtra("next",""+ R.drawable.lvl9);
                SelectLevel.this.startActivity(myIntent);
            }
        });

        lv9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(SelectLevel.this, Game.class);
                myIntent.putExtra("level",""+ "9");
                myIntent.putExtra("world",""+ myWorld);
                myIntent.putExtra("icon",""+ R.drawable.lvl2);
                myIntent.putExtra("next",""+ R.drawable.lvl9);
                SelectLevel.this.startActivity(myIntent);
            }
        });

//        lv1_star.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent myIntent = new Intent(SelectLevel.this, Game.class);
//                SelectLevel.this.startActivity(myIntent);
//            }
//        });

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

    public boolean isPlayable(String level){

        FirebaseDatabase updated = FirebaseDatabase.getInstance();
        DatabaseReference ref = updated.getReference(auth.getCurrentUser().getUid()+"/"+myWorld+"/"+level+ "/status");

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                System.out.println(dataSnapshot.getValue());
                if(dataSnapshot.getValue().toString().equals("unlocked")) {
                    isAccesable = true;
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed
            }
        });

        return isAccesable;
    }
}

