package com.example.appnews.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.example.appnews.R;
import com.example.appnews.adapter.FavoriteAdapter;
import com.example.appnews.model.Channel;
import com.example.appnews.sharePref.SharePreference;

import java.util.List;

public class FavoriteActivity extends AppCompatActivity {

    SharePreference shrdPrfence;
    List<Channel> favorites;
    FavoriteAdapter favoriteAdapter;
    ListView favList;
    Toolbar toolbar;
    LinearLayout linearLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.favorite);
        AddEvent();
        ActionBar();
        shrdPrfence = new SharePreference();
        favorites = shrdPrfence.getFavorites(this);

        if (favorites == null) {
            Snackbar snackbar = Snackbar
                    .make(linearLayout, "Không có item thích nào", Snackbar.LENGTH_LONG);
            snackbar.show();
        } else {
            if (favorites.size() == 0) {
                Snackbar snackbar = Snackbar
                        .make(linearLayout, "Không có item thích nào", Snackbar.LENGTH_LONG);
                snackbar.show();
            }

            if (favorites != null) {
                favoriteAdapter = new FavoriteAdapter(this, R.id.list_favorite, favorites);
                favList.setAdapter(favoriteAdapter);
            }
        }
    }

    private void ActionBar() {
        toolbar.setTitle("Danh sách các mục thích");
        toolbar.setNavigationIcon(R.drawable.ic_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void AddEvent() {
        linearLayout = (LinearLayout) findViewById(R.id.linearFavorite);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        favList = (ListView) findViewById(R.id.list_favorite);
    }
}
