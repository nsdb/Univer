package com.nsdb.univer.ui;

import java.util.ArrayList;

import com.nsdb.univer.data.AppPref;
import com.nsdb.univer.data.BookData;
import com.nsdb.univer.uisupporter.BookDataAdapter;
import com.nsdb.univer.uisupporter.OnClickMover;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;

public class BookDetail extends Activity implements OnItemClickListener {

	BookData data;
	
	WebView image;
	EditText title,publisher,author,pubdate,edition,original_price,discount_price;

	EditText description;
	CheckBox parcel,meet;
	
	ArrayList<BookData> datalist;
	BookDataAdapter adapter;
	ListView lv;
	
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
        if(data.image.compareTo("")!=0)
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
        
        // ListView
    	lv=(ListView)findViewById(R.id.booklist);
    	datalist=new ArrayList<BookData>();
    	adapter=new BookDataAdapter(this,datalist,lv);
    	lv.setAdapter(adapter);
    	lv.setOnItemClickListener(this);
    	adapter.updateData("",BookDataAdapter.RANGEMODE_OTHER,BookData.SALEMODE_ALL,1,data.seller_id);

    }

	public void onItemClick(AdapterView<?> parent, View view, int position, long l_position) {
		if(datalist.get(position).id != -1) {
			AppPref.setLastBookData(datalist.get(position));
			OnClickMover.moveActivity(this,"BookDetail","");
			finish();
		}
	}
}
