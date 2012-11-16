package com.nsdb.univer.ui.activity;

import java.io.InputStreamReader;
import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

import com.fedorvlasov.lazylist.ImageLoader;
import com.makeramen.segmented.SegmentedRadioGroup;
import com.nsdb.univer.R;
import com.nsdb.univer.data.BookData;
import com.nsdb.univer.supporter.adapter.BookDataAdapter;
import com.nsdb.univer.supporter.data.AppPref;
import com.nsdb.univer.supporter.network.NetworkSupporter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.RadioGroup.OnCheckedChangeListener;

public class BookDetail extends IntentPreservingActivity implements OnItemClickListener, OnCheckedChangeListener, OnClickListener {

    // data
	BookData lastdata;
	
    // second linear
	SegmentedRadioGroup sale;
	ImageView image;
	EditText title,publisher,author,pubdate,edition,original_price,discount_price;

    // third linear
	EditText description;
	CheckBox parcel,meet;
	
    // ListView
	ListView lv;
	BookDataAdapter adapter;
	
	// editbar
	LinearLayout editbar;
	SegmentedRadioGroup state;
	Button delete;
	ProgressDialog pdl;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // combine layout
        setContentView(R.layout.bookdetail_part2);
        View part1=((LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE))
				.inflate(R.layout.bookdetail_part1,null);
        ((ListView)findViewById(R.id.booklist)).addHeaderView(part1);

        // data
        lastdata=AppPref.getLastBookData();
        
        // second linear
        sale=(SegmentedRadioGroup)findViewById(R.id.sale);
        image=(ImageView)findViewById(R.id.image);
        title=(EditText)findViewById(R.id.title);
        publisher=(EditText)findViewById(R.id.publisher);
        author=(EditText)findViewById(R.id.author);
        pubdate=(EditText)findViewById(R.id.pubdate);
        edition=(EditText)findViewById(R.id.edition);
        original_price=(EditText)findViewById(R.id.original_price);
        discount_price=(EditText)findViewById(R.id.discount_price);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        switch(lastdata.sell) {
        case BookData.SALEMODE_BUY: sale.check(R.id.buy); break;
        case BookData.SALEMODE_SELL: sale.check(R.id.sell); break;
        }
        if(lastdata.image.compareTo("")!=0) {
        	ImageLoader loader=new ImageLoader(this);
        	loader.DisplayImage(getResources().getString(R.string.base_url)+lastdata.image,image);
        }
        title.setText(lastdata.title);
        publisher.setText(lastdata.publisher);
        author.setText(lastdata.author);
        pubdate.setText(lastdata.pubdate);
        edition.setText(""+lastdata.edition);
        original_price.setText(""+lastdata.original_price);
        discount_price.setText(""+lastdata.discount_price);
        
        // third linear
        description=(EditText)findViewById(R.id.description);
        parcel=(CheckBox)findViewById(R.id.parcel);
        meet=(CheckBox)findViewById(R.id.meet);
        description.setText(lastdata.description);
        if(lastdata.parcel==1)
        	parcel.setChecked(true);
        if(lastdata.meet==1)
        	meet.setChecked(true);
        
        // ListView
    	lv=(ListView)findViewById(R.id.booklist);
    	if(getIntent().getBooleanExtra("mine",false)==false) {
	    	adapter=new BookDataAdapter(this,lv);
	    	lv.setOnItemClickListener(this);
	    	adapter.updateData(lastdata.seller_id,true);
    	} else {
    		TextView otherlinear=(TextView)findViewById(R.id.otherlinear);
    		otherlinear.setVisibility(View.GONE);    		
	    	lv=(ListView)findViewById(R.id.booklist);
    		lv.setVisibility(View.GONE);
    	}
    	
    	// editbar
    	if(getIntent().getBooleanExtra("mine",false)==true) {
    		editbar=(LinearLayout)findViewById(R.id.editbar);
    		editbar.setVisibility(View.VISIBLE);
    		System.out.println("EditBar Visible");
    		state=(SegmentedRadioGroup)findViewById(R.id.state);
    		delete=(Button)findViewById(R.id.delete);
    		switch(lastdata.sale) {
    		case BookData.SALESTATE_ABLE: state.check(R.id.able); break;
    		case BookData.SALESTATE_BOOKED: state.check(R.id.booked); break;
    		case BookData.SALESTATE_DONE: state.check(R.id.done); break;
    		}
    		state.setOnCheckedChangeListener(this);
    		delete.setOnClickListener(this);
    	}
    }

	public void onItemClick(AdapterView<?> parent, View view, int position, long l_position) {
		if(adapter.getItem(position) != null) {
			AppPref.setLastBookData(adapter.getItem(position));
			startActivity( new Intent("BookDetail") );
			finish();
		}
	}

	public void onCheckedChanged(RadioGroup group, int checkedId) {
		
		switch(checkedId) {
		case R.id.able: new BookEditHelper("PUT",BookData.SALESTATE_ABLE).execute(); break;
		case R.id.booked: new BookEditHelper("PUT",BookData.SALESTATE_BOOKED).execute(); break;
		case R.id.done: new BookEditHelper("PUT",BookData.SALESTATE_DONE).execute(); break;		
		}
		
	}

	public void onClick(View v) {
		new BookEditHelper("DELETE").execute();
	}
	
	
	private class BookEditHelper extends AsyncTask<Void,Void,String> {
		
		private String method;
		private int value;
		
		public BookEditHelper(String method,int value) {
			this.method=method;
			this.value=value;
		}
		public BookEditHelper(String method) {
			this.method=method;
			this.value=-1;
		}
		
		
		@Override
		protected void onPreExecute() {
			pdl=ProgressDialog.show(BookDetail.this,"Loading","Loading...",true,false);
			super.onPreExecute();
		}

		@Override
		protected String doInBackground(Void... params) {
			
			// create url
			String url=getResources().getString(R.string.base_url)+'/';
			if(method.compareTo("PUT")==0)
				url=url+getResources().getString(R.string.editbook_url)+'/';
			else if(method.compareTo("DELETE")==0)
				url=url+getResources().getString(R.string.deletebook_url)+'/';
			else
				return null;
				
			System.out.println("XML URL : "+url);
					
			try {
				// create http post for sending
				HttpPost request=new HttpPost(url);
				ArrayList<NameValuePair> postdata=new ArrayList<NameValuePair>();
				if(method.compareTo("PUT")==0) {
					postdata.add(new BasicNameValuePair("id",""+lastdata.id));
					postdata.add(new BasicNameValuePair("sale",""+value));
				} else if(method.compareTo("DELETE")==0) {
					postdata.add(new BasicNameValuePair("id",""+lastdata.id));
				}
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
				Toast.makeText(BookDetail.this,"설정 성공",Toast.LENGTH_SHORT).show();
				
				// notify to BookMarketMain
				getIntent().putExtra("edited",true);
				// if deleted
				if(method.compareTo("DELETE")==0) {
					setResult(RESULT_OK,getIntent());
					finish();
				}

			} else {
				Toast.makeText(BookDetail.this,result,Toast.LENGTH_SHORT).show();				
			}
		}
	}

}
