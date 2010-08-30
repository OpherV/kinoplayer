package com.android.kino.logic;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

public class AlbumProperties implements Parcelable {

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
	
    public AlbumProperties(Parcel in) {
        mAlbumName = in.readString();
        mArtistName = in.readString();
        mAlbumYear = in.readInt();
        albumImage=null;
    }
	
	 public static final Parcelable.Creator<AlbumProperties> CREATOR = new Parcelable.Creator<AlbumProperties>() {
	        public AlbumProperties createFromParcel(Parcel in) {
	            return new AlbumProperties(in);
	        }

	        public AlbumProperties[] newArray(int size) {
	            return new AlbumProperties[size];
	        }
	    };

	@Override
	public int describeContents() {		
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(mAlbumName);
		dest.writeString(mArtistName);
		dest.writeInt(mAlbumYear);
		
	}
	
	
}
