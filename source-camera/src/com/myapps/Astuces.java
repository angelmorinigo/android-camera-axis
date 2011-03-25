package com.myapps;

import android.content.Context;

public class Astuces {

	private String[] s;
	private int max = 12;
	private Context context;

	public Astuces(Context mContext) {
		context = mContext;
		s = new String[max];
		for (int i = 0; i < max; i++)
			s[i] = context.getString(R.string.astuce1 + i);
	}

	public String getLabel(int i) {
		return s[i];
	}

	public int getMax() {
		return max;
	}

}