package com.android.kino.ui;

import java.util.LinkedList;

import android.content.Intent;
import android.os.Parcelable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.android.kino.R;
import com.android.kino.logic.AlbumList;
import com.android.kino.logic.AlbumProperties;
import com.android.kino.logic.ArtistList;
import com.android.kino.logic.ArtistProperties;
import com.android.kino.ui.listAdapters.AlbumAdapter;
import com.android.kino.ui.listAdapters.ArtistAdapter;

public class MenuArtistBrowse extends KinoUI implements OnItemClickListener {

	 private ArrayAdapter<ArtistProperties> artistListAdapter=null;
	    ArtistList artistList=null;
	    
		@Override
		protected void initUI() {

			setContentView(R.layout.menu_artistbrowse);		
			artistList=getIntent().getExtras().getParcelable("artistlist");
		
			artistListAdapter = new ArtistAdapter(this,0, artistList);
			
			
			ListView albumlistView = (ListView)findViewById(R.id.artistlist);
			albumlistView.setAdapter(artistListAdapter);			
			
			albumlistView.setOnItemClickListener(this);
			
		}
	    
	    @Override
	    public void onItemClick(AdapterView<?> parent, View view,
	                            int position, long id) {
	       
			ArtistProperties artistClicked = (ArtistProperties) parent.getItemAtPosition(position);			
			AlbumList albums=library.getAlbumsByArtist(artistClicked.getName());
			
			Intent albumlistIntent=new Intent(this,MenuAlbumBrowse.class);			
			//TODO fetch the allsongs playlist from the library and pass it
			albumlistIntent.putExtra("albumlist",(Parcelable)albums);						
    		startActivity(albumlistIntent);
			       
	       
	    }        	    		
	
}
