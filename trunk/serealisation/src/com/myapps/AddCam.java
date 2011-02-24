package com.myapps;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

public class AddCam extends Activity {
    /** Called when the activity is first created. */
    public EditText id, login, pass, ip, port, channel;
    public String url = null;
    Camera tmp;
    public boolean resultFromQr = false;
    private Context context;

    private String ipText = "Ip found with Qr";
    private String portText = "Port found with Qr";

    // public Spinner s;

    @Override
    public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.add_cam);
	// context = this;
	id = (EditText) findViewById(R.id.eid);
	login = (EditText) findViewById(R.id.elogin);
	pass = (EditText) findViewById(R.id.epass);
	ip = (EditText) findViewById(R.id.eip);
	port = (EditText) findViewById(R.id.eport);
	channel = (EditText) findViewById(R.id.echan);

	/* Spinner listener (ComboBox) */
	/*
	 * s = (Spinner) findViewById(R.id.spinner); ArrayAdapter<CharSequence>
	 * adapter = ArrayAdapter.createFromResource( context,
	 * R.array.protocol_array, android.R.layout.simple_spinner_item);
	 * adapter
	 * .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item
	 * ); s.setAdapter(adapter);
	 */

	Log.i(getString(R.string.logTag), "onCreate");
	/* Buttons listener */
	Button bAdd = (Button) findViewById(R.id.add);
	bAdd.setOnClickListener(new View.OnClickListener() {
	    public void onClick(View v) {
		String vId = id.getText().toString();
		String vLogin = login.getText().toString();
		String vPass = pass.getText().toString();
		String vIp = ip.getText().toString();
		String vPort = port.getText().toString();
		String vProtocol = "http"; // s.getSelectedItem().toString();
		String vChan = channel.getText().toString();

		/* Check result */
		if (vId.equalsIgnoreCase("") | vLogin.equalsIgnoreCase("")
			| vPass.equalsIgnoreCase(""))
		    return;
		if (vChan.equalsIgnoreCase(""))
		    vChan = "1";

		if (!resultFromQr) {
		    if (vIp.equalsIgnoreCase("") | vPort.equalsIgnoreCase("")
			    | vProtocol.equalsIgnoreCase(""))
			return;

		    tmp = new Camera(vId, vLogin, vPass, vIp, Integer
			    .parseInt(vPort), vProtocol, Integer
			    .parseInt(vChan));
		    Log.i(getString(R.string.logTag),
			    ("Camera : " + tmp.getURI()));
		} else {
		    tmp = new Camera(vId, vLogin, vPass, url, Integer
			    .parseInt(vChan));
		    Log.i(getString(R.string.logTag),
			    ("Camera : " + tmp.getURI()));
		}

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
	    public void onClick(View v) {
		try{
		Intent intent = new Intent("com.google.zxing.client.android.SCAN");
		intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
		startActivityForResult(intent, 0);
		}catch(ActivityNotFoundException e){
		    String marketSearch = "market://details?id=com.google.zxing.client.android";
		    Intent updateIntent = new Intent(Intent.ACTION_VIEW,
					Uri.parse(marketSearch));
			startActivity(updateIntent); 
		}
	    }
	});

    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
	if (requestCode == 0) {
	    if (resultCode == RESULT_OK) {
		resultFromQr = true;
		String contents = intent.getStringExtra("SCAN_RESULT");
		String format = intent.getStringExtra("SCAN_RESULT_FORMAT");
		url = contents;

		port.setHint(portText);
		ip.setHint(ipText);
		ip.setFocusable(false);
		port.setFocusable(false);
		Log.i("AppLog",
			("content :" + contents + " format : " + format));
	    } else if (resultCode == RESULT_CANCELED) {
		// Handle cancel
	    }
	}
    }
}