package kr.ac.snu.bi.sensorcollector.app;

import android.content.Context;
import android.provider.Settings.Secure;
import android.util.Log;
import kr.ac.snu.bi.sensorcollector.data.LabelDataManager;
import kr.ac.snu.bi.sensorcollector.data.VenueDataManager;

public class Application {

	private static Application instance;


	public synchronized static Application getInstance() {
		if (instance == null) {
			instance = new Application();
		}
		return instance;
	}
	
	
	// -----------------------------------------------------------------
	
	private String androidId;
	private boolean initialized;
	private Context context;
	
	private Application() {
	}
	
	public void initialize(Context context) {
		
		if (initialized)
			return;
		
		Log.i("test", "Application initialized");
		
		this.context = context.getApplicationContext();
		androidId = Secure.getString(context.getContentResolver(), Secure.ANDROID_ID);
		
		LabelDataManager.getInstance().load(this.context);
		VenueDataManager.getInstance().load(this.context);
		
		initialized = true;
	}
	
	public boolean isInitialized() {
		return initialized;
	}
	
	public String getAndroidId() {
		return androidId;
	}
	
	public Context getContext() {
		return context;
	}
}




