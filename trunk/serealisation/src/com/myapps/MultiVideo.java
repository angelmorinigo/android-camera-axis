package com.myapps;

import java.io.IOException;
import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.ImageView;
import android.widget.Toast;

/**
 * 
 * MultiVideo class implements the 4 videos Viewer
 * 
 */
public class MultiVideo extends Activity {
    private static Activity activity;
    private static ArrayList<Camera> camList;
    private String[] stringCamList;

    private Camera[] camView;
    private Thread[] t;
    private static ImageView[] img;
    private static boolean[] start;
    public static Bitmap[] newBMP;
    private int nbView = 0;

    protected static final int GUIUPDATEIDENTIFIER = 0x101;
    protected static final int URLERRORIDENTIFIER = 0x102;

    static Handler myViewUpdateHandler = new Handler() {
	public void handleMessage(Message msg) {
	    int index = msg.arg1;
	    if (msg.what == GUIUPDATEIDENTIFIER) {
		img[index].setImageBitmap(newBMP[index]);
		img[index].invalidate();
		Log.i("AppLog", "handleMessage");
	    }
	    if (msg.what == URLERRORIDENTIFIER) {
		start[index] = false;
		Toast.makeText(activity.getApplicationContext(),
			"Cam�ra introuvable", Toast.LENGTH_LONG).show();
	    }
	    super.handleMessage(msg);
	}
    };

    /**
     * Called when Activity start or resume
     */
    @SuppressWarnings("unchecked")
    @Override
    public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	activity = this;

	/* Init */
	/* recover the argument (camera list) */
	Bundle extras = getIntent().getExtras();
	camList = (ArrayList<Camera>) extras
		.getSerializable(getString(R.string.camListTag));
	nbView = extras.getInt(getString(R.string.nbViewTag));
	stringCamList = new String[camList.size()];
	for (int i = 0; i < camList.size(); i++)
	    stringCamList[i] = camList.get(i).id;
	Log.i(getString(R.string.logTag), "camera list found ");

	camView = new Camera[nbView];
	t = new Thread[nbView];
	img = new ImageView[nbView];
	start = new boolean[nbView];
	newBMP = new Bitmap[nbView];

	switch (nbView) {
	case 2:
	    setRequestedOrientation(0);
	    setContentView(R.layout.multi_video_2);
	    break;
	case 3:
	    setRequestedOrientation(1);
	    setContentView(R.layout.multi_video_3);
	    break;
	case 4:
	    setRequestedOrientation(0);
	    setContentView(R.layout.multi_video_4);
	    break;
	case 5:
	    setRequestedOrientation(0);
	    setContentView(R.layout.multi_video_5);
	    break;
	case 6:
	    setRequestedOrientation(0);
	    setContentView(R.layout.multi_video_6);
	    break;
	}
	/*
	 * get R.id.image0 address and inc it to find R.id.image1, R.id.image2,
	 * ... , R.id.image.n
	 */
	int dep = R.id.image0;
	for (int i = 0; i < nbView; i++) {
	    img[i] = (ImageView) findViewById(dep + i);
	}
	Log.i(getString(R.string.logTag), "img ok");

	/* Set Image and Listener for each view */
	for (int i = 0; i < nbView; i++) {
	    camView[i] = null;
	    img[i].setImageResource(R.drawable.cadre);
	    img[i].setOnClickListener(new myOnClickListener(i));
	    img[i].setOnLongClickListener(new myOnLongClickListener(i));
	}

	Log.i(getString(R.string.logTag), "listner ok");

    }

    /**
     * Stop each view before destroy
     */
    public void onDestroy() {
	for (int i = 0; i < nbView; i++)
	    if (t[i] != null)
		t[i].interrupt();
	super.onDestroy();

    }

    /**
     * 
     * LongClickListener run video activity when LongClick on a view
     * 
     */
    private class myOnLongClickListener implements OnLongClickListener {
	int index;

	public myOnLongClickListener(int i) {
	    this.index = i;
	}

	@Override
	public boolean onLongClick(View v) {
	    if (camView[index] != null) {
		Intent intent = new Intent(activity.getApplicationContext(),
			Video.class);
		Bundle objetbunble = new Bundle();
		objetbunble.putSerializable(getString(R.string.camTag),
			camView[index]);
		intent.putExtras(objetbunble);
		startActivity(intent);
		return true;
	    }
	    return false;
	}

    }

    /**
     * 
     * onClickListener show dialog to select video or stop video
     * 
     */
    private class myOnClickListener implements OnClickListener {
	int index;

	public myOnClickListener(int i) {
	    this.index = i;
	}

	@Override
	public void onClick(View v) {
	    if (!start[index]) {
		/* Show dialog */
		AlertDialog.Builder builder = new AlertDialog.Builder(activity);
		builder.setTitle("Camera");
		builder.setSingleChoiceItems(stringCamList, -1,
			new DialogInterface.OnClickListener() {
			    public void onClick(DialogInterface dialog, int item) {
				dialog.dismiss();
				start[index] = true;
				camView[index] = camList.get(item);
				try {
				    t[index] = new Thread(new PlayerThread(
					    camList.get(item), index, 250));
				} catch (IOException e) {
				    Log.i(getString(R.string.logTag),
					    "MultiVideo IOException");
				    Toast.makeText(
					    activity.getApplicationContext(),
					    "Camera introuvable",
					    Toast.LENGTH_LONG).show();
				    e.printStackTrace();
				}
				t[index].start();
			    }
			});
		AlertDialog alert = builder.create();
		alert.show();
	    } else {
		/* Stop video */
		Log.i(getString(R.string.logTag), "Interupt !!!");
		t[index].interrupt();
		start[index] = false;
	    }

	}

    }

}
