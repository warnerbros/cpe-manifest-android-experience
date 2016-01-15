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
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;

import com.tonicartos.widget.stickygridheaders.StickyGridHeadersBaseAdapter;
import com.tonicartos.widget.stickygridheaders.StickyGridHeadersGridView;
import com.wb.nextgen.NextGenApplication;
import com.wb.nextgen.R;

import net.flixster.android.localization.Localizer;
import net.flixster.android.localization.constants.KEYS;

import java.util.ArrayList;
import java.util.List;


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

	private static class NextGenDrawerItem{
		enum DrawerItemMode{
			DRAWER_ITEM_LAUNCH_ACTIVITY, DRAWER_ITEM_LAUNCH_FRAGMENT, DRAER_ITEM_ON_OFF_SWITH;
		}

		public final String itemText;
		public final DrawerItemMode mode;
		public final NextGenDrawerInteractionInterface actionInterface;

		public NextGenDrawerItem(String itemText, DrawerItemMode mode, NextGenDrawerInteractionInterface actionInterface){
			this.itemText = itemText;
			this.mode = mode;
			this.actionInterface = actionInterface;
		}

	}

	private static interface NextGenDrawerInteractionInterface {
		public void handleAction(Object object);
		//public Object getCurrentOnOff();
	}


	private static final List<NextGenDrawerItem> NextGenDrawerItemList = new ArrayList<NextGenDrawerItem>();

	static{
		NextGenDrawerItemList.add(new NextGenDrawerItem("Audio", NextGenDrawerItem.DrawerItemMode.DRAWER_ITEM_LAUNCH_ACTIVITY, new NextGenDrawerInteractionInterface(){
			public void handleAction(Object object){

			}
		}));
		NextGenDrawerItemList.add(new NextGenDrawerItem("Subtitle", NextGenDrawerItem.DrawerItemMode.DRAER_ITEM_ON_OFF_SWITH, new NextGenDrawerInteractionInterface(){
			public void handleAction(Object object){
				if (object instanceof  Boolean){
					NextGenApplication.setSubtitleOn(((Boolean)object).booleanValue());
				}
			}
		}));;
		NextGenDrawerItemList.add(new NextGenDrawerItem("User Profile", NextGenDrawerItem.DrawerItemMode.DRAWER_ITEM_LAUNCH_ACTIVITY, new NextGenDrawerInteractionInterface(){
			public void handleAction(Object object){

			}
		}));
	}

	private class DrawerListAdapter extends BaseAdapter implements StickyGridHeadersBaseAdapter, OnItemClickListener{

		@Override
		public int getCount() {
			return NextGenDrawerItemList.size();
		}

		@Override
		public long getItemId(int position) {
			return position;
		}


		@Override
		public View getView(final int index, View view, ViewGroup parent) {
			View target = null;
			final NextGenDrawerItem thisItem = NextGenDrawerItemList.get(index);
			if (view != null)
				target = view;
			else
				target = getActivity().getLayoutInflater().inflate(R.layout.navigation_drawer_row,  mainList, false);

			TextView textView = (TextView)target.findViewById(R.id.drawer_row_text);

			textView.setText(thisItem.itemText);

			Switch onOffSwitch = (Switch) target.findViewById(R.id.drawer_row_switch);
			ImageView rowIcon = (ImageView) target.findViewById(R.id.drawer_row_image);

			switch(thisItem.mode){
				case DRAWER_ITEM_LAUNCH_ACTIVITY:
					onOffSwitch.setVisibility(View.GONE);
					rowIcon.setVisibility(View.VISIBLE);
					break;
				case DRAER_ITEM_ON_OFF_SWITH:
					onOffSwitch.setVisibility(View.VISIBLE);
					rowIcon.setVisibility(View.GONE);
					onOffSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
						@Override
						public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
							thisItem.actionInterface.handleAction(new Boolean(isChecked));
						}
					});
					break;
				default:
					break;
			}
			
			//boolean isSelected = drawerListener.getCurrentSelectedDrawerKey() == itemKey;
			//textView.setSelected(isSelected);
			
			
			return target;
		}

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int index, long arg3) {
			NextGenDrawerItem thisItem = NextGenDrawerItemList.get(index);
			switch(thisItem.mode){
				case DRAWER_ITEM_LAUNCH_ACTIVITY:
					break;
				case DRAER_ITEM_ON_OFF_SWITH:
					//Switch onOffSwitch = (Switch) arg1.findViewById(R.id.drawer_row_switch);
					//thisItem.actionInterface.handleAction(onOffSwitch.on);
					break;
				default:
					break;
			}
		}


		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return NextGenDrawerItemList.get(position);
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
