package com.example.whatsapp.Adapters;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.whatsapp.ModelClasses.ChatUser;
import com.example.whatsapp.R;
import com.example.whatsapp.ViewStatus;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

import java.util.List;

public class StatusAdapter extends RecyclerView.Adapter<StatusAdapter.StatusViewHolder> {

    List<ChatUser> users;

    public StatusAdapter(List<ChatUser> users) {
        this.users = users;
    }

    @NonNull
    @Override
    public StatusViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.status,null);
        return new StatusViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StatusViewHolder holder, int position) {
        ChatUser user=users.get(position);

        holder.Name.setText(user.getName());
        Picasso.get().load(user.getProImage()).placeholder(R.drawable.profile).into(holder.imageView);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(holder.itemView.getContext(), ViewStatus.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("StatusUID",user.getUserID());
                holder.itemView.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    class StatusViewHolder extends RecyclerView.ViewHolder{

        CircularImageView imageView;
        TextView Name;

        public StatusViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView=itemView.findViewById(R.id.status_profile_user);
            Name=itemView.findViewById(R.id.status_name_id);
        }
    }
}
