package com.nsdb.univer.uisupporter;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;

public class OnClickMover implements OnClickListener
{
		
	Activity activity;
	String action;
	String filter;
	int requestCode;
	
	public OnClickMover(Activity activity, String action, String filter) {
		this.activity=activity;
		this.action=action;
		this.filter=filter;
		this.requestCode=-1;
	}
	public OnClickMover(Activity activity, String action, String filter, int requestCode) {
		this.activity=activity;
		this.action=action;
		this.filter=filter;
		this.requestCode=requestCode;
	}

	public void onClick(View v) {
		if(requestCode==-1)
			moveActivity(activity,action,filter);
		else
			moveActivityForResult(activity,action,filter,requestCode);
	}		
	
	public static void moveActivity(Activity activity,String action,String filter) {
		Intent i=new Intent();
		i.setAction(action);
		i.putExtra("filter",filter);
		activity.startActivity(i);		
	}
	public static void moveActivityForResult(Activity activity,String action,String filter,int requestCode) {
		Intent i=new Intent();
		i.setAction(action);
		i.putExtra("filter",filter);
		activity.startActivityForResult(i,requestCode);		
	}
}
