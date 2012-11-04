package com.nsdb.univer.ui;

import com.nsdb.univer.dataadapter.RangeDataLoader;
import com.woozzu.android.widget.IndexableListView;

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
import android.widget.TextView;

public class RangeSetting extends Activity implements OnItemClickListener, OnClickListener {
	
	String filter;
	TextView title;
	EditText edit;

	IndexableListView list;
	RangeDataLoader loader;
	
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
				loader.updateView( s.toString() );
			}
        });
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        	
        // listview
        list=(IndexableListView)findViewById(R.id.list);
        loader=new RangeDataLoader(this,list,false);
        list.setOnItemClickListener(this);
    	loader.updateData(filter);

    	
    	// Button
    	clear=(Button)findViewById(R.id.clear);
    	clear.setOnClickListener(this);
    }
    
	public void onItemClick(AdapterView<?> parent, View view, int position, long l_position) {
		
		if(loader.get(position) != null) {
			getIntent().putExtra("filter",filter);
			getIntent().putExtra("title",loader.get(position).title);
			getIntent().putExtra("nick",loader.get(position).nick);
			getIntent().putExtra("id",loader.get(position).id);
			setResult(RESULT_OK,getIntent());
			finish();
		}
	}

	public void onClick(View v) {
		
		getIntent().putExtra("filter",filter);
		getIntent().putExtra("title","");
		getIntent().putExtra("nick","");
		getIntent().putExtra("id",-1);
		setResult(RESULT_OK,getIntent());
		finish();
	}
}
