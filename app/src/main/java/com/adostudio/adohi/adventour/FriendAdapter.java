package com.adostudio.adohi.adventour;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.adostudio.adohi.adventour.db.Achievement;
import com.adostudio.adohi.adventour.db.User;
import com.bumptech.glide.RequestManager;

import java.util.ArrayList;

/**
 * Created by ADOHI on 2017-02-13.
 */

public class FriendAdapter extends RecyclerView.Adapter<FriendAdapter.ViewHolder> {
    private ArrayList<User> mUserDataset;
    private RequestManager mRequestManager;
    private static FriendActivity activity;
    private int addFriend;
    public FriendAdapter(FriendActivity activity, ArrayList<User> myUserDataset, int addFriend, RequestManager requestManager) {
        this.activity = activity;
        this.mUserDataset = myUserDataset;
        this.mRequestManager = requestManager;
        this.addFriend = addFriend;
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements  View.OnClickListener{

        public TextView friendNameTextView;
        public TextView friendScoreTextView;
        public TextView friendLatelyTextView;
        public TextView friendMemoTextView;
        public ImageView friendSumnailmageView;
        public String uid;

        public ViewHolder(View v) {
            super(v);
            friendNameTextView = (TextView) v.findViewById(R.id.tv_friend_name);
            friendScoreTextView = (TextView) v.findViewById(R.id.tv_friend_score);
            friendLatelyTextView = (TextView) v.findViewById(R.id.tv_friend_lately);
            friendMemoTextView = (TextView) v.findViewById(R.id.tv_friend_memo);
            friendSumnailmageView = (ImageView) v.findViewById(R.id.iv_friend_sumnail);
            v.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            int id = v.getId();
            Intent intent = new Intent(activity, ProfileActivity.class);
            intent.putExtra("uid", uid);
            intent.putExtra("mine", false);
            intent.putExtra("add", addFriend);
            activity.startActivity(intent);
        }
    }

    @Override
    public FriendAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                       int viewType) {

        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.friend_row, parent, false);

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        holder.friendNameTextView.setText(mUserDataset.get(position).userName);
        holder.friendScoreTextView.setText(mUserDataset.get(position).score+"점");
        try{
            holder.friendLatelyTextView.setText(mUserDataset.get(position).achievementList.get(0).title);
        } catch (Exception ex) {
            holder.friendLatelyTextView.setText("늅늅이 입니다");
        }
        holder.friendMemoTextView.setText(mUserDataset.get(position).memo);
        try{
            mRequestManager.load(mUserDataset.get(position).photoUrl).into(holder.friendSumnailmageView);
        }catch (Exception ex){

        }
        holder.uid = mUserDataset.get(position).uid;

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mUserDataset.size();
    }
}
