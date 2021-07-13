package edu.uchicago.fullstack.androidretro.AA_view.views;

import android.content.Context;
import android.util.AttributeSet;

import androidx.recyclerview.widget.LinearLayoutManager;

public class CustomLinearLayoutManager extends LinearLayoutManager{
    public CustomLinearLayoutManager(Context context) {
        super(context);
    }

    public CustomLinearLayoutManager(Context context, int orientation, boolean reverseLayout) {
        super(context, orientation, reverseLayout);
    }

    public CustomLinearLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    //override this method to prevent predictive-item-animations crashing on paging - which is a known bug
    //https://stackoverflow.com/questions/30220771/recyclerview-inconsistency-detected-invalid-item-position
    @Override
    public boolean supportsPredictiveItemAnimations() {
        return false;
    }
}
