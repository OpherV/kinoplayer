package com.android.kino.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.android.kino.Kino;
import com.android.kino.R;
import com.android.kino.logic.KinoMediaPlayer;
import com.android.kino.logic.KinoUser;

public class PlayerMain extends Activity implements KinoUser{
	private Kino kino = null;
	private KinoMediaPlayer mPlayer;
	
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
                updateSongDetails();
            }
        });
		
        Button btn_forward = (Button) findViewById(R.id.btn_forward);
        btn_forward.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                mPlayer.next();
                updateSongDetails();
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

	private void updateSongDetails(){
	    TextView songDetails = (TextView) this.findViewById(R.id.song_details);    
	    songDetails.setText(mPlayer.getCurrentMedia().toString());
	    // TODO call updateTrack something
	}
	
    @Override
    public void onKinoInit(Kino kino) {
        mPlayer= kino.getPlayer();
        updateSongDetails();
        
    }
}
