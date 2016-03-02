package com.wb.nextgen;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;

import com.flixster.android.captioning.CaptionedPlayer;
import com.tonicartos.widget.stickygridheaders.StickyGridHeadersBaseAdapter;
import com.tonicartos.widget.stickygridheaders.StickyGridHeadersGridView;
import com.wb.nextgen.util.utils.NextGenFragmentTransactionEngine;

import net.flixster.android.drm.ObservableVideoView;

import org.w3c.dom.Text;

/**
 * Created by gzcheng on 2/25/16.
 */
public class NextGenECView extends CaptionedPlayer {
    protected ObservableVideoView videoView;
    protected StickyGridHeadersGridView ecListView;
    private NextGenECListAdapter ecListAdaptor;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.next_gen_ecs_view);
        videoView = (ObservableVideoView) findViewById(R.id.surface_view);
        videoView.setMediaController(new MediaController(this));
        //videoView.setOnErrorListener(getOnErrorListener());
        videoView.setOnPreparedListener(new PreparedListener());
        //videoView.setOnCompletionListener(getOnCompletionListener());
        videoView.requestFocus();

        float density = NextGenApplication.getScreenDensity(this);
        int spacing = (int)(10 *density);
        ecListView = (StickyGridHeadersGridView) findViewById(R.id.next_gen_ec_list);
        ecListView.setNumColumns(1);
        //ecListView.setHorizontalSpacing(spacing);
        ecListView.setVerticalSpacing(spacing);
        ecListView.setPadding(spacing, 0, spacing, spacing);
        ecListView.setHeadersIgnorePadding(true);
        ecListAdaptor = new NextGenECListAdapter();
        ecListView.setAdapter(ecListAdaptor);
        ecListView.setOnItemClickListener(ecListAdaptor);

    }

    private class PreparedListener implements MediaPlayer.OnPreparedListener {
        @Override
        public void onPrepared(MediaPlayer mp) {

            videoView.start();
        }
    }

    public void onResume() {
        super.onResume();
        Intent intent = getIntent();
        Uri uri = intent.getData();
        videoView.setVisibility(View.VISIBLE);
        videoView.setVideoURI(uri);

        //hideShowNextGenView();
    }

    public class NextGenECListAdapter extends BaseAdapter implements StickyGridHeadersBaseAdapter, AdapterView.OnItemClickListener {

        //protected NextGenExtraActivity activity;

        protected LayoutInflater mInflater;

        private TextView headerTextView;

        NextGenECListAdapter() {

            mInflater = LayoutInflater.from(NextGenECView.this);
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            if (position >= getCount() || position < 0){
                return null;
            }


            if (convertView == null  ) {
                LayoutInflater inflater = getLayoutInflater();
                convertView = inflater.inflate(R.layout.next_gen_ec_list_item, parent, false);


            } else {

            }

            ImageView imageView = (ImageView)convertView.findViewById(R.id.ec_list_image);
            TextView ecNameText = (TextView)convertView.findViewById(R.id.ec_list_name_text);




            return convertView;
        }

        public int getCount() {
            return getListItemCount();
        }

        public Object getItem(int position) {
            return getListItemAtPosition(position);
        }

        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getHeaderView(int position, View convertView, ViewGroup parent) {
            if (headerTextView == null){
                headerTextView = new TextView(getActivity());
            }
            headerTextView.setText(getHeaderText());
            headerTextView.setTextSize(40);
            return headerTextView;
        }

        @Override
        public int getCountForHeader(int header) {
            // TODO Auto-generated method stub
            return getHeaderChildenCount(header);
        }

        @Override
        public int getNumHeaders() {
            // TODO Auto-generated method stub
            return getHeaderCount();
        }

        // AdapterView.OnItemClickListener
        @Override
        public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
            onListItmeClick(v, position, id);
        }

    }
}
