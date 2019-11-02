package com.example.appnews.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.BounceInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.example.appnews.R;
import com.example.appnews.activity.WebViewActivity;
import com.example.appnews.model.Channel;
import com.example.appnews.sharePref.SharePreference;
import com.squareup.picasso.Picasso;

import java.util.List;

import es.dmoral.toasty.Toasty;

public class FavoriteAdapter extends ArrayAdapter<Channel> {
    private List<Channel> listData;
    int res;
    private Context context;
    SharePreference sharedPreference;

    public FavoriteAdapter(Context context, int resource,List<Channel> listData) {
        super(context, resource,listData);
        this.context = context;
        this.listData = listData;
        this.res = resource;
        sharedPreference = new SharePreference();
    }


    @Override
    public int getCount() {
        return listData.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
        convertView = LayoutInflater.from(context).inflate(R.layout.cardview_item_news,parent, false);
        holder = new ViewHolder();
        holder.txtTitle = (TextView) convertView.findViewById(R.id.title_card);
        holder.txtDes = (TextView) convertView.findViewById(R.id.des_text_card);
        holder.btnFav = (ToggleButton)convertView.findViewById(R.id.btnFavorite);
        holder.img = (ImageView)convertView.findViewById(R.id.image_card);
        holder.progressBar = (ProgressBar)convertView.findViewById(R.id.progressbar_image);
        holder.relativeLayout = (RelativeLayout)convertView.findViewById(R.id.itemRecycler);
        holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, WebViewActivity.class);
                intent.putExtra("link",listData.get(position).getLink());
                context.startActivity(intent);
            }
        });
        convertView.setTag(holder);
        } else {
        holder = (ViewHolder) convertView.getTag();
        }
        final Channel channel = this.listData.get(position);
        holder.txtTitle.setText(channel.getTitle());
        holder.txtDes.setText(channel.getDescription());
        //button favorite
        final ScaleAnimation scaleAnimation = new ScaleAnimation(0.7f, 1.0f, 0.7f, 1.0f, Animation.RELATIVE_TO_SELF, 0.7f, Animation.RELATIVE_TO_SELF, 0.7f);
        scaleAnimation.setDuration(500);
        BounceInterpolator bounceInterpolator = new BounceInterpolator();
        scaleAnimation.setInterpolator(bounceInterpolator);
        /*If a product exists in shared preferences then set heart_red drawable
         * and set a tag*/
        if (checkFavoriteItem(channel)) {
            holder.btnFav.setBackgroundResource(R.drawable.ic_favorite);
            holder.btnFav.setTag("red");
        } else {
            holder.btnFav.setBackgroundResource(R.drawable.ic_favorite_border);
            holder.btnFav.setTag("grey");
        }
        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        holder.btnFav.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                String tag = holder.btnFav.getTag().toString();
                if(tag.equalsIgnoreCase("grey"))
                {
                    compoundButton.startAnimation(scaleAnimation);
                    holder.btnFav.setBackgroundResource(R.drawable.ic_favorite);
                    sharedPreference.addFavorite(context,listData.get(position));
                    holder.btnFav.setTag("red");
                    Toasty.success(context, "Đã thêm vào mục thích", Toast.LENGTH_SHORT,true).show();
                }
                else
                {
                    compoundButton.startAnimation(scaleAnimation);
                    holder.btnFav.setBackgroundResource(R.drawable.ic_favorite_border);
                    sharedPreference.removeFavorite(context,listData.get(position));
                    holder.btnFav.setTag("grey");
                    Toasty.info(context, "Bỏ thích", Toast.LENGTH_SHORT,true).show();
                }
            }
        });
        try {
            if (channel.getImage() == null)
            {
                return null;
            }
            else
            Picasso.with(context).load(channel.getImage()).into(holder.img);
            holder.progressBar.setVisibility(View.GONE);
        }
        catch (Exception e)
        {e.printStackTrace();}
        return convertView;
    }

        static class ViewHolder {
        TextView txtTitle;
        TextView txtDes;
        ToggleButton btnFav;
        ImageView img;
        ProgressBar progressBar;
        RelativeLayout relativeLayout;
    }
    /*Checks whether a particular news exists in SharedPreferences*/
    public boolean checkFavoriteItem(Channel checkNews) {
        boolean check = false;
        List<Channel> favorites = sharedPreference.getFavorites(context);
        if (favorites != null) {
            for (Channel channel : favorites) {
                if (channel.equals(checkNews)) {
                    check = true;
                    break;
                }
            }
        }
        return check;
    }
}
