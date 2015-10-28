package kr.ac.snu.bi.web.api.cred;

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

public class LogIn extends WebAPI{

	
	@Override
	public String run(JSONObject query) throws Exception {

		TDConnection connection = null;
		TDResultSet resultSet = null;
		JSONObject resultJson = new JSONObject();
		
		int resultCode = APIResult.UNKNOWN;
		
		try {
			String deviceId = query.getString("deviceId");
			
			
			int userId = 0;
			connection = TDConnectionPool.getConnection();

			resultSet = connection.query("SELECT * FROM User WHERE deviceId=?", deviceId);
			if (resultSet.next()){
				userId = resultSet.getInt("userId");
			}
			resultSet.close();
			
			if (userId == 0){
				connection.query("INSERT INTO User(deviceId) VALUES(?)", deviceId);
				resultSet = connection.query("SELECT LAST_INSERT_ID() as userId");
				
				resultSet.next();
				userId = resultSet.getInt("userId");
			}
			
			
			if (userId == 0){
				resultCode = APIResult.UNKNOWN;
				throw new Exception("login failed");
			}
			
			resultJson.put("userId", userId);
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
