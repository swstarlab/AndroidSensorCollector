package kr.ac.snu.bi.sensorcollector.collector;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import kr.ac.snu.bi.sensorcollector.data.SensorData;
import kr.ac.snu.bi.sensorcollector.network.ServerManager;
import kr.ac.snu.bi.sensorcollector.network.ServerResult;

import android.util.Log;

public class SensorCollector{

	private static final int CAPACITY = 120000;
	private static final int STOP_COLLECT_SIZE = CAPACITY*9/10;
	
	private static final int COLLECT_DELAY = 4000;
	private static final int COLLECT_PERIOD = 33;
	private static final int COLLECT_PERIOD_MIN = 20;
	private static final int SAVE_DELAY = COLLECT_DELAY + 5000;
	private static final int SAVE_TIMER_PERIOD = 500;
	private static final int SAVE_PERIOD = 5000;
	
	private static final int SAVE_MIN = 50;
	private static final int SAVE_MAX = 1000;
	
	
	
	private BlockingQueue<SensorData> queue;
	private Timer timer;
	private SensorMonitor sensorMonitor;
	private boolean running;
	
	
	public SensorCollector(SensorMonitor sensorMonitor) {
		queue = new LinkedBlockingQueue<SensorData>(CAPACITY);
		this.sensorMonitor = sensorMonitor;
	}
	
	public void start(){
		if (running)
			return;
		
		timer = new Timer();
		timer.scheduleAtFixedRate(new Collector(), COLLECT_DELAY, COLLECT_PERIOD);
		timer.scheduleAtFixedRate(new WebSaver(), SAVE_DELAY, SAVE_TIMER_PERIOD);
		
		running = true;
	}
	
	public void stop(){
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
			queue.add(sensorMonitor.createSensorData());
		}
	}
	
	private class WebSaver extends TimerTask {
		
		private long previousSaveTime;
		private List<SensorData> dataList;
		
		public WebSaver() {
			dataList = new ArrayList<SensorData>(SAVE_MAX);
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
				int result = ServerManager.getInstance().saveSensorDataList(dataList);
				if (result == ServerResult.SUCCESS){
					previousSaveTime = curTime;
					Log.i("test", "sensor data save success num=" + dataList.size());
					dataList.clear();
				}
				else{
					throw new Exception("saving sensor data list failed, result=" + result);
				}
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}
