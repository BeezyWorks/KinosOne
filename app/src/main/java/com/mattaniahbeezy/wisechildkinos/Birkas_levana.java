
package com.mattaniahbeezy.wisechildkinos;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.TextView;

public class Birkas_levana extends MainActivity {
	protected void onCreate(Bundle savedInstanceState) {
		content=R.layout.content_drawer;
		super.onCreate(savedInstanceState);
		
		TextView tv = new TextView(this);
		tv.setTypeface(tf);
		tv.setTextSize(size);
		if(style!=NIGHT)
			tv.setTextColor(getResources().getColor(android.R.color.primary_text_light));
		else
			tv.setTextColor(getResources().getColor(android.R.color.primary_text_dark));
		liz.addView(tv);
		
		switch(nusach){
		case ASHKENAZ:
			tv.setText(R.string.Kidush_levana);
			break;
		case STUPID:
			tv.setText(R.string.kidush_levana_stupid);
			break;
		case SFARDI:
			tv.setText(R.string.kiddush_levana_sfardi);
			break;

		}
	}

	

	public boolean onCreateOptionsMenu(Menu menu){
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main, menu);

		return super.onCreateOptionsMenu(menu);
	}
}
