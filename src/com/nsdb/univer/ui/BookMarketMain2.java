package com.nsdb.univer.ui;

import java.util.ArrayList;

import com.makeramen.segmented.SegmentedRadioGroup;
import com.nsdb.univer.data.BookData;
import com.nsdb.univer.uisupporter.BookDataAdapter;
import com.nsdb.univer.uisupporter.OnClickMover;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

public class BookMarketMain2 extends Activity implements OnItemClickListener, OnCheckedChangeListener {
	
	Button category,search,register;
	EditText searchtxt;
	
	SegmentedRadioGroup srg;
	int mode;

	ArrayList<BookData> data;
	BookDataAdapter adapter;
	ListView lv;

	@Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bookmarketmain2);
        
        // actionbar
        category=(Button)findViewById(R.id.category);
        search=(Button)findViewById(R.id.search);
        register=(Button)findViewById(R.id.register);
        searchtxt=(EditText)findViewById(R.id.searchtxt);
        category.setOnClickListener(new OnClickMover(this,"RangeSetting",""));
        
        // radio
        srg=(SegmentedRadioGroup)findViewById(R.id.segmentedradio);
        srg.setOnCheckedChangeListener(this);
        mode=BookData.SALEMODE_SELL;

        // ListView
    	data=new ArrayList<BookData>();
    	adapter=new BookDataAdapter(this,data);
    	lv=(ListView)findViewById(R.id.booklist);
    	lv.setAdapter(adapter);
    	lv.setOnItemClickListener(this);
	}

    @Override
    public void onResume() {
    	super.onResume();
    	adapter.updateData("",mode,1);
    }

	public void onItemClick(AdapterView<?> parent, View view, int position, long l_position) {
		
	}

	public void onCheckedChanged(RadioGroup group, int checkedId) {

		int tempMode=-1;

		switch(checkedId) {
		case R.id.sell: tempMode=BookData.SALEMODE_SELL; break;
		case R.id.buy: tempMode=BookData.SALEMODE_BUY; break;
		case R.id.mine: return;
		}
		
		if(tempMode != mode) {
			mode=tempMode;
			adapter.updateData("",mode,1);
		}
	}
}
