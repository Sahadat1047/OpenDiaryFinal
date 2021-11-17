package com.spacester.opendiaryp.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pedromassango.doubleclick.DoubleClick;
import com.pedromassango.doubleclick.DoubleClickListener;
import com.spacester.opendiaryp.model.ModelComments;
import com.spacester.opendiaryp.model.ModelUser;
import com.spacester.opendiaryp.R;
import com.spacester.opendiaryp.user.MediaView;
import com.spacester.opendiaryp.user.UserProfile;
import com.spacester.opendiaryp.welcome.GetTimeAgo;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

@SuppressWarnings("ALL")
public class AdapterComments extends RecyclerView.Adapter<AdapterComments.MyHolder> {

    final Context context;
    final List<ModelComments> commentsList;
    private DatabaseReference likeRef;
    private DatabaseReference postsRef1;

    boolean mProcessLike = false;

    private String userId;

    public AdapterComments(Context context, List<ModelComments> commentsList) {
        this.context = context;
        this.commentsList = commentsList;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_comment, parent, false);

        return new MyHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {
        String id = commentsList.get(position).getId();
        String cId = commentsList.get(position).getcId();
        String comment = commentsList.get(position).getComment();
        String time = commentsList.get(position).getTimestamp();
        String pId = commentsList.get(position).getpId();
        String pLikes = commentsList.get(position).getpLikes();
        String type = commentsList.get(position).getType();

        switch (type) {
            case "text":
                holder.mComment.setVisibility(View.VISIBLE);
                holder.rec_vid.setVisibility(View.GONE);
                holder.rec_img.setVisibility(View.GONE);
                holder.mComment.setText(comment);
                holder.play.setVisibility(View.GONE);
                break;
            case "image":
                holder.mComment.setVisibility(View.GONE);
                holder.rec_img.setVisibility(View.VISIBLE);
                holder.rec_vid.setVisibility(View.GONE);
                holder.play.setVisibility(View.GONE);
                Glide.with(context).asBitmap().centerCrop().load(comment).into(holder.rec_img);
                break;
            case "video":
                holder.mComment.setVisibility(View.GONE);
                holder.play.setVisibility(View.VISIBLE);
                holder.rec_img.setVisibility(View.GONE);
                holder.rec_vid.setVisibility(View.VISIBLE);
                Glide.with(context).asBitmap().centerCrop().load(comment).into(holder.rec_vid);
                break;
        }

        holder.rec_img.setOnClickListener(new DoubleClick(new DoubleClickListener() {
            @Override
            public void onSingleClick(View view) {
                Intent intent = new Intent(context, MediaView.class);
                intent.putExtra("type","image");
                intent.putExtra("uri",comment);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }

            @Override
            public void onDoubleClick(View view) {
                int pLikes = Integer.parseInt(commentsList.get(position).getpLikes());
                mProcessLike = true;
                String postId = commentsList.get(position).getcId();
                likeRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (mProcessLike){
                            if (dataSnapshot.child(postId).hasChild(userId)){
                                postsRef1.child(postId).child("pLikes").setValue(""+(pLikes-1));
                                likeRef.child(postId).child(userId).removeValue();
                                mProcessLike = false;
                            }else {
                                postsRef1.child(postId).child("pLikes").setValue(""+(pLikes+1));
                                likeRef.child(postId).child(userId).setValue("Liked");
                                mProcessLike = false;
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        }));

        holder.rec_vid.setOnClickListener(new DoubleClick(new DoubleClickListener() {
            @Override
            public void onSingleClick(View view) {
                Intent intent = new Intent(context, MediaView.class);
                intent.putExtra("type","video");
                intent.putExtra("uri",comment);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }

            @Override
            public void onDoubleClick(View view) {
                int pLikes = Integer.parseInt(commentsList.get(position).getpLikes());
                mProcessLike = true;
                String postId = commentsList.get(position).getcId();
                likeRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (mProcessLike){
                            if (dataSnapshot.child(postId).hasChild(userId)){
                                postsRef1.child(postId).child("pLikes").setValue(""+(pLikes-1));
                                likeRef.child(postId).child(userId).removeValue();
                                mProcessLike = false;
                            }else {
                                postsRef1.child(postId).child("pLikes").setValue(""+(pLikes+1));
                                likeRef.child(postId).child(userId).setValue("Liked");
                                mProcessLike = false;
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        }));


        likeRef = FirebaseDatabase.getInstance().getReference().child("cLikes");
        postsRef1 =FirebaseDatabase.getInstance().getReference().child("Posts").child(pId).child("Comments");


        holder.mName.setOnClickListener(v -> {
            Intent intent = new Intent(context, UserProfile.class);
            intent.putExtra("hisUid", id);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        });
        holder.mDp.setOnClickListener(v -> {
            Intent intent = new Intent(context, UserProfile.class);
            intent.putExtra("hisUid", id);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        });

        if (pLikes.equals("0")){
            holder.likeNo.setText("Like");

        }else {
            holder.likeNo.setText(pLikes);
        }
        setLikes(holder, cId);

        GetTimeAgo getTimeAgo = new GetTimeAgo();
        long lastTime = Long.parseLong(time);
        String lastSeenTime = GetTimeAgo.getTimeAgo(lastTime);
        holder.time.setText(lastSeenTime);


        holder.mComment.setText(comment);

        getUserInfo(holder, id);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        userId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();

        holder.mComment.setOnClickListener(new DoubleClick(new DoubleClickListener() {
            @Override
            public void onSingleClick(View view) {

            }

            @Override
            public void onDoubleClick(View view) {
                int pLikes = Integer.parseInt(commentsList.get(position).getpLikes());
                mProcessLike = true;
                String postId = commentsList.get(position).getcId();
                likeRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (mProcessLike){
                            if (dataSnapshot.child(postId).hasChild(userId)){
                                postsRef1.child(postId).child("pLikes").setValue(""+(pLikes-1));
                                likeRef.child(postId).child(userId).removeValue();
                                mProcessLike = false;
                            }else {
                                postsRef1.child(postId).child("pLikes").setValue(""+(pLikes+1));
                                likeRef.child(postId).child(userId).setValue("Liked");
                                mProcessLike = false;
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        }));

        holder.like.setOnClickListener(v -> {
            int pLikes1 = Integer.parseInt(commentsList.get(position).getpLikes());
            mProcessLike = true;
            String postId = commentsList.get(position).getcId();
            likeRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (mProcessLike){
                        if (dataSnapshot.child(postId).hasChild(userId)){
                            postsRef1.child(postId).child("pLikes").setValue(""+(pLikes1 -1));
                            likeRef.child(postId).child(userId).removeValue();
                            mProcessLike = false;
                        }else {
                            postsRef1.child(postId).child("pLikes").setValue(""+(pLikes1 +1));
                            likeRef.child(postId).child(userId).setValue("Liked");
                            mProcessLike = false;
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        });

        if (id.equals(userId)){
            holder.delete.setVisibility(View.VISIBLE);
        }else {
            holder.delete.setVisibility(View.GONE);
        }


        holder.delete.setOnClickListener(v -> {
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts").child(pId);
            ref.child("Comments").child(cId).removeValue();
            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String comments = ""+ snapshot.child("pComments").getValue();
                    int newCommentVal = Integer.parseInt(comments) -1;
                    ref.child("pComments").setValue(""+newCommentVal);

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        });


    }

    private void getUserInfo(MyHolder holder, String id) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users").child(id);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ModelUser user = snapshot.getValue(ModelUser.class);
                holder.mName.setText(Objects.requireNonNull(user).getName());
                try {
                    Picasso.get().load(user.getPhoto()).placeholder(R.drawable.avatar).into(holder.mDp);
                }catch (Exception ignored){

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void setLikes(MyHolder holder, String postKey) {
        likeRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(postKey).hasChild(userId)){

                    holder.like.setImageResource(R.drawable.ic_hearted);

                }else {
                    holder.like.setImageResource(R.drawable.ic_heart);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    @Override
    public int getItemCount() {
        return commentsList.size();
    }

    class MyHolder extends RecyclerView.ViewHolder{
        final CircleImageView mDp;
        final TextView mName;
        final TextView mComment;
        final TextView time;
        final TextView likeNo;
        final ImageView rec_img;
        final ImageView rec_vid;
        final ImageView play;
        final ImageView like;
        final ImageView delete;

        public MyHolder(@NonNull View itemView) {
            super(itemView);
            mDp = itemView.findViewById(R.id.dp);
            mName = itemView.findViewById(R.id.rec_msg);
            likeNo = itemView.findViewById(R.id.tv);
            rec_img = itemView.findViewById(R.id.rec_img);
            rec_vid = itemView.findViewById(R.id.rec_vid);
            play = itemView.findViewById(R.id.play);
            mComment = itemView.findViewById(R.id.comment);
            delete = itemView.findViewById(R.id.delete);
            Typeface typeface = ResourcesCompat.getFont(context, R.font.bold);
            Typeface typeface1 = ResourcesCompat.getFont(context, R.font.bold);
            mName.setTypeface(typeface);
            time = itemView.findViewById(R.id.textView12);
            like = itemView.findViewById(R.id.imageView7);
            mComment.setTypeface(typeface1);
            likeNo.setTypeface(typeface);
            time.setTypeface(typeface);
        }
    }

}
