package com.nsdb.univer.ui.activity;

import java.io.InputStreamReader;
import java.util.ArrayList;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

import com.nsdb.univer.R;
import com.nsdb.univer.supporter.data.AppPref;
import com.nsdb.univer.supporter.network.NetworkSupporter;
import com.nsdb.univer.supporter.ui.OnClickMover;

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
	private final static int REQUESTCODE_RANGE=1;
	
	Button apply;	
	ProgressDialog pdl;	
	
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
    	regionbtn.setOnClickListener(new OnClickMover(this,new Intent("RangeSetting").putExtra("range","region"),REQUESTCODE_RANGE));
    	univbtn.setOnClickListener(new OnClickMover(this,new Intent("RangeSetting").putExtra("range","univ"),REQUESTCODE_RANGE));
    	collegebtn.setOnClickListener(new OnClickMover(this,new Intent("RangeSetting").putExtra("range","college"),REQUESTCODE_RANGE));
    	majorbtn.setOnClickListener(new OnClickMover(this,new Intent("RangeSetting").putExtra("range","major"),REQUESTCODE_RANGE));
    	AppPref.getRangeSet().applyDataToView(region,univ,college,major,regionbtn,univbtn,collegebtn,majorbtn);
        
        apply=(Button)findViewById(R.id.apply);
        apply.setOnClickListener(this);
        
        pdl=null;
    }
    
    @Override
	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
    	if(resultCode==RESULT_OK) {
	    	AppPref.getRangeSet().applyDataToView(region,univ,college,major,regionbtn,univbtn,collegebtn,majorbtn);
    	}
	}
    
    // for apply button
	public void onClick(View v) {
		
		if(id.getText().toString().compareTo("")==0 ||
			nickname.getText().toString().compareTo("")==0 ||
			password.getText().toString().compareTo("")==0 ||
			AppPref.getRangeSet().isFilled()==false ) {
			Toast.makeText(this, "모든 데이터를 입력하여야 합니다",Toast.LENGTH_SHORT).show();				
		} else {
			System.out.println("ID : "+id.getText().toString());
			System.out.println("NickName : "+nickname.getText().toString());
			System.out.println("Password : "+password.getText().toString());
			System.out.println("region : "+AppPref.getRangeSet().get("region").id );
			System.out.println("university : "+AppPref.getRangeSet().get("univ").id );
			System.out.println("college : "+AppPref.getRangeSet().get("college").id );
			new RegisterUserHelper().execute();
		}

	}
	
	
	private class RegisterUserHelper extends AsyncTask<Void,Void,String> {

		@Override
		protected void onPreExecute() {
			pdl=ProgressDialog.show(RegisterUser.this,"Loading","Loading...",true,false);
			super.onPreExecute();
		}

		@Override
		protected String doInBackground(Void... params) {
			
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
				postdata.add( new BasicNameValuePair("region",""+AppPref.getRangeSet().get("region").id) );
				postdata.add( new BasicNameValuePair("university",""+AppPref.getRangeSet().get("univ").id) );
				postdata.add( new BasicNameValuePair("college",""+AppPref.getRangeSet().get("college").id) );
				//postdata.add( new BasicNameValuePair("major",""+AppPref.getRangeSet().get("major").id) );
				postdata.add( new BasicNameValuePair("device_type",""+2) );
				postdata.add( new BasicNameValuePair("deviceToken",""+123) );
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
				Toast.makeText(RegisterUser.this,"가입 성공",Toast.LENGTH_SHORT).show();
				AppPref.setString("id",id.getText().toString());
				AppPref.setString("password",password.getText().toString());
				startActivity( new Intent("TabMain") );
				setResult(RESULT_OK,getIntent());
				finish();
			} else {
				Toast.makeText(RegisterUser.this,result,Toast.LENGTH_SHORT).show();				
			}
		}
	}
	
	
}