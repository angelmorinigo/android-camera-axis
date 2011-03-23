package com.myapps;

import com.myapps.utils.xmlIO;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

/**
 * Activity used to add camera
 * Check out result with 
 * Camera cam = (Camera) extras.getSerializable(getString(R.string.camTag));
 */
public class AddCam extends Activity {
    private EditText id, login, pass, ip, port, channel;
 // private Spinner s;
    private Camera tmp;
    private boolean resultFromQr = false;

    

    
/**
 * Called when Activity start or resume
 */
    @Override
    public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.add_cam);

	id = (EditText) findViewById(R.id.eid);
	login = (EditText) findViewById(R.id.elogin);
	pass = (EditText) findViewById(R.id.epass);
	ip = (EditText) findViewById(R.id.eip);
	port = (EditText) findViewById(R.id.eport);
	channel = (EditText) findViewById(R.id.echan);

	/*
	 * Spinner listener (ComboBox) never use with one protocol
	 */
	/*
	 * s = (Spinner) findViewById(R.id.spinner); ArrayAdapter<CharSequence>
	 * adapter = ArrayAdapter.createFromResource( context,
	 * R.array.protocol_array, android.R.layout.simple_spinner_item);
	 * adapter
	 * .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item
	 * ); s.setAdapter(adapter);
	 */

	/*
	 * Buttons listener
	 */
	Button bAdd = (Button) findViewById(R.id.add);
	bAdd.setOnClickListener(new View.OnClickListener() {
	    /*
	     * Get informations from EditText and return a new Camera
	     */
	    public void onClick(View v) {
		String vId = id.getText().toString();
		String vLogin = login.getText().toString();
		String vPass = pass.getText().toString();
		String vIp = ip.getText().toString();
		String vPort = port.getText().toString();
		String vProtocol = "http"; // s.getSelectedItem().toString();
		String vChan = channel.getText().toString();

		if (vId.equalsIgnoreCase(""))
		    return;
		 if(vLogin.equalsIgnoreCase(""))
		     vLogin = "";
		 
		if(vPass.equalsIgnoreCase(""))
		    vPass = "";
		
		if (vChan.equalsIgnoreCase(""))
		    vChan = "1";

		    if (vIp.equalsIgnoreCase("") | vPort.equalsIgnoreCase("")
			    | vProtocol.equalsIgnoreCase(""))
			return;
		    tmp = new Camera(vId, vLogin, vPass, vIp, Integer
			    .parseInt(vPort), vProtocol, Integer
			    .parseInt(vChan));

		Log.i(getString(R.string.logTag), ("Camera : " + tmp.getURI()));

		/*
		 * Create Intent with extra to result the new Camera
		 */
		Intent outData = new Intent();
		Bundle objetbunble = new Bundle();
		objetbunble.putSerializable(getString(R.string.camTag), tmp);
		outData.putExtras(objetbunble);
		setResult(RESULT_OK, outData);
		finish();
	    }
	});

	Button bRet = (Button) findViewById(R.id.ret);
	bRet.setOnClickListener(new View.OnClickListener() {
	    /*
	     * Finish activity without result
	     */
	    public void onClick(View v) {
		Intent outData = new Intent();
		Bundle objetbunble = new Bundle();
		objetbunble.putSerializable(getString(R.string.camTag), null);
		outData.putExtras(objetbunble);
		setResult(RESULT_CANCELED, outData);
		finish();
	    }
	});

	Button b = (Button) findViewById(R.id.bqr);
	b.setOnClickListener(new OnClickListener() {
	    /*
	     * Read QrCode to create a Camera zxing project is required, if
	     * zxing isn't install go to market for download it
	     */
	    public void onClick(View v) {
		try {
		    Intent intent = new Intent(
			    "com.google.zxing.client.android.SCAN");
		    intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
		    startActivityForResult(intent, 0);
		} catch (ActivityNotFoundException e) {
		    String marketSearch = "market://details?id=com.google.zxing.client.android";
		    Intent updateIntent = new Intent(Intent.ACTION_VIEW, Uri
			    .parse(marketSearch));
		    startActivity(updateIntent);
		}
	    }
	});

    }

    /**
     * Called when activity result from zxing project (param detail copied from
     * official android doc)
     * 
     * @param requestCode The integer request code originally supplied to
     *                   startActivityForResult(), allowing you to identify who
     *                   this result came from.
     * @param resultCode
     *            The integer result code returned by the child activity through
     *            its setResult().
     * @param data
     *            An Intent, which can return result data to the caller (various
     *            data can be attached to Intent "extras").
     */
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
	if (requestCode == 0) {
	    if (resultCode == RESULT_OK) {
		resultFromQr = true;
		String contents = intent.getStringExtra("SCAN_RESULT");
		String format = intent.getStringExtra("SCAN_RESULT_FORMAT");
		
		Camera tmp = xmlIO.xmlReadString(contents);
		id.setText(tmp.id);
		ip.setText(tmp.uri.getHost());
		port.setText(""+tmp.uri.getPort());
		channel.setText(""+tmp.channel);
		Log.i(getString(R.string.logTag),
			("content :" + contents + " format : " + format));
	    } else if (resultCode == RESULT_CANCELED) {
		//Do nothing
	    }
	}
    }
}