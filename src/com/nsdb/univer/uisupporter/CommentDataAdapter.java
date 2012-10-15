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

import com.nsdb.univer.data.CommentData;
import com.nsdb.univer.ui.R;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class CommentDataAdapter extends ArrayAdapter<CommentData> {

	Activity activity;
	ArrayList<CommentData> dataVisible;
	ArrayList<CommentData> dataOriginal;
	ListView view;
	
	int professor_id;
	int pageNum;
	CommentDataGetter getter;

	public CommentDataAdapter(Activity activity, ArrayList<CommentData> data, ListView view) {
		super(activity,R.layout.stringdata, data);
		this.activity=activity;
		this.dataVisible=data;
		this.dataOriginal=new ArrayList<CommentData>();
		this.view=view;
		professor_id=-1;
		pageNum=-1;
		getter=null;
	}

	
	public void updateData(int professor_id,int pageNum) {
		this.professor_id=professor_id;
		this.pageNum=pageNum;
		if(getter != null)
			getter.cancel(true);
		getter=new CommentDataGetter();
		getter.execute();
	}
	
	public void updateView() {
		
		System.out.println("Original Data : "+dataOriginal.size());
		System.out.println("Visible Data : "+dataVisible.size());
		System.out.println("Professor Id : "+professor_id);

		if(dataOriginal.size()!=0) {
			dataVisible.clear();
			for(int i=0;i<dataOriginal.size();i++) {
				dataVisible.add(dataOriginal.get(i));
			}
		}
		
		notifyDataSetChanged();
		LayoutParams lp=view.getLayoutParams();
		lp.height=activity.getResources().getDimensionPixelSize(R.dimen.commentdata_height)*dataVisible.size();
		view.setLayoutParams(lp);
	}

	@Override
	public View getView(int position,View v,ViewGroup Parent) {
		if(v==null)
			v=((LayoutInflater)(activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE)))
			.inflate(R.layout.commentdata,null);
		
		TextView t=(TextView)v.findViewById(R.id.title);
		TextView d=(TextView)v.findViewById(R.id.description);
		//TextView p=(TextView)v.findViewById(R.id.pubdate);
		t.setText(dataVisible.get(position).title);
		d.setText(dataVisible.get(position).description);
		//p.setText(dataVisible.get(position).pubDate);
		return v;
		
	}
	
	
	private class CommentDataGetter extends AsyncTask<Void,Void,Void> {

		@Override
		protected void onPreExecute() {
			dataVisible.clear();
			dataVisible.add(new CommentData("불러오는 중..."));
			updateView();
			super.onPreExecute();
		}

		@Override
		protected Void doInBackground(Void... params) {
			
			// create url
			// {base_url}/feeds/comments/professors/professor_id=<professor_id>&page=<page>/
			String url=activity.getResources().getString(R.string.base_url)+'/'
					+activity.getResources().getString(R.string.get_url)+'/'
					+activity.getResources().getString(R.string.comment_url)+'/'
					+activity.getResources().getString(R.string.professor_url)+'/';
			ArrayList<String> getData=new ArrayList<String>();
			getData.add("professor_id="+professor_id);
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
				
				// clear data
				dataVisible.clear();
				dataOriginal.clear();
				
				// get rangedata from xml through JDOM
				SAXBuilder sax=new SAXBuilder();
				Document doc=sax.build(isr);
				Element rss=doc.getRootElement();
				Element channel=rss.getChild("channel");
				List<Element> items=channel.getChildren("item");
				for(Element item : items) {
					dataOriginal.add( new CommentData( item.getChild("title").getText(),
							  				          item.getChild("description").getText(),
							  			              item.getChild("pubDate").getText() ) );
				}
				
			} catch(Exception e) {
				e.printStackTrace();
				dataVisible.add(new CommentData("읽기 실패"));
				dataVisible.add(new CommentData("서버에 연결할 수 없습니다"));
			}
			
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			updateView();
		}
	}
}
