package com.nsdb.univer.common;

import java.io.InputStreamReader;
import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

import android.app.Activity;
import android.os.AsyncTask;
import android.widget.Toast;

import com.nsdb.univer.R;

public abstract class LoginChecker extends AsyncTask<Void, Void, Boolean> {
	
	private Activity activity;
	
	public LoginChecker(Activity activity) {
		this.activity=activity;
	}
	
	protected abstract void onStartLogin();
	protected abstract void onSuccessLogin();
	protected abstract void onFailLogin();
	

	@Override
	protected final void onPreExecute() {
		
		Toast.makeText(activity,"로그인 시도 중...",Toast.LENGTH_SHORT).show();
		System.out.println("Login start");
		onStartLogin();
		
	}

	@Override
	protected final Boolean doInBackground(Void... params) {
		
		if(AppPref.getString("id").compareTo("")==0 ||
			AppPref.getString("password").compareTo("")==0)
			return false;
		
		// login : 1.234.23.142/~ypunval/login/
		String url=activity.getResources().getString(R.string.base_url)+'/'
				+activity.getResources().getString(R.string.login_url)+'/';
				
		try {
			// create http post for sending
			HttpPost request=new HttpPost(url);
			ArrayList<NameValuePair> postdata=new ArrayList<NameValuePair>();
			postdata.add( new BasicNameValuePair("username",AppPref.getString("id") ) );
			postdata.add( new BasicNameValuePair("password",AppPref.getString("password") ) );
			UrlEncodedFormEntity ent = new UrlEncodedFormEntity(postdata,HTTP.UTF_8);
			request.setEntity(ent);

			// get result
			InputStreamReader isr=NetworkSupporter.getStreamFromRequest(request);
			String result=NetworkSupporter.getStringFromStream(isr);
			isr.close();
			
			// process result
			if(result.compareTo("200")==0) {
				return true;
			} else {
				return false;
			}
			
		} catch(Exception e) {
			e.printStackTrace();
			return false;
		}
		
	}
	
	@Override
	protected final void onPostExecute(Boolean result) {
		
		if(result==true) {
			
			Toast.makeText(activity,"로그인 성공",Toast.LENGTH_SHORT).show();
			System.out.println("Login success");
			onSuccessLogin();
			
		} else {
			
			Toast.makeText(activity,"로그인 실패, 로그인 화면으로 이동합니다",Toast.LENGTH_SHORT).show();
			System.out.println("Login fail");
			onFailLogin();
		}
	}
	

}
