package com.android.kino.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.android.kino.Kino;
import com.android.kino.R;
import com.android.kino.logic.AlbumProperties;
import com.android.kino.logic.ArtistProperties;
import com.android.kino.logic.MediaProperties;
import com.android.kino.logic.Playlist;
import com.android.kino.ui.listAdapters.SongAdapter;

public class MenuPlaylist extends KinoUI implements OnItemClickListener{
    
    private ArrayAdapter<MediaProperties> playlistAdapter=null;
    Playlist playlist=null;
    
	@Override
	protected void initUI() {

		setContentView(R.layout.menu_playlist);		
		playlist=getIntent().getExtras().getParcelable("playlist");					
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
			ViewGroup albumDetails = (ViewGroup)findViewById(R.id.menu_playlist_albumDetails);
			albumDetails.setVisibility(View.GONE);
			
		}
		else{
			
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
