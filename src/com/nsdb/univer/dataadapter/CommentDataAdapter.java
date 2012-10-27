package com.nsdb.univer.dataadapter;

import java.util.ArrayList;

import org.jdom2.Element;

import android.app.Activity;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.nsdb.univer.common.CommentData;
import com.nsdb.univer.ui.R;

public class CommentDataAdapter extends BaseDataAdapter<CommentData> {

	int professor_id;
	int pageNum;

	public CommentDataAdapter(Activity activity, ListView view, boolean inScrollView) {
		super(activity, R.layout.commentdata, view, inScrollView);
		this.professor_id=-1;
		this.pageNum=-1;
	}

	public void updateData(int professor_id,int pageNum) {
		this.professor_id=professor_id;
		this.pageNum=pageNum;
		if(pageNum==1) init();
		super.updateData();
	}

	@Override
	protected String getXmlUrl() {

		// {base_url}/feeds/comments/professors/professor_id=<professor_id>&page=<page>/
		String url=activity.getResources().getString(R.string.base_url)+'/'
				+activity.getResources().getString(R.string.get_url)+'/'
				+activity.getResources().getString(R.string.comment_url)+'/'
				+activity.getResources().getString(R.string.professor_url)+'/';
		ArrayList<String> getData=new ArrayList<String>();
		getData.add("professor_id="+professor_id);
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
	protected CommentData convertElement(Element item) {
		
		return new CommentData( item.getChild("title").getText(),
		          		item.getChild("description").getText(),
		          		item.getChild("pubDate").getText() );
	}

	@Override
	protected void dataViewSetting(int position, View v) {
		
		TextView t=(TextView)v.findViewById(R.id.title);
		TextView d=(TextView)v.findViewById(R.id.description);
		//TextView p=(TextView)v.findViewById(R.id.pubdate);

		t.setText(get(position).title);
		d.setText(get(position).description);
		//p.setText(dataVisible.get(position).pubDate);
	}

}
