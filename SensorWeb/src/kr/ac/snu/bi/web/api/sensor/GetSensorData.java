package kr.ac.snu.bi.web.api.sensor;

import java.io.StringWriter;
import java.sql.SQLException;

import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;






import au.com.bytecode.opencsv.CSVWriter;
import kr.ac.snu.bi.web.api.manager.APIException;
import kr.ac.snu.bi.web.api.manager.APIResult;
import kr.ac.snu.bi.web.api.manager.WebAPI;
import kr.ac.snu.bi.web.app.Configuration;
import kr.ac.snu.bi.web.db.TDConnection;
import kr.ac.snu.bi.web.db.TDConnectionPool;
import kr.ac.snu.bi.web.db.TDResultSet;

public class GetSensorData extends WebAPI {
	
	private static final int COUNT_MAX = 100000;
	
	@Override
	public String run(JSONObject query, HttpServletResponse response) throws Exception {
		
		TDConnection connection = null;
		TDResultSet resultSet = null;
		
		String result = "";
		boolean success = false;
		
		try {
			int userId = query.getInt("userId");
			long from = query.getLong("from");
			long to = query.getLong("to");
			
			
			connection = TDConnectionPool.getConnection();
			resultSet = connection.query("SELECT COUNT(*) as count FROM Sensor WHERE time >= ? AND time <= ? AND userId=? ORDER BY time ASC", from, to, userId);
			if (resultSet.next()){
				int count = resultSet.getInt("count");
				if (count > COUNT_MAX){
					throw new Exception("GetSensorData: too many results");
				}
			}
			else{
				throw new Exception("GetSensorData: counting failed");
			}
			resultSet.close();
			
			resultSet = connection.query("SELECT * FROM Sensor WHERE time >= ? AND time <= ? AND userId=? ORDER BY time ASC", from, to, userId);
			
			StringWriter sw = new StringWriter();
			CSVWriter cw = new CSVWriter(sw);
			cw.writeAll(resultSet.getResultSet(), true);
			cw.close();
			result = sw.toString();
			
			resultSet.close();
			success = true;
		
		} catch (Exception e){
			if (Configuration.LOG_INTERNAL_EXCEPTIONS){
				e.printStackTrace();
			}
			
			if (e instanceof SQLException){
				//resultCode = APIResult.DB_CONNECT_FAIL;
			}
			
			result = e.getMessage();
			success = false;
			
		} finally{
			
			if (connection != null){
				connection.close();
			}
			
			if (resultSet != null){
				resultSet.close();
			}
		}
		
		if (success){
			response.setContentType("text/csv");
			response.setHeader("Content-Disposition", "attachment; filename=sensor.csv");
		}
		else{
			response.setStatus(500);
		}
		
		return result;
	}
	
	@Override
	public String run(JSONObject query) throws Exception {
		return null;
	}
}
