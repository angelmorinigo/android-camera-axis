package com.myapps;

import android.content.Context;

public class Astuces {
	
	private String[] s;	
	private int max = 5 ;
	private Context context;
	
	public Astuces(Context mContext) {
		context = mContext;
		s[0] = context.getString(R.string.astuce1);
		s[1] = context.getString(R.string.astuce2);
		s[2] = context.getString(R.string.astuce3);
		s[3] = context.getString(R.string.astuce4);
		s[4] = context.getString(R.string.astuce5);
		s[5] = context.getString(R.string.astuce6);
		s[6] = context.getString(R.string.astuce7);
		s[7] = context.getString(R.string.astuce8);
		s[8] = context.getString(R.string.astuce9);
		s[9] = context.getString(R.string.astuce10);
		s[10] = context.getString(R.string.astuce11);
		s[11] = context.getString(R.string.astuce12);
	}
	
	public String getLabel(int i) {
		return s[i];
	}
	
	public int getMax() {
		return max;
	}
	
}