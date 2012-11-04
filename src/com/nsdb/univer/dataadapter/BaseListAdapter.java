package com.nsdb.univer.dataadapter;

import java.util.ArrayList;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.MeasureSpec;
import android.widget.ArrayAdapter;
import android.widget.ListView;


// 아답터는 자신에게 주어진 데이터를 책임진다. 바르게 표시하고, 바른 데이터를 전달해야 한다.
public abstract class BaseListAdapter<T> extends ArrayAdapter<T> {
	
	private Activity activity;
	private int dataResourceId;
	private ArrayList<T> data;
	private ListView view;

	public BaseListAdapter(Activity activity, int dataResourceId, ArrayList<T> objects, ListView view) {
		super(activity, dataResourceId, objects);
		this.activity=activity;
		this.dataResourceId=dataResourceId;
		this.data=objects;
		this.view=view;
	}
	
	@Override
	public View getView(int position,View v,ViewGroup Parent) {
		if(v==null)
			v=((LayoutInflater)(activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE)))
			.inflate(dataResourceId,null);
		
		position-=view.getHeaderViewsCount();		
		if(position>=0 && position<data.size()) {
			setView(position,v);
		}
		
		return v;
		
	}
	protected abstract void setView(int position,View v);
	
	
	public void tuneToScrollView() {
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

	
	public T get(int position) {
		position-=view.getHeaderViewsCount();
		if(position>=0 && position<data.size())
			return data.get(position);
		else
			return null;
	}
	public ArrayList<T> getDataList() { return data; }

}
