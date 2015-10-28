package kr.ac.snu.bi.web.api.basic;

import java.sql.SQLException;

import org.json.JSONObject;







import kr.ac.snu.bi.web.api.manager.APIResult;
import kr.ac.snu.bi.web.api.manager.WebAPI;
import kr.ac.snu.bi.web.app.Configuration;
import kr.ac.snu.bi.web.db.TDConnection;
import kr.ac.snu.bi.web.db.TDConnectionPool;
import kr.ac.snu.bi.web.db.TDResultSet;

public class BasicAPI extends WebAPI {

	@Override
	public String run(JSONObject query) throws Exception {
		
		TDConnection connection = null;
		TDResultSet resultSet = null;
		JSONObject resultJson = new JSONObject();
		
		int resultCode = APIResult.UNKNOWN;
		
		try {
		
			int id = query.getInt("id");
			
			connection = TDConnectionPool.getConnection();
			
			resultSet = connection.query("SELECT * FROM Test WHERE id=?", id);
			int data = -1;
			if (resultSet.next()){
				data = resultSet.getInt("data");
			}
			resultSet.close();
			// do something
			
			
			resultJson.put("data", data);
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
