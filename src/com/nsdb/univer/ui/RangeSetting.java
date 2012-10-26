package com.nsdb.univer.ui;

import com.nsdb.univer.common.AppPref;
import com.nsdb.univer.common.RangeData;
import com.nsdb.univer.dataadapter.RangeDataAdapter;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

public class RangeSetting extends Activity implements OnItemClickListener, OnClickListener {
	
	String filter;
	TextView title;
	EditText edit;

	ListView list;
	RangeDataAdapter adapter;
	
	Button clear;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rangesetting);
                
        // title
        filter=getIntent().getStringExtra("filter");
        title=(TextView)findViewById(R.id.title);        
        if(filter.compareTo("region")==0)
        	title.setText("지역선택");
        else if(filter.compareTo("univ")==0)
        	title.setText("학교선택");
        else if(filter.compareTo("college")==0)
        	title.setText("단과대선택");
        else if(filter.compareTo("major")==0)
        	title.setText("학과선택");
        else {
        	finish();
        	return;
        }
        
        // edittext
        edit=(EditText)findViewById(R.id.search);
        edit.addTextChangedListener(new TextWatcher()
        {
			public void afterTextChanged(Editable arg0) {}
			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {}
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				adapter.updateView( s.toString() );
			}
        });
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        	
        // listview
        list=(ListView)findViewById(R.id.list);
        adapter=new RangeDataAdapter(this,list,false);
        list.setOnItemClickListener(this);
    	adapter.updateData(filter);
    	
    	// Button
    	clear=(Button)findViewById(R.id.clear);
    	clear.setOnClickListener(this);
    }
    
	public void onItemClick(AdapterView<?> parent, View view, int position, long l_position) {
		
		if(adapter.get(position).id != -1) {
			AppPref.setRangeData(filter,adapter.get(position));
			if(filter.compareTo("region")==0) {
				AppPref.setRangeData("univ",new RangeData());
				AppPref.setRangeData("college",new RangeData());
				AppPref.setRangeData("major",new RangeData());
			} else if(filter.compareTo("univ")==0) {
				AppPref.setRangeData("college",new RangeData());				
				AppPref.setRangeData("major",new RangeData());
			} else if(filter.compareTo("college")==0) {
				AppPref.setRangeData("major",new RangeData());
			}
			setResult(RESULT_OK,getIntent());
			finish();
		}
	}

	public void onClick(View v) {
		
		AppPref.setRangeData(filter,new RangeData());
		if(filter.compareTo("region")==0) {
			AppPref.setRangeData("univ",new RangeData());
			AppPref.setRangeData("college",new RangeData());
			AppPref.setRangeData("major",new RangeData());
		} else if(filter.compareTo("univ")==0) {
			AppPref.setRangeData("college",new RangeData());				
			AppPref.setRangeData("major",new RangeData());
		} else if(filter.compareTo("college")==0) {
			AppPref.setRangeData("major",new RangeData());
		}
		setResult(RESULT_OK,getIntent());
		finish();
	}
}
