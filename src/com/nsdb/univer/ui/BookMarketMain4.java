package com.nsdb.univer.ui;

import java.util.ArrayList;

import com.makeramen.segmented.SegmentedRadioGroup;
import com.nsdb.univer.data.AppPref;
import com.nsdb.univer.data.BookData;
import com.nsdb.univer.uisupporter.ActiveFragment;
import com.nsdb.univer.uisupporter.BookDataAdapter;
import com.nsdb.univer.uisupporter.OnClickMover;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

public class BookMarketMain4 extends ActiveFragment implements OnItemClickListener, OnCheckedChangeListener {
	
	Button search;	
	EditText searchtxt;

	SegmentedRadioGroup sale;
	int saleMode;
	
	Button register;
	private final static int REQUESTCODE_REGISTERBOOK=1;
	
	Button region, univ, college, major;
	int rangeMode;
	private final static int REQUESTCODE_RANGE=2;

	ArrayList<BookData> data;
	BookDataAdapter adapter;
	ListView lv;
	
	BookMarketMain4(Activity activity) {
		super(activity,R.layout.bookmarketmain4);
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
        rangeMode=BookDataAdapter.getDefaultRangeMode();
    	// Not yet
    	major.setEnabled(false);

        // ListView
    	lv=(ListView)v.findViewById(R.id.booklist);
    	data=new ArrayList<BookData>();
    	adapter=new BookDataAdapter(THIS,data,lv);
    	lv.setAdapter(adapter);
    	lv.setOnItemClickListener(this);
    	updateView();
    	
    	return v;
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);
		
		if(resultCode==Activity.RESULT_OK) {
			updateView();
		}
	}
	

	public void onItemClick(AdapterView<?> parent, View view, int position, long l_position) {
		if(data.get(position).id != -1) {
			AppPref.setLastBookData(data.get(position));
			THIS.startActivity(new Intent("BookDetail"));
		}
	}

	public void onCheckedChanged(RadioGroup group, int checkedId) {
		switch(checkedId) {
		case R.id.sell:
			saleMode=BookData.SALEMODE_SELL;
			rangeMode=BookDataAdapter.getDefaultRangeMode();
			break;
		case R.id.buy:
			saleMode=BookData.SALEMODE_BUY;
			rangeMode=BookDataAdapter.getDefaultRangeMode();
			break;
		case R.id.mine:
			saleMode=BookData.SALEMODE_ALL;
			rangeMode=BookDataAdapter.RANGEMODE_MINE;
			break;
		}
		updateView();
	}

	public void updateView() {
		
		System.out.println("Range Button Update");
		System.out.println("Region : "+AppPref.getRangeData("region").nick);
		System.out.println("Univ : "+AppPref.getRangeData("univ").nick);
		System.out.println("College : "+AppPref.getRangeData("college").nick);
		System.out.println("Major : "+AppPref.getRangeData("major").nick);
		
    	// region
    	String temp=AppPref.getRangeData("region").nick;
    	if(temp.compareTo("")==0) {
    		temp="지역";
    		univ.setEnabled(false);
    	} else {
    		univ.setEnabled(true);
    	}
    	region.setText( temp );

    	// univ
    	temp=AppPref.getRangeData("univ").nick;
    	if(temp.compareTo("")==0) { 
    		temp="대학교";
    		college.setEnabled(false);
    	} else {
    		college.setEnabled(true);
    	}
    	univ.setText( temp );
    	
    	// college
    	temp=AppPref.getRangeData("college").nick;
    	if(temp.compareTo("")==0) { 
    		temp="단과대";
    		major.setEnabled(false);
    	} else {
    		major.setEnabled(true);
    	}
    	college.setText( temp );

    	// major
    	temp=AppPref.getRangeData("major").nick;
    	if(temp.compareTo("")==0) temp="학과";
    	major.setText( temp );

    	// rangemode
		if(rangeMode != BookDataAdapter.RANGEMODE_MINE)
			rangeMode=BookDataAdapter.getDefaultRangeMode();
		
    	// list
    	adapter.updateData("",rangeMode,saleMode,1);
	}

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("region",AppPref.getRangeData("region").id);
        outState.putInt("univ",AppPref.getRangeData("univ").id);
        outState.putInt("college",AppPref.getRangeData("college").id);
        outState.putInt("major",AppPref.getRangeData("major").id);
    }
}
