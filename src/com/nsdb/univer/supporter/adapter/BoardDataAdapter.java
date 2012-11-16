package com.nsdb.univer.supporter.adapter;

import java.util.ArrayList;

import org.jdom2.Element;

import com.nsdb.univer.R;
import com.nsdb.univer.data.BoardData;
import com.nsdb.univer.data.BookData;
import com.nsdb.univer.supporter.data.AppPref;
import com.woozzu.android.widget.RefreshableListView;

import android.content.Context;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

public class BoardDataAdapter extends DataLoadingArrayAdapter<BoardData> {

	private int pageNum;
	
	public BoardDataAdapter(Context context, ListView view) {
		super(context, R.layout.boarddata, view);
		pageNum=0;
	}

	@Override
	protected void init() {
		super.init();
		pageNum=0;
	}
	public void updateData(boolean init) {
		if(init) init();
		if(isLoadable()==false) return;
		// {base_url}/feeds/entries/category=<category>&id=<id>&page=<page>/
		String url=getContext().getResources().getString(R.string.base_url)+'/'
				+getContext().getResources().getString(R.string.get_url)+'/'
				+getContext().getResources().getString(R.string.board_url)+'/';
		// combine start
		ArrayList<String> getData=new ArrayList<String>();
		// category, id
		if(AppPref.getRangeSet().get("region").title.compareTo("")==0) {
			getData.add("category="+BookData.RANGEMODE_ALL);
			getData.add("id="+0);
		} else if(AppPref.getRangeSet().get("univ").title.compareTo("")==0) {
			getData.add("category="+BookData.RANGEMODE_REGION);
			getData.add("id="+AppPref.getRangeSet().get("region").id);
		} else {
			getData.add("category="+BookData.RANGEMODE_UNIV);
			getData.add("id="+AppPref.getRangeSet().get("univ").id);
		}
		// page
		getData.add("page="+(++pageNum));
		// combine end
		for(int i=0;i<getData.size();i++) {
			url=url+getData.get(i);
			if(i != getData.size()-1) url=url+'&';
		}
		url=url+'/';		
		System.out.println("XML URL : "+url);
		
		super.updateData(url);
	}
	@Override
	protected BoardData convertElementToObject(Element item) {
		return new BoardData(item);
	}
	@Override
	protected void applyUpdate() {
		super.applyUpdate();
		try {
			RefreshableListView rlv=(RefreshableListView)getListView();
			if(rlv.isRefreshing())
				rlv.completeRefreshing();
		} catch(ClassCastException e) {			
		}
	}

	@Override
	protected void setView(int position, View v) {
		
		TextView t=(TextView)v.findViewById(R.id.title);
		TextView d=(TextView)v.findViewById(R.id.description);

		t.setText(getItem(position).title);
		d.setText(getItem(position).description);
		
	}
	
	// this method makes ListView Item non-selectable
	@Override
	public boolean isEnabled(int position) { return false; }

}
