package ioan.ghisoi.disertation;

import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.facebook.login.widget.ProfilePictureView;

/**
 * Created by jarvis on 11/04/2017.
 */

public class SelectLevel extends AppCompatActivity {

    GridView androidGridView;
    ImageButton backButton;

    String[] gridViewString = {
            "★★✩", "★★★", "★✩✩", "★★★", "★✩✩", "★✩✩",
            "★✩✩", "★★★", "★★✩",

    } ;
    int[] gridViewImageId = {
            R.drawable.level1, R.drawable.level2, R.drawable.level3, R.drawable.level4, R.drawable.level5,
            R.drawable.level6, R.drawable.level7, R.drawable.level8, R.drawable.level9,
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_level);

        CustomGridViewActivity adapterViewAndroid = new CustomGridViewActivity(SelectLevel.this, gridViewString, gridViewImageId);
        androidGridView = (GridView) findViewById(R.id.grid_view_image_text);
        androidGridView.setVerticalScrollBarEnabled(false);
        androidGridView.setAdapter(adapterViewAndroid);

        scrollingBackground();

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

}
