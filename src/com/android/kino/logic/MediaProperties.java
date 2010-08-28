package com.android.kino.logic;

import java.io.File;
import java.io.IOException;

import org.cmc.music.metadata.IMusicMetadata;
import org.cmc.music.metadata.MusicMetadataSet;
import org.cmc.music.myid3.MyID3;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.android.kino.utils.CompareUtils;
import com.android.kino.utils.ConvertUtils;

public class MediaProperties implements Comparable<MediaProperties>, Parcelable {

    public String Filename = null;
    public String Title = null;
    public String Artist = null;
    public AlbumProperties Album = new AlbumProperties();
    public int TrackNumber = 0;
    public String Genre = null;
    public int Duration = 0;
    public int BitRate = 0;
    
    public MediaProperties() {
        // Empty on purpose
    }
    
    public MediaProperties(String filename) {
        Filename = filename;
        
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
        Album.Title = mp3data.getAlbum();
        Album.Year = ConvertUtils.tryParse(mp3data.getYear());
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

    public MediaProperties(String filename,
                           String title,
                           String artist,                           
                           String albumTitle,
                           int albumYear,
                           int trackNumber,
                           String genre,
                           int duration,
                           int bitrate) {
        Filename = filename;
        Title = title;        
        Artist = artist;
        Album.Title = albumTitle;
        Album.Year = albumYear;
        TrackNumber = trackNumber;
        Genre = genre;
        Duration = duration;
        BitRate = bitrate;
    }

    public MediaProperties(Parcel in) {
        Filename = in.readString();
        Title = in.readString();        
        Artist = in.readString();
        Album.Title = in.readString();
        Album.Year = in.readInt();
        TrackNumber = in.readInt();
        Genre = in.readString();
        Duration = in.readInt();
        BitRate = in.readInt();
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
        dest.writeString(Album.Title);
        dest.writeInt(Album.Year);
        dest.writeInt(TrackNumber);
        dest.writeString(Genre);
        dest.writeInt(Duration);
        dest.writeInt(BitRate);
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
    
    @Override
    public String toString() {
        return Artist + " - " + Title;
    }
    
    public class AlbumProperties implements Comparable<AlbumProperties> {
        public String Title = null;
        public int Year = 0;
        
        @Override
        public int compareTo(AlbumProperties another) {
            int result = CompareUtils.compareWithNulls(Title, another.Title);
            if (result != 0) {
                return result;
            }
            return Year - another.Year;
        }
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
