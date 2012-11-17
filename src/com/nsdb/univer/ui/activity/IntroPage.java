package com.nsdb.univer.ui.activity;

import com.nsdb.univer.R;
import com.nsdb.univer.supporter.NotificationHelper;
import com.nsdb.univer.supporter.data.AppPref;
import com.nsdb.univer.supporter.network.LoginChecker;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class IntroPage extends Activity {
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.intro);
        
        // init
        AppPref.load(this);

        // from Noti?
        if(getIntent()!=null && getIntent().getBooleanExtra("noti",false)==true) {
        	NotificationHelper.removeNotification(this,0);
        	AppPref.setInt("startTab",2);
        }
        ////
        
        // auto login
		//startActivityForResult( new Intent("LoginPage"),0 );
        new AutoLoginer(this).execute();
        
    }
    
    private class AutoLoginer extends LoginChecker {

		public AutoLoginer(Activity activity) {
			super(activity);
		}

		@Override
		protected void onStartLogin() {
		}

		@Override
		protected void onSuccessLogin() {
			startActivityForResult( new Intent("TabMain"),0 );
		}

		@Override
		protected void onFailLogin() {
			startActivityForResult( new Intent("LoginPage"),0 );
		}
    	
    }
    
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		AppPref.save(this);
		finish();
	}
    
}
