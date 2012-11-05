package com.nsdb.univer.ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.nsdb.univer.R;
import com.nsdb.univer.common.AppPref;
import com.nsdb.univer.common.LoginChecker;
import com.nsdb.univer.common.ui.OnClickMover;

public class LoginPage extends Activity implements OnClickListener {
	
	EditText id;
	EditText password;
	Button login;
	Button signup;
	ProgressDialog pdl;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        
        id=(EditText)findViewById(R.id.id);
        password=(EditText)findViewById(R.id.password);
        login=(Button)findViewById(R.id.login);
        signup=(Button)findViewById(R.id.signup);
        id.setText(AppPref.getString("id"));
        login.setOnClickListener(this);
        signup.setOnClickListener(new OnClickMover(this,new Intent("RegisterUser")));
        
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
			new LoginHelper(this).execute();
		}
	}

	private class LoginHelper extends LoginChecker {
		
		public LoginHelper(Activity activity) {
			super(activity);
		}
		
		@Override
		protected void onStartLogin() {
			pdl=ProgressDialog.show(LoginPage.this,"Loading","Loading...",true,false);
			AppPref.setString("id",id.getText().toString());
			AppPref.setString("password",password.getText().toString());			
		}
		
		@Override
		protected void onSuccessLogin() {
			pdl.dismiss();
			startActivity( new Intent("TabMain") );
			finish();
		}
		
		@Override
		protected void onFailLogin() {			
			pdl.dismiss();
		}

	}
}
