package kr.ac.snu.bi.web.main;

import java.io.IOException;
import java.sql.Timestamp;

public class Main {

	public static void main(String[] args) throws IOException {
		
		Timestamp stamp = new Timestamp(System.currentTimeMillis());
		System.out.println(stamp.toString());
	}
}
