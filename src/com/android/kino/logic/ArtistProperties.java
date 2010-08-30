package com.android.kino.logic;

import android.graphics.Bitmap;

public class ArtistProperties {
	private String mArtistName=null;
	private int mTotalSongs;
	private Bitmap mArtistImage=null;
	
	public ArtistProperties(String artistName,int totalSongs){
		mArtistName=artistName;
		mTotalSongs=totalSongs;
	}
	
	public String getName(){
		 return mArtistName;
	}
	
	public int getTotalSongs(){
		return mTotalSongs;
	}
	
}
