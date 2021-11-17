package com.spacester.opendiaryp.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.spacester.opendiaryp.model.ModelChat;
import com.spacester.opendiaryp.R;
import com.spacester.opendiaryp.post.PostDetails;
import com.spacester.opendiaryp.user.MediaView;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

@SuppressWarnings("ALL")
public class AdapterChat extends RecyclerView.Adapter<AdapterChat.MyHolder>{

    public static final int MSG_TYPE_LEFT = 0;
    public static final int MSG_TYPE_RIGHT = 1;

    private final Context context;
    private final List<ModelChat> modelChats;

    FirebaseUser firebaseUser;

    public AdapterChat(Context context, List<ModelChat> modelChats) {
        this.context = context;
        this.modelChats = modelChats;
    }

    @NonNull
    @Override
    public AdapterChat.MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       if (viewType == MSG_TYPE_RIGHT){
           View view = LayoutInflater.from(context).inflate(R.layout.row_chat_right, parent, false);

           return new MyHolder(view);
       }
        View view = LayoutInflater.from(context).inflate(R.layout.row_chat_left, parent, false);
        return new MyHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull AdapterChat.MyHolder holder, final int position) {

        ModelChat mChat = modelChats.get(position);

        String msg = modelChats.get(position).getMsg();
        String type = modelChats.get(position).getType();

        switch (type) {
            case "text":
                holder.rec_msg.setVisibility(View.VISIBLE);
                holder.rec_vid.setVisibility(View.GONE);
                holder.rec_img.setVisibility(View.GONE);
                holder.rec_msg.setText(msg);
                holder.postly.setVisibility(View.GONE);
                holder.play.setVisibility(View.GONE);
                break;
            case "image":
                holder.rec_msg.setVisibility(View.GONE);
                holder.rec_img.setVisibility(View.VISIBLE);
                holder.rec_vid.setVisibility(View.GONE);
                holder.postly.setVisibility(View.GONE);
                holder.play.setVisibility(View.GONE);
                Glide.with(context).asBitmap().centerCrop().load(msg).into(holder.rec_img);
                break;
            case "video":
                holder.rec_msg.setVisibility(View.GONE);
                holder.play.setVisibility(View.VISIBLE);
                holder.rec_img.setVisibility(View.GONE);
                holder.postly.setVisibility(View.GONE);
                holder.rec_vid.setVisibility(View.VISIBLE);
                Glide.with(context).asBitmap().centerCrop().load(msg).into(holder.rec_vid);
                break;
            case "post":
                holder.rec_msg.setVisibility(View.GONE);
                holder.play.setVisibility(View.GONE);
                holder.rec_img.setVisibility(View.GONE);
                holder.rec_vid.setVisibility(View.GONE);
                holder.postly.setVisibility(View.VISIBLE);
                Glide.with(context).asBitmap().centerCrop().load(msg).into(holder.rec_vid);
                break;
        }

        holder.postly.setOnClickListener(v -> {
            Intent intent = new Intent(context, PostDetails.class);
            intent.putExtra("postId", msg);
            context.startActivity(intent);
        });

        if (type.equals("post")){
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts");
            Query query = ref.orderByChild("pId").equalTo(msg);
            query.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    for (DataSnapshot ds : dataSnapshot.getChildren()){
                       String hisText = ""+ds.child("text").getValue();
                        String hisMeme = ""+ds.child("meme").getValue();
                        String hisVine = ""+ds.child("vine").getValue();
                        String hisDp = ""+ds.child("dp").getValue();
                        String hisName = ""+ds.child("name").getValue();

                        holder.text.setText(hisText);
                        holder.name.setText(hisName);
                        //DP
                        try {
                            Picasso.get().load(hisDp).placeholder(R.drawable.avatar).into(holder.circleImageView2);
                        }catch (Exception ignored){

                        }

                        //Post Image
                        if (hisMeme.equals("noImage")){
                            holder.pPlay.setVisibility(View.GONE);
                        }else {

                                Glide.with(context).asBitmap().centerCrop().load(hisMeme).into(holder.imageView13);

                        }

                        if (hisMeme.equals("noImage") && hisVine.equals("noVideo")){
                            holder.pPlay.setVisibility(View.GONE);
                            holder.imageView13.setVisibility(View.GONE);
                        }

                        //Post Vine
                        if (hisVine.equals("noVideo")){
                            holder.pPlay.setVisibility(View.GONE);
                        }else {
                            holder.pPlay.setVisibility(View.VISIBLE);
                            Glide.with(context).asBitmap().centerCrop().load(hisVine).into(holder.imageView13);

                        }

                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

        if (position == modelChats.size()-1){
            if (mChat.isIsSeen()) {
               holder.seen.setText("Seen");
            }else {
                holder.seen.setText("Delivered");
            }
        }else {

            holder.seen.setVisibility(View.GONE);
        }

        holder.rec_img.setOnClickListener(v -> {
            Intent intent = new Intent(context, MediaView.class);
            intent.putExtra("type","image");
            intent.putExtra("uri",msg);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        });
        holder.rec_vid.setOnClickListener(v -> {
            Intent intent = new Intent(context, MediaView.class);
            intent.putExtra("type","video");
            intent.putExtra("uri",msg);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        });


    }

    @Override
    public int getItemCount() {
        return modelChats.size();
    }

    static class MyHolder extends RecyclerView.ViewHolder{

        public final TextView rec_msg;
        public final TextView text;
        public final TextView name;
        public final TextView seen;
        public final ImageView rec_img;
        public final ImageView pPlay;
        public final ImageView imageView13;
        public final ImageView rec_vid;
        public final ImageView play;
        public final RelativeLayout messageLayout;
        public final ConstraintLayout postly;
        public final CircleImageView circleImageView2;

        public MyHolder(@NonNull View itemView) {
            super(itemView);
            rec_msg = itemView.findViewById(R.id.rec_msg);
            seen = itemView.findViewById(R.id.seen);
            rec_vid = itemView.findViewById(R.id.rec_vid);
            rec_img = itemView.findViewById(R.id.rec_img);
            circleImageView2 = itemView.findViewById(R.id.circleImageView2);
            play = itemView.findViewById(R.id.play);
            messageLayout = itemView.findViewById(R.id.messageLayout);
            text = itemView.findViewById(R.id.text);
            postly = itemView.findViewById(R.id.postly);
            name = itemView.findViewById(R.id.name);
            pPlay = itemView.findViewById(R.id.pPlay);
            imageView13 = itemView.findViewById(R.id.imageView13);
        }

    }

    @Override
    public int getItemViewType(int position) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (modelChats.get(position).getSender().equals(firebaseUser.getUid())){
            return MSG_TYPE_RIGHT;
        }else {
            return MSG_TYPE_LEFT;
        }
    }
}

