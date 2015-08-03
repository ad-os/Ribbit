package com.example.android.ribbit.adapters;

import android.content.Context;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.ribbit.R;
import com.example.android.ribbit.utils.MD5Util;
import com.example.android.ribbit.utils.ParseConstants;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.squareup.picasso.Picasso;

import java.util.Date;
import java.util.List;

/**
 * Created by adhyan on 20/7/15.
 */
public class UserAdapter extends ArrayAdapter<ParseUser> {

    private static final String TAG = UserAdapter.class.getSimpleName();
    protected Context mContext;
    protected List<ParseUser> mUsers;

    public UserAdapter(Context context, List<ParseUser> users){
        super(context, R.layout.message_item, users);
        mContext = context;
        mUsers = users;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;

        if (convertView == null) {

            convertView = LayoutInflater.from(mContext).inflate(R.layout.user_item, null);
            holder = new ViewHolder();
            holder.userImageView = (ImageView) convertView.findViewById(R.id.userImageView);
            holder.nameLabel = (TextView) convertView.findViewById(R.id.nameLabel);
            holder.checkImageView = (ImageView) convertView.findViewById(R.id.checkImageView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        ParseUser user = mUsers.get(position);
        String email = user.getEmail().toLowerCase();

        if (email.equals("")) {
            holder.userImageView.setImageResource(R.drawable.avatar_empty);
        } else {
            String hash = MD5Util.md5Hex(email);
            String gravatarUrl = "http://www.gravatar.com/avatar/" + hash + "?s=204&d=404";
            Log.d(TAG, gravatarUrl);
            Picasso.with(mContext)
                    .load(gravatarUrl)
                    .placeholder(R.drawable.avatar_empty)
                    .into(holder.userImageView);
        }

        holder.nameLabel.setText(user.getUsername());

        GridView gridView = (GridView) parent;

        if (gridView.isItemChecked(position)) {
            holder.checkImageView.setVisibility(View.VISIBLE);
        }
        else {
            holder.checkImageView.setVisibility(View.INVISIBLE);
        }

        return convertView;
    }

    //Contains all the view that we are adapting

    private static class ViewHolder{
        ImageView userImageView;
        ImageView checkImageView;
        TextView nameLabel;
    }

    public void refill(List<ParseUser> users) {
        mUsers.clear();
        mUsers.addAll(users);
        //we need to tell the adapter that see our model has been updated so refresh yourself accordingly.
        notifyDataSetChanged();
    }
}
