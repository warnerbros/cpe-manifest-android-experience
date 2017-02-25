package com.wb.nextgenlibrary.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.TextView;

import com.wb.nextgenlibrary.R;

import java.lang.reflect.Constructor;

/**
 * Created by gzcheng on 1/14/16.
 */
public class NGEActionBarFragmentActivity extends AppCompatActivity{
    //final Stack<Class<?>> fragmentClassStack = new Stack<Class<?>>();
    Fragment currentFragment;

    private void setupActionBar() {
        ActionBar ab = getSupportActionBar();
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

    private void transitToFragment(Class<?> fragmentClass, int viewId){

        try{
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.setCustomAnimations(R.anim.push_left_in, R.anim.push_left_out, R.anim.push_right_in, R.anim.push_right_out);

            Constructor<?> ctor = fragmentClass.getConstructor();
            currentFragment = (Fragment)ctor.newInstance(new Object[] {});

            //ft.add()

            ft.add(viewId/*R.id.next_gen_main_frame*/, currentFragment);
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            ft.addToBackStack("tag");
            ft.commit();
        }catch(Exception ex){}
    }

    public void popTopFragment(int viewId){
        //fragmentClassStack.pop();
        //Class<?> previousFragmentClass = fragmentClassStack.peek();
        try{
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.setCustomAnimations(R.anim.push_left_in, R.anim.push_left_out, R.anim.push_right_in, R.anim.push_right_out);

            //Constructor<?> ctor = fragmentClass.getConstructor();
            //Fragment fragment = (Fragment)ctor.newInstance(new Object[] {});

            //ft.add()

            ft.remove(currentFragment);
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            //ft.addToBackStack("tag");
            ft.commit();
        }catch(Exception ex){}
    }

    public void pushFragment(Class<?> fragmentClass, int viewId){
        //fragmentClassStack.add(fragmentClass);
        try{
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.setCustomAnimations(R.anim.push_left_in, R.anim.push_left_out, R.anim.push_right_in, R.anim.push_right_out);

            Constructor<?> ctor = fragmentClass.getConstructor();
            currentFragment = (Fragment)ctor.newInstance(new Object[] {});

            //ft.add()

            ft.add(viewId, currentFragment);
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            ft.addToBackStack("tag");
            ft.commit();
        }catch(Exception ex){}
    }
}
