package com.android.kino.ui;

import java.util.LinkedList;

import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import com.android.kino.R;
import com.android.kino.logic.AlbumProperties;
import com.android.kino.logic.ArtistProperties;
import com.android.kino.logic.MediaProperties;
import com.android.kino.logic.Playlist;
import com.android.kino.musiclibrary.Library;
import com.android.kino.ui.listAdapters.AlbumAdapter;
import com.android.kino.ui.listAdapters.ArtistAdapter;
import com.android.kino.ui.listAdapters.SongAdapter;

public class MenuMain extends KinoUI {

		
	private Library library;    
    private ListView playlistView=null;
    Playlist playlist=null;
    
    
    //listener for all song list
    OnItemClickListener songsClickListener=new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id){
			   // set the player to start playing the right song in the playlist
			   mPlayer.setCurrentPlaylist(playlist);
			   mPlayer.setCurrentMedia(position);
			   mPlayer.togglePlayPause();
			   startActivity(new Intent(MenuMain.this,PlayerMain.class));
		};
	};
	
	
    OnItemClickListener allartistsClickListener=new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id){
			ArtistProperties artistClicked = (ArtistProperties) parent.getItemAtPosition(position);
			
			LinkedList<AlbumProperties> albums=library.getAlbumsByArtist(artistClicked.getName());
			ArrayAdapter<AlbumProperties> playlistAdapter = new AlbumAdapter(MenuMain.this,android.R.layout.simple_list_item_1, albums);		
			playlistView.setAdapter(playlistAdapter);					
			playlistView.setOnItemClickListener(albumsClickListener);
			
		};
	};
	
    OnItemClickListener albumsClickListener=new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id){
			AlbumProperties albumClicked = (AlbumProperties) parent.getItemAtPosition(position);
			
			Playlist albumPlaylst = library.getSongsByAlbum(albumClicked.getArtistName(), albumClicked.getAlbumName());
			
			ArrayAdapter<MediaProperties> playlistAdapter = new SongAdapter(MenuMain.this,android.R.layout.simple_list_item_1, albumPlaylst);		
			playlistView.setAdapter(playlistAdapter);					
			playlistView.setOnItemClickListener(songsClickListener);
			
			playlist=albumPlaylst;					
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
					setAllArtists();					
				}
			});
			
			Button bAlbums = (Button) findViewById(R.id.mainmenu_btn_albums);
			bAlbums.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					setAllAlbums();
					
				}
			});
			
			Button bPreferences = (Button) findViewById(R.id.mainmenu_btn_preferences);
			bPreferences.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					Toast.makeText(MenuMain.this, "Not impemented yet... :(", Toast.LENGTH_SHORT).show();
					
				}
			});
	}
	
	private void setAllSongs(){
		playlist=library.getAllSongs();
		ArrayAdapter<MediaProperties> playlistAdapter = new SongAdapter(this,android.R.layout.simple_list_item_1, playlist);		
		playlistView.setAdapter(playlistAdapter);					
		playlistView.setOnItemClickListener(songsClickListener);
	}
	
	private void setAllArtists(){
		LinkedList<ArtistProperties> artists=library.getAllArtists();
		ArrayAdapter<ArtistProperties> playlistAdapter = new ArtistAdapter(this,android.R.layout.simple_list_item_1, artists);		
		playlistView.setAdapter(playlistAdapter);					
		playlistView.setOnItemClickListener(allartistsClickListener);
	}
	
	private void setAllAlbums(){
		LinkedList<AlbumProperties> albums=library.getAllAlbums();
		ArrayAdapter<AlbumProperties> playlistAdapter = new AlbumAdapter(this,android.R.layout.simple_list_item_1, albums);		
		playlistView.setAdapter(playlistAdapter);					
		playlistView.setOnItemClickListener(albumsClickListener);
	}
	
	protected void kinoReady(){
		library = kino.getLibrary();
		setAllSongs();
	};

	

}
