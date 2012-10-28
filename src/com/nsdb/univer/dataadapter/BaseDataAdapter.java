package com.nsdb.univer.dataadapter;

import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.client.methods.HttpGet;
import org.jdom2.Element;

import com.nsdb.univer.common.NetworkSupporter;
import com.nsdb.univer.ui.R;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.MeasureSpec;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public abstract class BaseDataAdapter<T> {
	
	// View setting
	protected Activity activity;
	private int dataResourceId;
	private View footerView;
	private ArrayList<T> dataVisible;
	private ArrayAdapter<T> adapter;
	private ListView view;
	private boolean inScrollView; // For ListView in ScrollView

	// Data getting
	private DataGetter getter;
	private ArrayList<T> dataOriginal;
	private boolean loadable;
	protected final static int RESULT_SUCCESS=1;
	protected final static int RESULT_EMPTY=2;
	protected final static int RESULT_ERROR=3;
	
	
	// constructor
	BaseDataAdapter(Activity activity,int dataResourceId,ListView view,boolean inScrollView) {
		this.activity=activity;
		this.dataResourceId=dataResourceId;
		this.footerView=((LayoutInflater)(activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE)))
				.inflate(R.layout.stringdata,null);
		this.dataVisible=new ArrayList<T>();
		this.adapter=new BaseArrayAdapter(activity,dataResourceId,dataVisible);
		this.view=view;
		this.inScrollView=inScrollView;
		view.addFooterView(footerView);
		view.setAdapter(adapter);
				
		this.dataOriginal=new ArrayList<T>();
		this.getter=null;
		this.loadable=true;
	}
	
	
	// 1. get data from server
	public final void updateData() {
		if(loadable==false) return;		
		if(getter != null)
			getter.cancel(true);
		getter=new DataGetter();
		getter.execute();
		loadable=false;
	}
	private final class DataGetter extends AsyncTask<Void,Void,Integer> {

		@Override
		protected void onPreExecute() {
			
			// footerView
			TextView t=(TextView)footerView.findViewById(R.id.text);
			t.setText("불러오는 중...");
			adapter.notifyDataSetChanged();
			view.setSelection(adapter.getCount()-1);
			
			customPreGetAction();
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
				t.setText("불러오는 중...");
				break;
			case RESULT_EMPTY:
				t.setText("데이터 없음");
				break;
			case RESULT_ERROR:
				t.setText("에러 발생");
				break;
			}
			adapter.notifyDataSetChanged();
			
			customPostGetAction(result);
			updateView(result);
		}

	}
	protected final void init() {
		loadable=true;
		dataVisible.clear();
		dataOriginal.clear();
		adapter.notifyDataSetChanged();
	}
	protected void customPreGetAction() {}
	protected void customPostGetAction(int result) {}
	protected abstract String getXmlUrl();
	protected abstract T convertElement(Element item);
	
	
	// 2. update data view (after finish update data asynctask)
	protected final void updateView() {
		updateView(RESULT_SUCCESS);
	}
	protected final void updateView(int result) {
		
		// visible data update if success
		if(result==RESULT_SUCCESS) {
			dataVisible.clear();
			for(int i=0;i<dataOriginal.size();i++) {
				if(filterOriginalData(dataOriginal.get(i))==true)
					dataVisible.add(dataOriginal.get(i));
			}
			adapter.notifyDataSetChanged();
		}

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
			
			if(position<dataVisible.size()) { // footerView Check
				dataViewSetting(position,v);
			}
			
			return v;
			
		}
	}
	protected boolean filterOriginalData(T data) { return true; }
	protected abstract void dataViewSetting(int position,View v);
	
	// 3. getter setter
	public T get(int position) {
		if(position<dataVisible.size())
			return dataVisible.get(position);
		else
			return null;
	}
	public boolean isLoadable() { return loadable; }
	public int getCount() { return dataVisible.size(); }
	public ListView getView() { return view; }
}
