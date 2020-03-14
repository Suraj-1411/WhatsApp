package com.example.whatsapp.Adapters;

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.whatsapp.AddpeopleGroup;
import com.example.whatsapp.ModelClasses.Model;
import com.example.whatsapp.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class GroupAdapter extends RecyclerView.Adapter<viewHolder>{
    public static final String TAG = "MyGroupAdapter";
    public static final List<String> selectedUserIds=new ArrayList<>();
    List<Model> modelList =new ArrayList<>();
    public GroupAdapter(List<Model> modelList){
        this.modelList = modelList;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view=LayoutInflater.from(parent.getContext()).inflate(R.layout.addgroup_people,null);

        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final viewHolder holder, final int position) {
        selectedUserIds.clear();
        holder.name.setText(modelList.get(position).getName());
        holder.about.setText(modelList.get(position).getAbout());
        Picasso.get().load(modelList.get(position).getProImage()).placeholder(R.drawable.profile).into(holder.imageView);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onClick(View view) {
                if(holder.btn.getVisibility()!=View.VISIBLE) {
                    holder.btn.setVisibility(View.VISIBLE);
                    selectedUserIds.add(modelList.get(position).getUserID());
                    //Log.d(AddpeopleGroup.TAG, "onClick: " + modelList.get(position).getName());
                }else{
                    selectedUserIds.remove(modelList.get(position).getUserID());
                    holder.btn.setVisibility(View.GONE);
                }
            }
        });

        Log.d(TAG, "onBindViewHolder: "+selectedUserIds);
    }

    @Override
    public int getItemCount() {
        return modelList.size();
    }
}
class viewHolder extends RecyclerView.ViewHolder{
    CircularImageView imageView,btn;
    TextView name,about;

    public viewHolder(@NonNull View itemView) {
        super(itemView);
        imageView=itemView.findViewById(R.id.group_image_id);
        name=itemView.findViewById(R.id.group_name_id);
        about=itemView.findViewById(R.id.group_about_id);
        btn=itemView.findViewById(R.id.group_check_btn);
    }
}
