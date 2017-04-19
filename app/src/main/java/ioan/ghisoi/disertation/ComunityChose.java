package ioan.ghisoi.disertation;

import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageView;

/**
 * Created by jarvis on 18/04/2017.
 */

public class ComunityChose extends AppCompatActivity {


    Button contribute, browse;
    ImageView back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contribution_maker);
        scrollingBackground();

        contribute = (Button) findViewById(R.id.makeContribution);
        browse = (Button) findViewById(R.id.seeContribution);
        back = (ImageView) findViewById(R.id.backButton);

        contribute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(ComunityChose.this, ComunityContributionContributor.class);
                ComunityChose.this.startActivity(myIntent);
            }
        });

        browse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(ComunityChose.this, ComunityContributionView.class);
                ComunityChose.this.startActivity(myIntent);
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(ComunityChose.this, MainActivity.class);
                ComunityChose.this.startActivity(myIntent);
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
