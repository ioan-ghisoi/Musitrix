package ioan.ghisoi.disertation;

import android.animation.ValueAnimator;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBar.LayoutParams;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.Gravity;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by jarvis on 17/04/2017.
 */

public class Rank extends AppCompatActivity {

    ArrayList<String> myParentArray = new ArrayList<>();
    ArrayList<RankElement> rankObjects = new ArrayList<RankElement>();
    LinearLayout mContainerView;
    ImageView myImage;
    LinearLayout ll_main;
    ProgressDialog pd;
    ImageButton back;
    FirebaseAuth auth = FirebaseAuth.getInstance();
    TextView rankTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rank);

        ll_main = (LinearLayout) findViewById(R.id.ll_main);
        back = (ImageButton) findViewById(R.id.backButton);
        rankTitle = (TextView) findViewById(R.id.rankTitle);

        Typeface mFont = Typeface.createFromAsset(getAssets(), "fonts/johnny.ttf");
        rankTitle.setTypeface(mFont);

        pd = new ProgressDialog(Rank.this, R.style.TransparentProgressDialog);
        pd.setMessage("Loading...");
        pd.show();

        scrollingBackground();


        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(Rank.this, MainActivity.class);
                Rank.this.startActivity(myIntent);
            }
        });

        final FirebaseDatabase updated = FirebaseDatabase.getInstance();
        DatabaseReference ref = updated.getReference();

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (final DataSnapshot child : dataSnapshot.getChildren()) {
                    System.out.println("mergea" + child.getKey());
                    final String parent = child.getKey();

                    DatabaseReference myRef = updated.getReference(parent + "/progress");
                    myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            System.out.println("progresss" + dataSnapshot.getValue().toString());
                            String value = dataSnapshot.getValue().toString();
                            rankObjects.add(new RankElement(parent, value));
                            populate2();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                    myParentArray.add(parent);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });


    }

    class RankElement {
        String uid, upicture, uscore;

        public RankElement(String uid, String uscore) {
            this.uid = uid;
            this.upicture = upicture;
            this.uscore = uscore;
        }

        public String getUid() {
            return uid;
        }

        public void setUid(String uid) {
            this.uid = uid;
        }

        public String getUpicture() {
            return upicture;
        }

        public void setUpicture(String upicture) {
            this.upicture = upicture;
        }

        public String getUscore() {
            return uscore;
        }

        public void setUscore(String uscore) {
            this.uscore = uscore;
        }
    }

    public void populate2() {

        if (rankObjects.size() == myParentArray.size()) {
            Collections.sort(rankObjects, new Comparator<RankElement>() {
                @Override
                public int compare(RankElement o1, RankElement o2) {
                    return o2.getUscore().compareToIgnoreCase(o1.getUscore());
                }
            });

            for (int i = 0; i < rankObjects.size(); i++) {
                System.out.println("OBIECTELE MELE" + rankObjects.get(i).getUscore());
            }

            FirebaseDatabase updated = FirebaseDatabase.getInstance();
            DatabaseReference ref = updated.getReference();

            for (int i = 0; i < rankObjects.size(); i++) {

                try {
                    DatabaseReference ref2 = ref.child(rankObjects.get(i).getUid() + "/playername");
                    final int finalI = i;
                    ref2.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            System.out.println("HAI IN PNLLLLLLLLLLL" + dataSnapshot.getValue().toString());

                            Collections.sort(rankObjects, new Comparator<RankElement>() {
                                @Override
                                public int compare(RankElement o1, RankElement o2) {
                                    return o2.getUscore().compareToIgnoreCase(o1.getUscore());
                                }
                            });
                            createRank(dataSnapshot.getValue(String.class), rankObjects.get(finalI).getUscore(), finalI, rankObjects.get(finalI).getUid());
                            pd.dismiss();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                } catch (Exception e) {
                    System.out.println(e);
                }

            }
        }

        System.out.println("THE SIZEEEEEEEEEE" + rankObjects.size() + " and " + myParentArray.size());
    }



    public void createRank(String player, String score, int i, String uid) {

        FirebaseDatabase updated = FirebaseDatabase.getInstance();
        DatabaseReference ref = updated.getReference();
        final DatabaseReference ref2 = ref.child(uid + "/lifes");


        try {
            if (!auth.getCurrentUser().getUid().equals(uid)) {
                Typeface mFont = Typeface.createFromAsset(getAssets(), "fonts/johnny.ttf");
                LinearLayout parent = new LinearLayout(this);
                LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(700, LinearLayout.LayoutParams.MATCH_PARENT);
                param.weight = 1;
                param.gravity = Gravity.CENTER;
                param.setMargins(0, 20, 0, 0);
                parent.setLayoutParams(param);
                parent.setGravity(Gravity.CENTER);
                parent.setOrientation(LinearLayout.HORIZONTAL);
                parent.setBackgroundColor(getResources().getColor(R.color.musitrix_white));

                TextView tv = new TextView(this);
                tv.setText(player);
                tv.setTypeface(mFont);

                TextView tv2 = new TextView(this);
                tv2.setText(score + " points");
                tv2.setTypeface(mFont);

                TextView tv3 = new TextView(this);
                tv3.setText("No." + (i + 1) + " ");
                tv3.setTextColor(getResources().getColor(R.color.musitrix_black));
                tv3.setTextSize(20);
                tv3.setTypeface(mFont);

                TextView tv4 = new TextView(this);
                tv4.setText("  ");
                tv4.setTextColor(getResources().getColor(R.color.musitrix_black));
                tv4.setTextSize(30);


                GradientDrawable shape = new GradientDrawable();
                shape.setCornerRadius(50);


                ImageView img = new ImageView(this);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(120, 100);
                img.setLayoutParams(layoutParams);
                img.setImageResource(R.drawable.rankuser);

                final ImageView img2 = new ImageView(this);
                LinearLayout.LayoutParams layoutParams2 = new LinearLayout.LayoutParams(130, 100);
                img2.setLayoutParams(layoutParams2);
                img2.setImageResource(R.drawable.sendlife);

                img2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        ref2.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                String lifes = dataSnapshot.getValue().toString();
                                int increment = Integer.parseInt(lifes) + 1;
                                ref2.setValue(increment);
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                        System.out.println("ceva");
                        img2.setImageResource(R.drawable.darklife);
                    }
                });


                shape.setColor(getResources().getColor(R.color.musitrix_green));


                parent.addView(tv3);
                parent.addView(tv4);
                parent.addView(tv);
                parent.addView(img);
                parent.addView(tv2);

                if (!auth.getCurrentUser().getUid().equals(uid)) {
                    parent.addView(img2);
                } else {
                    final ImageView placeholder = new ImageView(this);
                    LinearLayout.LayoutParams myParams = new LinearLayout.LayoutParams(130, 100);
                    placeholder.setLayoutParams(layoutParams2);
                    placeholder.setImageResource(R.drawable.darklife);
                    parent.addView(placeholder);
                }
                parent.setBackground(shape);
                ll_main.addView(parent);

            } else {


                Typeface mFont = Typeface.createFromAsset(getAssets(), "fonts/johnny.ttf");
                LinearLayout parent = new LinearLayout(this);
                LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(700, LinearLayout.LayoutParams.MATCH_PARENT);
                param.weight = 1;
                param.gravity = Gravity.CENTER;
                param.setMargins(0, 20, 0, 0);
                parent.setLayoutParams(param);
                parent.setGravity(Gravity.CENTER);
                parent.setOrientation(LinearLayout.HORIZONTAL);
                parent.setBackgroundColor(getResources().getColor(R.color.musitrix_white));

                TextView tv = new TextView(this);
                tv.setText(player);
                tv.setTypeface(mFont);
                tv.setTextColor(getResources().getColor(R.color.musitrix_white));

                TextView tv2 = new TextView(this);
                tv2.setText(score + " points");
                tv2.setTypeface(mFont);
                tv2.setTextColor(getResources().getColor(R.color.musitrix_white));

                TextView tv3 = new TextView(this);
                tv3.setText("No." + (i + 1) + " ");
                tv3.setTextColor(getResources().getColor(R.color.musitrix_green));
                tv3.setTextSize(20);
                tv3.setTypeface(mFont);

                TextView tv4 = new TextView(this);
                tv4.setText("  ");
                tv4.setTextColor(getResources().getColor(R.color.musitrix_white));
                tv4.setTextSize(30);


                GradientDrawable shape = new GradientDrawable();
                shape.setCornerRadius(50);


                ImageView img = new ImageView(this);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(120, 100);
                img.setLayoutParams(layoutParams);
                img.setImageResource(R.drawable.rankuser);


                shape.setColor(getResources().getColor(R.color.musitrix_black));


                parent.addView(tv3);
                parent.addView(tv4);
                parent.addView(tv);
                parent.addView(img);
                parent.addView(tv2);

                final ImageView placeholder = new ImageView(this);
                LinearLayout.LayoutParams myParams = new LinearLayout.LayoutParams(130, 100);
                placeholder.setLayoutParams(myParams);
                placeholder.setImageResource(R.drawable.darklife);
                placeholder.setVisibility(View.INVISIBLE);
                parent.addView(placeholder);
                parent.setBackground(shape);

                ll_main.addView(parent);
            }

        } catch (Exception e) {
            System.out.println(e);
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
}
