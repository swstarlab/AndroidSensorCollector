package kr.ac.snu.bi.web.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;


public class TDConnection {
	
	
	private Connection connection;
	
	
	public TDConnection(Connection connection) {
		if (connection == null)
			throw new NullPointerException();
		
		this.connection = connection;
	}
	
	public TDResultSet query(String query, Object... args) throws SQLException {

        PreparedStatement stat = connection.prepareStatement(query);

        for (int i = 0; i < args.length; i++) {
            Object arg = args[i];
            if (arg.getClass() == Integer.class) {
                stat.setInt(i + 1, (Integer) arg);
            } else if (arg.getClass() == Long.class) {
                stat.setLong(i + 1, (Long) arg);
            } else if (arg.getClass() == String.class) {
                stat.setString(i + 1, (String) arg);
            } else if (arg.getClass() == Double.class) {
                stat.setDouble(i + 1, (Double) arg);
            } else if (arg.getClass() == Timestamp.class) {
                stat.setTimestamp(i+1, (Timestamp)arg);
            }
        }

        // TDResultSet rs = stat.executeQuery();
        // return r

        if (stat.execute()) {
            return new TDResultSet(stat, stat.getResultSet());
        } else {
            stat.close();
            return null;
        }
    }
	
	private String getQuestions(int num){
		
		switch (num){
		case 1:
			return "?";
		case 2:
			return "?,?";
		case 3:
			return "?,?,?";
		case 4:
			return "?,?,?,?";
		case 5:
			return "?,?,?,?,?";
		case 6:
			return "?,?,?,?,?,?";
		case 7:
			return "?,?,?,?,?,?,?";
		case 8:
			return "?,?,?,?,?,?,?,?";
		case 9:
			return "?,?,?,?,?,?,?,?,?";
		case 10:
			return "?,?,?,?,?,?,?,?,?,?";
		case 11:
			return "?,?,?,?,?,?,?,?,?,?,?";
		case 12:
			return "?,?,?,?,?,?,?,?,?,?,?,?";
		case 13:
			return "?,?,?,?,?,?,?,?,?,?,?,?,?";
		case 14:
			return "?,?,?,?,?,?,?,?,?,?,?,?,?,?";
		case 15:
			return "?,?,?,?,?,?,?,?,?,?,?,?,?,?,?";
		case 16:
			return "?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?";
		case 17:
			return "?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?";
		case 18:
			return "?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?";
		case 19:
			return "?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?";
		case 20:
			return "?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?";
		default:
			return "";
		}
	}
	
	public void close() throws Exception {
		connection.close();
		connection = null;
	}
}







