package com.developerdru.vividity.data;

import android.arch.lifecycle.LiveData;
import android.support.annotation.IntDef;

import com.developerdru.vividity.data.entities.Photo;
import com.developerdru.vividity.data.entities.PhotoComment;

import java.lang.annotation.Retention;
import java.util.List;

import static java.lang.annotation.RetentionPolicy.SOURCE;

/**
 * Defines signature for various types of data sources
 */
public interface PhotoRepository {

    @Retention(SOURCE)
    @IntDef({ORDER_TIME_DESC, ORDER_UPVOTE_DESC, ORDER_COMMENT_COUNT_DESC})
    @interface OrderByParams {}
    int ORDER_TIME_DESC = 1;
    int ORDER_UPVOTE_DESC = 2;
    int ORDER_COMMENT_COUNT_DESC = 3;

    LiveData<Photo> getPhotos(@OrderByParams int orderBy, long startAt, int limit);
}
