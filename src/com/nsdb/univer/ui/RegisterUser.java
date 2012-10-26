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
import android.widget.TextView;
import android.widget.Toast;

public class RegisterUser extends Activity implements OnClickListener {
	
	EditText id,nickname,password;

	TextView region,univ,college,major;
	Button regionbtn,univbtn,collegebtn,majorbtn;
	
	Button apply;	
	ProgressDialog pdl;	
	private final static int REGISTERUSER_SUCCESS=200;
	private final static int REGISTERUSER_DUPLICATEEMAIL=101;
	private final static int REGISTERUSER_CUSTOMCHOICEERROR=102;
	private final static int REGISTERUSER_UNKNOWNERROR=103;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registeruser);
        
        id=(EditText)findViewById(R.id.id);
        nickname=(EditText)findViewById(R.id.nickname);
        password=(EditText)findViewById(R.id.password);
        
        region=(TextView)findViewById(R.id.region);
        univ=(TextView)findViewById(R.id.univ);
        college=(TextView)findViewById(R.id.college);
        major=(TextView)findViewById(R.id.major);
        regionbtn=(Button)findViewById(R.id.regionbtn);
        univbtn=(Button)findViewById(R.id.univbtn);
        collegebtn=(Button)findViewById(R.id.collegebtn);
        majorbtn=(Button)findViewById(R.id.majorbtn);
    	regionbtn.setOnClickListener(new OnClickMover(this,new Intent("RangeSetting").putExtra("filter","region")));
    	univbtn.setOnClickListener(new OnClickMover(this,new Intent("RangeSetting").putExtra("filter","univ")));
    	collegebtn.setOnClickListener(new OnClickMover(this,new Intent("RangeSetting").putExtra("filter","college")));
    	majorbtn.setOnClickListener(new OnClickMover(this,new Intent("RangeSetting").putExtra("filter","major")));
        
        apply=(Button)findViewById(R.id.apply);
        apply.setOnClickListener(this);
        
        pdl=null;
    }
    
    @Override
    public void onResume() {
    	super.onResume();

    	// refresh position
    	region.setText(AppPref.getRangeData("region").title);
    	if(region.getText().length()==0) {
    		region.setText("없음");
    		univbtn.setEnabled(false);
    	} else {
    		univbtn.setEnabled(true);    		
    	}
    	univ.setText(AppPref.getRangeData("univ").title);
    	if(univ.getText().length()==0) {
    		univ.setText("없음");
			collegebtn.setEnabled(false);
		} else {
			collegebtn.setEnabled(true);    		
		}
    	college.setText(AppPref.getRangeData("college").title);
    	if(college.getText().length()==0) {
    		college.setText("없음");
			majorbtn.setEnabled(false);
		} else {
			majorbtn.setEnabled(true);    		
		}
    	major.setText(AppPref.getRangeData("major").title);
    	if(major.getText().length()==0)
    		major.setText("없음");
    	////

    	// Not yet
    	majorbtn.setEnabled(false);
    }

    // for apply button
	public void onClick(View v) {
		
		if(id.getText().toString().compareTo("")==0 ||
			nickname.getText().toString().compareTo("")==0 ||
			password.getText().toString().compareTo("")==0 ||
			AppPref.getRangeData("region").id==-1 ||
			AppPref.getRangeData("univ").id==-1 ||
			AppPref.getRangeData("college").id==-1 ) {
			Toast.makeText(this, "모든 데이터를 입력하여야 합니다",Toast.LENGTH_SHORT).show();				
		} else {
			System.out.println("ID : "+id.getText().toString());
			System.out.println("NickName : "+nickname.getText().toString());
			System.out.println("Password : "+password.getText().toString());
			System.out.println("region : "+AppPref.getRangeData("region").id );
			System.out.println("university : "+AppPref.getRangeData("univ").id );
			System.out.println("college : "+AppPref.getRangeData("college").id );
			new RegisterUserHelper().execute();
		}

	}
	
	
	private class RegisterUserHelper extends AsyncTask<Void,Void,Integer> {

		@Override
		protected void onPreExecute() {
			pdl=ProgressDialog.show(RegisterUser.this,"Loading","Loading...",true,false);
			super.onPreExecute();
		}

		@Override
		protected Integer doInBackground(Void... params) {
			
			// create url
			// login : 1.234.23.142/~ypunval/register/
			String url=getResources().getString(R.string.base_url)+'/'
					+getResources().getString(R.string.registeruser_url)+'/';
			System.out.println("XML URL : "+url);
					
			try {
				// create http post for sending
				HttpPost request=new HttpPost(url);
				ArrayList<NameValuePair> postdata=new ArrayList<NameValuePair>();
				postdata.add( new BasicNameValuePair("email",id.getText().toString()) );
				postdata.add( new BasicNameValuePair("first_name",nickname.getText().toString()) );
				postdata.add( new BasicNameValuePair("password",password.getText().toString()) );
				postdata.add( new BasicNameValuePair("region",""+AppPref.getRangeData("region").id) );
				postdata.add( new BasicNameValuePair("university",""+AppPref.getRangeData("univ").id) );
				postdata.add( new BasicNameValuePair("college",""+AppPref.getRangeData("college").id) );
				//postdata.add( new BasicNameValuePair("major",""+AppPref.getRangeData("major").id) );
				postdata.add( new BasicNameValuePair("device_type",""+2) );
				postdata.add( new BasicNameValuePair("deviceToken",""+123) );
				UrlEncodedFormEntity ent = new UrlEncodedFormEntity(postdata,HTTP.UTF_8);
				request.setEntity(ent);

				// get result
				InputStreamReader isr=NetworkSupporter.getStreamFromRequest(request);
				String result=NetworkSupporter.getStringFromStream(isr);
				isr.close();
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
			case REGISTERUSER_SUCCESS:
				Toast.makeText(RegisterUser.this,"가입 성공",Toast.LENGTH_SHORT).show();
				AppPref.setString("id",id.getText().toString());
				AppPref.setString("password",password.getText().toString());
				startActivity( new Intent("TabMain") );
				finish();
				break;
			case REGISTERUSER_DUPLICATEEMAIL:
				Toast.makeText(RegisterUser.this,"중복 이메일",Toast.LENGTH_SHORT).show();
				break;
			case REGISTERUSER_CUSTOMCHOICEERROR:
				Toast.makeText(RegisterUser.this,"선택입력 에러",Toast.LENGTH_SHORT).show();
				break;
			case REGISTERUSER_UNKNOWNERROR:
				Toast.makeText(RegisterUser.this,"알 수 없는 에러",Toast.LENGTH_SHORT).show();
				break;
			default:
				Toast.makeText(RegisterUser.this,"알 수 없는 에러 (예외)",Toast.LENGTH_SHORT).show();
				break;
			}
		}
	}
	
	
}
