package com.nsdb.univer.dataadapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.MeasureSpec;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public abstract class BaseArrayAdapter<T> extends ArrayAdapter<T> {
	
	private int dataResourceId;
	private ListView view;
	private boolean variableHeight;

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
		
		position-=view.getHeaderViewsCount();		
		if(position>=0 && position<getCount()) {
			setView(position,v);
		}
		
		return v;	
	}
	protected abstract void setView(int position,View v);
	////

	// Variable Height (For ListView in ScrollView)
	public void setVariableHeight(boolean value) { variableHeight=value; }
	@Override
	public void notifyDataSetChanged() {
		super.notifyDataSetChanged();
		if(variableHeight) tuneToScrollView();
	}
	private void tuneToScrollView() {
		int totalHeight=0;
		int desiredWidth=MeasureSpec.makeMeasureSpec(view.getWidth(),MeasureSpec.AT_MOST);
		for(int i=0;i<getCount()+view.getHeaderViewsCount()+view.getFooterViewsCount();i++) {
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
	////
	
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
