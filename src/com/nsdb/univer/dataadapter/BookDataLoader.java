package com.nsdb.univer.dataadapter;

import java.util.ArrayList;

import org.jdom2.Element;

import android.app.Activity;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.fedorvlasov.lazylist.ImageLoader;
import com.nsdb.univer.common.AppPref;
import com.nsdb.univer.common.BookData;
import com.nsdb.univer.R;
import com.woozzu.android.widget.RefreshableListView;

public class BookDataLoader extends BaseDataLoader<BookData> {
	
	private ImageLoader loader;

	//private String title;
	private int rangeMode;
	private int saleMode;
	private int pageNum;
	private int sellerId;

	public final static int RANGEMODE_ALL=0;
	public final static int RANGEMODE_REGION=1;
	public final static int RANGEMODE_UNIV=2;
	public final static int RANGEMODE_COLLEGE=3;
	public final static int RANGEMODE_MAJOR=4;
	public final static int RANGEMODE_MINE=5;
	public final static int RANGEMODE_OTHER=6;

	public BookDataLoader(Activity activity,ListView view, boolean inScrollView) {
		super(activity, R.layout.bookdata, view, inScrollView);
		loader=new ImageLoader(activity);
		//this.title="";
		this.rangeMode=-1;
		this.saleMode=-1;
		this.pageNum=1;
		this.sellerId=-1;
	}
	
	@Override
	protected BaseListAdapter<BookData> createListAdapter(Activity activity,
			int dataResourceId, ArrayList<BookData> dataVisible, ListView view) {
		return new BookListAdapter(activity,dataResourceId,dataVisible,view);
	}

	public void updateData(String title, int categorymode, int salemode, int pageNum) {
		//this.title=title;
		this.rangeMode=categorymode;
		this.saleMode=salemode;
		this.pageNum=pageNum;
		if(pageNum==1) init();
		super.updateData();
	}
	public void updateData(String title, int categorymode, int salemode, int pagenum, int sellerId) {
		this.sellerId=sellerId;
		updateData(title,categorymode,salemode,pagenum);
	}

	@Override
	protected String getXmlUrl() {
		
		// {base_url}/feeds/books?search=<search>&sale=<sale>&category=<category>&id=<id>&page=<page>/
		String url=getActivity().getResources().getString(R.string.base_url)+'/'
				+getActivity().getResources().getString(R.string.get_url)+'/'
				+getActivity().getResources().getString(R.string.book_url)+'/';
		ArrayList<String> getData=new ArrayList<String>();
		getData.add("search="+0);
		getData.add("sale="+saleMode);
		getData.add("category="+rangeMode);	
		switch(rangeMode) {
		case RANGEMODE_ALL: getData.add("id="+0); break;
		case RANGEMODE_REGION: getData.add("id="+AppPref.getRangeSet().get("region").id); break;
		case RANGEMODE_UNIV: getData.add("id="+AppPref.getRangeSet().get("univ").id); break;
		case RANGEMODE_COLLEGE: getData.add("id="+AppPref.getRangeSet().get("college").id); break;
		case RANGEMODE_MAJOR: getData.add("id="+AppPref.getRangeSet().get("major").id); break;
		case RANGEMODE_MINE: getData.add("id="+0); break;
		case RANGEMODE_OTHER: getData.add("id="+sellerId); break;
		}
		getData.add("page="+pageNum);
		for(int i=0;i<getData.size();i++) {
			url=url+getData.get(i);
			if(i != getData.size()-1) url=url+'&';
		}
		url=url+'/';
		
		System.out.println("XML URL : "+url);
		return url;
	}
	
	@Override
	protected BookData convertElement(Element item) {
		return new BookData(item);
	}
	
	@Override
	protected void updateView(int result) {
		super.updateView(result);		
		try {
			RefreshableListView rlv=(RefreshableListView)getView();
			if(rlv.isRefreshing())
				rlv.completeRefreshing();
		} catch(ClassCastException e) {
		}
	}

	private class BookListAdapter extends BaseListAdapter<BookData> {

		public BookListAdapter(Activity activity, int dataResourceId,
				ArrayList<BookData> objects, ListView view) {
			super(activity, dataResourceId, objects, view);
		}

		@Override
		protected void setView(int position, View v) {
			
			TextView t=(TextView)v.findViewById(R.id.title);
			ImageView i=(ImageView)v.findViewById(R.id.thumbnail);
			TextView dp=(TextView)v.findViewById(R.id.discount_price);
			TextView op=(TextView)v.findViewById(R.id.original_price);

			// title
			t.setText( get(position).title );
			
			// imageview
			if(get(position).thumbnail.compareTo("")!=0) {
				System.out.println("thumbnail : "+get(position).thumbnail.substring(1));
				loader.DisplayImage(getActivity().getResources().getString(R.string.base_url)+get(position).thumbnail,i);
			} else {
				i.setImageResource(R.drawable.ic_launcher);
			}
			
			// discount price
			if(get(position).discount_price!=-1) {
				dp.setText( ""+get(position).discount_price );
			} else {
				dp.setText( "" );
			}
			
			// original price
			if(get(position).original_price!=-1) {
				op.setText( ""+get(position).original_price );			
			} else {
				op.setText( "" );
			}
		}
		
	}

	public static int getDefaultRangeMode() {
		if(AppPref.getRangeSet().get("region").title.compareTo("")==0) {
			return RANGEMODE_ALL;
		} else if(AppPref.getRangeSet().get("univ").title.compareTo("")==0) {
			return RANGEMODE_REGION;
		} else if(AppPref.getRangeSet().get("college").title.compareTo("")==0) {
			return RANGEMODE_UNIV;
		} else if(AppPref.getRangeSet().get("major").title.compareTo("")==0) {
			return RANGEMODE_COLLEGE;
		} else {
			return RANGEMODE_MAJOR;
		}
	}

	
}
