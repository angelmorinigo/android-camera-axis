package com.myapps;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class Home extends Activity {
	private static Dialog dialog_about;
	private static Activity activity;
	private ListView L;
	private ArrayList<Camera> camList;
	private String FILE = "camera.ser";

	@SuppressWarnings("unchecked")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.view);
		activity = this;

		try {
			FileInputStream fichier = activity.getApplication().openFileInput(
					FILE);
			ObjectInputStream ois = new ObjectInputStream(fichier);
			Log.i("AppLog", "lecture cameras effectuee");

			camList = (ArrayList<Camera>) ois.readObject();
			String[] s = new String[camList.size()];
			for (int i = 0; i < camList.size(); i++) {
				s[i] = camList.get(i).toString();
			}

			/* Affichage de la liste */
			L = (ListView) findViewById(R.id.lv);
			L.setAdapter(new ArrayAdapter<String>(this, R.layout.list_item, s));
			L.setTextFilterEnabled(true);
		} catch (java.io.IOException e) {
			Log.i("AppLog", "file not found");
			camList = new ArrayList<Camera>();
			String[] s = new String[camList.size()];
			for (int i = 0; i < camList.size(); i++) {
				s[i] = "";
			}

			/* Affichage de la liste */
			L = (ListView) findViewById(R.id.lv);
			L.setAdapter(new ArrayAdapter<String>(this, R.layout.list_item, s));
			L.setTextFilterEnabled(true);
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		L.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {

				Intent intent = new Intent(activity.getApplicationContext(), Video.class);
				startActivity(intent);
			}
		});

	}

	protected void onDestroy() {
		try {
			FileOutputStream fichier = activity.getApplicationContext()
					.openFileOutput(FILE, Context.MODE_APPEND);
			ObjectOutputStream oos = new ObjectOutputStream(fichier);
			oos.writeObject(camList);
			oos.flush();
			oos.close();
			fichier.close();
			Log.i("AppLog", "camera save");
		} catch (java.io.IOException e) {
			Log.i("AppLog", "file not save");
			e.printStackTrace();
		}
		super.onDestroy();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode,
			Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);
		Bundle extras = intent.getExtras();
		Camera tmp = (Camera) extras.getSerializable("camera");
		Log.i("AppLog", "camera " + tmp.id + " recuperer");

		camList.add(tmp);
		Log.i("AppLog", "camera ajouter");

		int nb = camList.size();
		String[] s = new String[nb];
		for (int i = 0; i < nb; i++) {
			s[i] = camList.get(i).toString();
		}
		Log.i("AppLog", "cam list :" + s);
		L.setAdapter(new ArrayAdapter<String>(this, R.layout.list_item, s));
	}

	/* Affichage du menu */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_option_about:
			dialog_about = new Dialog(activity);
			dialog_about.setContentView(R.layout.dialog_about);
			dialog_about.setTitle("A Propos");

			TextView text = (TextView) dialog_about.findViewById(R.id.about);
			text.setText(activity.getResources().getString(R.string.about));
			ImageView image = (ImageView) dialog_about
					.findViewById(R.id.about_image);
			image.setImageResource(R.drawable.ic_fave1);
			Button close = (Button) dialog_about.findViewById(R.id.aboutClose);
			close.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					dialog_about.cancel();
				}
			});

			dialog_about.show();
			return true;
		case R.id.menu_quitter:
			Log.i("AppLog", "Exit");
			activity.finish();
			return true;
		case R.id.menu_ajouter:
			Intent intent = new Intent(this, AddCam.class);
			startActivityForResult(intent, 1);
			return true;
		}
		return false;
	}
}