package com.bhash.cabtask;

import android.app.Activity;
import android.content.Context;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public abstract class BaseActivity extends AppCompatActivity {
    private Boolean activityFinishFlag;
    public boolean shouldDiscardChanges = false;
    private static final String TAG = BaseActivity.class.getCanonicalName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    protected abstract void initializeView();

    public void setupActionBar(String title, final ActionBarActivityLeftAction leftAction, final ActionBarActivityRightAction rightAction, final ActionBarActivityRight2Action right2Action) {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar == null) {
            return;
        }
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(false);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        View customView = getLayoutInflater().inflate(R.layout.framework_action_bar_view, null);
        getSupportActionBar().setCustomView(customView);
        Toolbar parent = (Toolbar) customView.getParent();
        parent.setContentInsetsAbsolute(0, 0);

        final TextView activityTitle = ((TextView) getSupportActionBar().getCustomView().findViewById(R.id.action_bar_title));
        /*Typeface custom_title_font = Typeface.createFromAsset(getAssets(),  "fonts/Lato-Regular.ttf");
        activityTitle.setTypeface(custom_title_font);*/
        activityTitle.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);
        activityTitle.setText(title);
        final ImageView leftIcon = ((ImageView) getSupportActionBar().getCustomView().findViewById(R.id.action_bar_leftIcon));
        leftIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actionBarLeftButtonClicked();
                if (activityFinishFlag && !shouldDiscardChanges) {
                    finish();
                } else if (shouldDiscardChanges) {
                    onBackPressed();
                }
            }
        });

        leftIcon.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    TypedValue outValue = new TypedValue();
                    getApplicationContext().getTheme().resolveAttribute(android.R.attr.selectableItemBackgroundBorderless, outValue, true);
                    ((ImageView) v).setBackgroundResource(outValue.resourceId);
                }
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    ((ImageView) v).setBackgroundColor(getResources().getColor(android.R.color.transparent));
                }
                return false;
            }
        });

        final ImageView rightIcon = ((ImageView) getSupportActionBar().getCustomView().findViewById(R.id.action_bar_rightIcon));
        rightIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        final ImageView rightIcon2 = ((ImageView) getSupportActionBar().getCustomView().findViewById(R.id.action_bar_rightIcon2));
        rightIcon2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        TextView rightText = ((TextView) getSupportActionBar().getCustomView().findViewById(R.id.action_bar_rightText));
        /*Typeface custom_font = Typeface.createFromAsset(getAssets(),  "fonts/Lato-Regular.ttf");
        rightText.setTypeface(custom_font);*/


        rightIcon.setVisibility(View.GONE);
        leftIcon.setVisibility(View.GONE);
        rightIcon2.setVisibility(View.INVISIBLE);
        RelativeLayout.LayoutParams relativeLayoutParam = (RelativeLayout.LayoutParams) activityTitle.getLayoutParams();
        switch (leftAction) {
            case ACTION_BACK:
                leftIcon.setImageResource(R.drawable.keyboard_back_icon);
                leftIcon.setVisibility(View.VISIBLE);
                activityFinishFlag = true;
                break;

        }
        switch (rightAction) {
            case ACTION_NONE:
                break;

        }
        switch (right2Action) {
            case ACTION_NONE:
                break;


        }
    }

    public void showToolbar(boolean show) {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar == null) {
            return;
        }
        if (show) {
            toolbar.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2));
        } else {
            toolbar.animate().translationY(-toolbar.getHeight()).setInterpolator(new AccelerateInterpolator(2));
        }
    }

    public void actionBarLeftButtonClicked() {
        //Toast.makeText(this, "LeftIcon Clicked", Toast.LENGTH_SHORT).show();
    }

    public void actionBarRightButtonClicked() {
        //Toast.makeText(this, "RightIcon Clicked", Toast.LENGTH_SHORT).show();
    }

    public void actionBarRight2ButtonClicked() {
        //Toast.makeText(this, "Clicked Right2Icon", Toast.LENGTH_SHORT).show();
    }

    public void actionBarRight2TextClicked() {
        //Toast.makeText(this, "Clicked Right2Text", Toast.LENGTH_SHORT).show();
    }

    public enum ActionBarActivityLeftAction {

        ACTION_NONE,
        ACTION_BACK
    }

    public enum ActionBarActivityRightAction {

        ACTION_NONE,
    }

    public enum ActionBarActivityRight2Action {
        ACTION_NONE,


    }

    @Override
    public void onBackPressed() {
        if (shouldDiscardChanges) {

        }
        finish();
    }
//------------------------hiding the keypad ontouch of outside----------------------------------------

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        View v = getCurrentFocus();
        if (v != null &&
                (ev.getAction() == MotionEvent.ACTION_UP || ev.getAction() == MotionEvent.ACTION_MOVE) &&
                v instanceof EditText &&
                !v.getClass().getName().startsWith("android.webkit.")) {
            int scrcoords[] = new int[2];
            v.getLocationOnScreen(scrcoords);
            float x = ev.getRawX() + v.getLeft() - scrcoords[0];
            float y = ev.getRawY() + v.getTop() - scrcoords[1];

            if (x < v.getLeft() || x > v.getRight() || y < v.getTop() || y > v.getBottom())
                hideKeyboard(this);
        }
        return super.dispatchTouchEvent(ev);
    }

    public static void hideKeyboard(Activity activity) {
        if (activity != null && activity.getWindow() != null && activity.getWindow().getDecorView() != null) {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(activity.getWindow().getDecorView().getWindowToken(), 0);
        }
    }



}