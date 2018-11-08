package com.developerdru.vividity.screens.home;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.IntDef;

import com.developerdru.vividity.data.PhotoRepository;
import com.developerdru.vividity.data.UserRepository;
import com.developerdru.vividity.data.entities.FollowUser;
import com.developerdru.vividity.data.entities.Photo;

import java.lang.annotation.Retention;
import java.util.List;

import static java.lang.annotation.RetentionPolicy.SOURCE;

class HomeVM extends ViewModel {

    @Retention(SOURCE)
    @IntDef({ORDER_TIME, ORDER_UPVOTE, ORDER_COMMENT_COUNT})
    @interface OrderByParams {
    }

    static final int ORDER_TIME = 6;
    static final int ORDER_UPVOTE = 7;
    static final int ORDER_COMMENT_COUNT = 8;

    private LiveData<List<Photo>> photos;

    private LiveData<List<FollowUser>> myFollows;

    HomeVM(PhotoRepository photoRepository, UserRepository userRepository, String myId,
           @OrderByParams int sortBy) {
        int sortByParam = getSortByParam(sortBy);
        photos = photoRepository.getPhotos(sortByParam);
        myFollows = userRepository.getFollowsList(myId);
    }

    LiveData<List<Photo>> getPhotos() {
        return photos;
    }

    LiveData<List<FollowUser>> getMyFollows() {
        return myFollows;
    }

    private int getSortByParam(@OrderByParams int sortBy) {
        switch (sortBy) {
            case ORDER_COMMENT_COUNT:
                return PhotoRepository.ORDER_COMMENT_COUNT_DESC;
            case ORDER_UPVOTE:
                return PhotoRepository.ORDER_UPVOTE_DESC;
            case ORDER_TIME:
                return PhotoRepository.ORDER_TIME_DESC;
        }
        return PhotoRepository.ORDER_TIME_DESC;
    }

}
