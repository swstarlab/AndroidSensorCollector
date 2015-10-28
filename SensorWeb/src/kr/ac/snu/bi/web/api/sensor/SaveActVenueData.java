package kr.ac.snu.bi.web.api.sensor;

import java.sql.SQLException;
import java.sql.Timestamp;

import org.json.JSONObject;

import kr.ac.snu.bi.web.api.manager.APIResult;
import kr.ac.snu.bi.web.api.manager.WebAPI;
import kr.ac.snu.bi.web.app.Configuration;
import kr.ac.snu.bi.web.db.TDConnection;
import kr.ac.snu.bi.web.db.TDConnectionPool;
import kr.ac.snu.bi.web.db.TDResultSet;

public class SaveActVenueData extends WebAPI {

	@Override
	public String run(JSONObject query) throws Exception {
		TDConnection connection = null;
		TDResultSet resultSet = null;
		JSONObject resultJson = new JSONObject();
		
		int resultCode = APIResult.UNKNOWN;
		
		try {
			int userId = query.getInt("userId");
			long time = query.getLong("time");
			String activity = query.getString("activity");
			String venue = query.getString("venue");
			double latitude = query.getDouble("latitude");
			double longitude = query.getDouble("longitude");
			
			Timestamp stamp = new Timestamp(time);
			
			connection = TDConnectionPool.getConnection();
			connection.query("INSERT INTO Activity(userId, time, activity, venue, latitude, longitude) VALUES(?,?,?,?,?,?)",
					userId, stamp, activity, venue, latitude, longitude);
			
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
