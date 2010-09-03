package com.android.kino.logic;

import java.util.LinkedList;

public class AlbumList extends LinkedList<AlbumProperties>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String mArtistTitle=null;
	
	public String getArtistTitle(){
		return mArtistTitle;
	}
	
	public AlbumList(){		
	}

	
	public AlbumList(String artistTitle){
		mArtistTitle=artistTitle;
	}



}
