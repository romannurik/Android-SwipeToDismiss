/*
 * Copyright 2013 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.swipedismiss;

import android.app.ListActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends ListActivity {
    ArrayAdapter<String> mAdapter;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set up ListView example
        String[] items = new String[20];
        for (int i = 0; i < items.length; i++) {
            items[i] = "Item " + (i + 1);
        }

        mAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1,
                android.R.id.text1,
                new ArrayList<String>(Arrays.asList(items)));
        setListAdapter(mAdapter);

        ListView listView = getListView();
        // Create a ListView-specific touch listener. ListViews are given special treatment because
        // by default they handle touches for their list items... i.e. they're in charge of drawing
        // the pressed state (the list selector), handling list item clicks, etc.
        SwipeDismissListViewTouchListener touchListener =
                new SwipeDismissListViewTouchListener(
                        listView,
                        new SwipeDismissListViewTouchListener.DismissCallbacks() {
                            @Override
                            public boolean canDismiss(int position) {
                                return true;
                            }

                            @Override
                            public void onDismiss(ListView listView, int[] reverseSortedPositions) {
                                for (int position : reverseSortedPositions) {
                                    mAdapter.remove(mAdapter.getItem(position));
                                }
                                mAdapter.notifyDataSetChanged();
                            }
                        });
        listView.setOnTouchListener(touchListener);
        // Setting this scroll listener is required to ensure that during ListView scrolling,
        // we don't look for swipes.
        listView.setOnScrollListener(touchListener.makeScrollListener());

        // Set up normal ViewGroup example
        final ViewGroup dismissableContainer = (ViewGroup) findViewById(R.id.dismissable_container);
        for (int i = 0; i < items.length; i++) {
            final Button dismissableButton = new Button(this);
            dismissableButton.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            dismissableButton.setText("Button " + (i + 1));
            dismissableButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(MainActivity.this,
                            "Clicked " + ((Button) view).getText(),
                            Toast.LENGTH_SHORT).show();
                }
            });
            // Create a generic swipe-to-dismiss touch listener.
            dismissableButton.setOnTouchListener(new SwipeDismissTouchListener(
                    dismissableButton,
                    null,
                    new SwipeDismissTouchListener.DismissCallbacks() {
                        @Override
                        public boolean canDismiss(Object token) {
                            return true;
                        }

                        @Override
                        public void onDismiss(View view, Object token) {
                            dismissableContainer.removeView(dismissableButton);
                        }
                    }));
            dismissableContainer.addView(dismissableButton);
        }
    }

    @Override
    protected void onListItemClick(ListView listView, View view, int position, long id) {
        Toast.makeText(this,
                "Clicked " + getListAdapter().getItem(position).toString(),
                Toast.LENGTH_SHORT).show();
    }
}
