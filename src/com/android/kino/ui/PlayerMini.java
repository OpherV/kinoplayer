package com.android.kino.ui;

import com.android.kino.R;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;

public class PlayerMini extends LinearLayout implements OnClickListener{
	private Context mContext;
	
	 public PlayerMini(Context context, AttributeSet attrs){	 
	    super(context, attrs);
		LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if(inflater != null){       
            inflater.inflate(R.layout.player_mini, this);
        }
        
        mContext=context;
        setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		mContext.startActivity(new Intent(mContext,PlayerMain.class));    
		
	}	 	

}
