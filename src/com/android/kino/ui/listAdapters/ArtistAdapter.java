package com.android.kino.ui.listAdapters;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.kino.R;
import com.android.kino.logic.ArtistProperties;
import com.android.kino.ui.KinoUI;

public class ArtistAdapter extends ArrayAdapter<ArtistProperties> {

		KinoUI mContext=null;
		List<ArtistProperties> artists=null;
		
		public ArtistAdapter(Context context, int textViewResourceId,
				List<ArtistProperties> objects) {
			super(context, textViewResourceId, objects);
			mContext=(KinoUI) context;
			artists=objects;
		}	
		
		 @Override
		public View getView(int position, View convertView, ViewGroup parent) {
			  
			ArtistProperties artistObj = artists.get(position);
			 
	        View v=null;  
	  		LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);              
	        v = inflater.inflate(R.layout.item_artist, null);
	        
	        
	        ImageView artistImage = (ImageView) v.findViewById(R.id.artistItem_aristImage);
	        artistImage.setImageBitmap(artistObj.getArtistImage(mContext));
	        
	        TextView artist = (TextView)  v.findViewById(R.id.artistItem_artistTitle);
	        artist.setText(artistObj.getName());
	        
			 
			return v;
		}
		 

	
}
