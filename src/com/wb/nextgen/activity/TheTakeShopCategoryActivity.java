package com.wb.nextgen.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.wb.nextgen.NextGenApplication;
import com.wb.nextgen.R;
import com.wb.nextgen.fragment.NextGenActorListFragment;
import com.wb.nextgen.fragment.NextGenExtraMainTableFragment;
import com.wb.nextgen.util.utils.NextGenFragmentTransactionEngine;

/**
 * Created by gzcheng on 4/8/16.
 */
public class TheTakeShopCategoryActivity extends FragmentActivity {

    ListView categoryListView;
    GridView itemsGridView;


    @Override
    public void onCreate(Bundle savedState) {
        super.onCreate(savedState);

        setContentView(R.layout.the_take_shop_category_view);
        categoryListView = (ListView)findViewById(R.id.the_take_category_list);
        itemsGridView = (GridView)findViewById(R.id.the_take_items_grid);
    }
}
