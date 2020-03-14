package com.example.whatsapp.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.whatsapp.ModelClasses.Message;
import com.example.whatsapp.R;

import java.util.ArrayList;
import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {
    List<Message> list=new ArrayList<>();

    public MessageAdapter(List<Message> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.chatprivate,null);
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {

        Message message = list.get(position);
        String type = message.getType();
        if(type!=null) {
            if (type.equals("sent")) {
                holder.receiverMessageCV.setVisibility(View.INVISIBLE);
                holder.senderMessageCV.setVisibility(View.VISIBLE);
                holder.senderMessage.setText(message.getMessage());

            } else if (type.equals("received")) {
                holder.senderMessageCV.setVisibility(View.INVISIBLE);
                holder.receiverMessageCV.setVisibility(View.VISIBLE);
                holder.receiverMessage.setText(message.getMessage());

            }
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class MessageViewHolder extends RecyclerView.ViewHolder{

        CardView receiverMessageCV,senderMessageCV;
        TextView receiverMessage,senderMessage;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);

            receiverMessage=itemView.findViewById(R.id.receiver_message_id);

            senderMessage=itemView.findViewById(R.id.sender_message_id);


            receiverMessageCV=itemView.findViewById(R.id.receiver_cardview_id);
            senderMessageCV=itemView.findViewById(R.id.sender_cardview_id);

        }
    }
}
