package com.myapps;
import java.util.Random;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

public class Dialog_welcome extends Dialog{

	public interface ReadyListener {
        public void ready(String name);
    }
	
    private String cheats ;
    private TextView text ;

    
    public Dialog_welcome(Context context , int theme) {
        super(context,theme);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_welcome);
        setTitle("Bienvenue !") ;
        text = (TextView) findViewById(R.id.text);
        
        Random randomGenerator = new Random() ;
        Astuces a = new Astuces() ;
        int randomInt = randomGenerator.nextInt(100)%a.getMax();

        text.setText("Astuce : ") ;
        cheats = a.getLabel(randomInt) ;
        text.append(cheats) ;
   
        Button buttonOK = (Button) findViewById(R.id.ok);
        buttonOK.setOnClickListener(new OKListener());
        
        Button right = (Button) findViewById(R.id.arrow_right) ;
        right.setOnClickListener(new RightListener());
    }

    private class OKListener implements android.view.View.OnClickListener {
        @Override
        public void onClick(View v) {
            Dialog_welcome.this.dismiss();
        }
    }
    
    private class RightListener implements android.view.View.OnClickListener {
        @Override
        public void onClick(View v) {
            Random randomGenerator = new Random() ;
            Astuces a = new Astuces() ;
            int randomInt = randomGenerator.nextInt(100)%a.getMax();
            text.setText("Astuce : ") ;
            cheats = a.getLabel(randomInt) ;
            text.append(cheats) ;
        }
    }

}
