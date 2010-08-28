package com.android.kino.ui;

import android.R;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import com.android.kino.Kino;
import com.android.kino.logic.KinoUser;
import com.android.kino.logic.Playlist;
import com.android.kino.musiclibrary.Library;

public class MenuMain extends ListActivity implements KinoUser {

	static final String[] MENU_ITMES = new String[] { "All Songs", "Music Library",
			"Settings" };
	
	private Kino kino;
	private Library library;	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {	    
        Toast.makeText(this, "MenuMain", Toast.LENGTH_SHORT).show();
		super.onCreate(savedInstanceState);
		
		kino = Kino.getKino(MenuMain.this);
		library = kino.getLibrary();

		setListAdapter(new ArrayAdapter<String>(this,
				R.layout.simple_list_item_1, MENU_ITMES));

		ListView lv = getListView();
		lv.setTextFilterEnabled(true);

		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				
				//this is ugly, but for the moment...
				CharSequence menuText=((TextView) view).getText();
				if  (menuText=="All Songs"){			
						Intent playlistIntent=new Intent(parent.getContext(),MenuPlaylist.class);
						Playlist pAllsongs = library.getAllSongs();
						//TODO fetch the allsongs playlist from the library and pass it
						playlistIntent.putExtra("playlist",(Parcelable)pAllsongs);						
			    		startActivity(playlistIntent);			    		
			    }
				
			}

		});
		
		kino.registerUser(this);
	}
	
	@Override
	protected void onDestroy() {
        kino.showNotification();
        super.onDestroy();
	}

    @Override
    public void onKinoInit(Kino kino) {
        Toast.makeText(this, "onKinoInit", Toast.LENGTH_SHORT).show();
        library = kino.getLibrary();
    }

}
