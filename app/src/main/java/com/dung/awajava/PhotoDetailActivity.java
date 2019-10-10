package com.dung.awajava;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.dung.awajava.adapter.SliderAdapter;
import com.dung.awajava.model.Photo;
import com.joaquimley.faboptions.FabOptions;
import com.smarteist.autoimageslider.CircularSliderHandle;
import com.smarteist.autoimageslider.IndicatorAnimations;
import com.smarteist.autoimageslider.IndicatorView.draw.controller.DrawController;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PhotoDetailActivity extends BaseActivity implements View.OnClickListener, Serializable {

    ImageView imgPhotoDetail;
    FabOptions fabPhoto;
    int photoPosition = 1;
    SliderView svPhoto;
    List<Photo> photoList;
    SliderAdapter adapter;
    int DOWNLOAD_NOTIFICATION_ID = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_detail);

        initView();
        getDataFromPreviousActivity();
        verifyPermissions();

    }

    private void getDataFromPreviousActivity() {
        photoList = MainActivity.list;
        Intent i = getIntent();
        photoPosition = i.getIntExtra("position", 1);
        adapter = new SliderAdapter(this, photoList);
        svPhoto.setSliderAdapter(adapter);
        svPhoto.setCurrentPagePosition(photoPosition);
    }

    private void initView() {
        photoList = new ArrayList<>();
        fabPhoto = findViewById(R.id.fabPhoto);
        fabPhoto.setButtonsMenu(R.menu.fab_menu);
        fabPhoto.setOnClickListener(this);
        registerForContextMenu(fabPhoto);
        svPhoto = findViewById(R.id.svPhoto);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab_back:
                onBackPressed();
                break;


            case R.id.fab_download:
                openContextMenu(fabPhoto);
                break;

            default:
                // no-op
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getMenuInflater().inflate(R.menu.download_menu, menu);
        Photo photo = photoList.get(svPhoto.getCurrentPagePosition());
        menu.findItem(R.id.downloadLow).setTitle("Download lowest quality ("+photo.getWidthS()+" X "+photo.getHeightS()+")");
        menu.findItem(R.id.downloadMedium).setTitle("Download medium quality ("+photo.getWidthM()+" X "+photo.getHeightM()+")");
        menu.findItem(R.id.downloadBest).setTitle("Download highest quality ("+photo.getWidthL()+" X "+photo.getHeightL()+")");
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        Photo photo = photoList.get(svPhoto.getCurrentPagePosition());
        if (item.getItemId() == R.id.downloadLow) {
            SaveImage(photo.getUrlS());
        } else if (item.getItemId() == R.id.downloadMedium) {
            SaveImage(photo.getUrlM());
        } else if (item.getItemId() == R.id.downloadBest) {
            SaveImage(photo.getUrlL());
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Animatoo.animateSwipeRight(this);
    }

    private void SaveImage(final String MyUrl){
        final NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        final NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "img_download");
        builder.setContentTitle("Picture Download")
                .setContentText("Download in progress")
                .setSmallIcon(android.R.drawable.stat_sys_download)
                .setPriority(NotificationCompat.PRIORITY_HIGH);
        class SaveThisImage extends AsyncTask<Void, Void, Void> {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                int PROGRESS_MAX = 100;
                int PROGRESS_CURRENT = 0;
                builder.setProgress(PROGRESS_MAX, PROGRESS_CURRENT, false);
                notificationManager.notify(2, builder.build());
            }
            @Override
            protected Void doInBackground(Void... arg0) {
                try{

                    File sdCard = Environment.getExternalStorageDirectory();
                    @SuppressLint("DefaultLocale") String fileName = String.format("%d.png", System.currentTimeMillis());
                    File dir = new File(sdCard.getAbsolutePath() + "/Download");
                    dir.mkdirs();
                    final File myImageFile = new File(dir, fileName); // Create image file
                    FileOutputStream fos = null;
                    try {
                        fos = new FileOutputStream(myImageFile);
                        Bitmap bitmap = Picasso.get().load(MyUrl).get();
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);

                        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                        intent.setData(Uri.fromFile(myImageFile));
                        PhotoDetailActivity.this.sendBroadcast(intent);
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        try {
                            fos.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }catch (Exception e){
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                super.onPostExecute(result);

                Intent intent = new Intent();
                intent.setAction(android.content.Intent.ACTION_VIEW);
                intent.setType("image/*");
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                // Open NotificationView.java Activity
                PendingIntent pIntent = PendingIntent.getActivity(PhotoDetailActivity.this, 0, intent,
                        PendingIntent.FLAG_UPDATE_CURRENT);

                builder.setContentText("Download complete");
                int PROGRESS_MAX = 100;
                int PROGRESS_CURRENT = 100;
                builder.setProgress(PROGRESS_MAX, PROGRESS_CURRENT, false)
                        .setSmallIcon(android.R.drawable.stat_sys_download_done)
                        .setContentIntent(pIntent);
                notificationManager.notify(2, builder.build());
            }
        }
        SaveThisImage shareimg = new SaveThisImage();
        shareimg.execute();
    }

    /// Check permission
    public void verifyPermissions() {
        // This will return the current Status
        int permissionExternalMemory = ActivityCompat.checkSelfPermission(PhotoDetailActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permissionExternalMemory != PackageManager.PERMISSION_GRANTED) {
            // If permission not granted then ask for permission real time.
            ActivityCompat.requestPermissions(PhotoDetailActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }
    }

}
