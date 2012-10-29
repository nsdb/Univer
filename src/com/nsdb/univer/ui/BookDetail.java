package com.nsdb.univer.ui;

import com.fedorvlasov.lazylist.ImageLoader;
import com.nsdb.univer.common.AppPref;
import com.nsdb.univer.common.BookData;
import com.nsdb.univer.dataadapter.BookDataAdapter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

public class BookDetail extends Activity implements OnItemClickListener {

	BookData lastdata;
	
	ImageView image;
	EditText title,publisher,author,pubdate,edition,original_price,discount_price;

	EditText description;
	CheckBox parcel,meet;
	
	ListView lv;
	BookDataAdapter adapter;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bookdetail);
        
        // data
        lastdata=AppPref.getLastBookData();
        
        // second linear
        image=(ImageView)findViewById(R.id.image);
        title=(EditText)findViewById(R.id.title);
        publisher=(EditText)findViewById(R.id.publisher);
        author=(EditText)findViewById(R.id.author);
        pubdate=(EditText)findViewById(R.id.pubdate);
        edition=(EditText)findViewById(R.id.edition);
        original_price=(EditText)findViewById(R.id.original_price);
        discount_price=(EditText)findViewById(R.id.discount_price);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        if(lastdata.image.compareTo("")!=0) {
        	ImageLoader loader=new ImageLoader(this);
        	loader.DisplayImage(lastdata.image,image);
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
    	adapter=new BookDataAdapter(this,lv,true);
    	lv.setOnItemClickListener(this);
    	adapter.updateData("",BookDataAdapter.RANGEMODE_OTHER,BookData.SALEMODE_ALL,1,lastdata.seller_id);
    }

	public void onItemClick(AdapterView<?> parent, View view, int position, long l_position) {
		if(adapter.get(position).id != -1) {
			AppPref.setLastBookData(adapter.get(position));
			startActivity( new Intent("BookDetail") );
			finish();
		}
	}
}
