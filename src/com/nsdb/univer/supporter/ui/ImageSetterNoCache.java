package com.nsdb.univer.supporter.ui;

import java.net.URL;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

public class ImageSetterNoCache extends AsyncTask<Void,Void,Bitmap> {
	
	private String url;
	private ImageView view;
	
	public ImageSetterNoCache(String url,ImageView view) {
		this.url=url;
		this.view=view;
	}
	
	@Override
	protected Bitmap doInBackground(Void... params) {
		
		try {
			URL url = new URL(this.url);
			Bitmap bitmap = BitmapFactory.decodeStream(url.openStream());
			return bitmap;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
	}

	@Override
	protected void onPostExecute(Bitmap result) {		
		view.setImageBitmap(result);
	}

}
