package com.wb.nextgen.fragment;

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

import com.wb.nextgen.NextGenApplication;
import com.wb.nextgen.R;


/**
 * Created by gzcheng on 1/12/16.
 */
public abstract class NextGenExtraLeftListFragment extends Fragment implements AdapterView.OnItemClickListener{

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
        float density = NextGenApplication.getScreenDensity(getActivity());
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

            /*
            titleTextView = new TextView(getActivity());
            titleTextView.setText(getHeaderText());
            titleTextView.setTextSize(getResources().getDimension(R.dimen.textMedium));
            titleTextView.setTextColor(getResources().getColor(R.color.list_title_text_color));
            listView.addHeaderView(titleTextView, null, false);*/
            listView.setItemChecked(getStartupSelectedIndex() + listView.getHeaderViewsCount(), true);
        }

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
        listAdaptor.selectedIndex = position - listView.getHeaderViewsCount();;
        listView.setSelection(position);
        onListItemClick(listAdaptor.getItem(listAdaptor.selectedIndex));
        listAdaptor.notifyDataSetChanged();
    }

    protected abstract void onListItemClick(Object selectedObject);

    protected abstract int getListItemCount();

    protected abstract Object getListItemAtPosition(int i);

    protected abstract int getListItemViewId();

    protected abstract void fillListRowWithObjectInfo(View rowView, Object item);

    protected abstract String getHeaderText();

    protected abstract int getStartupSelectedIndex();

    public void resetSelectedItem(){
        listAdaptor.selectedIndex = getStartupSelectedIndex();
        listView.setItemChecked(getStartupSelectedIndex() + listView.getHeaderViewsCount(), true);
        if (listAdaptor.selectedIndex != -1)
            onListItemClick(listAdaptor.getItem(listAdaptor.selectedIndex));
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


            if (convertView == null  ) {

                convertView = mInflater.inflate(getListItemViewId(), parent, false);

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

        public Object getItem(int position) {
            return getListItemAtPosition(position);
        }

        public long getItemId(int position) {
            return position;
        }



    }
}
