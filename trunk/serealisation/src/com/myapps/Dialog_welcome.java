package com.myapps;
import android.R.id;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class Dialog_welcome extends Dialog{

	public interface ReadyListener {
        public void ready(String name);
    }

    private String name;
    private ReadyListener readyListener;
    EditText etName;

    public Dialog_welcome(Context context, String name) {
        super(context);
        this.name = name;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_welcome);
        setTitle(name);
        TextView text = (TextView) findViewById(R.id.text);
        text.setText("Astuce : Appuyez sur la touche Menu pour accéder aux options");
        Button buttonOK = (Button) findViewById(R.id.ok);
        buttonOK.setOnClickListener(new OKListener());
    }

    private class OKListener implements android.view.View.OnClickListener {
        @Override
        public void onClick(View v) {
            //readyListener.ready(String.valueOf(etName.getText()));
            Dialog_welcome.this.dismiss();
        }
    }

}
