package com.nsdb.univer.ui.fragment;

import com.makeramen.segmented.SegmentedRadioGroup;
import com.nsdb.univer.R;
import com.nsdb.univer.supporter.adapter.BookDataAdapter;
import com.nsdb.univer.supporter.data.AppPref;
import com.nsdb.univer.supporter.ui.OnClickMover;
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
	
    // actionbar - search
	Button search;	
	EditText searchtxt;

    // actionbar - category
	SegmentedRadioGroup category;
	int categoryState;
	
    // actionbar - register
	Button register;
	private final static int REQUESTCODE_REGISTERBOOK=1;
	
    // range setting
	Button region, univ, college;
	private final static int REQUESTCODE_RANGE=2;

    // ListView
	RefreshableListView lv;
	BookDataAdapter adapter;
	private final static int REQUESTCODE_BOOKDETAIL=3;
	
	public BookMarketMain(Activity activity) {
		super(activity,R.layout.bookmarketmain);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v=super.onCreateView(inflater, container, savedInstanceState);
        
        // actionbar - search
        search=(Button)v.findViewById(R.id.search);
        searchtxt=(EditText)v.findViewById(R.id.searchtxt);

        // actionbar - sale
        category=(SegmentedRadioGroup)v.findViewById(R.id.sale);
        category.setOnCheckedChangeListener(this);
        categoryState=0;
        
        // actionbar - register
        register=(Button)v.findViewById(R.id.register);
        register.setOnClickListener(new OnClickMover(THIS,new Intent("RegisterBook"),REQUESTCODE_REGISTERBOOK));
        
        // range setting
    	region=(Button)v.findViewById(R.id.region);
    	univ=(Button)v.findViewById(R.id.univ);
    	college=(Button)v.findViewById(R.id.college);
    	region.setOnClickListener(new OnClickMover(THIS,new Intent("RangeSetting").putExtra("range","region"),REQUESTCODE_RANGE));
    	univ.setOnClickListener(new OnClickMover(THIS,new Intent("RangeSetting").putExtra("range","univ"),REQUESTCODE_RANGE));
    	college.setOnClickListener(new OnClickMover(THIS,new Intent("RangeSetting").putExtra("range","college"),REQUESTCODE_RANGE));
		AppPref.getRangeSet().applyDataToView(region, univ, college);

        // ListView
    	lv=(RefreshableListView)v.findViewById(R.id.booklist);
    	adapter=new BookDataAdapter(THIS,lv);
    	lv.setOnItemClickListener(this);
    	lv.setOnScrollListener(this);
    	lv.setOnRefreshListener(this);
    	adapter.updateData("",categoryState,true);

    	return v;
	}
	
	// Every Fragment's requestCode are linked! (REQUESTCODE_REGISTERBOARD==REQUESTCODE_REGISTERPROFESSOR...)
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);
		
		switch(requestCode) {
		case REQUESTCODE_RANGE:
			if(resultCode == Activity.RESULT_OK) {
				AppPref.getRangeSet().applyDataToView(region, univ, college);
		    	adapter.updateData("",categoryState,true);
			} break;
			
		case REQUESTCODE_REGISTERBOOK:
			if(resultCode == Activity.RESULT_OK || data.getBooleanExtra("range_changed",false)==true) {
				AppPref.getRangeSet().applyDataToView(region, univ, college);
				adapter.updateData("",categoryState,true);
			} break;
			
		case REQUESTCODE_BOOKDETAIL:
			if(data.getBooleanExtra("edited",false)==true) {
				adapter.updateData("",categoryState,true);
			} break;
		}
	}
	

	public void onItemClick(AdapterView<?> parent, View view, int position, long l_position) {
		if(adapter.getItem(position)!=null) {
			AppPref.setLastBookData(adapter.getItem(position));
			THIS.startActivityForResult(new Intent("BookDetail").putExtra("mine",categoryState==2),REQUESTCODE_BOOKDETAIL);
		}
	}

	public void onCheckedChanged(RadioGroup group, int checkedId) {
		switch(checkedId) {		
		case R.id.sell: categoryState=0; break;
		case R.id.buy: categoryState=1; break;
		case R.id.mine: categoryState=2; break;
		}
    	adapter.updateData("",categoryState,true);
	}

	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
		if(firstVisibleItem+visibleItemCount==totalItemCount) {
	    	adapter.updateData("",categoryState,false);
		}
	}

	public void onScrollStateChanged(AbsListView view, int scrollState) {}

	public void onRefresh(RefreshableListView listView) {
    	adapter.updateData("",categoryState,true);
	}

}
