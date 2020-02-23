package com.ohuseynov.app.facematch.takers;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ohuseynov.app.facematch.ProfileActivity;
import com.ohuseynov.app.facematch.R;
import com.ohuseynov.app.facematch.model.User;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ogtay on 6/12/17.
 */

public class FindAdapter extends RecyclerView.Adapter<FindAdapter.UserHolder>  {

    List<User> users;
    Context ctx;
    public FindAdapter(List<User> users, Context tc) {
        this.users = users;
        this.ctx=tc;
    }

    @Override
    public UserHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new UserHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.find_list_itme,parent,false));
    }

    @Override
    public void onBindViewHolder(UserHolder holder, int position) {
        final User user=users.get(position);

        if(user.getPhotoUrl()==null || user.getPhotoUrl().equals("")){
            holder.image.setVisibility(View.GONE);
            holder.ini.setText(user.getInitials());
            holder.dname.setText(user.getUserName());
        }else{
            holder.ini.setVisibility(View.GONE);
            holder.image.setVisibility(View.VISIBLE);
//            DownloadImageTask bm=new DownloadImageTask(holder.image);
//            bm.execute();
//            holder.image.setImageBitmap(bm.bm());
            holder.dname.setText(user.getUserName());
        }
        holder.ini.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent profile=new Intent(ctx, ProfileActivity.class);
                profile.putExtra("p_name",user.getUserName());
                profile.putExtra("p_email",user.getUserEmail());
                profile.putExtra("p_sts",user.getUserStatus());
                ctx.startActivity(profile);
            }
        });
        holder.cardView.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
            @Override
            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                menu.setHeaderTitle("Select The Action");
                menu.add(0, v.getId(), 0, "Open Profile").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        Intent profile=new Intent(ctx, ProfileActivity.class);
                        profile.putExtra("p_name",user.getUserName());
                        profile.putExtra("p_email",user.getUserEmail());
                        profile.putExtra("p_sts",user.getUserStatus());
                        profile.putExtra("p_url",user.getPhotoUrl());
                        ctx.startActivity(profile);
                        return false;
                    }
                });//groupId, itemId, order, title
            }
        });


    }

    public void setAdatper(ArrayList<User> ss){
        users=new ArrayList<>();
        users.addAll(ss);
        notifyDataSetChanged();
    }
    @Override
    public int getItemCount() {
        return users.size();
    }

    public class UserHolder extends RecyclerView.ViewHolder{
        TextView ini,dname;
        ImageView image;
        CardView cardView;
        public UserHolder(View itemView) {
            super(itemView);
            ini=(TextView) itemView.findViewById(R.id.initials);
            dname=(TextView) itemView.findViewById(R.id.d_nname);
            image=(ImageView) itemView.findViewById(R.id.u_image);
            cardView=(CardView) itemView.findViewById(R.id.card_me);

        }
    }
}
