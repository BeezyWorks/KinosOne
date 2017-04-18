package com.mattaniahbeezy.wisechildkinos;

import java.util.Date;

import android.app.ActionBar;
import android.app.ActionBar.OnNavigationListener;
import android.content.Context;
import android.media.AudioManager;
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

public class ShacharisActivity extends MainActivity {
	protected String[] spinnerNav;
	private LinearLayout[] lolz = new LinearLayout[12];
	private LinearLayout[] parshaLL= new LinearLayout[2];
	private ViewSwitcher switcher;
	private Context context=ShacharisActivity.this;	
	public TextView[][] tvs;

	private String ploniName;

	private static final int PIYUTIM=0;
	private static final int BIRKAS_HASHACHAR=1;
	private static final int KORBANOS=2;
	private static final int PESUKEI=3;
	private static final int KRIAS_SHMA=4;
	private static final int AMIDA=5;
	private static final int BEFORE_KRIA=72;
	private static final int ASHREI=102;
	private static final int SIYUM=12;




	@Override
	protected void onCreate(Bundle savedInstanceState) {
		content=R.layout.content_drawer;
		super.onCreate(savedInstanceState);
		switcher=new ViewSwitcher(context);
		scroller.removeAllViews();
		scroller.addView(switcher);
		getIntent().putExtra("shacharis", true);
		spinnerNav=getResources().getStringArray(R.array.kriaNoTachanun);


		tvs=new TextView[spinnerNav.length][27];
		for(int i=0; i<spinnerNav.length; i++)
			preparePrayers(i);
		Spinner();
		silence();
	}




	private void Spinner(){
		ActionBar actionBar=getActionBar();
		actionBar.setDisplayShowTitleEnabled(false);
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
		String[] spinnerNavItems={};

		spinnerNavItems = getResources().getStringArray(R.array.kriaNoTachanun);
		ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this,
				SPINNER_LAYOUT,
				spinnerNavItems);
		OnNavigationListener mOnNavigationListener = new OnNavigationListener() {
			public boolean onNavigationItemSelected(int position, long itemId) {
				if(!getIntent().getBooleanExtra(INHIBIT_ABAR, false))
					showPrayer(position);
				else
					//TODO this inhibiter needs fixin
					getIntent().removeExtra(INHIBIT_ABAR);

				return true;
			}
		};
		actionBar.setListNavigationCallbacks(spinnerAdapter, mOnNavigationListener);
		actionBar.setSelectedNavigationItem(getIntent().getIntExtra("restartPosition", 1));
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
			lolz[position].addView(tvs[position][i], new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		}
	}
	private void ParshaLayout(int position, TextView[] tvs){
		parshaLL[position]=new LinearLayout(context);
		parshaLL[position].setPadding(PADDING, PADDING, PADDING, PADDING);
		parshaLL[position].setOrientation(LinearLayout.VERTICAL);
		switch(style){
		case SEPIA:
			parshaLL[position].setBackgroundColor(getResources().getColor(R.color.sepia));
			break;
		case NIGHT:
			parshaLL[position].setBackgroundColor(getResources().getColor(android.R.color.background_dark));
			break;
		case DAY:
			parshaLL[position].setBackgroundColor(getResources().getColor(android.R.color.background_light));
		}

		for(int i=0; i<tvs.length; i++){
			tvs[i]=new TextView(getApplicationContext());
			tvs[i].setTypeface(tf);
			tvs[i].setTextSize(size);
			if(style!=NIGHT){
				tvs[i].setTextColor(getResources().getColor(android.R.color.primary_text_light));
			}
			else{
				tvs[i].setTextColor(getResources().getColor(android.R.color.primary_text_dark));
			}
			parshaLL[position].addView(tvs[i]);
		}
	}
	private void postLayouts(int position){
		for(int i=0; i<tvs[position].length; i++){
			if( tvs[position][i].getText().length()<1)
				tvs[position][i].setVisibility(View.GONE);
		}
	}

	private void postLayouts(TextView[] tvs){
		for(int i=0; i<tvs.length; i++){
			if( tvs[i].getText().length()<1)
				tvs[i].setVisibility(View.GONE);
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

	private void preparePrayers(int position){
		switch(determineOrder(position)){
		case PIYUTIM:
			piyutim(position);
			break;
		case BIRKAS_HASHACHAR:
			birkasHaShachar(position);
			break;
		case KORBANOS:
			korbanos(position);
			break;
		case PESUKEI:
			psuekiDizmra(position);
			break;
		case KRIAS_SHMA:
			kriasShma(position);
			break;
		case AMIDA:
			showAmida(position);
			break;
		case BEFORE_KRIA:
			beforeKria(position);
			break;
		case ASHREI:
			asrhei(position);
			break;
		case SIYUM:
			siyumShacharis(position);
			break;
		}

	}

	private void piyutim(int position) {
		Layouts(position);
		tvs[position][0].setText(R.string.piyutim);
		postLayouts(position);
	}






	private void siyumShacharis(int position) {
		Layouts(position);
		switch (nusach){
		case ASHKENAZ:
			setText(tvs[position][0], R.string.kadish_shaleim, 0, context);
			setText(tvs[position][1], R.string.aleinu, 0, context);			
			setText(tvs[position][6], R.string.kadish_yesom,0, context);			
			break;

		case STUPID:
			tvs[position][1].setText(R.string.tefila_beis_yaakov2);
			tvs[position][9].setText(R.string.stupid_end);
			break;
		case SFARDI:
			tvs[position][1].setText(R.string.tefila_beis_yaakov2);
			tvs[position][9].setText(R.string.stupid_end);
			break;
		}
		postLayouts(position);
	}


	private void asrhei(int position) {
		Layouts(position);	
		setText(tvs[position][0], R.string.ashrei, 0, context);	
		tvs[position][2].setText(R.string.uva_letzion);	
		tvs[position][4].setText(R.string.kedusha_dsidra);

		if(nusach==STUPID){
			tvs[position][4].setText(R.string.kedusha_dsidra_stupid);
			tvs[position][5].setText(R.string.kadish_shaleim_stupid);
			tvs[position][6].setText(R.string.return_torah);


		}

		if(nusach==SFARDI){
			tvs[position][0].setText(R.string.sfardi_ashrei);
			tvs[position][4].setText(R.string.kedusha_dsidra_sfardi);
			tvs[position][5].setText(R.string.kadish_shaleim_sfardi);
			tvs[position][6].setText(R.string.return_torah_sfardi);
		}

		postLayouts(position);
	}

	private void afterKria() {
		TextView[] tvs= new TextView[5];
		ParshaLayout(1, tvs);
		tvs[0].setText(R.string.chatzi_kadish);

		if(nusach!=SFARDI){
			tvs[1].setText(R.string.hagba);
		}

		tvs[3].setText(R.string.Tisha_haftara);


		tvs[4].setText(R.string.return_torah);

		if(nusach==STUPID){
			tvs[4].setText(" ");
			tvs[0].setText(R.string.chatzi_kadish_stupid);
		}

		if(nusach==SFARDI){
			tvs[4].setText(" ");
			tvs[0].setText(R.string.chatzi_kadish_sfardi);
		}
		postLayouts(tvs);
	}

	private void kria(){
		TextView[] tvs = new TextView[2];
		ParshaLayout(0, tvs);
		tvs[0].setText(R.string.Tisha_kria);	
		postLayouts(tvs);
	}




	private void beforeKria(int position) {
		Layouts(position);
		if(nusach!=SFARDI)	
			tvs[position][1].setText(R.string.vayehi_binso_haron);

		if(nusach==SFARDI){
			tvs[position][0].setText(R.string.sfardi_before_skria_notachanun);
			tvs[position][1].setText(" ");
		}
		kria();
		afterKria();
		postLayouts(position);

	}


	private void showAmida(int position) {
		Layouts(position);
		ploniName=getResources().getString(R.string.ploni);
		firstTwoAmida(position);
		switch(nusach){
		case ASHKENAZ:
			amidaAshkenaz(position);
			break;

		case STUPID:
			amidaSfard(position);
			break;

		case SFARDI:
			amidaSfardi(position);
			break;
		}
		postLayouts(position);

	}
	private void amidaSfardi(int position) {
		//		Kedusha
		setText(tvs[position][2], R.string.kedusha_stupid, 0, context);
		tvs[position][2].setTextSize(size+SMALL_TEXT);

		setText(tvs[position][3], R.string.kodesh, 0, context);

		//		Daaas through Re'eh 
		setText(tvs[position][4], R.string.daas_sfardi,0, context);
		setText(tvs[position][5], R.string.hashiveinu_sfardi, 0, context);
		setText(tvs[position][6], R.string.slach_lanu_sfardi,0, context);
		setText(tvs[position][7], R.string.reeh_na_sfardi,0, context);

		setText(tvs[position][8], R.string.aneinu_stupid, 0, context);
		tvs[position][8].setTextSize(size+SMALL_TEXT);

		//		Rephaeinu
		setText(tvs[position][9], R.string.refaeinu_sfardi, 0, context);
		sickPrayer(tvs[position][9], context);
		if(!sharedPref.getString("sickNames", ploniName).equals(ploniName))
			tvs[position][9].setText(tefilaBadCholeh(), BufferType.SPANNABLE);

		//		Barech Aleinu

		setText(tvs[position][10], R.string.barech_summer_sfardi, 0, context);


		//		Teka B'Shofar
		setText(tvs[position][11], R.string.teka_sfardi,0, context);

		//		Hashiva Shofteinu

		setText(tvs[position][12], R.string.hashiva_sfardi, 0, context);

		//		Birkas HaMinim through Shma Koleinu
		setText(tvs[position][13], R.string.minim_sfardi, 0, context);
		setText(tvs[position][14], R.string.tzadikim_sfardi,0, context);
		setText(tvs[position][15], R.string.yerushaleim_sfardi, 0, context);
		setText(tvs[position][16], R.string.es_tzemach_sfardi, 0, context);
		setText(tvs[position][17], R.string.shma_koleinu_sfardi, 0, context);

		//		Retzei

		setText(tvs[position][18], R.string.retzei_sfardi, 0, context);

		//		Modim
		setText(tvs[position][19], R.string.modim_sfardi, 0, context);

		setText(tvs[position][20], R.string.Modim_drabanan, 0, context);
		tvs[position][20].setTextSize(size+SMALL_TEXT);


		//		Shalom

		setText(tvs[position][22], R.string.shalom_sfardi, 0, context);

		setText(tvs[position][23], R.string.elohai_sfardi, 0, context);

	}

	private void amidaSfard(int position) {
		//		Kedusha

		setText(tvs[position][2], R.string.kedusha_stupid,0, context);
		tvs[position][2].setTextSize(size+SMALL_TEXT);
		setText(tvs[position][3], R.string.kodesh_stupid,0, context);

		//		Daaas through Re'eh 
		setText(tvs[position][4], R.string.daas_stupid,0, context);
		setText(tvs[position][5], R.string.hashiveinu_stupid, 0, context);
		setText(tvs[position][6], R.string.slach_lanu_stupid, 0, context);
		setText(tvs[position][7], R.string.reeh_na_stupid, 0, context);

		//		Aneinu
		if (jDate.isTaanis()) {
			setText(tvs[position][8], R.string.aneinu_stupid, 0, context);
			tvs[position][8].setTextSize(size+SMALL_TEXT);
		}

		//		Rephaeinu
		setText(tvs[position][9], R.string.rephaeinu_stupid, R.string.rephaeinu_english, context);
		sickPrayer(tvs[position][9], context);
		if(!sharedPref.getString("sickNames", ploniName).equals(ploniName))
			tvs[position][9].setText(tefilaBadCholeh(), BufferType.SPANNABLE);

		//		Barech Aleinu

		setText(tvs[position][10], R.string.barech_summer_stupid, R.string.barech_english_summer, context);

		//		Teka B'Shofar
		setText(tvs[position][11], R.string.teka_stupid, R.string.teka_english, context);

		//		Hashiva Shofteinu
		setText(tvs[position][12], R.string.hashiva_stupid, R.string.hashiva_english, context);

		//		Birkas HaMinim through Shma Koleinu
		setText(tvs[position][13], R.string.minim_stupid, R.string.minim_english, context);
		setText(tvs[position][14], R.string.tzadikim_stupid, R.string.tzadikim_english, context);
		setText(tvs[position][15], R.string.yerushaleim_stupid, R.string.yerushaleim_english, context);
		setText(tvs[position][16], R.string.es_tzemach_stupid, R.string.estzemach_english, context);
		setText(tvs[position][17], R.string.shmaKoleinu_stupid, R.string.shma_koleinu_english, context);
		extraPrayer(tvs[position][17], R.string.shmaKoleinu_stupid_long);

		//		Retzei
		setText(tvs[position][18], R.string.retzei_stupid, R.string.retzei_english, context);
		setText(tvs[position][19], R.string.modim_stupid, R.string.modim_english, context);


		setText(tvs[position][20], R.string.Modim_drabanan, R.string.modimDrabanan_english, context);
		tvs[position][20].setTextSize(size+SMALL_TEXT);
		setText(tvs[position][22], R.string.shalom_stupid, R.string.shalom_english, context);


		setText(tvs[position][23], R.string.elohai_stupid, R.string.elohai_natzor_english, context);

	}

	private void amidaAshkenaz(final int position) {

		//		Kedusha
		setText(tvs[position][2], R.string.Kedusha, R.string.kedusha_english, context);
		setText(tvs[position][3], R.string.Kedusha_chasima, R.string.kedusha_english, context);


		tvs[position][2].setTextSize(size+SMALL_TEXT);
		tvs[position][3].setTextSize(size+SMALL_TEXT);
		setText(tvs[position][4], R.string.kodesh, R.string.kedushashashem_english, context);

		//		Daas
		setText(tvs[position][5], R.string.daas, R.string.daas_english, context);

		//		HaShiveinu through Re'eh 
		setText(tvs[position][6], R.string.hashiveinu, R.string.hashiveinu_english, context);
		setText(tvs[position][7], R.string.slach_lanu, R.string.slach_english, context);
		setText(tvs[position][8], R.string.reeh_na, R.string.reeh_na_english, context);

		//		Aneinu

		setText(tvs[position][9], R.string.Aneinu, 0, context);
		tvs[position][9].setTextSize(size+SMALL_TEXT);


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

		setText(tvs[position][21], R.string.Modim_drabanan, R.string.modimDrabanan_english, context);
		tvs[position][21].setTextSize(size+SMALL_TEXT);
		setText(tvs[position][23], R.string.Shalom, R.string.shalom_english, context);
		setText(tvs[position][24], R.string.Elohai_natzor, R.string.elohai_natzor_english, context);
	}

	private void firstTwoAmida(int position) {
		//		Avos
		setText(tvs[position][0], R.string.Avos, R.string.avos_english, context);

		//		Gevuros
		setText(tvs[position][1], R.string.Gevuros_summer, R.string.gevuros_english_summer, context);

	}

	private void kriasShma(int position) {
		Layouts(position);
		switch (nusach) {
		case ASHKENAZ:
			setText(tvs[position][0], R.string.krias_shma_shacharis1, getResources().getStringArray(R.array.krias_shma_shacharis_english)[0], context);
			setText(tvs[position][1], R.string.krias_shma_shacharis2, getResources().getStringArray(R.array.krias_shma_shacharis_english)[1], context);
			setText(tvs[position][2], R.string.krias_shma_shacharis3, getResources().getStringArray(R.array.krias_shma_shacharis_english)[2], context);
			setText(tvs[position][3], R.string.krias_shma_shacharis4, getResources().getStringArray(R.array.krias_shma_shacharis_english)[3], context);
			setText(tvs[position][4], R.string.krias_shma_shacharis5, getResources().getStringArray(R.array.krias_shma_shacharis_english)[4], context);
			setText(tvs[position][5], R.string.krias_shma_shacharis6, getResources().getStringArray(R.array.krias_shma_shacharis_english)[5], context);
			setText(tvs[position][6], R.string.krias_shma_shacharis7, getResources().getStringArray(R.array.krias_shma_shacharis_english)[6], context);
			setText(tvs[position][7], R.string.krias_shma_shacharis8, getResources().getStringArray(R.array.krias_shma_shacharis_english)[7], context);
			setText(tvs[position][8], R.string.kriasShma1, R.string.kriasShma1_english, context);
			setText(tvs[position][9], R.string.kriasShma2, R.string.kriasShma2_english, context);
			setText(tvs[position][10], R.string.kriasShma3, R.string.kriasShma3_english, context);
			setText(tvs[position][11], R.string.krias_shma_shacharis9, getResources().getStringArray(R.array.krias_shma_shacharis_english)[8], context);
			setText(tvs[position][12], R.string.krias_shma_shacharis10, getResources().getStringArray(R.array.krias_shma_shacharis_english)[9], context);
			setText(tvs[position][13], R.string.krias_shma_shacharis11, getResources().getStringArray(R.array.krias_shma_shacharis_english)[10], context);
			setText(tvs[position][14], R.string.krias_shma_shacharis12, getResources().getStringArray(R.array.krias_shma_shacharis_english)[11], context);
			setText(tvs[position][15], R.string.krias_shma_shacharis13, getResources().getStringArray(R.array.krias_shma_shacharis_english)[12], context);
			setText(tvs[position][16], R.string.krias_shma_shacharis14, getResources().getStringArray(R.array.krias_shma_shacharis_english)[13], context);
			break;
		case STUPID:
			tvs[position][0].setText(R.string.krias_shma_stuipd);
			break;
		case SFARDI:
			tvs[position][0].setText(R.string.birkas_kriasShma_sfardi);
			break;
		default:
			break;
		}
		postLayouts(position);
	}

	private void psuekiDizmra(int position) {
		Layouts(position);
		switch(nusach){
		case ASHKENAZ:
			tvs[position][0].setText(R.string.baruch_shamar);
			tvs[position][1].setText(R.string.mizor_lsoda);
			tvs[position][3].setText(R.string.psukei_end);
			tvs[position][5].setText(R.string.chatzi_kadish);
			break;

		case STUPID:
			tvs[position][0].setText(R.string.hodu_stupid);
			tvs[position][2].setText(R.string.baruch_sheamar_stupid);
			tvs[position][3].setText(R.string.mizor_lsoda);
			tvs[position][4].setText(R.string.pesukei_stupid);
			tvs[position][5].setText(R.string.chatzi_kadish_stupid);
			break;

		case SFARDI:
			tvs[position][0].setText(R.string.pesukei_sfardi);
			tvs[position][1].setText(R.string.chatzi_kadish_sfardi);
			break;
		}
		postLayouts(position);
	}

	private int determineOrder(int position){
		switch(position){
		case 0:
			return PIYUTIM;
		case 1:
			return BIRKAS_HASHACHAR;
		case 2:
			return KORBANOS;
		case 3:
			return PESUKEI;
		case 4:
			return KRIAS_SHMA;
		case 5:
			return AMIDA;
		case 6:
			return BEFORE_KRIA;
		case 7:
			return ASHREI;
		case 8:
			return SIYUM;
		default:
			return BIRKAS_HASHACHAR;
		}
	}
	private void birkasHaShachar(int position){
		Layouts(position);
		switch(nusach){
		case ASHKENAZ:
			setText(tvs[position][0], R.string.birkashashachar1, R.string.birkashashachar_english1, context);
			setText(tvs[position][1], R.string.birkashashachar2, R.string.birkashashachar_english2, context);
			setText(tvs[position][2], R.string.birkashashachar3, R.string.birkashashachar_english3, context);
			setText(tvs[position][4], R.string.birkashashachar5, R.string.birkashashachar_english5, context);
			setText(tvs[position][5], R.string.birkashashachar6, R.string.birkashashachar_english6, context);
			setText(tvs[position][6], R.string.birkashashachar7, R.string.birkashashachar_english7, context);
				setText(tvs[position][3], R.string.birkashashachar4_tisha, R.string.birkashashachar_english4, context);
			
			break;
		case STUPID:
			tvs[position][0].setText(R.string.asher_stupid);
			tvs[position][2].setText(R.string.birkas_hashachar_stuipd);
			break;
		case SFARDI:
			tvs[position][0].setText(R.string.birkas_hashachar_sfardi);
			tvs[position][2].setText(R.string.birkas_hashachar_sfardi2);
			break;
		}
		postLayouts(position);

	}
	private void korbanos(int position){
		Layouts(position);
		switch(nusach){
		case ASHKENAZ:
			tvs[position][0].setText(R.string.korbanos);
			tvs[position][2].setText(R.string.eizo_hu);

			tvs[position][3].setText(R.string.kadish_drabanan);

			if(sharedPref.getBoolean("modernAsh", false)){
				setText(tvs[position][4], getResources().getTextArray(R.array.tehilim)[29], getResources().getTextArray(R.array.psalms)[29], context);
				setText(tvs[position][5], R.string.kadish_yesom, R.string.kadish_yesom_english, context);
				tvs[position][4].append("\n\n");
			}
			break;
		case STUPID:
			tvs[position][0].setText(R.string.korbanos_stuipd_1);

			if (jDate.isRoshChodesh()) {
				tvs[position][1].setText(R.string.korbanos_stuipd_rosh);
			}

			tvs[position][2].setText(R.string.korbanos_stupid_2);
			break;
		case SFARDI:
			tvs[position][0].setText(R.string.korbanos_sfardi);
			break;
		}	
		postLayouts(position);
	}

	@Override
	protected void onResume() {
		super.onResume();
		if(sharedPref.getBoolean("Silence", true)){
			AudioManager am = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
			am.setRingerMode(AudioManager.RINGER_MODE_SILENT);}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		ActionBar actionBar = getActionBar();
		int currentSection=determineOrder(actionBar.getSelectedNavigationIndex());
		if(currentSection!=PIYUTIM&&currentSection!=SIYUM){
			getMenuInflater().inflate(R.menu.action_bar_body, menu);
		}
		if(currentSection==PIYUTIM){
			getMenuInflater().inflate(R.menu.action_bar_first, menu);
		}
		if(currentSection==SIYUM){
			getMenuInflater().inflate(R.menu.action_bar_last, menu);
		}
		return super.onCreateOptionsMenu(menu);
	}

	private void showKria(int position){
		invalidateOptionsMenu();
		try{
			switcher.addView(parshaLL[position], new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		}
		catch(IllegalStateException e){

		}
		switcher.showNext();
		if(switcher.getNextView()!=null)
			switcher.removeView(switcher.getNextView());
		scroller.smoothScrollTo(0, 0);

		switcher.setInAnimation(null);
		switcher.setOutAnimation(null);
	}

	@Override
	protected void nextActivity(){
		final ActionBar actionBar = getActionBar();
		final int currentPosition=actionBar.getSelectedNavigationIndex();
		switcher.setInAnimation(inAn);switcher.setOutAnimation(outAn);
		int section = determineOrder(currentPosition);
		if(section==BEFORE_KRIA||switcher.getChildAt(0)==parshaLL[0]||switcher.getChildAt(0)==parshaLL[1]){
			if(switcher.getChildAt(0)==lolz[currentPosition]&&section==BEFORE_KRIA)
				showKria(0);
			else if(switcher.getChildAt(0)==parshaLL[0])
				showKria(1);
			else if(switcher.getChildAt(0)==parshaLL[1]){
				if(section==BEFORE_KRIA){
					if(!actionBar.isShowing()){
						getIntent().putExtra(INHIBIT_ABAR, true);
						showPrayer(currentPosition+1);
					}	
					actionBar.setSelectedNavigationItem(currentPosition+1);
				}
				else
					showPrayer(currentPosition);
			}
		}
		else if(currentPosition+1<spinnerNav.length){
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
		int section = determineOrder(currentPosition);
		switcher.setInAnimation(prevInAn);switcher.setOutAnimation(prevOutAn);
		if(parshaLL[0]!=null&&(section==ASHREI||switcher.getChildAt(0)==parshaLL[0]||switcher.getChildAt(0)==parshaLL[1])){
			if(switcher.getChildAt(0)==lolz[currentPosition]&&section==ASHREI)
				showKria(1);
			else if(switcher.getChildAt(0)==parshaLL[1])
				showKria(0);
			else if(switcher.getChildAt(0)==parshaLL[0]){
				if(section==ASHREI){
					actionBar.setSelectedNavigationItem(currentPosition-1);
					if(!actionBar.isShowing()){
						showPrayer(currentPosition-1);
						getIntent().putExtra(INHIBIT_ABAR, true);
					}	
				}
				else
					showPrayer(currentPosition);
			}
		}
		else if(currentPosition-1>-1){

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
		TextView[] lables = new TextView[7];
		for(int i=0;i<lables.length;i++){
			lables[i]=new TextView(ShacharisActivity.this);
			lables[i].setTextSize(QUICK_ZMAN_SIZE);
		}
		setLableText(lables);
		setQuickZmanColor(lables);
		return lables;
	}

	private TextView[] quickZmanTimes(){
		TextView[] times = new TextView[7];
		for(int i=0;i<times.length;i++){
			times[i]=new TextView(ShacharisActivity.this);
			times[i].setTextSize(QUICK_ZMAN_SIZE);
		}
		setQuickZmanColor(times);
		setQuickZmanTimesText(times);
		return times;
	}

	private void setQuickZmanTimesText(TextView[] tvs){
		tvs[0].setText(DateFormat.format(DATE_FORMAT, czc.getMisheyakir11Degrees()));
		tvs[1].setText(DateFormat.format(DATE_FORMAT, czc.getSunrise()));
		tvs[2].setText(DateFormat.format(DATE_FORMAT, czc.getSofZmanShmaMGA()));
		tvs[3].setText(DateFormat.format(DATE_FORMAT, czc.getSofZmanShmaGRA()));
		tvs[4].setText(DateFormat.format(DATE_FORMAT, czc.getSofZmanTfilaMGA()));
		tvs[5].setText(DateFormat.format(DATE_FORMAT, czc.getSofZmanTfilaGRA()));
		tvs[6].setText(DateFormat.format(DATE_FORMAT, czc.getChatzos()));
	}

	private void setQuickZmanColor(TextView[] tvs){
		Date now = new Date();
		now.setTime(System.currentTimeMillis());
		int red = getResources().getColor(android.R.color.holo_red_light);
		if(czc.getMisheyakir11Degrees().after(now))
			tvs[0].setTextColor(red);
		if(czc.getSunrise().after(now))
			tvs[1].setTextColor(red);
		if(czc.getSofZmanShmaMGA().before(now))
			tvs[2].setTextColor(red);
		if(czc.getSofZmanShmaGRA().before(now))
			tvs[3].setTextColor(red);
		if(czc.getSofZmanTfilaMGA().before(now))
			tvs[4].setTextColor(red);
		if(czc.getSofZmanTfilaGRA().before(now))
			tvs[5].setTextColor(red);
		if(czc.getChatzos().before(now))
			tvs[6].setTextColor(red);
	}

	private void setLableText(TextView[] lables){
		CharSequence[] zmanLables = getResources().getTextArray(R.array.dailyZmanim);
		for(int i =0;i<lables.length;i++){
			lables[i].setText(zmanLables[i+1]);
		}
	}
}