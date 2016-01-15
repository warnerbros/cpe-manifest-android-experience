package com.wb.nextgen;

import android.app.ActionBar;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NavUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

/**
 * Created by gzcheng on 1/14/16.
 */
public class NextGenActionBarFragmentActivity  extends FragmentActivity{

    private void setupActionBar() {
        ActionBar ab = getActionBar();
        ab.setDisplayShowCustomEnabled(true);
        ab.setDisplayShowTitleEnabled(false);
        ab.setDisplayUseLogoEnabled(false);
        ab.setHomeButtonEnabled(false);

        //ab.setIcon(R.drawable.your_left_action_icon);
       /* LayoutInflater inflator = (LayoutInflater) this
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflator.inflate(R.layout.next_gen_action_bar, null);

        TextView titleTV = (TextView) v.findViewById(R.id.title);
        Typeface font = Typeface.createFromAsset(getAssets(),
                "fonts/your_custom_font.ttf");
        titleTV.setTypeface(font);*/

        ab.setCustomView(R.layout.next_gen_action_bar);
        TextView title = (TextView)ab.getCustomView().findViewById(R.id.next_gen_action_bar_title);
        title.setText("Grant haha");
        ab.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.onStart();
        setupActionBar();
    }
    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
