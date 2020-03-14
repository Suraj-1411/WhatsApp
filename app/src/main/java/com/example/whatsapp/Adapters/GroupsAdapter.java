package com.example.whatsapp.Adapters;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.whatsapp.Fragments.Groups;
import com.example.whatsapp.GroupChat;
import com.example.whatsapp.GroupCreate;
import com.example.whatsapp.ModelClasses.GroupsModel;
import com.example.whatsapp.R;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class GroupsAdapter extends RecyclerView.Adapter<GroupsAdapter.GroupsViewHolder> {

    List<GroupsModel> models = new ArrayList<>();

    public GroupsAdapter(List<GroupsModel> models) {
        this.models = models;
    }

    @NonNull
    @Override
    public GroupsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.groups,null);
        return new GroupsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GroupsViewHolder holder, int position) {
        GroupsModel model=models.get(position);
        if(model!=null) {
            Picasso.get().load(model.getGroupIcon()).placeholder(R.mipmap.newgroup_users_pic_round).into(holder.imageView);
            holder.name.setText(model.getGroupname());
            holder.members.setText(model.getMembersize() + " Members");
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent=new Intent(holder.itemView.getContext(), GroupChat.class);
                intent.putExtra("GroupName",model.getGroupname());
                intent.putExtra("Members",model.getMembersize());
                intent.putExtra("GroupIcon",model.getGroupIcon());
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                holder.itemView.getContext().startActivity(intent);

            }
        });
    }

    @Override
    public int getItemCount() {
        return models.size();
    }

    class GroupsViewHolder extends RecyclerView.ViewHolder {

        CircularImageView imageView;
        TextView name,members;

        public GroupsViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView=itemView.findViewById(R.id.groups_image_id);
            name=itemView.findViewById(R.id.groups_name_id);
            members=itemView.findViewById(R.id.groups_members_id);
        }
    }
}
