package com.nsdb.univer.common;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;



import android.os.AsyncTask;

public abstract class BaseXmlItemGetter extends AsyncTask<Void,Void,Boolean> {
	
	private String url;
	
	@Override
	protected void onPreExecute() {
		url=getXmlUrl();
		super.onPreExecute();
	}

	@Override
	protected Boolean doInBackground(Void... params) {
		
		try {
			
			// create http get for sending
			HttpGet request=new HttpGet(url);
			
			// cookie load
			HttpClient client=new DefaultHttpClient();
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
			AppPref.setString("cookieName",cookieList.get(0).getName());
			AppPref.setString("cookieValue",cookieList.get(0).getValue());
			AppPref.setString("cookieDomain",cookieList.get(0).getDomain());
			AppPref.setString("cookiePath",cookieList.get(0).getPath());

			// get data from xml content through JDOM
			SAXBuilder sax=new SAXBuilder();
			Document doc=sax.build(isr);
			Element rss=doc.getRootElement();
			Element channel=rss.getChild("channel");
			List<Element> items=channel.getChildren("item");
			for(Element item : items) {
				processElement(item);
			}
			

		} catch(Exception e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}
	
	protected abstract String getXmlUrl();
	protected abstract void processElement(Element item);
	
	
	

}
