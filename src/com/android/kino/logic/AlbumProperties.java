package com.android.kino.logic;

import android.graphics.Bitmap;

public class AlbumProperties {

	private String mAlbumName;
	private String mArtistName;
	private int mAlbumYear;
	private Bitmap albumImage; 
	
	public String getAlbumName(){
		return mAlbumName;
	}
	
	public String getArtistName(){
		return mArtistName;
	}
	
	public int getYear(){
		return mAlbumYear;
	}
	
	public AlbumProperties(String albumName, String artistName, int albumYear){
		mAlbumName=albumName;
		mAlbumYear=albumYear;
		mArtistName=artistName;
	}
	
	
}
