package com.android.kino.ui.listAdapters;

import java.util.List;

import com.android.kino.R;
import com.android.kino.logic.MediaProperties;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class SongAdapter extends ArrayAdapter<MediaProperties> {

	Context mContext=null;
	List<MediaProperties> songs=null;
	
	public SongAdapter(Context context, int textViewResourceId,
			List<MediaProperties> objects) {
		super(context, textViewResourceId, objects);
		mContext=context;
		songs=objects;
	}	
	
	 @Override
	public View getView(int position, View convertView, ViewGroup parent) {
		  
		MediaProperties songObj = songs.get(position);
		 
        View v=null;  
  		LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);              
        v = inflater.inflate(R.layout.item_song, null);
        
        TextView songTitle = (TextView)  v.findViewById(R.id.songItem_songTitle);
        songTitle.setText(songObj.Title);
        
        TextView artist = (TextView)  v.findViewById(R.id.songItem_artistTitle);
        artist.setText(songObj.Artist);
        
        TextView albumTitle = (TextView)  v.findViewById(R.id.songItem_albumTitle);
        albumTitle.setText(songObj.Album.Title);
        
		 
		return v;
	}
	 

}
