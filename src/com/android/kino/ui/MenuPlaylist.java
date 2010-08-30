package com.android.kino.ui;

import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.android.kino.R;
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
			
		playlistAdapter = new SongAdapter(this, 0, playlist);
		
		ListView playlistView = (ListView)findViewById(R.id.playlist);
		playlistView.setAdapter(playlistAdapter);
		
		if (playlist.getAlbumTitle()==null){
			ViewGroup albumDetails = (ViewGroup)findViewById(R.id.menu_playlist_albumDetails);
			albumDetails.setVisibility(View.GONE);
		}
		else{
			TextView albumTitle = (TextView) findViewById(R.id.albumItem_albumTitle);
			albumTitle.setText(playlist.getAlbumTitle());
			
			TextView artistTitle = (TextView) findViewById(R.id.albumItem_artistTitle);
			artistTitle.setText(playlist.getArtistTitle());
			
			TextView albumYear = (TextView) findViewById(R.id.albumItem_albumYear);
			albumYear.setText(playlist.getAlbumYear()+"");
		}
		
		playlistView.setOnItemClickListener(this);
		
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
	
}
