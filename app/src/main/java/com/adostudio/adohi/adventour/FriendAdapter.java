package com.adostudio.adohi.adventour;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.adostudio.adohi.adventour.appInit.MyApplication;
import com.adostudio.adohi.adventour.db.User;
import com.bumptech.glide.RequestManager;

import java.util.ArrayList;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

/**
 * Created by ADOHI on 2017-02-13.
 */

public class FriendAdapter extends RecyclerView.Adapter<FriendAdapter.ViewHolder> {

    private static final String LOGTAG = "FriendAdapter";

    private ArrayList<User> userDataset;
    private RequestManager glideRequestManager;
    private static FriendActivity activity;
    private int addFriend;
    public FriendAdapter(FriendActivity activity, ArrayList<User> myUserDataset, int addFriend, RequestManager requestManager) {
        this.activity = activity;
        this.userDataset = myUserDataset;
        this.glideRequestManager = requestManager;
        this.addFriend = addFriend;
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements  View.OnClickListener{

        public TextView friendNameTextView;
        public TextView friendScoreTextView;
        public TextView friendflagTextView;
        public TextView friendLatelyTextView;
        public TextView friendMemoTextView;
        public ImageView friendSumnailmageView;
        public String uid;
        public String name;
        public String imageUrl;
        public ViewHolder(View v) {
            super(v);
            friendNameTextView = (TextView) v.findViewById(R.id.tv_friend_name);
            friendScoreTextView = (TextView) v.findViewById(R.id.tv_trophy_score);
            friendflagTextView = (TextView) v.findViewById(R.id.tv_flag_score);
            friendLatelyTextView = (TextView) v.findViewById(R.id.tv_friend_lately);
            friendMemoTextView = (TextView) v.findViewById(R.id.tv_friend_memo);
            friendSumnailmageView = (ImageView) v.findViewById(R.id.iv_friend_sumnail);
            v.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            int id = v.getId();
            if(activity.getIssueCheck()){
                MyApplication.setIssueFriendImageUrl(imageUrl);
                MyApplication.setIssueFriendName(name);
                MyApplication.setIssueFriendUid(uid);
                Log.d(LOGTAG, MyApplication.getIssueFriendUid());
                activity.finish();
            } else {
                Intent intent = new Intent(activity, ProfileActivity.class);
                intent.putExtra("frienduid", uid);
                intent.putExtra("mine", false);
                intent.putExtra("add", addFriend);
                activity.startActivity(intent);
            }
        }
    }

    @Override
    public FriendAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                       int viewType) {

        View friendRowView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.friend_row, parent, false);

        ViewHolder friendViewHolder = new ViewHolder(friendRowView);
        return friendViewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        holder.friendNameTextView.setText(userDataset.get(position).getUserName());
        holder.friendScoreTextView.setText(userDataset.get(position).getAchievementList().size()+"점");
        holder.friendflagTextView.setText(userDataset.get(position).getFlagList().size()+"점");
        try{
            holder.friendLatelyTextView.setText(userDataset.get(position).getAchievementList().get(0).getTitle());
        } catch (Exception ex) {
            holder.friendLatelyTextView.setText(activity.getString(R.string.newbie));
        }
        holder.friendMemoTextView.setText(userDataset.get(position).getMemo());
        try{
            glideRequestManager.load(userDataset.get(position).getPhotoUrl())
                    .bitmapTransform(new CropCircleTransformation(activity))
                    .into(holder.friendSumnailmageView);
        }catch (Exception ex){
            Log.e(LOGTAG, "friend photo load failed");
        }
        holder.uid = userDataset.get(position).getUid();
        holder.name = userDataset.get(position).getUserName();
        holder.imageUrl = userDataset.get(position).getPhotoUrl();
    }

    @Override
    public int getItemCount() {
        return userDataset.size();
    }
}
