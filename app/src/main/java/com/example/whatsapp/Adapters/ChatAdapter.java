package com.example.whatsapp.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.whatsapp.AddpeopleGroup;
import com.example.whatsapp.ModelClasses.Model;
import com.example.whatsapp.PrivateChat;
import com.example.whatsapp.R;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatViewHolder> {

    List<Model> models;
    Context context;
    public ChatAdapter(List<Model> models,Context context){
        this.models=models;
        this.context=context;
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.addchat_people,null);
        return new ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ChatViewHolder holder, int position) {
        final Model model = models.get(position);
        holder.name.setText(model.getName());
        holder.about.setText(model.getAbout());
        Picasso.get().load(model.getProImage()).placeholder(R.drawable.profile).into(holder.imageView);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name=model.getName();
                if(name.toLowerCase().equals("new group")){
                    Intent intent=new Intent(context, AddpeopleGroup.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    holder.itemView.getContext().startActivity(intent);
                }else {
                    Intent intent = new Intent(context, PrivateChat.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("UID",model.getUserID());
                    holder.itemView.getContext().startActivity(intent);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return models.size();
    }
}
class ChatViewHolder extends RecyclerView.ViewHolder {

    CircularImageView imageView;
    TextView name, about;

    public ChatViewHolder(@NonNull View itemView) {
        super(itemView);
        imageView = itemView.findViewById(R.id.chat_image_id);
        name = itemView.findViewById(R.id.chatt_name_id);
        about = itemView.findViewById(R.id.Chat_about_id);

    }
}