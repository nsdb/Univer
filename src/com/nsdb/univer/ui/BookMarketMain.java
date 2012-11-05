package com.nsdb.univer.ui;

import com.makeramen.segmented.SegmentedRadioGroup;
import com.nsdb.univer.R;
import com.nsdb.univer.common.AppPref;
import com.nsdb.univer.common.BookData;
import com.nsdb.univer.common.RangeData;
import com.nsdb.univer.common.ui.ActiveFragment;
import com.nsdb.univer.common.ui.OnClickMover;
import com.nsdb.univer.dataadapter.BookDataLoader;
import com.woozzu.android.widget.RefreshableListView;
import com.woozzu.android.widget.RefreshableListView.OnRefreshListener;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

public class BookMarketMain extends ActiveFragment implements OnItemClickListener, OnCheckedChangeListener, OnScrollListener, OnRefreshListener {
	
	Button search;	
	EditText searchtxt;

	SegmentedRadioGroup sale;
	int saleMode;
	
	Button register;
	private final static int REQUESTCODE_REGISTERBOOK=1;
	
	Button region, univ, college, major;
	int rangeMode;
	private final static int REQUESTCODE_RANGE=2;

	RefreshableListView lv;
	BookDataLoader loader;
	int pageNum;
	
	BookMarketMain(Activity activity) {
		super(activity,R.layout.bookmarketmain);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v=super.onCreateView(inflater, container, savedInstanceState);
        
        // actionbar - search
        search=(Button)v.findViewById(R.id.search);
        searchtxt=(EditText)v.findViewById(R.id.searchtxt);

        // actionbar - sale
        sale=(SegmentedRadioGroup)v.findViewById(R.id.sale);
        sale.setOnCheckedChangeListener(this);
        saleMode=BookData.SALEMODE_SELL;
        
        // actionbar - register
        register=(Button)v.findViewById(R.id.register);
        register.setOnClickListener(new OnClickMover(THIS,new Intent("RegisterBook"),REQUESTCODE_REGISTERBOOK));
        
        // range setting
    	region=(Button)v.findViewById(R.id.region);
    	univ=(Button)v.findViewById(R.id.univ);
    	college=(Button)v.findViewById(R.id.college);
    	major=(Button)v.findViewById(R.id.major);
    	region.setOnClickListener(new OnClickMover(THIS,new Intent("RangeSetting").putExtra("filter","region"),REQUESTCODE_RANGE));
    	univ.setOnClickListener(new OnClickMover(THIS,new Intent("RangeSetting").putExtra("filter","univ"),REQUESTCODE_RANGE));
    	college.setOnClickListener(new OnClickMover(THIS,new Intent("RangeSetting").putExtra("filter","college"),REQUESTCODE_RANGE));
    	major.setOnClickListener(new OnClickMover(THIS,new Intent("RangeSetting").putExtra("filter","major"),REQUESTCODE_RANGE));
        rangeMode=BookDataLoader.getDefaultRangeMode();
    	// Not yet
    	major.setEnabled(false);

        // ListView
    	lv=(RefreshableListView)v.findViewById(R.id.booklist);
    	loader=new BookDataLoader(THIS,lv,false);
    	lv.setOnItemClickListener(this);
    	lv.setOnScrollListener(this);
    	lv.setOnRefreshListener(this);
    	pageNum=1;
    	updateView();
    	
    	return v;
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);
		if(resultCode != Activity.RESULT_OK) return;
		
		switch(requestCode) {
		case REQUESTCODE_RANGE: {
			String filter=data.getStringExtra("filter");
			String title=data.getStringExtra("title");
			String nick=data.getStringExtra("nick");
			int id=data.getIntExtra("id",-1);
			AppPref.getRangeSet().set(filter,new RangeData( title,nick,id ));
			}
			pageNum=1;
			updateView();
			break;
			
		case REQUESTCODE_REGISTERBOOK:
			// RangeData is changed by RegisterBook activity
			pageNum=1;
			updateView();
			break;
		}
	}
	

	public void onItemClick(AdapterView<?> parent, View view, int position, long l_position) {
		if(loader.get(position)!=null) {
			AppPref.setLastBookData(loader.get(position));
			THIS.startActivity(new Intent("BookDetail"));
		}
	}

	public void onCheckedChanged(RadioGroup group, int checkedId) {
		switch(checkedId) {
		case R.id.sell:
			saleMode=BookData.SALEMODE_SELL;
			rangeMode=BookDataLoader.getDefaultRangeMode();
			break;
		case R.id.buy:
			saleMode=BookData.SALEMODE_BUY;
			rangeMode=BookDataLoader.getDefaultRangeMode();
			break;
		case R.id.mine:
			saleMode=BookData.SALEMODE_ALL;
			rangeMode=BookDataLoader.RANGEMODE_MINE;
			break;
		}
		updateView();
	}

	public void updateView() {
				
		// rangebutton
		AppPref.getRangeSet().applyDataToView(region, univ, college, major);

    	// rangemode
		if(rangeMode != BookDataLoader.RANGEMODE_MINE)
			rangeMode=BookDataLoader.getDefaultRangeMode();
		
    	// list
    	loader.updateData("",rangeMode,saleMode,pageNum);
	}

	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
		if(firstVisibleItem+visibleItemCount==totalItemCount && loader.isLoadable()) {
			pageNum++;
			updateView();
		}
	}

	public void onScrollStateChanged(AbsListView view, int scrollState) {}

	public void onRefresh(RefreshableListView listView) {
		pageNum=1;
		updateView();
		
	}

}
