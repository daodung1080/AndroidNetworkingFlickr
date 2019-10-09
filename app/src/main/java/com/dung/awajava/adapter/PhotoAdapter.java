package com.dung.awajava.adapter;

import android.content.Context;
import android.media.Image;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.dung.awajava.MainActivity;
import com.dung.awajava.PhotoDetailActivity;
import com.dung.awajava.R;
import com.dung.awajava.model.Photo;
import com.squareup.picasso.Picasso;

import java.util.List;

public class PhotoAdapter extends RecyclerView.Adapter<PhotoAdapter.PhotoHolder> {

    List<Photo> list;
    Context context;
    MainActivity m;

    public PhotoAdapter(List<Photo> list, Context context, MainActivity m) {
        this.list = list;
        this.context = context;
        this.m = m;
    }

    @NonNull
    @Override
    public PhotoHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_photo, viewGroup, false);
        return new PhotoHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PhotoHolder holder, final int i) {
        Photo photo = list.get(i);
        Picasso.get().load(photo.getUrlL()).into(holder.imgPhoto);
        Animation slide_down = AnimationUtils.loadAnimation(context, R.anim.slide_up_anim);
        holder.imgPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                m.clickOnImage(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    public class PhotoHolder extends RecyclerView.ViewHolder {
        ImageView imgPhoto;

        public PhotoHolder(@NonNull View itemView) {
            super(itemView);
            imgPhoto = itemView.findViewById(R.id.imgPhoto);
        }
    }

}
