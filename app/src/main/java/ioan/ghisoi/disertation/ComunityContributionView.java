package ioan.ghisoi.disertation;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by jarvis on 18/04/2017.
 */

public class ComunityContributionView extends AppCompatActivity {

    LinearLayout mParentLayout;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    TextView mComunityTitle;
    ArrayAdapter<String> songs;
    ProgressDialog pd;
    ImageButton back;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        mParentLayout = (LinearLayout) findViewById(R.id.main_view);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.contribution_view);

        mParentLayout = (LinearLayout) findViewById(R.id.main_view);
        mComunityTitle = (TextView) findViewById(R.id.mComunityTitle);
        back = (ImageButton) findViewById(R.id.back);

        pd = new ProgressDialog(ComunityContributionView.this, R.style.TransparentProgressDialog);
        pd.setMessage("Loading...");
        pd.show();

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(ComunityContributionView.this, ComunityChose.class);
                ComunityContributionView.this.startActivity(myIntent);
            }
        });

        Typeface mFont = Typeface.createFromAsset(getAssets(), "fonts/johnny.ttf");
        mComunityTitle.setTypeface(mFont);

        DatabaseReference songsWatcher = database.getReference("Contributors/");

        songsWatcher.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot childDataSnapshot : dataSnapshot.getChildren()) {
                    populateWithView(
                            "" + childDataSnapshot.child("name").getValue(),
                            "" + childDataSnapshot.child("song").getValue(),
                            "" + childDataSnapshot.child("image").getValue(),
                            "" + childDataSnapshot.child("piece").getValue(),
                            "" + childDataSnapshot.child("details").getValue());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public void populateWithView (final String userName, final String songLink, final String imageLink, final String piece, final String mDetails) {
        pd.dismiss();
        try{
            final LinearLayout parent = new LinearLayout(this);
            LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(700, LinearLayout.LayoutParams.MATCH_PARENT);
            param.weight = 1;
            param.gravity = Gravity.CENTER;
            param.setMargins(0, 20, 0, 0);
            parent.setLayoutParams(param);
            parent.setGravity(Gravity.CENTER);
            parent.setOrientation(LinearLayout.HORIZONTAL);
            parent.setBackgroundColor(getResources().getColor(R.color.musitrix_white));


            GradientDrawable shape = new GradientDrawable();
            shape.setCornerRadius(50);
            shape.setColor(getResources().getColor(R.color.musitrix_black));

            ImageView image = new ImageView(this);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(120, 100);
            image.setLayoutParams(layoutParams);
            image.setImageResource(R.drawable.activepiece);


            Typeface mFont = Typeface.createFromAsset(getAssets(), "fonts/johnny.ttf");
            TextView text = new TextView(this);
            text.setTypeface(mFont);
            text.setTextColor(getResources().getColor(R.color.musitrix_white));
            text.setText(userName);

            parent.addView(image);
            parent.addView(text);

            image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                Intent myIntent = new Intent(ComunityContributionView.this, CustomeGame.class);
                myIntent.putExtra("song",""+ songLink);
                myIntent.putExtra("image",""+ imageLink);
                myIntent.putExtra("pieces",""+ piece);
                myIntent.putExtra("details",""+ mDetails);
                ComunityContributionView.this.startActivity(myIntent);
                }
            });

            parent.setBackground(shape);
            mParentLayout.addView(parent);
        }catch (Exception e) {
            System.out.println(e);
        }
    }

}
