package com.nsdb.univer.uisupporter;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.View.OnClickListener;

public class ActiveFragmentHost extends FragmentActivity {
	
	private Fragment currentFragment;
	
    protected class OnClickSwitcher implements OnClickListener {

    	int screenId;
    	Fragment f;
    	String tag;
    	
    	public OnClickSwitcher(int screenId,Fragment f,String tag) {
    		this.screenId=screenId;
    		this.f=f;
    		this.tag=tag;
    	}
    	
		public void onClick(View v) {
			switchScreen(screenId,f,tag);			
		}
    	
    }
	
    public void switchScreen(int screenId, Fragment f,String tag) {
    	FragmentManager fm = getSupportFragmentManager();
    	FragmentTransaction ft = fm.beginTransaction();
    	ft.replace(screenId, f, tag);
    	ft.commit();
    	currentFragment=f;
    }
    
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);
		currentFragment.onActivityResult(requestCode, resultCode, data);
	}

}
