package com.myapps;

import android.content.Context;

/**
 * Implement a set of tricks
 */
public class Tricks {
	private String[] s;
	private int max = 12;
	private Context context;

	public Tricks(Context mContext) {
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