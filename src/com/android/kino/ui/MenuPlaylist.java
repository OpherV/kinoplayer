package com.android.kino.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Parcelable;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
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
					Playlist playlist=mPlayer.getPlaylist();
					
					ArtistProperties artistClicked = library.getArtistFromCache(playlist.getArtistTitle());			
					AlbumList albums=library.getAlbumsByArtist(artistClicked.getName());
					
					Intent albumlistIntent=new Intent(MenuPlaylist.this,MenuAlbumBrowse.class);								
					albumlistIntent.putExtra("albumlist",(Parcelable)albums);						
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
