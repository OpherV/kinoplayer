package com.android.kino.logic;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.cmc.music.metadata.IMusicMetadata;
import org.cmc.music.metadata.MusicMetadataSet;
import org.cmc.music.myid3.MyID3;

import android.graphics.Bitmap;
import android.os.IBinder;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.android.kino.musiclibrary.Library.LibraryBinder;
import com.android.kino.ui.KinoUI;
import com.android.kino.utils.CompareUtils;
import com.android.kino.utils.ConvertUtils;

public class MediaProperties implements Comparable<MediaProperties>, Parcelable {

    public String Filename = null;
    public String Title = null;
    public String Artist = null;
    public AlbumProperties Album =null;
    public int TrackNumber = 0;
    public String Genre = null;
    public int Duration = 0;
    public int BitRate = 0;
    public Bitmap albumImage=null;
    public Bitmap artistImage=null;
        
    private LibraryBinder libraryBinder=null;   
        
    
    public MediaProperties(String filename,LibraryBinder libbinder) {
        Filename = filename;
        libraryBinder=libbinder;
        
        // Read ID3 tags and fill parameters
        MusicMetadataSet mp3set;
        try {
            mp3set = new MyID3().read(new File(Filename));
        }
        catch (IOException e) {
            Log.e(getClass().getName(), "Failed to read file " + Filename, e);
            return;
        }
        IMusicMetadata mp3data = mp3set.getSimplified();
        Title = mp3data.getSongTitle();
        Artist = mp3data.getArtist();
        
        Album = libraryBinder.getLibrary().getAlbumFromCache(Artist,mp3data.getAlbum(),ConvertUtils.tryParse(mp3data.getYear()));
        
        Number trackNum = mp3data.getTrackNumber();
        if (trackNum != null) {
            TrackNumber = trackNum.intValue();
        }
        else {
            TrackNumber = 0;
        }
        Genre = mp3data.getGenre();
        try {
            Duration = ConvertUtils.tryParse(mp3data.getDurationSeconds());
        }
        catch (Exception e) {
            Duration = 0;
        }
        BitRate = 0;
    }

    public MediaProperties(LibraryBinder libbinder,
    					   String filename,
                           String title,
                           String artist,                           
                           String albumTitle,
                           int albumYear,
                           int trackNumber,
                           String genre,
                           int duration,
                           int bitrate) {
        libraryBinder=libbinder;
        Filename = filename;
        Title = title;        
        Artist = artist;
        
        Album = libraryBinder.getLibrary().getAlbumFromCache(Artist,albumTitle,albumYear);               
        TrackNumber = trackNumber;
        Genre = genre;
        Duration = duration;
        BitRate = bitrate;
    }

    //if the mediproperties was parcelled it means that the album already exists in the album cache
    public MediaProperties(Parcel in) {    	
    	     	
        Filename = in.readString();
        Title = in.readString();        
        Artist = in.readString();   
        
        String interimAlbumTitle=in.readString();
        int interimAlbumYear=in.readInt();
        
        TrackNumber = in.readInt();
        Genre = in.readString();
        Duration = in.readInt();
        BitRate = in.readInt();
        
        ArrayList<IBinder> binderList = in.createBinderArrayList()   ;         
        libraryBinder=(LibraryBinder) binderList.get(0);
        
        Album = libraryBinder.getLibrary().getAlbumFromCache(Artist,interimAlbumTitle,interimAlbumYear);       
           
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(Filename);
        dest.writeString(Title);
        dest.writeString(Artist);
        dest.writeString(Album.getAlbumName());
        dest.writeInt(Album.getAlbumYear());
        dest.writeInt(TrackNumber);
        dest.writeString(Genre);
        dest.writeInt(Duration);
        dest.writeInt(BitRate);
        
        ArrayList<IBinder> binderList = new ArrayList<IBinder>();
        binderList.add(libraryBinder);
        dest.writeBinderList(binderList);
    }
    
    @Override
    public int compareTo(MediaProperties another) {
        int result = Album.compareTo(another.Album);
        if (result != 0) {
            return result;
        }
        result = CompareUtils.compareWithNulls(Artist, another.Artist);
        if (result != 0) {
            return result;
        }
        result = CompareUtils.compareWithNulls(Title, another.Title);
        if (result != 0) {
            return result;
        }
        result = CompareUtils.compareWithNulls(Genre, another.Genre);
        if (result != 0) {
            return result;
        }
        result = Duration - another.Duration;
        if (result != 0) {
            return result;
        }
        result = TrackNumber - another.TrackNumber;
        if (result != 0) {
            return result;
        }
        result = BitRate - another.BitRate;
        if (result != 0) {
            return result;
        }
        return result;
    }
    
    
    //these functions may return null and start an image fetching process, if the image does not yet exist
    public Bitmap getArtistImage(KinoUI kinoui){
		if (artistImage!=null){
			return artistImage;
		}
		
    	ArtistProperties artist=kinoui.library.getArtistFromCache(Artist);
    	artistImage=artist.getArtistImage(kinoui);
    	
    	return artistImage;
    }
    
    public Bitmap getAlbumImage(KinoUI kinoui){
		if (albumImage!=null){
			return albumImage;
		}		    	
    	albumImage=Album.getAlbumImage(kinoui);
		
		return albumImage;
    }
    
    
    @Override
    public String toString() {
        return Artist + " - " + Title;
    }

    public static final Parcelable.Creator<MediaProperties> CREATOR = new Parcelable.Creator<MediaProperties>() {
        public MediaProperties createFromParcel(Parcel in) {
            return new MediaProperties(in);
        }

        public MediaProperties[] newArray(int size) {
            return new MediaProperties[size];
        }
    };
}
