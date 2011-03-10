package com.myapps.utils;

import android.content.Context;
import android.graphics.*;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class drawRectOnTouchView extends View {
    public drawRectOnTouchView(Context context, AttributeSet attrs) {
	super(context, attrs);
	mPaint = new Paint(Paint.DITHER_FLAG);
	mPaint.setColor(Color.GRAY);
    }

    public drawRectOnTouchView(Context context) {
	super(context);
	mPaint = new Paint(Paint.DITHER_FLAG);
	mPaint.setColor(Color.GRAY);
    }

    private PointF start = new PointF(0, 0);
    private PointF end = new PointF(0, 0);

    private Paint mPaint;

    @Override
    protected void onDraw(Canvas canvas) {
	canvas.drawColor(Color.TRANSPARENT);
	canvas.drawLine(start.x, start.y, end.x, end.y, mPaint);
	canvas.drawLine(end.x, start.y, start.x, end.y, mPaint);
	canvas.drawLine(start.x, end.y, end.x, end.y, mPaint);
	canvas.drawLine(end.x, start.y, end.x, end.y, mPaint);
	canvas.drawLine(start.x, start.y, start.x, end.y, mPaint);
	canvas.drawLine(start.x, start.y, end.x, start.y, mPaint);

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
	float x = event.getX();
	float y = event.getY();

	switch (event.getAction()) {
	case MotionEvent.ACTION_DOWN:
	    start.set(x, y);
	    break;
	case MotionEvent.ACTION_UP:
	    end.set(x, y);
	    invalidate();
	    break;
	}
	return true;
    }
}
