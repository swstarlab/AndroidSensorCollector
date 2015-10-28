package kr.ac.snu.bi.sensorcollector.network;

import java.util.List;

import kr.ac.snu.bi.sensorcollector.app.Application;
import kr.ac.snu.bi.sensorcollector.data.Act;
import kr.ac.snu.bi.sensorcollector.data.LocationData;
import kr.ac.snu.bi.sensorcollector.data.SensorData;
import kr.ac.snu.bi.sensorcollector.data.TestData;
import kr.ac.snu.bi.sensorcollector.data.Venue;

import org.json.JSONArray;
import org.json.JSONObject;

import android.util.Log;



public class ServerManager {

	private static ServerManager instance = null;
	
	public static ServerManager getInstance(){
		if (instance == null){
			instance = new ServerManager();
		}
		return instance;
	}
	
	// ------------------------------------------------------------------------------------------------------
	private static final boolean LOG_NETWORK_REQUEST = true;
	private static final boolean LOG_NETWORK_RESPONSE = true;
	
	private static final String HOST = "http://hanockka.cafe24.com/SensorWeb/api";
	//private static final String HOST = "http://hanockka.cafe24.com/SensorWeb/api";
	
	private int userId;
	
	private ServerManager() {
	}
	
	public void logIn() throws Exception {
		
		PostConnection conn = new PostConnection(HOST);
		conn.addParameter("service", "LogIn");
		
		//
		String deviceId = Application.getInstance().getAndroidId();
		
		// query
		JSONObject jsonQuery = new JSONObject();
		jsonQuery.put("deviceId", deviceId);
		conn.addParameter("query", buildJsonToQuery(jsonQuery));
		
		
		// result
		String data = decodeResponse(conn.execute());
		JSONObject json = new JSONObject(data);
		int result = json.getInt("result");
		
		if (result == ServerResult.SUCCESS){
			userId = json.getInt("userId");
		}
		else{
			throw new Exception("logIn failed");
		}
	}
	
	public int saveTestDataList(List<TestData> testDataList) throws Exception {
		
		// log in
		if (userId <= 0){
			logIn();
		}
		
		//
		PostConnection conn = new PostConnection(HOST);
		conn.addParameter("service", "TestAPI");
		
		// create dataList
		JSONArray arr = new JSONArray();
		for (int i = 0; i < testDataList.size(); i++){
			TestData data = testDataList.get(i);
			JSONObject obj = new JSONObject();
			obj.put("time", data.time);
			obj.put("data", data.data);
			arr.put(obj);
		}
		
		// query
		JSONObject jsonQuery = new JSONObject();
		jsonQuery.put("dataList", arr);
		jsonQuery.put("userId", userId);
		conn.addParameter("query", buildJsonToQuery(jsonQuery));
		
		
		// result
		String data = decodeResponse(conn.execute());
		JSONObject json = new JSONObject(data);
		int result = json.getInt("result");
		
		return result;
	}
	
	public int saveSensorDataList(List<SensorData> dataList) throws Exception {
		
		// log in
		if (userId <= 0){
			logIn();
		}
		
		//
		PostConnection conn = new PostConnection(HOST);
		conn.addParameter("service", "SaveSensorData");
		
		// create dataList
		JSONArray arr = new JSONArray();
		for (int i = 0; i < dataList.size(); i++){
			SensorData data = dataList.get(i);
			JSONObject obj = new JSONObject();
			
			obj.put("time", data.time);
			obj.put("accX", data.accX);
			obj.put("accY", data.accY);
			obj.put("accZ", data.accZ);
			obj.put("rotX", data.rotX);
			obj.put("rotY", data.rotY);
			obj.put("rotZ", data.rotZ);
			obj.put("azimuth", data.azimuth);
			obj.put("pitch", data.pitch);
			obj.put("roll", data.roll);
			
			arr.put(obj);
		}
		
		// query
		JSONObject jsonQuery = new JSONObject();
		jsonQuery.put("dataList", arr);
		jsonQuery.put("userId", userId);
		conn.addParameter("query", buildJsonToQuery(jsonQuery));
		
		
		// result
		String data = decodeResponse(conn.execute());
		JSONObject json = new JSONObject(data);
		int result = json.getInt("result");
		
		return result;
	}
	
	public int saveLocationDataList(List<LocationData> dataList) throws Exception {
		
		// log in
		if (userId <= 0){
			logIn();
		}
		
		//
		PostConnection conn = new PostConnection(HOST);
		conn.addParameter("service", "SaveLocationData");
		
		// create dataList
		JSONArray arr = new JSONArray();
		for (int i = 0; i < dataList.size(); i++){
			LocationData data = dataList.get(i);
			JSONObject obj = new JSONObject();
			
			obj.put("time", data.time);
			obj.put("gpsLat", data.gpsLatitude);
			obj.put("gpsLng", data.gpsLongitude);
			obj.put("gpsOn", data.gpsEnabled ? 1 : 0);
			obj.put("netLat", data.netLatitude);
			obj.put("netLng", data.netLongitude);
			obj.put("netOn", data.netEnabled ? 1 : 0);
			obj.put("cellId", data.cellId);
			
			arr.put(obj);
		}
		
		// query
		JSONObject jsonQuery = new JSONObject();
		jsonQuery.put("dataList", arr);
		jsonQuery.put("userId", userId);
		conn.addParameter("query", buildJsonToQuery(jsonQuery));
		
		
		// result
		String data = decodeResponse(conn.execute());
		JSONObject json = new JSONObject(data);
		int result = json.getInt("result");
		
		return result;
	}
	
	public int saveActVenue(Venue venue, List<Act> actList, long time) throws Exception {
		
		// log in
		if (userId <= 0){
			logIn();
		}
		
		//
		PostConnection conn = new PostConnection(HOST);
		conn.addParameter("service", "SaveActVenueData");
		
		// create act str
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < actList.size(); i++){
			Act act = actList.get(i);
			
			if (i > 0){
				sb.append("|");
			}
			sb.append("" + act.activity + "|" + act.category);
		}
		String activityStr = sb.toString();
		
		
		// query
		JSONObject jsonQuery = new JSONObject();
		jsonQuery.put("userId", userId);
		jsonQuery.put("time", time);
		jsonQuery.put("latitude", venue.latitude);
		jsonQuery.put("longitude", venue.longitude);
		jsonQuery.put("activity", activityStr);
		jsonQuery.put("venue", "" + venue.name + "|" + venue.category);
		conn.addParameter("query", buildJsonToQuery(jsonQuery));
		
		
		// result
		String data = decodeResponse(conn.execute());
		JSONObject json = new JSONObject(data);
		int result = json.getInt("result");
		
		return result;
	}
	
	private String buildJsonToQuery(JSONObject json) throws Exception{
		
		String str = json.toString();
		if (LOG_NETWORK_REQUEST){
			Log.i("test", "request: " + str);
		}
		
		return str;
	}
	
	private String decodeResponse(String data) throws Exception{
		
		if (LOG_NETWORK_RESPONSE){
			Log.i("test", "response: " + data);
		}
		
		return data;
	}
}
