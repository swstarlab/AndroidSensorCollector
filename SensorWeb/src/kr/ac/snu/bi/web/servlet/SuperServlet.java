package kr.ac.snu.bi.web.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import kr.ac.snu.bi.web.api.manager.WebAPIManager;
import kr.ac.snu.bi.web.app.Application;

import org.json.JSONObject;


public class SuperServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = -461223888187356401L;

	
	private Application application;
	private WebAPIManager webAPIManager;
	
	
	public SuperServlet() throws Exception {
		application = Application.getInstance();
		application.initialize();
		
		webAPIManager = application.getWebAPIManager();
	}
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		resp.setContentType("text/html; charset=UTF-8");
		
		
		PrintWriter pw = resp.getWriter();
		pw.write("Hello!!");
		pw.close();
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		resp.setContentType("text/html; charset=UTF-8");
		req.setCharacterEncoding("UTF-8");
		
		String service = req.getParameter("service");
		String query = req.getParameter("query");
		
		if (service == null || query == null){
			resp.setStatus(404); // not found
			return;
		}
		
		
		String responseData = null;
		try {
		
			JSONObject queryJson = new JSONObject(query);
			responseData = webAPIManager.runWebAPI(service, queryJson, resp);
			
		} catch (Exception e){
			System.out.println("Service: " + service + ", query: " + query);
			e.printStackTrace();
			resp.setStatus(500);
			return;
		}
		
		
		// 
		PrintWriter pw = resp.getWriter();
		if (responseData != null)
			pw.write(responseData);
		pw.close();
	}
}
















