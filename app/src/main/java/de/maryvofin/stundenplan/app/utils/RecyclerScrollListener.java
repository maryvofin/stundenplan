package de.maryvofin.stundenplan.app.utils;

import android.support.v7.widget.RecyclerView;

public abstract class RecyclerScrollListener extends RecyclerView.OnScrollListener {

    int scrollDist = 0;
    boolean isVisible = true;
    final static float MINIMUM = 25f;

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);

        if (isVisible && scrollDist > MINIMUM) {
            hide();
            scrollDist = 0;
            isVisible = false;
        }
        else if (!isVisible && scrollDist < -MINIMUM) {
            show();
            scrollDist = 0;
            isVisible = true;
        }
        if ((isVisible && dy > 0) || (!isVisible && dy < 0)) {
            scrollDist += dy;
        }
    }

    public abstract void show();
    public abstract void hide();
}
