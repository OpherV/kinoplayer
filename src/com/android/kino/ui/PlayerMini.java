package com.android.kino.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import com.android.kino.R;

public class PlayerMini extends LinearLayout{
	
	 public PlayerMini(Context context, AttributeSet attrs) {	 
	    super(context, attrs);
		LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if(inflater != null){       
            inflater.inflate(R.layout.player_mini, this);
        }
	} 	

}
