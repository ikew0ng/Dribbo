
package com.refactech.driibo.ui.adapter;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.refactech.driibo.R;
import com.refactech.driibo.data.RequestManager;
import com.refactech.driibo.type.dribble.Shot;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by Issac on 7/18/13.
 */
public class ShotsAdapter extends CursorAdapter {
    private LayoutInflater mLayoutInflater;

    public ShotsAdapter(Context context) {
        super(context, null, false);
        mLayoutInflater = ((Activity) context).getLayoutInflater();
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        return mLayoutInflater.inflate(R.layout.listitem_shot, null);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        Holder holder = getHolder(view);
        if (holder.imageRequest != null) {
            holder.imageRequest.cancelRequest();
        }

        if (holder.avartarRequest != null) {
            holder.avartarRequest.cancelRequest();
        }

        Shot shot = Shot.fromCursor(cursor);
        holder.imageRequest = RequestManager.loadImage(shot.getImage_url(),
                ImageLoader.getImageListener(holder.image, null, null));
        holder.avartarRequest = RequestManager.loadImage(shot.getPlayer().getAvatar_url(),
                ImageLoader.getImageListener(holder.avartar, null, null), 50, 50);
        holder.title.setText(shot.getTitle());
        holder.userName.setText(shot.getPlayer().getName());
        holder.text_view_count.setText(String.valueOf(shot.getViews_count()));
        holder.text_like_count.setText(String.valueOf(shot.getLikes_count()));
        holder.text_comment_count.setText(String.valueOf(shot.getComments_count()));
    }

    private Holder getHolder(final View view) {
        Holder holder = (Holder) view.getTag();
        if (holder == null) {
            holder = new Holder(view);
            view.setTag(holder);
        }
        return holder;
    }

    private class Holder {
        public NetworkImageView image;

        public NetworkImageView avartar;

        public TextView title;

        public TextView userName;

        public TextView text_view_count;

        public TextView text_comment_count;

        public TextView text_like_count;

        public ImageLoader.ImageContainer imageRequest;

        public ImageLoader.ImageContainer avartarRequest;

        public Holder(View view) {
            image = (NetworkImageView) view.findViewById(R.id.image);
            avartar = (NetworkImageView) view.findViewById(R.id.avartar);
            title = (TextView) view.findViewById(R.id.title);
            userName = (TextView) view.findViewById(R.id.userName);
            text_view_count = (TextView) view.findViewById(R.id.text_view_count);
            text_comment_count = (TextView) view.findViewById(R.id.text_comment_count);
            text_like_count = (TextView) view.findViewById(R.id.text_like_count);
        }
    }
}
