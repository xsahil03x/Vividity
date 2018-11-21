package com.developerdru.vividity.widgets;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.AppWidgetTarget;
import com.bumptech.glide.request.target.Target;
import com.crashlytics.android.Crashlytics;
import com.developerdru.vividity.R;
import com.developerdru.vividity.data.PhotoRepository;
import com.developerdru.vividity.data.RepositoryFactory;
import com.developerdru.vividity.data.entities.Photo;
import com.developerdru.vividity.screens.details.PhotoDetailsScreen;

import java.util.List;

public class PhotoListService extends RemoteViewsService {

    static final String EXTRA_WIDGET_ID = "widget_id";
    static final String EXTRA_PHOTO_ID = "widget_photo_id";
    static final int FETCH_SIZE = 10;

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new PhotoFactory(getApplicationContext(), intent);
    }

    private class PhotoFactory implements RemoteViewsFactory {

        private Context appContext;
        private int appWidgetId;
        private PhotoRepository photoRepository;
        private List<Photo> photos;

        private AppWidgetTarget appWidgetTarget;

        PhotoFactory(Context appContext, Intent intent) {
            this.appContext = appContext;
            appWidgetId = intent.getIntExtra(EXTRA_WIDGET_ID, -1);
            photoRepository = RepositoryFactory.getPhotoRepository();
        }

        @Override
        public void onCreate() {

        }

        @Override
        public void onDataSetChanged() {
            // Runs in background thread, can perform long running operations here
            try {
                photos = photoRepository.getPhotosBlocking(PhotoRepository.ORDER_TIME_DESC,
                        FETCH_SIZE);
            } catch (InterruptedException e) {
                e.printStackTrace();
                Crashlytics.logException(e);
            }
        }

        @Override
        public void onDestroy() {

        }

        @Override
        public int getCount() {
            return (photos == null ? 0 : photos.size());
        }

        @Override
        public RemoteViews getViewAt(int index) {
            Photo photo = photos.get(index);
            RemoteViews rv = new RemoteViews(appContext.getPackageName(), R.layout.item_photo_list);

            rv.setTextViewText(R.id.tv_wgt_uploader_name, photo.getUploader());

            // Set fill in intent
            Intent fillInIntent = new Intent();
            Bundle extras = new Bundle();
            extras.putString(PhotoDetailsScreen.KEY_PHOTO_ID, photo.getPicIdentifier());
            fillInIntent.putExtras(extras);

            try {
                Bitmap bitmap = Glide.with(appContext.getApplicationContext())
                        .asBitmap()
                        .load(photo.getDownloadURL())
                        .submit(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                        .get();

                rv.setImageViewBitmap(R.id.imgWidgetMain, bitmap);
            } catch (Exception e) {
                e.printStackTrace();
                Crashlytics.logException(e);
            }
            rv.setOnClickFillInIntent(R.id.imgWidgetMain, fillInIntent);
            return rv;
        }

        @Override
        public RemoteViews getLoadingView() {
            return null;
        }

        @Override
        public int getViewTypeCount() {
            return 1;
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }
    }
}
