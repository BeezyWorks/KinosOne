package com.mattaniahbeezy.wisechildkinos;

import android.os.Bundle;
import android.view.Menu;
import android.widget.TextView;

public class Eicha extends MainActivity {
	TextView tv;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		content=R.layout.content_drawer;
		super.onCreate(savedInstanceState);
		tv = new TextView(this);
		tv.setText(R.string.Eicha);
		tv.setTypeface(tf);
		tv.setTextSize(size);
		if(style!=NIGHT)
			tv.setTextColor(getResources().getColor(android.R.color.primary_text_light));
		else
			tv.setTextColor(getResources().getColor(android.R.color.primary_text_dark));
		liz.addView(tv);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return super.onCreateOptionsMenu(menu);
	}
	@Override
	protected void zoomIn() {
		super.zoomIn();
		tv.setTextSize(size);
	}	
	@Override
	protected void zoomOut() {
		super.zoomOut();
		tv.setTextSize(size);
	}
}
