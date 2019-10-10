package com.dung.awajava.adapter;

import android.content.Context;
import android.media.Image;
import android.opengl.Visibility;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dung.awajava.MainActivity;
import com.dung.awajava.PhotoDetailActivity;
import com.dung.awajava.R;
import com.dung.awajava.model.Photo;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
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
    public void onBindViewHolder(@NonNull final PhotoHolder holder, final int i) {
        final Photo photo = list.get(i);
        Picasso.get().load(photo.getUrlL()).into(holder.imgPhoto);
        final Animation slide_up = AnimationUtils.loadAnimation(context, R.anim.slide_up_anim);
        final Animation slide_down = AnimationUtils.loadAnimation(context, R.anim.slide_down_anim);
        holder.imgPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.llPhotoShow.getVisibility() == View.GONE) {
                    holder.llPhotoShow.setVisibility(View.VISIBLE);
                    DecimalFormat fm = new DecimalFormat("###,###,###");
                    holder.tvPhotoView.setText(fm.format(Integer.parseInt(photo.getViews()))+" Views");
                    holder.llPhotoShow.startAnimation(slide_up);
                    holder.llPhotoShow.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            m.clickOnImage(i);
                        }
                    });
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            holder.llPhotoShow.setVisibility(View.GONE);
                            holder.llPhotoShow.startAnimation(slide_down);
                        }
                    }, 4000);
                }
                else if(holder.llPhotoShow.getVisibility() == View.VISIBLE){
                    holder.llPhotoShow.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            m.clickOnImage(i);
                        }
                    });
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    public class PhotoHolder extends RecyclerView.ViewHolder {
        ImageView imgPhoto;
        LinearLayout llPhotoShow;
        TextView tvPhotoView;

        public PhotoHolder(@NonNull View itemView) {
            super(itemView);
            imgPhoto = itemView.findViewById(R.id.imgPhoto);
            llPhotoShow = itemView.findViewById(R.id.llPhotoShow);
            tvPhotoView = itemView.findViewById(R.id.tvPhotoView);
        }
    }

}
