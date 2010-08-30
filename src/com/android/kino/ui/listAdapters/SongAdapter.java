package com.android.kino.ui.listAdapters;

import java.util.List;

import com.android.kino.R;
import com.android.kino.logic.MediaProperties;
import com.android.kino.logic.Playlist;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class SongAdapter extends ArrayAdapter<MediaProperties> {

	Context mContext=null;
	Playlist playlist=null;
	
	public SongAdapter(Context context, int textViewResourceId,
			Playlist objects) {
		super(context, textViewResourceId, (List<MediaProperties>) objects);
		mContext=context;
		playlist=objects;
	}	
	
	 @Override
	public View getView(int position, View convertView, ViewGroup parent) {
		  
		MediaProperties songObj = playlist.get(position);
		 
        View v=null;  
  		LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);              
        v = inflater.inflate(R.layout.item_song, null);
        
        TextView songTitle = (TextView)  v.findViewById(R.id.songItem_songTitle);
        songTitle.setText((position+1)+". "+songObj.Title);
               
        TextView artist = (TextView)  v.findViewById(R.id.songItem_artistTitle);                
        TextView albumTitle = (TextView)  v.findViewById(R.id.songItem_albumTitle);
        
        //differntiate between an album based list and a nonalbum based list
        if (playlist.getAlbumTitle()==null){
	        artist.setText(songObj.Artist);
	        albumTitle.setText(songObj.Album.Title);
        }
        else{
	        artist.setVisibility(View.GONE);
	        albumTitle.setVisibility(View.GONE);
        }
        
		 
		return v;
	}
	 

}
