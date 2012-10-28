package com.nsdb.univer.ui;

import com.nsdb.univer.common.AppPref;
import com.nsdb.univer.common.LoginChecker;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class IntroPage extends Activity {
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.intro);
        
        AppPref.load(this);
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
