package com.android.kino.ui;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import com.android.kino.Kino;
import com.android.kino.R;
import com.android.kino.logic.KinoUser;
import com.android.kino.logic.MediaProperties;
import com.android.kino.logic.Playlist;
import com.android.kino.musiclibrary.Library;

public class MenuMain extends KinoUI {

		
	private Library library;
    private ArrayAdapter<MediaProperties> playlistAdapter=null;
    private ListView playlistView=null;
    Playlist playlist=null;
    
    
    //listener for all song list
    OnItemClickListener allsongsClickListener=new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id){
			   // set the player to start playing the right song in the playlist
			   mPlayer.setCurrentPlaylist(playlist);
			   mPlayer.setCurrentMedia(position);
			   mPlayer.togglePlayPause();
			   startActivity(new Intent(MenuMain.this,PlayerMain.class));
		};
	};
	
	
	@Override
	protected void initUI() {	    				
/*
						Intent playlistIntent=new Intent(parent.getContext(),MenuPlaylist.class);
						Playlist pAllsongs = library.getAllSongs();
						//TODO fetch the allsongs playlist from the library and pass it
						playlistIntent.putExtra("playlist",(Parcelable)pAllsongs);						
			    		startActivity(playlistIntent);
			    		
			    		//fetch like this
			    		 * playlist=getIntent().getExtras().getParcelable("playlist");
			    		*/
		
		
			setContentView(R.layout.menu_main);
			playlistView=(ListView)findViewById(R.id.mainmenu_list);			
									
			Button bAllSongs = (Button) findViewById(R.id.mainmenu_btn_allsongs);
			bAllSongs.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					setAllSongs();
					
				}
			});
			
			Button bArtists = (Button) findViewById(R.id.mainmenu_btn_artists);
			bArtists.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					Toast.makeText(MenuMain.this, "Not impemented yet... :(", Toast.LENGTH_SHORT).show();
					
				}
			});
			
			Button bAlbums = (Button) findViewById(R.id.mainmenu_btn_albums);
			bAlbums.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					Toast.makeText(MenuMain.this, "Not impemented yet... :(", Toast.LENGTH_SHORT).show();
					
				}
			});
	}
	
	private void setAllSongs(){
		playlist=library.getAllSongs();
		playlistAdapter = new ArrayAdapter<MediaProperties>(this,android.R.layout.simple_list_item_1, playlist);		
		playlistView.setAdapter(playlistAdapter);					
		playlistView.setOnItemClickListener(allsongsClickListener);
	}
	
	protected void kinoReady(){
		library = kino.getLibrary();
		setAllSongs();
	};

	

}
