package com.example.appnews.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.appnews.CheckInternetConnection;
import com.example.appnews.R;
import com.example.appnews.ReadRss;
import com.example.appnews.adapter.MyAdapter;
import com.example.appnews.emptyRecyclerView.EmptyRecyclerView;
import com.example.appnews.model.Channel;
import com.example.appnews.utils.Constants;

import java.util.ArrayList;

public class BaseFragment extends Fragment {
    private static final String LOG_TAG = BaseFragment.class.getName();
    /**
     * TextView that is displayed when the recycler view is empty
     */
    ArrayList<Channel> channels;
    private EmptyRecyclerView mRecyclerView;
    public RecyclerView recyclerView;
    private TextView mEmptyStateTextView;
    MyAdapter myAdapter;
    int keyFragment;
    CheckInternetConnection connection;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        AddEvent(rootView);
        connection= new CheckInternetConnection();
        channels = new ArrayList<>();
        if (!connection.isConnectedToNetwork(getContext()))
        {
            mEmptyStateTextView.setVisibility(View.VISIBLE);
            mEmptyStateTextView.setText(R.string.no_news);
        }
        else {
            keyFragment = getArguments().getInt("keyFragment");
            String address = getArguments().getString("url");
            switch (keyFragment) {
                case Constants.WORLD:
                    Log.e(LOG_TAG, LOG_TAG);
                    if (address.length() > 0) {
                        ReadRss readRss = new ReadRss(getActivity(), recyclerView, address);
                        readRss.execute();
                        break;
                    }
                case Constants.ENTERTAINMENT:
                    Log.e(LOG_TAG, LOG_TAG);
                    if (address.length() > 0) {
                        ReadRss readRss = new ReadRss(getActivity(), recyclerView, address);
                        readRss.execute();
                        break;
                    }
                case Constants.BALANCE:
                    Log.e(LOG_TAG, LOG_TAG);
                    if (address.length() > 0) {
                        ReadRss readRss = new ReadRss(getActivity(), recyclerView, address);
                        readRss.execute();
                        break;
                    }
                case Constants.BUSINESS:
                    Log.e(LOG_TAG, LOG_TAG);
                    if (address.length() > 0) {
                        ReadRss readRss = new ReadRss(getActivity(), recyclerView, address);
                        readRss.execute();
                        break;
                    }
                case Constants.EDUCATION:
                    Log.e(LOG_TAG, LOG_TAG);
                    if (address.length() > 0) {
                        ReadRss readRss = new ReadRss(getActivity(), recyclerView, address);
                        readRss.execute();
                        break;
                    }
                case Constants.SPORT:
                    Log.e(LOG_TAG, LOG_TAG);
                    if (address.length() > 0) {
                        ReadRss readRss = new ReadRss(getActivity(), recyclerView, address);
                        readRss.execute();
                        break;
                    }
                case Constants.TECHNOLOGY:
                    Log.e(LOG_TAG, LOG_TAG);
                    if (address.length() > 0) {
                        ReadRss readRss = new ReadRss(getActivity(), recyclerView, address);
                        readRss.execute();
                        break;
                    }
                case Constants.SOCIAL:
                    Log.e(LOG_TAG, LOG_TAG);
                    if (address.length() > 0) {
                        ReadRss readRss = new ReadRss(getActivity(), recyclerView, address);
                        readRss.execute();
                        break;
                    }
            }
            mEmptyStateTextView.setVisibility(View.GONE);
            LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
            mRecyclerView.setHasFixedSize(true);
            // Set the layoutManager
            mRecyclerView.setLayoutManager(layoutManager);
            myAdapter = new MyAdapter(channels, getActivity());
        }

        return rootView;
    }

    private void AddEvent(View v) {
        recyclerView = (RecyclerView) v.findViewById(R.id.recycler_view);
        mRecyclerView = v.findViewById(R.id.recycler_empty);
        // Find the empty view from the layout and set it on the new recycler view
        mEmptyStateTextView = (TextView) v.findViewById(R.id.empty_view);
        mRecyclerView.setEmptyView(mEmptyStateTextView);
    }

    private void checkEmpty() {
        Channel channel = new Channel();
        if (channel == null) {
            mEmptyStateTextView.setVisibility(View.VISIBLE);
            mEmptyStateTextView.setText(R.string.no_news);
        } else {
            mEmptyStateTextView.setVisibility(View.GONE);
            LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
            mRecyclerView.setHasFixedSize(true);
            // Set the layoutManager
            mRecyclerView.setLayoutManager(layoutManager);
            myAdapter = new MyAdapter(channels, getActivity());
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

}
