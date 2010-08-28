package com.android.kino.ui;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.android.kino.R;
import com.android.kino.logic.MediaProperties;
import com.android.kino.logic.Playlist;

public class MenuPlaylist extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.menu_playlist);
		
		Playlist playlist=null;
		ArrayAdapter<MediaProperties> playlistAdapter = new ArrayAdapter<MediaProperties>(this,android.R.layout.simple_list_item_1, playlist);
		
		ListView playlistView = (ListView)findViewById(R.id.playlist);
		playlistView.setAdapter(playlistAdapter);
		
	}
	
}
