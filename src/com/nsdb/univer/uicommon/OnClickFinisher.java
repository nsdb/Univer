package com.nsdb.univer.uicommon;

import android.app.Activity;
import android.view.View;
import android.view.View.OnClickListener;

public class OnClickFinisher implements OnClickListener {

	Activity activity;
	
	public OnClickFinisher(Activity activity) {
		this.activity=activity;
	}
	
	public void onClick(View v) {
		activity.finish();
	}

}
