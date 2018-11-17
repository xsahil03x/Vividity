package com.developerdru.vividity.data;

import com.developerdru.vividity.data.remote.CommentRepositoryImpl;
import com.developerdru.vividity.data.remote.PhotoRepositoryImpl;
import com.developerdru.vividity.data.remote.StorageRepositoryImpl;
import com.developerdru.vividity.data.remote.UserRepositoryImpl;

public class RepositoryFactory {

    public static PhotoRepository getPhotoRepository() {
        return PhotoRepositoryImpl.getInstance();
    }

    public static StorageRepository getStorageRepository() {
        return StorageRepositoryImpl.getInstance();
    }

    public static UserRepository getUserRepository() {
        return UserRepositoryImpl.getInstance();
    }

    public static CommentRepository getCommentsRepository() {
        return CommentRepositoryImpl.getInstance();
    }

}
