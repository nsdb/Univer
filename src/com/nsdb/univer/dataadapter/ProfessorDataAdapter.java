package com.nsdb.univer.dataadapter;

import java.util.ArrayList;

import org.jdom2.Element;

import android.app.Activity;
import android.view.View;
import android.webkit.WebView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.nsdb.univer.common.AppPref;
import com.nsdb.univer.common.ProfessorData;
import com.nsdb.univer.ui.R;

public class ProfessorDataAdapter extends BaseDataAdapter<ProfessorData> {

	//private String title;
	private int rangeMode;
	private int pageNum;
	
	private final static int RANGEMODE_ALL=0;
	private final static int RANGEMODE_REGION=1;
	private final static int RANGEMODE_UNIV=2;
	private final static int RANGEMODE_COLLEGE=3;
	private final static int RANGEMODE_MAJOR=4;

	public ProfessorDataAdapter(Activity activity, ListView view, boolean inScrollView) {
		super(activity, R.layout.professordata, view, inScrollView);
		//this.title="";
		this.rangeMode=-1;
		this.pageNum=-1;
	}

	public void updateData(String title, int pageNum) {
		//this.title=title;
		this.rangeMode=getDefaultRangeMode();
		this.pageNum=pageNum;
		super.updateData(pageNum==1);
	}
		
		
	@Override
	protected ProfessorData getReadyData() {
		return new ProfessorData("불러오는 중");
	}

	@Override
	protected ProfessorData getEndData(int result) {

		switch(result) {
		case RESULT_EMPTY:
			return new ProfessorData("데이터 없음");
		case RESULT_ERROR:
			return new ProfessorData("에러 발생. 읽기 실패");
		case RESULT_SUCCESS:
			return new ProfessorData("읽기 성공");
		default:
			return null;
		}
		
	}

	@Override
	protected String getXmlUrl() {

		// {base_url}/feeds/professors/search=<search>&category=<category>&id=<id>&page=<page>/
		String url=activity.getResources().getString(R.string.base_url)+'/'
				+activity.getResources().getString(R.string.get_url)+'/'
				+activity.getResources().getString(R.string.professor_url)+'/';
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
	protected void dataViewSetting(int position, View v) {

		TextView t=(TextView)v.findViewById(R.id.title);
		WebView w=(WebView)v.findViewById(R.id.thumbnail);
		TextView rt=(TextView)v.findViewById(R.id.totaltxt);
		RatingBar r=(RatingBar)v.findViewById(R.id.total);
		
		// title
		t.setText( get(position).title );
		
		// webview
		w.setFocusable(false);
		if(get(position).thumbnail.compareTo("")!=0) {
			System.out.println("thumbnail : "+get(position).thumbnail.substring(1));
			w.loadUrl( activity.getResources().getString(R.string.base_url)+get(position).thumbnail );
		} else {
			w.clearView();
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
