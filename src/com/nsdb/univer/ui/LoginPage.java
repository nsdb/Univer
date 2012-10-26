package com.nsdb.univer.ui;

import java.io.InputStreamReader;
import java.util.ArrayList;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

import com.nsdb.univer.common.AppPref;
import com.nsdb.univer.common.NetworkSupporter;
import com.nsdb.univer.common.ui.OnClickMover;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
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
	private final static int REQUESTCODE_LOGIN=1;
	private final static int REQUESTCODE_REGISTER=2;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        
        // Initializing
        AppPref.load(this);
        
        id=(EditText)findViewById(R.id.id);
        password=(EditText)findViewById(R.id.password);
        login=(Button)findViewById(R.id.login);
        signup=(Button)findViewById(R.id.signup);
        id.setText(AppPref.getString("id"));
        login.setOnClickListener(this);
        signup.setOnClickListener(new OnClickMover(this,new Intent("RegisterUser"),REQUESTCODE_REGISTER));
        
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

	private class LoginHelper extends AsyncTask<Void,Void,String> {

		@Override
		protected void onPreExecute() {
			pdl=ProgressDialog.show(LoginPage.this,"Loading","Loading...",true,false);
			super.onPreExecute();
		}

		@Override
		protected String doInBackground(Void... params) {
			
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

				// get result
				InputStreamReader isr=NetworkSupporter.getStreamFromRequest(request);
				String result=NetworkSupporter.getStringFromStream(isr);
				isr.close();
				return result;
				
			} catch(Exception e) {
				e.printStackTrace();
				return "알 수 없는 에러 발생";
			}
		}
		
		@Override
		protected void onPostExecute(String result) {
			
			pdl.dismiss();
			
			if(result.compareTo("200")==0) {
				Toast.makeText(LoginPage.this,"로그인 성공",Toast.LENGTH_SHORT).show();
				AppPref.setString("id",id.getText().toString());
				AppPref.setString("password",password.getText().toString());
				startActivityForResult( new Intent("TabMain"),REQUESTCODE_LOGIN );				
			} else {
				Toast.makeText(LoginPage.this,result,Toast.LENGTH_SHORT).show();				
			}

		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);
		
		// Saving
		if(requestCode==REQUESTCODE_LOGIN) {
			AppPref.save(this);
			finish();
		} else if(requestCode==REQUESTCODE_REGISTER && resultCode==RESULT_OK) {
			AppPref.save(this);
			finish();
		}
	}
}
