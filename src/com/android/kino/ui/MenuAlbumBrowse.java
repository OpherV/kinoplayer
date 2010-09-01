package com.android.kino.ui;

import android.content.Intent;
import android.os.Parcelable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.android.kino.Kino;
import com.android.kino.R;
import com.android.kino.logic.AlbumList;
import com.android.kino.logic.AlbumProperties;
import com.android.kino.logic.ArtistProperties;
import com.android.kino.logic.Playlist;
import com.android.kino.ui.listAdapters.AlbumAdapter;

public class MenuAlbumBrowse extends KinoUI implements OnItemClickListener {

	 private ArrayAdapter<AlbumProperties> albumListAdapter=null;
	    AlbumList albumList=null;
	    TextView artistTitleView;
	    View artistTitleContainer;
	    ArtistProperties artist;
	    
		@Override
		protected void initUI() {

			setContentView(R.layout.menu_albumbrowse);		
			albumList=getIntent().getExtras().getParcelable("albumlist");
			
			
		}
		
		@Override
		public void onKinoInit(Kino kino) {		
			super.onKinoInit(kino);

			 artistTitleView = (TextView)findViewById(R.id.menu_albumbrowse_artistname);			
			 artistTitleContainer = (View)findViewById(R.id.menu_albumbrowse_titleContainer);		
			
			albumListAdapter = new AlbumAdapter(this,R.layout.item_album, albumList);
			
			//Differentiate between "artist albums" and a general album list			
			
			if (albumList.getArtistTitle()!=null){				
				artistTitleView.setText(albumList.getArtistTitle());		
						        
				artist=library.getArtistFromCache(albumList.getArtistTitle());
		        ImageView artistImageBG = (ImageView) findViewById(R.id.menu_albumbrowse_bgimage);
		        artistImageBG.setImageBitmap(artist.getArtistImage(this));
		        artistTitleContainer.setVisibility(View.VISIBLE);
			}
			
			else{				
				artistTitleContainer.setVisibility(View.GONE);
			}
			
			ListView albumlistView = (ListView)findViewById(R.id.albumlist);
			albumlistView.setAdapter(albumListAdapter);			
			
			albumlistView.setOnItemClickListener(this);
			
		}
		

	    
	    @Override
	    public void onItemClick(AdapterView<?> parent, View view,
	                            int position, long id) {
	       	       
	    	AlbumProperties albumClicked = (AlbumProperties) parent.getItemAtPosition(position);	    			
			Playlist playlist=library.getPlaylistByAlbum(albumClicked.getArtistName(), albumClicked.getAlbumName());				    	
			Intent playlistIntent=new Intent(parent.getContext(),MenuPlaylist.class);	
			playlistIntent.putExtra("playlist",(Parcelable)playlist);
			playlistIntent.putExtra("albumPlaylist",true);
			playlistIntent.putExtra("albumTitle",albumClicked.getAlbumName());
			playlistIntent.putExtra("albumArtist",albumClicked.getArtistName());
			playlistIntent.putExtra("albumYear",albumClicked.getAlbumYear());
			
    		startActivity(playlistIntent);    		      
	       
	    }  
	    
		@Override
		public void updateUI() {	
			super.updateUI();
			albumListAdapter.notifyDataSetChanged();
			
			if (albumList.getArtistTitle()!=null){							
		        ImageView artistImageBG = (ImageView) findViewById(R.id.menu_albumbrowse_bgimage);
		        artistImageBG.setImageBitmap(artist.getArtistImage(this));		        
			}
		}
		
	    
	
}
