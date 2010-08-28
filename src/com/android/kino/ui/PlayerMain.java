package com.android.kino.ui;

import com.android.kino.Kino;
import com.android.kino.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class PlayerMain extends Activity {
	private Kino kino = Kino.getKino(this);
	private KinoMediaPlayer mPlayer;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {	
		super.onCreate(savedInstanceState);
		setContentView(R.layout.player_main);
		
		(TextView) View songDetails = findViewById(R.id.song_details);		
		
	}
}
