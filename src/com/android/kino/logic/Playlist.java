package com.android.kino.logic;

import java.util.Date;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.Random;

import android.os.Parcel;
import android.os.Parcelable;

public class Playlist extends LinkedList<MediaProperties> implements Parcelable {

    private static final long serialVersionUID = 1L;
    private String mAlbumTitle=null;
    private String mArtistTitle=null;
    private int mYear;
    
    public Playlist() {
        // Empty on purpose
    }
    
    public Playlist(String artistTitle, String albumTitle){
    	mArtistTitle=artistTitle;
    	mAlbumTitle=albumTitle;    	
    }
    
    public void setArtistTitle(String artistTitle){
    	mArtistTitle=artistTitle;
    }
    
    public void setAlbumTitle(String albumTitle){
    	mAlbumTitle=albumTitle;
    }
    
    public void setAlbumYear(int year){
    	mYear=year;
    }
    
    public String getAlbumTitle(){
    	return mAlbumTitle;
    }
    
    public String getArtistTitle(){
    	return mArtistTitle;
    }        
    
    public int getAlbumYear(){
    	return mYear;
    }

    public Playlist(Parcel in) {
    	mArtistTitle=in.readString();
    	mAlbumTitle=in.readString();
    	mYear=in.readInt();
        MediaProperties[] array = new MediaProperties[in.readInt()];
        in.readTypedArray(array, MediaProperties.CREATOR);
        for (MediaProperties item : array) {
            add(item);
        }
    }   

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
    	dest.writeString(mArtistTitle);
    	dest.writeString(mAlbumTitle);
    	dest.writeInt(mYear);
        dest.writeInt(size());
        MediaProperties[] array = new MediaProperties[size()];
        dest.writeTypedArray(toArray(array), flags);
    }

    /**
     * Shuffle the list in place.
     */
    public void shuffle() {
        shuffle(new Random(new Date().getTime()));
    }

    /**
     * Shuffle the list in place.
     * Using a specified random object.
     */
    public void shuffle(Random random) {
        shuffleList(this, random);
    }

    public ListIterator<MediaProperties> playlistIterator() {
        return playlistIterator(0);
    }

    public ListIterator<MediaProperties> playlistIterator(int position) {
        return listIterator(position);
    }

    public ListIterator<MediaProperties> randomIterator() {
        return randomIterator(0);
    }

    public ListIterator<MediaProperties> randomIterator(int position) {
        return new RandomIterator(position, new Random(new Date().getTime()));
    }
    
    public String toString() {
        return "Playlist(size: " + size() + ")";
    }
    
    private static <T> void shuffleList(LinkedList<T> list, Random random) {
        for (int i = list.size(); i > 1; --i) {
            list.add(list.remove(random.nextInt(i)));
        }
    }
    
    // TODO Add sort or iterators as needed by the UI (by track#, by artist,
    //      by title, by playing order etc.)
    
    private class RandomIterator implements ListIterator<MediaProperties> {
        
        private ListIterator<Integer> mOrder;

        public RandomIterator(int position, Random random) {
            LinkedList<Integer> order = new LinkedList<Integer>();
            for (int i = 0; i < Playlist.this.size(); ++i) {
                if (i != position) {
                    order.add(i);
                }
            }
            shuffleList(order, random);
            order.addFirst(position);
            mOrder = order.listIterator();
        }
        
        @Override
        public boolean hasNext() {
            return mOrder.hasNext();
        }

        @Override
        public MediaProperties next() {
            return Playlist.this.get(mOrder.next());
        }

        @Override
        public int nextIndex() {
            try {
                int next = mOrder.next();
                mOrder.previous();
                return next;
            }
            catch (NoSuchElementException e) {
                // Behave like listIterator
                return Playlist.this.size();
            }
        }

        @Override
        public boolean hasPrevious() {
            return mOrder.hasPrevious();
        }

        @Override
        public MediaProperties previous() {
            return Playlist.this.get(mOrder.previous());
        }

        @Override
        public int previousIndex() {
            try {
                int prev = mOrder.previous();
                mOrder.next();
                return prev;
            }
            catch (NoSuchElementException e) {
                // Behave like listIterator
                return -1;
            }
        }

        @Override
        public void add(MediaProperties arg0) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void set(MediaProperties object) {
            throw new UnsupportedOperationException();
        }
    }

    public static final Parcelable.Creator<Playlist> CREATOR = new Parcelable.Creator<Playlist>() {
        public Playlist createFromParcel(Parcel in) {
            return new Playlist(in);
        }

        public Playlist[] newArray(int size) {
            return new Playlist[size];
        }
    };

}
