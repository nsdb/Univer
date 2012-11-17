package com.nsdb.univer.supporter.adapter;

import java.util.ArrayList;

import org.jdom2.Element;

import com.nsdb.univer.R;
import com.nsdb.univer.data.CommentData;

import android.content.Context;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

// almost same as ProfessorCommentDataAdapter.java

public class BoardCommentDataAdapter extends DataLoadingArrayAdapter<CommentData> {
	
	private int pageNum;

	public BoardCommentDataAdapter(Context context, ListView view) {
		super(context, R.layout.commentdata, view);
		pageNum=0;
	}
	
	@Override
	protected void init() {
		super.init();
		pageNum=0;
	}
	public void updateData(int writer_id,boolean init) {
		if(init) init();
		if(isLoadable()==false) return;
		// {base_url}/feeds/comments/entry/entry_id=<entry_id>&page=<page>/
		String url=getContext().getResources().getString(R.string.base_url)+'/'
				+getContext().getResources().getString(R.string.get_url)+'/'
				+getContext().getResources().getString(R.string.boardcomment_url)+'/';
		ArrayList<String> getData=new ArrayList<String>();
		getData.add("entry_id="+writer_id);
		getData.add("page="+(++pageNum));
		for(int i=0;i<getData.size();i++) {
			url=url+getData.get(i);
			if(i != getData.size()-1) url=url+'&';
		}
		url=url+'/';		
		System.out.println("XML URL : "+url);
		
		super.updateData(url);
	}
	@Override
	protected CommentData convertElementToObject(Element item) {
		return new CommentData( item.getChild("title").getText(),
          		item.getChild("description").getText(),
          		item.getChild("pubDate").getText() );
	}

	@Override
	protected void setView(int position, View v) {
		
		TextView t=(TextView)v.findViewById(R.id.title);
		TextView d=(TextView)v.findViewById(R.id.description);
		//TextView p=(TextView)v.findViewById(R.id.pubdate);

		t.setText(getItem(position).title);
		d.setText(getItem(position).description);
		//p.setText(dataVisible.get(position).pubDate);
	}

}
