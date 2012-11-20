package com.nsdb.univer.supporter.adapter;

import java.io.InputStreamReader;
import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.jdom2.Element;

import com.fedorvlasov.lazylist.ImageLoader;
import com.nsdb.univer.R;
import com.nsdb.univer.data.BoardData;
import com.nsdb.univer.data.BookData;
import com.nsdb.univer.supporter.NetworkSupporter;
import com.nsdb.univer.supporter.data.AppPref;
import com.woozzu.android.widget.RefreshableListView;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class BoardDataAdapter extends DataLoadingArrayAdapter<BoardData> {

	private ImageLoader loader;
	private int pageNum;
	
	public BoardDataAdapter(Context context, ListView view) {
		super(context, R.layout.boarddata, view);
		loader=new ImageLoader(context);
		pageNum=0;
	}

	@Override
	protected void init() {
		super.init();
		pageNum=0;
	}
	public void updateData(boolean init) {
		if(init) init();
		if(isLoadable()==false) return;
		// {base_url}/feeds/entries/category=<category>&id=<id>&page=<page>/
		String url=getContext().getResources().getString(R.string.base_url)+'/'
				+getContext().getResources().getString(R.string.get_url)+'/'
				+getContext().getResources().getString(R.string.board_url)+'/';
		// combine start
		ArrayList<String> getData=new ArrayList<String>();
		// category, id
		if(AppPref.getRangeSet().get("region").title.compareTo("")==0) {
			getData.add("category="+BookData.RANGEMODE_ALL);
			getData.add("id="+0);
		} else if(AppPref.getRangeSet().get("univ").title.compareTo("")==0) {
			getData.add("category="+BookData.RANGEMODE_REGION);
			getData.add("id="+AppPref.getRangeSet().get("region").id);
		} else {
			getData.add("category="+BookData.RANGEMODE_UNIV);
			getData.add("id="+AppPref.getRangeSet().get("univ").id);
		}
		// page
		getData.add("page="+(++pageNum));
		// combine end
		for(int i=0;i<getData.size();i++) {
			url=url+getData.get(i);
			if(i != getData.size()-1) url=url+'&';
		}
		url=url+'/';		
		System.out.println("XML URL : "+url);
		
		super.updateData(url);
	}
	@Override
	protected BoardData convertElementToObject(Element item) {
		return new BoardData(item);
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
		TextView p=(TextView)v.findViewById(R.id.pubdate);
		TextView d=(TextView)v.findViewById(R.id.description);
		TextView l=(TextView)v.findViewById(R.id.like);
		TextView c=(TextView)v.findViewById(R.id.comment);
		TextView ln=(TextView)v.findViewById(R.id.likenum);
		TextView cn=(TextView)v.findViewById(R.id.commentnum);
		ImageView i=(ImageView)v.findViewById(R.id.image);

		t.setText(getItem(position).title);
		p.setText(getItem(position).pubDate);
		d.setText(getItem(position).description);
		l.setOnClickListener(new LikeClickListener(v,getItem(position).id));
		c.setOnClickListener(new CommentClickListener(getItem(position)));
		ln.setText(""+getItem(position).like);
		cn.setText(""+getItem(position).comment);
		if(getItem(position).image.compareTo("")!=0) {
			System.out.println("image : "+getItem(position).image);
			loader.DisplayImage(
					getContext().getResources().getString(R.string.base_url)+'/'
					+getContext().getResources().getString(R.string.media_url)+'/'
					+getItem(position).image,i);
			i.setVisibility(View.VISIBLE);
		} else {
			i.setVisibility(View.GONE);			
		}
		
	}
	
	// this method makes ListView Item non-selectable
	@Override
	public boolean isEnabled(int position) { return false; }
	
	// OnClickListener for Like Button
	private class LikeClickListener extends AsyncTask<Void,Void,String> implements OnClickListener {

		private View item;
		private int id;
		private ProgressDialog pdl;
		
		public LikeClickListener(View item,int id) {
			this.item=item;
			this.id=id;
		}		
		public void onClick(View v) {
			this.execute();
		}
		
		@Override
		protected void onPreExecute() {
			pdl=ProgressDialog.show(getContext(),"Loading","Loading...",true,false);
			super.onPreExecute();
		}

		@Override
		protected String doInBackground(Void... params) {

			// login : 1.234.23.142/~ypunval/like/entry
			String url=getContext().getResources().getString(R.string.base_url)+'/'
					+getContext().getResources().getString(R.string.likeboard_url)+'/';
			System.out.println("XML URL : "+url);
					
			try {
				// create http post for sending
				HttpPost request=new HttpPost(url);
				ArrayList<NameValuePair> postdata=new ArrayList<NameValuePair>();
				postdata.add( new BasicNameValuePair("id",""+id) );
				UrlEncodedFormEntity ent = new UrlEncodedFormEntity(postdata,HTTP.UTF_8);
				request.setEntity(ent);

				// get result
				InputStreamReader isr=NetworkSupporter.getStreamFromRequest(request);
				String result=NetworkSupporter.getStringFromStream(isr);
				isr.close();
				return result;
				
			} catch(Exception e) {
				e.printStackTrace();
				return "알 수 없는 에러 발생";
			}
		
		}
		
		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			pdl.dismiss();
			
			if(result.compareTo("200")==0) {
				Toast.makeText(getContext(),"좋아요 성공",Toast.LENGTH_SHORT).show();
				TextView ln=(TextView)item.findViewById(R.id.likenum);
				ln.setText(""+(Integer.parseInt(String.valueOf(ln.getText()))+1));
			} else {
				Toast.makeText(getContext(),result,Toast.LENGTH_SHORT).show();
			}
			
		}
	}
	// OnClickListener for Comment Button
	private class CommentClickListener implements OnClickListener {

		private BoardData lastData;
		
		public CommentClickListener(BoardData lastData) {
			this.lastData=lastData;
		}
		
		public void onClick(View v) {
			AppPref.setLastBoardData(lastData);
			getContext().startActivity( new Intent("BoardDetail") );
		}
		
	}
	

}
