package com.nsdb.univer.dataadapter;

import java.util.ArrayList;
import java.util.List;

import org.jdom2.Element;

import com.nsdb.univer.common.BaseXmlItemGetter;

import android.app.Activity;
import android.content.Context;
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
	private DataGetter getter;
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
	public final void updateData() {
		if(getter != null)
			getter.cancel(true);
		getter=new DataGetter();
		getter.execute();
	}
	private final class DataGetter extends BaseXmlItemGetter {

		@Override
		protected void onPreExecute() {
			
			// notify ready
			dataVisible.clear();
			setReadyData();
			adapter.notifyDataSetChanged();
			
			// clear last data
			dataOriginal.clear();

			super.onPreExecute();
		}

		@Override
		protected void onPostExecute(Boolean result) {
			
			// notify end
			dataVisible.clear();
			if(result==false)
				setEndData(RESULT_ERROR);
			if(dataOriginal.size()==0 && result==true)
				setEndData(RESULT_EMPTY);
			else
				setEndData(RESULT_SUCCESS);
			//adapter.notifyDataSetChanged();  already in updateView()
			updateView();
		}

		@Override
		protected String getXmlUrl() {
			return BaseDataAdapter.this.getXmlUrl();
		}

		@Override
		protected void processElement(Element item) {
			add(convertElement(item));
		}

	}
	protected abstract void setReadyData();
	protected abstract void setEndData(int result);
	protected abstract String getXmlUrl();
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
