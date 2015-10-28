package kr.ac.snu.bi.web.db;

import java.sql.SQLException;

import com.mchange.v2.c3p0.ComboPooledDataSource;

public class TDConnectionPool {

	
	private static TDConnectionPool instance = null;
	
	public static TDConnectionPool createInstance() throws Exception{
		if (instance == null){
			instance = new TDConnectionPool();
		}
		return instance;
	}
	
	public static TDConnectionPool getInstance() throws Exception{
		return instance;
	}
	
	public static TDConnection getConnection() throws SQLException {
		if (instance == null || !instance.isInitialized())
			return null;
		
		return instance.getTDConnection();
	}
	
	// ------------------------------------------------------------------------------------
	
	private ComboPooledDataSource pool;
	private boolean initialized;
	
	private TDConnectionPool(){
		
	}
	
	public void initialize() throws Exception{
		if (initialized)
			return;
		
		pool = new ComboPooledDataSource();
		pool.setDriverClass(DBInfo.JDBC_DRIVER);
		pool.setJdbcUrl(DBInfo.JDBC_URI);
		pool.setUser(DBInfo.DB_USER);                                  
		pool.setPassword(DBInfo.DB_PASSWORD);
		
		pool.getConnection().close();
		
		initialized = true;
	}
	
	public TDConnection getTDConnection() throws SQLException {
		return new TDConnection(pool.getConnection());
	}
	
	public ComboPooledDataSource getPool() {
		return pool;
	}
	
	public boolean isInitialized() {
		return initialized;
	}
}


