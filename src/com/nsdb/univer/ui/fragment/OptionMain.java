package com.nsdb.univer.ui.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.gcm.GCMRegistrar;
import com.nsdb.univer.R;
import com.nsdb.univer.supporter.data.AppPref;

public class OptionMain extends ActiveFragment implements OnClickListener {

	Button logout;
	
	public OptionMain(Activity activity) {
		super(activity,R.layout.optionmain);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v=super.onCreateView(inflater, container, savedInstanceState);
		
		logout=(Button)v.findViewById(R.id.logout);
		logout.setOnClickListener(this);
		
		return v;
	}

	public void onClick(View v) {
		AppPref.setString("id","");
		AppPref.setString("password","");
		AppPref.setInt("user_id",-1);
		AppPref.setString("value","");
        GCMRegistrar.checkDevice(THIS);
        GCMRegistrar.checkManifest(THIS);
        GCMRegistrar.unregister(THIS);
		startActivity( new Intent("LoginPage") );
		THIS.finish();
	}
}
