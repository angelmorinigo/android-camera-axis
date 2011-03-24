package com.myapps.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.graphics.Bitmap;

public class snapShotManager {

    public static boolean saveSnap(Bitmap bmp, String Url, String name) {

	File f = new File(Url);
	if (!f.exists()) {
	    f.mkdir();
	}

	FileOutputStream fichier;
	try {
	    fichier = new FileOutputStream(Url + name);
	    bmp.compress(Bitmap.CompressFormat.JPEG, 80, fichier);
	    fichier.flush();
	    fichier.close();
	} catch (FileNotFoundException e) {
	    e.printStackTrace();
	} catch (IOException e) {
	    e.printStackTrace();
	}

	return true;
    }

}
