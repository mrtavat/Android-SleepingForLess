package com.akexorcist.sleepingforless.database;

import org.parceler.Parcel;

/**
 * Created by Akexorcist on 3/24/2016 AD.
 */

@Parcel(parcelsIndex = false)
public class BookmarkResult {
    String postId;

    public BookmarkResult() {
    }

    public BookmarkResult(String postId) {
        this.postId = postId;
    }

    public String getPostId() {
        return postId;
    }
}
