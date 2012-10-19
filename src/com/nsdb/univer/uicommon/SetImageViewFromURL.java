package com.nsdb.univer.uicommon;

import java.net.URL;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

public class SetImageViewFromURL extends AsyncTask<Void,Void,Bitmap> {
	
	public ImageView view;
	public String urlPath;
	
	public SetImageViewFromURL(ImageView v,String u) {
		view=v;
		urlPath=u;
	}
	
	@Override
	protected Bitmap doInBackground(Void... params) {
		
		try {
			URL url = new URL(urlPath);
			Bitmap bitmap = BitmapFactory.decodeStream(url.openStream());
			return bitmap;
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}

	@Override
	protected void onPostExecute(Bitmap result) {		
		view.setImageBitmap(result);
	}

}
