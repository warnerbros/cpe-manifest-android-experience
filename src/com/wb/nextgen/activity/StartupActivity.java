package com.wb.nextgen.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.squareup.picasso.Picasso;
import com.wb.nextgen.Manifest;
import com.wb.nextgen.NextGenApplication;
import com.wb.nextgen.R;
import com.wb.nextgen.data.NextGenStyle;
import com.wb.nextgen.util.DialogUtils;
import com.wb.nextgen.util.ExceptionHandler;
import com.wb.nextgen.util.PicassoTrustAll;
import com.wb.nextgen.util.concurrent.ResultListener;
import com.wb.nextgen.util.concurrent.Worker;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * Created by gzcheng on 7/26/16.
 */
public class StartupActivity extends NextGenHideStatusBarActivity {

    public static class ManifestItem{
        public final String imageUrl;
        public final String movieName;
        public final String manifestFileUrl;
        public final String appDataFileUrl;
        ManifestItem(String movieName, String imageUrl, String manifestFileUrl, String appDataFileUrl){
            this.imageUrl = imageUrl;
            this.manifestFileUrl = manifestFileUrl;
            this.movieName = movieName;
            this.appDataFileUrl = appDataFileUrl;
        }
    }
    static List<ManifestItem> manifestItems;
    static {
        manifestItems = new ArrayList<ManifestItem>();
        /*manifestItems.add(new ManifestItem("Man of Steel v0.5",
                "https://d19p213wjrwt85.cloudfront.net/uvvu-images/EB180713D3536025E0405B0A07341ECE",
                "mos_hls_manifest_r60-v0.5.xml",
                "mos_appdata_locations_r60-v0.5.xml"));*/
        manifestItems.add(new ManifestItem("Man of Steel v0.6",
                "https://d19p213wjrwt85.cloudfront.net/uvvu-images/EB180713D3536025E0405B0A07341ECE",
                "mos_hls_manifest_r60-v0.6.xml",
                "mos_appdata_locations_r60-v0.6.xml"));
        manifestItems.add(new ManifestItem("Batman vs Superman",
                "https://d19p213wjrwt85.cloudfront.net/uvvu-images/2C89FE061219D322E05314345B0AFE72",
                "bvs_manifest_r60-v1.0.xml",
                "bvs_appdata_locations_r60-v1.0.xml"));
    }

    GridView manifestGrid;
    ManifestGridViewAdapter gridAdapter;

    ProgressDialog mDialog;
    @Override
    public void onCreate(Bundle savedState) {
        super.onCreate(savedState);
        setContentView(R.layout.startup_view);
        manifestGrid = (GridView) findViewById(R.id.manifest_grid);
        manifestGrid.setNumColumns(3);
        gridAdapter = new ManifestGridViewAdapter();
        manifestGrid.setAdapter(gridAdapter);
        manifestGrid.setOnItemClickListener(gridAdapter);
        mDialog = new ProgressDialog(this);

    }

    class ManifestGridViewAdapter extends BaseAdapter implements AdapterView.OnItemClickListener{


        public View getView(int position, View convertView, ViewGroup parent) {
            if (position >= getCount() || position < 0){
                return null;
            }



            if (convertView == null ) {
                LayoutInflater inflater = getLayoutInflater();
                convertView = inflater.inflate(R.layout.startup_grid_item, parent, false);

            }

            ManifestItem item = getItem(position);
            ImageView posterView = (ImageView)convertView.findViewById(R.id.movie_poster);
            TextView textView = (TextView)convertView.findViewById(R.id.movie_title);

            Glide.with(StartupActivity.this).load(item.imageUrl).fitCenter().into(posterView);
            //PicassoTrustAll.loadImageIntoView(StartupActivity.this, item.imageUrl, posterView);
            textView.setText(item.movieName);
            return convertView;
        }

        public int getCount() {
            return manifestItems.size();
        }

        public ManifestItem getItem(int position) {
            return manifestItems.get(position);
        }

        public long getItemId(int position) {
            return position;
        }

        @Override
        public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
            final ManifestItem item = getItem(position);
            mDialog.setMessage("Loading");
            mDialog.setCancelable(false);
            mDialog.show();
            Worker.execute(new Callable<Boolean>() {
                public Boolean call() throws Exception{
                    return NextGenApplication.startNextGenExperience(item);
                }
            }, new ResultListener<Boolean>(){
                public void onResult(Boolean result){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mDialog.hide();
                            Intent intent = new Intent(StartupActivity.this, NextGenActivity.class);
                            startActivity(intent);
                        }
                    });

                }

                public  void onException(Exception e){

                    mDialog.hide();
                }
            });


        }

    }
}
