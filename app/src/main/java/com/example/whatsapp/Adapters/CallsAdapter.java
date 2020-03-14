package com.example.whatsapp.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.whatsapp.ModelClasses.CallsModel;
import com.example.whatsapp.ModelClasses.ChatModel;
import com.example.whatsapp.R;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

import java.util.List;

public class CallsAdapter extends RecyclerView.Adapter<CallsAdapter.CallsViewHolder> {

    List<ChatModel>  models;
    List<CallsModel> callsModels;

    public CallsAdapter(List<ChatModel> models, List<CallsModel> callsModels) {
        this.models = models;
        this.callsModels = callsModels;
    }

    @NonNull
    @Override
    public CallsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.calls_layout,null);
        return new CallsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CallsViewHolder holder, int position) {

        ChatModel model = models.get(position);
        CallsModel callsModel=callsModels.get(position);

        Picasso.get().load(model.getProImage()).placeholder(R.drawable.profile).into(holder.profilePic);
        holder.name.setText(model.getName());
        holder.callTime.setText(callsModel.getTime());
        if(callsModel.getType().toLowerCase().equals("incoming")){
            Picasso.get().load(R.drawable.call_received).into(holder.callStatus);
        }
        else if(callsModel.getType().toLowerCase().equals("outgoing")){
            Picasso.get().load(R.drawable.call_made).into(holder.callStatus);
        }

    }

    @Override
    public int getItemCount() {
        return models.size();
    }

    class CallsViewHolder extends RecyclerView.ViewHolder{

        CircularImageView profilePic,callStatus;
        TextView name,callTime;

        public CallsViewHolder(@NonNull View itemView) {
            super(itemView);

            profilePic=itemView.findViewById(R.id.calls_image_id);
            callTime =itemView.findViewById(R.id.calls_time_id);
            name=itemView.findViewById(R.id.calls_name_id);
            callStatus=itemView.findViewById(R.id.calls_state_id);
        }
    }
}
