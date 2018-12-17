package com.translate.utils;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.support.design.widget.FloatingActionButton;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AnticipateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.translate.R;

import java.util.ArrayList;
import java.util.List;

public class FabGroupAnimation extends RelativeLayout implements View.OnClickListener {

    private FloatingActionButton show;
    private static final int DURATION_SHORT = 200;    // 动画过渡时间
    private static final int TIME = 50;               // 动画过渡时间
    private boolean isEnpand = false;           // FloatActionButton是否展开
    private boolean isFirstClick = true;        // 第一次点击
    private TextView masking;                   // 蒙版
    private int width, height;                  // 屏幕宽高
    private int fabCount;                       // Fab个数       
    private int size;                           // fab 的大小
    private float radius;                       // 屏幕对角线
    private List<View> fabs;                    // Fab组
    private int[] ids;                          // Fab组item的ID
    private int layoutID;                       // 布局
    private OnFabItemClickListener onFabItemClickListener;

    /**
     * 初始化
     *
     * @param activity
     * @param show
     */
    public FabGroupAnimation init(Activity activity, int layoutID, FloatingActionButton show) {
        width = activity.getWindowManager().getDefaultDisplay().getWidth();
        height = activity.getWindowManager().getDefaultDisplay().getHeight();
        radius = (float) Math.sqrt(Math.pow(width, 2) + Math.pow(height, 2));
        this.layoutID = layoutID;
        this.show = show;
        return this;
    }

    public FabGroupAnimation setView(int[] ids) {
        this.ids = ids;
        fabCount = ids.length;
        initView();
        return this;
    }

    /**
     * 判断是否展开
     *
     * @return
     */
    public boolean isEnpand() {
        return isEnpand;
    }

    public FabGroupAnimation setOnFabItemClickListener(OnFabItemClickListener onFabItemClickListener) {
        this.onFabItemClickListener = onFabItemClickListener;
        return this;
    }

    /**
     * 展开菜单
     */
    public FabGroupAnimation expandMenu() {
        size = show.getMeasuredHeight();                                        // 获取Fab的宽度
        isEnpand = true;
        setMaskingExpand(0, radius, new AnimatorListener() {       // 动画开始时设置可见，展开动画
            @Override
            public void onAnimationStart(Animator animation) {
                setVisible();
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                setFabRotation(0f, 45f);                         // 按钮旋转动画
                setGroupExpand(fabs.get(0), 0, 1.3f * 1, TIME * 3, null);
                setGroupExpand(fabs.get(1), 0, 1.3f * 2, TIME * 4, null);
                if (isFirstClick) {
                    setFabVisible();
                }
            }
        });
        return this;
    }

    /**
     * 收缩菜单
     */
    public FabGroupAnimation shrinkMenu() {
        isEnpand = false;
        setFabRotation(45f, 0f);                         // 按钮旋转动画
        setGroupShrink(fabs.get(0), 1.3f * 1, 0, TIME * 3, null);
        setGroupShrink(fabs.get(1), 1.3f * 2, 0, TIME * 4, new AnimatorListener() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                setMaskingExpand(radius, 0, new AnimatorListener() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        setVisible();
                    }
                });  // 动画结束时设置隐藏，收缩动画
            }
        });
        return this;
    }

    public FabGroupAnimation(Context context) {
        super(context);
    }

    public FabGroupAnimation(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FabGroupAnimation(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private void initView() {
        View rootView = inflate(getContext(), layoutID, this);
        fabs = new ArrayList<>();
        for (int i = 0; i < fabCount; i++) {            // 初始化Fab按钮
            fabs.add(rootView.findViewById(ids[i]));
            fabs.get(i).setOnClickListener(this);
            fabs.get(i).setVisibility(View.GONE);
        }
        masking = rootView.findViewById(R.id.masking);
        setMaskingClick();                              // 设置蒙版点击
    }

    /**
     * 点击蒙版时隐藏菜单
     */
    protected void setMaskingClick() {
        masking.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isEnpand) {
                    shrinkMenu();
                }
            }
        });
    }

    /**
     * Fab组展开动画
     *
     * @param target
     * @param startTransY
     * @param endTransY
     */
    protected void setGroupExpand(View target, float startTransY, float endTransY, long time, AnimatorListener listener) {
        ObjectAnimator animatorY = ObjectAnimator.ofFloat(target, "ScaleY", 0f, 1.0f);  // Y方向放大
        ObjectAnimator animatorTransY = ObjectAnimator.ofFloat(target, "TranslationY", -size * startTransY, -size * endTransY);  // Y方向向上平移
        ObjectAnimator animatorAlpha = ObjectAnimator.ofFloat(target, "Alpha", 0.5f, 1.0f);                       // 淡入动画
        AnimatorSet animatorSet = new AnimatorSet();                              // 动画集
        animatorSet.playTogether(animatorY, animatorTransY, animatorAlpha);           // 动画集一起播放
        animatorSet.setInterpolator(new OvershootInterpolator());      // 回弹插值器
        animatorSet.setDuration(time);                                        // 设置动画播放时间
        if (listener != null) {
            animatorSet.addListener(listener);
        }
        animatorSet.start();
    }

    /**
     * Fab组收缩动画
     *
     * @param target
     * @param startTransY
     * @param endTransY
     */
    protected void setGroupShrink(View target, float startTransY, float endTransY, long time, AnimatorListener listener) {
        ObjectAnimator animatorY = ObjectAnimator.ofFloat(target, "ScaleY", 1.0f, 0f);                        // Y方向缩小
        ObjectAnimator animatorTransY = ObjectAnimator.ofFloat(target, "TranslationY", -size * startTransY, -size * endTransY);  // Y方向向下平移
        ObjectAnimator animatorAlpha = ObjectAnimator.ofFloat(target, "Alpha", 1.0f, 0f);                         // 淡出动画
        AnimatorSet animatorSet = new AnimatorSet();                              // 动画集
        animatorSet.playTogether(animatorY, animatorTransY, animatorAlpha);           // 动画集一起播放
        animatorSet.setInterpolator(new AnticipateInterpolator());      // 设置插值器
        animatorSet.setDuration(time);                                        // 设置动画播放时间
        if (listener != null) {
            animatorSet.addListener(listener);
        }
        animatorSet.start();
    }

    /**
     * Fab旋转动画
     *
     * @param staer
     * @param end
     */
    protected void setFabRotation(float staer, float end) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(show, "Rotation", staer, end);
        animator.setDuration(DURATION_SHORT);
        animator.setInterpolator(new OvershootInterpolator());
        animator.start();
    }

    /**
     * 蒙版圆形展开动画
     *
     * @param start
     * @param end
     */
    protected void setMaskingExpand(float start, float end, AnimatorListener listener) {
        Animator animator = ViewAnimationUtils.createCircularReveal(masking, width, height, start, end);
        animator.setInterpolator(new AccelerateInterpolator());           // 加速插值器
        animator.setDuration(DURATION_SHORT);
        animator.addListener(listener);
        animator.start();
    }

    /**
     * 设置可见
     */
    private void setVisible() {
        masking.setVisibility(isEnpand ? View.VISIBLE : View.GONE);
        this.setVisibility(isEnpand ? View.VISIBLE : View.GONE);
    }

    /**
     * 设置Fab组Item可见
     */
    protected void setFabVisible() {
        for (int i = 0; i < fabCount; i++) {
            fabs.get(i).setVisibility(View.VISIBLE);                // 设置Fab按钮隐藏
        }
        isFirstClick = false;
    }

    /**
     * Fab组item点击事件
     *
     * @param view
     */
    @Override
    public void onClick(View view) {
        shrinkMenu();
        for (int i = 0; i < fabCount; i++) {
            if (view.getId() == ids[i]) {
                onFabItemClickListener.onFabItemClick((FloatingActionButton) fabs.get(i));
                break;
            }
        }
    }

    /**
     * 动画监听
     */
    public static abstract class AnimatorListener implements Animator.AnimatorListener {
        @Override
        public void onAnimationStart(Animator animation) {

        }

        @Override
        public void onAnimationEnd(Animator animation) {

        }

        @Override
        public void onAnimationCancel(Animator animation) {

        }

        @Override
        public void onAnimationRepeat(Animator animation) {

        }
    }

    /**
     * 点击事件接口
     */
    public interface OnFabItemClickListener {
        void onFabItemClick(FloatingActionButton fab);
    }
}