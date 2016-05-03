package com.github.rtoshiro.poview;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Rect;
import android.os.Build;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.RelativeLayout;

/**
 * Created by Tox on 6/17/15.
 */
public class PopoverView extends RelativeLayout implements View.OnTouchListener {

    public interface OnDismissListener {
        void onDismiss(PopoverView v);
    }

    public enum PopoverViewPosition {
        Top,
        Left,
        Bottom,
        Right,
        Any
    }

    private static final String TAG = "PopoverView";

    protected Context context;
    protected View contentView;
    protected OnDismissListener onDismissListener;
    protected RelativeLayout containerLayout;

    public PopoverView(Context context) {
        super(context);
        configure(context);
    }

    public PopoverView(Context context, AttributeSet attrs) {
        super(context, attrs);
        configure(context);
    }

    public PopoverView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        configure(context);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public PopoverView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        configure(context);
    }

    protected void configure(Context context) {
        this.context = context;
        this.setOnTouchListener(this);
    }

    /**
     * Calculates the available area for the reference Rect.
     * The Rect is related to the parent View. So, take care of the Rect passed as parameter
     * as it must be related to the parent's view.
     *
     * @param referenceRect The reference Rect in parent's View coordinate
     * @param position      The PopoverViewPosition that popover should be calculated to open
     * @return The Rect of available area
     */
    protected Rect getAvailableArea(Rect referenceRect, PopoverViewPosition position) {
        ViewGroup parentView = (ViewGroup) this.getParent();

        Rect rect = new Rect(parentView.getLeft(), parentView.getTop(), parentView.getRight(), parentView.getBottom());
        Rect rectLeft, rectTop, rectRight, rectBottom;

        // Top
        rectTop = new Rect(rect.left, rect.top, rect.right, referenceRect.top);

        // Left
        rectLeft = new Rect(rect.left, rect.top, referenceRect.left, rect.bottom);

        // Bottom
        rectBottom = new Rect(rect.left, referenceRect.bottom, rect.right, rect.bottom);

        // Right
        rectRight = new Rect(referenceRect.right, rect.top, rect.right, rect.bottom);

        if (position == PopoverViewPosition.Any)
            position = getAvailablePosition(referenceRect);

        if (position != null) {
            switch (position) {
                case Top:
                    return rectTop;
                case Left:
                    return rectLeft;
                case Bottom:
                    return rectBottom;
                case Right:
                    return rectRight;
            }
        }

        return null;
    }

    protected PopoverViewPosition getAvailablePosition(Rect referenceRect) {
        ViewGroup parentView = (ViewGroup) this.getParent();

        Rect rect = new Rect(parentView.getLeft(), parentView.getTop(), parentView.getRight(), parentView.getBottom());
        Rect rectLeft, rectTop, rectRight, rectBottom;

        // Top
        rectTop = new Rect(rect.left, rect.top, rect.right, referenceRect.top);

        // Left
        rectLeft = new Rect(rect.left, rect.top, referenceRect.left, rect.bottom);

        // Bottom
        rectBottom = new Rect(rect.left, referenceRect.bottom, rect.right, rect.bottom);

        // Right
        rectRight = new Rect(referenceRect.right, rect.top, rect.right, rect.bottom);

        // Calculates all areas
        long top = (rectTop.right - rectTop.left) * (rectTop.bottom - rectTop.top);
        long left = (rectLeft.right - rectLeft.left) * (rectLeft.bottom - rectLeft.top);
        long right = (rectRight.right - rectRight.left) * (rectRight.bottom - rectRight.top);
        long bottom = (rectBottom.right - rectBottom.left) * (rectBottom.bottom - rectBottom.top);

        long maxArea = Math.max(top, left);
        maxArea = Math.max(maxArea, right);
        maxArea = Math.max(maxArea, bottom);

        if (maxArea == top)
            return PopoverViewPosition.Top;
        else if (maxArea == left)
            return PopoverViewPosition.Left;
        else if (maxArea == right)
            return PopoverViewPosition.Right;
        else if (maxArea == bottom)
            return PopoverViewPosition.Bottom;

        return null;
    }

    public OnDismissListener getOnDismissListener() {
        return onDismissListener;
    }

    public void setOnDismissListener(OnDismissListener onDismissListener) {
        this.onDismissListener = onDismissListener;
    }

    public View getContentView() {
        return contentView;
    }

    public void setContentView(View contentView) {
        if (contentView != null) {
            if (this.contentView != null) {
                ViewGroup parentView = (ViewGroup) this.contentView.getParent();
                if (parentView == this.containerLayout)
                    parentView.removeView(this.contentView);
            }

            if (this.containerLayout == null) {
                this.containerLayout = new RelativeLayout(context);
                addView(this.containerLayout);
            }

            this.contentView = contentView;
            this.containerLayout.addView(this.contentView);
        }
    }

    public void setContentView(int resourceId) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(resourceId, null);
        if (view != null)
            setContentView(view);
    }

    public void show(final View referenceView, final PopoverViewPosition position) {
        if (contentView == null) return;

        ViewGroup rootView = (ViewGroup) referenceView.getRootView();
        if (rootView != null) {
            contentView.setVisibility(View.INVISIBLE);
            ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            rootView.addView(this, params);

            this.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    getViewTreeObserver().removeGlobalOnLayoutListener(this);

                    int referenceLocation[] = new int[2];
                    referenceView.getLocationOnScreen(referenceLocation);

                    Rect referenceRect = new Rect(referenceLocation[0], referenceLocation[1], referenceLocation[0] + referenceView.getWidth(), referenceLocation[1] + referenceView.getHeight());

                    PopoverViewPosition newPosition = position;
                    if (newPosition == PopoverViewPosition.Any)
                        newPosition = getAvailablePosition(referenceRect);

                    Rect availableRect = getAvailableArea(referenceRect, newPosition);
                    if (availableRect != null) {
                        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(availableRect.width(), availableRect.height());
                        layoutParams.topMargin = availableRect.top;
                        layoutParams.leftMargin = availableRect.left;
                        containerLayout.setLayoutParams(layoutParams);

                        LayoutParams contentViewParams = (LayoutParams) contentView.getLayoutParams();
                        if (newPosition == PopoverViewPosition.Left || newPosition == PopoverViewPosition.Right) {
                            int marginLeft = availableRect.width() - contentView.getWidth();
                            if (marginLeft < 0 || newPosition == PopoverViewPosition.Right)
                                marginLeft = 0;

                            int marginTop = referenceRect.centerY() - (contentView.getHeight() / 2);

                            // Top is hidden
                            if (marginTop < 0)
                                marginTop = 0;
                            else if (marginTop + contentView.getHeight() > availableRect.bottom)
                                marginTop = availableRect.bottom - contentView.getHeight();

                            contentViewParams.topMargin = marginTop;
                            contentViewParams.leftMargin = marginLeft;
                        } else {
                            int marginTop = availableRect.height() - contentView.getHeight();
                            if (marginTop < 0 || newPosition == PopoverViewPosition.Bottom)
                                marginTop = 0;

                            int marginLeft = referenceRect.centerX() - (contentView.getWidth() / 2);

                            // Top is hidden
                            if (marginLeft < 0)
                                marginLeft = 0;
                            else if (marginLeft + contentView.getWidth() > availableRect.right)
                                marginLeft = availableRect.right - contentView.getWidth();

                            contentViewParams.topMargin = marginTop;
                            contentViewParams.leftMargin = marginLeft;
                        }
                        contentView.setLayoutParams(contentViewParams);
                        contentView.setVisibility(View.INVISIBLE);

                        Handler mainHandler = new Handler(context.getMainLooper());
                        mainHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                contentView.setVisibility(View.VISIBLE);
                            }
                        });
                    }
                }
            });
        }
    }

    public void dismiss() {
        ViewGroup parentView = (ViewGroup) getParent();
        if (parentView != null)
            parentView.removeView(this);

        if (onDismissListener != null)
            onDismissListener.onDismiss(this);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            int contentViewLocation[] = new int[2];
            contentView.getLocationOnScreen(contentViewLocation);

            Rect rect = new Rect(contentViewLocation[0], contentViewLocation[1], contentViewLocation[0] + contentView.getWidth(), contentViewLocation[1] + contentView.getHeight());
            if (!rect.contains((int) event.getX(), (int) event.getY()))
                dismiss();
        }

        return false;
    }
}
