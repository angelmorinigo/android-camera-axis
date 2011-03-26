package com.myapps.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.graphics.Bitmap;

/**
 * 
 * Manages save of snapshots on device
 *
 */
public class snapShotManager {

	/**
	 * Save the {@code bmp} under {@code name} in the directory {@code url}
	 * @param bmp The Bitmap image to save
	 * @param url The directory in which save the image
	 * @param name The name given to the file
	 * @return true if save succeeded
	 */
    public static boolean saveSnap(Bitmap bmp, String url, String name) {

	File f = new File(url);
	if (!f.exists()) {
	    f.mkdir();
	}

	FileOutputStream fichier;
	try {
	    fichier = new FileOutputStream(url + name);
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
