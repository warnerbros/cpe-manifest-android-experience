package com.wb.nextgen.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.tonicartos.widget.stickygridheaders.StickyGridHeadersBaseAdapter;
import com.tonicartos.widget.stickygridheaders.StickyGridHeadersGridView;
import com.wb.nextgen.R;

import net.flixster.android.localization.Localizer;
import net.flixster.android.localization.constants.KEYS;


public class NextGenNavigationDrawerFragment extends Fragment {

	

	
	private StickyGridHeadersGridView mainList;
	private final DrawerListAdapter adapter = new DrawerListAdapter();

	


	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.navigation_drawer, container, false);
    }
	
	@Override 
	public void onViewCreated(View view, Bundle savedInstanceState){
		super.onViewCreated(view, savedInstanceState);
		mainList = (StickyGridHeadersGridView)view.findViewById(R.id.drawer_main_list);
		mainList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		resetCurrentStateDrawerList();
		mainList.setAdapter(adapter);
		mainList.setOnItemClickListener(adapter);
		mainList.setItemChecked(0, true);

		getActivity().setTitle("<");
		resetDrawer();
		
		
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, final Intent data) {

	}
	

	public void resetDrawer(){
		resetCurrentStateDrawerList();
		adapter.notifyDataSetChanged();
		//basePanel.setVisibility(FlixsterApplication.isLoggedin() ? View.GONE : View.VISIBLE);
	}
	
	private void resetCurrentStateDrawerList(){

	}
	
	private class DrawerListAdapter extends BaseAdapter implements StickyGridHeadersBaseAdapter, OnItemClickListener{
		
		@Override
		public int getCount() {
			return 10;
		}
		
		@Override
		public long getItemId(int position) {
			return position;
		}


		@Override
		public View getView(final int index, View view, ViewGroup parent) {
			View target = null;
			
			if (view != null)
				target = view;
			else
				target = getActivity().getLayoutInflater().inflate(R.layout.navigation_drawer_row,  mainList, false);

			TextView textView = (TextView)target.findViewById(R.id.drawer_row_text);
			
			textView.setText(Integer.toString(index));
			
			//boolean isSelected = drawerListener.getCurrentSelectedDrawerKey() == itemKey;
			//textView.setSelected(isSelected);
			
			
			return target;
		}

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int index, long arg3) {
			//int itemKey = currentDrawerList[index];
			/*if (drawerListener != null){
				if (drawerListener.onSelectItem(itemKey)){
					notifyDataSetChanged();
				}
			}*/
		}


		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public int getCountForHeader(int header) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public int getNumHeaders() {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getHeaderView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			return null;
		}
	}


	public void onDestroyView(){
		super.onDestroyView();
		getActivity().setTitle(">");
	}
}
