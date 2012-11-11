package com.nsdb.univer.dataadapter;

import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.client.methods.HttpGet;
import org.jdom2.Element;

import com.nsdb.univer.R;
import com.nsdb.univer.common.NetworkSupporter;

import android.content.Context;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

public abstract class DataLoadingArrayAdapter<T> extends BaseArrayAdapter<T> {

	private View footerNoticeView;
	// Data Getting
	private DataGetter getter;
	private ArrayList<T> originalData;
	private boolean loadable;
	protected final static int RESULT_SUCCESS=1;
	protected final static int RESULT_EMPTY=2;
	protected final static int RESULT_ERROR=3;

	public DataLoadingArrayAdapter(Context context, int dataResourceId, ListView view) {
		super(context, dataResourceId, view);
		// footerNoticeView
		this.footerNoticeView=((LayoutInflater)(context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)))
				.inflate(R.layout.stringdata,null);
		TextView t=(TextView)footerNoticeView.findViewById(R.id.text);
		t.setText("FooterView");
		// FooterView must add before set adapter, because of WrapperListAdapter
		// unless, you will see a lot of exceptions...
		view.setAdapter(null);
		view.addFooterView(footerNoticeView);
		view.setAdapter(this);
		// Data Getting
		getter=null;
		originalData=new ArrayList<T>();
		loadable=true;
	}
	
	// Data Getting
	protected void init() {
		loadable=true;
		clear();
		originalData.clear();
		//notifyDataSetChanged();
	}
	public void updateData(String url) {
		if(loadable==false) return;
		if(getter != null)
			getter.cancel(true);
		getter=new DataGetter(url);
		getter.execute();
		loadable=false;		
	}
	private final class DataGetter extends AsyncTask<Void,Void,Integer> {
		
		private String url;

		public DataGetter(String url) {
			this.url=url;
		}

		@Override
		protected void onPreExecute() {
			
			// footerView
			TextView t=(TextView)footerNoticeView.findViewById(R.id.text);
			t.setText("불러오는 중...");
			
			super.onPreExecute();
		}

		@Override
		protected Integer doInBackground(Void... params) {
			
			try {
				
				// create request for sending
				HttpGet request=new HttpGet(url);
				
				// get result
				InputStreamReader isr=NetworkSupporter.getStreamFromRequest(request);
				List<Element> items=NetworkSupporter.getXmlElementsFromStream(isr);
				int count=0;
				for(count=0 ; count<items.size() ; count++) {
					originalData.add( convertElementToObject(items.get(count)) );
				}
				isr.close();
				
				if(count==0) // dataOriginal is not always empty before.
					return RESULT_EMPTY;
				else
					return RESULT_SUCCESS;

			} catch(Exception e) {
				e.printStackTrace();
				return RESULT_ERROR;
			}
			
		}

		@Override
		protected void onPostExecute(Integer result) {
			
			// loadable
			if(result==RESULT_SUCCESS) {
				loadable=true;
			} else {
				loadable=false;
			}
			
			// footerView
			TextView t=(TextView)footerNoticeView.findViewById(R.id.text);
			switch(result) {
			case RESULT_SUCCESS:
				t.setText("");
				break;
			case RESULT_EMPTY:
				t.setText("데이터 없음");
				break;
			case RESULT_ERROR:
				t.setText("에러 발생");
				break;
			}
			
			// apply original data
			applyUpdate();
		}

	}
	protected abstract T convertElementToObject(Element item);
	protected void applyUpdate() {
		clear();
		for(int i=0;i<originalData.size();i++) {
			add(originalData.get(i));
		}
		//notifyDataSetChanged();				
	}
	
	protected boolean isLoadable() { return loadable; }
	protected ArrayList<T> getOriginalData() { return originalData; }
}
