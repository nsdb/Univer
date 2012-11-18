package com.nsdb.univer.supporter.adapter;

import java.util.ArrayList;

import org.jdom2.Element;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.fedorvlasov.lazylist.ImageLoader;
import com.nsdb.univer.R;
import com.nsdb.univer.data.BookData;
import com.nsdb.univer.data.ProfessorData;
import com.nsdb.univer.supporter.data.AppPref;
import com.woozzu.android.widget.RefreshableListView;

public class ProfessorDataAdapter extends DataLoadingArrayAdapter<ProfessorData> {

	private ImageLoader loader;
	private int pageNum;

	public ProfessorDataAdapter(Context context, ListView view) {
		super(context, R.layout.professordata, view);
		loader=new ImageLoader(context);
		pageNum=0;
	}

	@Override
	protected void init() {
		super.init();
		pageNum=0;
	}
	public void updateData(String filterTitle, boolean init) {
		if(init) init();
		if(isLoadable()==false) return;
		// create url : {base_url}/feeds/professors/search=<search>&category=<category>&id=<id>&page=<page>/
		String url=getContext().getResources().getString(R.string.base_url)+'/'
				+getContext().getResources().getString(R.string.get_url)+'/'
				+getContext().getResources().getString(R.string.professor_url)+'/';
		// combine start
		ArrayList<String> getData=new ArrayList<String>();
		getData.add("search="+0);
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
		}
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
	protected ProfessorData convertElementToObject(Element item) {
		return new ProfessorData(item);
	}
	@Override
	protected void applyUpdate() {
		super.applyUpdate();
		RefreshableListView rlv=(RefreshableListView)getListView();
		if(rlv.isRefreshing())
			rlv.completeRefreshing();
	}

	@Override
	protected void setView(int position, View v) {
		
		ImageView i=(ImageView)v.findViewById(R.id.thumbnail);
		TextView t=(TextView)v.findViewById(R.id.title);
		RatingBar r=(RatingBar)v.findViewById(R.id.total);
		TextView u=(TextView)v.findViewById(R.id.univ);
		TextView c=(TextView)v.findViewById(R.id.college);
		TextView l=(TextView)v.findViewById(R.id.like);
		TextView dl=(TextView)v.findViewById(R.id.dislike);
		TextView co=(TextView)v.findViewById(R.id.comment_count);
		
		
		// imageview
		if(getItem(position).thumbnail.compareTo("")!=0) {
			System.out.println("thumbnail : "+getItem(position).thumbnail.substring(1));
			loader.DisplayImage(getContext().getResources().getString(R.string.base_url)+getItem(position).thumbnail,i);
		} else {
			i.setImageResource(R.drawable.ic_launcher);
		}
		
		// title
		t.setText( getItem(position).title );

		// ratingbar
		if(getItem(position).count > 0) {
			double d=getItem(position).total/(getItem(position).count*5);
			d=Math.round(d*100)/100;
			r.setRating( (float)d/2 );
		} else {
			r.setRating(0.0f);				
		}

		// univ
		u.setText( getItem(position).university );

		// title
		c.setText( getItem(position).college );

		// title
		l.setText( ""+getItem(position).like );

		// title
		dl.setText( ""+getItem(position).dislike );
		
		// title
		co.setText( ""+getItem(position).comment_count );
	}
	

}
