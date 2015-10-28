package kr.ac.snu.bi.web.api.manager;

import java.util.HashMap;

import javax.servlet.http.HttpServletResponse;

import kr.ac.snu.bi.web.api.basic.BasicAPI;
import kr.ac.snu.bi.web.api.basic.TestAPI;
import kr.ac.snu.bi.web.api.cred.LogIn;
import kr.ac.snu.bi.web.api.sensor.GetLocationData;
import kr.ac.snu.bi.web.api.sensor.GetSensorData;
import kr.ac.snu.bi.web.api.sensor.SaveActVenueData;
import kr.ac.snu.bi.web.api.sensor.SaveLocationData;
import kr.ac.snu.bi.web.api.sensor.SaveSensorData;

import org.json.JSONObject;

public class WebAPIManager {

	private static WebAPIManager instance = null;
	
	public static WebAPIManager getInstance(){
		if (instance == null){
			instance = new WebAPIManager();
		}
		return instance;
	}
	
	// ------------------------------------------------------------------------------------
	
	
	private HashMap<String, WebAPI> apiMap;
	
	
	private WebAPIManager(){
		
		apiMap = new HashMap<String, WebAPI>();
		
		apiMap.put(BasicAPI.class.getSimpleName(), new BasicAPI());
		apiMap.put(TestAPI.class.getSimpleName(), new TestAPI());
		apiMap.put(SaveSensorData.class.getSimpleName(), new SaveSensorData());
		apiMap.put(GetSensorData.class.getSimpleName(), new GetSensorData());
		apiMap.put(LogIn.class.getSimpleName(), new LogIn());
		apiMap.put(SaveLocationData.class.getSimpleName(), new SaveLocationData());
		apiMap.put(GetLocationData.class.getSimpleName(), new GetLocationData());
		apiMap.put(SaveActVenueData.class.getSimpleName(), new SaveActVenueData());
	}
	
	public String runWebAPI(String service, JSONObject query, HttpServletResponse resp) throws Exception{
		
		WebAPI api = apiMap.get(service);
		if (api == null)
			throw new APIException("no api", APIResult.NO_SERVICE);
		if (query == null)
			throw new APIException("no parameters", APIResult.NO_PARAM);
		
		return api.run(query, resp);
	}
}









