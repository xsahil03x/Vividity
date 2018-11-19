package com.developerdru.vividity.widgets;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.bumptech.glide.request.target.AppWidgetTarget;
import com.developerdru.vividity.R;
import com.developerdru.vividity.data.PhotoRepository;
import com.developerdru.vividity.data.RepositoryFactory;
import com.developerdru.vividity.data.entities.Photo;
import com.developerdru.vividity.utils.GlideApp;

import java.util.List;

public class PhotoListService extends RemoteViewsService {

    static final String EXTRA_WIDGET_ID = "widget_id";
    static final String EXTRA_PHOTO_ID = "widget_photo_id";

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
            photos = photoRepository.getPhotos(PhotoRepository.ORDER_TIME_DESC).getValue();
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
            extras.putString(EXTRA_PHOTO_ID, photo.getPicIdentifier());
            fillInIntent.putExtras(extras);

            // Set the image
            appWidgetTarget = new AppWidgetTarget(appContext, R.id.imgWidgetMain, rv, appWidgetId);
            GlideApp.with(appContext.getApplicationContext())
                    .asBitmap()
                    .load(photo.getDownloadURL())
                    .into(appWidgetTarget);

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
