package com.ohuseynov.app.facematch.takers;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.ohuseynov.app.facematch.R;
import com.ohuseynov.app.facematch.model.AddF;
import com.ohuseynov.app.facematch.model.Room;
import com.ohuseynov.app.facematch.model.User;
import com.ohuseynov.app.facematch.utils.myUtils;

import java.util.List;

public class RequestAdapter extends RecyclerView.Adapter<RequestAdapter.RequestHodler>{

    private List<User> data;
    private Context ctx;
    private boolean accepted=false;
    private String from="";
    private myUtils utils=new myUtils();
    private String to=utils.getUserMailId();
    public RequestAdapter(List<User> data,Context ct) {
        this.data = data;
        this.ctx=ct;
    }

    @Override
    public RequestHodler onCreateViewHolder(ViewGroup parent, int viewType) {
        return new RequestHodler(LayoutInflater.from(parent.getContext()).inflate(R.layout.r_list_laylot,parent,false));
    }

    @Override
    public void onBindViewHolder(RequestHodler holder, int position) {
        final User user=data.get(position);
        from= myUtils.getMIdFromMail(user.getUserEmail());
        holder.init.setText(user.getInitials());
        holder.namee.setText(user.getUserName());
        holder.acc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                accepted=true;
                makeDone(myUtils.getMIdFromMail(user.getUserEmail()),to,accepted);
            }
        });
        holder.dec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                accepted=false;
                makeDone(myUtils.getMIdFromMail(user.getUserEmail()),to,accepted);

            }
        });
    }

    private void makeDone(final String from, final String to, final boolean accepted) {
        final DatabaseReference ref=myUtils.getDatabase().getReference("users").child("user_req");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot s: dataSnapshot.getChildren()){
                    AddF d=s.getValue(AddF.class);
                if (d.from.equals(from) && d.to.equals(to)){
                    if (!accepted){
                        ref.child(s.getKey()).removeValue();
                    }else {
                        d.accepted = accepted;
                        ref.child(s.getKey()).setValue(d);
                        addChat(from,to);
                    }
                }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void addChat(String from, String to) {
        DatabaseReference ref=myUtils.getDatabase().getReference("users").child("chats");
        ref.push().setValue(new Room(from,to));
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class RequestHodler extends RecyclerView.ViewHolder{
        TextView init, namee;
        Button acc,dec;
        public RequestHodler(View itemView) {
            super(itemView);
            init=(TextView) itemView.findViewById(R.id.r_inits);
            namee=(TextView) itemView.findViewById(R.id.r_ndname);
            acc=(Button) itemView.findViewById(R.id.r_accept);
            dec=(Button) itemView.findViewById(R.id.r_decline);

        }
    }
}
