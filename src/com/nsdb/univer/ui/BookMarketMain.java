package com.nsdb.univer.ui;

import java.util.ArrayList;

import com.nsdb.univer.data.AppPref;
import com.nsdb.univer.data.BookData;
import com.nsdb.univer.uisupporter.BookDataAdapter;
import com.nsdb.univer.uisupporter.OnClickMover;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class BookMarketMain extends Activity implements OnItemClickListener {
	
	Button region,univ,college;
	//Button search;
	//Button sell,buy,mine;
	
	ArrayList<BookData> data;
	BookDataAdapter adapter;
	ListView lv;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bookmarketmain);
    	
        // RangeData
        AppPref.init();
        
        // Button
    	region=(Button)findViewById(R.id.region);
    	univ=(Button)findViewById(R.id.univ);
    	college=(Button)findViewById(R.id.college);
    	//search=(Button)findViewById(R.id.search);
    	//sell=(Button)findViewById(R.id.sell);
    	//buy=(Button)findViewById(R.id.buy);
    	//mine=(Button)findViewById(R.id.mine);
    	region.setOnClickListener(new OnClickMover(this,"RangeSetting","region"));
    	univ.setOnClickListener(new OnClickMover(this,"RangeSetting","univ"));
    	college.setOnClickListener(new OnClickMover(this,"RangeSetting","college"));
    	
    	// ListView
    	data=new ArrayList<BookData>();
    	adapter=new BookDataAdapter(this,data);
    	lv=(ListView)findViewById(R.id.booklist);
    	lv.setAdapter(adapter);
    }
    
    @Override
    public void onResume() {
    	super.onResume();
    	
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
    	if(temp.compareTo("")==0) temp="단과대";
    	college.setText( temp );
    	
    	// book list
    	adapter.updateData("",BookData.SALEMODE_SELL,1);
    }

	public void onItemClick(AdapterView<?> parent, View view, int position, long l_position) {
	}
}
