package com.nsdb.univer.ui;

import com.nsdb.univer.data.AppPref;
import com.nsdb.univer.data.BookData;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;

public class BookDetail extends Activity {

	BookData data;
	
	WebView image;
	EditText title,publisher,author,pubdate,edition,original_price,discount_price;

	EditText description;
	CheckBox parcel,meet;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bookdetail);
        
        // data
        data=AppPref.getLastBookData();
        
        // second linear
        image=(WebView)findViewById(R.id.image);
        title=(EditText)findViewById(R.id.title);
        publisher=(EditText)findViewById(R.id.publisher);
        author=(EditText)findViewById(R.id.author);
        pubdate=(EditText)findViewById(R.id.pubdate);
        edition=(EditText)findViewById(R.id.edition);
        original_price=(EditText)findViewById(R.id.original_price);
        discount_price=(EditText)findViewById(R.id.discount_price);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        image.loadUrl( getResources().getString(R.string.base_url)+data.image );
        title.setText(data.title);
        publisher.setText(data.publisher);
        author.setText(data.author);
        pubdate.setText(data.pubdate);
        edition.setText(""+data.edition);
        original_price.setText(""+data.original_price);
        discount_price.setText(""+data.discount_price);
        
        // third linear
        description=(EditText)findViewById(R.id.description);
        parcel=(CheckBox)findViewById(R.id.parcel);
        meet=(CheckBox)findViewById(R.id.meet);
        description.setText(data.description);
        if(data.parcel==1)
        	parcel.setChecked(true);
        if(data.meet==1)
        	meet.setChecked(true);
        

    }
}
