package com.android.kino.ui;

import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.SeekBar.OnSeekBarChangeListener;

import com.android.kino.R;
import com.android.kino.utils.ConvertUtils;

public class PlayerMain extends KinoUI implements OnSeekBarChangeListener{			
	
	private boolean updatingSeekBar=false;
	private SeekBar songSeek=null;


	
	@Override
	protected void initUI(){		
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
		        			          
	}

	protected void initSongDetails(){
	    
		song = mPlayer.getCurrentMedia();
		
		
		//TODO properly make image getting, as to not hinder performance
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
	    	    
	    
	    timeElapsed = (TextView) this.findViewById(R.id.player_timeElapsed);    
	    timeElapsed.setText(ConvertUtils.formatTime(mPlayer.getCurrentTrackDurationInSeconds()));
	    
	    songSeek.setOnSeekBarChangeListener(this);	    	    
	    	  

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
}
