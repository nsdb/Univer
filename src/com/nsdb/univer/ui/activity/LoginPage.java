package com.nsdb.univer.ui.activity;

import java.io.InputStreamReader;
import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.EditText;
import android.widget.Toast;

import com.nsdb.univer.R;
import com.nsdb.univer.supporter.GCMRegIdGetter;
import com.nsdb.univer.supporter.NetworkSupporter;
import com.nsdb.univer.supporter.data.AppPref;
import com.nsdb.univer.supporter.ui.FontSetter;
import com.nsdb.univer.supporter.ui.OnClickMover;

public class LoginPage extends Activity implements OnClickListener {
	
	EditText id;
	EditText password;
	ImageButton login;
	ImageButton signup;
	ProgressDialog pdl;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        FontSetter.setDefault(this);
        
        id=(EditText)findViewById(R.id.id);
        password=(EditText)findViewById(R.id.password);
        login=(ImageButton)findViewById(R.id.login);
        signup=(ImageButton)findViewById(R.id.signup);
        id.setText(AppPref.getString("id"));
        login.setOnClickListener(this);
        signup.setOnClickListener(new OnClickMover(this,new Intent("RegisterUser")));
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        
    }

    // for login button
	public void onClick(View v) {
		
		if(id.getText().toString().compareTo("")==0) {
			Toast.makeText(this, "ID를 입력하세요.",Toast.LENGTH_SHORT).show();
		} else if(password.getText().toString().compareTo("")==0) {
			Toast.makeText(this, "비밀번호를 입력하세요.",Toast.LENGTH_SHORT).show();				
		} else {
			new LoginHelper().execute();
		}
	}
	
	
	private class LoginHelper extends AsyncTask<Void,Void,String> {

		@Override
		protected final void onPreExecute() {
			super.onPreExecute();
			pdl=ProgressDialog.show(LoginPage.this,"Loading","Loading...",true,false);			
		}

		@Override
		protected String doInBackground(Void... params) {

			// login : 1.234.23.142/~ypunval/login/
			String url=getResources().getString(R.string.base_url)+'/'
					+getResources().getString(R.string.login_url)+'/';

			try {
				HttpPost request=new HttpPost(url);
				ArrayList<NameValuePair> postdata=new ArrayList<NameValuePair>();
				postdata.add( new BasicNameValuePair("username",AppPref.getString("id") ) );
				postdata.add( new BasicNameValuePair("password",AppPref.getString("password") ) );
				postdata.add( new BasicNameValuePair("device_type",""+2) );
				postdata.add( new BasicNameValuePair("deviceToken",GCMRegIdGetter.get(LoginPage.this)) );
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
		protected final void onPostExecute(String result) {
			super.onPostExecute(result);			
			pdl.dismiss();
			if(result==null) {
				Toast.makeText(LoginPage.this,"로그인 실패",Toast.LENGTH_SHORT).show();
			} else try {		
				System.out.println("result : "+result);
				String[] splited=result.split("\\+");        // To avoid PatternSyntaxException
				
				AppPref.setString("id",id.getText().toString());
				AppPref.setString("password",password.getText().toString());
				AppPref.setInt("user_id",Integer.parseInt(splited[0]));
				AppPref.setString("value",splited[1]);
				
				System.out.println("Login Success");
				System.out.println("id : "+id.getText().toString());
				System.out.println("password : "+password.getText().toString());
				System.out.println("user_id : "+splited[0]);
				System.out.println("value : "+splited[1]);
				
				Toast.makeText(LoginPage.this,"로그인 성공",Toast.LENGTH_SHORT).show();
				startActivity( new Intent("TabMain") );
				finish();
				
			} catch(Exception e) {
				Toast.makeText(LoginPage.this,result,Toast.LENGTH_SHORT).show();
				e.printStackTrace();
			}
			
		}
		
		
	}
	
	
	
	

}
