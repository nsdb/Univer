package com.nsdb.univer.supporter.ui;

import android.app.Activity;

public class NullIntentPreventer {
	
	public static void prevent(Activity activity) {
		activity.setResult(Activity.RESULT_CANCELED,activity.getIntent());		
	}

}
