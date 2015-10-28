package kr.ac.snu.bi.sensorcollector.data;

import java.util.ArrayList;
import java.util.List;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;


public class LabelDataManager {

	
	private static LabelDataManager instance = null;
	
	private LabelDataManager(){
		loaded = false;
	}
	
	public static LabelDataManager getInstance(){
		if (instance == null){
			instance = new LabelDataManager();
		}
		return instance;	
	}	
	
	// ----------------------------------------------------------------------------------------------------
	
	private static final String PREF_NAME = "sada";
	private static final String SAVE_DATA_KEY = "sd_key";
	
	private boolean loaded;
	private SharedPreferences pref;
	
	private List<LabelItem> labelItemList;
	
	public synchronized void load(Context context) {
		
		pref = context.getSharedPreferences(PREF_NAME, 0);
		
		String dataString = pref.getString(SAVE_DATA_KEY, null);
		deserialize(dataString);
		
		//
		if (labelItemList == null || labelItemList.size() == 0){
			labelItemList = loadDefaultLabelItemList(context);
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
		
		labelItemList = new ArrayList<LabelItem>();
		
		// clear
		SharedPreferences.Editor editor = pref.edit();
		editor.clear();
		editor.commit();
		
		loaded = false;
	}
	
	private List<LabelItem> loadDefaultLabelItemList(Context context){
		
		List<LabelItem> labelList = new ArrayList<LabelItem>();
		
		labelList.add(new LabelItem("Home", false, true));
		labelList.add(new LabelItem("Watching TV", false, false));
		labelList.add(new LabelItem("Vacuuming", false, false));
		
		labelList.add(new LabelItem("Office", false, true));
		labelList.add(new LabelItem("Research", false, false));
		labelList.add(new LabelItem("Drink Coffe", false, false));
		labelList.add(new LabelItem("Writing Report", false, false));
		
		labelList.add(new LabelItem("Transportation", false, true));
		labelList.add(new LabelItem("Subway", false, false));
		labelList.add(new LabelItem("Bus", false, false));
		labelList.add(new LabelItem("Driving Car", false, false));
		
		return labelList;
	}
	
	private String serialize(){
		
		JSONObject jsonObj = new JSONObject();
		JSONArray jsonArr = new JSONArray();
		
		try {
			if (labelItemList != null){
				for (int i = 0; i < labelItemList.size(); i++){
					JSONObject obj = new JSONObject();
					LabelItem item = labelItemList.get(i);
					
					obj.put("label", item.getLabel());
					obj.put("category", item.isCategory());
					
					jsonArr.put(obj);
				}
			}
			
			jsonObj.put("labelItemList", jsonArr);
		} catch (JSONException e) {
			e.printStackTrace();
			return "";
		}
		
		return jsonObj.toString();
	}
	
	private void deserialize(String str){
		
		labelItemList = new ArrayList<LabelItem>();
		
		if (str == null || str.length() == 0)
			return;
		
		try {
			JSONObject jsonObj = new JSONObject(str);
			JSONArray jsonArr = jsonObj.getJSONArray("labelItemList");
			
			for (int i = 0; i < jsonArr.length(); i++){
				JSONObject obj = jsonArr.getJSONObject(i);
				
				String label = obj.getString("label");
				boolean category = obj.getBoolean("category");
				LabelItem item = new LabelItem(label, false, category);
				labelItemList.add(item);
			}
			
		} catch (JSONException e) {
			e.printStackTrace();
			labelItemList.clear();
		}
	}
	
	public List<LabelItem> getLabelItemList() {
		return labelItemList;
	}
}
