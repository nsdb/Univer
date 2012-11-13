package com.nsdb.univer.ui.activity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.jdom2.Element;

import com.nsdb.univer.R;
import com.nsdb.univer.data.BookData;
import com.nsdb.univer.supporter.data.AppPref;
import com.nsdb.univer.supporter.network.NetworkSupporter;
import com.nsdb.univer.supporter.ui.ImageSetterNoCache;
import com.nsdb.univer.supporter.ui.OnClickMover;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore.Images;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;

public class RegisterBook extends Activity implements OnClickListener, OnCheckedChangeListener {

	
	RadioGroup sale;
	int saleMode;
	TextView region,univ,college,major;
	Button regionbtn,univbtn,collegebtn,majorbtn;
	private final static int REQUESTCODE_RANGE=4;
	
	Button barcode;
	String isbn;
	ImageButton imagesearch;
	ImageView image;
	private final static int REQUESTCODE_CAPTUREIMAGE=2;
	private final static int REQUESTCODE_GETIMAGE=3;
	EditText title,publisher,author,pubdate,edition,original_price,discount_price;
	private final static int REQUESTCODE_BARCODE=1;
	
	EditText description;
	CheckBox parcel,meet;
	
	Button apply;	
	ProgressDialog pdl;

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registerbook);
        
        // first linear
        sale=(RadioGroup)findViewById(R.id.sale);
        saleMode=BookData.SALEMODE_SELL;
        sale.setOnCheckedChangeListener(this);
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
        
        // second linear
        barcode=(Button)findViewById(R.id.barcode);
        isbn="1";
        imagesearch=(ImageButton)findViewById(R.id.imagesearch);
        image=(ImageView)findViewById(R.id.image);
        title=(EditText)findViewById(R.id.title);
        publisher=(EditText)findViewById(R.id.publisher);
        author=(EditText)findViewById(R.id.author);
        pubdate=(EditText)findViewById(R.id.pubdate);
        edition=(EditText)findViewById(R.id.edition);
        original_price=(EditText)findViewById(R.id.original_price);
        discount_price=(EditText)findViewById(R.id.discount_price);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        barcode.setOnClickListener(new OnClickMover(this,new Intent("com.google.zxing.client.android.SCAN"),REQUESTCODE_BARCODE));
        imagesearch.setOnClickListener(this);
        
        // third linear
        description=(EditText)findViewById(R.id.description);
        parcel=(CheckBox)findViewById(R.id.parcel);
        meet=(CheckBox)findViewById(R.id.meet);
        
        // apply
        apply=(Button)findViewById(R.id.apply);
        apply.setOnClickListener(this);

    }
    
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		switch(checkedId) {
		case R.id.sell: saleMode=BookData.SALEMODE_SELL; break;
		case R.id.buy: saleMode=BookData.SALEMODE_BUY; break;
		}
	}

	private class BookInfoGetter extends AsyncTask<Void,Void,Boolean> {
		
		private class BookInfo {
			public String image;
			public String title;
			public String publisher;
			public String author;
			public String pubdate;
			public String edition;
			public String price;
		}
		private BookInfo data;

		@Override
		protected void onPreExecute() {
			pdl=ProgressDialog.show(RegisterBook.this,"Loading","Loading...",true,false);
			data=null;
			super.onPreExecute();
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			
			try {
				
				// create url
				String url=getResources().getString(R.string.bookinfogetter_url);
				ArrayList<String> getData=new ArrayList<String>();
				getData.add("key="+getResources().getString(R.string.bookinfogetter_key));
				getData.add("query=art");
				getData.add("display=10");	
				getData.add("start=1");	
				getData.add("target=book_adv");	
				getData.add("d_isbn="+isbn);	
				url=url+'?';
				for(int i=0;i<getData.size();i++) {
					url=url+getData.get(i);
					if(i != getData.size()-1) url=url+'&';
					else url=url+'/';
				}
				System.out.println("XML URL : "+url);

				HttpGet request=new HttpGet(url);
				InputStreamReader isr=NetworkSupporter.getStreamFromRequest(request);
				Element item=NetworkSupporter.getXmlElementsFromStream(isr).get(0);
				data=new BookInfo();
				data.image=item.getChildText("image");
				data.title=item.getChildText("title");
				data.publisher=item.getChildText("publisher");
				data.author=item.getChildText("author");
				data.pubdate=item.getChildText("pubdate");
				data.edition=""+1;
				data.price=item.getChildText("price");
				isr.close();
				
				return true;
				
			} catch(Exception e) {
				e.printStackTrace();
				return false;
			}
		}

		@Override
		protected void onPostExecute(Boolean result) {
			
			pdl.dismiss();
			if(result==false) {
				Toast.makeText(RegisterBook.this,"일치하는 책을 찾지 못했습니다. 다시 시도해보세요.",Toast.LENGTH_SHORT).show();				
				return;
			}

			System.out.println("Image URL : "+data.image);
			new ImageSetterNoCache(data.image,image).execute();
			title.setText(data.title);
			publisher.setText(data.publisher);
			author.setText(data.author);
			pubdate.setText(data.pubdate);
			edition.setText(data.edition);
			original_price.setText(data.price);
			discount_price.requestFocus();
			
		}

	}

	// for imagesearch, apply button
	public void onClick(View v) {

		switch(v.getId()) {
		
		case R.id.imagesearch: {
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
			title.getText().toString().compareTo("")==0 ||
			publisher.getText().toString().compareTo("")==0 ||
			author.getText().toString().compareTo("")==0 ||
			pubdate.getText().toString().compareTo("")==0 ||
			edition.getText().toString().compareTo("")==0 ||
			original_price.getText().toString().compareTo("")==0 ||
			discount_price.getText().toString().compareTo("")==0 ) {
			Toast.makeText(this, "모든 데이터를 입력하여야 합니다.",Toast.LENGTH_SHORT).show();
			} else {
				new RegisterBookHelper().execute();
			} break;
		}
	}
    
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);
		
		if(resultCode==RESULT_OK) {
			switch(requestCode) {
			case REQUESTCODE_RANGE: {
				AppPref.getRangeSet().applyDataToView(region,univ,college,major,regionbtn,univbtn,collegebtn,majorbtn);
				getIntent().putExtra("range_changed",true);
				} break;
			case REQUESTCODE_BARCODE:
				isbn=data.getStringExtra("SCAN_RESULT");
				new BookInfoGetter().execute();
				break;
			case REQUESTCODE_CAPTUREIMAGE:
				image.setImageBitmap( (Bitmap)data.getExtras().get("data") );			
				break;
			case REQUESTCODE_GETIMAGE:
				try {
					image.setImageBitmap( Images.Media.getBitmap( getContentResolver(),data.getData() ) );
				} catch (FileNotFoundException e) {
					Toast.makeText(this,"FileNotFoundException",Toast.LENGTH_SHORT).show();
					e.printStackTrace();
				} catch (IOException e) {
					Toast.makeText(this,"IOException",Toast.LENGTH_SHORT).show();
					e.printStackTrace();
				}
				break;				
			}
		}
	}
	
	private class RegisterBookHelper extends AsyncTask<Void,Void,String> {
		
		@Override
		protected void onPreExecute() {
			pdl=ProgressDialog.show(RegisterBook.this,"Loading","Loading...",true,false);
			super.onPreExecute();

			image.setDrawingCacheEnabled(true);
		}

		@Override
		protected String doInBackground(Void... params) {
			
			// create url
			// login : 1.234.23.142/~ypunval/books/
			String url=getResources().getString(R.string.base_url)+'/'
					+getResources().getString(R.string.book_url)+'/';
			System.out.println("XML URL : "+url);
					
			try {				
				// create http post for sending
				HttpPost request=new HttpPost(url);
				
				// multipart entity
				MultipartEntity ment=new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
				// base data
				ment.addPart("title",new StringBody(title.getText().toString(),Charset.forName("UTF-8")));				
				ment.addPart("original_price",new StringBody(original_price.getText().toString()));
				ment.addPart("discount_price",new StringBody(discount_price.getText().toString()));
				ment.addPart("published",new StringBody(pubdate.getText().toString()));
				ment.addPart("edition",new StringBody(edition.getText().toString()));
				ment.addPart("publisher",new StringBody(publisher.getText().toString(),Charset.forName("UTF-8")));
				ment.addPart("book_author",new StringBody(author.getText().toString(),Charset.forName("UTF-8")));
				ment.addPart("content",new StringBody(description.getText().toString(),Charset.forName("UTF-8")));
				ment.addPart("region",new StringBody(""+AppPref.getRangeSet().get("region").id));
				ment.addPart("university",new StringBody(""+AppPref.getRangeSet().get("univ").id));
				ment.addPart("college",new StringBody(""+AppPref.getRangeSet().get("college").id));
				//ment.addPart("major",new StringBody(""+AppPref.getRangeSet().get("major").id));
				ment.addPart("sale",new StringBody(""+BookData.SALESTATE_ABLE));
				ment.addPart("parcel",new StringBody((parcel.isChecked())? "1":"0"));
				ment.addPart("meet",new StringBody((meet.isChecked())? "1":"0"));
				ment.addPart("country",new StringBody("1"));
				ment.addPart("sell",new StringBody(""+saleMode));
				ment.addPart("isbn",new StringBody(""+isbn));
				// image data
				Bitmap bit=image.getDrawingCache();
				if(bit != null) {
					// create temp image file
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

			if(result.compareTo("200")==0) {
				Toast.makeText(RegisterBook.this,"등록 성공",Toast.LENGTH_SHORT).show();
				setResult(RESULT_OK,getIntent());
				finish();
			} else {
				Toast.makeText(RegisterBook.this,result,Toast.LENGTH_SHORT).show();				
			}
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		switch (keyCode)
		{
		case KeyEvent.KEYCODE_BACK:
			// if you don't set result, returned Intent (when this activity is finished) is null.
			setResult(RESULT_CANCELED,getIntent());
			finish();
			return true;
		default:
			return false;
		}
	}
}
