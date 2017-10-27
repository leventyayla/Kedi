package tez.levent.feyyaz.kedi.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import java.util.ArrayList;
import java.util.List;

import tez.levent.feyyaz.kedi.R;
import tez.levent.feyyaz.kedi.models.Yorum;

import static tez.levent.feyyaz.kedi.activities.SplashScreen.URL;

// Created by Levent on 14.03.2017.

public class EtkinlikYorumAdapter extends RecyclerView.Adapter<EtkinlikYorumAdapter.YorumHolder> {

    private List<Yorum> yorumlar =new ArrayList<>();
    private String USER_IMAGE_DIR = URL + "kullanici_pp/";
    private Context context;

    public EtkinlikYorumAdapter(Context context){
        this.context = context;
    }

    static class YorumHolder extends RecyclerView.ViewHolder {
        TextView isim,icerik,tarih;
        ImageView profile_image;
        RatingBar puan;

        YorumHolder(@NonNull View itemView) {
            super(itemView);
            isim = (TextView) itemView.findViewById(R.id.isim);
            icerik = (TextView) itemView.findViewById(R.id.icerik);
            tarih = (TextView) itemView.findViewById(R.id.tarih);
            puan = (RatingBar) itemView.findViewById(R.id.puanlama);
            profile_image = (ImageView) itemView.findViewById(R.id.profile_image);
        }

    }
    @NonNull
    @Override
    public YorumHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_yorum, parent, false);
        return new YorumHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final YorumHolder holder, int position) {
        final Yorum yorum = yorumlar.get(position);

        holder.icerik.setText(yorum.getIcerik());
        holder.isim.setText(yorum.getIsim() + " " + yorum.getSoyisim());
        holder.tarih.setText(yorum.getTarih());
        holder.puan.setRating(yorum.getPuan());

        if (yorum.getUrl().equals("null")){
            holder.profile_image.setImageResource(R.drawable.logo);
        }else{
            holder.profile_image.setImageDrawable(null);
            Glide.with(context)
                    .load(USER_IMAGE_DIR + yorum.getUrl())
                    .asBitmap()
                    .centerCrop()
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .into(new SimpleTarget<Bitmap>(100,100) {
                        @Override
                        public void onResourceReady(Bitmap resource, GlideAnimation glideAnimation) {
                            RoundedBitmapDrawable dr = RoundedBitmapDrawableFactory.create(context.getResources(), resource);
                            dr.setCircular(true);
                            Animation myFadeInAnimation = AnimationUtils.loadAnimation(context, R.anim.fade_in);
                            holder.profile_image.setImageDrawable(dr);
                            holder.profile_image.startAnimation(myFadeInAnimation);
                        }
                    });
        }
    }

    @NonNull
    public List<Yorum> getList(){
        return yorumlar;
    }

    public Yorum getYorum(int position){
        return yorumlar.get(position);
    }

    public void addItem(Yorum yorum) {
        yorumlar.add(yorum);
        notifyItemInserted(yorumlar.size()-1);
    }

    public void clearList() {
        int size = getItemCount();
        yorumlar.clear();
        notifyItemRangeRemoved(0,size);
    }

    @Override
    public int getItemCount() {
        return yorumlar.size();
    }
}