package com.nsdb.univer.ui;

import java.util.ArrayList;

import com.nsdb.univer.data.AppPref;
import com.nsdb.univer.data.BookData;
import com.nsdb.univer.uisupporter.BookDataAdapter;
import com.nsdb.univer.uisupporter.OnClickMover;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

public class BookMarketMain3A extends Activity implements OnItemClickListener, OnClickListener {
	
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

	@Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bookmarketmain3);
        
        // actionbar - category
        category=(Button)findViewById(R.id.category);
        saleMode=BookData.SALEMODE_SELL;
        category.setOnClickListener(this);
        
        // actionbar - search
        search=(Button)findViewById(R.id.search);
        searchtxt=(EditText)findViewById(R.id.searchtxt);
        
        // actionbar - register
        register=(Button)findViewById(R.id.register);
        register.setOnClickListener(new OnClickMover(this,"RegisterBook",""));
        
        // range setting
    	region=(Button)findViewById(R.id.region);
    	univ=(Button)findViewById(R.id.univ);
    	college=(Button)findViewById(R.id.college);
    	major=(Button)findViewById(R.id.major);
    	region.setOnClickListener(new OnClickMover(this,"RangeSetting","region",REQUESTCODE_RANGE));
    	univ.setOnClickListener(new OnClickMover(this,"RangeSetting","univ",REQUESTCODE_RANGE));
    	college.setOnClickListener(new OnClickMover(this,"RangeSetting","college",REQUESTCODE_RANGE));
    	major.setOnClickListener(new OnClickMover(this,"RangeSetting","major",REQUESTCODE_RANGE));
        rangeMode=BookDataAdapter.getDefaultRangeMode();
    	// Not yet
    	major.setEnabled(false);

        // ListView
    	data=new ArrayList<BookData>();
    	adapter=new BookDataAdapter(this,data);
    	lv=(ListView)findViewById(R.id.booklist);
    	lv.setAdapter(adapter);
    	lv.setOnItemClickListener(this);
    	updateView();
	}
	
	@Override protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);
		
		if(resultCode==RESULT_OK) {
			switch(requestCode) {

			case REQUESTCODE_REGISTERBOOK:
				if(rangeMode != BookDataAdapter.RANGEMODE_MINE)
					rangeMode=BookDataAdapter.getDefaultRangeMode();
				updateView();
				break;
				
			case REQUESTCODE_RANGE:
				if(rangeMode != BookDataAdapter.RANGEMODE_MINE) {
					rangeMode=BookDataAdapter.getDefaultRangeMode();
					updateView();
				} break;

			}
		}
	}
	

	public void onItemClick(AdapterView<?> parent, View view, int position, long l_position) {
		
	}

	// for category button
	public void onClick(View v) {

		final String items[] = { "팝니다", "삽니다", "내상점" };
		AlertDialog.Builder ab = new AlertDialog.Builder(this);
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

    	// list
    	adapter.updateData("",rangeMode,saleMode,1);
	}

}
