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
import android.view.ViewGroup.LayoutParams;
import android.webkit.WebView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.nsdb.univer.data.AppPref;
import com.nsdb.univer.data.BookData;
import com.nsdb.univer.data.ProfessorData;
import com.nsdb.univer.ui.R;

public class ProfessorDataAdapter extends ArrayAdapter<ProfessorData> {

	Activity activity;
	ArrayList<ProfessorData> dataVisible;
	ArrayList<ProfessorData> dataOriginal;
	ListView view;
	
	String title;
	int rangeMode;
	int pageNum;
	
	ProfessorDataGetter getter;
		
	public final static int RANGEMODE_ALL=0;
	public final static int RANGEMODE_REGION=1;
	public final static int RANGEMODE_UNIV=2;
	public final static int RANGEMODE_COLLEGE=3;
	public final static int RANGEMODE_MAJOR=4;

	public ProfessorDataAdapter(Activity activity, ArrayList<ProfessorData> data, ListView view) {
		super(activity,R.layout.bookdata, data);
		this.activity=activity;
		this.dataVisible=data;
		this.dataOriginal=new ArrayList<ProfessorData>();
		this.view=view;
		rangeMode=-1;
		pageNum=1;
		getter=null;
	}

	
	public void updateData(String title, int categorymode, int pagenum) {
		this.title=title;
		this.rangeMode=categorymode;
		this.pageNum=pagenum;

		if(getter != null)
			getter.cancel(true);
		getter=new ProfessorDataGetter();
		getter.execute();
	}	
	
	public void updateView() {
		
		System.out.println("Original Data : "+dataOriginal.size());
		System.out.println("Visible Data : "+dataVisible.size());
		System.out.println("Filter Title : "+title);
		System.out.println("Filter PageNum : "+pageNum);

		if(dataOriginal.size()!=0) {	
			dataVisible.clear();
			for(int i=0;i<dataOriginal.size();i++) {
				dataVisible.add(dataOriginal.get(i));
			}
		}
		
		notifyDataSetChanged();
		LayoutParams lp=view.getLayoutParams();
		lp.height=activity.getResources().getDimensionPixelSize(R.dimen.professordata_height)*dataVisible.size();
		view.setLayoutParams(lp);
	}

	@Override
	public View getView(int position,View v,ViewGroup Parent) {
		if(v==null)
			v=((LayoutInflater)(activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE)))
			.inflate(R.layout.professordata,null);

		TextView t=(TextView)v.findViewById(R.id.title);
		WebView w=(WebView)v.findViewById(R.id.thumbnail);
		TextView rt=(TextView)v.findViewById(R.id.totaltxt);
		RatingBar r=(RatingBar)v.findViewById(R.id.total);
		
		if( position >= dataVisible.size() ) {
			t.setText("Invalid");
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
			
			// ratingbar
			if(dataVisible.get(position).count > 0) {
				double d=dataVisible.get(position).total/(dataVisible.get(position).count*5);
				d=Math.round(d*100)/100;
				rt.setText(""+d);
				r.setRating( (float)d/2 );
			} else {
				rt.setText("0.0");
				r.setRating(0.0f);				
			}
			
		}
		
		return v;
		
	}
	
	
	private class ProfessorDataGetter extends AsyncTask<Void,Void,Boolean> {

		@Override
		protected void onPreExecute() {
			dataVisible.clear();
			dataOriginal.clear();
			dataVisible.add(new ProfessorData("불러오는 중..."));
			updateView();
			super.onPreExecute();
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			
			// create url
			// {base_url}/feeds/professors/search=<search>&category=<category>&id=<id>&page=<page>/
			String url=activity.getResources().getString(R.string.base_url)+'/'
					+activity.getResources().getString(R.string.get_url)+'/'
					+activity.getResources().getString(R.string.professor_url)+'/';
			ArrayList<String> getData=new ArrayList<String>();
			getData.add("search="+0);
			getData.add("category="+rangeMode);	
			switch(rangeMode) {
			case RANGEMODE_ALL: getData.add("id="+0); break;
			case RANGEMODE_REGION: getData.add("id="+AppPref.getRangeData("region").id); break;
			case RANGEMODE_UNIV: getData.add("id="+AppPref.getRangeData("univ").id); break;
			case RANGEMODE_COLLEGE: getData.add("id="+AppPref.getRangeData("college").id); break;
			case RANGEMODE_MAJOR: getData.add("id="+AppPref.getRangeData("major").id); break;
			}
			getData.add("page="+pageNum);
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
				
				// get professordata from xml through JDOM
				SAXBuilder sax=new SAXBuilder();
				Document doc=sax.build(isr);
				Element rss=doc.getRootElement();
				Element channel=rss.getChild("channel");
				List<Element> items=channel.getChildren("item");
				for(Element item : items) {
					dataOriginal.add( new ProfessorData(item) );
				}
				
			} catch(Exception e) {
				e.printStackTrace();
				return false;
			}
			
			return true;
		}
		
		@Override
		protected void onPostExecute(Boolean result) {
			if(result==true && dataOriginal.size()==0) {
				dataVisible.add(new ProfessorData("데이터 없음"));
			} else if(result==false) {
				dataVisible.add(new ProfessorData("읽기 실패"));
				dataVisible.add(new ProfessorData("서버에 연결할 수 없습니다"));				
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
