package com.android.kino.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

import com.android.kino.Kino;
import com.android.kino.R;
import com.android.kino.logic.KinoMediaPlayer;
import com.android.kino.logic.KinoUser;
import com.android.kino.logic.MediaProperties;
import com.android.kino.logic.Playlist;

public class MenuPlaylist extends Activity implements KinoUser,OnItemClickListener{
    
    private Kino kino=null;
    private KinoMediaPlayer mPlayer = null;
    private ArrayAdapter<MediaProperties> playlistAdapter=null;
    Playlist playlist=null;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		kino=Kino.getKino(this);
		kino.registerUser(this);
		
		
		setContentView(R.layout.menu_playlist);		
		playlist=getIntent().getExtras().getParcelable("playlist");
		
		playlistAdapter = new ArrayAdapter<MediaProperties>(this,android.R.layout.simple_list_item_1, playlist);
		
		ListView playlistView = (ListView)findViewById(R.id.playlist);
		playlistView.setAdapter(playlistAdapter);			
		
		playlistView.setOnItemClickListener(this);
		
	}

    @Override
    public void onKinoInit(Kino kino) {
        mPlayer=kino.getPlayer();    
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
