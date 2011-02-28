package com.myapps;

import android.graphics.PointF;
import android.util.FloatMath;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

public class TouchListener implements OnTouchListener {
	private static final String TAG = "TouchLog";
	static final int NONE = 0;
	static final int DRAG = 1;
	static final int ZOOM = 2;
	public int mode = NONE;
	
	private PointF start = new PointF();
	private PointF current = new PointF();
	private float panStep = 5;
	private float tiltStep = 5;
	private float moveX = 0;
	private float moveY = 0;
	private float currentDist = 0;
	
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		switch (event.getAction() & MotionEvent.ACTION_MASK) {
		case MotionEvent.ACTION_DOWN:
			start.set(event.getX(), event.getY());
			mode = DRAG;
			Log.i(TAG, "mode=DRAG");
			break;
		
		case MotionEvent.ACTION_POINTER_DOWN:
			currentDist = calculateDistance(new PointF(event.getX(0), event.getY(0)),
					new PointF(event.getX(1), event.getY(1)));
			mode = ZOOM;
			Log.i(TAG, "mode=ZOOM");
			
		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_POINTER_UP:
			mode = NONE;
			break;

		case MotionEvent.ACTION_MOVE:
			if (mode == DRAG) {
				current.set(event.getX(), event.getY());
	            moveX = calculateMoveX(start, current);
	            moveY = calculateMoveY(start, current);
	            // => move(moveX, moveY);
			} else if (mode == ZOOM) {
				float startDist = currentDist;
	            currentDist = calculateDistance(new PointF(event.getX(0), event.getY(0)),
	            		new PointF(event.getX(1), event.getY(1)));
	            Log.i(TAG, "currentDist=" + currentDist);
	            if (currentDist > 1) {
	               float scale = currentDist / startDist;
	            }
	            // => zoom(scale);
			}
			break;
		}
		
		return true;
	}
	
	private float calculateMoveX(PointF a, PointF b) {
		return Math.abs(b.x - a.x);
	}
	
	private float calculateMoveY(PointF a, PointF b) {
		return Math.abs(b.y - a.y);
	}
	
	private float calculateDistance(PointF a, PointF b) {
		float x = b.x - a.x;
		float y = b.y - a.y;
		return FloatMath.sqrt(x * x + y * y);
	}
}
