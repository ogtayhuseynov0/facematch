package com.ohuseynov.app.facematch.takers;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ohuseynov.app.facematch.R;
import com.ohuseynov.app.facematch.model.ChatUser;
import com.ohuseynov.app.facematch.model.Msg;

import java.util.List;

/**
 * Created by ogtay on 6/15/17.
 */

public class MsgAdaptor extends RecyclerView.Adapter<MsgAdaptor.MessageHolder>{
    List<Msg> data;
    Context context;
    String from, to;
    public MsgAdaptor(List<Msg> data, Context context, String from, String to) {
        this.data = data;
        this.context = context;
        this.from=from;
        this.to=to;
    }

    @Override
    public MessageHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MessageHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.msg_right_layout,parent,false));
    }

    @Override
    public void onBindViewHolder(MessageHolder holder, int position) {
        Msg msg=data.get(position);
        if (msg.getFrom().equals(from)){
            holder.r.setVisibility(View.VISIBLE);
            holder.l.setVisibility(View.GONE);
            holder.msg.setText(Html.fromHtml(msg.getMsg()+""));
        }else{
            holder.r.setVisibility(View.GONE);
            holder.l.setVisibility(View.VISIBLE);
            holder.msg_l.setText(Html.fromHtml(msg.getMsg()+""));
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class MessageHolder extends RecyclerView.ViewHolder{
        TextView msg, msg_l;
        LinearLayout l,r;
        public MessageHolder(View itemView) {
            super(itemView);
            msg= (TextView) itemView.findViewById(R.id.msg_r);
            msg_l= (TextView) itemView.findViewById(R.id.msg_l);
            r=(LinearLayout) itemView.findViewById(R.id.right_l);
            l=(LinearLayout) itemView.findViewById(R.id.left_l);

        }
    }
}
