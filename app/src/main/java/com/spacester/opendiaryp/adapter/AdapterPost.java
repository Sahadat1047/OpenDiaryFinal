package com.spacester.opendiaryp.adapter;

import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.google.android.ads.nativetemplates.TemplateView;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.muddzdev.styleabletoastlibrary.StyleableToast;
import com.pedromassango.doubleclick.DoubleClick;
import com.pedromassango.doubleclick.DoubleClickListener;
import com.spacester.opendiaryp.Adpref;
import com.spacester.opendiaryp.groups.ShareGroupActivity;
import com.spacester.opendiaryp.model.ModelPost;
import com.spacester.opendiaryp.post.PostLikedBy;
import com.spacester.opendiaryp.R;
import com.spacester.opendiaryp.post.PostDetails;
import com.spacester.opendiaryp.search.Search;
import com.spacester.opendiaryp.shareChat.ShareActivity;
import com.spacester.opendiaryp.post.UpdatePost;
import com.spacester.opendiaryp.user.MediaView;
import com.spacester.opendiaryp.user.UserProfile;
import com.spacester.opendiaryp.welcome.GetTimeAgo;
import com.squareup.picasso.Picasso;
import com.volokh.danylo.hashtaghelper.HashTagHelper;

import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.os.Environment.DIRECTORY_DOWNLOADS;

@SuppressWarnings("ALL")
public class AdapterPost extends RecyclerView.Adapter<AdapterPost.MyHolder> {

    Context context;
    final List<ModelPost> postList;

    private String userId;
    private final DatabaseReference likeRef;
    private final DatabaseReference viewRef;
    private final DatabaseReference postsRef1;
    boolean mProcessLike = false;
    boolean mProcessView = false;

    public AdapterPost(Context context, List<ModelPost> postList) {
        this.context = context;
        this.postList = postList;
        likeRef = FirebaseDatabase.getInstance().getReference().child("Likes");
        viewRef = FirebaseDatabase.getInstance().getReference().child("Views");
        postsRef1 = FirebaseDatabase.getInstance().getReference().child("Posts");
    }


    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.post, parent, false);
        context = parent.getContext();
        return new MyHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {
        String dp = postList.get(position).getDp();
        String id = postList.get(position).getId();
        String meme = postList.get(position).getMeme();
        String name = postList.get(position).getName();
        String pId = postList.get(position).getpId();
        String pTime = postList.get(position).getpTime();
        String text = postList.get(position).getText();
        String vine = postList.get(position).getVine();
        String type = postList.get(position).getType();
        String pViews = postList.get(position).getpViews();
        String comment = postList.get(position).getpComments();

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        userId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
        //Time
        GetTimeAgo getTimeAgo = new GetTimeAgo();
        long lastTime = Long.parseLong(pTime);
        String lastSeenTime = GetTimeAgo.getTimeAgo(lastTime);

        //Set Data
        holder.pName.setText(name);
        holder.pText.setText(text);
        holder.pType.setText(type);
        holder.views.setText(pViews);
        setLikes(holder, pId);
        setViews(holder, pId);
        String ed_text = holder.pText.getText().toString().trim();
        if (ed_text.length() > 0) {
            holder.constraintLayout9.setVisibility(View.VISIBLE);

        } else {
            holder.constraintLayout9.setVisibility(View.GONE);
        }

        if (comment.equals("0")) {
            holder.commentNo.setText("Comment");

        } else {
            holder.commentNo.setText(comment);
        }

        HashTagHelper mTextHashTagHelper = HashTagHelper.Creator.create(context.getResources().getColor(R.color.colorPrimary), hashTag -> {
            Intent intent1 = new Intent(context, Search.class);
            intent1.putExtra("hashTag",hashTag);
           context.startActivity(intent1);
        });

        mTextHashTagHelper.handle(holder.pText);

        //More
        //Time

        holder.like.setOnClickListener(v -> {
            mProcessLike = true;
            String postId = postList.get(position).getpId();
            likeRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (mProcessLike) {
                        if (dataSnapshot.child(postId).hasChild(userId)) {
                            likeRef.child(postId).child(userId).removeValue();
                            mProcessLike = false;
                        } else {
                            likeRef.child(postId).child(userId).setValue("Liked");
                            mProcessLike = false;
                            addToHisNotification(""+id,""+pId,"Liked your post");
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        });

        holder.comment.setOnClickListener(v -> {
            if (vine.equals("noVideo")) {
                Intent intent = new Intent(context, PostDetails.class);
                intent.putExtra("postId", pId);
                context.startActivity(intent);
            } else {

                int pViews1 = Integer.parseInt(postList.get(position).getpViews());
                mProcessView = true;
                String postId = postList.get(position).getpId();
                viewRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (mProcessView) {
                            if (dataSnapshot.child(postId).hasChild(userId)) {
                                mProcessView = false;
                                Intent intent = new Intent(context, PostDetails.class);
                                intent.putExtra("postId", pId);
                                context.startActivity(intent);
                            } else {
                                postsRef1.child(postId).child("pViews").setValue("" + (pViews1 + 1));
                                mProcessView = false;
                                Intent intent = new Intent(context, PostDetails.class);
                                intent.putExtra("postId", pId);
                                viewRef.child(postId).child(userId).setValue("viewed");
                                context.startActivity(intent);
                                addToHisNotification(""+id,""+pId,"Viewed  your post");
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


            }
        });
        //Share
        holder.share.setOnClickListener(v -> shareMoreOptions(holder.share,holder, pId));

        //Click
        holder.pName.setOnClickListener(v -> {
            Intent intent = new Intent(context, UserProfile.class);
            intent.putExtra("hisUid", id);
            context.startActivity(intent);
        });
        holder.pDp.setOnClickListener(v -> {
            Intent intent = new Intent(context, UserProfile.class);
            intent.putExtra("hisUid", id);
            context.startActivity(intent);
        });

        holder.more.setOnClickListener(v -> showMoreOptions(holder.more, id, userId, pId, meme, vine));



        //DP
        try {
            Picasso.get().load(dp).placeholder(R.drawable.avatar).into(holder.pDp);
        } catch (Exception ignored) {

        }

        //Post Image
        if (meme.equals("noImage")) {
            holder.pMeme.setVisibility(View.GONE);
        } else {
            try {
                Picasso.get().load(meme).into(holder.pMeme);
            } catch (Exception ignored) {

            }
        }

        if (meme.equals("noImage") &&  vine.equals("noVideo")){
            holder.load.setVisibility(View.GONE);
        }



        Uri uri = Uri.parse(vine);
        //Post Vine
        if (vine.equals("noVideo")) {
            holder.video_share.setVisibility(View.GONE);
            holder.pVine.setVisibility(View.GONE);
        } else {
            try {
                Glide.with(context).asBitmap().centerCrop().load(uri).into(holder.pVine);
                holder.pause.setVisibility(View.VISIBLE);
                holder.video_share.setVisibility(View.VISIBLE);
                holder.share.setVisibility(View.GONE);
                holder.view_ly.setVisibility(View.VISIBLE);
            } catch (Exception ignored) {

            }
        }

        holder.video_share.setOnClickListener(v -> vidshareMoreOptions(holder.video_share, pId,vine,text));




        Adpref adpref;
        adpref = new Adpref(context);
        if (adpref.loadAdsModeState()){
            if (!vine.equals("noVideo")){
                holder.ad.setVisibility(View.VISIBLE);
            }
        }



        holder.viewlt.setOnClickListener(new DoubleClick(new DoubleClickListener() {
            @Override
            public void onSingleClick(View view) {
                if (vine.equals("noVideo")) {
                    Intent intent = new Intent(context, PostDetails.class);
                    intent.putExtra("postId", pId);
                    context.startActivity(intent);
                } else {

                    int pViews = Integer.parseInt(postList.get(position).getpViews());
                    mProcessView = true;
                    String postId = postList.get(position).getpId();
                    viewRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (mProcessView) {
                                if (dataSnapshot.child(postId).hasChild(userId)) {
                                    mProcessView = false;
                                    Intent intent = new Intent(context, PostDetails.class);
                                    intent.putExtra("postId", pId);
                                    context.startActivity(intent);
                                } else {
                                    postsRef1.child(postId).child("pViews").setValue("" + (pViews + 1));
                                    mProcessView = false;
                                    Intent intent = new Intent(context, PostDetails.class);
                                    intent.putExtra("postId", pId);
                                    viewRef.child(postId).child(userId).setValue("viewed");
                                    context.startActivity(intent);
                                    addToHisNotification(""+id,""+pId,"Viewed  your post");
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });


                }
            }

            @Override
            public void onDoubleClick(View view) {
                mProcessLike = true;
                String postId = postList.get(position).getpId();
                likeRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (mProcessLike) {
                            if (dataSnapshot.child(postId).hasChild(userId)) {
                                likeRef.child(postId).child(userId).removeValue();
                                mProcessLike = false;
                            } else {
                                likeRef.child(postId).child(userId).setValue("Liked");
                                mProcessLike = false;
                                addToHisNotification(""+id,""+pId,"Liked your post");
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        }));

        holder.constraintLayout9.setOnClickListener(new DoubleClick(new DoubleClickListener() {
            @Override
            public void onSingleClick(View view) {
                if (vine.equals("noVideo")) {
                    Intent intent = new Intent(context, PostDetails.class);
                    intent.putExtra("postId", pId);
                    context.startActivity(intent);
                } else {

                    int pViews = Integer.parseInt(postList.get(position).getpViews());
                    mProcessView = true;
                    String postId = postList.get(position).getpId();
                    viewRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (mProcessView) {
                                if (dataSnapshot.child(postId).hasChild(userId)) {
                                    mProcessView = false;
                                    Intent intent = new Intent(context, PostDetails.class);
                                    intent.putExtra("postId", pId);
                                    context.startActivity(intent);
                                } else {
                                    postsRef1.child(postId).child("pViews").setValue("" + (pViews + 1));
                                    mProcessView = false;
                                    Intent intent = new Intent(context, PostDetails.class);
                                    intent.putExtra("postId", pId);
                                    viewRef.child(postId).child(userId).setValue("viewed");
                                    context.startActivity(intent);
                                    addToHisNotification(""+id,""+pId,"Viewed  your post");
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });


                }
            }

            @Override
            public void onDoubleClick(View view) {
                mProcessLike = true;
                String postId = postList.get(position).getpId();
                likeRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (mProcessLike) {
                            if (dataSnapshot.child(postId).hasChild(userId)) {
                                likeRef.child(postId).child(userId).removeValue();
                                mProcessLike = false;
                            } else {
                                likeRef.child(postId).child(userId).setValue("Liked");
                                mProcessLike = false;
                                addToHisNotification(""+id,""+pId,"Liked your post");
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        }));

        noLike(position,holder);

    }

    private void noLike(int position, MyHolder holder) {
        String postId = postList.get(position).getpId();
      likeRef.child(postId).addValueEventListener(new ValueEventListener() {
          @Override
          public void onDataChange(@NonNull DataSnapshot snapshot) {
             String numOfLikes = String.valueOf((int) snapshot.getChildrenCount());
              if (numOfLikes.equals("0")) {
                  holder.likeNo.setText("Like");

              } else {
                  holder.likeNo.setText(snapshot.getChildrenCount()+"");
              }

          }

          @Override
          public void onCancelled(@NonNull DatabaseError error) {

          }
      });
    }

    private void vidshareMoreOptions(RelativeLayout video_share, String pId, String vine, String text) {
        PopupMenu popupMenu = new PopupMenu(context, video_share, Gravity.END);

        popupMenu.getMenu().add(Menu.NONE,0,0, "In chats");
        popupMenu.getMenu().add(Menu.NONE,1,0, "To apps");
        popupMenu.setOnMenuItemClickListener(item -> {
            int id = item.getItemId();
            if (id==0){

                Intent intent = new Intent(context, ShareActivity.class);
                intent.putExtra("postId", pId);
                context.startActivity(intent);

            }else if (id==1){
                Intent intent2 = new Intent(Intent.ACTION_SEND);
                intent2.setType("text/*");
                intent2.putExtra(Intent.EXTRA_SUBJECT,"Subject Here");
                intent2.putExtra(Intent.EXTRA_TEXT, text +" Link: "+ vine);
                context.startActivity(Intent.createChooser(intent2, "Share Via"));

            }

            return false;
        });
        popupMenu.show();
    }


    private void shareMoreOptions(RelativeLayout share, MyHolder holder, String pId) {
        PopupMenu popupMenu = new PopupMenu(context, share, Gravity.END);

        popupMenu.getMenu().add(Menu.NONE,0,0, "In chats");
        popupMenu.getMenu().add(Menu.NONE,1,0, "In groups");
        popupMenu.getMenu().add(Menu.NONE,2,0, "To apps");
        popupMenu.setOnMenuItemClickListener(item -> {
            int id = item.getItemId();
            if (id==0){
                Intent intent = new Intent(context, ShareActivity.class);
                intent.putExtra("postId", pId);
                context.startActivity(intent);
            }else if (id==1){
                Intent intent = new Intent(context, ShareGroupActivity.class);
                intent.putExtra("postId", pId);
                context.startActivity(intent);
            }
            else if (id==2){
                String shareText = holder.pText.getText().toString().trim();
                BitmapDrawable bitmapDrawable = (BitmapDrawable) holder.pMeme.getDrawable();
                if (bitmapDrawable == null) {
                    shareTextOnly(shareText);
                } else {
                    Bitmap bitmap = bitmapDrawable.getBitmap();
                    shareImageAndText(shareText, bitmap);
                }
            }

            return false;
        });
        popupMenu.show();
    }


    private void shareImageAndText(String text, Bitmap bitmap) {
        Uri uri = saveImageToShare(bitmap);
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        intent.putExtra(Intent.EXTRA_TEXT, text);
        intent.putExtra(Intent.EXTRA_SUBJECT, "Subject Here");
        intent.setType("image/*");
        context.startActivity(Intent.createChooser(intent, "Share Via"));
    }

    private Uri saveImageToShare(Bitmap bitmap) {
        File imageFolder = new File(context.getCacheDir(), "images");
        Uri uri = null;
        try {
            imageFolder.mkdir();
            File file = new File(imageFolder, "shared_image.png");
            FileOutputStream stream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 90, stream);
            stream.flush();
            stream.close();
            uri = FileProvider.getUriForFile(context, "com.spacester.myfriends.fileprovider", file);

        } catch (Exception e) {
            StyleableToast st = new StyleableToast(context, e.getMessage(), Toast.LENGTH_LONG);
            st.setBackgroundColor(Color.parseColor("#001E55"));
            st.setTextColor(Color.WHITE);
            st.setIcon(R.drawable.ic_check_wt);
            st.setMaxAlpha();
            st.show();
        }
        return uri;
    }


    private void shareTextOnly(String text) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/*");
        intent.putExtra(Intent.EXTRA_SUBJECT,"Subject Here");
        intent.putExtra(Intent.EXTRA_TEXT, text);
        context.startActivity(Intent.createChooser(intent, "Share Via"));
    }

    private void setViews(MyHolder holder, String postKey) {
        viewRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(postKey).hasChild(userId)){

                    holder.eye.setImageResource(R.drawable.ic_eyed);

                }else {
                    holder.eye.setImageResource(R.drawable.ic_eye);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void setLikes(MyHolder holder, String postKey) {
        likeRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(postKey).hasChild(userId)){

                    holder.like_img.setImageResource(R.drawable.ic_liked);

                }else {
                    holder.like_img.setImageResource(R.drawable.ic_like);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void showMoreOptions(ImageView more, String id, String userId, String pId, String meme, String vine) {

        PopupMenu popupMenu = new PopupMenu(context, more, Gravity.END);


        if (id.equals(userId)){
            popupMenu.getMenu().add(Menu.NONE,0,0, "Delete");
            popupMenu.getMenu().add(Menu.NONE,1,0, "Edit");
        }
        popupMenu.getMenu().add(Menu.NONE, 2,0,"Save");
        popupMenu.getMenu().add(Menu.NONE, 3,0,"Details");
        popupMenu.getMenu().add(Menu.NONE, 4,0,"Liked By");
        if (!meme.equals("noImage")){
            popupMenu.getMenu().add(Menu.NONE, 5,0,"Download");
        }
        if (!vine.equals("noVideo")){
            popupMenu.getMenu().add(Menu.NONE, 6,0,"Download");
        }
        if(!meme.equals("noImage") || !vine.equals("noVideo")){
            popupMenu.getMenu().add(Menu.NONE,7,0, "Fullscreen");
        }
        popupMenu.setOnMenuItemClickListener(item -> {
            int id1 = item.getItemId();
            if (id1 ==0){
                beginDelete(pId,meme,vine);
            }else if (id1 ==1){
                Intent intent = new Intent(context, UpdatePost.class);
                intent.putExtra("key","editPost");
                intent.putExtra("editPostId", pId);
                context.startActivity(intent);
            }
            else if (id1 ==2){
     FirebaseDatabase.getInstance().getReference().child("Saves").child(userId)
             .child(pId).setValue(true);
            }
            else if (id1 ==3){
                Intent intent = new Intent(context, PostDetails.class);
                intent.putExtra("postId", pId);
                context.startActivity(intent);
            }
            else if (id1 ==4){
                Intent intent = new Intent(context, PostLikedBy.class);
                intent.putExtra("postId", pId);
                context.startActivity(intent);
            }
            else if (id1 ==5){
                StorageReference picRef = FirebaseStorage.getInstance().getReferenceFromUrl(meme);
                picRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    String url = uri.toString();
                    downloadFile(context, "Image", ".png", DIRECTORY_DOWNLOADS, url);

                }).addOnFailureListener(e -> {

                });
            }
            else if (id1 ==6){
                StorageReference picRef = FirebaseStorage.getInstance().getReferenceFromUrl(vine);
                picRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    String url = uri.toString();
                    downloadFile(context, "Video", ".mp4", DIRECTORY_DOWNLOADS, url);

                }).addOnFailureListener(e -> {

                });
            }else if (id1 ==7){
                if(!vine.equals("noVideo")){
                    Intent intent = new Intent(context, MediaView.class);
                    intent.putExtra("type","video");
                    intent.putExtra("uri",vine);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                   context.startActivity(intent);
                }else
                if(!meme.equals("noImage")){
                    Intent intent = new Intent(context, MediaView.class);
                    intent.putExtra("type","image");
                    intent.putExtra("uri",meme);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                }

            }
            return false;
        });
        popupMenu.show();


    }

    private void beginDelete(String pId, String meme, String vine) {

        if (vine.equals("noVideo") && meme.equals("noImage")){
   deleteWithoutBoth(pId);
        }else if (vine.equals("noVideo")){
          deleteWithoutVine(pId, meme);
        }else if (meme.equals("noImage")){
           deleteWithoutMeme(pId, vine);
        }

    }

    private void deleteWithoutBoth(String pId) {
        Query query = FirebaseDatabase.getInstance().getReference("Posts").orderByChild("pId").equalTo(pId);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    ds.getRef().removeValue();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void deleteWithoutVine(String pId, String meme) {

        StorageReference picRef = FirebaseStorage.getInstance().getReferenceFromUrl(meme);
        picRef.delete().addOnSuccessListener(aVoid -> {

            Query query = FirebaseDatabase.getInstance().getReference("Posts").orderByChild("pId").equalTo(pId);
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot ds: dataSnapshot.getChildren()){
                        ds.getRef().removeValue();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }).addOnFailureListener(e -> {

        });

    }

    public void downloadFile(Context context, String fileName, String fileExtension, String destinationDirectory, String url){
        DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        Uri uri1 = Uri.parse(url);
        DownloadManager.Request request = new DownloadManager.Request(uri1);
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalFilesDir(context, destinationDirectory, fileName + fileExtension);
        Objects.requireNonNull(downloadManager).enqueue(request);
    }

    private void deleteWithoutMeme(String pId, String vine) {
        StorageReference vidRef = FirebaseStorage.getInstance().getReferenceFromUrl(vine);
        vidRef.delete().addOnSuccessListener(aVoid -> {

            Query query = FirebaseDatabase.getInstance().getReference("Posts").orderByChild("pId").equalTo(pId);
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot ds: dataSnapshot.getChildren()){
                        ds.getRef().removeValue();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }).addOnFailureListener(e -> {

        });
    }

    private void addToHisNotification(String hisUid, String pId, String notification){
        String timestamp = ""+System.currentTimeMillis();
        HashMap<Object, String> hashMap = new HashMap<>();
        hashMap.put("pId", pId);
        hashMap.put("timestamp", timestamp);
        hashMap.put("pUid", hisUid);
        hashMap.put("notification", notification);
        hashMap.put("sUid", userId);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(hisUid).child("Notifications").child(timestamp).setValue(hashMap)
                .addOnSuccessListener(aVoid -> {

                }).addOnFailureListener(e -> {

                });

    }

    @Override
    public int getItemCount() {
        return postList.size();
    }




    class MyHolder extends RecyclerView.ViewHolder{

        final CircleImageView pDp;
        final ImageView pMeme;
        final ImageView more;
        final ImageView like_img;
        final ImageView eye;
        final TextView pName;
        final TextView pType;
        final TextView pText;
        final TextView likeNo;
        final TextView commentNo;
        final TextView views;
        final ImageView pVine;
        final RelativeLayout like;
        final RelativeLayout comment;
        final RelativeLayout share;
        final RelativeLayout view_ly;
        final RelativeLayout video_share;
        final RelativeLayout ad;
        final ImageView pause;
        final ProgressBar load;
        final ConstraintLayout constraintLayout9;
        final ConstraintLayout viewlt;


        public MyHolder(@NonNull View itemView) {
            super(itemView);
            pDp = itemView.findViewById(R.id.circleImageView3);
            eye = itemView.findViewById(R.id.eye);
            pMeme = itemView.findViewById(R.id.imageView2);
            pName = itemView.findViewById(R.id.name);
            ad = itemView.findViewById(R.id.ad);
            pType = itemView.findViewById(R.id.username);
            likeNo = itemView.findViewById(R.id.likeNo);
            commentNo = itemView.findViewById(R.id.commentNo);
            load = itemView.findViewById(R.id.load);
            views = itemView.findViewById(R.id.views);
            view_ly = itemView.findViewById(R.id.view_ly);
            more = itemView.findViewById(R.id.more);
            pVine = itemView.findViewById(R.id.videoView);
            pause = itemView.findViewById(R.id.exomedia_controls_play_pause_btn);
            like_img = itemView.findViewById(R.id.like_img);
            pText = itemView.findViewById(R.id.textView2);
            viewlt = itemView.findViewById(R.id.viewlt);
            like = itemView.findViewById(R.id.relativeLayout);
            comment = itemView.findViewById(R.id.relativeLayout6);
            share = itemView.findViewById(R.id.meme_share);
            video_share = itemView.findViewById(R.id.vine_share);
            constraintLayout9 = itemView.findViewById(R.id.constraintLayout9);
            MobileAds.initialize(context, initializationStatus -> {

            });
            AdLoader.Builder builder = new AdLoader.Builder(context, context.getString(R.string.native_ad_unit_id));
            builder.forUnifiedNativeAd(unifiedNativeAd -> {
                TemplateView templateView = itemView.findViewById(R.id.my_template);
                templateView.setNativeAd(unifiedNativeAd);
            });

            AdLoader adLoader = builder.build();
            AdRequest adRequest = new AdRequest.Builder().build();
            adLoader.loadAd(adRequest);

        }
    }

}

