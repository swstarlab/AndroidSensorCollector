package kr.ac.snu.bi.sensorcollector.network;

import java.util.ArrayList;
import java.util.List;

import kr.ac.snu.bi.sensorcollector.data.Venue;

import org.json.JSONArray;
import org.json.JSONObject;

import android.util.Log;

public class FourSquareConn {
	
	private static FourSquareConn instance = null;

	public static FourSquareConn getInstance(){
		if (instance == null){
			instance = new FourSquareConn();
		}
		return instance;
	}
	
	// ------------------------------------------------------------------------------------------------------
	
	
	private static final String HOST = "https://api.foursquare.com/v2/";
	private static final String VENUES_SEARCH = "venues/search";
	private static final String CLIENT_ID = "IGPH0SE2BGNCSYLG2VKNQYH2TPM0ORCQNK3GJPTDPEG31TUE";
	private static final String CLIENT_SECRET = "54WNTC2AMFOZH232Q3SWPDE0T5W4VQFCXIOSVWXZGECJGED0";
	private static final String VERSION = "20150106";
	
	private static final int LOCATION_NUM_MAX = 15;
	
	
	public List<Venue> venuesSearch(double lat, double lon, String query) throws Exception{
	
		GetConnection conn = new GetConnection(HOST + VENUES_SEARCH);
		conn.addParameter("client_id", CLIENT_ID);
		conn.addParameter("client_secret", CLIENT_SECRET);
		conn.addParameter("v", VERSION);
		conn.addParameter("ll", "" + lat + "," + lon);
		conn.addParameter("query", query);
		
		List<Venue> venueList = new ArrayList<Venue>();
		
		String res = conn.execute();
		JSONObject jsonObj = new JSONObject(res);
		JSONArray venues = jsonObj.getJSONObject("response").getJSONArray("venues");
		
		for (int i = 0; i < venues.length() && i < LOCATION_NUM_MAX; i++){
			
			Venue venue = new Venue();
			
			JSONObject venueJsonObj = venues.getJSONObject(i);
			venue.name = venueJsonObj.getString("name");
			
			JSONArray categoriesJsonArr = venueJsonObj.getJSONArray("categories");
			venue.category = "Unknown";
			if (categoriesJsonArr.length() > 0){
				venue.category = categoriesJsonArr.getJSONObject(0).getString("name");
			}
			
			JSONObject locationJsonObj = venueJsonObj.getJSONObject("location");
			venue.latitude = locationJsonObj.getDouble("lat");
			venue.longitude = locationJsonObj.getDouble("lng");
			
			venueList.add(venue);
		}
		
		return venueList;
	}
}




