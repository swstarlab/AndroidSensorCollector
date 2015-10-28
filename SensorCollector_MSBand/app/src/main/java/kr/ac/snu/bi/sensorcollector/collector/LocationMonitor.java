package kr.ac.snu.bi.sensorcollector.collector;

import java.util.Timer;
import java.util.TimerTask;

import kr.ac.snu.bi.sensorcollector.data.LocationData;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;
import android.util.Log;

public class LocationMonitor {

	private static final int LOCATION_UPDATE_PERIOD = 1000;
	private static final int CELL_ID_UPDATE_PERIOD = 1000;
	private static final int CELL_ID_COLLECT_DELAY = 500;
	
	
	private LocationManager locationManager;
	private TelephonyManager telephonyManager;
	
	private int cellId;
	
	private double gpsLatitude;
	private double gpsLongitude;
	private boolean gpsEnabled;
	
	private double netLatitude;
	private double netLongitude;
	private boolean netEnabled;
	
	private GPSListener gpsListener;
	private NetListener netListener;
	
	private Object gpsMonitor;
	private Object netMonitor;
	
	private Timer timer;

	private boolean running;
	
	
	public LocationMonitor(Context context) {
		locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
		telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		
		gpsListener = new GPSListener();
		netListener = new NetListener();
		
		gpsMonitor = new Object();
		netMonitor = new Object();
	}
	
	public void start(){
		if (running)
			return;
		
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, LOCATION_UPDATE_PERIOD, 0, gpsListener);
		locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, LOCATION_UPDATE_PERIOD, 0, netListener);
		
		timer = new Timer();
		timer.scheduleAtFixedRate(new CellIdMonitor(), CELL_ID_COLLECT_DELAY, CELL_ID_UPDATE_PERIOD);
		
		running = true;
	}
	
	public void stop(){
		if (!running)
			return;
		
		locationManager.removeUpdates(gpsListener);
		locationManager.removeUpdates(netListener);
		
		timer.cancel();
		timer = null;
		
		running = false;
	}
	
	public LocationData createLocationData(){
		
		LocationData data = new LocationData();
		
		synchronized (gpsMonitor) {
			data.gpsLatitude = gpsLatitude;
			data.gpsLongitude = gpsLongitude;
			data.gpsEnabled = gpsEnabled;
		}
		
		synchronized (netMonitor) {
			data.netLatitude = netLatitude;
			data.netLongitude = netLongitude;
			data.netEnabled = netEnabled;
		}
		
		data.cellId = cellId;
		data.time = System.currentTimeMillis();
		
		return data;
	}
	
	public boolean isGpsEnabled() {
		return gpsEnabled;
	}
	
	public boolean isNetEnabled() {
		return netEnabled;
	}

	// -------------------------------------------------------------------------------
	
	private class CellIdMonitor extends TimerTask {
		
		@Override
		public void run() {
			GsmCellLocation cellLocation = (GsmCellLocation) telephonyManager.getCellLocation();
			cellId = cellLocation.getCid();
		}
	}
	
	private class GPSListener implements LocationListener {

		@Override
		public void onLocationChanged(Location location) {
			Log.i("test", "gps onLocationChanged");
			
			synchronized (gpsMonitor) {
				gpsLatitude = location.getLatitude();
				gpsLongitude = location.getLongitude();
				gpsEnabled = true;
			}
		}

		@Override
		public void onProviderDisabled(String provider) {
			Log.i("test", "gps onProviderDisabled");
			synchronized (gpsMonitor) {
				gpsEnabled = false;
			}
		}

		@Override
		public void onProviderEnabled(String provider) {
			Log.i("test", "gps onProviderEnabled");
			synchronized (gpsMonitor) {
				gpsEnabled = true;
			}
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
		}
	}
	
	private class NetListener implements LocationListener {

		@Override
		public void onLocationChanged(Location location) {
			Log.i("test", "net onLocationChanged");
			
			synchronized (netMonitor) {
				netLatitude = location.getLatitude();
				netLongitude = location.getLongitude();
				netEnabled = true;
			}
		}

		@Override
		public void onProviderDisabled(String provider) {
			Log.i("test", "net onProviderDisabled");
			synchronized (netMonitor) {
				netEnabled = false;
			}
		}

		@Override
		public void onProviderEnabled(String provider) {
			Log.i("test", "net onProviderEnabled");
			synchronized (netMonitor) {
				netEnabled = true;
			}
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
		}
	}
}
