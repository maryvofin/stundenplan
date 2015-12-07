package de.maryvofin.stundenplan.app.utils;

import android.support.design.widget.FloatingActionButton;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;

import de.maryvofin.stundenplan.app.R;

public class FABAnimator extends RecyclerScrollListener{

    FloatingActionButton fab;
    int fabMargin;
    boolean large = false;

    public FABAnimator(FloatingActionButton fab) {
        this.fab = fab;
        fabMargin = fab.getContext().getResources().getDimensionPixelSize(R.dimen.fab_margin);
    }

    @Override
    public void show() {
        fab.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2)).start();
    }

    @Override
    public void hide() {
        fab.animate().translationY(fab.getHeight()+fabMargin).setInterpolator(new AccelerateInterpolator(2)).start();
    }

    public void grow() {
        if(large) return;
        Animation animation = AnimationUtils.loadAnimation(fab.getContext(), R.anim.simple_grow);
        fab.startAnimation(animation);
        large = true;
    }

    public void shrink() {
        if(!large) return;
        Animation animation = AnimationUtils.loadAnimation(fab.getContext(), R.anim.simple_shrink);
        fab.startAnimation(animation);
        large = false;
    }
}
