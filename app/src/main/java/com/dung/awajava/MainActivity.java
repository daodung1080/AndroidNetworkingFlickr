package com.dung.awajava;

import android.content.Intent;
import android.os.Handler;
import android.os.Parcelable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.ParsedRequestListener;
import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.dung.awajava.adapter.PhotoAdapter;
import com.dung.awajava.model.Fave;
import com.dung.awajava.model.Photo;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializer;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener {

    RecyclerView rvPhoto;
    PhotoAdapter adapter;
    public static List<Photo> list;
    SwipeRefreshLayout srPhoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        loadPhotos();

    }

    private void loadPhotos() {
        list.clear();
        AndroidNetworking.post("https://www.flickr.com/services/rest/")
                .addBodyParameter("method", "flickr.favorites.getList")
                .addBodyParameter("api_key", "0542f4aa46c91a3e1fd345f694efcd6b")
                .addBodyParameter("user_id", "184847495@N04")
                .addBodyParameter("format", "json")
                .addBodyParameter("nojsoncallback", "1")
                .addBodyParameter(
                        "extras",
                        "views,media,path_alias,url_sq,url_t,url_s,url_q,url_m,url_n,url_z,url_c,url_l,url_o"
                )
                .addBodyParameter("per_page", "10")
                .addBodyParameter("page", "1")
                .setTag("test")
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsObject(Fave.class, new ParsedRequestListener() {
                    @Override
                    public void onResponse(Object response) {
                        Fave fave = (Fave) response;
                        List<Photo> photos = fave.getPhotos().getPhoto();
                        list.addAll(photos);
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onError(ANError anError) {
                        showMessage("Lay du lieu khong thanh cong " + anError.toString());
                    }
                });
    }

    private void initView() {
        rvPhoto = findViewById(R.id.rvPhoto);
        list = new ArrayList<>();
        adapter = new PhotoAdapter(list, this, this);
        StaggeredGridLayoutManager ln = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        rvPhoto.setLayoutManager(ln);
        rvPhoto.setAdapter(adapter);
        srPhoto = findViewById(R.id.srPhoto);
        srPhoto.setOnRefreshListener(this);
    }

    @Override
    public void onRefresh() {
        srPhoto.setRefreshing(true);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                loadPhotos();
                srPhoto.setRefreshing(false);
            }
        }, 2000);
    }

    public void clickOnImage(int position) {
        Intent i = new Intent(MainActivity.this, PhotoDetailActivity.class);
        i.putExtra("position", position);
        startActivity(i);
        Animatoo.animateSwipeRight(this);
    }
}
