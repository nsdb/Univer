package com.nsdb.univer.ui.activity;

import java.io.InputStreamReader;
import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

import com.makeramen.segmented.SegmentedRadioGroup;
import com.nsdb.univer.R;
import com.nsdb.univer.data.ProfessorData;
import com.nsdb.univer.supporter.data.AppPref;
import com.nsdb.univer.supporter.network.NetworkSupporter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RatingBar;
import android.widget.Toast;

public class EvaluateProfessor extends Activity implements OnClickListener, OnCheckedChangeListener {

	ProfessorData lastdata;
	RatingBar quality,report,grade,attendance,personality;
	SegmentedRadioGroup evaluate;
	int checkState;
	EditText comment;
	Button apply;
	ProgressDialog pdl;
	
	private static int CHECK_GOOD=1;
	private static int CHECK_BAD=0;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.evaluateprofessor);
        
        lastdata=AppPref.getLastProfessorData();
        quality=(RatingBar)findViewById(R.id.quality);
        report=(RatingBar)findViewById(R.id.report);
        grade=(RatingBar)findViewById(R.id.grade);
        attendance=(RatingBar)findViewById(R.id.attendance);
        personality=(RatingBar)findViewById(R.id.personality);
        evaluate=(SegmentedRadioGroup)findViewById(R.id.evaluate);
        checkState=CHECK_GOOD;
        comment=(EditText)findViewById(R.id.comment);
        apply=(Button)findViewById(R.id.apply);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        evaluate.setOnCheckedChangeListener(this);
        apply.setOnClickListener(this);
	}

	public void onCheckedChanged(RadioGroup group, int checkedId) {
		switch(checkedId) {
		case R.id.good: checkState=CHECK_GOOD; break;
		case R.id.bad: checkState=CHECK_BAD; break;
		}
	}
	public void onClick(View v) {		
		new EvaluateProfessorHelper().execute();
	}
	
	private class EvaluateProfessorHelper extends AsyncTask<Void,Void,String> {
		
		@Override
		protected void onPreExecute() {
			pdl=ProgressDialog.show(EvaluateProfessor.this,"Loading","Loading...",true,false);
			super.onPreExecute();
		}

		@Override
		protected String doInBackground(Void... params) {
			
			// create url
			// login : {base_url}/evaluations/
			String url=getResources().getString(R.string.base_url)+'/'
					+getResources().getString(R.string.evaluateprofessor_url)+'/';
			System.out.println("XML URL : "+url);
					
			try {
				// create http post for sending
				HttpPost request=new HttpPost(url);
				ArrayList<NameValuePair> postdata=new ArrayList<NameValuePair>();
				postdata.add( new BasicNameValuePair("user_id",""+AppPref.getInt("user_id")));				
				postdata.add( new BasicNameValuePair("value",AppPref.getString("value")));				
				postdata.add( new BasicNameValuePair("professor",""+lastdata.id) );
				postdata.add( new BasicNameValuePair("quality",""+quality.getRating()*2) );
				postdata.add( new BasicNameValuePair("report",""+report.getRating()*2) );
				postdata.add( new BasicNameValuePair("grade",""+grade.getRating()*2) );
				postdata.add( new BasicNameValuePair("personality",""+personality.getRating()*2) );
				postdata.add( new BasicNameValuePair("attendance",""+attendance.getRating()*2) );
				postdata.add( new BasicNameValuePair("like",""+checkState) );
				if(comment.getText().toString().compareTo("")!=0)
					postdata.add( new BasicNameValuePair("content",""+comment.getText().toString()) );					
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
				Toast.makeText(EvaluateProfessor.this,"등록 성공",Toast.LENGTH_SHORT).show();
				setResult(RESULT_OK,getIntent());
				finish();
			} else {
				Toast.makeText(EvaluateProfessor.this,result,Toast.LENGTH_SHORT).show();				
			}
		}
		
	}

}
