package com.android.kino.logic;

import java.util.LinkedList;

import android.os.Parcel;
import android.os.Parcelable;

public class AlbumList extends LinkedList<AlbumProperties> implements Parcelable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String mArtistTitle=null;
	
	public String getArtistTitle(){
		return mArtistTitle;
	}
	
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	public AlbumList(){		
	}

	
	public AlbumList(String artistTitle){
		mArtistTitle=artistTitle;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {        
        dest.writeString(mArtistTitle);
        dest.writeInt(size());
        AlbumProperties[] array = new AlbumProperties[size()];
        dest.writeTypedArray(toArray(array), flags);
		
	}
	
    public AlbumList(Parcel in) {
    	mArtistTitle = in.readString();
    	AlbumProperties[] array = new AlbumProperties[in.readInt()];
        in.readTypedArray(array, AlbumProperties.CREATOR);
        for (AlbumProperties item : array) {
            add(item);
        }
    }
    
    public static final Parcelable.Creator<AlbumList> CREATOR = new Parcelable.Creator<AlbumList>() {
        public AlbumList createFromParcel(Parcel in) {
            return new AlbumList(in);
        }

        public AlbumList[] newArray(int size) {
            return new AlbumList[size];
        }
    };

}
