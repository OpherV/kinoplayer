package com.android.kino.ui;

import java.util.ArrayList;

import android.content.Intent;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.android.kino.Kino;
import com.android.kino.R;
import com.android.kino.logic.AlbumList;
import com.android.kino.logic.AlbumProperties;
import com.android.kino.logic.ArtistProperties;
import com.android.kino.logic.MediaProperties;
import com.android.kino.logic.Playlist;
import com.android.kino.ui.listAdapters.SongAdapter;

public class MenuPlaylist extends KinoUI implements OnItemClickListener{
    
    private ArrayAdapter<MediaProperties> playlistAdapter=null;
    Playlist playlist=null;
    Button btn_return=null;
    ViewGroup albumDetails=null;
    
	@Override
	protected void initUI() {
		super.initUI();
		
		setContentView(R.layout.menu_playlist);		
		playlist=getIntent().getExtras().getParcelable("playlist");
		
		albumDetails = (ViewGroup)findViewById(R.id.menu_playlist_albumDetailsContainer);
		btn_return= (Button) this.findViewById(R.id.btn_return);
	}
    
    @Override
    public void onItemClick(AdapterView<?> parent, View view,
                            int position, long id) {
       // set the player to start playing the right song in the playlist
       mPlayer.setCurrentPlaylist(playlist);
       mPlayer.setCurrentMedia(position);
       mPlayer.togglePlayPause();
       startActivity(new Intent(this,PlayerMain.class));       
       
    }    
    
    @Override
    public void onKinoInit(Kino kino) {
    	super.onKinoInit(kino);
    	
    	AlbumProperties album=null;
    	ArtistProperties artist=null;
		boolean isAlbumPlaylist=getIntent().getExtras().getBoolean("albumPlaylist");
		if (isAlbumPlaylist){
			album=library.getAlbumFromCache(getIntent().getExtras().getString("albumArtist"),
															getIntent().getExtras().getString("albumTitle"),
															getIntent().getExtras().getInt("albumYear"));
			artist= library.getArtistFromCache(album.getArtistName());
		}		 
    	
		playlistAdapter = new SongAdapter(this, 0, playlist, isAlbumPlaylist);
		
		ListView playlistView = (ListView)findViewById(R.id.playlist);
		playlistView.setAdapter(playlistAdapter);					
		
		if (!isAlbumPlaylist){			
			albumDetails.setVisibility(View.GONE);
									
				btn_return.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {											
						Intent mainMenuIntent=new Intent(MenuPlaylist.this,MenuMain.class);											
			    		startActivity(mainMenuIntent);
						
					}
				});
			
		}
		else{
			
			btn_return.setOnClickListener(new OnClickListener() { 
				
				@Override
				public void onClick(View v) {					

					AlbumList albums=null;
					
					String artistTitle=playlist.getArtistTitle();
					if (artistTitle!=null){
						ArtistProperties artistClicked = library.getArtistFromCache(artistTitle);
						albums=library.getAlbumsByArtist(artistClicked.getName());
					}
					else{
						albums=library.getAllAlbums();
					}							
					
					
					Intent albumlistIntent=new Intent(MenuPlaylist.this,MenuAlbumBrowse.class);			
					
					ArrayList<String> albumTitles = new ArrayList<String>();
					ArrayList<String> albumArtists= new ArrayList<String>();
					int[] albumYears = new int[albums.size()];
				
					for (int i=0;i<albums.size();i++){
						albumTitles.add(albums.get(i).getAlbumName());
						albumArtists.add(albums.get(i).getArtistName());
						albumYears[i]=albums.get(i).getAlbumYear();
						
					}
					
					if (artistTitle!=null){
						albumlistIntent.putExtra("artistTitle",artistTitle);
					}
					albumlistIntent.putExtra("albumTitleList",albumTitles);
					albumlistIntent.putExtra("albumArtistList",albumArtists);
					albumlistIntent.putExtra("albumYears",albumYears);
		    		startActivity(albumlistIntent);
					
				}
			});
			
			//bg
			ImageView artistImageBG = (ImageView) findViewById(R.id.menu_playlist_bgimage);
	        artistImageBG.setImageBitmap(artist.getArtistImage(this));	        
			
			ImageView image = (ImageView) this.findViewById(R.id.menu_playlist_albumImage);
		    Bitmap albumImage=album.getAlbumImage(this);
		    if (albumImage!=null){
		    	image.setImageBitmap(albumImage);		    	
		    }		    
		    
		    final AlbumProperties fAlbum=album;
		    final ArtistProperties fArtist=artist;
		    image.setOnLongClickListener(new OnLongClickListener() {
				
				@Override
				public boolean onLongClick(View v) {
					Intent viewAlbumArt = new Intent(MenuPlaylist.this,MenuAlbumArtView.class);
					viewAlbumArt.putExtra("artistTitle", fArtist.getName());
					viewAlbumArt.putExtra("albumTitle", fAlbum.getAlbumName());
					viewAlbumArt.putExtra("albumYear", fAlbum.getAlbumYear());
					startActivity(viewAlbumArt);
					return false;
				}
			});
		    
			
			TextView albumTitle = (TextView) findViewById(R.id.albumItem_albumTitle);
			albumTitle.setText(album.getAlbumName());
			
			TextView artistTitle = (TextView) findViewById(R.id.albumItem_artistTitle);
			artistTitle.setText(album.getArtistName());
			
			TextView albumYear = (TextView) findViewById(R.id.albumItem_albumYear);
			if (album.getAlbumYear()>0){
				albumYear.setVisibility(View.VISIBLE);
				albumYear.setText(playlist.getAlbumYear()+"");
			}
			else{
				albumYear.setVisibility(View.GONE);
			}
		}
		
		playlistView.setOnItemClickListener(this);   
    }
	
}
