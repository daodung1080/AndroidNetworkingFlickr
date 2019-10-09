package com.dung.awajava.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bogdwellers.pinchtozoom.ImageMatrixTouchHandler;
import com.dung.awajava.R;
import com.dung.awajava.model.Photo;
import com.smarteist.autoimageslider.SliderViewAdapter;
import com.squareup.picasso.Picasso;

import java.util.List;

import jp.wasabeef.picasso.transformations.BlurTransformation;

public class SliderAdapter extends SliderViewAdapter<SliderAdapter.SliderAdapterVH> {

    private Context context;
    private List<Photo> list;

    public SliderAdapter(Context context, List<Photo> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public SliderAdapterVH onCreateViewHolder(ViewGroup parent) {
        View inflate = LayoutInflater.from(context).inflate(R.layout.image_slider_layout_item, null);
        return new SliderAdapterVH(inflate);
    }

    @Override
    public void onBindViewHolder(SliderAdapterVH holder, int i) {

        Photo photo = list.get(i);
        Picasso.get().load(photo.getUrlM()).into(holder.imgPhotoDetail);
        Picasso.get()
                .load(photo.getUrlL())
                .transform(new BlurTransformation(context, 25, 1))
                .into(holder.imgBlur);

    }

    @Override
    public int getCount() {
        //slider view count could be dynamic size
        return list.size();
    }

    class SliderAdapterVH extends SliderViewAdapter.ViewHolder {

        View itemView;
        ImageView imgPhotoDetail;
        ImageView imgBlur;

        public SliderAdapterVH(View itemView) {
            super(itemView);
            imgPhotoDetail = itemView.findViewById(R.id.imgPhotoDetail);
            imgBlur = itemView.findViewById(R.id.imgBlur);
            imgPhotoDetail.setOnTouchListener(new ImageMatrixTouchHandler(itemView.getContext()));
            this.itemView = itemView;
        }
    }
}
