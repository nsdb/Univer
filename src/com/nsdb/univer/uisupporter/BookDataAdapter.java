package com.nsdb.univer.uisupporter;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.nsdb.univer.data.AppPref;
import com.nsdb.univer.data.BookData;
import com.nsdb.univer.ui.R;

public class BookDataAdapter extends ArrayAdapter<BookData> {

	Activity activity;
	ArrayList<BookData> data_visible;
	ArrayList<BookData> data_original;
	
	String title;
	int categorymode;
	int salemode;
	int pagenum;
	int sellerId;
	
	BookDataGetter getter;
		
	public final static int CATEGORYMODE_ALL=0;
	public final static int CATEGORYMODE_REGION=1;
	public final static int CATEGORYMODE_UNIV=2;
	public final static int CATEGORYMODE_COLLEGE=3;
	public final static int CATEGORYMODE_MAJOR=4;
	public final static int CATEGORYMODE_MINE=5;
	public final static int CATEGORYMODE_OTHER=6;

	public BookDataAdapter(Activity activity, ArrayList<BookData> data) {
		super(activity,R.layout.bookdata, data);
		this.activity=activity;
		this.data_visible=data;
		this.data_original=new ArrayList<BookData>();
		categorymode=-1;
		salemode=-1;
		pagenum=1;
		sellerId=-1;
		getter=null;
	}

	
	public void updateData(String title, int categorymode, int salemode, int pagenum) {
		this.title=title;
		this.categorymode=categorymode;
		this.salemode=salemode;
		this.pagenum=pagenum;

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
		updateData(title,getDefaultCategoryMode(),salemode,pagenum);
	}
	
	public void updateView() {
		
		System.out.println("Original Data : "+data_original.size());
		System.out.println("Visible Data : "+data_visible.size());
		System.out.println("Filter Title : "+title);
		System.out.println("Filter SellBuy : "+salemode);
		System.out.println("Filter PageNum : "+pagenum);

		if(data_original.size()!=0) {	
			data_visible.clear();
			for(int i=0;i<data_original.size();i++) {
				data_visible.add(data_original.get(i));
			}
		}
		
		notifyDataSetChanged();
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

		if( position >= data_visible.size() ) {
			t.setText("Invalid");
			w.clearView();
			dp.setText("");
			op.setText("");
		} else {
			
			// title
			t.setText( data_visible.get(position).title );
			
			// webview
			w.setFocusable(false);
			if(data_visible.get(position).thumbnail.compareTo("")!=0) {
				System.out.println("thumbnail : "+data_visible.get(position).thumbnail.substring(1));
				w.loadUrl( activity.getResources().getString(R.string.base_url)
						+data_visible.get(position).thumbnail.substring(1) );
			} else {
				w.clearView();
			}
			
			// discount price
			if(data_visible.get(position).discount_price!=-1) {
				dp.setText( ""+data_visible.get(position).discount_price );
			} else {
				dp.setText( "" );
			}
			
			// original price
			if(data_visible.get(position).original_price!=-1) {
				op.setText( ""+data_visible.get(position).original_price );			
			} else {
				op.setText( "" );
			}
		}

		return v;
		
	}
	
	
	private class BookDataGetter extends AsyncTask<Void,Void,Void> {

		@Override
		protected void onPreExecute() {
			data_visible.clear();
			data_visible.add(new BookData("불러오는 중..."));
			notifyDataSetChanged();
			super.onPreExecute();
		}

		@Override
		protected Void doInBackground(Void... params) {
			
			// create url
			// {base_url}/feeds/books?search=<search>&sale=<sale>&category=<category>&id=<id>&page=<page>/
			String url=activity.getResources().getString(R.string.base_url)+'/'
					+activity.getResources().getString(R.string.get_url)+'/'
					+activity.getResources().getString(R.string.book_url)+'/';
			ArrayList<String> getData=new ArrayList<String>();
			getData.add("search="+0);
			getData.add("sale="+salemode);
			getData.add("category="+categorymode);	
			switch(categorymode) {
			case CATEGORYMODE_ALL: getData.add("id="+0); break;
			case CATEGORYMODE_REGION: getData.add("id="+AppPref.getRangeData("region").id); break;
			case CATEGORYMODE_UNIV: getData.add("id="+AppPref.getRangeData("univ").id); break;
			case CATEGORYMODE_COLLEGE: getData.add("id="+AppPref.getRangeData("college").id); break;
			case CATEGORYMODE_MAJOR: getData.add("id="+AppPref.getRangeData("major").id); break;
			case CATEGORYMODE_MINE: getData.add("id="+0); break;
			case CATEGORYMODE_OTHER: getData.add("id="+sellerId); break;
			}
			getData.add("page="+pagenum);
			for(int i=0;i<getData.size();i++) {
				url=url+getData.get(i);
				if(i != getData.size()-1) url=url+'&';
			}
			url=url+'/';
			
			System.out.println("XML URL : "+url);

			
			try {
				// get xml stream from server
				HttpGet request=new HttpGet(url);
				HttpClient client=new DefaultHttpClient();
				HttpResponse response=client.execute(request);
				InputStream is=response.getEntity().getContent();
				InputStreamReader isr=new InputStreamReader(is,"utf-8");
				
				// clear data
				data_visible.clear();
				data_original.clear();
				
				// get bookdata from xml through JDOM
				SAXBuilder sax=new SAXBuilder();
				Document doc=sax.build(isr);
				Element rss=doc.getRootElement();
				Element channel=rss.getChild("channel");
				List<Element> items=channel.getChildren("item");
				for(Element item : items) {
					data_original.add( new BookData( item.getChild("title").getText(),
														Integer.parseInt(item.getChild("original_price").getText()),
														Integer.parseInt(item.getChild("discount_price").getText()),
							  				            item.getChild("thumbnail").getText(),
							  			                Integer.parseInt(item.getChild("id").getText()) ) );
				}
				
			} catch(Exception e) {
				e.printStackTrace();
				data_visible.add(new BookData("읽기 실패"));
				data_visible.add(new BookData("서버에 연결할 수 없습니다"));
			}
			
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			updateView();
		}
	}
	
	public static int getDefaultCategoryMode() {
		if(AppPref.getRangeData("region").title.compareTo("")==0) {
			return CATEGORYMODE_ALL;
		} else if(AppPref.getRangeData("univ").title.compareTo("")==0) {
			return CATEGORYMODE_REGION;
		} else if(AppPref.getRangeData("college").title.compareTo("")==0) {
			return CATEGORYMODE_UNIV;
		} else if(AppPref.getRangeData("major").title.compareTo("")==0) {
			return CATEGORYMODE_COLLEGE;
		} else {
			return CATEGORYMODE_MAJOR;
		}
	}

}
