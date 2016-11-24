package me.recette.ButtonAnimation;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import butterknife.Bind;
import butterknife.ButterKnife;
import me.recette.OneRecipeActivity;
import me.recette.MainListActivity;
import me.recette.R;

/**
 * Created by Me on 04/11/2016.
 */
public class LikeButtonView extends FrameLayout implements View.OnClickListener {
    private static final DecelerateInterpolator DECCELERATE_INTERPOLATOR = new DecelerateInterpolator();
    private static final AccelerateDecelerateInterpolator ACCELERATE_DECELERATE_INTERPOLATOR = new AccelerateDecelerateInterpolator();
    private static final OvershootInterpolator OVERSHOOT_INTERPOLATOR = new OvershootInterpolator(4);
    private String layoutName;
    private Context context;

    @Bind(R.id.ivStar)
    ImageView ivStar;
    @Bind(R.id.vDotsView)
    DotsView vDotsView;
    @Bind(R.id.vCircle)
    CircleView vCircle;
    private ImageView difficultyDownwardArrow;
    private ImageView difficultyUpwardArrow;
    private ImageView timeDownwardArrow;
    private ImageView timeUpwardArrow;
    private ImageView costDownwardArrow;
    private ImageView costUpwardArrow;

    private boolean isChecked;
    private AnimatorSet animatorSet;

    public LikeButtonView(Context context) {
        super(context);
        this.context = context;
        //init();
    }

    public LikeButtonView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        //init();
    }

    public LikeButtonView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        //init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public LikeButtonView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.context = context;
        //init();
    }

    public void init() {

        int layoutID = getResources().getIdentifier(layoutName, "layout", context.getPackageName());
        LayoutInflater.from(getContext()).inflate(layoutID, this, true);
        difficultyDownwardArrow = (ImageView) ((Activity) context).findViewById(R.id.difficultyDownwardArrow);
        difficultyUpwardArrow = (ImageView) ((Activity) context).findViewById(R.id.difficultyUpwardArrow);
        timeDownwardArrow = (ImageView) ((Activity) context).findViewById(R.id.timeDownwardArrow);
        timeUpwardArrow = (ImageView) ((Activity) context).findViewById(R.id.timeUpwardArrow);
        costDownwardArrow = (ImageView) ((Activity) context).findViewById(R.id.costDownwardArrow);
        costUpwardArrow = (ImageView) ((Activity) context).findViewById(R.id.costUpwardArrow);
        ButterKnife.bind(this);
        setOnClickListener(this);
    }

    public String getLayoutName() {
        return layoutName;
    }

    public void setLayoutName(String layoutName) {
        this.layoutName = layoutName;
    }

    @Override
    public void onClick(View v) {
        OneRecipeActivity.likeValue = !OneRecipeActivity.likeValue;
        isChecked = !isChecked;

        int drawable1 = 0;
        int drawable2 = 0;
        int drawable3 = getResources().getIdentifier("upward_arrow", "drawable", context.getPackageName());
        int drawable4 = getResources().getIdentifier("upward_arrow_red", "drawable", context.getPackageName());
        int drawable5 = getResources().getIdentifier("downward_arrow", "drawable", context.getPackageName());
        int drawable6 = getResources().getIdentifier("downward_arrow_red", "drawable", context.getPackageName());

        if(layoutName.equals("view_like_button")) {
            drawable1 = getResources().getIdentifier("favorite_red", "drawable", context.getPackageName());
            drawable2 = getResources().getIdentifier("favorite_icon", "drawable", context.getPackageName());
        }
        if(layoutName.equals("view_like_button_main_list_activity")) {
            drawable1 = getResources().getIdentifier("favorite_red", "drawable", context.getPackageName());
            drawable2 = getResources().getIdentifier("favorite_icon", "drawable", context.getPackageName());

            if(isChecked) MainListActivity.likeFilter = '1';
            else MainListActivity.likeFilter = '0';

        }
        if(layoutName.equals("view_cost_button")) {
            drawable1 = getResources().getIdentifier("euro_icon_red", "drawable", context.getPackageName());
            drawable2 = getResources().getIdentifier("euro_icon", "drawable", context.getPackageName());
            costUpwardArrow.setImageResource(isChecked ? drawable4 : drawable3);
            costDownwardArrow.setImageResource(isChecked ? drawable5 : drawable6);
            if(isChecked) MainListActivity.costFilter = '1';
            else MainListActivity.costFilter = '0';
            //Log.d("Cost Filter", String.valueOf(MainListActivity.costFilter));
        }
        if(layoutName.equals("view_difficulty_button")) {
            drawable1 = getResources().getIdentifier("level_icon_red", "drawable", context.getPackageName());
            drawable2 = getResources().getIdentifier("level_icon", "drawable", context.getPackageName());
            difficultyUpwardArrow.setImageResource(isChecked ? drawable4 : drawable3);
            difficultyDownwardArrow.setImageResource(isChecked ? drawable5 : drawable6);
            if(isChecked) MainListActivity.difficultyFilter = '1';
            else MainListActivity.difficultyFilter = '0';
        }
        if(layoutName.equals("view_time_button")) {
            drawable1 = getResources().getIdentifier("alarm_icon_red", "drawable", context.getPackageName());
            drawable2 = getResources().getIdentifier("alarm_icon", "drawable", context.getPackageName());
            timeUpwardArrow.setImageResource(isChecked ? drawable4 : drawable3);
            timeDownwardArrow.setImageResource(isChecked ? drawable5 : drawable6);
            if(isChecked) MainListActivity.timeFilter = '1';
            else MainListActivity.timeFilter = '0';
        }

        ivStar.setImageResource(isChecked ? drawable1 : drawable2);

        if (animatorSet != null) {
            animatorSet.cancel();
        }

        if (isChecked) {
            ivStar.animate().cancel();
            ivStar.setScaleX(0);
            ivStar.setScaleY(0);
            vCircle.setInnerCircleRadiusProgress(0);
            vCircle.setOuterCircleRadiusProgress(0);
            vDotsView.setCurrentProgress(0);

            animatorSet = new AnimatorSet();

            ObjectAnimator outerCircleAnimator = ObjectAnimator.ofFloat(vCircle, CircleView.OUTER_CIRCLE_RADIUS_PROGRESS, 0.1f, 1f);
            outerCircleAnimator.setDuration(250);
            outerCircleAnimator.setInterpolator(DECCELERATE_INTERPOLATOR);

            ObjectAnimator innerCircleAnimator = ObjectAnimator.ofFloat(vCircle, CircleView.INNER_CIRCLE_RADIUS_PROGRESS, 0.1f, 1f);
            innerCircleAnimator.setDuration(200);
            innerCircleAnimator.setStartDelay(200);
            innerCircleAnimator.setInterpolator(DECCELERATE_INTERPOLATOR);

            ObjectAnimator starScaleYAnimator = ObjectAnimator.ofFloat(ivStar, ImageView.SCALE_Y, 0.2f, 1f);
            starScaleYAnimator.setDuration(350);
            starScaleYAnimator.setStartDelay(250);
            starScaleYAnimator.setInterpolator(OVERSHOOT_INTERPOLATOR);

            ObjectAnimator starScaleXAnimator = ObjectAnimator.ofFloat(ivStar, ImageView.SCALE_X, 0.2f, 1f);
            starScaleXAnimator.setDuration(350);
            starScaleXAnimator.setStartDelay(250);
            starScaleXAnimator.setInterpolator(OVERSHOOT_INTERPOLATOR);

            ObjectAnimator dotsAnimator = ObjectAnimator.ofFloat(vDotsView, DotsView.DOTS_PROGRESS, 0, 1f);
            dotsAnimator.setDuration(900);
            dotsAnimator.setStartDelay(50);
            dotsAnimator.setInterpolator(ACCELERATE_DECELERATE_INTERPOLATOR);

            animatorSet.playTogether(
                    outerCircleAnimator,
                    innerCircleAnimator,
                    starScaleYAnimator,
                    starScaleXAnimator,
                    dotsAnimator
            );
            animatorSet.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationCancel(Animator animation) {
                    vCircle.setInnerCircleRadiusProgress(0);
                    vCircle.setOuterCircleRadiusProgress(0);
                    vDotsView.setCurrentProgress(0);
                    ivStar.setScaleX(1);
                    ivStar.setScaleY(1);
                }
            });


            animatorSet.start();
        }

        MainListActivity.performFiltering();
        MainListActivity.showToastAfterFilter();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                ivStar.animate().scaleX(0.7f).scaleY(0.7f).setDuration(150).setInterpolator(DECCELERATE_INTERPOLATOR);
                setPressed(true);
                break;

            case MotionEvent.ACTION_MOVE:
                float x = event.getX();
                float y = event.getY();
                boolean isInside = (x > 0 && x < getWidth() && y > 0 && y < getHeight());
                if (isPressed() != isInside) {
                    setPressed(isInside);
                }
                break;

            case MotionEvent.ACTION_UP:
                ivStar.animate().scaleX(1).scaleY(1).setInterpolator(DECCELERATE_INTERPOLATOR);
                if (isPressed()) {
                    performClick();
                    setPressed(false);
                }
                break;
        }
        return true;
    }

    public void setClicked(boolean clicked){
        if (clicked) {
            ivStar.setImageResource(R.drawable.favorite_red);
            isChecked = true;
        }
    }


}
