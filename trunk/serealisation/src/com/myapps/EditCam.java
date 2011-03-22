package com.myapps;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class EditCam extends Activity {
    private EditText id, login, pass, address, channel;
    private Camera cam;
    private int position;

    /**
     * Called when Activity start or resume
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.edit_cam);

	/* Recover arguments */
	Bundle extras = getIntent().getExtras();
	cam = (Camera) extras.getSerializable(getString(R.string.camTag));
	position = extras.getInt(getString(R.string.camPosition));
	id = (EditText) findViewById(R.id.emid);
	login = (EditText) findViewById(R.id.emlogin);
	pass = (EditText) findViewById(R.id.empass);
	channel = (EditText) findViewById(R.id.emchan);
	address = (EditText) findViewById(R.id.emaddr);

	id.setText(cam.id);
	login.setText(cam.login);
	pass.setText(cam.pass);
	channel.setText("" + cam.channel);
	address.setText(cam.uri.toString());

	Button modify = (Button) findViewById(R.id.modify);
	modify.setOnClickListener(new View.OnClickListener() {
	    /*
	     * Get informations from EditText and return a new Camera
	     */
	    public void onClick(View v) {
		String vId = id.getText().toString();
		String vLogin = login.getText().toString();
		String vPass = pass.getText().toString();
		String vAddr = address.getText().toString();
		String vChan = channel.getText().toString();

		if (vId.equalsIgnoreCase("") | vAddr.equalsIgnoreCase(""))
		    return;
		if (vChan.equalsIgnoreCase(""))
		    vChan = "1";

		Camera tmp = new Camera(vId, vLogin, vPass, vAddr, Integer
			.parseInt(vChan));

		/*
		 * Create Intent with extra to result the new Camera
		 */
		Intent outData = new Intent();
		Bundle objetbunble = new Bundle();
		objetbunble.putSerializable(getString(R.string.camTag), tmp);
		outData.putExtras(objetbunble);
		outData.putExtra(getString(R.string.camPosition), position);
		setResult(RESULT_OK, outData);
		finish();
	    }
	});
	Button bRet = (Button) findViewById(R.id.cancel);
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
    }
}
