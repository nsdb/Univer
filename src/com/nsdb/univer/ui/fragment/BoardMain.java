package com.nsdb.univer.ui.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.Button;

import com.nsdb.univer.R;
import com.nsdb.univer.supporter.adapter.BoardDataAdapter;
import com.nsdb.univer.supporter.data.AppPref;
import com.nsdb.univer.supporter.ui.OnClickMover;
import com.woozzu.android.widget.RefreshableListView;
import com.woozzu.android.widget.RefreshableListView.OnRefreshListener;

public class BoardMain extends ActiveFragment implements OnScrollListener, OnRefreshListener {
	
	// actionbar
	Button register;
	private final static int REQUESTCODE_REGISTERBOARD=1;
	// range
	Button region,univ;
	private final static int REQUESTCODE_RANGE=2;
	// listview
	RefreshableListView lv;
	BoardDataAdapter adapter;

	public BoardMain(Activity activity) {
		super(activity, R.layout.boardmain);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v=super.onCreateView(inflater, container, savedInstanceState);
		
		// actionbar
		register=(Button)v.findViewById(R.id.register);
        register.setOnClickListener(new OnClickMover(THIS,new Intent("RegisterBoard"),REQUESTCODE_REGISTERBOARD));
        // range
    	region=(Button)v.findViewById(R.id.region);
    	univ=(Button)v.findViewById(R.id.univ);
    	region.setOnClickListener(new OnClickMover(THIS,new Intent("RangeSetting").putExtra("range","region"),REQUESTCODE_RANGE));
    	univ.setOnClickListener(new OnClickMover(THIS,new Intent("RangeSetting").putExtra("range","univ"),REQUESTCODE_RANGE));
		AppPref.getRangeSet().applyDataToView(region, univ);
		// listview
		lv=(RefreshableListView)v.findViewById(R.id.boardlist);
		adapter=new BoardDataAdapter(THIS,lv);
		adapter.updateData(true);
    	lv.setOnScrollListener(this);
    	lv.setOnRefreshListener(this);
		
		return v;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);
		
		switch(requestCode) {
		case REQUESTCODE_RANGE:
			if(resultCode == Activity.RESULT_OK) {
				AppPref.getRangeSet().applyDataToView(region, univ);
		    	adapter.updateData(true);
			} break;
			
		case REQUESTCODE_REGISTERBOARD:
			if(resultCode == Activity.RESULT_OK) {
				adapter.updateData(true);
			} break;
		}
	}

	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
		if(firstVisibleItem+visibleItemCount==totalItemCount) {
	    	adapter.updateData(false);
		}
	}

	public void onScrollStateChanged(AbsListView view, int scrollState) {}

	public void onRefresh(RefreshableListView listView) {
    	adapter.updateData(true);
	}

}
