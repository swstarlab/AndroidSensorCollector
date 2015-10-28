package kr.ac.snu.bi.sensorcollector.collector;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import android.util.Log;

import kr.ac.snu.bi.sensorcollector.data.TestData;
import kr.ac.snu.bi.sensorcollector.network.ServerManager;
import kr.ac.snu.bi.sensorcollector.network.ServerResult;

public class TestCollector {

	private static final int CAPACITY = 100000;
	private static final int STOP_COLLECT_SIZE = CAPACITY*9/10;
	
	private static final int COLLECT_DELAY = 2000;
	private static final int COLLECT_PERIOD = 100;
	private static final int SAVE_DELAY = COLLECT_DELAY + 3000;
	private static final int SAVE_TIMER_PERIOD = 1000;
	private static final int SAVE_PERIOD = 3000;
	
	private static final int SAVE_MIN = 10;
	private static final int SAVE_MAX = 100;
	
	
	private BlockingQueue<TestData> queue;
	private Timer timer;
	private boolean running;
	
	public TestCollector() {
		queue = new LinkedBlockingQueue<TestData>(CAPACITY);
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

		private int counter;
		
		@Override
		public void run() {
			
			if (queue.size() >= STOP_COLLECT_SIZE){
				return;
			}

			TestData d = new TestData();
			d.time = System.currentTimeMillis();
			d.data = counter++;
			queue.add(d);
			
			if (counter % 100 == 0){
				Log.i("test", "collecting test data counter=" + counter);
			}
		}
	}
	
	private class WebSaver extends TimerTask {
		
		private long previousSaveTime;
		private List<TestData> dataList;
		
		public WebSaver() {
			dataList = new ArrayList<TestData>(SAVE_MAX);
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
				int result = ServerManager.getInstance().saveTestDataList(dataList);
				if (result == ServerResult.SUCCESS){
					previousSaveTime = curTime;
					Log.i("test", "test data save success num=" + dataList.size());
					dataList.clear();
				}
				else{
					throw new Exception("saving test data list failed, result=" + result);
				}
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
	}
}













