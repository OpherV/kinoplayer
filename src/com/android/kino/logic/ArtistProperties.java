package com.android.kino.logic;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

public class ArtistProperties implements Parcelable{
	private String mArtistName=null;
	private int mTotalSongs;
	private Bitmap mArtistImage=null;
	
	public ArtistProperties(String artistName,int totalSongs){
		mArtistName=artistName;
		mTotalSongs=totalSongs;
	}
	
    public ArtistProperties(Parcel in) {        
        mArtistName = in.readString();
        mTotalSongs = in.readInt();
        mArtistImage=null;
    }
	
	public String getName(){
		 return mArtistName;
	}
	
	public int getTotalSongs(){
		return mTotalSongs;
	}
	
	 public static final Parcelable.Creator<ArtistProperties> CREATOR = new Parcelable.Creator<ArtistProperties>() {
	        public ArtistProperties createFromParcel(Parcel in) {
	            return new ArtistProperties(in);
	        }

	        public ArtistProperties[] newArray(int size) {
	            return new ArtistProperties[size];
	        }
	    };

	@Override
	public int describeContents() {		
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(mArtistName);
		dest.writeInt(mTotalSongs);		
		
	}
	
}
