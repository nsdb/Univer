package com.nsdb.univer.ui.activity;

import com.nsdb.univer.R;
import com.nsdb.univer.supporter.adapter.RangeDataAdapter;
import com.nsdb.univer.supporter.data.AppPref;
import com.woozzu.android.widget.IndexableListView;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.TextView;

public class RangeSetting extends Activity implements OnItemClickListener {
	
	String range;
	TextView actionbartitle;
	EditText edit;

	IndexableListView list;
	RangeDataAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rangesetting);
                
        // title
        range=getIntent().getStringExtra("range");
        actionbartitle=(TextView)findViewById(R.id.actionbartitle);        
        if(range.compareTo("region")==0)
        	actionbartitle.setText("지역선택");
        else if(range.compareTo("univ")==0)
        	actionbartitle.setText("학교선택");
        else if(range.compareTo("college")==0)
        	actionbartitle.setText("단과대선택");
        else if(range.compareTo("major")==0)
        	actionbartitle.setText("학과선택");
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
				adapter.filter( s.toString() );
			}
        });
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        	
        // listview
        list=(IndexableListView)findViewById(R.id.list);
        adapter=new RangeDataAdapter(this,list);
        list.setOnItemClickListener(this);
        adapter.updateData(range);

    	
    }
    
	public void onItemClick(AdapterView<?> parent, View view, int position, long l_position) {
		
		if(adapter.getItem(position) != null) {
			AppPref.getRangeSet().set(range,adapter.getItem(position));
			setResult(RESULT_OK,getIntent());
			finish();
		}
	}
}
