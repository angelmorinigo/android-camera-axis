package com.myapps;
import java.util.Random;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/**
 * 
 * Describes welcome dialog displayed at the start of application
 *
 */
public class DialogWelcome extends Dialog {

	public interface ReadyListener {
        public void ready(String name);
    }
	
    private String cheats;
    private TextView text;

    /**
     * Constructor
     * @param context The context
     * @param theme The theme used
     */
    public DialogWelcome(Context context , int theme) {
        super(context,theme);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_welcome);
        setTitle(R.string.messageBienvenue);
        text = (TextView) findViewById(R.id.text);
        
        Random randomGenerator = new Random();
        Tricks a = new Tricks(getContext());
        int randomInt = randomGenerator.nextInt(100)%a.getMax();

        text.setText(R.string.messageAstuce);
        cheats = a.getLabel(randomInt);
        text.append(cheats);
   
        Button buttonOK = (Button) findViewById(R.id.ok);
        buttonOK.setOnClickListener(new OKListener());
        
        Button right = (Button) findViewById(R.id.arrow_right);
        right.setOnClickListener(new RightListener());
    }

    /**
     * 
     * Listener for "OK" button
     *
     */
    private class OKListener implements android.view.View.OnClickListener {
        @Override
        public void onClick(View v) {
            DialogWelcome.this.dismiss();
        }
    }
    
    /**
     * 
     * Listener for "Next" button
     *
     */
    private class RightListener implements android.view.View.OnClickListener {
        @Override
        public void onClick(View v) {
            Random randomGenerator = new Random();
            Tricks a = new Tricks(getContext());
            int randomInt = randomGenerator.nextInt(100)%a.getMax();
            text.setText(R.string.messageAstuce);
            cheats = a.getLabel(randomInt);
            text.append(cheats);
        }
    }

}
