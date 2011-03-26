package com.myapps;

import android.graphics.PointF;
import android.util.FloatMath;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

/**
 * 
 * Implements a listener on touch events for PTZ camera control 
 *
 */
public class TouchListener implements OnTouchListener {
    private static final String TAG = "AppLog";
    static final int NONE = 0;
    static final int DRAG = 1;
    static final int ZOOM = 2;
    public int mode = NONE;

    private CameraControl camC;
    private float width, height;
    private PointF current = new PointF(0, 0);
    private float startDist = 0, currentDist = 0;
    private float zoomStep = 1500;
    private int sens;

    public TouchListener(CameraControl pCamC) {
	camC = pCamC;
	this.sens = Home.preferences.getInt("seekBarPreference", 10);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
	width = v.getWidth();
	height = v.getHeight();
	switch (event.getAction() & MotionEvent.ACTION_MASK) {
	case MotionEvent.ACTION_DOWN:
	    current.set(event.getX(), event.getY());
	    mode = DRAG;
	    Log.i(TAG, "mode=DRAG");
	    break;

	case MotionEvent.ACTION_POINTER_DOWN:
	    currentDist = calculateDistance(
		    new PointF(event.getX(0), event.getY(0)),
		    new PointF(event.getX(1), event.getY(1)));
	    mode = ZOOM;
	    Log.i(TAG, "mode=ZOOM");
	    break;

	case MotionEvent.ACTION_POINTER_UP:
	    startDist = currentDist;
	    currentDist = calculateDistance(
		    new PointF(event.getX(0), event.getY(0)),
		    new PointF(event.getX(1), event.getY(1)));
	    Log.i(TAG, "mode=ZOOM(P_UP)");
	    break;

	case MotionEvent.ACTION_UP:
	    if (mode == DRAG) {
		PointF start = new PointF(current.x, current.y);
		current.set(event.getX(), event.getY());
		float moveX = scaleMoveX(calculateMoveX(start, current));
		float moveY = scaleMoveY(calculateMoveY(start, current));
		Log.i(TAG, "move(X,Y):" + moveX + "," + moveY
			+ "sensibilite / " + sens);
		camC.changeValFunc(CameraControl.PAN, -1 * moveX / sens, -1
			* moveY / sens);
	    } else if (mode == ZOOM) {
		Log.i(TAG, "startDist=" + startDist);
		Log.i(TAG, "currentDist=" + currentDist);
		if (Math.abs(startDist - currentDist) > 10) {
		    float ratio = (currentDist / startDist > 1) ? currentDist
			    / startDist : -1 * (startDist / currentDist);
		    Log.i(TAG, "ratio=" + ratio);
		    camC.changeValFunc(CameraControl.ZOOM, scaleZoom(ratio), 0);
		}
	    }
	    mode = NONE;
	    Log.i(TAG, "mode=NONE");
	    try {
		/* Bloc UI thread to not spam request */
		Thread.sleep(200);
	    } catch (InterruptedException e) {
		e.printStackTrace();
	    }
	    break;
	}

	return true;
    }

    /**
     * Calculate the horizontal distance between 2 points
     * @param a The first point
     * @param b The second point 
     * @return The difference between a and b on x-axis
     */
    private float calculateMoveX(PointF a, PointF b) {
	return b.x - a.x;
    }

    /**
     * Calculate the vertical distance between 2 points
     * @param a The first point
     * @param b The second point
     * @return The difference between a and b on y-axis
     */
    private float calculateMoveY(PointF a, PointF b) {
	return b.y - a.y;
    }

    /**
     * Calculate the distance between 2 points
     * @param a The first point
     * @param b The second point
     * @return The difference between a and b
     */
    private float calculateDistance(PointF a, PointF b) {
	float x = b.x - a.x;
	float y = b.y - a.y;
	return FloatMath.sqrt(x * x + y * y);
    }

    /**
     * Scale screen horizontal move to real pan move performed by the camera
     * @param distance The value to scale to width
     * @return The scaled distance
     */
    private float scaleMoveX(float distance) {
	return width / 180 * distance;
    }

    /**
     * Scale screen vertical move to real tilt move performed by the camera
     * @param distance The value to scale to height
     * @return The scaled distance
     */
    private float scaleMoveY(float distance) {
	return -1 * (height / 180 * distance);
    }

    /**
     * Scale screen zoom to real zoom performed by the camera
     * @param ratio The zoom ratio to scale
     * @return The scaled ratio
     */
    private float scaleZoom(float ratio) {
	return ratio * zoomStep;
    }
}
