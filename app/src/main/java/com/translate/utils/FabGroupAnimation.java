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
import android.view.animation.AccelerateDecelerateInterpolator;
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
    private static final int DURATION_LONG = 500;    // 动画过渡时间
    private static final int DURATION_SHORT = 250;    // 动画过渡时间
    private boolean isEnpand = false;           // FloatActionButton是否展开
    private boolean isFirstClick = true;        // 第一次点击
    private boolean isDesc = false;             // Fab文字描述
    private TextView masking;                   // 蒙版
    private int width, height;                  // 屏幕宽高
    private int fabCount;                       // Fab个数       
    private int size;                           // fab 的大小
    private float radius;                       // 屏幕对角线
    private List<View> fabs;                    // Fab组
    private List<TextView> textViews;           // Fab组
    private int[] ids;                          // Fab组item的ID 
    private String[] descs;                     // Fab文字描述 
    private int[] textIDs;                      // Fab组文字的ID
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

    public FabGroupAnimation setView(int[] ids, String[] descs, int[] textIDs) {
        this.ids = ids;
        this.descs = descs;
        this.textIDs = textIDs;
        fabCount = ids.length;
        isDesc = true;
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
        size = show.getMeasuredHeight();                                      // 获取Fab的宽度
        isEnpand = true;
        setMaskingExpand(0, radius);                                            // 展开动画
        setMaskingAlpha(0.25f, 0.75f, new AnimatorListener() {        // 动画开始时设置可见
            @Override
            public void onAnimationStart(Animator animation) {
                setVisible();
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                setFabRotation(0f, 45f);                         // 按钮旋转动画
                for (int i = 0; i < fabCount; i++) {                                  // Fab组内的item展开
                    setGroupExpand(fabs.get(i), 0, 0, 0, 1.3f * (i + 1));
                    setGroupExpand(textViews.get(i), 0, 1.3f, 0, 1.3f * (i + 1));
                }
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
        setMaskingExpand(radius, 0);                                            // 收缩动画
        setMaskingAlpha(0.75f, 0.5f, new AnimatorListener() {        // 动画结束时设置隐藏
            @Override
            public void onAnimationStart(Animator animation) {
                setFabRotation(45f, 0f);                         // 按钮旋转动画
                for (int i = 0; i < fabCount; i++) {                                  // Fab组内的item收缩
                    setGroupShrink(fabs.get(i), 0, 0, 1.3f * (i + 1), 0);
                    setGroupShrink(textViews.get(i), 1.3f, 0, 1.3f * (i + 1), 0);
                }
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                setVisible();
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
        if (isDesc) {
            textViews = new ArrayList<>();              // 初始化文字
            for (int i = 0; i < fabCount; i++) {
                textViews.add((TextView) rootView.findViewById(textIDs[i]));
                textViews.get(i).setOnClickListener(this);
                textViews.get(i).setText(descs[i]);
                textViews.get(i).setVisibility(View.GONE);
            }
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
     * 设置Fab组Item可见
     */
    protected void setFabVisible() {
        for (int i = 0; i < fabCount; i++) {
            fabs.get(i).setVisibility(View.VISIBLE);                // 设置Fab按钮隐藏
            if (isDesc) {
                textViews.get(i).setVisibility(View.VISIBLE);       // 设置文字隐藏
            }
        }
        isFirstClick = false;
    }

    /**
     * Fab组展开动画
     *
     * @param target
     * @param startTransX
     * @param endTransX
     * @param startTransY
     * @param endTransY
     */
    protected void setGroupExpand(View target, float startTransX, float endTransX, float startTransY, float endTransY) {
        ObjectAnimator animatorX = ObjectAnimator.ofFloat(target,
                "ScaleX", 0f, 1.0f);                        // X方向放大
        ObjectAnimator animatorY = ObjectAnimator.ofFloat(target,                 // Y方向放大
                "ScaleY", 0f, 1.0f);
        ObjectAnimator animatorTransX = ObjectAnimator.ofFloat(target,
                "TranslationX", -size * startTransX, -size * endTransX);  // X方向向左平移
        ObjectAnimator animatorTransY = ObjectAnimator.ofFloat(target,
                "TranslationY", -size * startTransY, -size * endTransY);  // Y方向向上平移
        ObjectAnimator animatorAlpha = ObjectAnimator.ofFloat(target,
                "Alpha", 0.5f, 1.0f);                       // 淡入动画
        AnimatorSet animatorSet = new AnimatorSet();                              // 动画集
        animatorSet.playTogether(animatorX, animatorY, animatorTransX, animatorTransY, animatorAlpha);           // 动画集一起播放
        animatorSet.setInterpolator(new OvershootInterpolator());      // 回弹插值器
        animatorSet.setDuration(DURATION_SHORT);                                        // 设置动画播放时间
        animatorSet.start();
    }

    /**
     * Fab组收缩动画
     *
     * @param target
     * @param startTransX
     * @param endTransX
     * @param startTransY
     * @param endTransY
     */
    protected void setGroupShrink(View target, float startTransX, float endTransX, float startTransY, float endTransY) {
        ObjectAnimator animatorX = ObjectAnimator.ofFloat(target,
                "ScaleX", 1.0f, 0f);                        // X方向缩小
        ObjectAnimator animatorY = ObjectAnimator.ofFloat(target,
                "ScaleY", 1.0f, 0f);                        // Y方向缩小
        ObjectAnimator animatorTransX = ObjectAnimator.ofFloat(target,
                "TranslationX", -size * startTransX, -size * endTransX);  // X方向向右平移
        ObjectAnimator animatorTransY = ObjectAnimator.ofFloat(target,
                "TranslationY", -size * startTransY, -size * endTransY);  // Y方向向下平移
        ObjectAnimator animatorAlpha = ObjectAnimator.ofFloat(target,
                "Alpha", 1.0f, 0f);                         // 淡出动画
        AnimatorSet animatorSet = new AnimatorSet();                              // 动画集
        animatorSet.playTogether(animatorX, animatorY, animatorTransX, animatorTransY, animatorAlpha);           // 动画集一起播放
        animatorSet.setInterpolator(new AnticipateInterpolator());      // 设置插值器
        animatorSet.setDuration(DURATION_SHORT);                                        // 设置动画播放时间
        animatorSet.start();
    }

    /**
     * Fab旋转动画
     *
     * @param staer
     * @param end
     */
    protected void setFabRotation(float staer, float end) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(
                show, "Rotation", staer, end);
        animator.setDuration(DURATION_SHORT);
        animator.setInterpolator(new OvershootInterpolator());
        animator.start();
    }

    /**
     * 蒙版透明度动画
     *
     * @param start
     * @param end
     */
    protected void setMaskingAlpha(float start, float end, AnimatorListener listener) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(masking, "Alpha", start, end);
        animator.setDuration(DURATION_SHORT);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.addListener(listener);
        animator.start();
    }

    /**
     * 蒙版圆形展开动画
     *
     * @param start
     * @param end
     */
    protected void setMaskingExpand(float start, float end) {
        Animator animator = ViewAnimationUtils.createCircularReveal(masking, width, height, start, end);
        animator.setInterpolator(new AccelerateInterpolator());           // 加速插值器
        animator.setDuration(DURATION_SHORT);
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
     * Fab组item点击事件
     *
     * @param view
     */
    @Override
    public void onClick(View view) {
        shrinkMenu();
        for (int i = 0; i < fabCount; i++) {
            if (view.getId() == ids[i] || view.getId() == textIDs[i]) {
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