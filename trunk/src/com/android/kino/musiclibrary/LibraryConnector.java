package com.android.kino.musiclibrary;

import com.android.kino.Kino;
import com.android.kino.MediaPlayerService;

import android.content.ComponentName;
import android.content.Context;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class LibraryConnector implements ServiceConnection{
	
	private Kino kino;
	
	public LibraryConnector(Kino kinoObj){
		kino=kinoObj;
	}
	
	@Override
	public void onServiceConnected(ComponentName playerService, IBinder service) {	
		((Library) service).kino=kino;
		Log.d("Library","Please 1");
		Log.d(this.getClass().toString(), "library service connected");
	}

	@Override
	public void onServiceDisconnected(ComponentName playerService) {
		//Toast.makeText(mPlayer, "Library disconnected :(", Toast.LENGTH_SHORT).show();
		
	}

}