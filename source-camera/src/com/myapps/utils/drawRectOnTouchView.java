package com.myapps.utils;

import com.myapps.R;

import android.content.Context;
import android.graphics.*;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class drawRectOnTouchView extends View {
    public drawRectOnTouchView(Context context, AttributeSet attrs) {
	super(context, attrs);
	mPaint = new Paint(Paint.DITHER_FLAG);
	mPaint.setColor(context.getResources().getColor(R.color.greenyellow));
	mPaint.setAlpha(175);
	mPaint.setStyle(Paint.Style.STROKE);
	mPaint.setStrokeJoin(Paint.Join.ROUND);
	mPaint.setStrokeCap(Paint.Cap.ROUND);
	mPaint.setStrokeWidth(4);
    }

    public drawRectOnTouchView(Context context) {
	super(context);
	mPaint = new Paint(Paint.DITHER_FLAG);
	mPaint.setColor(Color.GRAY);
    }

    private PointF start = new PointF(0, 0);
    private PointF end = new PointF(0, 0);
    private boolean isDraw = false;
    private Paint mPaint;

    @Override
    protected void onDraw(Canvas canvas) {
	canvas.drawColor(Color.TRANSPARENT);
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
	    isDraw = false;
	    break;
	case MotionEvent.ACTION_MOVE:
	    end.set(x, y);
	    isDraw = false;
	    invalidate();
	    break;
	case MotionEvent.ACTION_UP:
	    end.set(x, y);
	    isDraw = true;
	    invalidate();
	    break;
	}
	return true;
    }

    public PointF getStart() {
	return start;
    }

    public PointF getEnd() {
	return end;
    }

    public boolean isDraw() {
	if(start.equals(end.x, end.y))
	    return false;
	return isDraw;
    }

    public String toString() {
	return "Point A = {" + start.x + "," + start.y + "}" + " Point B = {"
		+ end.x + "," + end.y + "}";

    }

}
