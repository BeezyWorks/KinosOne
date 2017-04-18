package com.mattaniahbeezy.wisechildkinos;

import android.app.ActionBar;
import android.app.ActionBar.OnNavigationListener;
import android.content.Context;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ViewSwitcher;

public class Bedtime extends MainActivity {	
	private LinearLayout[] lls = new LinearLayout[2];
	private TextView[] mitaTVs= new TextView[5];
	private TextView[] tikunTVs= new TextView[4];
	private Context context;
	private ViewSwitcher switcher;
	private int CURRENTLY_SHOWING=0;
	final static private int SHMA=0;
	final static private int TIKUN=1;

	protected void onCreate(Bundle savedInstanceState) {
		content=R.layout.content_drawer;
		context=getApplicationContext();
		getIntent().putExtra("bedtime", true);
		super.onCreate(savedInstanceState);
		initBedtimeLayout();
		alHaMita();
		tikunChatzos();
		initSpinner();
		getActionBar().hide();
	}
	
	
	
	private void initBedtimeLayout(){
		switcher=new ViewSwitcher(context);
		scroller.removeAllViews();
		scroller.addView(switcher);
		switcher.removeAllViews();
		LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		for(int i=0; i<mitaTVs.length; i++){
			mitaTVs[i]=new TextView(context);
			if(style!=NIGHT)
				mitaTVs[i].setTextColor(getResources().getColor(android.R.color.primary_text_light));
			else
				mitaTVs[i].setTextColor(getResources().getColor(android.R.color.primary_text_dark));
			
			mitaTVs[i].setTypeface(tf);
			mitaTVs[i].setTextSize(size);
		}
		for(int i=0; i<tikunTVs.length; i++){
			tikunTVs[i]=new TextView(context);
			if(style!=NIGHT)
				tikunTVs[i].setTextColor(getResources().getColor(android.R.color.primary_text_light));
			else
				tikunTVs[i].setTextColor(getResources().getColor(android.R.color.primary_text_dark));
			
			tikunTVs[i].setTypeface(tf);
			tikunTVs[i].setTextSize(size);
		}
		for(int i=0; i<lls.length; i++){
			lls[i]= new LinearLayout(context);
			lls[i].setOrientation(LinearLayout.VERTICAL);
			lls[i].setPadding(15, 15, 15, 15);
			switch(style){
			case SEPIA:
				lls[i].setBackgroundColor(getResources().getColor(R.color.sepia));
				scroller.setBackgroundColor(getResources().getColor(R.color.sepia));
				switcher.setBackgroundColor(getResources().getColor(R.color.sepia));
				break;
			case NIGHT:
				lls[i].setBackgroundColor(getResources().getColor(android.R.color.background_dark));
				scroller.setBackgroundColor(getResources().getColor(android.R.color.background_dark));
				switcher.setBackgroundColor(getResources().getColor(android.R.color.background_dark));
				break;
			case DAY:
				lls[i].setBackgroundColor(getResources().getColor(android.R.color.background_light));
				scroller.setBackgroundColor(getResources().getColor(android.R.color.background_light));
				switcher.setBackgroundColor(getResources().getColor(android.R.color.background_light));
				break;
			}
		}
		switcher.addView(lls[0],0, params);switcher.addView(lls[1], 1, params);
	}

	private void initSpinner() {
		ActionBar actionBar=getActionBar();
		actionBar.setDisplayShowTitleEnabled(false);
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
		String[] spinnerNav = getResources().getStringArray(R.array.bedtimeArray);
		ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this,
				SPINNER_LAYOUT,
				spinnerNav);
		OnNavigationListener mOnNavigationListener = new OnNavigationListener() {
			public boolean onNavigationItemSelected(int position, long itemId) {
				switch (position) {
				case 0:
					if(switcher.getDisplayedChild()==1){
						switcher.setInAnimation(prevInAn);
						switcher.setOutAnimation(prevOutAn);
						switcher.showNext();
					}
					CURRENTLY_SHOWING=SHMA;
					break;
				case 1:
					CURRENTLY_SHOWING=TIKUN;
					if(switcher.getDisplayedChild()==0){
						switcher.setInAnimation(inAn);
						switcher.setOutAnimation(outAn);
						switcher.showNext();
					}
					break;
				default:
					break;
				}
				invalidateOptionsMenu();
				return true;
			}
		};
		actionBar.setListNavigationCallbacks(spinnerAdapter, mOnNavigationListener);
	}

	private void alHaMita(){
		switch(nusach){
		case ASHKENAZ:
			mitaTVs[0].setText(R.string.Bedtime);
			break;
		case SFARDI:
				mitaTVs[0].setText(R.string.bedtime_sfardi_notachanun);
			break;
		case STUPID:
			mitaTVs[0].setText(R.string.bedtime_stupid);
			mitaTVs[1].setText(getResources().getStringArray(R.array.tehilim)[0]+"\n");
			mitaTVs[2].setText(getResources().getStringArray(R.array.tehilim)[1]+"\n");
			mitaTVs[3].setText(getResources().getStringArray(R.array.tehilim)[2]+"\n");
			mitaTVs[4].setText(getResources().getStringArray(R.array.tehilim)[3]);
			break;
		}
		for(int i=0; i<mitaTVs.length; i++){
			if(mitaTVs[i].getText().length()>0){
				lls[0].addView(mitaTVs[i], new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
			}
		}
	}

	private void tikunChatzos(){
		if(!noRochel()){
			setText(tikunTVs[0],R.string.tikun_rochel, 0, context);
			if(nusach==SFARDI){
				setText(tikunTVs[0],R.string.tikun_rochel_sfardi, 0, context);
			}
		}
		else {
			setText(tikunTVs[0], R.string.noRochel, 0, context);

			tikunTVs[0].setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					setText(tikunTVs[0],R.string.tikun_rochel, 0, context);
					if(nusach==SFARDI){
						setText(tikunTVs[0],R.string.tikun_rochel_sfardi, 0, context);
					}
					tikunTVs[0].setOnTouchListener(gestureListener);

				}
			});


		}

		if(!noLeah()){
			setText(tikunTVs[1], R.string.tikun_leah_sfardi1, 0, context);
			if(noRochel()){
				setText(tikunTVs[3], R.string.tikun_leah_sfardi2_noRochel, 0, context);
			}
			else{
				setText(tikunTVs[3], R.string.tikun_leah_sfardi2, 0, context);
			}
		}
		else{
			setText(tikunTVs[1], R.string.noLeah, 0, context);
		}
		for(int i=0; i<tikunTVs.length; i++){
			if(tikunTVs[i].getText().length()>0){
				lls[1].addView(tikunTVs[i], new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
			}
		}
	}

	private boolean noLeah(){
		return true;
	}
	private boolean noRochel(){
		return false;
	}

	@Override
	protected void nextActivity() {
		if(switcher.getDisplayedChild()==0){
			getActionBar().setSelectedNavigationItem(1);
			switcher.setInAnimation(inAn);
			switcher.setOutAnimation(outAn);
			switcher.showNext();
			CURRENTLY_SHOWING=TIKUN;
			invalidateOptionsMenu();
		}
	}
	
	@Override
	protected void previousActivity() {
		if(switcher.getDisplayedChild()==1){
			getActionBar().setSelectedNavigationItem(0);
			switcher.setInAnimation(prevInAn);
			switcher.setOutAnimation(prevOutAn);
			switcher.showNext();
			CURRENTLY_SHOWING=SHMA;
			invalidateOptionsMenu();
		}
	}
	
	public boolean onCreateOptionsMenu(Menu menu){
		MenuInflater inflater = getMenuInflater();
		switch(CURRENTLY_SHOWING){
		case SHMA:
			inflater.inflate(R.menu.action_bar_first, menu);
			break;
		case TIKUN:
			inflater.inflate(R.menu.action_bar_last, menu);
			break;
		}

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	protected View quickZmanimLablesLayout(LinearLayout linearLayout) {
		linearLayout.setPadding(PADDING, QUICK_ZMAN_TOP_PADDING, PADDING, 0);
		linearLayout.setOrientation(LinearLayout.VERTICAL);
		TextView[] tvs = quickZmanLables();
		for(int i=0;i<tvs.length;i++)
			linearLayout.addView(tvs[i]);
		return linearLayout;
	}
	
	@Override
	protected View quickZmanimTimesLayout(LinearLayout linearLayout) {
		linearLayout.setPadding(PADDING, QUICK_ZMAN_TOP_PADDING, PADDING, 0);
		linearLayout.setOrientation(LinearLayout.VERTICAL);
		TextView[] tvs = quickZmanTimes();
		for(int i=0;i<tvs.length;i++)
			linearLayout.addView(tvs[i]);
		return linearLayout;
	}
	
	private TextView[] quickZmanLables(){
		TextView[] lables = new TextView[2];
		for(int i=0;i<lables.length;i++){
			lables[i]=new TextView(Bedtime.this);
			lables[i].setTextSize(QUICK_ZMAN_SIZE);
		}
		setLableText(lables);
		return lables;
	}
	
	private TextView[] quickZmanTimes(){
		TextView[] times = new TextView[2];
		for(int i=0;i<times.length;i++){
			times[i]=new TextView(Bedtime.this);
			times[i].setTextSize(QUICK_ZMAN_SIZE);
		}
		setQuickZmanTimesText(times);
		return times;
	}
	
	private void setQuickZmanTimesText(TextView[] tvs){
		tvs[0].setText(DateFormat.format(DATE_FORMAT, czc.getChatzos()));
		tvs[1].setText(DateFormat.format(DATE_FORMAT, czc.getAlosHashachar()));
	}
	
	
	private void setLableText(TextView[] lables){
		CharSequence[] zmanLables = getResources().getTextArray(R.array.dailyZmanim);
		lables[0].setText(zmanLables[0]);
		lables[1].setText(zmanLables[7]);
	}
}
