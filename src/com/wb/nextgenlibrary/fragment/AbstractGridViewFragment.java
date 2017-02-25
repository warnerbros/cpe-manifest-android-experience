package com.wb.nextgenlibrary.fragment;

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
import android.widget.GridView;
import android.widget.TextView;

import com.wb.nextgenlibrary.NextGenExperience;
import com.wb.nextgenlibrary.R;


/**
 * Created by gzcheng on 1/12/16.
 */
public abstract class AbstractGridViewFragment extends Fragment implements AdapterView.OnItemClickListener{

    protected GridView gridView;
    protected NextGenGridViewAdapter listAdaptor;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.next_gen_grid_view, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        float density = NextGenExperience.getScreenDensity(getActivity());
        int spacing = (int)(10 *density);
        gridView = (GridView)view.findViewById(R.id.next_gen_grid);
        if (gridView != null){
            gridView.setNumColumns(getNumberOfColumns());
            gridView.setHorizontalSpacing(spacing);
            gridView.setVerticalSpacing(spacing);
            gridView.setPadding(spacing, 0, spacing, spacing);
            //gridView.setHeadersIgnorePadding(true);
            listAdaptor = new NextGenGridViewAdapter();
            gridView.setAdapter(listAdaptor);
            gridView.setOnItemClickListener(this);
        }

    }

    @Override
    public void onDestroy(){
        listAdaptor = null;
        if (gridView != null)
            gridView.setAdapter(null);
        super.onDestroy();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
        listAdaptor.selectedIndex = position;
        gridView.setItemChecked(position, true);
        onListItemClick(v, position, id);
    }

    protected abstract void onListItemClick(View v, int position, long id);

    protected abstract int getNumberOfColumns();

    protected abstract int getListItemCount();

    protected abstract Object getListItemAtPosition(int i);

    protected abstract int getListItemViewId();


    protected int getListItemViewId(int position){
        return getListItemViewId();
    }

    protected abstract void fillListRowWithObjectInfo(int position, View rowView, Object item, boolean isSelected);

    protected abstract String getHeaderText();

    protected abstract int getHeaderChildenCount(int header);

    protected abstract int getHeaderCount();

    protected abstract int getStartupSelectedIndex();

    protected void setupNewContentView(View view){

    }

    protected void invalidateOldContentView(View view){}

    public void resetSelectedItem(){
        listAdaptor.selectedIndex = getStartupSelectedIndex();
    }

    public class NextGenGridViewAdapter extends BaseAdapter{

        //protected OutOfMovieActivity activity;

        protected int selectedIndex = getStartupSelectedIndex();

        protected LayoutInflater mInflater;

        private TextView headerTextView;

        NextGenGridViewAdapter() {

            mInflater = LayoutInflater.from(getActivity());
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            if (position >= getCount() || position < 0){
                return null;
            }
            int id = getListItemViewId(position);



            if (convertView == null || (convertView.getTag() instanceof Integer && !((Integer)convertView.getTag()).equals(id)) ) {

                if (convertView != null){
                    invalidateOldContentView(convertView);
                }

                LayoutInflater inflater = getActivity().getLayoutInflater();
                convertView = inflater.inflate(id, parent, false);
                convertView.setTag(new Integer(id));
                setupNewContentView(convertView);

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

    }
}
