package com.android.kino.ui.listAdapters;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.android.kino.R;
import com.android.kino.logic.AlbumList;
import com.android.kino.logic.AlbumProperties;

public class AlbumAdapter extends ArrayAdapter<AlbumProperties> {

	private Context mContext=null;
	private AlbumList albums=null;
	private int itemResource;
	
	public AlbumAdapter(Context context, int textViewResourceId,
			AlbumList objects) {
		super(context, textViewResourceId, (List<AlbumProperties>) objects);
		mContext=context;
		albums=objects;
		itemResource=textViewResourceId;
	}	
	
	 @Override
	public View getView(int position, View convertView, ViewGroup parent) {
		  
		AlbumProperties albumObj = albums.get(position);
		 
        View v=null;  
  		LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);              
        v = inflater.inflate(itemResource, null);
        

        TextView albumTitle = (TextView)  v.findViewById(R.id.albumItem_albumTitle);
        albumTitle.setText(albumObj.getAlbumName());
                
        TextView artistTitle = (TextView)  v.findViewById(R.id.albumItem_artistTitle);
        
        //differntiate between displaying artist album or general albums
        if (albums.getArtistTitle()==null){
        	artistTitle.setText(albumObj.getArtistName());
        }
        else{
        	artistTitle.setVisibility(View.GONE);
        }
                
        TextView albumYear = (TextView)  v.findViewById(R.id.albumItem_albumYear);
        
        if (albumObj.getYear()>0){
        	albumYear.setText(albumObj.getYear()+"");
        }
        else{
        	albumYear.setVisibility(View.GONE);
        }
        
		 
		return v;
	}
	 


}
