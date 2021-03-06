package com.nsdb.univer.ui.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class ActiveFragment extends Fragment {
	
	protected Activity THIS;
	private int layoutId;
	
	public ActiveFragment() { super(); }
	public ActiveFragment(Activity activity,int layoutId) {
		super();
		THIS=activity;
		this.layoutId=layoutId;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
	    View v = inflater.inflate(layoutId, null);
	    ViewGroup.LayoutParams params=new ViewGroup.LayoutParams(
	    		ViewGroup.LayoutParams.MATCH_PARENT,
	    		ViewGroup.LayoutParams.MATCH_PARENT);
	    v.setLayoutParams(params);
	    
	    return v;
	}
	
}
