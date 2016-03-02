package com.wb.nextgen.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.wb.nextgen.R;

import java.lang.reflect.Constructor;
import java.util.Stack;

/**
 * Created by gzcheng on 2/24/16.
 */
public class NextGenStackFragment extends Fragment {

    final Stack<Class<?>> fragmentClassStack = new Stack<Class<?>>();
    Fragment currentFragment;
    View fragmentFrame;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.next_gen_fragment_frame, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fragmentFrame = view.findViewById(R.id.next_gen_main_frame);
    }

    private void transitToFragment(Class<?> fragmentClass){

        try{
            FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
            ft.setCustomAnimations(R.anim.push_left_in, R.anim.push_left_out, R.anim.push_right_in, R.anim.push_right_out);

            Constructor<?> ctor = fragmentClass.getConstructor();
            currentFragment = (Fragment)ctor.newInstance(new Object[] {});

            //ft.add()

            ft.add(R.id.next_gen_main_frame, currentFragment);
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            ft.addToBackStack("tag");
            ft.commit();
        }catch(Exception ex){}
    }

    public void popTopFragment(){
        //fragmentClassStack.pop();
        //Class<?> previousFragmentClass = fragmentClassStack.peek();
        try{
            FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
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

    public void pushFragment(Class<?> fragmentClass){
        fragmentClassStack.add(fragmentClass);
        try{
            FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
            ft.setCustomAnimations(R.anim.push_left_in, R.anim.push_left_out, R.anim.push_right_in, R.anim.push_right_out);

            Constructor<?> ctor = fragmentClass.getConstructor();
            currentFragment = (Fragment)ctor.newInstance(new Object[] {});

            //ft.add()

            ft.add(R.id.next_gen_main_frame, currentFragment);
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            ft.addToBackStack("tag");
            ft.commit();
        }catch(Exception ex){}
    }
}
