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
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.SeekBar.OnSeekBarChangeListener;

import com.android.kino.Kino;
import com.android.kino.R;
import com.android.kino.logic.KinoMediaPlayer;
import com.android.kino.logic.KinoUser;
import com.android.kino.logic.MediaProperties;
import com.android.kino.utils.ConvertUtils;

public class PlayerMain extends Activity implements KinoUser, OnSeekBarChangeListener{
	private Kino kino = null;
	private KinoMediaPlayer mPlayer;
	
	
	private MediaProperties song=null;
	private TextView timeElapsed=null;
	private SeekBar songSeek=null;
	
	private boolean updatingSeekBar=false;
	
	private Handler guiUpdater= new Handler();	
	final int GUI_UPDATE_INTERVAL = 1000;
	
	// updates the gui every second
	private Runnable guiUpdateTask = new Runnable() {
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
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {	
		super.onCreate(savedInstanceState);		
		kino=Kino.getKino(this);		
		setContentView(R.layout.player_main);
		
		Button btn_back = (Button) findViewById(R.id.btn_back);
		btn_back.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                mPlayer.previous();
                initSongDetails();
            }
        });
		
        Button btn_forward = (Button) findViewById(R.id.btn_forward);
        btn_forward.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                mPlayer.next();
                initSongDetails();
            }
        });
        
        Button btn_play = (Button) findViewById(R.id.btn_play);
        btn_play.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                mPlayer.togglePlayPause();                
            }
        });
		        			          
		kino.registerUser(this);
	}

	private void initSongDetails(){
	    
		song = mPlayer.getCurrentMedia();
		
		//bg image
		MediaImageContainer songImages = new MediaImageContainer(mPlayer.getCurrentMedia());	        
        ImageView playerBGview = (ImageView) findViewById(R.id.playerBG);
        
        playerBGview.setImageBitmap(songImages.getArtistImage());
		
        //details
		TextView titleCaption = (TextView) this.findViewById(R.id.player_title);    
		titleCaption.setText(song.Title);
	    
	    TextView artistCaption = (TextView) this.findViewById(R.id.player_artist);    
	    artistCaption.setText(song.Artist);
	    
	    TextView albumCaption = (TextView) this.findViewById(R.id.player_album);    
	    albumCaption.setText(song.Album.Title);
	    
	    
	    //seekbar
	    songSeek = (SeekBar) this.findViewById(R.id.player_seek);	    
	    songSeek.setMax(  mPlayer.getCurrentTrackDurationInSeconds() );
	    
	    Log.d(getClass().getName(),"duration of song "+mPlayer.getCurrentTrackDurationInSeconds());
	    
	    timeElapsed = (TextView) this.findViewById(R.id.player_timeElapsed);    
	    timeElapsed.setText(ConvertUtils.formatTime(mPlayer.getCurrentTrackDurationInSeconds()));
	    
	    songSeek.setOnSeekBarChangeListener(this);	    	    
	    	  

	    // TODO call updateTrack something
	}
	
	private void updateSongDetails(){	
		//make sure this won't happen while dragging
		if (!updatingSeekBar){
			timeElapsed.setText(ConvertUtils.formatTime(mPlayer.getPlaybackPosition().getPosition()/1000));
			songSeek.setProgress(mPlayer.getPlaybackPosition().getPosition()/1000);
		}
	}
	    
	
	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		float progress = (float) seekBar.getProgress()/ (float) seekBar.getMax();		
		mPlayer.seek(progress);
		songSeek.setProgress(seekBar.getProgress());		
		updatingSeekBar=false;
	}
	
	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
		updatingSeekBar=true;
	}
	
	@Override
	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromUser) {				
		if (fromUser){
			timeElapsed.setText(ConvertUtils.formatTime( seekBar.getProgress() ));
		}
		
	}
	
    @Override
    public void onKinoInit(Kino kino) {
        mPlayer= kino.getPlayer();
        initSongDetails();
        
        //start the gui update timer                      
     	
     	//start in interval
        guiUpdater.postAtTime(guiUpdateTask, GUI_UPDATE_INTERVAL);
        
    }      
    
    @Override
    protected void onDestroy() {    
    	super.onDestroy();
    	//stop the gui updater
    	guiUpdater.removeCallbacks(guiUpdateTask);
    }
    
}
