package com.android.kino.ui.listAdapters;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.kino.R;
import com.android.kino.logic.AlbumList;
import com.android.kino.logic.AlbumProperties;
import com.android.kino.ui.KinoUI;
import com.android.kino.ui.MenuAlbumArtView;

public class AlbumAdapter extends ArrayAdapter<AlbumProperties> {

	private KinoUI mContext=null;
	private AlbumList albums=null;
	private int itemResource;
	
	public AlbumAdapter(Context context, int textViewResourceId,
			AlbumList objects) {
		super(context, textViewResourceId, (List<AlbumProperties>) objects);
		mContext=(KinoUI) context;
		albums=objects;
		itemResource=textViewResourceId;
	}	
	
	 @Override
	public View getView(int position, View convertView, ViewGroup parent) {
		  
		final AlbumProperties albumObj = albums.get(position);
		 
        View v=null;  
  		LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);              
        v = inflater.inflate(itemResource, null);
        

        TextView albumTitle = (TextView)  v.findViewById(R.id.albumItem_albumTitle);
        albumTitle.setText(albumObj.getAlbumName());
                
        TextView artistTitle = (TextView)  v.findViewById(R.id.albumItem_artistTitle);
        
        //differntiate between displaying artist album or general albums
        if (albums.getArtistTitle()==null){
        	artistTitle.setText(albumObj.getArtistName());
        }
        else{
        	artistTitle.setVisibility(View.GONE);
        }
                
        TextView albumYear = (TextView)  v.findViewById(R.id.albumItem_albumYear);
        
        if (albumObj.getYear()>0){
        	albumYear.setText(albumObj.getYear()+"");
        }
        else{
        	albumYear.setVisibility(View.GONE);
        }
        
        ImageView albumImage  = (ImageView) v.findViewById(R.id.albumItem_albumImage);
        Bitmap albumBitmap=albumObj.getAlbumImage(mContext);
        if (albumBitmap!=null){
        	albumImage.setImageBitmap(albumBitmap);
        }
        
        albumImage.setOnLongClickListener(new OnLongClickListener() {
			
			@Override
			public boolean onLongClick(View v) {
				Intent viewAlbumArt = new Intent(mContext,MenuAlbumArtView.class);
				viewAlbumArt.putExtra("artistTitle", albumObj.getArtistName());
				viewAlbumArt.putExtra("albumTitle", albumObj.getAlbumName());
				viewAlbumArt.putExtra("albumYear", albumObj.getAlbumYear());
				mContext.startActivity(viewAlbumArt);
				return false;
			}
		});
        
		 
		return v;
	}
	 


}
