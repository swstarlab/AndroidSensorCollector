package kr.ac.snu.bi.web.db;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;

import kr.ac.snu.bi.web.api.manager.APIException;
import kr.ac.snu.bi.web.api.manager.APIResult;


public class TDResultSet {

	private Statement statement;
	private ResultSet resultSet;
	
	public TDResultSet(Statement statement, ResultSet resultSet) {
		this.statement = statement;
		this.resultSet = resultSet;
	}
	
	public void close() throws Exception{
		if (statement != null)
			statement.close();
		statement = null;
	}
	
	public boolean first() throws Exception{
		if (resultSet == null)
			return false;
		
		return resultSet.first();
	}
	
	public boolean next() throws Exception{
		if (resultSet == null)
			return false;
		
		return resultSet.next();
	}
	
	public boolean nextResultSet() throws Exception{
		if (statement.getMoreResults()){
			resultSet = statement.getResultSet();
			return true;
		}
		else{
			return false;
		}
	}

	public String getString(String column) throws Exception{
		if (resultSet == null)
			return null;
		return resultSet.getString(column) ;
	}
	
	public int getInt(String column) throws Exception{
		if (resultSet == null)
			throw new APIException("no column", APIResult.NO_COLUMN);
		return resultSet.getInt(column) ;
	}
	
	public long getLong(String column) throws Exception{
		if (resultSet == null)
			throw new APIException("no column", APIResult.NO_COLUMN);
		return resultSet.getLong(column) ;
	}
	
	public double getDouble(String column) throws Exception{
		if (resultSet == null)
			throw new APIException("no column", APIResult.NO_COLUMN);
		return resultSet.getDouble(column) ;
	}
	
	public Date getDate(String column) throws Exception{
		if (resultSet == null)
			throw new APIException("no column", APIResult.NO_COLUMN);
		return resultSet.getDate(column) ;
	}
	
	public Timestamp getTimestamp(String column) throws Exception{
		if (resultSet == null)
			throw new APIException("no column", APIResult.NO_COLUMN);
		return resultSet.getTimestamp(column) ;
	}
	
	public ResultSet getResultSet() {
		return resultSet;
	}
}
