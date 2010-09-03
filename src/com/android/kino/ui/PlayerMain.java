package com.android.kino.ui;

import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.SeekBar.OnSeekBarChangeListener;

import com.android.kino.R;
import com.android.kino.utils.ConvertUtils;

public class PlayerMain extends KinoUI implements OnSeekBarChangeListener{			
	
	final private int BTNTIMEOUT=6000;
	
	private boolean updatingSeekBar=false;
	private SeekBar songSeek=null;
	ImageView btn_play=null;
	ImageView btn_forward=null;
	ImageView btn_back=null;	
	ImageView playerBGview =null;	
	
	private boolean navbuttonsVisible=true;
	
	private Runnable rHideButtons=new Runnable(){

		@Override
		public void run() {				
			fadeoutButtons();	
			navbuttonsVisible=false;
		}
    	
    };
    
		
	@Override
	protected void initUI(){		
		setContentView(R.layout.player_main);
		btn_back = (ImageView) findViewById(R.id.btn_back);
		btn_forward = (ImageView) findViewById(R.id.btn_forward);
		
        btn_play = (ImageView) findViewById(R.id.btn_play);
        
        playerBGview = (ImageView) findViewById(R.id.playerBG); 
        
        playerBGview.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                mPlayer.togglePlayPause();
                setPausePlay();
                if (!navbuttonsVisible){
                	guiUpdater.removeCallbacks(rHideButtons);
                	fadeinButtons();
	                navbuttonsVisible=true;
                }
            }
        });
                                     
        
	    // Gesture detection
        GestureActions bgActions = new GestureActions(){
        	@Override
        	public void swipeLeft() {
                mPlayer.previous();
                initSongDetails();
                setPausePlay();
        	}
        	
        	@Override
        	public void swipeRight() {            	
                    mPlayer.next();
                    initSongDetails();
                    setPausePlay();
        	}
        	        	
        };
        
        final GestureDetector gestureDetector = new GestureDetector(new GenericGestureDetector(bgActions));       
        View.OnTouchListener gestureListener = new View.OnTouchListener() {
             public boolean onTouch(View v, MotionEvent event) {            	 
                 if (gestureDetector.onTouchEvent(event)) {                	 
                     return true;
                 }                 
                 return false;
             }
         };
         
         playerBGview.setOnTouchListener(gestureListener);
		        			          
	}
	
	private void fadeinButtons(){
        fadein_partial(btn_play);
     //   fadein_partial(btn_forward);
     //   fadein_partial(btn_back);
        
        scheduleTask(rHideButtons,BTNTIMEOUT);
        
	}
	
	private void fadeoutButtons(){				
        fadeout_partial(btn_play);
     //   fadeout_partial(btn_forward);
     //   fadeout_partial(btn_back);
        
	}

	protected void initSongDetails(){
	    
		song = mPlayer.getCurrentMedia();
		
		
		//bg image			                      
        playerBGview.setImageBitmap(song.getArtistImage(this));
		
        //details
		TextView titleCaption = (TextView) this.findViewById(R.id.player_title);    
		titleCaption.setText(song.Title);
	    
	    TextView artistCaption = (TextView) this.findViewById(R.id.player_artist);    
	    artistCaption.setText(song.Artist);
	    
	    TextView albumCaption = (TextView) this.findViewById(R.id.player_album);    
	    albumCaption.setText(song.Album.getAlbumName());
	    
	    ImageView image = (ImageView) this.findViewById(R.id.player_albumImage); 
	    image.setImageBitmap(song.getAlbumImage(this));
	    
	    
	    //seekbar
	    songSeek = (SeekBar) this.findViewById(R.id.player_seek);	    
	    songSeek.setMax(  mPlayer.getCurrentTrackDurationInSeconds() );
	    	    
	    
	    timeElapsed = (TextView) this.findViewById(R.id.player_timeElapsed);    
	    timeElapsed.setText(ConvertUtils.formatTime(mPlayer.getCurrentTrackDurationInSeconds()));
	    
	    songSeek.setOnSeekBarChangeListener(this);	    	    
	    	  
	    setPausePlay();
	    // TODO call updateTrack something	    	   
	}
	
	protected void updateSongDetails(){	
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
	public void updateUI() {	
		super.updateUI();
		
		//bg image			       
        ImageView playerBGview = (ImageView) findViewById(R.id.playerBG);        
        playerBGview.setImageBitmap(song.getArtistImage(this));
		
	}
	
	private void setPausePlay(){
		if (mPlayer.isPlaying()){
			btn_play.setBackgroundDrawable(this.getResources().getDrawable(R.drawable.icn_pause));
		}
		else{
			btn_play.setBackgroundDrawable(this.getResources().getDrawable(R.drawable.icn_play));
		}
	}
	
	@Override
	protected void onResume() {	
		super.onResume();
		setPausePlay();
		fadeinButtons();
	}	
	        
	
}
