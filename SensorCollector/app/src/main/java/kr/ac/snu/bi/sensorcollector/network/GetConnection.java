package kr.ac.snu.bi.sensorcollector.network;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import android.util.Log;

public class GetConnection {

	private String url;
	private List<NameValuePair> nameValuePairList;
	
	public GetConnection(String url) {
		this.url = url;
		nameValuePairList = new ArrayList<NameValuePair>(3);
	}
	
	public void addParameter(String name, String value){
		nameValuePairList.add(new BasicNameValuePair(name, value));
	}
	
	public String execute() throws Exception{
		
		HttpParams httpParams = new BasicHttpParams();
	    HttpConnectionParams.setConnectionTimeout(httpParams, 8000);
	    
	    String paramStr = URLEncodedUtils.format(nameValuePairList, "utf-8");
	    
		HttpClient client = new DefaultHttpClient(httpParams);
		HttpGet get = new HttpGet(url + "?" + paramStr);
 
		HttpResponse response = client.execute(get);
		String res = stringFromResponse(response);
		
		return res;
	}
	
	private String stringFromResponse(HttpResponse response) throws Exception{
		
		InputStream is = response.getEntity().getContent();
		
		// read it with BufferedReader
		BufferedReader br = new BufferedReader(new InputStreamReader(is));
	 
		StringBuilder sb = new StringBuilder();
		String line = null;
		while ((line = br.readLine()) != null) {
			sb.append(line + '\n');
		}
		return sb.toString();
	}
}
