package com.example.whatsapp.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.whatsapp.ModelClasses.ChatModel;
import com.example.whatsapp.R;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class NewGroupAdapter extends RecyclerView.Adapter<NewGroupAdapter.NewGroupViewHolder> {

    List<ChatModel> chatModels=new ArrayList<>();

    public NewGroupAdapter(List<ChatModel> chatModels) {
        this.chatModels = chatModels;
    }

    @NonNull
    @Override
    public NewGroupViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.newgroup_layout,null);
        return new NewGroupViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NewGroupViewHolder holder, int position) {

        ChatModel model=chatModels.get(position);

        Picasso.get().load(model.getProImage()).placeholder(R.drawable.profile).into(holder.imageView);
        holder.name.setText(model.getName());
    }

    @Override
    public int getItemCount() {
        return chatModels.size();
    }

    class NewGroupViewHolder extends RecyclerView.ViewHolder{

        CircularImageView imageView;
        TextView name;

        public NewGroupViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView=itemView.findViewById(R.id.NG_icon_id);
            name=itemView.findViewById(R.id.NG_name_id);
        }
    }
}
