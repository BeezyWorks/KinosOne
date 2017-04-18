package com.mattaniahbeezy.wisechildkinos;

import java.util.Date;

import android.app.ActionBar;
import android.app.ActionBar.OnNavigationListener;
import android.content.Context;
import android.content.res.Resources;
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

public class MinchaActivity extends MainActivity {
	protected String[] spinnerNav = {};
	private ViewSwitcher switcher;
	private LinearLayout[] lolz = new LinearLayout[6];
	public TextView[][] tvs;
	private Context context = MinchaActivity.this;

	public static final int KORBANOS=0;
	public static final int ASHREI=1;
	public static final int KRIA=2;
	public static final int AMIDA=3;
	public static final int TACHANUN=4;
	public static final int ALEINU=5;
	public static final int SHIR_HASHIRIM=6;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		content=R.layout.content_drawer;
		super.onCreate(savedInstanceState);
		switcher=new ViewSwitcher(context);
		scroller.removeAllViews();
		scroller.addView(switcher);
		setSpinnerNav();
		tvs=new TextView[spinnerNav.length][27];
		for(int i=0; i<spinnerNav.length; i++)
			preparePrayers(i);
		Spinner();
		silence();


	}

	private void Spinner() {
		getIntent().putExtra("mincha", true);
		ActionBar actionBar=getActionBar();
		actionBar.setDisplayShowTitleEnabled(false);
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);



		ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this,
				SPINNER_LAYOUT,
				spinnerNav);
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
	}

	private void preparePrayers(int position){
		switch(determineOrder(position)){
		case KORBANOS:
			initKorbanos(position);
			break;
		case ASHREI:
			initAshrei(position);
			break;
		case KRIA:
			initKria(position);
			break;
		case AMIDA:
			initAmida(position);
			break;
		case ALEINU:
			initAleinu(position);
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

	private void setSpinnerNav() {
		Resources res = getResources();
		spinnerNav=res.getStringArray(R.array.Mincha_kria_noTachanun);
	}

	private int determineOrder(int position){
		switch(position){
		case 0:
			return KORBANOS;
		case 1:
			return ASHREI;
		case 2:
			return KRIA;
		case 3:
			return AMIDA;
		case 4:
			return ALEINU;
		}
		return KORBANOS;
	}

	private void Layouts(int position){
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
	private void postLayouts(int position){
		for(int i=0; i<tvs[position].length; i++){
			if( tvs[position][i].getText().length()<1)
				tvs[position][i].setVisibility(View.GONE);
		}
	}

	private void shirShelYom(int position){
		switch (jDate.getDayOfWeek()) {
		case 1:
			tvs[position][1].setText(R.string.sunday);
			break;
		case 2:
			tvs[position][1].setText(R.string.monday);
			break;
		case 3:
			tvs[position][1].setText(R.string.tuesday);
			break;
		case 4:
			tvs[position][1].setText(R.string.wednesday);
			break;
		case 5:
			tvs[position][1].setText(R.string.thursday);
			break;
		case 6:
			tvs[position][1].setText(R.string.friday);
			break;
		default:
			break;
		}
	}

	private void initKorbanos(int position){
		Layouts(position);
		shirShelYom(position);

		switch(nusach){
		case ASHKENAZ:
			tvs[position][0].setText(getResources().getText(R.string.birkashashachar8));
			tvs[position][2].setText(R.string.kadish_yesom);
			break;
		case STUPID:
			tvs[position][0].setText(R.string.talis_stupid);
			tvs[position][2].setText(R.string.stupid_yom_extra);
			tvs[position][3].setText(R.string.kadish_yesom_stupid);
			break;
		case SFARDI:
			tvs[position][0].setText(R.string.talis_sfardi);
			tvs[position][2].setText(R.string.stupid_yom_extra);
			tvs[position][3].setText(R.string.kadish_yesom_sfardi);
			break;
		}


		switch(nusach){
		case ASHKENAZ:
			tvs[position][4].setText(R.string.korbanos_noBrocha);
			break;
		case STUPID:
			tvs[position][4].setText(R.string.mincha_korbanos_stupid);
			break;
		case SFARDI:
			tvs[position][4].setText(R.string.korbanos_mincha_sfardi);
			break;
		}

		postLayouts(position);
	}

	private void initAshrei(int position){
		Layouts(position);
		switch(nusach){
		case ASHKENAZ:
			setText(tvs[position][0], R.string.ashrei, R.string.ashrei_english, context);
			setText(tvs[position][1], R.string.chatzi_kadish, R.string.chatzi_kadish_english, context);
			break;
		case STUPID:
			tvs[position][0].setText(R.string.ashrei);
			tvs[position][1].setText(R.string.chatzi_kadish_stupid);
			break;
		case SFARDI:
			tvs[position][0].setText(R.string.ashrei_sfardi_mincha);
			tvs[position][1].setText(R.string.chatzi_kadish_sfardi);
			break;
		}
		postLayouts(position);
	}

	private void initKria(int position){
		Layouts(position);
		tvs[position][0].setText(R.string.vayehi_binso_haron);

		tvs[position][1].setText(R.string.vayachel);

		tvs[position][2].setText(R.string.mincha_kria_2);

		tvs[position][3].setText(R.string.chatzi_kadish);

		postLayouts(position);
	}

	private void initAmida(final int position){
		Layouts(position);
		String ploniName=getResources().getString(R.string.ploni);
		switch (nusach){
		case ASHKENAZ:
			//			Avos
			tvs[position][0].setText(R.string.Avos);

			//		Gevuros
			tvs[position][1].setText(R.string.Gevuros_summer);


			//		Kedusha
			tvs[position][2].setText(R.string.Kedusha);
			tvs[position][3].setText(R.string.Kedusha_chasima);

			tvs[position][2].setTextSize(size+SMALL_TEXT);
			tvs[position][3].setTextSize(size+SMALL_TEXT);
			tvs[position][4].setText(R.string.kodesh);

			//		Daas
			tvs[position][5].setText(R.string.daas);

			//		HaShiveinu through Re'eh 
			tvs[position][6].setText(R.string.hashiveinu);
			tvs[position][7].setText(R.string.slach_lanu);
			tvs[position][8].setText(R.string.reeh_na);

			//		Aneinu
			tvs[position][9].setText(R.string.Aneinu);
			tvs[position][9].setTextSize(size+SMALL_TEXT);


			//		Rephaeinu
			tvs[position][10].setText(R.string.rephaeinu);
			sickPrayer(tvs[position][10], context);
			if(!sharedPref.getString("sickNames", ploniName).equals(ploniName))
				tvs[position][10].setText(tefilaBadCholeh(), BufferType.SPANNABLE);

			//		Barech Aleinu
			tvs[position][11].setText(R.string.Barech_summer);

			//		Teka B'Shofar
			tvs[position][12].setText(R.string.Teka_beshofar);

			//		Hashiva Shofteinu
			tvs[position][13].setText(R.string.Hashiva);

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

			tvs[position][15].setText(R.string.tzadikim);
			tvs[position][16].setText(R.string.Yerushaleim_tisha);
			tvs[position][17].setText(R.string.Es_tzemach);
			tvs[position][18].setText(R.string.Shma_Koleinu_fast);


			//		Retzei
			tvs[position][19].setText(R.string.retzei);

			//		Modim
			tvs[position][20].setText(R.string.Modim);

			tvs[position][21].setText(R.string.Modim_drabanan);
			tvs[position][21].setTextSize(size+SMALL_TEXT);

			//		Kohanim

			if (sharedPref.getBoolean("inIsrael", false)) {
				tvs[position][22].setText(R.string.Birkas_kohanim_israel);
			}
			else {
				tvs[position][22].setText(R.string.Birkas_kohanim_chul);
			}
			tvs[position][22].setTextSize(size+SMALL_TEXT);

			//		Shalom
			tvs[position][23].setText(R.string.Shalom);

			tvs[position][24].setText(R.string.Elohai_natzor);			
			break;

		case STUPID:
			//			Avos
			tvs[position][0].setText(R.string.Avos);
			//		Gevuros
			tvs[position][1].setText(R.string.Gevuros_summer);

			//		Kedusha
			tvs[position][2].setText(R.string.kedusha_stupid);
			tvs[position][2].setTextSize(size+SMALL_TEXT);
			tvs[position][3].setText(R.string.kodesh_stupid);

			//		Daaas through Re'eh 
			tvs[position][4].setText(R.string.daas_stupid);
			tvs[position][5].setText(R.string.hashiveinu_stupid);
			tvs[position][6].setText(R.string.slach_lanu_stupid);
			tvs[position][7].setText(R.string.reeh_na_stupid);

			//		Aneinu
			tvs[position][8].setText(R.string.aneinu_stupid);
			tvs[position][8].setTextSize(size+SMALL_TEXT);

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
			tvs[position][15].setText(R.string.yerushaleim_tisha_stupid);
			tvs[position][16].setText(R.string.es_tzemach_stupid);
			tvs[position][17].setText(R.string.shmaKoleinu_fast_stupid);

			//		Retzei
			tvs[position][18].setText(R.string.retzei_stupid);

			//		Modim
			tvs[position][19].setText(R.string.modim_stupid);


			tvs[position][20].setText(R.string.Modim_drabanan);
			tvs[position][20].setTextSize(size+SMALL_TEXT);

			//		Kohanim
			if (sharedPref.getBoolean("inIsrael", false)) {
				tvs[position][21].setText(R.string.Birkas_kohanim_israel);
			}
			else {
				tvs[position][21].setText(R.string.Birkas_kohanim_chul);
			}
			tvs[position][21].setTextSize(size+SMALL_TEXT);


			//		Shalom
			tvs[position][22].setText(R.string.shalom_stupid);

			tvs[position][23].setText(R.string.elohai_stupid);

			break;

		case SFARDI:
			//			Avos
			tvs[position][0].setText(R.string.Avos);

			//		Gevuros
			tvs[position][1].setText(R.string.Gevuros_summer);


			//		Kedusha
			tvs[position][2].setText(R.string.kedusha_stupid);
			tvs[position][2].setTextSize(size+SMALL_TEXT);


			tvs[position][3].setText(R.string.kodesh);

			//		Daaas through Re'eh 
			tvs[position][4].setText(R.string.daas_sfardi);
			tvs[position][5].setText(R.string.hashiveinu_stupid);
			tvs[position][6].setText(R.string.slach_lanu_sfardi);
			tvs[position][7].setText(R.string.reeh_na_sfardi);

			//		Aneinu
			tvs[position][8].setText(R.string.aneinu_stupid);
			tvs[position][8].setTextSize(size+SMALL_TEXT);

			//		Rephaeinu
			tvs[position][9].setText(R.string.refaeinu_sfardi);
			sickPrayer(tvs[position][9], context);
			if(!sharedPref.getString("sickNames", ploniName).equals(ploniName))
				tvs[position][9].setText(tefilaBadCholeh(), BufferType.SPANNABLE);


			//		Barech Aleinu
			tvs[position][10].setText(R.string.barech_summer_sfardi);

			//		Teka B'Shofar
			tvs[position][11].setText(R.string.teka_sfardi);

			//		Hashiva Shofteinu
			tvs[position][12].setText(R.string.hashiva_sfardi);


			//		Birkas HaMinim through Shma Koleinu
			tvs[position][13].setText(R.string.minim_sfardi);

			tvs[position][14].setText(R.string.tzadikim_sfardi);
			tvs[position][15].setText(R.string.yerushaleim_sfardi_tisha);

			tvs[position][16].setText(R.string.es_tzemach_sfardi);
			tvs[position][17].setText(R.string.shma_koleinu_sfardi_fast);

			//		Retzei
			tvs[position][18].setText(R.string.retzei_sfardi);

			//		Modim
			tvs[position][19].setText(R.string.modim_sfardi);

			tvs[position][20].setText(R.string.Modim_drabanan);
			tvs[position][20].setTextSize(size+SMALL_TEXT);

			//		Shalom
			tvs[position][21].setText(R.string.shalom_sfardi);

			tvs[position][23].setText(R.string.elohai_sfardi);
			break;
		}
		postLayouts(position);
	}

	private void initAleinu(int position){
		Layouts(position);
		setText(tvs[position][1], R.string.aleinu, R.string.aleinu_english, context);

		switch (nusach){
		case ASHKENAZ:
			setText(tvs[position][0], R.string.kadish_shaleim, R.string.kadish_shaleim_english, context);
			setText(tvs[position][2], R.string.kadish_yesom, R.string.kadish_yesom_english, context);
			break;
		case STUPID:
			tvs[position][0].setText(R.string.kadish_shaleim_stupid);
			tvs[position][2].setText(R.string.kadish_yesom_stupid);
			break;
		case SFARDI:
			tvs[position][0].setText(R.string.kadish_shaleim_sfardi);
			tvs[position][1].setText(sfardiMinchaPsalm(position));
			tvs[position][2].setText(R.string.kadish_yesom_sfardi);
			tvs[position][3].setText(R.string.aleinu);
		}
		postLayouts(position);
	}

	private CharSequence sfardiMinchaPsalm(int position){
		Resources res = getResources();
		CharSequence[] tehilim = res.getTextArray(R.array.tehilim);
		String space = "\n\n";
		return tehilim[101]+space;
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



	@Override
	protected void previousActivity(){
		final ActionBar actionBar = getActionBar();
		final int currentPosition=actionBar.getSelectedNavigationIndex();
		switcher.setInAnimation(prevInAn);switcher.setOutAnimation(prevOutAn);
		if(currentPosition-1>-1){

			actionBar.setSelectedNavigationItem(currentPosition-1);
			if(!actionBar.isShowing()){
				showPrayer(currentPosition-1);
				getIntent().putExtra(INHIBIT_ABAR, true);
			}	
		}
	}



	@Override
	protected void restartActivity() {
		getIntent().removeExtra(INHIBIT_ABAR);
		getIntent().putExtra("restartPosition", getActionBar().getSelectedNavigationIndex());
		super.restartActivity();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		ActionBar actionBar = getActionBar();
		int currentSection=determineOrder(actionBar.getSelectedNavigationIndex());
		if(currentSection!=KORBANOS&&currentSection!=ALEINU){
			getMenuInflater().inflate(R.menu.action_bar_body, menu);
		}
		if(currentSection==KORBANOS){
			getMenuInflater().inflate(R.menu.action_bar_first, menu);
		}
		if(currentSection==ALEINU){
			getMenuInflater().inflate(R.menu.action_bar_last, menu);
		}
		return super.onCreateOptionsMenu(menu);
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
		linearLayout.setOrientation(LinearLayout.VERTICAL);
		linearLayout.setPadding(PADDING, QUICK_ZMAN_TOP_PADDING, PADDING, 0);
		TextView[] tvs = quickZmanLables();
		for(int i=0;i<tvs.length;i++)
			linearLayout.addView(tvs[i]);
		return linearLayout;
	}

	@Override
	protected View quickZmanimTimesLayout(LinearLayout linearLayout) {
		linearLayout.setOrientation(LinearLayout.VERTICAL);
		linearLayout.setPadding(PADDING, QUICK_ZMAN_TOP_PADDING, PADDING, 0);
		TextView[] tvs = quickZmanTimes();
		for(int i=0;i<tvs.length;i++)
			linearLayout.addView(tvs[i]);
		return linearLayout;
	}

	private TextView[] quickZmanLables(){
		TextView[] lables = new TextView[3];
		for(int i=0;i<lables.length;i++){
			lables[i]=new TextView(MinchaActivity.this);
			lables[i].setTextSize(QUICK_ZMAN_SIZE);
		}
		setLableText(lables);
		setQuickZmanColor(lables);
		return lables;
	}

	private TextView[] quickZmanTimes(){
		TextView[] times = new TextView[3];
		for(int i=0;i<times.length;i++){
			times[i]=new TextView(MinchaActivity.this);
			times[i].setTextSize(QUICK_ZMAN_SIZE);
		}
		setQuickZmanColor(times);
		setQuickZmanTimesText(times);
		return times;
	}

	private void setQuickZmanTimesText(TextView[] tvs){
		tvs[0].setText(DateFormat.format(DATE_FORMAT, czc.getMinchaGedola()));
		tvs[1].setText(DateFormat.format(DATE_FORMAT, czc.getPlagHamincha()));
		tvs[2].setText(DateFormat.format(DATE_FORMAT, czc.getSunset()));
	}

	private void setQuickZmanColor(TextView[] tvs){
		Date now = new Date();
		now.setTime(System.currentTimeMillis());
		int red = getResources().getColor(android.R.color.holo_red_light);
		if(czc.getMinchaGedola().after(now))
			tvs[0].setTextColor(red);
		if(czc.getSunset().before(now))
			tvs[2].setTextColor(red);
	}

	private void setLableText(TextView[] lables){
		CharSequence[] zmanLables = getResources().getTextArray(R.array.dailyZmanim);
		lables[0].setText(zmanLables[8]);
		lables[1].setText(zmanLables[10]);
		lables[2].setText(zmanLables[11]);
	}
}
