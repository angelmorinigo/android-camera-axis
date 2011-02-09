package com.myapps;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class record extends Activity {
	/** Called when the activity is first created. */
	public EditText id;
	public EditText login;
	public EditText pass;
	public EditText ip;
	public EditText port;

	/* Definition de la camera */

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		id = (EditText) findViewById(R.id.eid);
		login = (EditText) findViewById(R.id.elogin);
		pass = (EditText) findViewById(R.id.epass);
		ip = (EditText) findViewById(R.id.eip);
		port = (EditText) findViewById(R.id.eport);


		/* Button listener */
		Button bAdd = (Button) findViewById(R.id.add);
		bAdd.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				String vId = id.getText().toString();
				String vLogin = login.getText().toString();
				String vPass = pass.getText().toString();
				String vIp = ip.getText().toString();
				String vPort = port.getText().toString();

				if (vId.equalsIgnoreCase("") 
						| vLogin.equalsIgnoreCase("")
						| vPass.equalsIgnoreCase("")
						| vIp.equalsIgnoreCase("")
						| vPort.equalsIgnoreCase("") )
					return;

		
				Camera tmp = new Camera(vId, vLogin, vPass, vIp, Integer.parseInt(vPort));
			
				Intent outData = new Intent();
				Bundle objetbunble = new Bundle();
				objetbunble.putSerializable("camera", tmp);
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
				objetbunble.putSerializable("camera", null);
				outData.putExtras(objetbunble);
				setResult(RESULT_CANCELED, outData);
				finish();
			}
		});

	}
}