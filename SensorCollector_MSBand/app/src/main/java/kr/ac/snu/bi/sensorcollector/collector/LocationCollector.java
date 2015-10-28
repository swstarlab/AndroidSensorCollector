package kr.ac.snu.bi.sensorcollector.collector;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import kr.ac.snu.bi.sensorcollector.data.LocationData;
import kr.ac.snu.bi.sensorcollector.network.ServerManager;
import kr.ac.snu.bi.sensorcollector.network.ServerResult;

import android.util.Log;

public class LocationCollector{

	private static final int CAPACITY = 4000;
	private static final int STOP_COLLECT_SIZE = CAPACITY*9/10;
	
	private static final int COLLECT_DELAY = 2000;
	private static final int COLLECT_PERIOD = 10000;
	private static final int COLLECT_PERIOD_MIN = COLLECT_PERIOD*8/10;
	private static final int SAVE_DELAY = COLLECT_DELAY + 5000;
	private static final int SAVE_TIMER_PERIOD = 3000;
	private static final int SAVE_PERIOD = 10000;
	
	private static final int SAVE_MIN = 1;
	private static final int SAVE_MAX = 50;
	
	
	
	private BlockingQueue<LocationData> queue;
	private Timer timer;
	private LocationMonitor locationMonitor;
	private boolean running;
	
	
	public LocationCollector(LocationMonitor locationMonitor) {
		queue = new LinkedBlockingQueue<LocationData>(CAPACITY);
		this.locationMonitor = locationMonitor;
	}
	
	public synchronized void start(){
		if (running)
			return;
		
		queue.clear();
		
		timer = new Timer();
		timer.scheduleAtFixedRate(new Collector(), COLLECT_DELAY, COLLECT_PERIOD);
		timer.scheduleAtFixedRate(new WebSaver(), SAVE_DELAY, SAVE_TIMER_PERIOD);
		
		running = true;
	}
	
	public synchronized void stop(){
		if (!running)
			return;
		
		timer.cancel();
		timer = null;
		
		running = false;
	}
	
	public boolean isRunning() {
		return running;
	}
	
	// -------------------------------------------------------------------------------------------
	
	private class Collector extends TimerTask {

		
		private long previous;
		
		@Override
		public void run() {
			
			if (queue.size() >= STOP_COLLECT_SIZE){
				return;
			}
			
			long cur = System.currentTimeMillis();
			if (cur - previous < COLLECT_PERIOD_MIN){
				return;
			}

			previous = cur;
			queue.add(locationMonitor.createLocationData());
		}
	}
	
	private class WebSaver extends TimerTask {
		
		private long previousSaveTime;
		private List<LocationData> dataList;
		
		public WebSaver() {
			dataList = new ArrayList<LocationData>(SAVE_MAX);
		}

		@Override
		public void run() {
			
			long curTime = System.currentTimeMillis();
			if (curTime - previousSaveTime < SAVE_PERIOD)
				return;
			
			
			int pollSize = Math.min(SAVE_MAX - dataList.size(), queue.size());
			if (pollSize < 0){
				pollSize = 0;
			}
			
			if (pollSize + dataList.size() < SAVE_MIN){
				return;
			}
			
			for (int i = 0; i < pollSize; i++){
				dataList.add(queue.poll());
			}
			
			try {
				int result = ServerManager.getInstance().saveLocationDataList(dataList);
				if (result == ServerResult.SUCCESS){
					previousSaveTime = curTime;
					Log.i("test", "location data save success num=" + dataList.size());
					dataList.clear();
				}
				else{
					throw new Exception("saving location data list failed, result=" + result);
				}
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
