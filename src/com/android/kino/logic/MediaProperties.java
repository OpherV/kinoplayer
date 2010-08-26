package com.android.kino.logic;

import org.farng.mp3.MP3File;
import org.farng.mp3.id3.AbstractID3v2;

import com.android.kino.utils.CompareUtils;
import com.android.kino.utils.ConvertUtils;

public class MediaProperties implements Comparable<MediaProperties> {

    public AlbumProperties Album = new AlbumProperties();
    public String Title = null;
    public int TrackNumber = 0;
    public String Genre = null;
    public int Duration = 0;
    public int BitRate = 0;
    
    public MediaProperties() {
        // Empty on purpose
    }
    
    public MediaProperties(String filename) {
        // TODO Untested
        // Read ID3 tags and fill parameters
        try {
            MP3File mp3file = new MP3File(filename);
            AbstractID3v2 idv2 = mp3file.getID3v2Tag();
            Album.Year = ConvertUtils.tryParse(idv2.getYearReleased());
            Album.Title = idv2.getAlbumTitle();
            Album.Artist = idv2.getLeadArtist();
            if (CompareUtils.isNullOrEmpty(Album.Title)) {
                Album.Artist = idv2.getAuthorComposer();
            }
            Title = idv2.getSongTitle();
            TrackNumber = ConvertUtils.tryParse(idv2.getTrackNumberOnAlbum());
            Genre = idv2.getSongGenre();
            Duration = idv2.getSize();
            BitRate = mp3file.getBitRate();
            // TODO: Read missing tags from ID3v1
            // TODO: If artist and title not found in tags, take it from the
            //       mp3file object (it has methods for it)
        }
        catch (Exception e) {
            // Can't read file tags, try to take the artist name and track
            //  title out of the filename
            String[] parts = filename.split("-");
            if (parts.length == 1) {
                Title = parts[0];
            }
            else {
                Album.Artist = parts[0].trim();
                Title = parts[1];
                for (int i = 2; i < parts.length; ++i) {
                    Title += parts[i];
                }
                Title = Title.trim();
            }
        }
    }

    @Override
    public int compareTo(MediaProperties another) {
        int result = Album.compareTo(another.Album);
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
    
    public class AlbumProperties implements Comparable<AlbumProperties> {
        public String Artist = null;
        public String Title = null;
        public int Year = 0;
        
        @Override
        public int compareTo(AlbumProperties another) {
            int result = CompareUtils.compareWithNulls(Artist, another.Artist);
            if (result != 0) {
                return result;
            }
            result = CompareUtils.compareWithNulls(Title, another.Title);
            if (result != 0) {
                return result;
            }
            return Year - another.Year;
        }
    }
}
