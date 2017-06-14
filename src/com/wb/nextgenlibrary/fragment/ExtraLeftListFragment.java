package com.wb.nextgenlibrary.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.wb.nextgenlibrary.NextGenExperience;
import com.wb.nextgenlibrary.R;


/**
 * Created by gzcheng on 1/12/16.
 */
public abstract class ExtraLeftListFragment<T> extends Fragment implements AdapterView.OnItemClickListener{

    protected ListView listView;
    protected NextGenExtraLeftPanelAdapter listAdaptor;
    protected TextView titleTextView;
    View view;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if (view != null) {
            ViewGroup parent = (ViewGroup) view.getParent();
            if (parent != null)
                parent.removeView(view);
        }
        try {
            view = inflater.inflate(R.layout.next_gen_list_view, container, false);
        } catch (InflateException e) {
        /* map is already there, just return view as it is */
        }
        return view;

    }
    protected int getPadding(){
        float density = NextGenExperience.getScreenDensity(getActivity());
        int spacing = (int)(10 *density);
        return spacing;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        listView = (ListView)view.findViewById(R.id.next_gen_lists);
        if (listView != null){
            listView.setPadding(getPadding(), 0, getPadding(), getPadding());
            listAdaptor = new NextGenExtraLeftPanelAdapter();
            listView.setAdapter(listAdaptor);
            listView.setOnItemClickListener(this);

            listView.setItemChecked(getStartupSelectedIndex() + listView.getHeaderViewsCount(), true);
        }

    }

    @Override
    public void onDestroy(){
        listAdaptor = null;
        if (listView != null){
            listView.setAdapter(null);
        }
        super.onDestroy();
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
        listAdaptor.selectedIndex = position - listView.getHeaderViewsCount();;
        listView.setSelection(position);
        listView.setItemChecked(position, true);
        onListItemClick(position, listAdaptor.getItem(listAdaptor.selectedIndex));
        listAdaptor.notifyDataSetChanged();
    }

    protected abstract void onListItemClick(int index, T selectedObject);

    protected abstract int getListItemCount();

    protected abstract T getListItemAtPosition(int i);

    protected abstract int getListItemViewId();

    protected int getListItemViewId(int row){
        return getListItemViewId();
    }

    protected abstract void fillListRowWithObjectInfo(View rowView, T item);

    protected abstract String getHeaderText();

    protected abstract int getStartupSelectedIndex();

    public void resetSelectedItem(){
        listAdaptor.selectedIndex = getStartupSelectedIndex();
        listView.setItemChecked(getStartupSelectedIndex() + listView.getHeaderViewsCount(), true);
        if (listAdaptor.selectedIndex != -1)
            onListItemClick(listAdaptor.selectedIndex, listAdaptor.getItem(listAdaptor.selectedIndex));
        listAdaptor.notifyDataSetChanged();
    }

    public void notifyDataSetChanged(){
        if (listAdaptor != null)
            listAdaptor.notifyDataSetChanged();
    }

    public class NextGenExtraLeftPanelAdapter extends BaseAdapter {

        protected int selectedIndex = getStartupSelectedIndex();

        protected LayoutInflater mInflater;


        NextGenExtraLeftPanelAdapter() {

            mInflater = LayoutInflater.from(getActivity());
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            if (position >= getCount() || position < 0){
                return null;
            }
            int targetViewId = getListItemViewId(position);

            if (convertView == null   || targetViewId != convertView.getId()) {

                convertView = mInflater.inflate(getListItemViewId(position), parent, false);

            } else {

            }

            boolean bSelectedStateChanged = (selectedIndex == position) != convertView.isActivated();


            if (bSelectedStateChanged ) {       //programmatically changes the activated state of the row.
                convertView.setActivated(selectedIndex == position);
                convertView.postInvalidate();
            }
            fillListRowWithObjectInfo(convertView, getItem(position));

            return convertView;
        }

        public int getCount() {
            return getListItemCount();
        }

        public T getItem(int position) {
            return getListItemAtPosition(position);
        }

        public long getItemId(int position) {
            return position;
        }



    }
}
