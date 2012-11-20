package com.nsdb.univer.ui.activity;

import java.io.InputStreamReader;
import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

import com.nsdb.univer.R;
import com.nsdb.univer.supporter.NotificationHelper;
import com.nsdb.univer.supporter.data.AppPref;
import com.nsdb.univer.supporter.network.NetworkSupporter;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
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
        
        // login check
        if(AppPref.getInt("user_id")==-1 || AppPref.getString("value").compareTo("")==0)
        	startActivityForResult( new Intent("LoginPage"),0 );
        else
        	new LoginChecker().execute();
        
    }
    
    private class LoginChecker extends AsyncTask<Void,Void,String> {

		@Override
		protected String doInBackground(Void... params) {

			// login : 1.234.23.142/~ypunval/login_check/
			String url=getResources().getString(R.string.base_url)+'/'
					+getResources().getString(R.string.logincheck_url)+'/';

			try {
				// create http post for sending
				HttpPost request=new HttpPost(url);
				ArrayList<NameValuePair> postdata=new ArrayList<NameValuePair>();
				postdata.add( new BasicNameValuePair("user_id",""+AppPref.getInt("user_id") ) );
				postdata.add( new BasicNameValuePair("value",AppPref.getString("value") ) );
				UrlEncodedFormEntity ent = new UrlEncodedFormEntity(postdata,HTTP.UTF_8);
				request.setEntity(ent);

				// get result
				InputStreamReader isr=NetworkSupporter.getStreamFromRequest(request);
				String result=NetworkSupporter.getStringFromStream(isr);
				isr.close();
				return result;
				
			} catch(Exception e) {
				e.printStackTrace();
				return null;
			}
		}
		
		@Override
		protected void onPostExecute(String result) {
			if(result.compareTo("200")==0) {
				startActivityForResult( new Intent("TabMain"),0 );
			} else {
				startActivityForResult( new Intent("LoginPage"),0 );				
			}
		}
    	
    }
    
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		AppPref.save(this);
		finish();
	}
    
}
