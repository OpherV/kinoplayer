package com.android.kino.logic;

import java.io.File;

import com.android.kino.Kino;
import com.android.kino.logic.tasks.FetchAlbumDetails;
import com.android.kino.ui.KinoUI;
import com.android.kino.utils.CompareUtils;
import com.android.kino.utils.ConvertUtils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

public class AlbumProperties implements Parcelable, Comparable<AlbumProperties>  {

	private String mAlbumName;
	private String mArtistName;	
	private int mAlbumYear;
	private Bitmap albumImage;
	private boolean searchingForAlbumImage=false;
	    
    
    @Override
    public int compareTo(AlbumProperties another) {
        int result = CompareUtils.compareWithNulls(mAlbumName, another.getAlbumName());
        if (result != 0) {
            return result;
        }
        return getAlbumYear() - another.getAlbumYear();
    }
   
    public int getAlbumYear(){
		return mAlbumYear;
    }	
	
	public String getAlbumName(){
		return mAlbumName;
	}
	
	public String getArtistName(){
		return mArtistName;
	}
	
	public int getYear(){
		return mAlbumYear;
	}
	
	public void setYear(int year){
		mAlbumYear=year;		
	}
	
	public void setTitle(String title){
		mAlbumName=title;
	}
	
	public void setArtist(String artist){
		mArtistName=artist;
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
	
    public Bitmap getAlbumImage(KinoUI kinoui){
		if (albumImage!=null){
			return albumImage;
		}

		//TODO make sure that the SDcard is properly mounted    	
		String albumImagePath=Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+Kino.ALBUM_DIR;
		String albumFileName=ConvertUtils.safeFileName(mArtistName)+"-"+ConvertUtils.safeFileName(mAlbumName)+".jpg";
		File albumImageFile = new File(albumImagePath,albumFileName);
		
		//TODO obviously, change this
		if (albumImageFile.exists()){
			albumImage = BitmapFactory.decodeFile(albumImageFile.getAbsolutePath());
		}
		else{
			if (!searchingForAlbumImage){				
				Log.e(kinoui.getClass().getName(),"no artist image file: "+albumImagePath+"/"+albumFileName);
				TaskMasterService taskmaster=kinoui.getTaskMaster();
				taskmaster.addTask(new FetchAlbumDetails(mArtistName, mAlbumName));
				searchingForAlbumImage=true;
			}
			
		}
		
		return albumImage;
    }
	
	
}
