package com.mattaniahbeezy.wisechildkinos;



import com.crashlytics.android.Crashlytics;
import io.fabric.sdk.android.Fabric;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import net.sourceforge.zmanim.ComplexZmanimCalendar;
import net.sourceforge.zmanim.hebrewcalendar.Daf;
import net.sourceforge.zmanim.hebrewcalendar.HebrewDateFormatter;
import net.sourceforge.zmanim.hebrewcalendar.JewishCalendar;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.location.Location;
import android.os.Bundle;
import android.text.InputType;
import android.text.format.DateFormat;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


public class Main extends MainActivity {
	Date zmanChoice=new Date();
	int offset = 0;
	PendingIntent pendingIntent;
	Intent intentAlarm;
	Location location;
	String msg;

	public static final int ALOS_HASHACHAR=1;
	public static final int NEITZ=2;
	public static final int SOF_ZMAN_SHMA=3;
	public static final int SOF_ZMAN_TFILA=4;
	public static final int CHATZOS=5;
	public static final int MINCHA_GEDOLA=6;
	public static final int MINCHA_KETANA=7;
	public static final int PLAG_MINCHA=8;
	public static final int SHKIA=9;
	public static final int TZEIS=10;

	public static final int LUACH_ZMANIM=1;
	public static final int SHACHARIS=2;
	public static final int MINCHA=3;
	public static final int MAARIV=4;
	public static final int SHONOS=5;
	public static final int BEDTIME_SHMA=6;
	public static final int SFIRAS_HAOMER=7;


	@SuppressLint("NewApi")
	protected void onCreate(Bundle savedInstanceState) {
		content=R.layout.zmanim;
		super.onCreate(savedInstanceState);
		Fabric.with(this, new Crashlytics());
		TextView connection = (TextView)findViewById(R.id.connection);

		final TextView nusachToggler=(TextView)findViewById(R.id.Nusach);
		nusachToggler.setTypeface(null, Typeface.BOLD);
		setNusachTogglerText(nusachToggler);



		nusachToggler.setOnLongClickListener(new View.OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {
				toggleStar();
				setNusachTogglerText(nusachToggler);
				return false;
			}
		});

		nusachToggler.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v){
				changeNusach(nusachToggler);
			}
		});

		if(style==DAY){
			View ll = findViewById(R.id.content_frame);
			View pp=findViewById(R.id.rLayout);
			ll.setBackgroundColor(getResources().getColor(android.R.color.background_light));
			pp.setBackgroundColor(getResources().getColor(android.R.color.background_light));
		}

		if(czc!=null)
			setZmanimTexts();



		if(czc==null){
			Toast.makeText(getApplicationContext(), 
					"Could not access your location at this time. Try again later.", Toast.LENGTH_LONG).show();
			connection.setText("No connection");
		}
		setConnectionText(connection);		

		connection.setOnLongClickListener(new View.OnLongClickListener() {

			@Override
			public boolean onLongClick(View arg0) {
				AlertDialog.Builder block = new AlertDialog.Builder(Main.this);
				block.setIcon(getResources().getDrawable(R.drawable.ic_action_settings));
				block.setTitle("Set Manual Location");
				final SharedPreferences.Editor edit = sharedPref.edit();
				View blockView = Main.this.getLayoutInflater().inflate(R.layout.locationdialog, null);
				block.setView(blockView);
				final CheckBox manCheck = (CheckBox) blockView.findViewById(R.id.manualLocCheck);
				manCheck.setText("Enable Manual Location");
				manCheck.setChecked(sharedPref.getBoolean("manualLoc", false));
				final LinearLayout blayout = (LinearLayout) blockView.findViewById(R.id.locationPrefs);
				if(!manCheck.isChecked()){
					blayout.setVisibility(View.GONE);
				}
				manCheck.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
						edit.putBoolean("manualLoc", isChecked);
						if(isChecked){
							blayout.setVisibility(View.VISIBLE);
						}
						else {
							blayout.setVisibility(View.GONE);
						}
					}
				});
				final EditText latEdit = (EditText) blockView.findViewById(R.id.latInput);
				latEdit.setInputType(InputType.TYPE_CLASS_NUMBER| InputType.TYPE_NUMBER_FLAG_DECIMAL |InputType.TYPE_NUMBER_FLAG_SIGNED);
				latEdit.setText(String.valueOf(sharedPref.getFloat("latitude", (float) 46.10273)));
				final EditText longEdit = (EditText) blockView.findViewById(R.id.longInput);
				longEdit.setText(String.valueOf(sharedPref.getFloat("longitude", (float) 12.82331)));
				longEdit.setInputType(InputType.TYPE_CLASS_NUMBER| InputType.TYPE_NUMBER_FLAG_DECIMAL |InputType.TYPE_NUMBER_FLAG_SIGNED);
				final EditText elEdit = (EditText) blockView.findViewById(R.id.elevationInput);
				elEdit.setInputType(InputType.TYPE_CLASS_NUMBER |InputType.TYPE_NUMBER_FLAG_DECIMAL);
				elEdit.setText(String.valueOf(sharedPref.getFloat("elevation", (float) 754)));


				block.setPositiveButton("Save", new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						edit.putFloat("latitude", Float.parseFloat(latEdit.getText().toString()));
						edit.putFloat("longitude", Float.parseFloat(longEdit.getText().toString()));
						edit.putFloat("elevation", Float.parseFloat(elEdit.getText().toString()));
						edit.commit();
						getIntent().addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);				
						startActivity(getIntent());
						finish();
						overridePendingTransition(0, 0);
					}
				});

				block.create().show();
				return false;
			}
		});

		if(getIntent().getBooleanExtra("setAlarm", false)){
			finish();
		}


		HebrewDateFormatter hdf = new HebrewDateFormatter();
		JewishCalendar parshaDate = (JewishCalendar) jDate.clone();

		hdf.setHebrewFormat(true);
		TextView weeksParsha = (TextView)findViewById(R.id.weeksParsha);

		while (parshaDate.getParshaIndex()<0) {
			parshaDate.forward();
		}

		weeksParsha.setText(hdf.formatParsha(parshaDate));

		TextView rainy = (TextView) findViewById(R.id.Rain);
			rainy.setText(R.string.no_rain_string);
		
		
		TextView summery = (TextView) findViewById(R.id.Summer);
		summery.setText(R.string.summer_string);
		

		Daf todaysDaf = (Daf) jDate.getDafYomiBavli();
		String daf = hdf.formatDafYomiBavli(todaysDaf);

		TextView date = (TextView) findViewById(R.id.Hebrewdate);
		date.setText(hdf.format(jDate));
		
		TextView dafYomi = (TextView) findViewById(R.id.DafYomi);
		dafYomi.setText(daf);


		TextView mussaf= (TextView) findViewById(R.id.Mussaf);
		if (jDate.isRoshChodesh() && jDate.isChanukah()==false) {		
			mussaf.setText(hdf.formatRoshChodesh(jDate));
		}
		else {
			mussaf.setText(hdf.formatYomTov(jDate));
		}
		TextView roshChodesh = (TextView)findViewById(R.id.Chanuka);
		roshChodesh.setVisibility(View.GONE);
		if (jDate.isChanukah()) {
			mussaf.setText(hdf.formatYomTov(jDate));
			roshChodesh.setVisibility(View.VISIBLE);
			roshChodesh.setText(hdf.formatRoshChodesh(jDate));	
		}

		setIntent(new Intent(this, Main.class));

		if(mussaf.getText().length()<1){
			mussaf.setVisibility(View.GONE);
		}
	}



	private void setZmanimTexts() {

		TextView candleLightText = (TextView)findViewById(R.id.CandleLightTxt);
		TextView motzashText = (TextView)findViewById(R.id.MotzashTxt);
		TextView candleLight=(TextView)findViewById(R.id.CandleLight);
		TextView motzash=(TextView)findViewById(R.id.motzash);
		TextView chatzosText=(TextView)findViewById(R.id.ChatzosTxt);
		TextView chatzos=(TextView)findViewById(R.id.Chatzos);
		candleLightText.setText("");
		candleLightText.setTypeface(null, Typeface.BOLD);
		motzashText.setText(" ");
		motzashText.setTypeface(null,Typeface.BOLD);
		candleLight.setText(" ");
		candleLight.setTypeface(null, Typeface.BOLD);
		motzash.setText("");
		motzash.setTypeface(null, Typeface.BOLD);
		chatzos.setText("");
		chatzos.setTypeface(null, Typeface.BOLD);
		chatzosText.setText("");
		chatzosText.setTypeface(null, Typeface.BOLD);

		TextView alos = (TextView) findViewById(R.id.Alos);
		if(sharedPref.getBoolean("alosGreen", true)){

			alos.setText(DateFormat.format(DATE_FORMAT, czc.getAlosHashachar()));
		}
		else{


			TextView alosTitle = (TextView) findViewById(R.id.textView7);
			alos.setText(DateFormat.format(DATE_FORMAT, czc.getMisheyakir11Point5Degrees()));
			alosTitle.setText(getResources().getStringArray(R.array.dailyZmanim)[1]);
		}

		toggleAlos(alos);


		TextView neitzHaChama = (TextView) findViewById(R.id.neitz);
		neitzHaChama.setText(DateFormat.format(DATE_FORMAT, czc.getSunrise()));


		TextView shma = (TextView) findViewById(R.id.shma);
		if(!sharedPref.getBoolean("shmaZman", true)){
			shma.setText(DateFormat.format(DATE_FORMAT, czc.getSofZmanShmaMGA()));
			TextView matext = (TextView) findViewById(R.id.weeksParsha1);
			matext.setText(R.string.shmaMA);
		}

		else{
			shma.setText(DateFormat.format(DATE_FORMAT, czc.getSofZmanShmaGRA()));
		}

		toggleShma(shma, czc);

		TextView minchaG = (TextView) findViewById(R.id.mincha);
		minchaG.setText(DateFormat.format(DATE_FORMAT, czc.getMinchaGedola()));

		TextView shkiasHaChama = (TextView) findViewById(R.id.shkia);
		shkiasHaChama.setText(DateFormat.format(DATE_FORMAT, czc.getSunset()));

		TextView teisHakochavim = (TextView) findViewById(R.id.tzeis);
		if(sharedPref.getBoolean("rebeinuTom", false)){
			teisHakochavim.setText(DateFormat.format(DATE_FORMAT, czc.getTzais72()));
			TextView rt = (TextView) findViewById(R.id.weeksParsha7);
			rt.setText(R.string.tzeisRT);
		}
		else{
			teisHakochavim.setText(DateFormat.format(DATE_FORMAT, czc.getTzais()));
		}
		toggleTzeis(teisHakochavim, czc);

		Date greatdate = new Date();		
		int afterShkia= greatdate.compareTo(czc.getSunset());
		if (afterShkia>0){
			jDate.forward();
			tomorrow=true;
		}

			chatzosText.setText(R.string.chatzos_string);
			chatzos.setText(DateFormat.format(DATE_FORMAT, czc.getChatzos()));
	}



	private void setConnectionText(TextView connection) {
		switch(sharedPref.getInt("locationValue", GPS_LOCATION)){
		case GPS_LOCATION:
			connection.setText("GPS Location");
			break;
		case NETWORK_LOCATION:
			connection.setText("Network Location");
			break;
		case SAVED_LOCATION:
			connection.setText("Saved Location");
			break;
		}
		if(sharedPref.getBoolean("manualLoc", false)){
			connection.setText("Manual Location");
		}
	}



	private void setNusachTogglerText(TextView nusachToggler) {
		switch(nusach){
		case ASHKENAZ:
			nusachToggler.setText(R.string.Ashkenazi_string);
			if(sharedPref.getBoolean("modernAsh", false)){
				nusachToggler.append("*");
			}
			break;
		case STUPID:
			nusachToggler.setText(R.string.Stupid_string);
			break;
		case SFARDI:
			nusachToggler.setText(R.string.Sfardi_string);
			break;
		}

	}



	private void changeNusach(TextView v){
		SharedPreferences.Editor edit = sharedPref.edit();
		switch (nusach) {
		case ASHKENAZ:
			edit.putString("nusach", String.valueOf(STUPID));
			break;
		case STUPID:
			edit.putString("nusach", String.valueOf(SFARDI));
			break;
		case SFARDI:
			edit.putString("nusach", String.valueOf(ASHKENAZ));
			break;

		default:
			break;
		}

		if(!getIntent().getBooleanExtra("longPress", false)){
			edit.apply();
			nusach=Integer.valueOf(sharedPref.getString("nusach", "0"));
		}
		setNusachTogglerText(v);
		getIntent().removeExtra("longPress");
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		prepareRotationIcon(menu);
		return super.onPrepareOptionsMenu(menu);
	}


	public boolean onCreateOptionsMenu(Menu menu){
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.zmanimabar, menu);

		return super.onCreateOptionsMenu(menu);
	}


	private void toggleAlos(TextView tv){
		tv.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				TextView alosTitle = (TextView) findViewById(R.id.textView7);
				if(!sharedPref.getBoolean("alosGreen", true)){
					TextView alos = (TextView) findViewById(R.id.Alos);
					alosTitle.setText(getResources().getStringArray(R.array.dailyZmanim)[0]);
					alos.setText(DateFormat.format(DATE_FORMAT, czc.getAlosHashachar()));
				}
				else{
					TextView alos = (TextView) findViewById(R.id.Alos);
					alos.setText(DateFormat.format(DATE_FORMAT, czc.getMisheyakir11Point5Degrees()));
					alosTitle.setText(getResources().getStringArray(R.array.dailyZmanim)[1]);
				}
				sharedPref.edit().putBoolean("alosGreen", !sharedPref.getBoolean("alosGreen", true)).commit();
			}
		});

	}

	private void toggleShma(final TextView tv, final ComplexZmanimCalendar czc){
		final TextView other = (TextView) findViewById(R.id.weeksParsha1);
		tv.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if(sharedPref.getBoolean("shmaZman", true)){
					tv.setText(DateFormat.format(DATE_FORMAT, czc.getSofZmanShmaMGA()));
					other.setText(R.string.shmaMA);
				}

				else{
					tv.setText(DateFormat.format(DATE_FORMAT, czc.getSofZmanShmaGRA()));
					other.setText(R.string.SofZman);
				}
				sharedPref.edit().putBoolean("shmaZman", !sharedPref.getBoolean("shmaZman", true)).commit();
			}
		});

	}

	private void toggleTzeis (final TextView tv, final ComplexZmanimCalendar czc){
		final TextView other = (TextView) findViewById(R.id.weeksParsha7);
		tv.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if(!sharedPref.getBoolean("rebeinuTom", false)){
					tv.setText(DateFormat.format(DATE_FORMAT, czc.getTzais72()));
					other.setText(R.string.tzeisRT);
				}
				else{
					tv.setText(DateFormat.format(DATE_FORMAT, czc.getTzais()));
					other.setText(R.string.tzeis);
				}
				sharedPref.edit().putBoolean("rebeinuTom", !sharedPref.getBoolean("rebeinuTom", false)).commit();
			}
		});
	}

	private void toggleStar(){
		getIntent().putExtra("longPress", true);
		if(sharedPref.getString("nusach", "0").equals("0")){
			sharedPref.edit().putBoolean("modernAsh", !sharedPref.getBoolean("modernAsh", false)).commit();
		}
	}
	protected static boolean isProInstalled(Context context) {
		// the packagename of the 'key' app
		String proPackage = "com.mattaniahbeezy.icecreamsiddurdonatekey";

		// get the package manager
		final PackageManager pm = context.getPackageManager();

		// get a list of installed packages
		List<PackageInfo> list = pm.getInstalledPackages(PackageManager.GET_DISABLED_COMPONENTS);

		// let's iterate through the list
		Iterator<PackageInfo> i = list.iterator();
		while(i.hasNext()) {
			PackageInfo p = i.next();
			// check if proPackage is in the list AND whether that package is signed
			//  with the same signature as THIS package
			if((p.packageName.equals(proPackage)))
				return true;
		}
		return false;
	}

}



