package kr.ac.snu.bi.web.api.manager;

import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;




public abstract class WebAPI {

	
	
	public abstract String run(JSONObject query) throws Exception;
	
	public String run(JSONObject query, HttpServletResponse resp) throws Exception {
		return run(query);
	}
}
