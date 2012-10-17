package com.nsdb.univer.ui;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;

import com.nsdb.univer.data.AppPref;
import com.nsdb.univer.data.BookData;
import com.nsdb.univer.uisupporter.BookDataAdapter;
import com.nsdb.univer.uisupporter.OnClickMover;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;

public class RegisterBook extends Activity implements OnClickListener, OnCheckedChangeListener {

	
	RadioGroup sale;
	int saleMode;
	TextView regiontxt,univtxt,collegetxt,majortxt;
	Button region,univ,college,major;
	
	Button barcode;
	String isbn;
	Button imagesearch;
	ImageView image;
	EditText title,publisher,author,pubdate,edition,original_price,discount_price;
	private final static int REQUESTCODE_BARCODE=1;
	
	EditText description;
	CheckBox parcel,meet;
	
	Button apply;	
	ProgressDialog pdl;
	private final static int REGISTERBOOK_SUCCESS=200;

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registerbook);
        
        // first linear
        sale=(RadioGroup)findViewById(R.id.sale);
        saleMode=BookData.SALEMODE_SELL;
        sale.setOnCheckedChangeListener(this);
        regiontxt=(TextView)findViewById(R.id.regiontxt);
        univtxt=(TextView)findViewById(R.id.univtxt);
        collegetxt=(TextView)findViewById(R.id.collegetxt);
        majortxt=(TextView)findViewById(R.id.majortxt);
        region=(Button)findViewById(R.id.region);
        univ=(Button)findViewById(R.id.univ);
        college=(Button)findViewById(R.id.college);
        major=(Button)findViewById(R.id.major);
        region.setOnClickListener(new OnClickMover(this,"RangeSetting","region"));
        univ.setOnClickListener(new OnClickMover(this,"RangeSetting","univ"));
        college.setOnClickListener(new OnClickMover(this,"RangeSetting","college"));
        major.setOnClickListener(new OnClickMover(this,"RangeSetting","major"));
        
        // second linear
        barcode=(Button)findViewById(R.id.barcode);
        isbn="1";
        imagesearch=(Button)findViewById(R.id.imagesearch);
        image=(ImageView)findViewById(R.id.image);
        title=(EditText)findViewById(R.id.title);
        publisher=(EditText)findViewById(R.id.publisher);
        author=(EditText)findViewById(R.id.author);
        pubdate=(EditText)findViewById(R.id.pubdate);
        edition=(EditText)findViewById(R.id.edition);
        original_price=(EditText)findViewById(R.id.original_price);
        discount_price=(EditText)findViewById(R.id.discount_price);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        barcode.setOnClickListener(new OnClickMover(this,"com.google.zxing.client.android.SCAN","",REQUESTCODE_BARCODE));
        imagesearch.setOnClickListener(this);
        
        // third linear
        description=(EditText)findViewById(R.id.description);
        parcel=(CheckBox)findViewById(R.id.parcel);
        meet=(CheckBox)findViewById(R.id.meet);
        
        // apply
        apply=(Button)findViewById(R.id.apply);
        apply.setOnClickListener(this);

    }
    
    @Override
    public void onResume() {
    	super.onResume();

    	// refresh position
    	regiontxt.setText(AppPref.getRangeData("region").title);
    	if(regiontxt.getText().length()==0) {
    		regiontxt.setText("없음");
    		univ.setEnabled(false);
    	} else {
    		univ.setEnabled(true);    		
    	}
    	univtxt.setText(AppPref.getRangeData("univ").title);
    	if(univtxt.getText().length()==0) {
    		univtxt.setText("없음");
			college.setEnabled(false);
		} else {
			college.setEnabled(true);    		
		}
    	collegetxt.setText(AppPref.getRangeData("college").title);
    	if(collegetxt.getText().length()==0) {
    		collegetxt.setText("없음");
			major.setEnabled(false);
		} else {
			major.setEnabled(true);    		
		}
    	majortxt.setText(AppPref.getRangeData("major").title);
    	if(majortxt.getText().length()==0)
    		majortxt.setText("없음");
    	////

    	// Not yet
    	major.setEnabled(false);
    }

	public void onCheckedChanged(RadioGroup group, int checkedId) {
		switch(checkedId) {
		case R.id.sell: saleMode=BookData.SALEMODE_SELL; break;
		case R.id.buy: saleMode=BookData.SALEMODE_BUY; break;
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);
		
		if(resultCode==RESULT_OK) {
			isbn=data.getStringExtra("SCAN_RESULT");
			new BookInfoGetter().execute();
		}
	}
	
	private class BookInfoGetter extends AsyncTask<Void,Void,String[]> {

		@Override
		protected void onPreExecute() {
			pdl=ProgressDialog.show(RegisterBook.this,"Loading","Loading...",true,false);
			super.onPreExecute();
		}

		@Override
		protected String[] doInBackground(Void... params) {
			
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
			
			try {
				// get xml stream from server
				HttpGet request=new HttpGet(url);
				HttpClient client=new DefaultHttpClient();
				HttpResponse response=client.execute(request);
				InputStream is=response.getEntity().getContent();
				InputStreamReader isr=new InputStreamReader(is,"utf-8");

				// get bookdata from xml through JDOM
				SAXBuilder sax=new SAXBuilder();
				Document doc=sax.build(isr);
				Element rss=doc.getRootElement();
				Element channel=rss.getChild("channel");
				Element item=channel.getChild("item");
				String[] result=new String[7];
				//image_url,title,publisher,author,pubdate,edition,original_price
				result[0]=item.getChildText("image");
				result[1]=item.getChildText("title");
				result[2]=item.getChildText("publisher");
				result[3]=item.getChildText("author");
				result[4]=item.getChildText("pubdate");
				result[5]=""+1;
				result[6]=item.getChildText("price");				
				return result;
				
			} catch(Exception e) {
				e.printStackTrace();
				return null;
			}	
			
		}
		
		@Override
		protected void onPostExecute(String[] result) {
			
			image.setImageURI(Uri.parse(result[0]));
			title.setText(result[1]);
			publisher.setText(result[2]);
			author.setText(result[3]);
			pubdate.setText(result[4]);
			edition.setText(result[5]);
			original_price.setText(result[6]);
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
						startActivityForResult( new Intent(Intent.ACTION_GET_CONTENT).setType("image/-"),
												REQUESTCODE_GETIMAGE );
						break;
					}
					dialog.cancel();
				}
			});
			ab.show();			
			} break;
			

		case R.id.apply:
			if(title.getText().toString().compareTo("")==0 ||
			publisher.getText().toString().compareTo("")==0 ||
			author.getText().toString().compareTo("")==0 ||
			pubdate.getText().toString().compareTo("")==0 ||
			edition.getText().toString().compareTo("")==0 ||
			original_price.getText().toString().compareTo("")==0 ||
			discount_price.getText().toString().compareTo("")==0 ) {
			Toast.makeText(this, "도서정보의 모든 데이터를 입력하여야 합니다.",Toast.LENGTH_SHORT).show();
			} else {
				new RegisterBookHelper().execute();
			} break;
		}
	}
    
	private class RegisterBookHelper extends AsyncTask<Void,Void,Integer> {

		@Override
		protected void onPreExecute() {
			pdl=ProgressDialog.show(RegisterBook.this,"Loading","Loading...",true,false);
			super.onPreExecute();
		}

		@Override
		protected Integer doInBackground(Void... params) {
			
			// create url
			// login : 1.234.23.142/~ypunval/books/
			String url=getResources().getString(R.string.base_url)+'/'
					+getResources().getString(R.string.book_url)+'/';
			System.out.println("XML URL : "+url);
					
			try {				
				// create http post for sending
				HttpPost request=new HttpPost(url);
				ArrayList<NameValuePair> postdata=new ArrayList<NameValuePair>();
				postdata.add(new BasicNameValuePair("title", title.getText().toString()));
				postdata.add(new BasicNameValuePair("original_price", original_price.getText().toString()));
				postdata.add(new BasicNameValuePair("discount_price", discount_price.getText().toString()));
				postdata.add(new BasicNameValuePair("published", pubdate.getText().toString()));
				postdata.add(new BasicNameValuePair("edition", edition.getText().toString()));
				postdata.add(new BasicNameValuePair("publisher", publisher.getText().toString()));
				postdata.add(new BasicNameValuePair("book_author", author.getText().toString()));
				postdata.add(new BasicNameValuePair("content", description.getText().toString()));
				postdata.add(new BasicNameValuePair("region", ""+AppPref.getRangeData("region").id));
				postdata.add(new BasicNameValuePair("university", ""+AppPref.getRangeData("univ").id));
				postdata.add(new BasicNameValuePair("college", ""+AppPref.getRangeData("college").id));
				postdata.add(new BasicNameValuePair("major", ""+AppPref.getRangeData("major").id));
				postdata.add(new BasicNameValuePair("sale", ""+BookData.SALESTATE_ABLE));
				postdata.add(new BasicNameValuePair("parcel", (parcel.isChecked())? "1":"0"));
				postdata.add(new BasicNameValuePair("meet", (meet.isChecked())? "1":"0"));
				postdata.add(new BasicNameValuePair("country", "1"));
				postdata.add(new BasicNameValuePair("sell", ""+saleMode));
				postdata.add(new BasicNameValuePair("isbn", ""+isbn));
				UrlEncodedFormEntity ent = new UrlEncodedFormEntity(postdata,HTTP.UTF_8);
				request.setEntity(ent);

				// cookie load
				HttpClient client=new DefaultHttpClient();
				CookieStore cookieStore=((DefaultHttpClient)client).getCookieStore();
				List<Cookie> cookieList=cookieStore.getCookies();
				String cookieName=AppPref.getString("cookieName");
				if(cookieList.size()==0 && cookieName.compareTo("")!=0) {
					String cookieValue=AppPref.getString("cookieValue");
					String cookieDomain=AppPref.getString("cookieDomain");
					String cookiePath=AppPref.getString("cookiePath");
					BasicClientCookie cookie=new BasicClientCookie( cookieName,cookieValue );
					cookie.setDomain(cookieDomain);
					cookie.setPath(cookiePath);
					cookieStore.addCookie(cookie);
				}

				// get data from server
				HttpResponse response=client.execute(request);
				InputStream is=response.getEntity().getContent();
				InputStreamReader isr=new InputStreamReader(is);
				BufferedReader br=new BufferedReader(isr);
				String result="";
				String temp=br.readLine();
				while(temp!=null) {
					result=temp;
					System.out.println("Result : "+result);
					temp=br.readLine();
				}
				
				// cookie save
				AppPref.setString("cookieName",cookieList.get(0).getName());
				AppPref.setString("cookieValue",cookieList.get(0).getValue());
				AppPref.setString("cookieDomain",cookieList.get(0).getDomain());
				AppPref.setString("cookiePath",cookieList.get(0).getPath());
				
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
			case REGISTERBOOK_SUCCESS:
				Toast.makeText(RegisterBook.this,"등록 성공",Toast.LENGTH_SHORT).show();
				setResult(RESULT_OK,getIntent());
				finish();
				break;
			default:
				Toast.makeText(RegisterBook.this,"등록 실패",Toast.LENGTH_SHORT).show();
				break;
			}
		}
	}

}
