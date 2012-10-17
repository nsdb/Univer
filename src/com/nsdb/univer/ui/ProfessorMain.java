package com.nsdb.univer.ui;

import java.util.ArrayList;

import com.nsdb.univer.data.AppPref;
import com.nsdb.univer.data.ProfessorData;
import com.nsdb.univer.uisupporter.ActiveFragment;
import com.nsdb.univer.uisupporter.BookDataAdapter;
import com.nsdb.univer.uisupporter.OnClickMover;
import com.nsdb.univer.uisupporter.ProfessorDataAdapter;

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

public class ProfessorMain extends ActiveFragment implements OnItemClickListener {
	
	Button search;	
	EditText searchtxt;
	
	Button register;
	private final static int REQUESTCODE_REGISTERPROFESSOR=1;
	
	Button region, univ, college, major;
	int rangeMode;
	private final static int REQUESTCODE_RANGE=2;

	ArrayList<ProfessorData> data;
	ProfessorDataAdapter adapter;
	ListView lv;
	
	ProfessorMain(Activity activity) {
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
    	region.setOnClickListener(new OnClickMover(THIS,new Intent("RangeSetting").putExtra("filter","region"),REQUESTCODE_RANGE));
    	univ.setOnClickListener(new OnClickMover(THIS,new Intent("RangeSetting").putExtra("filter","univ"),REQUESTCODE_RANGE));
    	college.setOnClickListener(new OnClickMover(THIS,new Intent("RangeSetting").putExtra("filter","univ"),REQUESTCODE_RANGE));
    	major.setOnClickListener(new OnClickMover(THIS,new Intent("RangeSetting").putExtra("filter","major"),REQUESTCODE_RANGE));
        rangeMode=ProfessorDataAdapter.getDefaultRangeMode();
    	// Not yet
    	major.setEnabled(false);

        // ListView
    	lv=(ListView)v.findViewById(R.id.professorlist);
    	data=new ArrayList<ProfessorData>();
    	adapter=new ProfessorDataAdapter(THIS,data,lv);
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
			AppPref.setLastProfessorData(data.get(position));
			THIS.startActivity( new Intent("ProfessorDetail") );
		}		
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

    	// rangemode
		rangeMode=BookDataAdapter.getDefaultRangeMode();

		// list
    	adapter.updateData("",rangeMode,1);
	}

}
