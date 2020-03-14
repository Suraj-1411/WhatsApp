package com.example.whatsapp.Adapters;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.whatsapp.ModelClasses.ChatUser;
import com.example.whatsapp.PrivateChat;
import com.example.whatsapp.R;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ChatsUserAdapter extends RecyclerView.Adapter<chatsuserViewHolder> {

    List<ChatUser> chatUserList;

    public ChatsUserAdapter(List<ChatUser> chatUserList) {
        this.chatUserList=chatUserList;
    }

    @NonNull
    @Override
    public chatsuserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.chatuser,null);

        return new chatsuserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull chatsuserViewHolder holder, int position) {

        ChatUser user=chatUserList.get(position);

        holder.name.setText(user.getName());
        holder.lastseen.setText("Last Seen :"+user.getLastseen());
        Picasso.get().load(user.getProImage()).placeholder(R.drawable.profile).into(holder.image);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(holder.itemView.getContext(), PrivateChat.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("UID",user.getUserID());
                holder.itemView.getContext().startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return chatUserList.size();
    }
}
class chatsuserViewHolder extends RecyclerView.ViewHolder{

    CircularImageView image;
    TextView name,lastseen;


    public chatsuserViewHolder(@NonNull View itemView) {
        super(itemView);
        image=itemView.findViewById(R.id.chatuser_image_id);
        name=itemView.findViewById(R.id.chatuser_name_id);
        lastseen=itemView.findViewById(R.id.chatuser_lastseen_id);
    }
}
