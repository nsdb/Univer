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

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.MeasureSpec;
import android.webkit.WebView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.nsdb.univer.data.AppPref;
import com.nsdb.univer.data.BookData;
import com.nsdb.univer.ui.R;

public class BookDataAdapter extends ArrayAdapter<BookData> {

	Activity activity;
	ArrayList<BookData> dataVisible;
	ArrayList<BookData> dataOriginal;
	ListView view;
	
	String title;
	int rangeMode;
	int saleMode;
	int pageNum;
	int sellerId;
	
	BookDataGetter getter;
		
	public final static int RANGEMODE_ALL=0;
	public final static int RANGEMODE_REGION=1;
	public final static int RANGEMODE_UNIV=2;
	public final static int RANGEMODE_COLLEGE=3;
	public final static int RANGEMODE_MAJOR=4;
	public final static int RANGEMODE_MINE=5;
	public final static int RANGEMODE_OTHER=6;

	public BookDataAdapter(Activity activity, ArrayList<BookData> data, ListView view) {
		super(activity,R.layout.bookdata, data);
		this.activity=activity;
		this.dataVisible=data;
		this.dataOriginal=new ArrayList<BookData>();
		this.view=view;
		rangeMode=-1;
		saleMode=-1;
		pageNum=1;
		sellerId=-1;
		getter=null;
	}

	
	public void updateData(String title, int categorymode, int salemode, int pagenum) {
		this.title=title;
		this.rangeMode=categorymode;
		this.saleMode=salemode;
		this.pageNum=pagenum;

		if(getter != null)
			getter.cancel(true);
		getter=new BookDataGetter();
		getter.execute();
	}	
	public void updateData(String title, int categorymode, int salemode, int pagenum, int sellerId) {
		this.sellerId=sellerId;
		updateData(title,categorymode,salemode,pagenum);
	}
	public void updateData(String title, int salemode, int pagenum) {
		updateData(title,getDefaultRangeMode(),salemode,pagenum);
	}
	
	public void updateView() {
		
		System.out.println("Original Data : "+dataOriginal.size());
		System.out.println("Visible Data : "+dataVisible.size());
		System.out.println("Filter Title : "+title);
		System.out.println("Filter SellBuy : "+saleMode);
		System.out.println("Filter PageNum : "+pageNum);

		if(dataOriginal.size()!=0) {	
			dataVisible.clear();
			for(int i=0;i<dataOriginal.size();i++) {
				dataVisible.add(dataOriginal.get(i));
			}
		}
		
		notifyDataSetChanged();

		// resizing height of listview
		int totalHeight=0;
		int desiredWidth=MeasureSpec.makeMeasureSpec(view.getWidth(),MeasureSpec.AT_MOST);
		for(int i=0;i<getCount();i++) {
			View item=getView(i,null,view);
			item.measure(desiredWidth,MeasureSpec.UNSPECIFIED);
			totalHeight+=item.getMeasuredHeight();
			System.out.println(""+i+" "+item.getMeasuredHeight()+" "+totalHeight);
		}
		ViewGroup.LayoutParams params=view.getLayoutParams();
		params.height=totalHeight+(view.getDividerHeight()*(getCount()-1));
		view.setLayoutParams(params);
		view.requestLayout();
	}

	@Override
	public View getView(int position,View v,ViewGroup Parent) {
		if(v==null)
			v=((LayoutInflater)(activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE)))
			.inflate(R.layout.bookdata,null);
		
		TextView t=(TextView)v.findViewById(R.id.title);
		WebView w=(WebView)v.findViewById(R.id.thumbnail);
		TextView dp=(TextView)v.findViewById(R.id.discount_price);
		TextView op=(TextView)v.findViewById(R.id.original_price);

		if( position >= dataVisible.size() ) {
			t.setText("Invalid");
			w.clearView();
			dp.setText("");
			op.setText("");
		} else {
			
			// title
			t.setText( dataVisible.get(position).title );
			
			// webview
			w.setFocusable(false);
			if(dataVisible.get(position).thumbnail.compareTo("")!=0) {
				System.out.println("thumbnail : "+dataVisible.get(position).thumbnail.substring(1));
				w.loadUrl( activity.getResources().getString(R.string.base_url)+dataVisible.get(position).thumbnail );
			} else {
				w.clearView();
			}
			
			// discount price
			if(dataVisible.get(position).discount_price!=-1) {
				dp.setText( ""+dataVisible.get(position).discount_price );
			} else {
				dp.setText( "" );
			}
			
			// original price
			if(dataVisible.get(position).original_price!=-1) {
				op.setText( ""+dataVisible.get(position).original_price );			
			} else {
				op.setText( "" );
			}
		}

		return v;
		
	}
	
	
	private class BookDataGetter extends AsyncTask<Void,Void,Boolean> {

		@Override
		protected void onPreExecute() {
			dataVisible.clear();
			dataOriginal.clear();
			dataVisible.add(new BookData("불러오는 중..."));
			updateView();
			super.onPreExecute();
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			
			// create url
			// {base_url}/feeds/books?search=<search>&sale=<sale>&category=<category>&id=<id>&page=<page>/
			String url=activity.getResources().getString(R.string.base_url)+'/'
					+activity.getResources().getString(R.string.get_url)+'/'
					+activity.getResources().getString(R.string.book_url)+'/';
			ArrayList<String> getData=new ArrayList<String>();
			getData.add("search="+0);
			getData.add("sale="+saleMode);
			getData.add("category="+rangeMode);	
			switch(rangeMode) {
			case RANGEMODE_ALL: getData.add("id="+0); break;
			case RANGEMODE_REGION: getData.add("id="+AppPref.getRangeData("region").id); break;
			case RANGEMODE_UNIV: getData.add("id="+AppPref.getRangeData("univ").id); break;
			case RANGEMODE_COLLEGE: getData.add("id="+AppPref.getRangeData("college").id); break;
			case RANGEMODE_MAJOR: getData.add("id="+AppPref.getRangeData("major").id); break;
			case RANGEMODE_MINE: getData.add("id="+0); break;
			case RANGEMODE_OTHER: getData.add("id="+sellerId); break;
			}
			getData.add("page="+pageNum);
			for(int i=0;i<getData.size();i++) {
				url=url+getData.get(i);
				if(i != getData.size()-1) url=url+'&';
			}
			url=url+'/';
			
			System.out.println("XML URL : "+url);

			
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
				
				// get bookdata from xml through JDOM
				HttpResponse response=client.execute(request);
				InputStream is=response.getEntity().getContent();
				InputStreamReader isr=new InputStreamReader(is,"utf-8");
				SAXBuilder sax=new SAXBuilder();
				Document doc=sax.build(isr);
				Element rss=doc.getRootElement();
				Element channel=rss.getChild("channel");
				List<Element> items=channel.getChildren("item");
				for(Element item : items) {
					dataOriginal.add( new BookData(item) );
				}
				
				// cookie save
				AppPref.setString("cookieName",cookieList.get(0).getName());
				AppPref.setString("cookieValue",cookieList.get(0).getValue());
				AppPref.setString("cookieDomain",cookieList.get(0).getDomain());
				AppPref.setString("cookiePath",cookieList.get(0).getPath());

			} catch(Exception e) {
				e.printStackTrace();
				return false;
			}
			
			return true;
		}
		
		@Override
		protected void onPostExecute(Boolean result) {
			dataVisible.clear();
			if(result==true && dataOriginal.size()==0) {
				dataVisible.add(new BookData("데이터 없음"));
			} else if(result==false) {
				dataVisible.add(new BookData("읽기 실패"));
				dataVisible.add(new BookData("서버에 연결할 수 없습니다"));				
			}
			updateView();
		}
	}
	
	public static int getDefaultRangeMode() {
		if(AppPref.getRangeData("region").title.compareTo("")==0) {
			return RANGEMODE_ALL;
		} else if(AppPref.getRangeData("univ").title.compareTo("")==0) {
			return RANGEMODE_REGION;
		} else if(AppPref.getRangeData("college").title.compareTo("")==0) {
			return RANGEMODE_UNIV;
		} else if(AppPref.getRangeData("major").title.compareTo("")==0) {
			return RANGEMODE_COLLEGE;
		} else {
			return RANGEMODE_MAJOR;
		}
	}

}
