package com.example.appnews.adapter;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.appnews.R;
import com.example.appnews.fragment.BaseFragment;
import com.example.appnews.utils.Constants;

public class CategoryFragmentPagerAdapter extends FragmentPagerAdapter {

    private Context mContext;
    private Fragment selectedFragment;

    public CategoryFragmentPagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        mContext = context;
    }
    @Override
    public Fragment getItem(int position) {
        selectedFragment=null;
        int keyFragment = 0;
        Bundle args = new Bundle();
        switch (position) {
            case Constants.WORLD:
                keyFragment = Constants.WORLD;
                args.putInt("keyFragment",keyFragment);
                args.putString("url",mContext.getResources().getString(R.string.common_url_word));
                selectedFragment = new BaseFragment();
                selectedFragment.setArguments(args);
                return selectedFragment;
            case Constants.ENTERTAINMENT:
                keyFragment = Constants.ENTERTAINMENT;
                args.putInt("keyFragment",keyFragment);
                args.putString("url",mContext.getResources().getString(R.string.common_url_entertainment));
                selectedFragment = new BaseFragment();
                selectedFragment.setArguments(args);
                return selectedFragment;
            case Constants.BALANCE:
                keyFragment = Constants.BALANCE;
                args.putString("url",mContext.getResources().getString(R.string.common_url_balance));
                args.putInt("keyFragment",keyFragment);
                selectedFragment = new BaseFragment();
                selectedFragment.setArguments(args);
                return selectedFragment;
            case Constants.BUSINESS:
                keyFragment = Constants.BUSINESS;
                args.putString("url",mContext.getResources().getString(R.string.common_url_business));
                args.putInt("keyFragment",keyFragment);
                selectedFragment = new BaseFragment();
                selectedFragment.setArguments(args);
                return selectedFragment;
            case Constants.EDUCATION:
                keyFragment = Constants.EDUCATION;
                args.putString("url",mContext.getResources().getString(R.string.common_url_education));
                args.putInt("keyFragment",keyFragment);
                selectedFragment = new BaseFragment();
                selectedFragment.setArguments(args);
                return selectedFragment;
            case Constants.SPORT:
                keyFragment = Constants.SPORT;
                args.putString("url",mContext.getResources().getString(R.string.common_url_sport));
                args.putInt("keyFragment",keyFragment);
                selectedFragment = new BaseFragment();
                selectedFragment.setArguments(args);
                return selectedFragment;
            case Constants.TECHNOLOGY:
                keyFragment = Constants.TECHNOLOGY;
                args.putString("url",mContext.getResources().getString(R.string.common_url_technology));
                args.putInt("keyFragment",keyFragment);
                selectedFragment = new BaseFragment();
                selectedFragment.setArguments(args);
                return selectedFragment;
            case Constants.SOCIAL:
                keyFragment = Constants.SOCIAL;
                args.putString("url",mContext.getResources().getString(R.string.common_url_social));
                args.putInt("keyFragment",keyFragment);
                selectedFragment = new BaseFragment();
                selectedFragment.setArguments(args);
                return selectedFragment;
            default:
                return null;
        }

    }

    /**
     *Return the total number of pages.
     */
    @Override
    public int getCount() {
        return 8;
    }


    /**
     * Return page title of the tap
     */
    @Override
    public CharSequence getPageTitle(int position) {
        int titleResId;
        switch (position) {
            case Constants.WORLD:
                titleResId = R.string.ic_title_WORLD;
                break;
            case Constants.ENTERTAINMENT:
                titleResId = R.string.ic_title_ENTERTAINMENT;
                break;
            case Constants.BALANCE:
                titleResId = R.string.ic_title_BALANCE;
                break;
            case Constants.BUSINESS:
                titleResId = R.string.ic_title_BUSINESS;
                break;
            case Constants.EDUCATION:
                titleResId = R.string.ic_title_EDUCATION;
                break;
            case Constants.SPORT:
                titleResId = R.string.ic_title_SPORT;
                break;
            case Constants.TECHNOLOGY:
                titleResId = R.string.ic_title_TECHNOLOGY;
                break;
            case Constants.SOCIAL:
                titleResId = R.string.ic_title_SOCIAL;
                break;
            default:
                titleResId = R.string.ic_title_SOCIAL;
                break;
        }
        return mContext.getString(titleResId);
    }
}