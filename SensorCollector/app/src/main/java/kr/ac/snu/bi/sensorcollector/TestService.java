package kr.ac.snu.bi.sensorcollector;

import kr.ac.snu.bi.sensorcollector.app.Application;
import kr.ac.snu.bi.sensorcollector.collector.CollectorManager;
import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.util.Log;

public class TestService extends IntentService {
	
	public static final String SERVICE_NAME = "kr.ac.snu.bi.sensor.TestService";

	private CollectorManager collectorManager;
	
	public TestService() {
		super(SERVICE_NAME);
		
		Log.i("test", "TestService created");
	}
	
	public TestService(String name) {
		super(name);
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		Log.i("test", "onHandleIntent");
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.i("test", "onStartCommand");
		
		Application application = Application.getInstance();
        if (!application.isInitialized()){
        	 application.initialize(this);
        }
		
        collectorManager = CollectorManager.getInstance();
        collectorManager.getLocationMonitor().start();
        collectorManager.getLocationCollector().start();
		
		return Service.START_STICKY;
	}
	
	@Override
	public void onDestroy() {
		Log.i("test", "onDestroy");
		if (collectorManager != null){
			collectorManager.getLocationCollector().stop();
		}
		super.onDestroy();
	}
	
	@Override
	public void onStart(Intent intent, int startId) {
		Log.i("test", "onStart");
		super.onStart(intent, startId);
	}
}
