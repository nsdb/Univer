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

import com.nsdb.univer.data.AppPref;
import com.nsdb.univer.data.ProfessorData;
import com.nsdb.univer.data.RangeData;
import com.nsdb.univer.ui.R;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class RangeDataAdapter extends ArrayAdapter<RangeData> {

	Activity activity;
	ArrayList<RangeData> dataVisible;
	ArrayList<RangeData> dataOriginal;
	
	String filterRange;
	String filterTitle;
	
	RangeDataGetter getter;

	public RangeDataAdapter(Activity activity, ArrayList<RangeData> data) {
		super(activity,R.layout.stringdata, data);
		this.activity=activity;
		this.dataVisible=data;
		this.dataOriginal=new ArrayList<RangeData>();
		filterRange="";
		filterTitle="";
		getter=null;
	}

	
	public void updateData(String filterRange) {
		this.filterRange=filterRange;
		if(getter != null)
			getter.cancel(true);
		getter=new RangeDataGetter();
		getter.execute();
	}
	
	public void updateView(String filterTitle) {
		this.filterTitle=filterTitle;
		
		System.out.println("Original Data : "+dataOriginal.size());
		System.out.println("Visible Data : "+dataVisible.size());
		System.out.println("Filter Title : "+filterTitle);

		if(dataOriginal.size()!=0) {
			dataVisible.clear();
			for(int i=0;i<dataOriginal.size();i++) {
				if(dataOriginal.get(i).title.contains(filterTitle))
					dataVisible.add(dataOriginal.get(i));
			}
		}
		
		notifyDataSetChanged();
	}

	@Override
	public View getView(int position,View v,ViewGroup Parent) {
		if(v==null)
			v=((LayoutInflater)(activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE)))
			.inflate(R.layout.stringdata,null);
		
		TextView t=(TextView)v.findViewById(R.id.text);
		if( position >= dataVisible.size() ) {
			t.setText("Invalid");
		} else {
			t.setText( dataVisible.get(position).title );
		}
		return v;
		
	}
	
	
	private class RangeDataGetter extends AsyncTask<Void,Void,Boolean> {

		@Override
		protected void onPreExecute() {
			dataVisible.clear();
			dataOriginal.clear();
			dataVisible.add(new RangeData("불러오는 중..."));
			updateView("");
			super.onPreExecute();
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			
			// create url
			// region : 1.234.23.142/~ypunval/feeds/region/
			// univ : 1.234.23.142/~ypunval/feeds/univ/(region_id)/
			// college:1.234.23.142/~ypunval/feeds/collage/(univ_id)/
			// major  :1.234.23.142/~ypunval/feeds/major/(college_id)/
			String url=activity.getResources().getString(R.string.base_url)+'/'
					+activity.getResources().getString(R.string.get_url)+'/'
					+filterRange+"/";
			if(filterRange.compareTo("univ")==0 && AppPref.getRangeData("region").id != -1)
				url=url+AppPref.getRangeData("region").id+"/";
			else if(filterRange.compareTo("college")==0 && AppPref.getRangeData("univ").id != -1)
				url=url+AppPref.getRangeData("univ").id+"/";
			else if(filterRange.compareTo("major")==0 && AppPref.getRangeData("college").id != -1)
				url=url+AppPref.getRangeData("college").id+"/";
			System.out.println("XML URL : "+url);
					
			try {
				// get xml stream from server
				HttpGet request=new HttpGet(url);
				HttpClient client=new DefaultHttpClient();
				HttpResponse response=client.execute(request);
				InputStream is=response.getEntity().getContent();
				InputStreamReader isr=new InputStreamReader(is,"utf-8");
				
				// get rangedata from xml through JDOM
				SAXBuilder sax=new SAXBuilder();
				Document doc=sax.build(isr);
				Element rss=doc.getRootElement();
				Element channel=rss.getChild("channel");
				List<Element> items=channel.getChildren("item");
				for(Element item : items) {
					dataOriginal.add( new RangeData( item.getChild("title").getText(),
							  				          item.getChild("nick").getText(),
							  			              Integer.parseInt(item.getChild("id").getText()) ) );
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
				dataVisible.add(new RangeData("데이터 없음"));
			} else if(result==false) {
				dataVisible.add(new RangeData("읽기 실패"));
				dataVisible.add(new RangeData("서버에 연결할 수 없습니다"));				
			}
			updateView("");
		}
	}
}
