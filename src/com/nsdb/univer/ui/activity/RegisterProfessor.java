package com.nsdb.univer.ui.activity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;

import com.nsdb.univer.R;
import com.nsdb.univer.supporter.data.AppPref;
import com.nsdb.univer.supporter.network.NetworkSupporter;
import com.nsdb.univer.supporter.ui.OnClickMover;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore.Images;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class RegisterProfessor extends IntentPreservingActivity implements OnClickListener {

	ImageView image;
	boolean imageAdded;
	private final static int REQUESTCODE_CAPTUREIMAGE=1;
	private final static int REQUESTCODE_GETIMAGE=2;
	
	EditText title;
	Button region,univ,college,major;
	private final static int REQUESTCODE_RANGE=3;
	
	Button apply;
	ProgressDialog pdl;
	

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.registerprofessor);
		
        image=(ImageView)findViewById(R.id.image);
        imageAdded=false;
        title=(EditText)findViewById(R.id.title);
        //getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        image.setOnClickListener(this);
		
    	region=(Button)findViewById(R.id.region);
    	univ=(Button)findViewById(R.id.univ);
    	college=(Button)findViewById(R.id.college);
    	major=(Button)findViewById(R.id.major);
    	region.setOnClickListener(new OnClickMover(this,new Intent("RangeSetting").putExtra("range","region"),REQUESTCODE_RANGE));
    	univ.setOnClickListener(new OnClickMover(this,new Intent("RangeSetting").putExtra("range","univ"),REQUESTCODE_RANGE));
    	college.setOnClickListener(new OnClickMover(this,new Intent("RangeSetting").putExtra("range","college"),REQUESTCODE_RANGE));
    	major.setOnClickListener(new OnClickMover(this,new Intent("RangeSetting").putExtra("range","major"),REQUESTCODE_RANGE));
		AppPref.getRangeSet().applyDataToView(region, univ, college, major);
		
        apply=(Button)findViewById(R.id.apply);
        apply.setOnClickListener(this);		
        
	}


	// for imagesearch, apply button
	public void onClick(View v) {

		switch(v.getId()) {
		
		case R.id.image: {
			final String items[] = { "직접 찍기", "앨범에서 가져오기" };
			AlertDialog.Builder ab = new AlertDialog.Builder(this);
			ab.setTitle("이미지");
			ab.setItems(items, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {

					switch(whichButton) {
					case 0:
						startActivityForResult( new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE),
												REQUESTCODE_CAPTUREIMAGE );
						break;
					case 1:
						startActivityForResult( new Intent(Intent.ACTION_PICK)
													.setType(android.provider.MediaStore.Images.Media.CONTENT_TYPE),
												REQUESTCODE_GETIMAGE );
						break;
					}
					dialog.cancel();
				}
			});
			ab.show();			
			} break;
			

		case R.id.apply:
			if(AppPref.getRangeSet().isFilled()==false ||
			title.getText().toString().compareTo("")==0 ) {
			Toast.makeText(this, "모든 데이터를 입력하여야 합니다.",Toast.LENGTH_SHORT).show();
			} else {
				new RegisterProfessorHelper().execute();
			} break;
		}
	}
	
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);
		
		if(resultCode==RESULT_OK) {
			switch(requestCode) {
			case REQUESTCODE_RANGE:
				AppPref.getRangeSet().applyDataToView(region,univ,college,major);
				getIntent().putExtra("range_changed",true);
				break;
			case REQUESTCODE_CAPTUREIMAGE:
				image.setImageBitmap( (Bitmap)data.getExtras().get("data") );	
				imageAdded=true;
				break;
			case REQUESTCODE_GETIMAGE:
				try {
					image.setImageBitmap( Images.Media.getBitmap( getContentResolver(),data.getData() ) );
					imageAdded=true;
				} catch (FileNotFoundException e) {
					Toast.makeText(this,"FileNotFoundException",Toast.LENGTH_SHORT).show();
					e.printStackTrace();
				} catch (IOException e) {
					Toast.makeText(this,"IOException",Toast.LENGTH_SHORT).show();
					e.printStackTrace();
				} catch (Exception e) {
					Toast.makeText(this,"Unknown Exception",Toast.LENGTH_SHORT).show();
					e.printStackTrace();					
				}
				break;				
			}
		}
	}
	
	
	private class RegisterProfessorHelper extends AsyncTask<Void,Void,String> {
		
		@Override
		protected void onPreExecute() {
			pdl=ProgressDialog.show(RegisterProfessor.this,"Loading","Loading...",true,false);
			image.setDrawingCacheEnabled(true);
			super.onPreExecute();

		}

		@Override
		protected String doInBackground(Void... params) {
			
			// create url : 1.234.23.142/~ypunval/professors/
			String url=getResources().getString(R.string.base_url)+'/'
					+getResources().getString(R.string.professor_url)+'/';
			System.out.println("XML URL : "+url);
					
			try {				
				// create http post for sending
				HttpPost request=new HttpPost(url);
				
				// multipart entity
				MultipartEntity ment=new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
				// base data
				ment.addPart("user_id",new StringBody(""+AppPref.getInt("user_id")));				
				ment.addPart("value",new StringBody(AppPref.getString("value")));				
				ment.addPart("name",new StringBody(title.getText().toString(),Charset.forName("UTF-8")));				
				ment.addPart("region",new StringBody(""+AppPref.getRangeSet().get("region").id));
				ment.addPart("university",new StringBody(""+AppPref.getRangeSet().get("univ").id));
				ment.addPart("college",new StringBody(""+AppPref.getRangeSet().get("college").id));
				//ment.addPart("major",new StringBody(""+AppPref.getRangeSet().get("major").id));
				// image data
				if(imageAdded) {
					// create temp image file
					Bitmap bit=image.getDrawingCache();
					FileOutputStream out = openFileOutput("temp.jpg",MODE_PRIVATE);
					bit.compress(Bitmap.CompressFormat.JPEG,85,out);
					out.flush();
					out.close();
					
					// create FileBody
					File f=new File(getFilesDir().getPath()+"/temp.jpg");
					FileBody bin=new FileBody(f);
					ment.addPart("image",bin);
				}
				// set
				request.setEntity(ment);
				
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
			image.setDrawingCacheEnabled(false);

			if(result.compareTo("200")==0) {
				Toast.makeText(RegisterProfessor.this,"등록 성공",Toast.LENGTH_SHORT).show();
				setResult(RESULT_OK,getIntent());
				finish();
			} else {
				Toast.makeText(RegisterProfessor.this,result,Toast.LENGTH_SHORT).show();				
			}
		}
	}
	
}
