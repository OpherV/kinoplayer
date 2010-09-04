package com.android.kino.ui;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;

import com.android.kino.Kino;
import com.android.kino.R;
import com.android.kino.logic.AlbumProperties;
import com.android.kino.logic.ArtistProperties;

public class MenuAlbumArtView extends KinoUI {
	private ArtistProperties artist=null;
	private AlbumProperties album=null;
	
	ImageView artistImageBG=null;
	ImageView albumArt=null;

	
	private Button btn_return=null;;
	
	
	@Override
	protected void initUI() {	
		super.initUI();				
		setContentView(R.layout.menu_albumartview);
		artistImageBG = (ImageView) findViewById(R.id.menu_bgimage);                
        albumArt = (ImageView) findViewById(R.id.menu_albumImage);
        btn_return= (Button) this.findViewById(R.id.btn_return);
	}
	
	@Override
	public void onKinoInit(Kino kino) {	
		super.onKinoInit(kino);
		
		String artistTitle=getIntent().getExtras().getString("artistTitle");
		String albumTitle=getIntent().getExtras().getString("albumTitle");
		int albumYear=getIntent().getExtras().getInt("albumYear");
		
		artist=library.getArtistFromCache(artistTitle);
		album=library.getAlbumFromCache(artistTitle, albumTitle, albumYear);
				        
        
        btn_return.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {											
				MenuAlbumArtView.this.finish();				
			}
		});       
        
        updateUI();
	}
	
	@Override
	public void updateUI() {
		super.updateUI();
		artistImageBG.setImageBitmap(artist.getArtistImage(this));
		albumArt.setImageBitmap(album.getAlbumImage(this));
	}
	
}
