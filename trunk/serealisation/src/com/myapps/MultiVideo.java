package com.myapps;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

public class MultiVideo extends Activity {
	private static Activity activity;
	private static ArrayList<Camera> camList;
	private String[] stringCamList;

	private Thread[] t = new Thread[4];
	private static ImageView[] img = new ImageView[4];
	private boolean[] start = new boolean[4];
	public static Bitmap[] newBMP = new Bitmap[4];

	protected static final int GUIUPDATEIDENTIFIER = 0x101;

	static Handler myViewUpdateHandler = new Handler() {
		public void handleMessage(Message msg) {
			int index = msg.arg1;
			if (msg.what == GUIUPDATEIDENTIFIER) {
					img[index].setImageBitmap(newBMP[index]);
					img[index].invalidate();
					Log.i("AppLog", "handleMessage");
				}
			super.handleMessage(msg);
		}
	};

	@SuppressWarnings("unchecked")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.multi_view);
		activity = this;

		/* Init */
		setRequestedOrientation(0);

		img[0] = (ImageView) findViewById(R.id.image0);
		img[1] = (ImageView) findViewById(R.id.image1);
		img[2] = (ImageView) findViewById(R.id.image2);
		img[3] = (ImageView) findViewById(R.id.image3);
		Log.i(getString(R.string.logTag), "img ok");

		
		/* RÃ©cupÃ©ration des arguments */
		Bundle extras = getIntent().getExtras();
		camList = (ArrayList<Camera>) extras
				.getSerializable(getString(R.string.camListTag));
		
		Log.i(getString(R.string.logTag), "camera list recupérée ");

		stringCamList = new String[camList.size()];
		for (int i = 0; i < camList.size(); i++)
			stringCamList[i] = camList.get(i).id;
		
		for (int i = 0; i < 4; i++)
			img[i].setOnClickListener(new myOnClickListener(i));
		
		Log.i(getString(R.string.logTag), "listner ok");
	}

	public void onDestroy() {
		for (int i = 0; i < 4; i++)
			if(t[i] != null)
			t[i].interrupt();
		super.onDestroy();

	}
	
	

	private class myOnClickListener implements OnClickListener {
		int index;

		public myOnClickListener(int i) {
			this.index = i;
		}

		@Override
		public void onClick(View v) {
			if (!start[index]) {
				AlertDialog.Builder builder = new AlertDialog.Builder(activity);
				builder.setTitle("Camera");
				builder.setSingleChoiceItems(stringCamList, -1,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int item) {
								dialog.dismiss();
								start[index] = true;
								t[index] = new Thread(new PlayerThread(camList
										.get(item), index,50));
								t[index].start();
							}
						});
				AlertDialog alert = builder.create();
				alert.show();
			} else {
				Log.i(getString(R.string.logTag), "Interupt !!!");
				t[index].interrupt();
				start[index] = false;
			}
		
			
		}

	}

}
