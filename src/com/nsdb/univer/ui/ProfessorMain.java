package com.nsdb.univer.ui;

import com.nsdb.univer.common.AppPref;
import com.nsdb.univer.common.RangeData;
import com.nsdb.univer.common.ui.ActiveFragment;
import com.nsdb.univer.common.ui.OnClickMover;
import com.nsdb.univer.dataadapter.ProfessorDataAdapter;

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
	private final static int REQUESTCODE_RANGE=2;

	ListView lv;
	ProfessorDataAdapter adapter;
	
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
    	college.setOnClickListener(new OnClickMover(THIS,new Intent("RangeSetting").putExtra("filter","college"),REQUESTCODE_RANGE));
    	major.setOnClickListener(new OnClickMover(THIS,new Intent("RangeSetting").putExtra("filter","major"),REQUESTCODE_RANGE));
    	// Not yet
    	major.setEnabled(false);

        // ListView
    	lv=(ListView)v.findViewById(R.id.professorlist);
    	adapter=new ProfessorDataAdapter(THIS,lv,false);
    	lv.setOnItemClickListener(this);
    	updateView();
    	
    	return v;
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);
		if(resultCode != Activity.RESULT_OK) return;
		
		switch(requestCode) {
		case REQUESTCODE_RANGE: {
			String filter=data.getStringExtra("filter");
			String title=data.getStringExtra("title");
			String nick=data.getStringExtra("nick");
			int id=data.getIntExtra("id",-1);
			AppPref.getRangeSet().set(filter,new RangeData( title,nick,id ));
			}
			updateView();
			break;
			
		case REQUESTCODE_REGISTERPROFESSOR:
			// RangeData is changed by RegisterProfessor activity
			updateView();
			break;
		}
	}
	

	public void onItemClick(AdapterView<?> parent, View view, int position, long l_position) {
		if(adapter.get(position).id != -1) {
			AppPref.setLastProfessorData(adapter.get(position));
			THIS.startActivity( new Intent("ProfessorDetail") );
		}		
	}
	
	public void updateView() {
		
		// rangebutton
		AppPref.getRangeSet().applyDataToView(region, univ, college, major);

		// list
    	adapter.updateData("",1);
	}

}
