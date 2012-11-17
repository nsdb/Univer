package com.nsdb.univer.ui.activity;

import android.app.Activity;
import android.os.Bundle;

public class IntentPreservingActivity extends Activity {
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// if you don't set result, returned Intent (when this activity is finished) is null.
		setResult(RESULT_CANCELED,getIntent());		
		
	}

}
