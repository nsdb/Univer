package com.nsdb.univer.dataadapter;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
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

import com.nsdb.univer.data.AppPref;
import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.MeasureSpec;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public abstract class BaseDataAdapter<T> {
	
	// View setting
	protected Activity activity;
	private int dataResourceId;
	private ArrayList<T> dataVisible;
	private ArrayAdapter<T> adapter;
	private ListView view;
	private boolean inScrollView; // For ListView in ScrollView

	// Data getting
	private HttpGETGetter getter;
	private ArrayList<T> dataOriginal;
	protected final static int RESULT_SUCCESS=1;
	protected final static int RESULT_EMPTY=2;
	protected final static int RESULT_ERROR=3;
	
	
	// constructor
	BaseDataAdapter(Activity activity,int dataResourceId,ListView view,boolean inScrollView) {
		this.activity=activity;
		this.dataResourceId=dataResourceId;
		this.dataVisible=new ArrayList<T>();
		this.adapter=new BaseArrayAdapter(activity,dataResourceId,dataVisible);
		this.view=view;
		this.inScrollView=inScrollView;
		view.setAdapter(adapter);
				
		this.dataOriginal=new ArrayList<T>();
		this.getter=null;	
	}
	
	
	// 1. get data from server
	// Activity only can use it.
	public final void updateData() {
		if(getter != null)
			getter.cancel(true);
		getter=new HttpGETGetter();
		getter.execute();
	}
	private final class HttpGETGetter extends AsyncTask<Void,Void,Integer> {

		private String url;
		
		@Override
		protected void onPreExecute() {
			
			// notify ready
			dataVisible.clear();
			setReadyData();
			adapter.notifyDataSetChanged();
			
			// clear last data
			dataOriginal.clear();

			// create server url
			url=createHttpUrl();
			
			super.onPreExecute();
		}

		@Override
		protected Integer doInBackground(Void... params) {
			
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
				
				// get data from xml through JDOM
				HttpResponse response=client.execute(request);
				InputStream is=response.getEntity().getContent();
				InputStreamReader isr=new InputStreamReader(is,"utf-8");
				SAXBuilder sax=new SAXBuilder();
				Document doc=sax.build(isr);
				Element rss=doc.getRootElement();
				Element channel=rss.getChild("channel");
				List<Element> items=channel.getChildren("item");
				for(Element item : items) {
					dataOriginal.add(convertElement(item));
				}
				
				// cookie save
				AppPref.setString("cookieName",cookieList.get(0).getName());
				AppPref.setString("cookieValue",cookieList.get(0).getValue());
				AppPref.setString("cookieDomain",cookieList.get(0).getDomain());
				AppPref.setString("cookiePath",cookieList.get(0).getPath());

			} catch(Exception e) {
				e.printStackTrace();
				return RESULT_ERROR;
			}

			if(dataOriginal.size()==0)
				return RESULT_EMPTY;
			else
				return RESULT_SUCCESS;
		}

		@Override
		protected void onPostExecute(Integer result) {
			
			// notify end
			dataVisible.clear();
			setEndData(result);
			//adapter.notifyDataSetChanged();  already in updateView()
			updateView();
		}
	}
	protected abstract void setReadyData();
	protected abstract void setEndData(int result);
	protected abstract String createHttpUrl();
	protected abstract T convertElement(Element item);
	
	
	// 2. update data view (after finish update data asynctask)
	protected final void updateView() {
		
		// visible data update
		if(dataOriginal.size()!=0) {	// if got data is empty, message in dataVisible is kept.
			dataVisible.clear();
			for(int i=0;i<dataOriginal.size();i++)
				if(filterOriginalData(dataOriginal.get(i))==true)
					dataVisible.add(dataOriginal.get(i));
		}
		adapter.notifyDataSetChanged(); // It is on outside because onPostExecute() don't use it.

		// resizing height of listview (For ListView in ScrollView)
		if(inScrollView) {
			int totalHeight=0;
			int desiredWidth=MeasureSpec.makeMeasureSpec(view.getWidth(),MeasureSpec.AT_MOST);
			for(int i=0;i<adapter.getCount();i++) {
				View item=adapter.getView(i,null,view);
				item.measure(desiredWidth,MeasureSpec.UNSPECIFIED);
				totalHeight+=item.getMeasuredHeight();
				System.out.println(""+i+" "+item.getMeasuredHeight()+" "+totalHeight);
			}
			ViewGroup.LayoutParams params=view.getLayoutParams();
			params.height=totalHeight+(view.getDividerHeight()*(adapter.getCount()-1));
			view.setLayoutParams(params);
			view.requestLayout();
		}		
	}
	private final class BaseArrayAdapter extends ArrayAdapter<T> {

		public BaseArrayAdapter(Context context, int textViewResourceId, List<T> objects) {
			super(context, textViewResourceId, objects);
		}
		
		@Override
		public View getView(int position,View v,ViewGroup Parent) {
			if(v==null)
				v=((LayoutInflater)(activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE)))
				.inflate(dataResourceId,null);
			
			dataViewSetting(position,v);
			
			return v;
		}
	}
	protected boolean filterOriginalData(T data) { return true; }
	protected abstract void dataViewSetting(int position,View v);
	
	// 3. getter setter
	public T get(int position) { return dataVisible.get(position); }
	protected void add(T data) { dataVisible.add(data); }
}
