package com.example.whatsapp.Adapters;

import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.whatsapp.GroupChat;
import com.example.whatsapp.ModelClasses.GroupChatModel;
import com.example.whatsapp.R;

import java.util.ArrayList;
import java.util.List;

public class GroupChatAdapter extends RecyclerView.Adapter<GroupChatAdapter.GroupChatViewHolder> {

    List<GroupChatModel> models=new ArrayList<>();

    public GroupChatAdapter(List<GroupChatModel> models) {
        this.models = models;
    }

    @NonNull
    @Override
    public GroupChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.chatgroup,null);
        return new GroupChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GroupChatViewHolder holder, int position) {

        GroupChatModel model=models.get(position);
        String type = model.getType();
        if(model!=null){
            if(type.equals("sent")){
                holder.group_receiver_cardView.setVisibility(View.INVISIBLE);
                holder.group_sender_cardView.setVisibility(View.VISIBLE);
                holder.group_sender_message.setText(model.getMessage());
            } else if (type.equals("received")) {
                holder.group_sender_cardView.setVisibility(View.INVISIBLE);
                holder.group_receiver_cardView.setVisibility(View.VISIBLE);
                holder.group_receiver_message.setText(model.getMessage());
                holder.group_receiver_username.setText(model.getName());
            }
        }

    }

    @Override
    public int getItemCount() {
        return models.size();
    }

    class GroupChatViewHolder extends RecyclerView.ViewHolder{

        CardView group_sender_cardView,group_receiver_cardView;
        TextView group_sender_message,group_receiver_message,group_receiver_username;

        public GroupChatViewHolder(@NonNull View itemView) {
            super(itemView);

            group_sender_cardView=itemView.findViewById(R.id.group_sender_cardview_id);
            group_receiver_cardView=itemView.findViewById(R.id.group_receiver_cardview_id);
            group_sender_message=itemView.findViewById(R.id.group_sender_message_id);
            group_receiver_message=itemView.findViewById(R.id.group_receiver_message_id);
            group_receiver_username=itemView.findViewById(R.id.group_receiver_user_name);
        }
    }
}
