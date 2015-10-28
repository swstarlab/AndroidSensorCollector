package kr.ac.snu.bi.web.api.basic;

import java.sql.SQLException;

import org.json.JSONArray;
import org.json.JSONObject;

import kr.ac.snu.bi.web.api.manager.APIException;
import kr.ac.snu.bi.web.api.manager.APIResult;
import kr.ac.snu.bi.web.api.manager.WebAPI;
import kr.ac.snu.bi.web.app.Configuration;
import kr.ac.snu.bi.web.db.TDConnection;
import kr.ac.snu.bi.web.db.TDConnectionPool;
import kr.ac.snu.bi.web.db.TDResultSet;

public class TestAPI extends WebAPI {

	@Override
	public String run(JSONObject query) throws Exception {
		
		TDConnection connection = null;
		TDResultSet resultSet = null;
		JSONObject resultJson = new JSONObject();
		
		int resultCode = APIResult.UNKNOWN;
		
		try {
			JSONArray dataList = query.getJSONArray("dataList");
			int userId = query.getInt("userId");
			
			if (userId <= 0){
				throw new APIException("invalid userId=" + userId, APIResult.INVALID_USER_ID);
			}
			
			StringBuilder sb = new StringBuilder("INSERT INTO Test(userId, time, data) VALUES ");
			for (int i = 0; i < dataList.length(); i++){
				JSONObject obj = dataList.getJSONObject(i);
				long time = obj.getLong("time");
				int data = obj.getInt("data");
				
				sb.append("(" + userId + "," + time + ", " + data + ")");
				
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
