package com.android.kino.ui;

import android.content.Intent;
import android.os.Parcelable;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.SeekBar.OnSeekBarChangeListener;

import com.android.kino.R;
import com.android.kino.logic.AlbumProperties;
import com.android.kino.logic.KinoMediaPlayer;
import com.android.kino.logic.Playlist;
import com.android.kino.utils.ConvertUtils;

public class PlayerMain extends KinoUI implements OnSeekBarChangeListener{			
	
	final private int BTNTIMEOUT=6000;
	
	private boolean updatingSeekBar=false;
	private SeekBar songSeek=null;
	ImageView btn_play=null;
	ImageView btn_forward=null;
	ImageView btn_back=null;	
	ImageView playerBGview =null;	
	
	Button btn_return=null;
	Button btn_repeat=null;
	Button btn_shuffle=null;
	
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
                	fadeinMediaButtons();
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
	
	private void fadeinMediaButtons(){
        fadein_partial(btn_play);
        
        scheduleTask(rHideButtons,BTNTIMEOUT);
        
	}
	
	private void fadeInMenuButtons(){
        fadein_partial(btn_return);
        fadein_partial(btn_shuffle);
        fadein_partial(btn_repeat);
	}
	
	private void fadeoutButtons(){				
        fadeout_partial(btn_play);
     //   fadeout_partial(btn_forward);
     //   fadeout_partial(btn_back);
        
	}

	protected void initSongDetails(){
	    
		song = mPlayer.getCurrentMedia();
		
		
		btn_return= (Button) this.findViewById(R.id.btn_return);
		btn_return.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Playlist playlist=mPlayer.getPlaylist();
				
				Intent playlistIntent=new Intent(PlayerMain.this,MenuPlaylist.class);	
				playlistIntent.putExtra("playlist",(Parcelable)playlist);
				if (playlist.getAlbumTitle()!=null){
					playlistIntent.putExtra("albumPlaylist",true);
					playlistIntent.putExtra("albumTitle",playlist.getAlbumTitle());
					playlistIntent.putExtra("albumArtist",playlist.getArtistTitle());
					playlistIntent.putExtra("albumYear",playlist.getAlbumYear());					}				    									
				
	    		startActivity(playlistIntent);    		      
				
			}
		});
		
		btn_shuffle= (Button) this.findViewById(R.id.btn_shuffle);   
		btn_shuffle.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                if (mPlayer.isShuffle()){
                	mPlayer.setShuffle(false);
                }
                else{
                	mPlayer.setShuffle(true);
                }
                 
                setShuffle();
            }
        });
		
		btn_repeat= (Button) this.findViewById(R.id.btn_repeat);
		btn_repeat.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                if (mPlayer.getRepeatMode()==KinoMediaPlayer.RepeatMode.ALL){
                	mPlayer.setRepeatMode(KinoMediaPlayer.RepeatMode.ONE);
                }
                else if (mPlayer.getRepeatMode()==KinoMediaPlayer.RepeatMode.ONE){
                	mPlayer.setRepeatMode(KinoMediaPlayer.RepeatMode.OFF);
                }
                else if (mPlayer.getRepeatMode()==KinoMediaPlayer.RepeatMode.OFF){
                	mPlayer.setRepeatMode(KinoMediaPlayer.RepeatMode.ALL);
                }
                setRepeat();
            }
        });
		
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
	    setShuffle();
	    setRepeat();
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
	
	private void setShuffle(){
		if (mPlayer.isShuffle()){
			btn_shuffle.setBackgroundDrawable(this.getResources().getDrawable(R.drawable.icn_shuffle));
		}
		else{
			btn_shuffle.setBackgroundDrawable(this.getResources().getDrawable(R.drawable.icn_shuffleoff));			
		}
	}
	
	private void setRepeat(){
		if (mPlayer.getRepeatMode()==KinoMediaPlayer.RepeatMode.OFF){
			btn_repeat.setBackgroundDrawable(this.getResources().getDrawable(R.drawable.icn_repeatoff));
		}
		else if (mPlayer.getRepeatMode()==KinoMediaPlayer.RepeatMode.ALL){
			btn_repeat.setBackgroundDrawable(this.getResources().getDrawable(R.drawable.icn_repeatall));
		}
		else if (mPlayer.getRepeatMode()==KinoMediaPlayer.RepeatMode.ONE){
			btn_repeat.setBackgroundDrawable(this.getResources().getDrawable(R.drawable.icn_repeatone));
		}
	}
	
	@Override
	protected void onResume() {	
		super.onResume();
		setPausePlay();
		fadeinMediaButtons();
		fadeInMenuButtons();
	}	
	        
	
}
