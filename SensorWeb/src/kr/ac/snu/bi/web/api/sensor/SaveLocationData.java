package kr.ac.snu.bi.web.api.sensor;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONObject;

import kr.ac.snu.bi.web.api.manager.APIResult;
import kr.ac.snu.bi.web.api.manager.WebAPI;
import kr.ac.snu.bi.web.app.Configuration;
import kr.ac.snu.bi.web.db.TDConnection;
import kr.ac.snu.bi.web.db.TDConnectionPool;
import kr.ac.snu.bi.web.db.TDResultSet;

public class SaveLocationData extends WebAPI {

	@Override
	public String run(JSONObject query) throws Exception {
		TDConnection connection = null;
		TDResultSet resultSet = null;
		JSONObject resultJson = new JSONObject();
		
		int resultCode = APIResult.UNKNOWN;
		
		try {
			JSONArray dataList = query.getJSONArray("dataList");
			int userId = query.getInt("userId");
			
			StringBuilder sb = new StringBuilder("INSERT INTO Location(userId, time, gpsLat, gpsLng, gpsOn, netLat, netLng, netOn, cellId) VALUES ");
			for (int i = 0; i < dataList.length(); i++){
				JSONObject obj = dataList.getJSONObject(i);
				
				long time = obj.getLong("time");
				double gpsLat = obj.getDouble("gpsLat");
				double gpsLng = obj.getDouble("gpsLng");
				int gpsOn = obj.getInt("gpsOn");
				double netLat = obj.getDouble("netLat");
				double netLng = obj.getDouble("netLng");
				int netOn = obj.getInt("netOn");
				int cellId = obj.getInt("cellId");
				
				Timestamp stamp = new Timestamp(time);
				
				sb.append(String.format("(%d, '%s', %.15f, %.15f, %d, %.15f, %.15f, %d, %d)", userId, stamp.toString(), gpsLat, gpsLng, gpsOn, netLat, netLng, netOn, cellId));
				
				if (i < dataList.length() - 1){
					sb.append(",");
				}
			}
			
			connection = TDConnectionPool.getConnection();
			connection.query(sb.toString());
			
			resultJson.put("result", APIResult.SUCCESS);
		
		} catch (Exception e){
			if (Configuration.LOG_INTERNAL_EXCEPTIONS){
				e.printStackTrace();
			}
			
			if (e instanceof SQLException){
				resultCode = APIResult.DB_CONNECT_FAIL;
			}
			
			resultJson.put("result", resultCode);
			resultJson.put("errorMsg", e.getMessage());
			
		} finally{
			
			if (connection != null){
				connection.close();
			}
			
			if (resultSet != null){
				resultSet.close();
			}
		}
		
		return resultJson.toString();
	}

}
