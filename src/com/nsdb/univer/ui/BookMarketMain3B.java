package com.nsdb.univer.ui;

import java.util.ArrayList;

import com.nsdb.univer.data.AppPref;
import com.nsdb.univer.data.BookData;
import com.nsdb.univer.uisupporter.ActiveFragment;
import com.nsdb.univer.uisupporter.BookDataAdapter;
import com.nsdb.univer.uisupporter.OnClickMover;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

public class BookMarketMain3B extends ActiveFragment implements OnItemClickListener, OnClickListener {
	
	Button category;
	int saleMode;
	
	Button search;	
	EditText searchtxt;
	
	Button register;
	private final static int REQUESTCODE_REGISTERBOOK=1;
	
	Button region, univ, college, major;
	int rangeMode;
	private final static int REQUESTCODE_RANGE=2;

	ArrayList<BookData> data;
	BookDataAdapter adapter;
	ListView lv;
	
	BookMarketMain3B(Activity activity) {
		super(activity,R.layout.bookmarketmain3);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v=super.onCreateView(inflater, container, savedInstanceState);
        
        // actionbar - category
        category=(Button)v.findViewById(R.id.category);
        saleMode=BookData.SALEMODE_SELL;
        category.setOnClickListener(this);
        
        // actionbar - search
        search=(Button)v.findViewById(R.id.search);
        searchtxt=(EditText)v.findViewById(R.id.searchtxt);
        
        // actionbar - register
        register=(Button)v.findViewById(R.id.register);
        register.setOnClickListener(new OnClickMover(THIS,"RegisterBook","",REQUESTCODE_REGISTERBOOK));
        
        // range setting
    	region=(Button)v.findViewById(R.id.region);
    	univ=(Button)v.findViewById(R.id.univ);
    	college=(Button)v.findViewById(R.id.college);
    	major=(Button)v.findViewById(R.id.major);
    	region.setOnClickListener(new OnClickMover(THIS,"RangeSetting","region",REQUESTCODE_RANGE));
    	univ.setOnClickListener(new OnClickMover(THIS,"RangeSetting","univ",REQUESTCODE_RANGE));
    	college.setOnClickListener(new OnClickMover(THIS,"RangeSetting","college",REQUESTCODE_RANGE));
    	major.setOnClickListener(new OnClickMover(THIS,"RangeSetting","major",REQUESTCODE_RANGE));
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
			OnClickMover.moveActivity(THIS,"BookDetail","");
		}
	}

	// for category button
	public void onClick(View v) {

		final String items[] = { "팝니다", "삽니다", "내상점" };
		AlertDialog.Builder ab = new AlertDialog.Builder(THIS);
		ab.setTitle("Category");
		ab.setItems(items,
			new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {

					switch(whichButton) {
					case 0:
						saleMode=BookData.SALEMODE_SELL;
						rangeMode=BookDataAdapter.getDefaultRangeMode();
						break;
					case 1:
						saleMode=BookData.SALEMODE_BUY;
						rangeMode=BookDataAdapter.getDefaultRangeMode();
						break;
					case 2:
						saleMode=BookData.SALEMODE_ALL;
						rangeMode=BookDataAdapter.RANGEMODE_MINE;
						break;
					}
					updateView();
					dialog.cancel();

				}
			});
		ab.show();

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

}
