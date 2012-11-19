package com.nsdb.univer.ui.activity;

import java.util.ArrayList;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.View.OnClickListener;

public class ActiveFragmentHost extends FragmentActivity {
	
	private ArrayList<Fragment> fragmentList;
	private ArrayList<String> tagList;
	private ArrayList<Integer> idList;
	private int cPosition;
	
	public ActiveFragmentHost() {
		fragmentList=new ArrayList<Fragment>();
		tagList=new ArrayList<String>();
		idList=new ArrayList<Integer>();
		cPosition=-1;
	}
	
	protected void addFragment(Fragment fragment,String tag,int layoutId) {
		fragmentList.add(fragment);
		tagList.add(tag);
		idList.add(layoutId);

		FragmentManager fm = getSupportFragmentManager();
    	FragmentTransaction ft = fm.beginTransaction();
    	ft.replace(layoutId,fragment,tag);
    	ft.commit();
	}

	protected void switchScreen(int position) {
		cPosition=position;
		for(int i=0;i<fragmentList.size();i++) {
			findViewById(idList.get(i)).setVisibility(View.GONE);						
		}
		findViewById(idList.get(cPosition)).setVisibility(View.VISIBLE);
	}
	
	protected int getCurrentPosition() {
		return cPosition;
	}

    protected class OnClickSwitcher implements OnClickListener {

    	int position;
    	
		public OnClickSwitcher(int position) {
			this.position=position;
		}

		public void onClick(View v) {
			switchScreen(position);
		}
    	
    }

    @Override
	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);
		for(int i=0;i<fragmentList.size();i++)
			fragmentList.get(i).onActivityResult(requestCode, resultCode, data);
	}
}
