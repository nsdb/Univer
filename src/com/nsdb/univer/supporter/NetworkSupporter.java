package com.nsdb.univer.supporter;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;

import com.nsdb.univer.supporter.data.AppPref;

public class NetworkSupporter {

	public static InputStreamReader getStreamFromRequest(HttpUriRequest request) throws Exception {

		HttpClient client=new DefaultHttpClient();

		// cookie load
		CookieStore cookieStore=((DefaultHttpClient)client).getCookieStore();
		List<Cookie> cookieList=cookieStore.getCookies();
		String cookieName=AppPref.getString("cookieName");
		if(cookieList.size()==0 && cookieName.compareTo("")!=0) {
			String cookieValue=AppPref.getString("cookieValue");
			String cookieDomain=AppPref.getString("cookieDomain");
			String cookiePath=AppPref.getString("cookiePath");
			BasicClientCookie cookie=new BasicClientCookie( cookieName,cookieValue );
			cookie.setDomain(cookieDomain);
			cookie.setPath(cookiePath);
			cookieStore.addCookie(cookie);
		}
		
		// get response
		HttpResponse response=client.execute(request);
		InputStream is=response.getEntity().getContent();
		InputStreamReader isr=new InputStreamReader(is,"utf-8");

		// cookie save
		if(cookieList.size()>0) {
			AppPref.setString("cookieName",cookieList.get(0).getName());
			AppPref.setString("cookieValue",cookieList.get(0).getValue());
			AppPref.setString("cookieDomain",cookieList.get(0).getDomain());
			AppPref.setString("cookiePath",cookieList.get(0).getPath());
		}
		
		return isr;
		
	}
	
	public static List<Element> getXmlElementsFromStream(InputStreamReader isr) throws Exception {
		
		SAXBuilder sax=new SAXBuilder();
		Document doc=sax.build(isr);
		Element rss=doc.getRootElement();
		Element channel=rss.getChild("channel");
		List<Element> items=channel.getChildren("item");
		
		return items;
		
	}
	
	public static String getStringFromStream(InputStreamReader isr) throws Exception {

		BufferedReader br=new BufferedReader(isr);
		String result="";
		String temp=br.readLine();
		while(temp!=null) {
			System.out.println(temp);
			result=temp;
			temp=br.readLine();
		}
		
		return result;
		
	}
	
}
