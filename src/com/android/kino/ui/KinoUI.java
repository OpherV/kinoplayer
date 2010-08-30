package com.android.kino.ui;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import com.android.kino.Kino;
import com.android.kino.R;
import com.android.kino.logic.KinoMediaPlayer;
import com.android.kino.logic.KinoUser;
import com.android.kino.logic.MediaProperties;
import com.android.kino.musiclibrary.Library;
import com.android.kino.utils.ConvertUtils;

public class KinoUI extends Activity implements KinoUser{
	protected Kino kino = null;
	protected KinoMediaPlayer mPlayer;
	
	protected MediaProperties song=null;
	protected TextView timeElapsed=null;
	protected ProgressBar songProgress=null;
	protected PlayerMini playermini;
	protected Library library;  

	protected Handler guiUpdater;	
	final int GUI_UPDATE_INTERVAL = 1000;
	
	// updates the gui every second
	protected Runnable guiUpdateTask = new Runnable() {
   	   public void run() {
   	       final long start = GUI_UPDATE_INTERVAL;
   	       long millis = SystemClock.uptimeMillis() - start;
   	       int seconds = (int) (millis / 1000);
   	       int minutes = seconds / 60;
   	       seconds     = seconds % 60;
   	          	          	          	      
   	       //if song was changed, reload all details
   	       if (song == mPlayer.getCurrentMedia()){
   	    	   updateSongDetails();
   	       }
   	       else{
   	    	   initSongDetails();
   	       }
   	     
   	      guiUpdater.postAtTime(this,
   	               start + (((minutes * 60) + seconds + 1) * 1000));
   	   }
   	};
   	
    public void onKinoInit(Kino kino) {
        mPlayer= kino.getPlayer();        
        library = kino.getLibrary();
        initSongDetails();                
        
        //start the gui update timer                           	
        guiUpdater = new Handler();
        guiUpdater.postAtTime(guiUpdateTask, GUI_UPDATE_INTERVAL);
                
        kinoReady();
    }      
    
    @Override
    protected void onDestroy() {    
    	super.onDestroy();
    	//stop the gui updater
    	guiUpdater.removeCallbacks(guiUpdateTask);
    }
    
    protected void initSongDetails(){
	    
		song = mPlayer.getCurrentMedia();
		
		playermini= (PlayerMini) this.findViewById(R.id.player_mini);		
		
		//make sure that a song is indeed playing
		if (song!=null){
		
	        //details
			TextView titleCaption = (TextView) this.findViewById(R.id.miniplayer_title);    
			titleCaption.setText(song.Title);
		    
		    TextView artistCaption = (TextView) this.findViewById(R.id.miniplayer_artist);    
		    artistCaption.setText(song.Artist);
		    
		    TextView albumCaption = (TextView) this.findViewById(R.id.miniplayer_album);    
		    albumCaption.setText(song.Album.Title);
		    
		    
		    //seekbar
		    songProgress = (ProgressBar) this.findViewById(R.id.miniplayer_progress);	    
		    songProgress.setMax(  mPlayer.getCurrentTrackDurationInSeconds() );		    		    
		    
		    timeElapsed = (TextView) this.findViewById(R.id.miniplayer_timeElapsed);    
		    timeElapsed.setText(ConvertUtils.formatTime(mPlayer.getCurrentTrackDurationInSeconds()));
	    
		}
		else{
			   playermini.setVisibility(View.GONE);
		}
	}
	
	protected void updateSongDetails(){	
		//make sure this won't happen while dragging
		if (song!=null){
			timeElapsed.setText(ConvertUtils.formatTime(mPlayer.getPlaybackPosition().getPosition()/1000));
			songProgress.setProgress(mPlayer.getPlaybackPosition().getPosition()/1000);
		}
	}
					
	@Override
	protected void onCreate(Bundle savedInstanceState) {	
		super.onCreate(savedInstanceState);		
		kino=Kino.getKino(this);		
		
		initUI();
		        			          
		kino.registerUser(this);
	}
	
	@Override
	protected void onResume() {
		super.onResume();		
		if (playermini!=null && song!=null){
	    	   playermini.setVisibility(View.VISIBLE);		   	   
		}
	}

	
	protected void initUI(){}
	protected void kinoReady(){};
	
	
	
}
