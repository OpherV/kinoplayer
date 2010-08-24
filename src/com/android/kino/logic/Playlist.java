package com.android.kino.logic;

import java.util.Date;
import java.util.LinkedList;
import java.util.Random;

public class Playlist extends LinkedList<MediaProperties> {

    private static final long serialVersionUID = 1L;

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
        for (int i = this.size(); i > 1; --i) {
            MediaProperties track = this.remove(random.nextInt(i));
            this.add(track);
        }
    }
    
    // TODO Add sort or iterators as needed by the UI (by track#, by artist,
    //      by title, by playing order etc.)

}
