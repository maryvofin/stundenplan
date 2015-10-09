package android.support.v4.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by mark on 02.10.2015.
 */
public class ClickablePagerTabStrip extends PagerTabStripV22 {
    public ClickablePagerTabStrip(Context context, AttributeSet attrs) {
        super(context, attrs);

        mCurrText.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                performClick();
            }
        });
    }

    public ClickablePagerTabStrip(Context context) {
        super(context);
    }
}
