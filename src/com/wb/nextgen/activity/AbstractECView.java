package com.wb.nextgen.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.tonicartos.widget.stickygridheaders.StickyGridHeadersBaseAdapter;
import com.tonicartos.widget.stickygridheaders.StickyGridHeadersGridView;
import com.wb.nextgen.NextGenApplication;
import com.wb.nextgen.R;
import com.wb.nextgen.data.DemoData;
import com.wb.nextgen.util.PicassoTrustAll;
import com.wb.nextgen.util.utils.F;

/**
 * Created by gzcheng on 2/25/16.
 */
public abstract class AbstractECView extends FragmentActivity {

    protected StickyGridHeadersGridView ecListView;
    private NextGenECListAdapter ecListAdaptor;
    protected DemoData.ECGroupData ecGroupData ;


    public abstract void onLeftListItemSelected(DemoData.ECContentData ecContentData);

    public abstract int getContentViewId();

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(getContentViewId());

        Intent intent = getIntent();
        String groupId = intent.getStringExtra(F.ID);

        ecGroupData = DemoData.findECGroupDataById(groupId);


        float density = NextGenApplication.getScreenDensity(this);
        int spacing = (int)(10 *density);
        ecListView = (StickyGridHeadersGridView) findViewById(R.id.next_gen_ec_list);
        ecListView.setNumColumns(1);
        //ecListView.setSelection(0);
        //ecListView.setHorizontalSpacing(spacing);
        ecListView.setVerticalSpacing(spacing);
        ecListView.setPadding(spacing, 0, spacing, spacing);
        ecListView.setHeadersIgnorePadding(true);
        ecListAdaptor = new NextGenECListAdapter();
        ecListView.setAdapter(ecListAdaptor);
        ecListView.setOnItemClickListener(ecListAdaptor);
    }


    public String getHeaderText(){
        return ecGroupData.title;
    }


    public void onResume() {
        super.onResume();
        /*Intent intent = getIntent();
        Uri uri = intent.getData();*/
        if (ecListAdaptor != null){
            ecListAdaptor.onItemClick(ecListView, null, 0, 0);
        }
        //hideShowNextGenView();
    }

    public class NextGenECListAdapter extends BaseAdapter implements StickyGridHeadersBaseAdapter, AdapterView.OnItemClickListener {

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

        @Override
        public View getHeaderView(int position, View convertView, ViewGroup parent) {
            if (headerTextView == null){
                headerTextView = new TextView(AbstractECView.this);
            }
            headerTextView.setText(getHeaderText());
            headerTextView.setTextSize(20);
            return headerTextView;
        }

        @Override
        public int getCountForHeader(int header) {
            // TODO Auto-generated method stub
            return ecGroupData.ecContents.size();
        }

        @Override
        public int getNumHeaders() {
            // TODO Auto-generated method stub
            return 1;
        }

        // AdapterView.OnItemClickListener
        @Override
        public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
            selectedItemIndex = position;
            onLeftListItemSelected(ecGroupData.ecContents.get(position));
            notifyDataSetChanged();
            //onListItmeClick(v, position, id);
        }

    }
}
