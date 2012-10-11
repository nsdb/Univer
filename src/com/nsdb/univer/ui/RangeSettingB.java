package com.nsdb.univer.ui;

import java.util.ArrayList;

import com.makeramen.segmented.SegmentedRadioGroup;
import com.nsdb.univer.data.AppPref;
import com.nsdb.univer.data.RangeData;
import com.nsdb.univer.uisupporter.RangeDataAdapter;

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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

public class RangeSettingB extends Activity implements OnCheckedChangeListener, OnItemClickListener, OnClickListener {

	SegmentedRadioGroup srg;
	RadioButton region,univ,college;
	String category;
	RangeData regionData,univData,collegeData;

	EditText edit;

	ArrayList<RangeData> data;
	RangeDataAdapter adapter;
	ListView list;
	
	Button apply;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rangesetting_b);
        
        // radiogroup
        srg=(SegmentedRadioGroup)findViewById(R.id.segmentedradio);
        srg.setOnCheckedChangeListener(this);
        region=(RadioButton)findViewById(R.id.region);
        univ=(RadioButton)findViewById(R.id.univ);
        college=(RadioButton)findViewById(R.id.college);
        category="region";
        regionData=new RangeData( AppPref.getRangeData("region") );
        univData=new RangeData( AppPref.getRangeData("univ") );
        collegeData=new RangeData( AppPref.getRangeData("college") );

        // edittext
        edit=(EditText)findViewById(R.id.search);
        edit.addTextChangedListener(new TextWatcher() {
			public void afterTextChanged(Editable arg0) {}
			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {}
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				adapter.updateView( s.toString() );
			}
        });
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        
        // listview
        data=new ArrayList<RangeData>();
        adapter=new RangeDataAdapter(this,data);
        list=(ListView)findViewById(R.id.list);
        list.setAdapter(adapter);
        list.setOnItemClickListener(this);
        
        // button
        apply=(Button)findViewById(R.id.apply);
        apply.setOnClickListener(this);
    }

    @Override
    public void onResume() {
    	super.onResume();
    	adapter.updateData(category);    	
		radioViewChange();
    }

	public void onItemClick(AdapterView<?> parent, View view, int position, long l_position) {
		
		// data change
		if(data.get(position).id != -1) {

			AppPref.setRangeData(category,data.get(position));

			// sub category initializing
			if(category.compareTo("region")==0) {
				univData=new RangeData();
				collegeData=new RangeData();
			} else if(category.compareTo("univ")==0) {
				collegeData=new RangeData();
			}
			
		}
		
		// view change
		radioViewChange();
			
	}        	

	public void onCheckedChanged(RadioGroup group, int checkedId) {
		
		String tempCategory="";

		switch(checkedId) {
		case R.id.region: tempCategory="region"; break;
		case R.id.univ: tempCategory="univ"; break;
		case R.id.college: tempCategory="college"; break;
		}
		
		if(category.compareTo(tempCategory)!=0) {
			category=tempCategory;
			adapter.updateData(category);
		}

	}
	
	public void radioViewChange() {
		
    	// view change - region
    	String temp=regionData.nick;
    	if(temp.compareTo("")==0) {
    		temp="지역";
    		univ.setEnabled(false);
    	} else {
    		univ.setEnabled(true);
    	}
    	region.setText( temp );

    	// view change - univ
    	temp=univData.nick;
    	if(temp.compareTo("")==0) { 
    		temp="대학교";
    		college.setEnabled(false);
    	} else {
    		college.setEnabled(true);
    	}
    	univ.setText( temp );
    	
    	// view change - college
    	temp=collegeData.nick;
    	if(temp.compareTo("")==0) temp="단과대";
    	college.setText( temp );
		
	}

	// for apply button
	public void onClick(View v) {
		AppPref.setRangeData("region",regionData);
		AppPref.setRangeData("univ",univData);
		AppPref.setRangeData("college",collegeData);
		finish();
	}
}
