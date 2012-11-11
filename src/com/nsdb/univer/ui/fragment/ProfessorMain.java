package com.nsdb.univer.ui.fragment;

import com.nsdb.univer.R;
import com.nsdb.univer.supporter.adapter.ProfessorDataAdapter;
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
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;

public class ProfessorMain extends ActiveFragment implements OnItemClickListener, OnScrollListener, OnRefreshListener {
	
    // actionbar - search
	Button search;	
	EditText searchtxt;
	
    // actionbar - register
	Button register;
	private final static int REQUESTCODE_REGISTERPROFESSOR=1;
	
    // range setting
	Button region, univ, college, major;
	private final static int REQUESTCODE_RANGE=2;

    // ListView
	RefreshableListView lv;
	ProfessorDataAdapter adapter;
	
	public ProfessorMain(Activity activity) {
		super(activity,R.layout.professormain);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v=super.onCreateView(inflater, container, savedInstanceState);
        
        // actionbar - search
        search=(Button)v.findViewById(R.id.search);
        searchtxt=(EditText)v.findViewById(R.id.searchtxt);
        
        // actionbar - register
        register=(Button)v.findViewById(R.id.register);
        register.setOnClickListener(new OnClickMover(THIS,new Intent("RegisterProfessor"),REQUESTCODE_REGISTERPROFESSOR));
        
        // range setting
    	region=(Button)v.findViewById(R.id.region);
    	univ=(Button)v.findViewById(R.id.univ);
    	college=(Button)v.findViewById(R.id.college);
    	major=(Button)v.findViewById(R.id.major);
    	region.setOnClickListener(new OnClickMover(THIS,new Intent("RangeSetting").putExtra("range","region"),REQUESTCODE_RANGE));
    	univ.setOnClickListener(new OnClickMover(THIS,new Intent("RangeSetting").putExtra("range","univ"),REQUESTCODE_RANGE));
    	college.setOnClickListener(new OnClickMover(THIS,new Intent("RangeSetting").putExtra("range","college"),REQUESTCODE_RANGE));
    	major.setOnClickListener(new OnClickMover(THIS,new Intent("RangeSetting").putExtra("range","major"),REQUESTCODE_RANGE));
		AppPref.getRangeSet().applyDataToView(region, univ, college, major);

        // ListView
    	lv=(RefreshableListView)v.findViewById(R.id.professorlist);
    	adapter=new ProfessorDataAdapter(THIS,lv);
    	lv.setOnItemClickListener(this);
    	lv.setOnScrollListener(this);
    	lv.setOnRefreshListener(this);
    	adapter.updateData("",true);
    	
    	return v;
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);
		if(resultCode != Activity.RESULT_OK) return;
		
		switch(requestCode) {
		case REQUESTCODE_RANGE:
			if(resultCode == Activity.RESULT_OK) {
				AppPref.getRangeSet().applyDataToView(region, univ, college, major);
		    	adapter.updateData("",true);
			} break;
			
		case REQUESTCODE_REGISTERPROFESSOR:
			if(resultCode == Activity.RESULT_OK || data.getBooleanExtra("range_changed",false)==true) {
				AppPref.getRangeSet().applyDataToView(region, univ, college, major);
				adapter.updateData("",true);
			} break;
		}
	}
	

	public void onItemClick(AdapterView<?> parent, View view, int position, long l_position) {
		if(adapter.getItem(position)!=null) {
			AppPref.setLastProfessorData(adapter.getItem(position));
			THIS.startActivity( new Intent("ProfessorDetail") );
		}		
	}
	
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
		if(firstVisibleItem+visibleItemCount==totalItemCount) {
			adapter.updateData("",false);
		}
	}

	public void onScrollStateChanged(AbsListView view, int scrollState) {}

	public void onRefresh(RefreshableListView listView) {
    	adapter.updateData("",true);
	}
}
