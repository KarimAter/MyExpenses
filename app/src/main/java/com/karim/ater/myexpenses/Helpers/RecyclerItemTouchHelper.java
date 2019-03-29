package com.karim.ater.myexpenses.Helpers;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.ItemTouchHelper;
import android.view.View;

import com.karim.ater.myexpenses.HoldersAdapters.FeedsAdapter;


public class RecyclerItemTouchHelper extends ItemTouchHelper.SimpleCallback {
    private RecyclerItemTouchHelperListener listener;
    private Drawable icon;

    public RecyclerItemTouchHelper(int dragDirs, int swipeDirs, RecyclerItemTouchHelperListener listener) {
        super(dragDirs, swipeDirs);
        this.listener = listener;
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        return true;
    }

    @Override
    public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
        if (viewHolder != null) {
            final View foregroundView = ((FeedsAdapter.FeedsViewHolder) viewHolder).itemView;

            getDefaultUIUtil().onSelected(foregroundView);
        }
    }

    @Override
    public void onChildDrawOver(Canvas c, RecyclerView recyclerView,
                                RecyclerView.ViewHolder viewHolder, float dX, float dY,
                                int actionState, boolean isCurrentlyActive) {
        final View foregroundView = ((FeedsAdapter.FeedsViewHolder) viewHolder).itemView;
        getDefaultUIUtil().onDrawOver(c, recyclerView, foregroundView, dX, dY,
                actionState, isCurrentlyActive);
    }

    @Override
    public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        final View foregroundView = ((FeedsAdapter.FeedsViewHolder) viewHolder).itemView;
        getDefaultUIUtil().clearView(foregroundView);
    }

    @Override
    public void onChildDraw(Canvas c, RecyclerView recyclerView,
                            RecyclerView.ViewHolder viewHolder, float dX, float dY,
                            int actionState, boolean isCurrentlyActive) {
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        int backgroundCornerOffset = 0;
        final View foregroundView = ((FeedsAdapter.FeedsViewHolder) viewHolder).itemView;
        ColorDrawable background = new ColorDrawable(Color.RED);
        Drawable icon = ContextCompat.getDrawable(((FeedsAdapter) recyclerView.getAdapter()).activity.getBaseContext(), android.R.drawable.ic_menu_delete);


        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
            if (dX < 0) {

                int iconMargin = (foregroundView.getHeight() - icon.getIntrinsicHeight()) / 2;
                int iconTop = foregroundView.getTop() + (foregroundView.getHeight() - icon.getIntrinsicHeight()) / 2;
                int iconBottom = iconTop + icon.getIntrinsicHeight();

                background.setBounds(foregroundView.getRight() + ((int) dX) - backgroundCornerOffset,
                        foregroundView.getTop(), foregroundView.getRight(), foregroundView.getBottom());

                int iconLeft = foregroundView.getRight() - iconMargin - icon.getIntrinsicWidth();
                int iconRight = foregroundView.getRight() - iconMargin;
                icon.setBounds(iconLeft, iconTop, iconRight, iconBottom);
                background.draw(c);
                icon.draw(c);
            }
//        } else { // view is unSwiped
//            background.setBounds(0, 0, 0, 0);
//            icon.setBounds(0,0,0,0);
//        }

            else {

                icon.setBounds(0, 0, 0, 0);
                icon.draw(c);
            }
        }
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        listener.onSwiped(viewHolder, direction, viewHolder.getAdapterPosition());
    }

    @Override
    public int convertToAbsoluteDirection(int flags, int layoutDirection) {
        return super.convertToAbsoluteDirection(flags, layoutDirection);
    }

    public interface RecyclerItemTouchHelperListener {
        void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position);
    }
}
