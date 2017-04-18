package com.mattaniahbeezy.wisechildkinos;

import java.util.Calendar;
import java.util.TimeZone;

import net.sourceforge.zmanim.ComplexZmanimCalendar;
import net.sourceforge.zmanim.hebrewcalendar.JewishCalendar;
import net.sourceforge.zmanim.util.GeoLocation;
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationManager;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.text.Editable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.text.style.StyleSpan;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextClock;
import android.widget.TextView;
import android.widget.TextView.BufferType;

import com.crashlytics.android.Crashlytics;

import io.fabric.sdk.android.Fabric;


public abstract class MainActivity extends Activity{
	private String[] drawerListViewItems;
	private ListView drawerListView;
	private DrawerLayout drawer;
	private ActionBarDrawerToggle drawerToggle;
	protected int content;

	protected static final int SPINNER_LAYOUT=R.layout.spinnerlist;

	protected ScaleGestureDetector SGD;
	protected GestureDetector detector;

	private static int SWIPE_MIN_DISTANCE_DIP = 80;
	private static int SWIPE_MIN_DISTANCE;
	private static final int SWIPE_MAX_OFF_PATH = 250;
	private static final int SWIPE_THRESHOLD_VELOCITY = 200;

	protected static final int SMALL_TEXT=-7;
	protected static final int LARGER_TEXT=5;
	protected static final int TITLE_TEXT=2;
	protected static final int PADDING_DIP=15;
	protected int PADDING;

	protected static final CharSequence FORMAT_12HRS="h:mm";
	protected static final CharSequence FORMAT_24HRS="k:mm";
	protected static CharSequence DATE_FORMAT;

	protected JewishCalendar jDate;
	protected JewishCalendar jTomorrow;

	protected SharedPreferences sharedPref;
	View.OnTouchListener gestureListener;

	protected boolean erevTisha;
	protected boolean tisha;


	protected static final String SIZE_PREF="size";
	protected float size;
	private AudioManager am;
	protected boolean tomorrow;
	protected static final String INHIBIT_ABAR="inhibitAbar";

	protected Typeface tf;
	protected LinearLayout liz;
	protected ScrollView scroller;
	protected Animation inAn;
	protected Animation outAn;
	protected Animation prevInAn;
	protected Animation prevOutAn;

	protected static final int GPS_LOCATION=0;
	protected static final int NETWORK_LOCATION=1;
	protected static final int SAVED_LOCATION=2;

	protected int nusach;
	protected static final String NUSACH_PREF="nusach";
	protected static final int ASHKENAZ=0;
	protected static final int STUPID=1;
	protected static final int SFARDI=2;

	protected int style;
	protected static final String STYLE_PREF="style";
	protected static final String BEDTIME_STYLE_PREF="bedtimestyle";
	protected static final int SEPIA=0;
	protected static final int NIGHT=1;
	protected static final int DAY=2;

	protected static String SHAKE_INHIBITOR="shakeyInhibitor";
	protected static final int QUICK_ZMAN_TOP_PADDING_DIP=30;
	protected int QUICK_ZMAN_TOP_PADDING;
	protected static final float TEXT_CLOCK_SIZE=25f;
	protected static final float QUICK_ZMAN_SIZE=20f;

	private SensorManager mSensorManager;
	private float mAccel; // acceleration apart from gravity
	private float mAccelCurrent; // current acceleration including gravity
	private float mAccelLast; // last acceleration including gravity
	private int SHAKEYSPEED=SensorManager.SENSOR_DELAY_UI;

	private static final String MARKETLINK="market://details?id=com.mattaniahbeezy.wisechildkinos";
	private static final String FULLSCREEN="fullScreen";

	protected ComplexZmanimCalendar czc;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (getIntent().getBooleanExtra("EXIT", false)) {			
			finish();
		}
		Fabric.with(this, new Crashlytics());
		setContentView(content);
		initAll();

	}

	protected void initAll(){
		sharedPref = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);	
		setjDate();

		tf = Typeface.createFromAsset(getAssets(), sharedPref.getString("font", "TaameyFrankCLM.ttf"));
		try{
			nusach=Integer.valueOf(sharedPref.getString(NUSACH_PREF, "0"));

			try {
				size = Float.parseFloat(sharedPref.getString(SIZE_PREF, "30"));
			} catch (NumberFormatException ex) {
				sharedPref.edit().putString(SIZE_PREF, "30").apply();;
			}
			style = Integer.valueOf(sharedPref.getString(STYLE_PREF, "0"));
			if(getIntent().getBooleanExtra("bedtime", false))
				style=Integer.valueOf(sharedPref.getString(BEDTIME_STYLE_PREF, "1"));
		}
		catch(NumberFormatException e){
			sharedPref.edit().putString(NUSACH_PREF, "0").apply();
			sharedPref.edit().putString(STYLE_PREF, "0").apply();
			sharedPref.edit().putString(BEDTIME_STYLE_PREF, "1").apply();
		}


		try {
			SWIPE_MIN_DISTANCE_DIP = (int) Float.parseFloat(sharedPref.getString("swipeSense", "80"));
		} catch (NumberFormatException ex) {
			SharedPreferences.Editor edit = sharedPref.edit();
			edit.putString("swipeSense", "80");
			edit.commit();
		}

		if(sharedPref.getBoolean("animation", true)){
			inAn = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.enter_animation);
			outAn = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.exit_animation);
			prevInAn=AnimationUtils.loadAnimation(getApplicationContext(), R.anim.enter_animation_previous);
			prevOutAn=AnimationUtils.loadAnimation(getApplicationContext(), R.anim.exit_animation_previous);
		}

		initMeasurements();
		setZmanim();
		setUpShakey();
		scroller=(ScrollView) findViewById(R.id.content_frame);
		if(content==R.layout.content_drawer)
			initLayout();

		am = (AudioManager)getSystemService(Context.AUDIO_SERVICE);

		uiStuff();
		initGestures();
		setDrawerNav();
	}

	private void initMeasurements(){
		PADDING=(int) dipToPixels(PADDING_DIP);
		QUICK_ZMAN_TOP_PADDING=(int) dipToPixels(QUICK_ZMAN_TOP_PADDING_DIP);
		SWIPE_MIN_DISTANCE=(int) dipToPixels(SWIPE_MIN_DISTANCE_DIP);
	}

	protected void partialReInit(){
		uiStuff();
		setContentView(content);
		initGestures();
		setDrawerNav();
	}

	private void initGestures() {
		detector=new GestureDetector(this, new MyGestureDetector());
		SGD= new ScaleGestureDetector(this, new ScaleListener());
		if(sharedPref.getBoolean("swipe", true)){
			gestureListener = new View.OnTouchListener() {
				public boolean onTouch(View v, MotionEvent event) {
					SGD.onTouchEvent(event);
					return detector.onTouchEvent(event);
				}
			};
		}
		final View v = findViewById(R.id.content_frame);
		v.setOnTouchListener(gestureListener);

	}


	private void setDrawerNav(){
		drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
		drawerListViewItems = getResources().getStringArray(R.array.drawerNav);
		drawerListView = (ListView) findViewById(R.id.left_drawer);
		drawerListView.setAdapter(new ArrayAdapter<String>(this,
				R.layout.listitems, drawerListViewItems));
		drawerListView.setOnItemClickListener(new DrawerItemClickListener());

		setDrawerToggle();

	}


	private void setDrawerToggle(){
		drawerToggle = new ActionBarDrawerToggle(this, drawer,
				R.drawable.ic_drawer, 
				R.string.drawer_open, R.string.drawer_close);
		drawer.setDrawerListener(drawerToggle);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setHomeButtonEnabled(true);
	}

	private class DrawerItemClickListener implements ListView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			int switchIndex=position;

			switch (switchIndex) {
			case 0:
				Intent zero = new Intent(MainActivity.this, Main.class);
				startActivity(zero);
				break;
			case 1:
				Intent kivun = new Intent(MainActivity.this, Kinos.class);
				startActivity(kivun);
				break;
			case 2:
				Intent first = new Intent(MainActivity.this, Eicha.class);
				startActivity(first);
				break;
			case 3:
				Intent second = new Intent(MainActivity.this, MaarivActivity.class);
				startActivity(second);
				break;
			case 4:
				Intent third =new Intent(MainActivity.this, ShacharisActivity.class);
				startActivity(third);
				break;
			case 5:
				Intent fourth =new Intent(MainActivity.this, MinchaActivity.class);
				startActivity(fourth);
				break;
			case 8:
				Intent fifth =new Intent(MainActivity.this, Bedtime.class);
				startActivity(fifth);
				break;
			case 7:
				Intent sixth =new Intent(MainActivity.this, Birkas_levana.class);
				startActivity(sixth);
				break;
			case 6:
				Intent chumash=new Intent(MainActivity.this, Halacha.class);
				startActivity(chumash);
				break;
			case 9:
				Intent psalms=new Intent(MainActivity.this, AppSettings.class);
				startActivity(psalms);
				break;
			default:
				break;}
		}

	}
	protected void englishDialog(View tv, final int id, final Context context){
		englishDialog(tv, getResources().getText(id), context);
	}

	protected void englishDialog(View tv, final CharSequence id, final Context context){
		tv.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				AlertDialog.Builder builder= new AlertDialog.Builder(context);
				LayoutInflater grumps = MainActivity.this.getLayoutInflater();
				View view = grumps.inflate(R.layout.englishdialog, null);
				builder.setView(view);
				TextView text = (TextView) view.findViewById(R.id.englishText);
				text.setText(id);
				TextView close = (TextView) view.findViewById(R.id.englishClose);
				View scroll = view.findViewById(R.id.englishScroll);
				LinearLayout ll = (LinearLayout) view.findViewById(R.id.englishLL);
				String key = STYLE_PREF;
				if(getIntent().getBooleanExtra("bedtime", false)){
					key=BEDTIME_STYLE_PREF;
				}
				if(id.length()>810){
					scroll.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, (int) (600 / (context.getResources().getDisplayMetrics().densityDpi / 160f))));
				}
				if(sharedPref.getString(key, "sepia").equals("night")){
					text.setTextColor(getResources().getColor(android.R.color.primary_text_dark));
					close.setTextColor(getResources().getColor(android.R.color.primary_text_dark));
					scroll.setBackgroundColor(getResources().getColor(android.R.color.background_dark));
					ll.setBackgroundColor(getResources().getColor(android.R.color.background_dark));
				}
				if(sharedPref.getString(key, "sepia").equals("day")){
					scroll.setBackgroundColor(getResources().getColor(android.R.color.background_light));
					ll.setBackgroundColor(getResources().getColor(android.R.color.background_light));
				}
				final AlertDialog english = builder.create();
				close.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						english.dismiss();

					}
				});

				english.show();
			}
		});
		tv.setOnTouchListener(gestureListener);

	}
	protected void setText(TextView tv, int hebID, int engID, Context context){
		tv.setText(hebID);
		tv.setVisibility(View.VISIBLE);
		if(sharedPref.getBoolean("english", false)&& engID!=0){
			englishDialog(tv, engID, context);
		}
	}

	protected void setText(TextView tv, int hebID, String engID, Context context){
		tv.setText(hebID);
		tv.setVisibility(View.VISIBLE);
		if(sharedPref.getBoolean("english", false)&& engID!=null){
			englishDialog(tv, engID, context);
		}
	}

	protected void setText(TextView tv, CharSequence hebID, CharSequence engID, Context context){
		tv.setText(hebID);
		tv.setVisibility(View.VISIBLE);
		if(sharedPref.getBoolean("english", false)&& engID!=null){
			englishDialog(tv, engID, context);
		}
	}

	protected void setText(TextView tv, CharSequence hebID, int engID, Context context){
		tv.setText(hebID);
		tv.setVisibility(View.VISIBLE);
		if(sharedPref.getBoolean("english", false)&& engID!=0){
			englishDialog(tv, engID, context);
		}
	}



	protected void onPostCreate(Bundle savedInstanceState){
		super.onPostCreate(savedInstanceState);
		drawerToggle.syncState();
		if(getIntent().getBooleanExtra("restart", false)){
			scroller.post(new Runnable() {
				@Override
				public void run() {
					scroller.scrollTo(0, getIntent().getIntExtra("restartY", 0));
				}
			});
		}
	}


	protected float dipToPixels(float dipValue) {
		DisplayMetrics metrics = this.getResources().getDisplayMetrics();
		return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dipValue, metrics);
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		if(drawerToggle.onOptionsItemSelected(item)){
			return true;
		}

		switch (item.getItemId()) {
		case R.id.next:
			nextActivity();
			break;

		case R.id.previous:
			previousActivity();			
			break;

		case R.id.displayOptions:
			displayOptions();
			break;
		case R.id.SettingsButton:
			startActivity(new Intent(MainActivity.this, AppSettings.class));
			break;
		case R.id.close:
			exitSiddur();
			return true;

		case R.id.shakeButton:
			onShaken();
			break;
		case R.id.about:
			openAbout();
			break;
		case R.id.rotation:
			toggleRotation();
			break;
		default:
			break;	

		}

		return false;
	}

	protected void prepareRotationIcon(Menu menu){
		Drawable icon = getResources().getDrawable(R.drawable.ic_action_screen_rotation);
		CharSequence rotate="Lock Rotation";
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
		if(sharedPref.getBoolean("portrait", false)){
			icon=getResources().getDrawable(R.drawable.ic_action_screen_locked_to_portrait);
			rotate="Unlock Rotation";
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);
		}
		menu.findItem(R.id.rotation).setIcon(icon);
		menu.findItem(R.id.rotation).setTitle(rotate);
	}
	private void toggleRotation(){
		sharedPref.edit().putBoolean("portrait", !sharedPref.getBoolean("portrait", false)).apply();
		invalidateOptionsMenu();
	}


	protected void openAbout(){
		AlertDialog.Builder about = new AlertDialog.Builder(MainActivity.this);
		LayoutInflater grumps = MainActivity.this.getLayoutInflater();
		View aboutView = grumps.inflate(R.layout.about, null);
		about.setView(aboutView);
		about.setNegativeButton("Close", null);
		about.setNeutralButton("Send Report", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				final Intent emailIntent = new Intent( android.content.Intent.ACTION_SEND);

				emailIntent.setType("plain/text");

				emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL,
						new String[] { "mattaniah.beezy@gmail.com" });

				emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,
						"WiseChild Kinos");

				emailIntent.putExtra(android.content.Intent.EXTRA_TEXT,
						"Thanks for your feedback");

				startActivity(Intent.createChooser(
						emailIntent, "Send mail..."));

			}
		});
		about.setPositiveButton("Get Ice Cream Siddur", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				openDonateKey();
			}
		});
		about.setTitle("About");
		about.setIcon(R.drawable.ic_action_about);
		TextView version = (TextView) aboutView.findViewById(R.id.versionNumber);
		PackageInfo pInfo = null;
		try {
			pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		aboutView.setScrollbarFadingEnabled(false);
		String vNum = pInfo.versionName;
		version.setText("WiseChild Kinos Version " + vNum);
		version.setPadding(0, 0, 0, 20);
		version.setTypeface(null, Typeface.BOLD);

		TextView ask = (TextView) aboutView.findViewById(R.id.rateAsk);
		ask.setText("\nRate on the Play Store");
		ask.setTextColor(getResources().getColor(R.color.blue));
		ask.setTypeface(null, Typeface.BOLD);
		ask.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setAction(Intent.ACTION_VIEW);
				intent.addCategory(Intent.CATEGORY_BROWSABLE);
				intent.setData(Uri.parse(MARKETLINK));
				startActivity(intent);
			}
		});


		AlertDialog aboutD = about.create();
		aboutD.show();
	}

	private void openDonateKey(){
		Intent intent = new Intent();
		intent.setAction(Intent.ACTION_VIEW);
		intent.addCategory(Intent.CATEGORY_BROWSABLE);
		intent.setData(Uri.parse("market://details?id=com.mattaniahbeezy.icecreamsiddur"));
		startActivity(intent);
	}

	protected void exitSiddur(){
		AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
		builder.setPositiveButton("Exit", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {

				if(sharedPref.getBoolean("silence", true)){
					am.setRingerMode(sharedPref.getInt("ringer", AudioManager.RINGER_MODE_NORMAL));
				}
				Intent intent = new Intent(MainActivity.this, Main.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
				intent.putExtra("EXIT", true);
				startActivity(intent);

			}
		});
		builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				// User cancelled the dialog
			}
		});
		builder.setMessage("Exit and Restore Sound?")
		.setTitle("Exit");
		AlertDialog dialog = builder.create();
		dialog.show();

	}


	protected int getParsha(){
		int parshaIndex;
		JewishCalendar parshaDate=(JewishCalendar)jDate.clone();
		while(parshaDate.getParshaIndex()<0){
			parshaDate.forward();
		}
		parshaIndex=parshaDate.getParshaIndex();
		if(parshaIndex>52){
			parshaIndex=doubleParshaFixer(parshaIndex);
		}
		return parshaIndex;
	}
	private int doubleParshaFixer(int parshaIndex){
		switch(parshaIndex){
		case 53:
			parshaIndex=21;
			break;
		case 54:
			parshaIndex=26;
			break;
		case 55:
			parshaIndex=28;
			break;
		case 56:
			parshaIndex=31;
			break;
		case 57:
			parshaIndex=38;
			break;
		case 58:
			parshaIndex=41;
			break;
		case 59:
			parshaIndex=50;
			break;
		}
		return parshaIndex;
	}

	protected void zoomOut(){
		int currentSize=Integer.valueOf(sharedPref.getString(SIZE_PREF, "30"));
		if((currentSize-5)<15)
			currentSize=20;
		sharedPref.edit().putString(SIZE_PREF, String.valueOf(currentSize-5)).commit();
		size = Float.parseFloat(sharedPref.getString(SIZE_PREF, "30"));
	}

	protected void zoomIn(){
		int currentSize=Integer.valueOf(sharedPref.getString(SIZE_PREF, "30"));
		sharedPref.edit().putString(SIZE_PREF, String.valueOf(currentSize+5)).commit();
		size = Float.parseFloat(sharedPref.getString(SIZE_PREF, "30"));
	}

	private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
		@Override
		public boolean onScale(ScaleGestureDetector detector) {
			float scaleFactor = detector.getScaleFactor();

			if (scaleFactor > 1) {
				if(sharedPref.getBoolean("pinch", true))
					zoomIn();
			} else {
				if(sharedPref.getBoolean("pinch", true))
					zoomOut();
			}
			return true;
		}
	}
	class MyGestureDetector extends SimpleOnGestureListener {
		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
			try {
				if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH)

					return false;
				// right to left swipe
				if(e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {					
					previousActivity();
					return true;
					//				left to right swipe
				}  else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
					nextActivity();
					return true;
				}
			} catch (Exception e) {
				// nothing
			}
			return false;
		}

		@Override
		public boolean onDoubleTap(MotionEvent e) {
			if(sharedPref.getBoolean("doubleTap", true)){
				toggleFullscreen();	
			}
			return true;
		}

		@Override
		public boolean onSingleTapConfirmed(MotionEvent e) {
			if(!sharedPref.getBoolean("english", false)&&sharedPref.getBoolean("singleTap", true)){
				toggleABar();
			}
			return super.onSingleTapConfirmed(e);
		}

		@Override
		public void onLongPress(MotionEvent e) {
			if(sharedPref.getBoolean("longPress", true)){
				exitSiddur();
			}
			super.onLongPress(e);
		}

	}



	public void toggleABar(){
		ActionBar actionBar = getActionBar();
		if(actionBar.isShowing()){
			actionBar.hide();
		}
		else{
			getIntent().putExtra("inhibitAbar", true);
			actionBar.show();
		}

	}

	public void toggleFullscreen(){
		sharedPref.edit().putBoolean(FULLSCREEN, !sharedPref.getBoolean(FULLSCREEN, false)).apply();
		getIntent().putExtra(FULLSCREEN, true);
		restartActivity();
	}


	protected void silence(){
		AudioManager am = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
		if(sharedPref.getBoolean("Silence", true)){
			am.setRingerMode(AudioManager.RINGER_MODE_SILENT);}
	}

	protected void initLayout(){
		liz = new LinearLayout(getApplicationContext());
		liz.setPadding(PADDING, PADDING, PADDING, PADDING);
		liz.setOrientation(LinearLayout.VERTICAL);
		scroller.addView(liz);

		switch(style){
		case NIGHT:
			liz.setBackgroundColor(getResources().getColor(android.R.color.background_dark));
			scroller.setBackgroundColor(getResources().getColor(android.R.color.background_dark));
			break; 
		case DAY:
			liz.setBackgroundColor(getResources().getColor(android.R.color.background_light));
			scroller.setBackgroundColor(getResources().getColor(android.R.color.background_light));
			break;
		case SEPIA:
			liz.setBackgroundColor(getResources().getColor(R.color.sepia));
			scroller.setBackgroundColor(getResources().getColor(R.color.sepia));
			break;
		}

	}


	protected void extraPrayer(final TextView tv, final int id){		
		if(!sharedPref.getBoolean("english", false)){
			tv.append(" *");

			tv.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					tv.setText(id);
					tv.setOnTouchListener(gestureListener);
				}
			});
		}
	}



	protected void sickPrayer (final TextView tv, final Context context){
		tv.append(" *");
		tv.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {			
				tv.setText(tefilaBadCholeh(), BufferType.SPANNABLE);
				tv.setOnLongClickListener(new View.OnLongClickListener() {
					@Override
					public boolean onLongClick(View v) {
						return changeSickNames(tv, context);
					}
				});
			}
		});
	}

	protected SpannableString tefilaBadCholeh(){
		CharSequence names = " "+  sharedPref.getString("sickNames", getResources().getString(R.string.ploni)) +" ";
		CharSequence prayer = null;
		switch(nusach){
		case ASHKENAZ:
			prayer =getResources().getString(R.string.tefilah_bad_choleh1) +  names+ getResources().getText(R.string.tefilah_bad_choleh2);		
			break;
		case STUPID:
			prayer =getResources().getString(R.string.rephaeinu_stupid_long1) +  names+ getResources().getText(R.string.rephaeinu_stupid_long2);		
			break;
		case SFARDI:
			prayer =getResources().getString(R.string.refaeinu_long_sfardi1) +  names+ getResources().getText(R.string.refaeinu_long_sfardi2);		
			break;
		}


		SpannableString first = new SpannableString(prayer);
		first.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 
				0,11, 0);

		return first;
	}

	private boolean changeSickNames(final TextView tv, Context context){
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle("Change names");
		final EditText edit = new EditText(context);
		builder.setView(edit);
		edit.setText(sharedPref.getString("sickNames", ""));
		builder.setPositiveButton("Save", new OnClickListener() {

			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				CharSequence prayer = null;
				sharedPref.edit().putString("sickNames", edit.getText().toString()).commit();
				if(edit.getText().length()<1){
					sharedPref.edit().putString("sickNames", getResources().getString(R.string.ploni)).commit();
				}
				CharSequence names = " " + sharedPref.getString("sickNames", getResources().getString(R.string.ploni))+" ";

				switch(nusach){
				case ASHKENAZ:
					prayer = getResources().getString(R.string.tefilah_bad_choleh1)+  names+ getResources().getText(R.string.tefilah_bad_choleh2);		
					break;
				case STUPID:
					prayer =getResources().getString(R.string.rephaeinu_stupid_long1) +  names+ getResources().getText(R.string.rephaeinu_stupid_long2);		
					break;
				case SFARDI:
					prayer =getResources().getString(R.string.refaeinu_long_sfardi1) +  names+ getResources().getText(R.string.refaeinu_long_sfardi2);		
					break;
				}

				SpannableString first = new SpannableString(prayer);
				first.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 
						0,11, 0);
				tv.setText(first, BufferType.SPANNABLE);
			}
		});
		builder.create().show();
		return false;
	}

	private final SensorEventListener mSensorListener = new SensorEventListener() {

		public void onSensorChanged(SensorEvent se) {
			float x = se.values[0];
			float y = se.values[1];
			float z = se.values[2];
			mAccelLast = mAccelCurrent;
			mAccelCurrent = (float) Math.sqrt((double) (x*x + y*y + z*z));
			float delta = mAccelCurrent - mAccelLast;
			mAccel = mAccel * 0.9f + delta; // perform low-cut filter
			try{
				if(mAccel>8){
					onShaken();
				}
			}
			catch(NullPointerException e){

			}
		}

		public void onAccuracyChanged(Sensor sensor, int accuracy) {
		}
	};
	@Override
	protected void onResume() {
		super.onResume();
		try{
			if(sharedPref.getBoolean("shakey", true)&&czc!=null){
				mSensorManager.registerListener(mSensorListener, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SHAKEYSPEED);
			}
		}
		catch(NullPointerException e){
			restartActivity();
		}
	}

	private void setZmanim(){
		try{
			Double longitude=0D;
			Double latitude=0D;
			Double elevation=0D;

			LocationManager lm = (LocationManager)getSystemService("location");
			Location location = lm.getLastKnownLocation("gps");
			sharedPref.edit().putInt("locationValue", GPS_LOCATION).apply();
			if (location == null){
				location = lm.getLastKnownLocation("network");
				sharedPref.edit().putInt("locationValue", NETWORK_LOCATION);
			}
			if (location != null||sharedPref.getBoolean("locationSaved", false)||sharedPref.getBoolean("manualLoc", false)){
				if(location!=null){
					longitude = Double.valueOf(location.getLongitude());
					latitude = Double.valueOf(location.getLatitude());
					elevation = Double.valueOf(location.getAltitude());

					sharedPref.edit().putLong("Latitude", Double.doubleToLongBits(location.getLatitude())).commit();
					sharedPref.edit().putLong("Longitude", Double.doubleToLongBits(location.getLongitude())).commit();
					sharedPref.edit().putLong("Elevation", Double.doubleToLongBits(location.getAltitude())).commit();
					sharedPref.edit().putBoolean("locationSaved", true).commit();
				}

				if(location==null||sharedPref.getBoolean("manualLoc", false)){
					longitude=Double.longBitsToDouble(sharedPref.getLong("Longitude", 0));
					latitude=Double.longBitsToDouble(sharedPref.getLong("Latitude", 0));
					elevation=Double.longBitsToDouble(sharedPref.getLong("Elevation", 0));
					sharedPref.edit().putInt("locationValue", SAVED_LOCATION);
				}




				GeoLocation here = new GeoLocation("geo", latitude.doubleValue(), longitude.doubleValue(), elevation.doubleValue(), TimeZone.getDefault());
				if (elevation.doubleValue() < 0.0D)
					here = new GeoLocation("geo", latitude.doubleValue(), longitude.doubleValue(), TimeZone.getDefault());

				czc = new ComplexZmanimCalendar(here);
				Calendar cal = Calendar.getInstance();
				cal.set(Calendar.DAY_OF_MONTH, jDate.getGregorianDayOfMonth());
				cal.set(Calendar.MONTH, jDate.getGregorianMonth());
				cal.set(Calendar.YEAR, jDate.getGregorianYear());
				czc.setCalendar(cal);

				DATE_FORMAT=FORMAT_24HRS;
				if(sharedPref.getBoolean("amZman", false)){
					DATE_FORMAT=FORMAT_12HRS;
				}
				tomorrow=Calendar.getInstance().getTime().after(czc.getSunset());
			}
		}
		catch(IllegalArgumentException e){
		}

	}
	protected void setjDate(){
		jDate=new JewishCalendar();
		while(jDate.getYomTovIndex()!=JewishCalendar.TISHA_BEAV)
			jDate.forward();

		jTomorrow=(JewishCalendar) jDate.clone();
		jTomorrow.forward();
	}
	protected void setUpShakey(){
		mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		if(sharedPref.getBoolean("shakey", true)&&czc!=null){
			mSensorManager.registerListener(mSensorListener, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SHAKEYSPEED);	
		}
		mAccel = 0.00f;
		mAccelCurrent = SensorManager.GRAVITY_EARTH;
		mAccelLast = SensorManager.GRAVITY_EARTH;
	}

	@Override
	protected void onPause() {
		try{
			if(sharedPref.getBoolean("shakey", true)&&czc!=null){
				mSensorManager.unregisterListener(mSensorListener);
			}
		}
		catch(NullPointerException e){
			restartActivity();
		}
		super.onPause();
	}

	@Override
	protected void onDestroy(){
		try{
			if(sharedPref.getBoolean("shakey", true)&&czc!=null){
				mSensorManager.unregisterListener(mSensorListener);
			}
		}
		catch(NullPointerException e){

		}
		super.onDestroy();
	}

	@SuppressLint("NewApi")
	private View makeTextClock(){
		if(Build.VERSION.SDK_INT>=17){
			TextClock tv1 = new TextClock(MainActivity.this);
			((TextClock) tv1).setFormat12Hour("h:mm");
			((TextClock) tv1).setFormat24Hour("h:mm");
			tv1.setPadding(0, 0, 0, 25);
			tv1.setGravity(Gravity.CENTER);
			tv1.setTextSize(size+10);
			tv1.setTypeface(null, Typeface.BOLD);
			return tv1;
		}
		else{
			TextView tv1= new TextView(MainActivity.this);
			tv1.setPadding(0, 0, 0, 25);
			tv1.setGravity(Gravity.CENTER);
			tv1.setTextSize(size+10);
			tv1.setTypeface(null, Typeface.BOLD);
			return tv1;
		}
	}

	protected void onShaken(){
		AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
		setShakeyDismissListener(builder);
		ScrollView scroll = new ScrollView(MainActivity.this);
		builder.setView(scroll);
		scroll.addView(createShakenView());
		if(!getIntent().getBooleanExtra(SHAKE_INHIBITOR, false)){
			builder.create().show();
			getIntent().putExtra(SHAKE_INHIBITOR, true);
		}
	}

	private View createShakenView(){
		LinearLayout frame = new LinearLayout(MainActivity.this);
		frame.setOrientation(LinearLayout.VERTICAL);
		frame.addView(createTextClock());
		frame.addView(createQuickZmanimFrame());
		return frame;
	}

	private View createQuickZmanimFrame(){
		LinearLayout frame = new LinearLayout(MainActivity.this);
		frame.setOrientation(LinearLayout.HORIZONTAL);
		frame.setGravity(Gravity.CENTER_HORIZONTAL);
		frame.addView(quickZmanimTimesLayout(new LinearLayout(MainActivity.this)));
		frame.addView(quickZmanimLablesLayout(new LinearLayout(MainActivity.this)));
		return frame;
	}


	protected View quickZmanimLablesLayout(LinearLayout linearLayout) {
		return linearLayout;
	}

	protected View quickZmanimTimesLayout(LinearLayout linearLayout) {
		return linearLayout;
	}

	private View createTextClock(){
		if(Build.VERSION.SDK_INT>=17)
			return textClockNew();
		else
			return textClockOld();

	}

	@SuppressLint("NewApi")
	private View textClockNew(){
		TextClock clock = new TextClock(MainActivity.this);
		clock.setTextSize(TEXT_CLOCK_SIZE);
		clock.setTypeface(null, Typeface.BOLD);
		clock.setGravity(Gravity.CENTER_HORIZONTAL);
		clock.setFormat12Hour(FORMAT_12HRS);
		clock.setFormat24Hour(FORMAT_24HRS);
		return clock;
	}

	private View textClockOld(){
		TextView clock = new TextView(MainActivity.this);
		clock.setTextSize(TEXT_CLOCK_SIZE);
		clock.setTypeface(null, Typeface.BOLD);
		clock.setText(DateFormat.format(DATE_FORMAT, Calendar.getInstance()));
		clock.setGravity(Gravity.CENTER_HORIZONTAL);
		return clock;
	}

	@SuppressLint("NewApi")
	protected void setShakeyDismissListener(AlertDialog.Builder builder){
		if(Build.VERSION.SDK_INT>=17)
			builder.setOnDismissListener(new OnDismissListener() {

				@Override
				public void onDismiss(DialogInterface dialog) {
					shakenDismiss();
				}
			});
		else
			builder.setOnCancelListener(new OnCancelListener() {

				@Override
				public void onCancel(DialogInterface dialog) {
					shakenDismiss();
				}
			});		
	}

	private void shakenDismiss(){
		getIntent().removeExtra(SHAKE_INHIBITOR);
	}



	protected  void nextActivity(){

	}
	protected  void previousActivity(){

	}


	protected void restartActivity(){
		getIntent().putExtra("restartY", scroller.getScrollY());
		getIntent().putExtra("restart", true);
		getIntent().addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
		startActivity(getIntent());
		overridePendingTransition(0, 0);
		finish();
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		if (hasFocus) {
			uiStuff();
		}
	}

	protected void uiStuff(){
		if(!sharedPref.getBoolean("swipe", true)){
			getActionBar().show();
		}
		if(sharedPref.getBoolean("portrait", false)){
			setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		}
		if(sharedPref.getBoolean("screenOn", false)){
			getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		}

		View decorView=getWindow().getDecorView();

		decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
		if (sharedPref.getBoolean(FULLSCREEN, false)) {
			getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
					WindowManager.LayoutParams.FLAG_FULLSCREEN);
			if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.KITKAT){
				decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN| View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY|View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
			}			
		}

		if(!sharedPref.getBoolean(FULLSCREEN, false)) {
			getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
			decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);
		}

	}



	private void displayOptions(){
		AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
		builder.setTitle("Display Options");
		builder.setIcon(R.drawable.ic_action_settings);

		String font=sharedPref.getString("font", "TaameyFrankCLM.ttf");
		int DAVID=0;
		int HADASIM=1;
		int MEKOROT=2;
		int FRANK=3;

		int fPrev=FRANK;
		if(font.equals("DavidCLM-Medium.ttf")){
			fPrev=DAVID;
		}
		if(font.equals("HadasimCLM-Regular.ttf")){
			fPrev=HADASIM;
		}
		if(font.equals("Mekorot-Vilna.ttf")){
			fPrev=MEKOROT;
		}
		if(font.equals("TaameyFrankCLM.ttf")){
			fPrev=FRANK;
		}



		LayoutInflater inflater = MainActivity.this.getLayoutInflater();
		View promptsView = inflater.inflate(R.layout.displayoptions, null);
		builder.setView(promptsView);

		final SharedPreferences.Editor edit = sharedPref.edit();


		builder.setPositiveButton("Save changes", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int id) {
				edit.commit();
				style = Integer.parseInt(sharedPref.getString(STYLE_PREF, "0"));
				nusach=Integer.parseInt(sharedPref.getString(NUSACH_PREF, "0"));
				restartActivity();
			}
		});

		String[] nList= {"Ashkenaz", "Sfard", "Edot HaMizrach"};
		final Spinner nSpinner = (Spinner) promptsView.findViewById(R.id.nusachSpinner);
		ArrayAdapter<String> nusachAdapter = new ArrayAdapter<String>(getApplicationContext(),
				R.layout.optionsspinnerlist,
				nList);
		nSpinner.setAdapter(nusachAdapter);
		nSpinner.setSelection(Integer.parseInt(sharedPref.getString(NUSACH_PREF, "0")));
		nSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				switch (position) {
				case 0:
					edit.putString(NUSACH_PREF, String.valueOf(ASHKENAZ));
					break;
				case 1:
					edit.putString(NUSACH_PREF, String.valueOf(STUPID));
					break;
				case 2:
					edit.putString(NUSACH_PREF, String.valueOf(SFARDI));
					break;
				default:
					break;
				}

			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});

		String[] tList= getResources().getStringArray(R.array.themeList);
		final Spinner tSpinner = (Spinner) promptsView.findViewById(R.id.themeSpinner);
		ArrayAdapter<String> themeAdapter = new ArrayAdapter<String>(getApplicationContext(),
				R.layout.optionsspinnerlist,
				tList);
		tSpinner.setAdapter(themeAdapter);
		tSpinner.setSelection(style);
		tSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				String gangman=STYLE_PREF;
				if(getIntent().getBooleanExtra("Bedtime", false)){
					gangman=BEDTIME_STYLE_PREF;
				}
				switch (position) {
				case 0:
					edit.putString(gangman, String.valueOf(SEPIA));
					break;
				case 1:
					edit.putString(gangman, String.valueOf(NIGHT));
					break;
				case 2:
					edit.putString(gangman, String.valueOf(DAY));
					break;
				default:
					break;
				}

			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});

		String[] fList= getResources().getStringArray(R.array.fontList);
		final Spinner fSpinner = (Spinner) promptsView.findViewById(R.id.fontSpinner);
		ArrayAdapter<String> fontAdapter = new ArrayAdapter<String>(getApplicationContext(),
				R.layout.optionsspinnerlist,
				fList);
		fSpinner.setAdapter(fontAdapter);
		fSpinner.setSelection(fPrev);
		fSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				switch (position) {
				case 0:
					edit.putString("font", "DavidCLM-Medium.ttf");
					break;
				case 1:
					edit.putString("font", "HadasimCLM-Regular.ttf");
					break;
				case 2:
					edit.putString("font", "Mekorot-Vilna.ttf");
					break;

				case 3:
					edit.putString("font", "TaameyFrankCLM.ttf");
					break;
				default:
					break;
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});

		final EditText sizeEdit = (EditText) promptsView.findViewById(R.id.sizeSpinner);
		sizeEdit.setText(sharedPref.getString(SIZE_PREF, "30"));
		sizeEdit.clearFocus();
		sizeEdit.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {					
			}

			@Override
			public void afterTextChanged(Editable s) {
				if(s.length()>0){
					edit.putString(SIZE_PREF, s.toString().trim());
				}
			}
		});



		AlertDialog dialog = builder.create();
		dialog.show();
	}


	public void showMenora(TextView tv){
		tv.append("*");
		tv.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
				ImageView menora = new ImageView(MainActivity.this);
				menora.setImageDrawable(getResources().getDrawable(R.drawable.menora));
				builder.setView(menora);
				builder.create().show();

			}
		});
	}


	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		boolean actionBar=getActionBar().isShowing();
		super.onConfigurationChanged(newConfig);
		getIntent().removeExtra("inhibitShakey");
		if(!actionBar)
			getActionBar().hide();
	}

	protected int getMinimText(){
		if(sharedPref.getBoolean("originalMinim", false))
			return R.string.minim;
		else
			return R.string.minim_original;
	}
}
