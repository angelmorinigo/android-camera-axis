package com.myapps;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Authenticator;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.sql.Time;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.net.Uri.Builder;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.Toast;

public class Video extends Activity {
	private String uri;
	private Camera cam;
	private CameraControl camC;
	private boolean connected = false;
	public Activity activity;
	private static ImageView img;
	private URLConnection con;

	private Thread p;
	private boolean start = false;
	protected static final int GUIUPDATEIDENTIFIER = 0x101;
	static final String[] SIZE = new String[] { "1280x1024", "1280x960",
			"1280x720", "768x576", "4CIF", "704x576", "704x480", "VGA",
			"640x480", "640x360", "2CIFEXP", "2CIF", "704x288", "704x240",
			"480x360", "CIF", "384x288", "352x288", "352x240", "320x240",
			"240x180", "QCIF", "192x144", "176x144", "176x120", "160x120" };
	static Bitmap newBMP;

	static Handler myViewUpdateHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == GUIUPDATEIDENTIFIER) {
				img.setImageBitmap(newBMP);
				img.invalidate();
				Log.i("AppLog", "handleMessage");
			}
			super.handleMessage(msg);
		}
	};

	
	
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.video);
		setRequestedOrientation(0);
		activity = this;
		
		img = (ImageView) findViewById(R.id.imageView1);
		Button buttonPlay = (Button) findViewById(R.id.Play);
		Button buttonSnap = (Button) findViewById(R.id.Snap);
		newBMP = null;
		
		
		/* Récupération des arguments */
		Bundle extras = getIntent().getExtras();
		cam = (Camera) extras.getSerializable(getString(R.string.camTag));
		camC = new CameraControl(cam);
		uri = cam.getUrl();
		Log.i(getString(R.string.logTag), "Demande lecture " + uri);

		
		
		/* Button Listener */
		
		buttonPlay.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!start) {
					start = true;
					p = new Thread(new PlayerThread());
					p.start();
				} else {
					Log.i(getString(R.string.logTag), "Interupt !!!");
					p.interrupt();
					start = false;
				}
			}
		});
		
		
		buttonSnap.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				AlertDialog.Builder builder = new AlertDialog.Builder(activity);
				builder.setTitle("SnapShot Format");
				builder.setSingleChoiceItems(SIZE, -1,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int item) {
								try {
									Bitmap bmp = camC.takeSnapshot(SIZE[item]);
									FileOutputStream fichier = new FileOutputStream("/sdcard/test.jpeg");
								      img.setImageBitmap(bmp);
									bmp.compress(Bitmap.CompressFormat.JPEG,
											80, fichier);
									fichier.flush();
									fichier.close();
									Log.i(getString(R.string.logTag), "Snap Save !!");
								} catch (IOException e) {
									e.printStackTrace();
								}
								dialog.dismiss();
							}
						});
				AlertDialog alert = builder.create();
				alert.show();
			}
		});
		
		/*
		 * Contrôle du PTZ par déplacement sur l'écran
		 */
		img.setOnTouchListener(new OnTouchListener() {
			float startX, startY;
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					startX = event.getX();
					startY = event.getY();
					return true;
				}
				return false;
			}
		});
	}

	/* onCreate comment */
	/* Authentification */
	/*
	 * try { URL url = new URL("http://192.168.1.20:80"); con =
	 * url.openConnection(); con.setRequestProperty("Authorization",
	 * base64Encoder.userNamePasswordBase64(cam.login, cam.pass));
	 * con.connect(); connected = true; } catch (IOException e) {
	 * e.printStackTrace(); }
	 */

	// try {
	// String UrlIndex = cam.protocol + "://" + cam.ip;
	// uri = "rtsp://root:root@192.168.1.20:554/mpeg4/1/media.amp";
	// URI t = new URI("rtsp", "root:root"
	/*
	 * base64Encoder. userNamePasswordBase64 (cam.login, cam.pass)
	 */
	/*
	 * , "192.168.1.20", 554, null, null, null); // URL url = new URL(UrlIndex);
	 * 
	 * Log.i(getString(R.string.logTag), "URI : " + t.toString()); /* final URL
	 * UrlIndex = new URL("rtsp", "192.168.1.20", 554, "", new
	 * URLStreamHandler() {
	 * 
	 * @Override protected URLConnection openConnection(URL u) throws
	 * IOException { URLConnection con = new
	 * con.setRequestProperty("Authorization",
	 * base64Encoder.userNamePasswordBase64( cam.login, cam.pass));
	 * 
	 * return con; } });// .toURL();
	 */
	// URL urlIndex = new URL("rtsp", "192.168.1.20", 554, "/");
	/*
	 * URL urlIndex = new URL("rtsp://192.168.1.20");
	 * Log.i(getString(R.string.logTag), "URL : " + urlIndex.toString());
	 * urlIndex.openConnection(); con.setRequestProperty("Authorization",
	 * base64Encoder.userNamePasswordBase64(cam.login, cam.pass));
	 * con.connect(); connected = true;
	 * 
	 * Log.i(getString(R.string.logTag), "con connect" + con.getURL()); } catch
	 * (MalformedURLException e) { Log.i(getString(R.string.logTag),
	 * "con fail MalformedURLException"); e.printStackTrace(); } catch
	 * (IOException e) { Log.i(getString(R.string.logTag),
	 * "con fail IOException"); e.printStackTrace(); }
	 */

	/*
	 * @Override public void surfaceCreated(SurfaceHolder holder) {
	 * 
	 * try { Log.i(getString(R.string.logTag), "demarrage video"); try {
	 * Authenticator.setDefault(new MyAuthenticator("root", "root"));
	 * 
	 * Uri.Builder build = Uri.parse("rtsp://192.168.1.20:554") .buildUpon(); ;
	 */
	/*
	 * Uri.Builder build = new Builder(); build.scheme("rtsp");
	 * build.encodedPath("//192.168.1.20:554/mpeg4/1/media.amp"); //
	 * build.encodedAuthority (base64Encoder.userNamePasswordBase64(cam.login,
	 * // cam.pass)); Uri b = build.build();
	 * //mediaPlayer.setDataSource(context, b); URL urlIndex = new
	 * URL(build.build().toString()); // con = urlIndex.openConnection(); //
	 * con.setRequestProperty("Authorization", base64Encoder //
	 * .userNamePasswordBase64(cam.login, cam.pass)); // con.connect();
	 * 
	 * Log.i(getString(R.string.logTag), "Uri : " + urlIndex); //
	 * mediaPlayer.setDataSource(context, b); // Uri u =
	 * Uri.parse("rtsp://192.168.1.20/mpeg4/media.amp"); //
	 * Log.i(getString(R.string.logTag), "con " + u.toString()); // mediaPlayer
	 * .setDataSource("rtsp://192.168.1.20/mpeg4/media.amp#Authorization: "
	 * +base64Encoder.userNamePasswordBase64(cam.login, // cam.pass)); //
	 * mediaPlayer.setDataSource(context, Uri.fromParts("rtsp", //
	 * "root:root@192.168.1.20/mpeg4/media.amp", null)); //
	 * mediaPlayer.setDataSource(context,Uri. } catch (SecurityException e) {
	 * e.printStackTrace(); } mediaPlayer.setScreenOnWhilePlaying(true);
	 * mediaPlayer.prepare(); Log.i(getString(R.string.logTag),
	 * "video en cours de lecture"); } catch (IllegalArgumentException e) {
	 * e.printStackTrace(); } catch (IllegalStateException e) {
	 * e.printStackTrace(); } catch (IOException e) { Toast.makeText(this,
	 * "No video found", Toast.LENGTH_LONG).show();
	 * Log.i(getString(R.string.logTag), "No video found"); e.printStackTrace();
	 * this.finish(); }
	 * 
	 * }
	 */

}
