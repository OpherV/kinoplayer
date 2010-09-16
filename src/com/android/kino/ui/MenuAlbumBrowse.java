package com.android.kino.ui;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Parcelable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.android.kino.Kino;
import com.android.kino.R;
import com.android.kino.logic.AlbumList;
import com.android.kino.logic.AlbumProperties;
import com.android.kino.logic.ArtistList;
import com.android.kino.logic.ArtistProperties;
import com.android.kino.logic.Playlist;
import com.android.kino.ui.listAdapters.AlbumAdapter;
import android.view.View.OnClickListener;

public class MenuAlbumBrowse extends KinoUI implements OnItemClickListener {

	 private ArrayAdapter<AlbumProperties> albumListAdapter=null;
	    AlbumList albumList=null;
	    TextView artistTitleView;
	    View artistTitleContainer;
	    ArtistProperties artist;
	    boolean mBGImageSet=false;
	    
	    Button btn_return=null;
	    
		@Override
		protected void initUI() {
			super.initUI();
			
			setContentView(R.layout.menu_albumbrowse);		
						
			btn_return= (Button) this.findViewById(R.id.btn_return);
			
			
		}		
		
		@Override
		public void onKinoInit(Kino kino) {		
			super.onKinoInit(kino);
			
			
			ArrayList<String> albumTitles = getIntent().getExtras().getStringArrayList("albumTitleList");
			ArrayList<String> albumArtists= getIntent().getExtras().getStringArrayList("albumArtistList");
			int[] albumYears= getIntent().getExtras().getIntArray("albumYears");
			final String artistTitle=getIntent().getExtras().getString("artistTitle");
			albumList=new AlbumList(artistTitle);
			
			for (int i=0;i<albumTitles.size();i++){
				albumList.add(library.getAlbumFromCache(albumArtists.get(i),
														albumTitles.get(i),
														albumYears[i]) );
			}
			
			
			btn_return.setOnClickListener(new OnClickListener() { 
				
				@Override
				public void onClick(View v) {

					
					if (artistTitle!=null){
						Intent albumlistIntent=new Intent(MenuAlbumBrowse.this,MenuArtistBrowse.class);
						ArtistList allArtists=library.getAllArtists();
						
						ArrayList<String> artistNames = new ArrayList<String>();
						for (ArtistProperties artist : allArtists){
							artistNames.add(artist.getName());
						}
						
						albumlistIntent.putExtra("artistlist",artistNames);						
			    		startActivity(albumlistIntent);
					}
					else{
						Intent mainMenuIntent=new Intent(MenuAlbumBrowse.this,MenuMain.class);											
			    		startActivity(mainMenuIntent);	
					}
					
				}
			});

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
			
			TextView txt_nodata = (TextView) findViewById(R.id.txt_nodata);
			if  (albumListAdapter.getCount()==0){
				txt_nodata.setVisibility(View.VISIBLE);
				txt_nodata.setText("No albums in library");				
			}
			else{
				txt_nodata.setVisibility(View.GONE);
			}
			
			
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
			
			if (albumList.getArtistTitle()!=null && !mBGImageSet){	
		        ImageView artistImageBG = (ImageView) findViewById(R.id.menu_albumbrowse_bgimage);		        
		        artistImageBG.setImageBitmap(artist.getArtistImage(this));
		        mBGImageSet=true;
			}
		}
		
}
