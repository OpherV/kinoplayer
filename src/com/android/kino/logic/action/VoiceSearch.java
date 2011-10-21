package com.android.kino.logic.action;

import com.android.kino.Kino;
import com.android.kino.logic.AlbumList;
import com.android.kino.logic.KinoMediaPlayer;
import com.android.kino.logic.Playlist;

import android.app.Activity;
import android.app.SearchManager;
import android.os.Bundle;
import android.widget.Toast;

public class VoiceSearch  extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String query = getIntent().getStringExtra(SearchManager.QUERY);
        
        int by=query.indexOf("by");
        int album=query.indexOf("album");
        int artist=query.indexOf("artist");
        
        String artistName;
        String albumName;        
        Kino k =Kino.getKino(this);
        KinoMediaPlayer kinoPlayer = k.getPlayer();
        Playlist p =null;
        
        if (by!=-1){
        	artistName=query.substring(by+2,query.length()).trim();
        	albumName=query.substring(0,by).trim();
        	p = k.getLibrary().getPlaylistByAlbum(artistName, albumName);
        	if (p==null){
        		Toast.makeText(this, "could not find "+albumName+" by "+artistName, Toast.LENGTH_SHORT).show();
        	}
        }
        
        if (artist!=-1){
        	artistName=query.substring(artist+6,query.length()).trim();
        	//p = k.getLibrary().getArtistPlaylist("artistName");
        	if (p==null){
        		Toast.makeText(this, "could not artist "+artistName, Toast.LENGTH_SHORT).show();
        	}
        }
        
        if (p!=null){
        	kinoPlayer.setCurrentPlaylist(p);
        	kinoPlayer.setCurrentMedia(0);
        	kinoPlayer.togglePlayPause();        	
        }                
    	
        
    }
}