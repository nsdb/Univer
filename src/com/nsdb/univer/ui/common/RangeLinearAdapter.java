package com.nsdb.univer.ui.common;

import android.app.Activity;
import android.content.Intent;
import android.widget.Button;
import android.widget.TextView;

import com.nsdb.univer.ui.R;

public class RangeLinearAdapter {
	
	TextView region,univ,college,major;
	Button regionbtn,univbtn,collegebtn,majorbtn;
	
	public RangeLinearAdapter(Activity activity) {
		
        region=(TextView)activity.findViewById(R.id.region);
        univ=(TextView)activity.findViewById(R.id.univ);
        college=(TextView)activity.findViewById(R.id.college);
        major=(TextView)activity.findViewById(R.id.major);
        regionbtn=(Button)activity.findViewById(R.id.regionbtn);
        univbtn=(Button)activity.findViewById(R.id.univbtn);
        collegebtn=(Button)activity.findViewById(R.id.collegebtn);
        majorbtn=(Button)activity.findViewById(R.id.majorbtn);
	}

}
