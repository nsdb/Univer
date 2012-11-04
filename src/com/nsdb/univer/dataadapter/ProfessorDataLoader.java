package com.nsdb.univer.dataadapter;

import java.util.ArrayList;

import org.jdom2.Element;

import android.app.Activity;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.fedorvlasov.lazylist.ImageLoader;
import com.nsdb.univer.common.AppPref;
import com.nsdb.univer.common.ProfessorData;
import com.nsdb.univer.ui.R;
import com.woozzu.android.widget.RefreshableListView;

public class ProfessorDataLoader extends BaseDataLoader<ProfessorData> {

	private ImageLoader loader;
	
	//private String title;
	private int rangeMode;
	private int pageNum;
	
	private final static int RANGEMODE_ALL=0;
	private final static int RANGEMODE_REGION=1;
	private final static int RANGEMODE_UNIV=2;
	private final static int RANGEMODE_COLLEGE=3;
	private final static int RANGEMODE_MAJOR=4;

	public ProfessorDataLoader(Activity activity, ListView view, boolean inScrollView) {
		super(activity, R.layout.professordata, view, inScrollView);
		loader=new ImageLoader(getActivity());
		//this.title="";
		this.rangeMode=-1;
		this.pageNum=-1;
	}

	@Override
	protected BaseListAdapter<ProfessorData> createListAdapter(
			Activity activity, int dataResourceId,
			ArrayList<ProfessorData> dataVisible, ListView view) {
		return new ProfessorListAdapter(activity,dataResourceId,dataVisible,view);
	}

	public void updateData(String title, int pageNum) {
		//this.title=title;
		this.rangeMode=getDefaultRangeMode();
		this.pageNum=pageNum;
		if(pageNum==1) init();
		super.updateData();
	}
		
	@Override
	protected String getXmlUrl() {

		// {base_url}/feeds/professors/search=<search>&category=<category>&id=<id>&page=<page>/
		String url=getActivity().getResources().getString(R.string.base_url)+'/'
				+getActivity().getResources().getString(R.string.get_url)+'/'
				+getActivity().getResources().getString(R.string.professor_url)+'/';
		ArrayList<String> getData=new ArrayList<String>();
		getData.add("search="+0);
		getData.add("category="+rangeMode);	
		switch(rangeMode) {
		case RANGEMODE_ALL: getData.add("id="+0); break;
		case RANGEMODE_REGION: getData.add("id="+AppPref.getRangeSet().get("region").id); break;
		case RANGEMODE_UNIV: getData.add("id="+AppPref.getRangeSet().get("univ").id); break;
		case RANGEMODE_COLLEGE: getData.add("id="+AppPref.getRangeSet().get("college").id); break;
		case RANGEMODE_MAJOR: getData.add("id="+AppPref.getRangeSet().get("major").id); break;
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
	protected ProfessorData convertElement(Element item) {
		return new ProfessorData(item);
	}
	
	@Override
	protected void updateView(int result) {
		super.updateView(result);
		RefreshableListView rlv=(RefreshableListView)getView();
		if(rlv.isRefreshing())
			rlv.completeRefreshing();
	}

	private class ProfessorListAdapter extends BaseListAdapter<ProfessorData> {

		public ProfessorListAdapter(Activity activity, int dataResourceId,
				ArrayList<ProfessorData> objects, ListView view) {
			super(activity, dataResourceId, objects, view);
		}

		@Override
		protected void setView(int position, View v) {
			
			TextView t=(TextView)v.findViewById(R.id.title);
			ImageView i=(ImageView)v.findViewById(R.id.thumbnail);
			TextView rt=(TextView)v.findViewById(R.id.totaltxt);
			RatingBar r=(RatingBar)v.findViewById(R.id.total);
			
			// title
			t.setText( get(position).title );
			
			// imageview
			if(get(position).thumbnail.compareTo("")!=0) {
				System.out.println("thumbnail : "+get(position).thumbnail.substring(1));
				loader.DisplayImage(getActivity().getResources().getString(R.string.base_url)+get(position).thumbnail,i);
			} else {
				i.setImageResource(R.drawable.ic_launcher);
			}
			
			// ratingbar
			if(get(position).count > 0) {
				double d=get(position).total/(get(position).count*5);
				d=Math.round(d*100)/100;
				rt.setText(""+d);
				r.setRating( (float)d/2 );
			} else {
				rt.setText("0.0");
				r.setRating(0.0f);				
			}
		}
		
	}

	private int getDefaultRangeMode() {
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
