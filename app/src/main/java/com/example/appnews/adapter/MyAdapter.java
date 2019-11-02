package com.example.appnews.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.BounceInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.example.appnews.R;
import com.example.appnews.ReadRss;
import com.example.appnews.activity.WebViewActivity;
import com.example.appnews.model.Channel;
import com.example.appnews.sharePref.SharePreference;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import es.dmoral.toasty.Toasty;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
    private ArrayList<Channel> channels;
    private Context mCtx;
    SharePreference sharedPreference;

    public MyAdapter(ArrayList<Channel> channels, Context mCtx) {
        this.channels = channels;
        this.mCtx = mCtx;
        sharedPreference = new SharePreference();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mCtx).inflate(R.layout.cardview_item_news, viewGroup, false);
        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder viewHolder, final int position) {
        Channel current = channels.get(position);
        viewHolder.Title.setText(current.getTitle());
        viewHolder.Description.setText(current.getDescription());
        viewHolder.Date.setText(current.getDate());
        viewHolder.progressBar.setVisibility(View.VISIBLE);
        viewHolder.ImageView.setVisibility(View.GONE);
        if (ReadRss.map.get(channels.get(position).getImage()) == null) {
            new DownloadImage(channels.get(position).getImage()).execute(viewHolder);
        } else {
            viewHolder.progressBar.setVisibility(View.GONE);
            viewHolder.ImageView.setVisibility(View.VISIBLE);
            viewHolder.ImageView.setImageBitmap(ReadRss.map.get(channels.get(position).getImage()));
        }
        viewHolder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mCtx, ""+position, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(mCtx, WebViewActivity.class);
                intent.putExtra("link",channels.get(position).getLink());
                mCtx.startActivity(intent);
            }
        });
        //button favorite
        final ScaleAnimation scaleAnimation = new ScaleAnimation(0.7f, 1.0f, 0.7f, 1.0f, Animation.RELATIVE_TO_SELF, 0.7f, Animation.RELATIVE_TO_SELF, 0.7f);
        scaleAnimation.setDuration(500);
        BounceInterpolator bounceInterpolator = new BounceInterpolator();
        scaleAnimation.setInterpolator(bounceInterpolator);
        /*If a product exists in shared preferences then set heart_red drawable
         * and set a tag*/
        if (checkFavoriteItem(current)) {
            viewHolder.btnFavorite.setBackgroundResource(R.drawable.ic_favorite);
            viewHolder.btnFavorite.setTag("red");
        } else {
            viewHolder.btnFavorite.setBackgroundResource(R.drawable.ic_favorite_border);
            viewHolder.btnFavorite.setTag("grey");
        }
        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mCtx);
        viewHolder.btnFavorite.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                String tag = viewHolder.btnFavorite.getTag().toString();
                if(tag.equalsIgnoreCase("grey"))
                {
                    compoundButton.startAnimation(scaleAnimation);
                    viewHolder.btnFavorite.setBackgroundResource(R.drawable.ic_favorite);
                    sharedPreference.addFavorite(mCtx,channels.get(position));
                    viewHolder.btnFavorite.setTag("red");
                    Toasty.success(mCtx, "Đã thêm vào mục thích", Toast.LENGTH_SHORT,true).show();
                }
                else
                {
                    compoundButton.startAnimation(scaleAnimation);
                    viewHolder.btnFavorite.setBackgroundResource(R.drawable.ic_favorite_border);
                    sharedPreference.removeFavorite(mCtx,channels.get(position));
                    viewHolder.btnFavorite.setTag("grey");
                    Toasty.info(mCtx, "Bỏ thích", Toast.LENGTH_SHORT,true).show();
                }
            }
        });
    }

    /*Checks whether a particular news exists in SharedPreferences*/
    public boolean checkFavoriteItem(Channel checkNews) {
        boolean check = false;
        List<Channel> favorites = sharedPreference.getFavorites(mCtx);
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


    @Override
    public int getItemCount() {
        return channels.size();
    }
    public void setChannels(ArrayList<Channel> channels) {
        this.channels = channels;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ProgressBar progressBar;
        TextView Title, Description, Date;
        ImageView ImageView;
        CardView cardView;
        RelativeLayout relativeLayout;
        ToggleButton btnFavorite;

        public MyViewHolder(final View itemView) {
            super(itemView);
            btnFavorite = (ToggleButton)itemView.findViewById(R.id.btnFavorite);
            Title = (TextView) itemView.findViewById(R.id.title_card);
            Description = (TextView) itemView.findViewById(R.id.des_text_card);
            Date = (TextView) itemView.findViewById(R.id.date_card);
            ImageView = (ImageView) itemView.findViewById(R.id.image_card);
            cardView = (CardView) itemView.findViewById(R.id.card_view);
            progressBar = (ProgressBar) itemView.findViewById(R.id.progressbar_image);
            relativeLayout = (RelativeLayout) itemView.findViewById(R.id.itemRecycler);
        }
    }


    private class DownloadImage extends AsyncTask<MyViewHolder, Void, Bitmap> {
        String summaryImg;
        MyViewHolder viewHolder;

        public DownloadImage(String summaryImg) {
            this.summaryImg = summaryImg;
        }

        @Override
        protected Bitmap doInBackground(MyViewHolder... params) {
            try {
                viewHolder = params[0];
                URLConnection connection = new URL(summaryImg).openConnection();
                connection.connect();
                BufferedInputStream buffInput = new BufferedInputStream(connection.getInputStream());
                ByteArrayOutputStream dataStream = new ByteArrayOutputStream(1024);
                int current = 0;
                while ((current = buffInput.read()) != -1) {
                    dataStream.write((byte) current);
                }
                Bitmap bitmap = Bitmap.createBitmap(BitmapFactory.decodeByteArray(dataStream.toByteArray(),
                        0, dataStream.toByteArray().length));
                ReadRss.map.put(summaryImg, bitmap);
                return bitmap;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            if (result != null) {
                viewHolder.progressBar.setVisibility(View.GONE);
                viewHolder.ImageView.setVisibility(View.VISIBLE);
                viewHolder.ImageView.setImageBitmap(result);
            }
            super.onPostExecute(result);
        }
    }

    //Format Date
    private String formatDate(String dateStringUTC){
            // Parse the dateString into a Date object
            SimpleDateFormat simpleDateFormat =
                    new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z");
            Date dateObject = null;
            try {
                dateObject = simpleDateFormat.parse(dateStringUTC);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        // Initialize a SimpleDateFormat instance and configure it to provide a more readable
        // representation according to the given format, but still in UTC
        SimpleDateFormat df = new SimpleDateFormat("MMM d, yyyy  h:mm a",Locale.ENGLISH);
        String formattedDateUTC = df.format(dateObject);
        // Convert UTC into Local time
        df.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date date = null;
        try {
            date = df.parse(formattedDateUTC);
            df.setTimeZone(TimeZone.getDefault());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return df.format(date);
    }
    private static long getDateInMillis(String formattedDate) {
        SimpleDateFormat simpleDateFormat =
                new SimpleDateFormat("MMM d, yyyy  h:mm a");
        long dateInMillis;
        Date dateObject;
        try {
            dateObject = simpleDateFormat.parse(formattedDate);
            dateInMillis = dateObject.getTime();
            return dateInMillis;
        } catch (ParseException e) {
            Log.e("Problem parsing date", e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }
    private CharSequence getTimeDifference(String formattedDate) {
        long currentTime = System.currentTimeMillis();


        long publicationTime = getDateInMillis(formattedDate);
        return DateUtils.getRelativeTimeSpanString(publicationTime, currentTime,
                DateUtils.SECOND_IN_MILLIS);
    }
    /**
     * Clear all data (a list of {@link Channel} objects)
     */
    public void clearAll() {
        channels.clear();
        notifyDataSetChanged();
    }

    /**
     * Add  a list of {@link Channel}
     *
     * @param newlist is the list of news, which is the data source of the adapter
     */
    public void addAll(List<Channel> newlist) {
        channels.clear();
        channels.addAll(newlist);
        notifyDataSetChanged();
    }
}
