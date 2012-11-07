package com.nsdb.univer.dataadapter;

import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.client.methods.HttpGet;
import org.jdom2.Element;

import com.nsdb.univer.common.NetworkSupporter;
import com.nsdb.univer.R;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

public abstract class BaseDataLoader<T> {
	
	private Activity activity;
	private ListView view;
	private BaseListAdapter<T> adapter;

	// other setting
	private View footerView;
	private boolean inScrollView; // For ListView in ScrollView

	// Data getting
	private DataGetter getter;
	private ArrayList<T> dataOriginal;
	private boolean loadable;
	protected final static int RESULT_SUCCESS=1;
	protected final static int RESULT_EMPTY=2;
	protected final static int RESULT_ERROR=3;
	
	
	// constructor
	BaseDataLoader(Activity activity,int dataResourceId,ListView view,boolean inScrollView) {
		this.activity=activity;
		this.view=view;
		this.adapter=createListAdapter(activity,dataResourceId,new ArrayList<T>(),view);

		this.footerView=((LayoutInflater)(activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE)))
				.inflate(R.layout.stringdata,null);
		TextView t=(TextView)footerView.findViewById(R.id.text);
		t.setText("FooterView");
		this.inScrollView=inScrollView;
				
		this.dataOriginal=new ArrayList<T>();
		this.getter=null;
		this.loadable=true;

		// must add footerview before set adapter!
		view.addFooterView(footerView);
		view.setAdapter(adapter);

	}
	
	
	// 0. adapter
	protected abstract BaseListAdapter<T> createListAdapter(Activity activity, int dataResourceId, ArrayList<T> dataVisible, ListView view);
	
	
	// 1. get data from server
	public void updateData() {
		if(loadable==false) return;		
		if(getter != null)
			getter.cancel(true);
		getter=new DataGetter();
		getter.execute();
		loadable=false;
	}
	protected final void init() {
		loadable=true;
		adapter.getDataList().clear();
		dataOriginal.clear();
		adapter.notifyDataSetChanged();
	}

	private final class DataGetter extends AsyncTask<Void,Void,Integer> {

		@Override
		protected void onPreExecute() {
			
			// footerView
			TextView t=(TextView)footerView.findViewById(R.id.text);
			t.setText("불러오는 중...");
			if(inScrollView) adapter.tuneToScrollView();
			
			super.onPreExecute();
		}

		@Override
		protected Integer doInBackground(Void... params) {
			
			try {
				
				// create request for sending
				HttpGet request=new HttpGet(getXmlUrl());
				
				// get result
				InputStreamReader isr=NetworkSupporter.getStreamFromRequest(request);
				List<Element> items=NetworkSupporter.getXmlElementsFromStream(isr);
				int count=0;
				for(count=0 ; count<items.size() ; count++) {
					dataOriginal.add( convertElement(items.get(count)) );
				}
				isr.close();
				
				if(count==0) // dataOriginal is not always empty before.
					return RESULT_EMPTY;
				else
					return RESULT_SUCCESS;

			} catch(Exception e) {
				e.printStackTrace();
				return RESULT_ERROR;
			}
			
		}

		@Override
		protected void onPostExecute(Integer result) {
			
			// loadable
			if(result==RESULT_SUCCESS) {
				loadable=true;
			} else {
				loadable=false;
			}
			
			// footerView
			TextView t=(TextView)footerView.findViewById(R.id.text);
			switch(result) {
			case RESULT_SUCCESS:
				t.setText("");
				break;
			case RESULT_EMPTY:
				t.setText("데이터 없음");
				break;
			case RESULT_ERROR:
				t.setText("에러 발생");
				break;
			}
			
			updateView(result);
		}

	}
	protected abstract String getXmlUrl();
	protected abstract T convertElement(Element item);
	
	
	// 2. update data view (after finish update data asynctask)
	protected void updateView(int result) {
		
		// visible data update if success
		if(result==RESULT_SUCCESS) {
			ArrayList<T> data=adapter.getDataList();
			data.clear();
			for(int i=0;i<dataOriginal.size();i++) {
				data.add(dataOriginal.get(i));
			}
			adapter.notifyDataSetChanged();
		}

		if(inScrollView) adapter.tuneToScrollView();
	}
	
	// getter setter
	public T get(int position) { return adapter.get(position); }
	public Activity getActivity() { return activity; }
	public ArrayList<T> getDataList() { return adapter.getDataList(); }
	public boolean isLoadable() { return loadable; }
	public ListView getView() { return view; }
}
