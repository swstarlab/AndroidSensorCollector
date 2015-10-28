package kr.ac.snu.bi.sensorcollector.data;

import java.util.ArrayList;
import java.util.List;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;


public class VenueDataManager {

	
	private static VenueDataManager instance = null;
	
	private VenueDataManager(){
		loaded = false;
	}
	
	public static VenueDataManager getInstance(){
		if (instance == null){
			instance = new VenueDataManager();
		}
		return instance;	
	}	
	
	// ----------------------------------------------------------------------------------------------------
	
	private static final String PREF_NAME = "venue";
	private static final String SAVE_DATA_KEY = "sd_key";
	
	private boolean loaded;
	private SharedPreferences pref;
	
	private List<Venue> venueList;
	
	public synchronized void load(Context context) {
		
		pref = context.getSharedPreferences(PREF_NAME, 0);
		
		String dataString = pref.getString(SAVE_DATA_KEY, null);
		deserialize(dataString);
		
		//
		if (venueList == null){
			venueList = new ArrayList<Venue>();
		}
		
		//
		loaded = true;
		
		//
		save();
	}
	
	public synchronized void save() {
		
		// validate
		if (!loaded)
			throw new RuntimeException("save data before loading");
		
		//
		SharedPreferences.Editor editor = pref.edit();
		
		//
		editor.putString(SAVE_DATA_KEY, serialize());
		
		// commit
		if (!editor.commit())
			throw new RuntimeException("data commit failed");
	}
	
	public synchronized void clear(){
		
		// validate
		if (!loaded)
			throw new RuntimeException("clear data before loading");
		
		venueList = new ArrayList<Venue>();
		
		// clear
		SharedPreferences.Editor editor = pref.edit();
		editor.clear();
		editor.commit();
		
		loaded = false;
	}
	
	private String serialize(){
		
		JSONObject jsonObj = new JSONObject();
		JSONArray jsonArr = new JSONArray();
		
		try {
			if (venueList != null){
				for (int i = 0; i < venueList.size(); i++){
					JSONObject obj = new JSONObject();
					Venue venue = venueList.get(i);
					
					obj.put("name", venue.name);
					obj.put("category", venue.category);
					obj.put("latitude", venue.latitude);
					obj.put("longitude", venue.longitude);
					
					jsonArr.put(obj);
				}
			}
			
			jsonObj.put("venueList", jsonArr);
		} catch (JSONException e) {
			e.printStackTrace();
			return "";
		}
		
		return jsonObj.toString();
	}
	
	private void deserialize(String str){
		
		venueList = new ArrayList<Venue>();
		
		if (str == null || str.length() == 0)
			return;
		
		try {
			JSONObject jsonObj = new JSONObject(str);
			JSONArray jsonArr = jsonObj.getJSONArray("venueList");
			
			for (int i = 0; i < jsonArr.length(); i++){
				JSONObject obj = jsonArr.getJSONObject(i);
				
				Venue venue = new Venue();
				venue.name = obj.getString("name");
				venue.category = obj.getString("category");
				venue.latitude = obj.getDouble("latitude");
				venue.longitude = obj.getDouble("longitude");
				
				venueList.add(venue);
			}
			
		} catch (JSONException e) {
			e.printStackTrace();
			venueList.clear();
		}
	}
	
	public void addNewVenue(Venue venue){
		
		// validate
		if (venue == null)
			return;
		
		if (!loaded)
			throw new RuntimeException("add data before loading");
		
		// check duplicate venue
		for (Venue v: venueList){
			if (v.name.compareTo(venue.name) == 0)
				return;
		}
		
		//
		venueList.add(venue);
		
		save();
	}
	
	public List<Venue> getVenueList() {
		return venueList;
	}
}
