package com.nsdb.univer.supporter.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public abstract class BaseArrayAdapter<T> extends ArrayAdapter<T> {
	
	private int dataResourceId;
	private ListView view;

	public BaseArrayAdapter(Context context, int dataResourceId, ListView view) {
		super(context, dataResourceId);
		this.dataResourceId=dataResourceId;
		this.view=view;
		view.setAdapter(this);
	}
	
	// View Setting
	@Override
	public View getView(int position,View v,ViewGroup Parent) {
		if(v==null)
			v=((LayoutInflater)(getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE)))
			.inflate(dataResourceId,null);
		
		if(position>=0 && position<getCount()) {
			setView(position,v);
		}
		
		return v;	
	}
	protected abstract void setView(int position,View v);
	////

	// ScrollView makes a lot of bug, so I will not use it, instead use HeaderView on ListView.
	/*
	// Variable Height (For ListView in ScrollView)
	public void tuneToScrollView() {
		int totalHeight=0;
		int desiredWidth=MeasureSpec.makeMeasureSpec(view.getWidth(),MeasureSpec.AT_MOST);
		// heights of items
		for(int i=0;i<getCount();i++) {
			View item=getView(i,null,view);
			item.measure(desiredWidth,MeasureSpec.UNSPECIFIED);
			totalHeight+=item.getMeasuredHeight();
		}
		// heights of header, footer views (because I can't get view, use template height)
		totalHeight+=(view.getHeaderViewsCount()+view.getFooterViewsCount())*150;
		// apply
		ViewGroup.LayoutParams params=view.getLayoutParams();
		params.height=totalHeight+(view.getDividerHeight()*(getCount()-1));
		view.setLayoutParams(params);
		view.requestLayout();
	}
	////
	*/

	@Override
	public T getItem(int position) {
		if(position>=getCount() || position<0) {
			return null;
		} else {
			return super.getItem(position);
		}
	}
	protected ListView getListView() { return view; }

}
