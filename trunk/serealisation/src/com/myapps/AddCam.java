package com.myapps;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class AddCam extends Activity {
	/** Called when the activity is first created. */
	public EditText id, login, pass, ip, port;
	//private String protocol;
	private String logTag = "AppLog";
	private Context context;
	public Spinner s;

	/* Definition de la camera */

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		context = this;
		id = (EditText) findViewById(R.id.eid);
		login = (EditText) findViewById(R.id.elogin);
		pass = (EditText) findViewById(R.id.epass);
		ip = (EditText) findViewById(R.id.eip);
		port = (EditText) findViewById(R.id.eport);

		/* Spinner listener (ComboBox) */
		s = (Spinner) findViewById(R.id.spinner);
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
				context, R.array.protocol_array,
				android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		s.setAdapter(adapter);
	/*	s.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// protocol = arg0.getItemAtPosition(arg2).toString();
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// protocol = "";
			}
		});

		/* Buttons listener */
		Button bAdd = (Button) findViewById(R.id.add);
		bAdd.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				String vId = id.getText().toString();
				String vLogin = login.getText().toString();
				String vPass = pass.getText().toString();
				String vIp = ip.getText().toString();
				String vPort = port.getText().toString();
				String vProtocol = s.getSelectedItem().toString();

				if (vId.equalsIgnoreCase("") | vLogin.equalsIgnoreCase("")
						| vPass.equalsIgnoreCase("") | vIp.equalsIgnoreCase("")
						| vPort.equalsIgnoreCase(""))
					return;

				Camera tmp = new Camera(vId, vLogin, vPass, vIp, Integer
						.parseInt(vPort), vProtocol);

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