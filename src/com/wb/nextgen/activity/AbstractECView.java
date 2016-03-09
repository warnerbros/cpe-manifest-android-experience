package com.wb.nextgen.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.wb.nextgen.NextGenApplication;
import com.wb.nextgen.R;
import com.wb.nextgen.data.DemoData;
import com.wb.nextgen.fragment.ECViewLeftListFragment;
import com.wb.nextgen.fragment.NextGenExtraLeftListFragment;
import com.wb.nextgen.util.PicassoTrustAll;
import com.wb.nextgen.util.utils.F;

/**
 * Created by gzcheng on 2/25/16.
 */
public abstract class AbstractECView extends FragmentActivity {

    //protected ListView ecListView;
    //private NextGenECListAdapter ecListAdaptor;
    protected DemoData.ECGroupData ecGroupData ;
    protected ECViewLeftListFragment listFragment;

    public abstract void onLeftListItemSelected(DemoData.ECContentData ecContentData);

    public abstract int getContentViewId();

    public DemoData.ECGroupData getECGroupData(){
        return ecGroupData;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        String groupId = intent.getStringExtra(F.ID);
        ecGroupData = DemoData.findECGroupDataById(groupId);

        setContentView(getContentViewId());

        float density = NextGenApplication.getScreenDensity(this);
        int spacing = (int)(10 *density);

        listFragment = (ECViewLeftListFragment) getSupportFragmentManager().findFragmentById(R.id.ec_fragment_list);

        /*
        ecListView = (ListView) findViewById(R.id.next_gen_ec_list);
        ecListView.setPadding(spacing, 0, spacing, spacing);
        ecListAdaptor = new NextGenECListAdapter();
        ecListView.setAdapter(ecListAdaptor);
        ecListView.setOnItemClickListener(ecListAdaptor);

        TextView titleTextView = new TextView(this);
        titleTextView.setText(getHeaderText());
        titleTextView.setTextSize(getResources().getDimension(R.dimen.list_title_font_size));
        ecListView.addHeaderView(titleTextView);*/
    }

    public void onStart() {
        super.onStart();
        if (listFragment != null){
            listFragment.onListItemClick(ecGroupData.ecContents.get(0));
            listFragment.scrollToTop();
        }
    }

    public abstract int getListItemViewLayoutId();
/*
    public String getHeaderText(){
        return ecGroupData.title;
    }


        public void onResume() {
            super.onResume();
            if (ecListAdaptor != null){
                ecListAdaptor.onItemClick(ecListView, null, 0, 0);
            }
        }

        public class NextGenECListAdapter extends BaseAdapter implements AdapterView.OnItemClickListener {

            //protected NextGenExtraActivity activity;

            protected LayoutInflater mInflater;

            private TextView headerTextView;

            private int selectedItemIndex = 0;

            NextGenECListAdapter() {

                mInflater = LayoutInflater.from(AbstractECView.this);
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

            DemoData.ECContentData thisEC = ecGroupData.ecContents.get(position);

            ImageView imageView = (ImageView)convertView.findViewById(R.id.ec_list_image);
            if (imageView != null){
                //ViewGroup.LayoutParams imageLayoutParams = imageView.getLayoutParams();

                PicassoTrustAll.loadImageIntoView(AbstractECView.this, thisEC.posterImgUrl, imageView);
            }

            TextView ecNameText = (TextView)convertView.findViewById(R.id.ec_list_name_text);
            if (ecNameText != null){
                ecNameText.setText(thisEC.title);
            }

            ImageView mask = (ImageView)convertView.findViewById(R.id.ec_inactive_mask);
            if (mask != null){
                if (selectedItemIndex == position){
                    mask.setVisibility(View.INVISIBLE);
                }else
                    mask.setVisibility(View.VISIBLE);
            }

            return convertView;
        }

        public int getCount() {
            return ecGroupData.ecContents.size();
        }

        public Object getItem(int position) {
            return ecGroupData.ecContents.get(position);
        }

        public long getItemId(int position) {
            return position;
        }


        // AdapterView.OnItemClickListener
        @Override
        public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
            selectedItemIndex = position - ecListView.getHeaderViewsCount();
            onLeftListItemSelected(ecGroupData.ecContents.get(position));
            notifyDataSetChanged();
            //onListItmeClick(v, position, id);
        }

    }*/
}
