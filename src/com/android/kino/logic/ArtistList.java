package com.android.kino.logic;

import java.util.LinkedList;

import android.os.Parcel;
import android.os.Parcelable;

public class ArtistList extends LinkedList<ArtistProperties> implements Parcelable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public ArtistList(){
		
	}
	
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	@Override
	public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(size());
        ArtistProperties[] array = new ArtistProperties[size()];
        dest.writeTypedArray(toArray(array), flags);
		
	}
	
    public ArtistList(Parcel in) {
    	ArtistProperties[] array = new ArtistProperties[in.readInt()];
        in.readTypedArray(array, ArtistProperties.CREATOR);
        for (ArtistProperties item : array) {
            add(item);
        }
    }
    
    public static final Parcelable.Creator<ArtistList> CREATOR = new Parcelable.Creator<ArtistList>() {
        public ArtistList createFromParcel(Parcel in) {
            return new ArtistList(in);
        }

        public ArtistList[] newArray(int size) {
            return new ArtistList[size];
        }
    };

}
