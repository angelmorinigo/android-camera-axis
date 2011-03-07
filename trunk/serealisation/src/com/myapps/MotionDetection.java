package com.myapps;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;

import android.os.Message;
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
	HttpURLConnection con;
	try {
	    con = camC
		    .sendCommand("axis-cgi/motion/motiondata.cgi?Sensitivity=65&History=50&Size=25");
	    InputStreamReader isr = new InputStreamReader(con.getInputStream());
	    BufferedReader br = new BufferedReader(isr);
	    String s;
	    int lvlc, lvlb, lvlf;
	    long last = System.currentTimeMillis();
	    long delay = 10000;
	    while (true) {
		s = br.readLine();
		if (s.contains("level=") == true) {
		    lvlc = s.indexOf("level=");
		    s = s.substring(lvlc);
		    lvlb = s.indexOf("=");
		    lvlf = s.indexOf(";");
		    s = s.substring(lvlb + 1, lvlf);
		    if (Integer.parseInt(s) > limit) {
			Message m = new Message();
			m.what = Video.MVTMSG;
			if (last + delay < System.currentTimeMillis()) {
			    Video.myViewUpdateHandler.sendMessage(m);
			    last = System.currentTimeMillis();
			}
		    }
		}
	    }
	} catch (IOException e) {
	    e.printStackTrace();
	}
    }
}