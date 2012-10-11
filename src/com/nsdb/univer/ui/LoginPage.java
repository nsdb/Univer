package com.nsdb.univer.ui;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

import com.nsdb.univer.data.AppPref;
import com.nsdb.univer.uisupporter.OnClickMover;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginPage extends Activity implements OnClickListener {
	
	EditText id;
	EditText password;
	Button login;
	Button signup;
	ProgressDialog pdl;
	
	public final static int LOGIN_SUCCESS=200;
	public final static int LOGIN_FAIL=101;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        
        // Initializing
        AppPref.init();
        
        id=(EditText)findViewById(R.id.id);
        password=(EditText)findViewById(R.id.password);
        login=(Button)findViewById(R.id.login);
        signup=(Button)findViewById(R.id.signup);
        login.setOnClickListener(this);
        signup.setOnClickListener(new OnClickMover(this,"RegisterUser",""));
    }

    // for login button
	public void onClick(View v) {
		
		if(id.getText().toString().compareTo("")==0) {
			Toast.makeText(this, "ID를 입력하세요.",Toast.LENGTH_SHORT).show();
		} else if(password.getText().toString().compareTo("")==0) {
			Toast.makeText(this, "비밀번호를 입력하세요.",Toast.LENGTH_SHORT).show();				
		} else {
			System.out.println("ID : "+id.getText().toString());
			System.out.println("Password : "+password.getText().toString());
			new LoginHelper().execute();
		}
	}

	private class LoginHelper extends AsyncTask<Void,Void,Integer> {

		@Override
		protected void onPreExecute() {
			pdl=ProgressDialog.show(LoginPage.this,"Loading","Loading...",true,false);
			super.onPreExecute();
		}

		@Override
		protected Integer doInBackground(Void... params) {
			
			// create url
			// login : 1.234.23.142/~ypunval/login/
			String url=getResources().getString(R.string.base_url)+'/'
					+getResources().getString(R.string.login_url)+'/';
			System.out.println("XML URL : "+url);
					
			try {
				// create http post for sending
				HttpPost request=new HttpPost(url);
				ArrayList<NameValuePair> postdata=new ArrayList<NameValuePair>();
				postdata.add( new BasicNameValuePair("username",id.getText().toString()) );
				postdata.add( new BasicNameValuePair("password",password.getText().toString()) );
				UrlEncodedFormEntity ent = new UrlEncodedFormEntity(postdata,HTTP.UTF_8);
				request.setEntity(ent);

				// get data from server
				HttpClient client=new DefaultHttpClient();
				HttpResponse response=client.execute(request);
				InputStream is=response.getEntity().getContent();
				InputStreamReader isr=new InputStreamReader(is);
				BufferedReader br=new BufferedReader(isr);
				String result="";
				String temp=br.readLine();
				while(temp!=null) {
					result=result+temp;
					temp=br.readLine();
				}
				System.out.println("Result : "+result);
				
				return Integer.parseInt(result);
				
			} catch(Exception e) {
				e.printStackTrace();
				return -1;
			}
		}
		
		@Override
		protected void onPostExecute(Integer result) {
			
			pdl.dismiss();

			switch(result) {
			case LOGIN_SUCCESS: 
				Toast.makeText(LoginPage.this,"로그인 성공",Toast.LENGTH_SHORT).show();
				AppPref.setString("id",id.getText().toString());
				AppPref.setString("password",password.getText().toString());
				//OnClickMover.moveActivity(LoginPage.this,"BookMarketMain","");
				//finish();
				break;
			case LOGIN_FAIL:
				Toast.makeText(LoginPage.this,"로그인 실패",Toast.LENGTH_SHORT).show();
				break;
			default:
				Toast.makeText(LoginPage.this,"알 수 없는 에러 발생",Toast.LENGTH_SHORT).show();
				break;					
			}

			OnClickMover.moveActivity(LoginPage.this,"TabMain","");
			finish();
		}
	}
}
