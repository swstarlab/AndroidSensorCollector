package kr.ac.snu.bi.sensorcollector;

import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import kr.ac.snu.bi.sensorcollector.app.Application;
import kr.ac.snu.bi.sensorcollector.collector.CollectorManager;
import kr.ac.snu.bi.sensorcollector.collector.LocationMonitor;
import kr.ac.snu.bi.sensorcollector.data.Act;
import kr.ac.snu.bi.sensorcollector.data.LocationData;
import kr.ac.snu.bi.sensorcollector.data.Venue;
import kr.ac.snu.bi.sensorcollector.network.ServerManager;
import kr.ac.snu.bi.sensorcollector.network.ServerResult;
import kr.ac.snu.bi.sensorcollector.tab.ActTab;
import kr.ac.snu.bi.sensorcollector.tab.FourSquareTab;
import kr.ac.snu.bi.sensorcollector.tab.LocationTab;

import android.app.ActionBar;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.NotificationCompat;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;


public class MainActivity extends FragmentActivity implements ActionBar.TabListener{
	
	
	private static final int HANDLE_FETCH_DATE = 1;
	private static final int HANDLE_RENDER_TIME = 2;
	
	private static final int FETCH_DATE_PERIOD = 3000;
	private static final int RENDER_TIME_PERIOD = 300;
	
	private SectionsPagerAdapter sectionsPagerAdapter;
	private ViewPager viewPager;
	
	private LocationTab locationTab;
	private FourSquareTab fourSquareTab;
	private ActTab actTab;
	
	private Application application;
	private CollectorManager collectorManager;
	private LocationMonitor locationMonitor;
	private Timer timer;
	
	
	public MainActivity() {
	}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

 		// Create the adapter that will return a fragment for each of the three
 		// primary sections of the app.
 		sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());

 		// Set up the ViewPager with the sections adapter.
 		viewPager = (ViewPager) findViewById(R.id.pager);
 		viewPager.setOffscreenPageLimit(4);
 		viewPager.setAdapter(sectionsPagerAdapter);

        application = Application.getInstance();
        if (!application.isInitialized()){
        	 application.initialize(this);
        }
        
        collectorManager = CollectorManager.getInstance();
        collectorManager.getLocationMonitor().start();
        
        locationMonitor = collectorManager.getLocationMonitor();
        
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				handler.sendEmptyMessage(HANDLE_FETCH_DATE);
			}
		}, 0, FETCH_DATE_PERIOD);
        
        timer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				handler.sendEmptyMessage(HANDLE_RENDER_TIME);
			}
		}, 0, RENDER_TIME_PERIOD);
        
        
        notifyPermanentIcon();
    }
    
    @Override
    public void onContentChanged() {
    	super.onContentChanged();
    }

    @Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.send_act_venue) {	
			return sendActVenue(); 
		}
		return super.onOptionsItemSelected(item);
	}
    
    @Override
    protected void onStart() {
    	super.onStart();
    }
    
    @Override
    protected void onDestroy() {
    	super.onDestroy();
    	
    	if (timer != null){
    		timer.cancel();
    		timer = null;
    	}
    }

	@Override
	public void onTabReselected(ActionBar.Tab tab,
			android.app.FragmentTransaction ft) {
	}

	@Override
	public void onTabSelected(ActionBar.Tab tab,
			android.app.FragmentTransaction ft) {
		viewPager.setCurrentItem(tab.getPosition());
	}

	@Override
	public void onTabUnselected(ActionBar.Tab tab,
			android.app.FragmentTransaction ft) {
	}
	
	private void notifyPermanentIcon(){
		Intent notificationIntent = new Intent(getApplicationContext(), MainActivity.class);
		notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |   Intent.FLAG_ACTIVITY_SINGLE_TOP);   // To open only one activity on launch.
		PendingIntent pIntent = PendingIntent.getActivity(getApplicationContext(), 0, notificationIntent, 0);
		
		NotificationCompat.Builder builder =
			    new NotificationCompat.Builder(this)
			    .setSmallIcon(R.drawable.ic_launcher)
			    .setOngoing(true)
			    .setContentTitle("Sensor Collector")
			    .setContentText("collect without a rest dude!!")
			    .setContentIntent(pIntent);
		
		int notificationId = 001;
		NotificationManager notifyMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		notifyMgr.notify(notificationId, builder.build());
	}
	
	private boolean sendActVenue(){
		
		if (actTab == null){
			Toast.makeText(this, R.string.no_act, Toast.LENGTH_LONG).show();
			return false;
		}
		
		final List<Act> actList = actTab.getCheckedActList();
		if (actList == null || actList.size() == 0){
			Toast.makeText(this, R.string.no_act, Toast.LENGTH_LONG).show();
			return false;
		}
		
		Venue venue = (fourSquareTab == null) ? null: fourSquareTab.getSelectedVenue();
		if (venue == null){
			venue = new Venue();
			venue.name = "null";
			venue.category = "null";
		}

        if (venue.isLatLongEmpty()){
            LocationData locationData = locationMonitor.createLocationData();
            double lat = 0;
            double lng = 0;

            if (locationData.gpsEnabled){
                lat = locationData.gpsLatitude;
                lng = locationData.gpsLongitude;
            } else {
                lat = locationData.netLatitude;
                lng = locationData.netLongitude;
            }

            venue.latitude = lat;
            venue.longitude = lng;
        }
		
		final Venue sendVenue = venue;
		final long time = actTab.getSelectedTime();
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				
				ServerManager sm = ServerManager.getInstance();
				
				try {
					int result = sm.saveActVenue(sendVenue, actList, time);
					if (result == ServerResult.SUCCESS){
						runOnUiThread(new Runnable() {
							
							@Override
							public void run() {
								Toast.makeText(MainActivity.this, R.string.send_success, Toast.LENGTH_LONG).show();
							}
						});
					} else {
						runOnUiThread(new Runnable() {
							
							@Override
							public void run() {
								Toast.makeText(MainActivity.this, R.string.send_failed, Toast.LENGTH_LONG).show();
							}
						});
					}
				} catch (Exception e) {
					e.printStackTrace();
					
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							Toast.makeText(MainActivity.this, R.string.send_failed, Toast.LENGTH_LONG).show();
						}
					});
				}
			}
		}).start();
		
		return true;
	}
	
	private Handler handler = new Handler(){
		public void handleMessage(Message msg) {
			
			switch (msg.what){
			
			case HANDLE_FETCH_DATE:
				if (locationTab != null)
					locationTab.render(locationMonitor.createLocationData());
				break;
			case HANDLE_RENDER_TIME:
				if (actTab != null)
					actTab.renderTime();
				break;
			}
			
		};
	};

	
	public class SectionsPagerAdapter extends FragmentPagerAdapter {
		
		private Context context;

		public SectionsPagerAdapter(Context context, FragmentManager fm) {
			super(fm);
			this.context = context;
		}

		@Override
		public Fragment getItem(int position) {
			// getItem is called to instantiate the fragment for the given page.
			// Return a DummySectionFragment (defined as a static inner class
			// below) with the page number as its lone argument.
			
			Log.i("test", "getItem pos: " + position);
			switch(position) {
			case 0:
				if (locationTab == null)
					locationTab = new LocationTab(context);
				return locationTab;
			case 1:
				if (actTab == null)
					actTab = new ActTab(context);
				return actTab;
			case 2:
				if (fourSquareTab == null)
					fourSquareTab = new FourSquareTab(context);
				return fourSquareTab;
			}
			return null;
		}

		@Override
		public int getCount() {
			// Show 3 total pages.
			return 3;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			Locale l = Locale.getDefault();
			switch (position) {
			case 0:
				return getString(R.string.title_section1).toUpperCase(l);
			case 1:
				return getString(R.string.title_section2).toUpperCase(l);
			case 2:
				return getString(R.string.title_section3).toUpperCase(l);
			}
			return null;
		}
	}
}






