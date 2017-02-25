package com.wb.nextgenlibrary.testassets;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.wb.nextgenlibrary.R;
import com.wb.nextgenlibrary.activity.NGEHideStatusBarActivity;
import com.wb.nextgenlibrary.activity.WebViewActivity;
import com.wb.nextgenlibrary.util.utils.F;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gzcheng on 9/27/16.
 */

public class TestItemsActivity extends NGEHideStatusBarActivity {
    RecyclerView testItemsRecyclerView;
    final static List<TestItem> testItemList = new ArrayList<>();
    static{
        testItemList.add(new TestItem("360 test", "http://s3.amazonaws.com/r60-cpe/hosting/radius60/manifest/microhtml/Bravo-Video360/index.html#/home-view"));
        testItemList.add(new TestItem("Lego Selfie Builder", "http://selfie.legobatman.com/"));
        testItemList.add(new TestItem("Storks: Delivery Dash", "http://s3.amazonaws.com/r60-cpe/hosting/radius60/manifest/microhtml/Storks-Delivery-Dash/index.html"));
        testItemList.add(new TestItem("Storks: Tulip Builder", "http://s3.amazonaws.com/r60-cpe/hosting/radius60/manifest/microhtml/Storks-Tulip-Builder/index.html"));
    }
    @Override
    public void onCreate(Bundle savedState) {
        super.onCreate(savedState);
        setContentView(R.layout.test_features_view);
        testItemsRecyclerView = (RecyclerView) findViewById(R.id.next_gen_test_item_rv);
        testItemsRecyclerView.setAdapter(new TestItemsRecyclerAdapter());
        testItemsRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
    }

    static class TestItem{
        public final String name;
        public final String url;
        public TestItem(String name, String url){
            this.name = name;
            this.url = url;
        }
    }


    public class TestItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView testItemTextView;
        TestItem testItem;

        TestItemViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            testItemTextView = (TextView) itemView.findViewById(R.id.test_item_text);
        }

        public void setItem(TestItem testItem){
            this.testItem = testItem;
            testItemTextView.setText(testItem.name);
        }

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(TestItemsActivity.this, WebViewActivity.class);
            intent.putExtra(F.URL, testItem.url);

            intent.setAction(Intent.ACTION_VIEW);
            startActivity(intent);

        }
    }

    class TestItemsRecyclerAdapter extends RecyclerView.Adapter<TestItemViewHolder> {
        @Override
        public TestItemViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {

            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.next_gen_test_item_row, viewGroup, false);
            TestItemViewHolder pvh = new TestItemViewHolder(v);
            return pvh;
        }

        public void onBindViewHolder(TestItemViewHolder holder, int position) {
            TestItem item = testItemList.get(position);
            holder.setItem(item);

        }

        @Override
        public void onAttachedToRecyclerView(RecyclerView recyclerView) {
            super.onAttachedToRecyclerView(recyclerView);
        }

        public int getItemCount() {
           return testItemList.size();
        }
    }
}
