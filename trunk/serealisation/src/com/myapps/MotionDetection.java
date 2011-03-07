package com.myapps;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;

import android.util.Log;

public class MotionDetection implements Runnable {
	private CameraControl camC;
	private HttpURLConnection con;
	private String motionId;
	private int limit;

	public MotionDetection(CameraControl pCamC, String pMotionId, int pLimit) {
		camC = pCamC;
		motionId = pMotionId;
		limit = pLimit;
	}

	@Override
	public void run() {
		InputStream result;
		BufferedReader in;
		String line;
		int level;
		try {
			con = camC.sendCommand("axis-cgi/motion/motiondata.cgi?"
					+ "group=" + motionId);
			while (!Thread.currentThread().isInterrupted()) {
				result = con.getInputStream();
				in = new BufferedReader(new InputStreamReader(result));
				while ((line = in.readLine()) != null) {
					level = Integer.parseInt(line.substring(
								line.indexOf("level=") + 6,
								line.indexOf(",treshold")));
					if (level > limit) {
						Log.i("AppLog", "Mouvement!!!");
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}