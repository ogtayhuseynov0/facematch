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
import android.widget.TextView;
import android.widget.Toast;

import com.ohuseynov.app.facematch.ChatRoomActivity;
import com.ohuseynov.app.facematch.R;
import com.ohuseynov.app.facematch.model.ChatUser;

import java.util.List;

/**
 * Created by ogtay on 6/13/17.
 */

public class ChatAdapter  extends RecyclerView.Adapter<ChatAdapter.ChatHolder>{

    List<ChatUser> list;
    Context ctx;

    public ChatAdapter(List<ChatUser> list, Context ctx) {
        this.list = list;
        this.ctx=ctx;
    }

    @Override
    public ChatHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ChatHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_list_item,parent,false));
    }

    @Override
    public void onBindViewHolder(final ChatHolder holder, int position) {
        final ChatUser user=list.get(position);
        holder.ini.setText(user.getDinit());
        holder.name.setText(user.getDname());
        holder.lastmsg.setText(user.getLastMsg());
        holder.view.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
            @Override
            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                menu.setHeaderTitle("Select The Action");
                menu.add(0, v.getId(), 0, "Open Chat").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        Intent nee= new Intent(ctx, ChatRoomActivity.class);
                        nee.putExtra("name",user.getDname());
                        nee.putExtra("init",user.getDinit());
                        nee.putExtra("mailid",user.getMailId());
                        ctx.startActivity(nee);
                        return false;
                    }
                });//groupId, itemId, order, title
                menu.add(0, v.getId(), 0, "Profile");
            }
        });

        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent nee= new Intent(ctx, ChatRoomActivity.class);
                nee.putExtra("name",user.getDname());
                nee.putExtra("init",user.getDinit());
                nee.putExtra("mailid",user.getMailId());
                ctx.startActivity(nee);
            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ChatHolder extends RecyclerView.ViewHolder{
        TextView ini,name,lastmsg;
        CardView view;
        public ChatHolder(View itemView) {
            super(itemView);
            ini=(TextView) itemView.findViewById(R.id.initials_chat);
            name=(TextView) itemView.findViewById(R.id.chat_name);
            lastmsg=(TextView) itemView.findViewById(R.id.last_msg);
            view=(CardView) itemView.findViewById(R.id.card_id);

        }
    }
}
