
package com.refactech.driibo.ui.adapter;

import com.haarman.listviewanimations.swinginadapters.AnimationAdapter;
import com.refactech.driibo.AppData;

import android.R;
import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class CardsAnimationAdapter extends AnimationAdapter {

    private float mTranslationY = 150;

    private float mRotationX = 8;

    private long mDuration;

    public CardsAnimationAdapter(BaseAdapter baseAdapter) {
        super(baseAdapter);
        mDuration = AppData.getContext().getResources().getInteger(R.integer.config_mediumAnimTime);
    }

    @Override
    protected long getAnimationDelayMillis() {
        return 30;
    }

    @Override
    protected long getAnimationDurationMillis() {
        return mDuration;
    }

    @Override
    public Animator[] getAnimators(ViewGroup parent, View view) {
        return new Animator[] {
                ObjectAnimator.ofFloat(view, "translationY", mTranslationY, 0),
                ObjectAnimator.ofFloat(view, "rotationX", mRotationX, 0)
        };
    }

    @Override
    protected void prepareAnimation(View view) {
        view.setTranslationY(mTranslationY);
        view.setRotationX(mRotationX);
    }
}
