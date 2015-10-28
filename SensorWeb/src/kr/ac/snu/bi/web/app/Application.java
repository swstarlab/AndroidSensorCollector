package kr.ac.snu.bi.web.app;

import kr.ac.snu.bi.web.api.manager.WebAPIManager;
import kr.ac.snu.bi.web.db.TDConnectionPool;



public class Application {

	
	private static Application instance = null;
	
	public static Application getInstance(){
		if (instance == null){
			instance = new Application();
		}
		return instance;
	}
	
	
	
	// ----------------------------------------------------------------------
	
	private WebAPIManager webAPIManager;
	private TDConnectionPool connectionPool;
	private boolean initialized;
	
	private Application(){
		
	}
	
	public void initialize() throws Exception{
		if (initialized)
			return;
		
		webAPIManager = WebAPIManager.getInstance();
		connectionPool = TDConnectionPool.createInstance();
		connectionPool.initialize();
		

		initialized = true;
	}
	
	public WebAPIManager getWebAPIManager() {
		return webAPIManager;
	}
	
	public TDConnectionPool getConnectionPool() {
		return connectionPool;
	}
	
	public boolean isInitialized() {
		return initialized;
	}
}






