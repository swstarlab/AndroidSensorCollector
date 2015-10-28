package kr.ac.snu.bi.sensorcollector.collector;

import android.content.Context;
import kr.ac.snu.bi.sensorcollector.app.Application;



public class CollectorManager {
	
	private static CollectorManager instance;


	public synchronized static CollectorManager getInstance() {
		if (instance == null) {
			instance = new CollectorManager();
		}
		return instance;
	}
	
	// -----------------------------------------------------------------
	

	private boolean running;
	private LocationCollector locationCollector;
	private LocationMonitor locationMonitor;
	
	public CollectorManager() {
		Context context = Application.getInstance().getContext();
		
		locationMonitor = new LocationMonitor(context);
		locationCollector = new LocationCollector(locationMonitor);

	}

	public LocationMonitor getLocationMonitor() {
		return locationMonitor;
	}
	
	public LocationCollector getLocationCollector() {
		return locationCollector;
	}
	
	public boolean isRunning() {
		return running;
	}
	
}
