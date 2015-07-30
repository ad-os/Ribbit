package com.example.android.ribbit.adapters;

import android.content.Context;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.ribbit.utils.ParseConstants;
import com.example.android.ribbit.R;
import com.parse.ParseObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by adhyan on 20/7/15.
 */
public class MessageAdapter extends ArrayAdapter<ParseObject> {

    private static final String TAG = MessageAdapter.class.getSimpleName();
    protected Context mContext;
    protected List<ParseObject> mMessages;

    public MessageAdapter(Context context, List<ParseObject> messages){
        super(context, R.layout.message_item, messages);
        mContext = context;
        mMessages = messages;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;

        if (convertView == null) {

            convertView = LayoutInflater.from(mContext).inflate(R.layout.message_item, null);
            holder = new ViewHolder();
            holder.iconImageView = (ImageView) convertView.findViewById(R.id.messageIcon);
            holder.nameLabel = (TextView) convertView.findViewById(R.id.senderLabel);
            holder.timeLabel = (TextView) convertView.findViewById(R.id.timeLabel);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        ParseObject message = mMessages.get(position);
//        Date date = message.getCreatedAt(); intentionally commented.
        Date createdAt = message.getCreatedAt();
        long now = new Date().getTime();
        String convertDate = DateUtils.getRelativeTimeSpanString(
                createdAt.getTime(),
                now,
                DateUtils.SECOND_IN_MILLIS
        ).toString();

        if (message.getString(ParseConstants.KEY_FILE_TYPE).equals(ParseConstants.TYPE_IMAGE)){
            holder.iconImageView.setImageResource(R.drawable.ic_picture);
        } else {
            holder.iconImageView.setImageResource(R.drawable.ic_video);
        }
        holder.nameLabel.setText(message.getString(ParseConstants.KEY_SENDER_NAME));
        holder.timeLabel.setText(convertDate);
        return convertView;
    }

//    private String getTimeInHours(Date date) {
//        long startTime = date.getTime();
//        //gets the current system date and time
//        Date endDate = new Date();
//
//        long duration  = endDate.getTime() - startTime;
//        long diffInSeconds = TimeUnit.MILLISECONDS.toSeconds(duration);
//        long diffInMinutes = TimeUnit.MILLISECONDS.toMinutes(duration);
//        long diffInHours = TimeUnit.MILLISECONDS.toHours(duration);
//
//        if (diffInHours > 23) {
//            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MMMM-dd");
//            return formatter.format(date);
//        }
//        else if (diffInMinutes > 59){
//            return diffInHours + " hrs ago";
//        } else if (diffInSeconds > 59){
//            return diffInMinutes + " minutes ago";
//        } else {
//            return diffInSeconds + " seconds ago";
//        }
//    } intentionally commented.

    private static class ViewHolder{
        ImageView iconImageView;
        TextView nameLabel;
        TextView timeLabel;
    }

    public void refill(List<ParseObject> messages) {
        mMessages.clear();
        mMessages.addAll(messages);
        //we need to tell the adapter that see our model has been updated so refresh yourself accordingly.
        notifyDataSetChanged();
    }
}
