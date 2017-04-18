package com.mattaniahbeezy.wisechildkinos;

import java.util.Calendar;

import android.app.ActionBar;
import android.app.ActionBar.OnNavigationListener;
import android.content.Context;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import android.widget.TextView.BufferType;
import android.widget.ViewSwitcher;

public class MaarivActivity extends MainActivity{
	private ViewSwitcher switcher;
	protected String[] spinnerNav;

	private LinearLayout[] lolz = new LinearLayout[5];
	private Context context=MaarivActivity.this;	
	public TextView[][] tvs;

	public static final int SHMA=0;
	public static final int AMIDA=1;
	public static final int SIYUM=2;
	public static final int MEGILA=3;
	public static final int EICHA=4;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		content=R.layout.content_drawer;
		super.onCreate(savedInstanceState);
		switcher=new ViewSwitcher(context);
		scroller.removeAllViews();
		scroller.addView(switcher);
		getIntent().putExtra("maariv", true);
		spinnerNav=initSpinnerNavItem();


		tvs=new TextView[spinnerNav.length][27];

		for(int i=0; i<spinnerNav.length; i++)
			preparePrayers(i);
		initSpinner();
		silence();
	}

	private void preparePrayers(int position) {
		switch(determineOrder(position)){
		case SHMA:
			initShma(position);
			break;
		case AMIDA:
			initAmida(position);
			break;
		case SIYUM:
			initSiyum(position);
			break;
		case EICHA:
			initEicha(position);
			break;
		}

	}
	private void showPrayer(int position){
		invalidateOptionsMenu();
		try{
			switcher.addView(lolz[position], new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		}
		catch(IllegalStateException e){

		}
		switcher.showNext();
		if(switcher.getNextView()!=null)
			switcher.removeView(switcher.getNextView());
		scroller.smoothScrollTo(0, 0);

		switcher.setInAnimation(null);
		switcher.setOutAnimation(null);
		if(getIntent().getBooleanExtra("restart", false)){
			scroller.post(new Runnable() {
				@Override
				public void run() {
					scroller.scrollTo(0, getIntent().getIntExtra("restartY", 0));
				}
			});
			getIntent().removeExtra("restart");
		}
	}

	private void postLayouts(int position){
		for(int i=0; i<tvs[position].length; i++){
			if( tvs[position][i].getText().length()<1)
				tvs[position][i].setVisibility(View.GONE);
		}
	}

	private void initEicha(int position) {
		initLayout(position);
		switch(nusach){
		case ASHKENAZ:
			tvs[position][0].setText(R.string.kadish_shaleim);
			break;
		case STUPID:
			tvs[position][0].setText(R.string.kadish_shaleim_stupid);
			break;
		case SFARDI:
			tvs[position][0].setText(R.string.kadish_shaleim_sfardi);
			break;
		}
		tvs[position][1].setText(R.string.Eicha);
		postLayouts(position);
	}


	private void initSiyum(int position) {
		initLayout(position);
		switch (nusach){
		case ASHKENAZ:
			setText(tvs[position][0], R.string.kadish_shaleim, R.string.kadish_shaleim_english, context);
			setText(tvs[position][1], R.string.aleinu, R.string.aleinu_english, context);
			setText(tvs[position][2], R.string.kadish_yesom, R.string.kadish_yesom_english, context);
			break;

		case STUPID:
			tvs[position][0].setText(R.string.kadish_shaleim_stupid);
			tvs[position][1].setText(R.string.shir_lmaalos_stupid);
			tvs[position][6].setText(R.string.aleinu);
			tvs[position][7].setText(R.string.kadish_yesom_stupid);
			break;

		case SFARDI:
			tvs[position][0].setText(R.string.kadish_shaleim_sfardi);
			tvs[position][1].setText(R.string.shir_lmaalos_sfardi);
			tvs[position][6].setText(R.string.aleinu);
			tvs[position][7].setText(R.string.kadish_yesom_sfardi);
			break;
		}
		postLayouts(position);
	}

	private void initAmida(final int position) {
		String ploniName = getResources().getString(R.string.ploni);
		initLayout(position);
		switch (nusach){
		case ASHKENAZ:
			//			Avos
			setText(tvs[position][0], R.string.Avos, R.string.avos_english, context);

			//		Gevuros
			setText(tvs[position][1], R.string.Gevuros_summer, R.string.gevuros_english_summer, context);
			setText(tvs[position][4], R.string.kodesh, R.string.kedushashashem_english, context);

			//		Daas
			setText(tvs[position][5], R.string.daas, R.string.daas_english, context);
			if(jDate.getDayOfWeek()==7){
				setText(tvs[position][5], R.string.daas_motzash, R.string.daas_english, context);
			}

			//		HaShiveinu through Re'eh 
			setText(tvs[position][6], R.string.hashiveinu, R.string.hashiveinu_english, context);
			setText(tvs[position][7], R.string.slach_lanu, R.string.slach_english, context);
			setText(tvs[position][8], R.string.reeh_na, R.string.reeh_na_english, context);


			//		Rephaeinu
			setText(tvs[position][10], R.string.rephaeinu, R.string.rephaeinu_english, context);
			sickPrayer(tvs[position][10], context);
			if(!sharedPref.getString("sickNames", ploniName).equals(ploniName))
				tvs[position][10].setText(tefilaBadCholeh(), BufferType.SPANNABLE);

			//		Barech Aleinu
			setText(tvs[position][11], R.string.Barech_summer, R.string.barech_english_summer, context);

			//		Teka B'Shofar
			setText(tvs[position][12], R.string.Teka_beshofar, R.string.teka_english, context);

			//		Hashiva Shofteinu
			setText(tvs[position][13], R.string.Hashiva, R.string.hashiva_english, context);

			//		Birkas HaMinim through Shma Koleinu
			setText(tvs[position][14], getMinimText(), R.string.minim_english, context);
			tvs[position][14].append("*");
			tvs[position][14].setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					sharedPref.edit().putBoolean("originalMinim", !sharedPref.getBoolean("originalMinim", false)).apply();
					setText(tvs[position][14], getMinimText(), R.string.minim_english, context);
				}
			});
			setText(tvs[position][15], R.string.tzadikim, R.string.tzadikim_english, context);
			setText(tvs[position][16], R.string.Yerushaleim_regular, R.string.yerushaleim_english, context);
			setText(tvs[position][17], R.string.Es_tzemach, R.string.estzemach_english, context);
			setText(tvs[position][18], R.string.Shma_Koleinu, R.string.shma_koleinu_english, context);
			extraPrayer(tvs[position][18], R.string.shma_koleinu_long);

			//		Retzei
			setText(tvs[position][19], R.string.retzei, R.string.retzei_english, context);

			//		Modim
			setText(tvs[position][20], R.string.Modim, R.string.modim_english, context);


			//		Shalom
			setText(tvs[position][21], R.string.Shalom_rav, R.string.shalom_english, context);

			setText(tvs[position][22], R.string.Elohai_natzor, R.string.elohai_natzor_english, context);
			break;

		case STUPID:
			//			Avos
			tvs[position][0].setText(R.string.Avos);

			//		Gevuros
			tvs[position][1].setText(R.string.Gevuros_summer);

			//		Kedusha
			tvs[position][3].setText(R.string.kodesh_stupid);

			//		Daaas through Re'eh 
			tvs[position][4].setText(R.string.daas_stupid);
			if(jDate.getDayOfWeek()==7){
				tvs[position][4].setText(R.string.daas_motzash_stupid);
			}
			tvs[position][5].setText(R.string.hashiveinu_stupid);
			tvs[position][6].setText(R.string.slach_lanu_stupid);
			tvs[position][7].setText(R.string.reeh_na_stupid);


			//		Rephaeinu
			tvs[position][9].setText(R.string.rephaeinu_stupid);
			sickPrayer(tvs[position][9], context);
			if(!sharedPref.getString("sickNames", ploniName).equals(ploniName))
				tvs[position][9].setText(tefilaBadCholeh(), BufferType.SPANNABLE);

			//		Barech Aleinu
			tvs[position][10].setText(R.string.barech_summer_stupid);

			//		Teka B'Shofar
			tvs[position][11].setText(R.string.teka_stupid);

			//		Hashiva Shofteinu
			tvs[position][12].setText(R.string.hashiva_stupid);

			//		Birkas HaMinim through Shma Koleinu
			tvs[position][13].setText(R.string.minim_stupid);
			tvs[position][14].setText(R.string.tzadikim_stupid);
			tvs[position][15].setText(R.string.yerushaleim_stupid);
			tvs[position][16].setText(R.string.es_tzemach_stupid);
			tvs[position][17].setText(R.string.shmaKoleinu_stupid);
			extraPrayer(tvs[position][17], R.string.shmaKoleinu_stupid_long);

			//		Retzei
			tvs[position][18].setText(R.string.retzei_stupid);

			//		Modim
			tvs[position][19].setText(R.string.modim_stupid);

			//		Shalom
			tvs[position][20].setText(R.string.shalom_stupid);

			tvs[position][21].setText(R.string.elohai_stupid);
			break;

		case SFARDI:
			//			Avos
			tvs[position][0].setText(R.string.Avos);

			//		Gevuros
			tvs[position][1].setText(R.string.Gevuros_summer);
			tvs[position][3].setText(R.string.kodesh);

			//		Daaas through Re'eh 
			tvs[position][4].setText(R.string.daas_sfardi);
			if(jDate.getDayOfWeek()==7){
				tvs[position][4].setText(R.string.daas_motzash_sfardi);
			}
			tvs[position][5].setText(R.string.hashiveinu_stupid);
			tvs[position][6].setText(R.string.slach_lanu_sfardi);
			tvs[position][7].setText(R.string.reeh_na_sfardi);


			//		Rephaeinu
			tvs[position][8].setText(R.string.refaeinu_sfardi);
			sickPrayer(tvs[position][8], context);
			if(!sharedPref.getString("sickNames", ploniName).equals(ploniName))
				tvs[position][8].setText(tefilaBadCholeh(), BufferType.SPANNABLE);

			//		Barech Aleinu
			tvs[position][9].setText(R.string.barech_summer_sfardi);

			//		Teka B'Shofar
			tvs[position][10].setText(R.string.teka_sfardi);

			//		Hashiva Shofteinu
			tvs[position][11].setText(R.string.hashiva_sfardi);

			//		Birkas HaMinim through Shma Koleinu
			tvs[position][12].setText(R.string.minim_sfardi);

			tvs[position][13].setText(R.string.tzadikim_sfardi);

			tvs[position][14].setText(R.string.yerushaleim_sfardi);

			tvs[position][15].setText(R.string.es_tzemach_sfardi);

			tvs[position][16].setText(R.string.shma_koleinu_sfardi);

			//		Retzei
			tvs[position][17].setText(R.string.retzei_sfardi);

			//		Modim
			tvs[position][18].setText(R.string.modim_sfardi);

			//		Shalom
			tvs[position][19].setText(R.string.shalom_sfardi);

			tvs[position][20].setText(R.string.elohai_sfardi);
			break;
		}
		postLayouts(position);
	}

	private void initShma(int position) {
		initLayout(position);
		switch(nusach){
		case ASHKENAZ:
			setText(tvs[position][0], R.string.maariv_shma1, R.string.maariv_shma1_english, context);
			setText(tvs[position][1], R.string.maariv_shma2, R.string.maariv_shma2_english, context);
			setText(tvs[position][2], R.string.maariv_shma3, R.string.maariv_shma3_english, context);
			setText(tvs[position][3], R.string.kriasShma1, R.string.kriasShma1_english, context);
			setText(tvs[position][4], R.string.kriasShma2, R.string.kriasShma2_english, context);
			setText(tvs[position][5], R.string.kriasShma3, R.string.kriasShma3_english, context);
			setText(tvs[position][6], R.string.maariv_shma4, R.string.maariv_shma4_english, context);
			setText(tvs[position][7], R.string.maariv_shma5, R.string.maariv_shma5_english, context);
			setText(tvs[position][8], R.string.maariv_shma6, R.string.maariv_shma6_english, context);
			setText(tvs[position][10], R.string.chatzi_kadish, R.string.chatzi_kadish_english, context);

			if(sharedPref.getBoolean("modernAsh", false)){
				setText(tvs[position][9], R.string.maariv_shma7, 0, context);
			}
			break;
		case STUPID:
			tvs[position][0].setText(R.string.maariv_shma_stupid);
			tvs[position][1].setText(R.string.maariv_shma7);
			tvs[position][2].setText(R.string.chatzi_kadish_stupid);
			if(sharedPref.getBoolean("inIsrael", false)){
				tvs[position][1].setVisibility(View.GONE);
			}
			break;
		case SFARDI:
			if(jTomorrow.isRoshChodesh()){
				tvs[position][0].setText(R.string.barchi_nafshi);
				tvs[position][1].setText(R.string.maariv_shma_sfardi);
			}
			tvs[position][0].setText(R.string.maariv_shma_sfardi);
			tvs[position][1].setText(R.string.chatzi_kadish_sfardi);			
			break;
		}
		postLayouts(position);

	}

	private void initLayout(int position) {
		lolz[position]=new LinearLayout(context);
		lolz[position].setPadding(PADDING, PADDING, PADDING, PADDING);
		lolz[position].setOrientation(LinearLayout.VERTICAL);
		switch(style){
		case SEPIA:
			lolz[position].setBackgroundColor(getResources().getColor(R.color.sepia));
			break;
		case NIGHT:
			lolz[position].setBackgroundColor(getResources().getColor(android.R.color.background_dark));
			break;
		case DAY:
			lolz[position].setBackgroundColor(getResources().getColor(android.R.color.background_light));
		}

		for(int i=0; i<tvs[position].length; i++){
			tvs[position][i]=new TextView(getApplicationContext());
			tvs[position][i].setTypeface(tf);
			tvs[position][i].setTextSize(size);
			if(style!=NIGHT){
				tvs[position][i].setTextColor(getResources().getColor(android.R.color.primary_text_light));
			}
			else{
				tvs[position][i].setTextColor(getResources().getColor(android.R.color.primary_text_dark));
			}
			lolz[position].addView(tvs[position][i]);
		}
	}

	private int determineOrder(int position) {
		switch(position){
		case 0:
			return SHMA;
		case 1:
			return AMIDA;
		case 2:
			return EICHA;
		case 3:
			return SIYUM;
		}
		return 0;
	}

	private void initSpinner() {
		ActionBar actionBar=getActionBar();
		actionBar.setDisplayShowTitleEnabled(false);
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);

		String[] spinnerNavItems={};

		spinnerNavItems = initSpinnerNavItem();
		ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this,
				SPINNER_LAYOUT,
				spinnerNavItems);
		OnNavigationListener mOnNavigationListener = new OnNavigationListener() {
			public boolean onNavigationItemSelected(int position, long itemId) {
				if(!getIntent().getBooleanExtra(INHIBIT_ABAR, false))
					showPrayer(position);
				else
					getIntent().removeExtra(INHIBIT_ABAR);

				return true;
			}

		};
		actionBar.setListNavigationCallbacks(spinnerAdapter, mOnNavigationListener);
		actionBar.setSelectedNavigationItem(getIntent().getIntExtra("restartPosition", 0));
		if(getIntent().getBooleanExtra("omer", false))
			actionBar.setSelectedNavigationItem(2);
	}

	private String[] initSpinnerNavItem() {
		return getResources().getStringArray(R.array.maarivArray);
	}

	@Override
	protected void nextActivity(){
		final ActionBar actionBar = getActionBar();
		final int currentPosition=actionBar.getSelectedNavigationIndex();
		switcher.setInAnimation(inAn);switcher.setOutAnimation(outAn);	
		if(currentPosition+1<spinnerNav.length){
			if(!actionBar.isShowing()){
				getIntent().putExtra(INHIBIT_ABAR, true);
				showPrayer(currentPosition+1);
			}	
			actionBar.setSelectedNavigationItem(currentPosition+1);
		}
	}
	protected void previousActivity(){
		final ActionBar actionBar = getActionBar();
		final int currentPosition=actionBar.getSelectedNavigationIndex();
		switcher.setInAnimation(prevInAn);switcher.setOutAnimation(prevOutAn);
		actionBar.setSelectedNavigationItem(currentPosition-1);
		if(currentPosition-1>-1){

			actionBar.setSelectedNavigationItem(currentPosition-1);
			if(!actionBar.isShowing()){
				showPrayer(currentPosition-1);
				getIntent().putExtra(INHIBIT_ABAR, true);
			}	
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		ActionBar actionBar = getActionBar();
		int currentSection=determineOrder(actionBar.getSelectedNavigationIndex());
		if(currentSection==SHMA)
			getMenuInflater().inflate(R.menu.action_bar_first, menu);
		else if(currentSection==SIYUM)
			getMenuInflater().inflate(R.menu.action_bar_last, menu);
		else
			getMenuInflater().inflate(R.menu.action_bar_body, menu);
		return super.onCreateOptionsMenu(menu);
	}

	protected void restartActivity() {
		getIntent().removeExtra(INHIBIT_ABAR);
		getIntent().putExtra("restartPosition", getActionBar().getSelectedNavigationIndex());
		super.restartActivity();
	}
	protected void zoomOut(){
		super.zoomOut();
		for(int i=0; i<spinnerNav.length; i++){
			for(int j=0; j<tvs[i].length; j++){
				tvs[i][j].setTextSize(size);
			}
		}
	}

	protected void zoomIn(){
		super.zoomIn();
		for(int i=0; i<spinnerNav.length; i++){
			for(int j=0; j<tvs[i].length; j++){
				tvs[i][j].setTextSize(size);
			}
		}
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
		TextView[] lables = new TextView[3];
		for(int i=0;i<lables.length;i++){
			lables[i]=new TextView(MaarivActivity.this);
			lables[i].setTextSize(QUICK_ZMAN_SIZE);
		}
		setLableText(lables);
		setQuickZmanColor(lables);
		return lables;
	}

	private TextView[] quickZmanTimes(){
		TextView[] times = new TextView[3];
		for(int i=0;i<times.length;i++){
			times[i]=new TextView(MaarivActivity.this);
			times[i].setTextSize(QUICK_ZMAN_SIZE);
		}
		setQuickZmanColor(times);
		setQuickZmanTimesText(times);
		return times;
	}

	private void setQuickZmanTimesText(TextView[] tvs){
		tvs[0].setText(DateFormat.format(DATE_FORMAT, czc.getPlagHamincha()));
		tvs[1].setText(DateFormat.format(DATE_FORMAT, czc.getTzais()));
		tvs[2].setText(DateFormat.format(DATE_FORMAT, czc.getTzais72()));
	}

	private void setQuickZmanColor(TextView[] tvs){
		Calendar now = Calendar.getInstance();
		now.setTimeInMillis(System.currentTimeMillis());
		int red = getResources().getColor(android.R.color.holo_red_light);
		if(czc.getPlagHamincha().after(now.getTime()))
			tvs[0].setTextColor(red);
		if(czc.getTzais().after(now.getTime()))
			tvs[1].setTextColor(red);
		if(czc.getTzais72().after(now.getTime()))
			tvs[2].setTextColor(red);
	}

	private void setLableText(TextView[] lables){
		CharSequence[] zmanLables = getResources().getTextArray(R.array.dailyZmanim);
		lables[0].setText(zmanLables[10]);
		lables[1].setText(zmanLables[12]);
		lables[2].setText(zmanLables[13]);
	}
}
