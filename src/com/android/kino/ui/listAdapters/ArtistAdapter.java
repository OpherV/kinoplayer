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

		// static to save the reference to the outer class and to avoid access to
		// any members of the containing class
		static class ViewHolder {
			public ImageView artistImage;
			public TextView artistText;
		}

	
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
			  
			ViewHolder holder;
			ArtistProperties artistObj = artists.get(position);
			 
			
			
	        View v=convertView;        
            //make sure to use the image cache
	        if (v == null) { 
	        	LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);              
		        v = inflater.inflate(R.layout.item_artist, null);	        
	        
				holder = new ViewHolder();
				holder.artistText = (TextView)  v.findViewById(R.id.artistItem_artistTitle);
				holder.artistImage = (ImageView) v.findViewById(R.id.artistItem_aristImage);
				v.setTag(holder);
	        
	        }
	        else
	        {
				holder = (ViewHolder) v.getTag();
			}
	        
	        holder.artistText.setText(artistObj.getName());
	        holder.artistImage.setImageBitmap(artistObj.getArtistImage(mContext));	       
	
	        
			 
			return v;
		}
		 

	
}
