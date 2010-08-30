package com.android.kino.ui.listAdapters;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.android.kino.R;
import com.android.kino.logic.ArtistProperties;

public class ArtistAdapter extends ArrayAdapter<ArtistProperties> {

		Context mContext=null;
		List<ArtistProperties> artists=null;
		
		public ArtistAdapter(Context context, int textViewResourceId,
				List<ArtistProperties> objects) {
			super(context, textViewResourceId, objects);
			mContext=context;
			artists=objects;
		}	
		
		 @Override
		public View getView(int position, View convertView, ViewGroup parent) {
			  
			ArtistProperties artistObj = artists.get(position);
			 
	        View v=null;  
	  		LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);              
	        v = inflater.inflate(R.layout.item_artist, null);
	        

	        
	        TextView artist = (TextView)  v.findViewById(R.id.artistItem_artistTitle);
	        artist.setText(artistObj.getName());
	        
			 
			return v;
		}
		 

	
}
