package com.mattaniahbeezy.wisechildkinos;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.NumberPicker;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.ViewSwitcher.ViewFactory;

public class Halacha extends MainActivity {
	CharSequence[] text;
	TextSwitcher tv;
	private static final String SIMAN="siman";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		content=R.layout.content_drawer;
		super.onCreate(savedInstanceState);
		getActionBar().setDisplayShowTitleEnabled(false);
		initSwitchers();


		text = getResources().getTextArray(R.array.halacha);	
		tv.setText(text[sharedPref.getInt(SIMAN, 0)]);

	}


	private void simanPicker() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Choose Siman");
		final NumberPicker simanim = new NumberPicker(this);
		simanim.setMaxValue(12);
		simanim.setDisplayedValues(getResources().getStringArray(R.array.simanim));
		simanim.setValue(sharedPref.getInt(SIMAN, 0));
		builder.setView(simanim);
		builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				sharedPref.edit().putInt(SIMAN, simanim.getValue()).apply();
				tv.setText(text[sharedPref.getInt(SIMAN, 0)]);
				invalidateOptionsMenu();
			}
		});
		builder.create().show();
	}

	protected void previousActivity(){
		int siman=sharedPref.getInt(SIMAN, 1)-1;
		if(siman==-1){
			siman=12;
		}
		
		sharedPref.edit().putInt(SIMAN, siman).commit();	
		tv.setInAnimation(prevInAn);tv.setOutAnimation(prevOutAn);
		tv.setText(text[sharedPref.getInt(SIMAN, 1)]);
		scroller.post(new Runnable() {

			@Override
			public void run() {
				scroller.scrollTo(0, 0);

			}
		});
		invalidateOptionsMenu();
	}


	@Override
	protected void nextActivity() {
		int siman=sharedPref.getInt(SIMAN, 1)+1;
		if(siman==13){
			siman=0;
		}
		sharedPref.edit().putInt(SIMAN, siman).commit();
		tv.setInAnimation(inAn);tv.setOutAnimation(outAn);
		tv.setText(text[sharedPref.getInt(SIMAN, 1)]);
		scroller.post(new Runnable() {

			@Override
			public void run() {
				scroller.scrollTo(0, 0);

			}
		});
		invalidateOptionsMenu();
	}

	private void initSwitchers(){
		scroller.removeAllViews();
		initLayout();
		tv=new TextSwitcher(Halacha.this);
		tv.setMeasureAllChildren(false);
		liz.addView(tv, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		initBody();
	}

	private void initBody(){
		tv.removeAllViews();
		tv.setFactory(new ViewFactory(){

			public View makeView(){
				TextView textView = new TextView(Halacha.this);
				textView.setTypeface(tf);
				textView.setTextSize(size);
				if(style==NIGHT)
					textView.setTextColor(getResources().getColor(android.R.color.primary_text_dark));
				else
					textView.setTextColor(getResources().getColor(android.R.color.primary_text_light));

				return textView;
			}
		});
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		CharSequence[] simanim=getResources().getTextArray(R.array.simanim);
		MenuItem item = menu.findItem(R.id.title);
		item.setVisible(true);
		item.setTitle(simanim[sharedPref.getInt(SIMAN, 0)]);
		return super.onPrepareOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if(item.getItemId()==R.id.title)
			simanPicker();
		return super.onOptionsItemSelected(item);
	}
	
	public boolean onCreateOptionsMenu(Menu menu){
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main, menu);

		return super.onCreateOptionsMenu(menu);
	}
	@Override
	protected void onDestroy() {
		sharedPref.edit().putInt("halachaY", scroller.getScrollY()).apply();
		super.onDestroy();
	}
	@Override
	protected void onPause() {
		sharedPref.edit().putInt("halachaY", scroller.getScrollY()).apply();
		super.onPause();
	}
	@Override
	protected void onStop() {
		sharedPref.edit().putInt("halachaY", scroller.getScrollY()).apply();
		super.onStop();
	}
	@Override
	protected void onPostCreate(Bundle savedInstanceState) {

		scroller.post(new Runnable() {

			@Override
			public void run() {
				scroller.smoothScrollTo(0, sharedPref.getInt("halachaY", 0));
			}
		});
		super.onPostCreate(savedInstanceState);
	}

	@Override
	protected void zoomIn() {
		super.zoomIn();
		tv.setInAnimation(null);
		tv.setOutAnimation(null);
		initBody();
		tv.setText(text[sharedPref.getInt(SIMAN, 1)]);
	}

	@Override
	protected void zoomOut() {
		super.zoomOut();
		tv.setInAnimation(null);
		tv.setOutAnimation(null);
		initBody();
		tv.setText(text[sharedPref.getInt(SIMAN, 1)]);
	}

}
