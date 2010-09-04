package com.android.kino.ui;

import java.util.ArrayList;

import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

import com.android.kino.Kino;
import com.android.kino.R;
import com.android.kino.logic.AlbumList;
import com.android.kino.logic.ArtistList;
import com.android.kino.logic.ArtistProperties;
import com.android.kino.ui.listAdapters.ArtistAdapter;

public class MenuArtistBrowse extends KinoUI implements OnItemClickListener {

	 private ArrayAdapter<ArtistProperties> artistListAdapter=null;
	    ArtistList artistList=new ArtistList();
	    Button btn_return=null;
	    
		@Override
		protected void initUI() {
			super.initUI();
			setContentView(R.layout.menu_artistbrowse);
		}
	    
	    @Override
	    public void onItemClick(AdapterView<?> parent, View view,
	                            int position, long id) {
	       
			ArtistProperties artistClicked = (ArtistProperties) parent.getItemAtPosition(position);			
			AlbumList albums=library.getAlbumsByArtist(artistClicked.getName());
			
			Intent albumlistIntent=new Intent(this,MenuAlbumBrowse.class);			
			
			ArrayList<String> albumTitles = new ArrayList<String>();
			ArrayList<String> albumArtists= new ArrayList<String>();
			int[] albumYears = new int[albums.size()];
		
			for (int i=0;i<albums.size();i++){
				albumTitles.add(albums.get(i).getAlbumName());
				albumArtists.add(albums.get(i).getArtistName());
				albumYears[i]=albums.get(i).getAlbumYear();
				
			}
			
			albumlistIntent.putExtra("artistTitle",artistClicked.getName());
			albumlistIntent.putExtra("albumTitleList",albumTitles);
			albumlistIntent.putExtra("albumArtistList",albumArtists);
			albumlistIntent.putExtra("albumYears",albumYears);
    		startActivity(albumlistIntent);
			       
	       
	    }  
	    
	    @Override
	    public void onKinoInit(Kino kino) {	    
	    	super.onKinoInit(kino);
	    	
	    	ArrayList<String> artistNameList=getIntent().getExtras().getStringArrayList("artistlist");
			for(String artistName : artistNameList ){
				artistList.add(library.getArtistFromCache(artistName) );
			}			
		
			artistListAdapter = new ArtistAdapter(this,0, artistList);
			
			
			ListView albumlistView = (ListView)findViewById(R.id.artistlist);
			albumlistView.setAdapter(artistListAdapter);			
						
			btn_return= (Button) this.findViewById(R.id.btn_return);

			btn_return.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {											
					Intent mainMenuIntent=new Intent(MenuArtistBrowse.this,MenuMain.class);											
		    		startActivity(mainMenuIntent);
					
				}
			});
			
			
			albumlistView.setOnItemClickListener(this);
	    	
	    }
		
		@Override
		public void updateUI() {	
			super.updateUI();
			artistListAdapter.notifyDataSetChanged();
		}

}
