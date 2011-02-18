package com.myapps;

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
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.net.Uri.Builder;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

public class Video extends Activity {
	private MediaPlayer mediaPlayer;
	private String uri;
	private Camera cam;
	private boolean connected = false;

	private ImageView img;
	private URLConnection con;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.video);
		setRequestedOrientation(0);
		mediaPlayer = new MediaPlayer();

		/* Récupération des arguments */
		Bundle extras = getIntent().getExtras();
		cam = (Camera) extras.getSerializable(getString(R.string.camTag));

		uri = cam.getUrl();
		Log.i(getString(R.string.logTag), "Demande lecture " + uri);

		/* Authentification */
	/*	try {
			URL url = new URL("http://192.168.1.20:80");
			con = url.openConnection();
			con.setRequestProperty("Authorization",
					base64Encoder.userNamePasswordBase64(cam.login, cam.pass));
			con.connect();
			connected = true;
		} catch (IOException e) {
			e.printStackTrace();
		}*/
		
		// try {
		// String UrlIndex = cam.protocol + "://" + cam.ip;
		// uri = "rtsp://root:root@192.168.1.20:554/mpeg4/1/media.amp";
		// URI t = new URI("rtsp", "root:root"
		/*
		 * base64Encoder. userNamePasswordBase64 (cam.login, cam.pass)
		 */
		/*
		 * , "192.168.1.20", 554, null, null, null); // URL url = new
		 * URL(UrlIndex);
		 * 
		 * Log.i(getString(R.string.logTag), "URI : " + t.toString()); /* final
		 * URL UrlIndex = new URL("rtsp", "192.168.1.20", 554, "", new
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
		 * Log.i(getString(R.string.logTag), "con connect" + con.getURL()); }
		 * catch (MalformedURLException e) { Log.i(getString(R.string.logTag),
		 * "con fail MalformedURLException"); e.printStackTrace(); } catch
		 * (IOException e) { Log.i(getString(R.string.logTag),
		 * "con fail IOException"); e.printStackTrace(); }
		 */

		img = (ImageView) findViewById(R.id.imageView1);

		/* Button Listener */
		Button buttonPlay = (Button) findViewById(R.id.Play);
		buttonPlay.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				/*
				 * PlayerThread p = new PlayerThread(); p.start();
				 * while(p.isAlive()){ img.setImageBitmap(p.getBMP());
				 * img.invalidate(); Log.i(getString(R.string.logTag), "-"); }
				 * p.destroy();
				 */
				try {
					/* Prends juste un screenshot */
					long t1 = System.currentTimeMillis();
					URL url = new URL("http://192.168.1.20/axis-cgi/jpg/image.cgi");
					con = url.openConnection();
					con.setRequestProperty("Authorization",
							base64Encoder.userNamePasswordBase64(cam.login, cam.pass));
					con.connect();
					connected = true;
					Bitmap bmp = null;			
					InputStream stream = con.getInputStream();
					bmp = BitmapFactory.decodeStream(stream);
					stream.close();
					img.setImageBitmap(bmp);
					long t2 = System.currentTimeMillis();
					long t3 = t2-t1;
					Log.i(getString(R.string.logTag), "ms : " + t3);
				} catch (MalformedURLException e) {
				} catch (IOException e) {
				}
			}
		});
	}

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
