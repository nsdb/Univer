package com.nsdb.univer.dataadapter;

import java.util.ArrayList;

import org.jdom2.Element;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.fedorvlasov.lazylist.ImageLoader;
import com.nsdb.univer.R;
import com.nsdb.univer.common.AppPref;
import com.nsdb.univer.common.BookData;
import com.woozzu.android.widget.RefreshableListView;

public class BookDataAdapter extends DataLoadingArrayAdapter<BookData> {
	
	private ImageLoader loader;
	private int pageNum;

	public BookDataAdapter(Context context, ListView view) {
		super(context, R.layout.bookdata, view);
		loader=new ImageLoader(context);
		pageNum=0;
	}
	
	@Override
	protected void init() {
		super.init();
		pageNum=0;
	}
	public void updateData(String filterTitle, int categoryState, boolean init) {
		if(init) init();
		if(isLoadable()==false) return;
		// create Url : {base_url}/feeds/books?search=<search>&sale=<sale>&category=<category>&id=<id>&page=<page>/
		String url=getContext().getResources().getString(R.string.base_url)+'/'
				+getContext().getResources().getString(R.string.get_url)+'/'
				+getContext().getResources().getString(R.string.book_url)+'/';
		// combine start
		ArrayList<String> getData=new ArrayList<String>();
		// search
		getData.add("search="+0);
		// sale
		switch(categoryState) {
		case 0: getData.add("sale="+BookData.SALEMODE_SELL); break;
		case 1: getData.add("sale="+BookData.SALEMODE_BUY); break;
		case 2: getData.add("sale="+BookData.SALEMODE_ALL); break;
		}
		// category, id
		switch(categoryState) {
		case 0: case 1:
			if(AppPref.getRangeSet().get("region").title.compareTo("")==0) {
				getData.add("category="+BookData.RANGEMODE_ALL);
				getData.add("id="+0);
			} else if(AppPref.getRangeSet().get("univ").title.compareTo("")==0) {
				getData.add("category="+BookData.RANGEMODE_REGION);
				getData.add("id="+AppPref.getRangeSet().get("region").id);
			} else if(AppPref.getRangeSet().get("college").title.compareTo("")==0) {
				getData.add("category="+BookData.RANGEMODE_UNIV);
				getData.add("id="+AppPref.getRangeSet().get("univ").id);
			} else if(AppPref.getRangeSet().get("major").title.compareTo("")==0) {
				getData.add("category="+BookData.RANGEMODE_COLLEGE);
				getData.add("id="+AppPref.getRangeSet().get("college").id);
			} else {
				getData.add("category="+BookData.RANGEMODE_MAJOR);
				getData.add("id="+AppPref.getRangeSet().get("major").id);
			} break;
		case 2:
			getData.add("category="+BookData.RANGEMODE_MINE);
			getData.add("id="+0);
			break;
		}
		// pagenum
		getData.add("page="+(++pageNum));
		// combine end
		for(int i=0;i<getData.size();i++) {
			url=url+getData.get(i);
			if(i != getData.size()-1) url=url+'&';
		}
		url=url+'/';		
		System.out.println("XML URL : "+url);
		
		// go
		super.updateData(url);
		
	}
	public void updateData(int seller_id,boolean init) {
		if(init) init();
		if(isLoadable()==false) return;
		// create Url : {base_url}/feeds/books?search=<search>&sale=<sale>&category=<category>&id=<id>&page=<page>/
		String url=getContext().getResources().getString(R.string.base_url)+'/'
				+getContext().getResources().getString(R.string.get_url)+'/'
				+getContext().getResources().getString(R.string.book_url)+'/';
		// conbine start
		ArrayList<String> getData=new ArrayList<String>();
		getData.add("search="+0);
		getData.add("sale="+BookData.SALEMODE_ALL);
		getData.add("category="+BookData.RANGEMODE_OTHER);
		getData.add("id="+seller_id);
		getData.add("page="+(++pageNum));
		// combine end
		for(int i=0;i<getData.size();i++) {
			url=url+getData.get(i);
			if(i != getData.size()-1) url=url+'&';
		}
		url=url+'/';		
		System.out.println("XML URL : "+url);
		
		// go
		super.updateData(url);
		
	}
	@Override
	protected BookData convertElementToObject(Element item) {
		return new BookData(item);
	}
	@Override
	protected void applyUpdate() {
		super.applyUpdate();
		try {
			RefreshableListView rlv=(RefreshableListView)getListView();
			if(rlv.isRefreshing())
				rlv.completeRefreshing();
		} catch(ClassCastException e) {			
		}
	}

	@Override
	protected void setView(int position, View v) {
		
		TextView t=(TextView)v.findViewById(R.id.title);
		ImageView i=(ImageView)v.findViewById(R.id.thumbnail);
		TextView dp=(TextView)v.findViewById(R.id.discount_price);
		TextView op=(TextView)v.findViewById(R.id.original_price);

		// title
		t.setText( getItem(position).title );
		
		// imageview
		if(getItem(position).thumbnail.compareTo("")!=0) {
			System.out.println("thumbnail : "+getItem(position).thumbnail.substring(1));
			loader.DisplayImage(getContext().getResources().getString(R.string.base_url)+getItem(position).thumbnail,i);
		} else {
			i.setImageResource(R.drawable.ic_launcher);
		}
		
		// discount price
		if(getItem(position).discount_price!=-1) {
			dp.setText( ""+getItem(position).discount_price );
		} else {
			dp.setText( "" );
		}
		
		// original price
		if(getItem(position).original_price!=-1) {
			op.setText( ""+getItem(position).original_price );			
		} else {
			op.setText( "" );
		}
		
	}

}
