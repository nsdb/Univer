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
import com.nsdb.univer.common.RangeData;
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
	RangeData regiondata,univdata,collegedata,majordata;
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
        regiondata=AppPref.getRangeData("region");
        univdata=AppPref.getRangeData("univ");
        collegedata=AppPref.getRangeData("college");
        majordata=AppPref.getRangeData("major");
    	regionbtn.setOnClickListener(new OnClickMover(this,new Intent("RangeSetting").putExtra("filter","region"),REQUESTCODE_RANGE));
    	univbtn.setOnClickListener(new OnClickMover(this,new Intent("RangeSetting").putExtra("filter","univ"),REQUESTCODE_RANGE));
    	collegebtn.setOnClickListener(new OnClickMover(this,new Intent("RangeSetting").putExtra("filter","college"),REQUESTCODE_RANGE));
    	majorbtn.setOnClickListener(new OnClickMover(this,new Intent("RangeSetting").putExtra("filter","major"),REQUESTCODE_RANGE));
        
        apply=(Button)findViewById(R.id.apply);
        apply.setOnClickListener(this);
        
        pdl=null;
    }
    
    @Override
    public void onResume() {
    	super.onResume();

    	// refresh position
    	region.setText(regiondata.title);
    	if(region.getText().length()==0) {
    		region.setText("없음");
    		univbtn.setEnabled(false);
    	} else {
    		univbtn.setEnabled(true);    		
    	}
    	univ.setText(univdata.title);
    	if(univ.getText().length()==0) {
    		univ.setText("없음");
			collegebtn.setEnabled(false);
		} else {
			collegebtn.setEnabled(true);    		
		}
    	college.setText(collegedata.title);
    	if(college.getText().length()==0) {
    		college.setText("없음");
			majorbtn.setEnabled(false);
		} else {
			majorbtn.setEnabled(true);    		
		}
    	major.setText(majordata.title);
    	if(major.getText().length()==0)
    		major.setText("없음");
    	////

    	// Not yet
    	majorbtn.setEnabled(false);
    }

    @Override
	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
    	if(resultCode==RESULT_OK) {
			String filter=data.getStringExtra("filter");
			String title=data.getStringExtra("title");
			String nick=data.getStringExtra("nick");
			int id=data.getIntExtra("id",-1);
			if(filter.compareTo("region")==0) {
				regiondata=new RangeData(title,nick,id);
				univdata=new RangeData();
				collegedata=new RangeData();
				majordata=new RangeData();
			} else if(filter.compareTo("univ")==0) {
				univdata=new RangeData(title,nick,id);
				collegedata=new RangeData();
				majordata=new RangeData();
			} else if(filter.compareTo("college")==0) {
				collegedata=new RangeData(title,nick,id);
				majordata=new RangeData();
			} else if(filter.compareTo("major")==0) {
				majordata=new RangeData();
			}
    		
    	}
	}
    
    // for apply button
	public void onClick(View v) {
		
		if(id.getText().toString().compareTo("")==0 ||
			nickname.getText().toString().compareTo("")==0 ||
			password.getText().toString().compareTo("")==0 ||
			regiondata.id==-1 ||
			univdata.id==-1 ||
			collegedata.id==-1 ) {
			Toast.makeText(this, "모든 데이터를 입력하여야 합니다",Toast.LENGTH_SHORT).show();				
		} else {
			System.out.println("ID : "+id.getText().toString());
			System.out.println("NickName : "+nickname.getText().toString());
			System.out.println("Password : "+password.getText().toString());
			System.out.println("region : "+regiondata.id );
			System.out.println("university : "+univdata.id );
			System.out.println("college : "+collegedata.id );
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
				postdata.add( new BasicNameValuePair("region",""+regiondata.id) );
				postdata.add( new BasicNameValuePair("university",""+univdata.id) );
				postdata.add( new BasicNameValuePair("college",""+collegedata.id) );
				//postdata.add( new BasicNameValuePair("major",""+majordata.id) );
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
				AppPref.setRangeData("region",regiondata);
				AppPref.setRangeData("univ",univdata);
				AppPref.setRangeData("college",collegedata);
				AppPref.setRangeData("major",majordata);
				startActivity( new Intent("TabMain") );
				setResult(RESULT_OK,getIntent());
				finish();
			} else {
				Toast.makeText(RegisterUser.this,result,Toast.LENGTH_SHORT).show();				
			}
		}
	}
	
	
}
