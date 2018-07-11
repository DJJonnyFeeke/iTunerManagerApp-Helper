package me.personal.jfeeke.multimediamobileviewer;

import android.app.ActionBar;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ViewFlipper;

public class HelperActivity extends AppCompatActivity {

    private ViewFlipper simpleViewFlipper;
    Button btnNext,btnClose;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_helper);

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.override_helper_title);

        // get The references of Button and ViewFlipper
        btnNext = findViewById(R.id.buttonNext);
        btnClose = findViewById(R.id.buttonClose);
        simpleViewFlipper = findViewById(R.id.simpleViewFlipper); // get the reference of ViewFlipper
        // Declare in and out animations and load them using AnimationUtils class
        Animation in = AnimationUtils.loadAnimation(this, android.R.anim.slide_in_left);
        Animation out = AnimationUtils.loadAnimation(this, android.R.anim.slide_out_right);

        // set the animation type to ViewFlipper
        simpleViewFlipper.setInAnimation(in);
        simpleViewFlipper.setOutAnimation(out);

        // ClickListener for NEXT button
        // When clicked on Button ViewFlipper will switch between views
        // The current view will go out and next view will come in with specified animation
        btnNext.setOnClickListener(v -> {
            // TODO Auto-generated method stub
            // show the next view of ViewFlipper
            simpleViewFlipper.showNext();
        });

        // ClickListener for CLOSE button
        // When clicked on Button the ViewFlipper will close
        btnClose.setOnClickListener(v -> {
            // TODO Auto-generated method stub
            // close the ViewFlipper
            finish();
        });
    }

}
