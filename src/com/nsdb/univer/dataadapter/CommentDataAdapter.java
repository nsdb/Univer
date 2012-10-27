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
		super.updateData(pageNum==1);
	}

	@Override
	protected CommentData getReadyData() {
		return new CommentData("불러오는 중");
	}

	@Override
	protected CommentData getEndData(int result) {

		switch(result) {
		case RESULT_EMPTY:
			return new CommentData("데이터 없음");
		case RESULT_ERROR:
			return new CommentData("에러 발생. 읽기 실패");
		case RESULT_SUCCESS:
			return new CommentData("읽기 성공");
		default:
			return null;
		}
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
