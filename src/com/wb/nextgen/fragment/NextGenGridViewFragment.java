package com.wb.nextgen.fragment;

/**
 * Created by gzcheng on 3/3/16.
 */

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.tonicartos.widget.stickygridheaders.StickyGridHeadersBaseAdapter;
import com.tonicartos.widget.stickygridheaders.StickyGridHeadersGridView;
import com.wb.nextgen.NextGenApplication;
import com.wb.nextgen.R;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.wb.nextgen.NextGenApplication;
import com.wb.nextgen.R;
import com.tonicartos.widget.stickygridheaders.StickyGridHeadersBaseAdapter;


/**
 * Created by gzcheng on 1/12/16.
 */
public abstract class NextGenGridViewFragment extends Fragment implements AdapterView.OnItemClickListener{

    protected StickyGridHeadersGridView gridView;
    protected NextGenExtraLeftPanelAdapter listAdaptor;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.next_gen_grid_view, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        float density = NextGenApplication.getScreenDensity(getActivity());
        int spacing = (int)(10 *density);
        gridView = (StickyGridHeadersGridView)view.findViewById(R.id.next_gen_grid);
        if (gridView != null){
            gridView.setNumColumns(getNumberOfColumns());
            gridView.setHorizontalSpacing(spacing);
            gridView.setVerticalSpacing(spacing);
            gridView.setPadding(spacing, 0, spacing, spacing);
            //gridView.setHeadersIgnorePadding(true);
            listAdaptor = new NextGenExtraLeftPanelAdapter();
            gridView.setAdapter(listAdaptor);
            gridView.setOnItemClickListener(this);
        }

    }
    @Override
    public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
        listAdaptor.selectedIndex = position;
        gridView.setItemChecked(position, true);
        onListItmeClick(v, position, id);
    }

    protected abstract void onListItmeClick(View v, int position, long id);

    protected abstract int getNumberOfColumns();

    protected abstract int getListItemCount();

    protected abstract Object getListItemAtPosition(int i);

    protected abstract int getListItemViewId();

    protected abstract void fillListRowWithObjectInfo(int position, View rowView, Object item, boolean isSelected);

    protected abstract String getHeaderText();

    protected abstract int getHeaderChildenCount(int header);

    protected abstract int getHeaderCount();

    protected abstract int getStartupSelectedIndex();

    public void resetSelectedItem(){
        listAdaptor.selectedIndex = getStartupSelectedIndex();
    }

    public class NextGenExtraLeftPanelAdapter extends BaseAdapter implements StickyGridHeadersBaseAdapter {

        //protected NextGenExtraActivity activity;

        protected int selectedIndex = getStartupSelectedIndex();

        protected LayoutInflater mInflater;

        private TextView headerTextView;

        NextGenExtraLeftPanelAdapter() {

            mInflater = LayoutInflater.from(getActivity());
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            if (position >= getCount() || position < 0){
                return null;
            }


            if (convertView == null  ) {
                LayoutInflater inflater = getActivity().getLayoutInflater();
                convertView = inflater.inflate(getListItemViewId(), parent, false);


            } else {

            }


            gridView.setItemChecked(position, selectedIndex == position);
            fillListRowWithObjectInfo(position, convertView, getItem(position), selectedIndex == position);

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
            headerTextView.setTextSize(20);
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

    }
}
