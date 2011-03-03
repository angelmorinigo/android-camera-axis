package com.myapps;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

/**
 * 
 * First Activity called. Describe home interface.
 * 
 */
public class Home extends Activity {
    private static Dialog dialog_about;
    private static Activity activity;
    private ListView L;
    private ArrayList<Camera> camList;
    private String[] nb_view;

    private String exportPath = "/sdcard/com.myapps.camera/export.xml";
    public static SharedPreferences preferences;

    private final static int EDIT_CODE = 2;
    private final static String ITEM_TITLE = "title";
    private final static String ITEM_CAPTION = "caption";

    private Map<String, ?> createItem(String title, String caption) {
	Map<String, String> item = new HashMap<String, String>();
	item.put(ITEM_TITLE, title);
	item.put(ITEM_CAPTION, caption);
	return item;
    }

    private void updateListView(boolean init) {

	List<Map<String, ?>> printCamList = new LinkedList<Map<String, ?>>();
	for (int i = 0; i < camList.size(); i++) {
	    printCamList.add(createItem(camList.get(i).id, camList.get(i)
		    .getURI()));
	}

	/* Print cameras list */
	L = (ListView) findViewById(R.id.lv);
	L.setAdapter(new SimpleAdapter(this, printCamList, R.layout.list_item,
		new String[] { ITEM_TITLE, ITEM_CAPTION }, new int[] {
			R.id.list_complex_title, R.id.list_complex_caption }));
	if (init = true)
	    L.setTextFilterEnabled(true);

    }

    /**
     * Called when Activity start or resume
     */
    @SuppressWarnings("unchecked")
    @Override
    public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.view);
	activity = this;

	/* Resolve preferences */
	preferences = PreferenceManager.getDefaultSharedPreferences(this);

	/* Print tricky */
	if (preferences.getBoolean(getString(R.string.isWelcome), true) == false) {
	    Dialog_welcome myDialog = new Dialog_welcome(this,
		    getString(R.string.messageBienvenue));
	    myDialog.show();
	}

	/* Open custum camera file if it exist */
	try {
	    FileInputStream fichier = activity.getApplication().openFileInput(
		    getString(R.string.fileName));
	    ObjectInputStream ois = new ObjectInputStream(fichier);
	    Log.i(getString(R.string.logTag), "lecture cameras effectuee");
	    camList = (ArrayList<Camera>) ois.readObject();
	    updateListView(false);
	} catch (java.io.IOException e) {
	    Log.i(getString(R.string.logTag), "file not found");
	    camList = new ArrayList<Camera>();
	    updateListView(true);
	    e.printStackTrace();
	} catch (ClassNotFoundException e) {
	    e.printStackTrace();
	}
	L.setOnItemLongClickListener(new OnItemLongClickListener() {
	    @Override
	    /* Print dialog to modifie or delete camera */
	    public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
		    final int position, long arg3) {
		AlertDialog alert;
		AlertDialog.Builder builder = new AlertDialog.Builder(activity);
		builder.setMessage(getString(R.string.messageChoose))
			.setCancelable(false)
			.setPositiveButton(getString(R.string.boutonModifier),
				new DialogInterface.OnClickListener() {
				    @Override
				    public void onClick(DialogInterface dialog,
					    int id) {
					Intent intent = new Intent(activity
						.getApplicationContext(),
						EditCam.class);
					Bundle objetbunble = new Bundle();
					objetbunble.putSerializable(
						getString(R.string.camTag),
						camList.get(position));
					intent.putExtra(
						getString(R.string.camPosition),
						position);
					intent.putExtras(objetbunble);
					dialog.cancel();
					startActivityForResult(intent,
						EDIT_CODE);
				    }
				})
			.setNegativeButton(getString(R.string.boutonSupprimer),
				new DialogInterface.OnClickListener() {
				    @Override
				    public void onClick(DialogInterface dialog,
					    int id) {
					removeCam(position);
					dialog.cancel();
				    }
				});
		alert = builder.create();
		alert.show();
		return true;
	    }
	});

	L.setOnItemClickListener(new OnItemClickListener() {
	    @Override
	    /* Start video activity to view and control camera */
	    public void onItemClick(AdapterView<?> arg0, View arg1,
		    int position, long arg3) {

		Intent intent = new Intent(activity.getApplicationContext(),
			Video.class);
		Bundle objetbunble = new Bundle();
		objetbunble.putSerializable(getString(R.string.camTag),
			camList.get(position));
		intent.putExtras(objetbunble);
		startActivity(intent);
	    }
	});

    }

    /**
     * Show alert to remove the "position" camera
     */
    private void removeCam(final int position) {
	AlertDialog alert_reset;
	AlertDialog.Builder builder = new AlertDialog.Builder(activity);
	builder.setMessage(getString(R.string.messageRemove))
		.setCancelable(false)
		.setPositiveButton(getString(R.string.oui),
			new DialogInterface.OnClickListener() {
			    @Override
			    public void onClick(DialogInterface dialog, int id) {
				camList.remove(position);
				updateListView(false);
				dialog.cancel();
			    }
			})
		.setNegativeButton(getString(R.string.non),
			new DialogInterface.OnClickListener() {
			    @Override
			    public void onClick(DialogInterface dialog, int id) {
				dialog.cancel();
			    }
			});
	alert_reset = builder.create();
	alert_reset.show();
    }

    /**
     * Called when Activity stop. Override to record preferences
     */
    protected void onDestroy() {
	try {
	    FileOutputStream fichier = activity.getApplicationContext()
		    .openFileOutput(getString(R.string.fileName),
			    Context.MODE_PRIVATE);
	    ObjectOutputStream oos = new ObjectOutputStream(fichier);
	    oos.writeObject(camList);
	    oos.flush();
	    oos.close();
	    fichier.close();
	    Log.i(getString(R.string.logTag), "camera save");
	} catch (java.io.IOException e) {
	    Log.i(getString(R.string.logTag), "file not save");
	    e.printStackTrace();
	}
	super.onDestroy();
    }

    /**
     * Called when Activity result from AddCam activity. Add the new camera and
     * refresh the listView. (param detail copied from official android doc)
     * 
     * @param requestCode
     *            The integer request code originally supplied to
     *            startActivityForResult(), allowing you to identify who this
     *            result came from.
     * @param resultCode
     *            The integer result code returned by the child activity through
     *            its setResult().
     * @param data
     *            An Intent, which can return result data to the caller (various
     *            data can be attached to Intent "extras").
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode,
	    Intent intent) {
	super.onActivityResult(requestCode, resultCode, intent);
	if (resultCode == Activity.RESULT_OK) {
	    Bundle extras = intent.getExtras();
	    Camera tmp = (Camera) extras
		    .getSerializable(getString(R.string.camTag));
	    Log.i(getString(R.string.logTag), "camera " + tmp.id + " recuperer");
	    /* Always add at the head */
	    int position = 0;
	    if (requestCode == EDIT_CODE) {
		position = extras.getInt(getString(R.string.camPosition));
		camList.remove(position);
	    }
	    camList.add(position, tmp);
	    updateListView(false);

	}
    }

    /**
     * Assign custom menu to activity
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
	MenuInflater inflater = getMenuInflater();
	inflater.inflate(R.menu.menu, menu);
	return true;
    }

    /**
     * Implements Menu Items Listener
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
	final Dialog dialogImportExport;
	TextView textImportExport;
	final EditText editImportExport;
	Button submit;

	switch (item.getItemId()) {
	case R.id.menu_option_about:
	    dialog_about = new Dialog(activity);
	    dialog_about.setContentView(R.layout.dialog_about);
	    dialog_about.setTitle(getString(R.string.aboutTitle));

	    TextView text = (TextView) dialog_about.findViewById(R.id.about);
	    text.setText(getString(R.string.messageAbout));
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
	    Log.i(getString(R.string.logTag), "Exit");
	    activity.finish();
	    return true;
	case R.id.menu_ajouter:
	    Intent intent = new Intent(this, AddCam.class);
	    startActivityForResult(intent, 1);
	    return true;
	case R.id.menu_multi_vue:
	    nb_view = getResources().getStringArray(R.array.multi_view_array);
	    AlertDialog.Builder builder = new AlertDialog.Builder(activity);
	    builder.setTitle(getString(R.string.cameraAlertTitle));
	    builder.setSingleChoiceItems(nb_view, -1,
		    new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int item) {
			    dialog.dismiss();
			    Intent intent1 = new Intent(activity,
				    MultiVideo.class);
			    Bundle objetbunble = new Bundle();
			    objetbunble.putSerializable(
				    getString(R.string.camListTag), camList);
			    intent1.putExtras(objetbunble);
			    intent1.putExtra(getString(R.string.nbViewTag),
				    (item + 2));
			    Log.i(getString(R.string.logTag),
				    "Start multi view");
			    startActivity(intent1);
			}
		    });
	    AlertDialog alert = builder.create();
	    alert.show();
	    return true;
	case R.id.export:
	    dialogImportExport = new Dialog(activity);
	    dialogImportExport.requestWindowFeature(Window.FEATURE_LEFT_ICON);
	    dialogImportExport.setContentView(R.layout.imp_exp);
	    dialogImportExport.setTitle(getString(R.string.boutonExporter));
	    textImportExport = (TextView) dialogImportExport
		    .findViewById(R.id.importExport);
	    textImportExport.setText(getString(R.string.messageExport));
	    editImportExport = (EditText) dialogImportExport
		    .findViewById(R.id.imp_exp_url);
	    editImportExport.setText(exportPath);
	    submit = (Button) dialogImportExport.findViewById(R.id.imp_exp_ok);
	    submit.setText(getString(R.string.boutonExporter));
	    submit.setOnClickListener(new View.OnClickListener() {
		@Override
		public void onClick(View v) {
		    exportPath = editImportExport.getText().toString();
		    xmlIO.xmlWrite(camList, exportPath);
		    dialogImportExport.cancel();
		}
	    });
	    dialogImportExport.show();
	    dialogImportExport.getWindow().setFeatureDrawableResource(
		    Window.FEATURE_LEFT_ICON, R.drawable.light);
	    return true;
	case R.id.importer:
	    dialogImportExport = new Dialog(activity);
	    dialogImportExport.requestWindowFeature(Window.FEATURE_LEFT_ICON);
	    dialogImportExport.setContentView(R.layout.imp_exp);
	    dialogImportExport.setTitle(getString(R.string.boutonImporter));
	    textImportExport = (TextView) dialogImportExport
		    .findViewById(R.id.importExport);
	    textImportExport.setText(getString(R.string.messageImport));
	    editImportExport = (EditText) dialogImportExport
		    .findViewById(R.id.imp_exp_url);
	    editImportExport.setText(exportPath);
	    submit = (Button) dialogImportExport.findViewById(R.id.imp_exp_ok);
	    submit.setText(getString(R.string.boutonImporter));
	    submit.setOnClickListener(new View.OnClickListener() {
		@Override
		public void onClick(View v) {
		    exportPath = editImportExport.getText().toString();
		    camList = xmlIO.xmlRead(exportPath);
		    updateListView(true);
		    dialogImportExport.cancel();
		}
	    });
	    dialogImportExport.show();
	    dialogImportExport.getWindow().setFeatureDrawableResource(
		    Window.FEATURE_LEFT_ICON, R.drawable.light);
	    return true;
	case R.id.parametres:
	    activity.startActivityForResult(new Intent(activity,
		    MesPreferences.class), 1);
	    return true;
	}
	return false;
    }
}