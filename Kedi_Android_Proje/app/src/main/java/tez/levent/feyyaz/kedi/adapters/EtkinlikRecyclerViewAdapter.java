package tez.levent.feyyaz.kedi.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.signature.StringSignature;

import java.util.ArrayList;
import java.util.List;

import tez.levent.feyyaz.kedi.R;
import tez.levent.feyyaz.kedi.activities.SplashScreen;
import tez.levent.feyyaz.kedi.models.Etkinlik;

/**
 * Created by Levent on 11.12.2016.
 */

public class EtkinlikRecyclerViewAdapter extends RecyclerView.Adapter<EtkinlikRecyclerViewAdapter.EtkinlikHolder> {

    private List<Etkinlik> etkinlikler;
    private MyClickListener myClickListener;
    private String ETKINLIK_IMAGE_DIR = SplashScreen.URL + "etkinlik_foto/";

    public EtkinlikRecyclerViewAdapter(){
        etkinlikler=new ArrayList<>();
    }

    class EtkinlikHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView baslik,aciklama,sure,tarih;
        ImageView gorsel;
        ProgressBar pb;

        EtkinlikHolder(@NonNull View itemView) {
            super(itemView);
            baslik = (TextView) itemView.findViewById(R.id.baslik);
            aciklama = (TextView) itemView.findViewById(R.id.aciklama);
            sure = (TextView) itemView.findViewById(R.id.sure);
            tarih = (TextView) itemView.findViewById(R.id.tarih);
            gorsel = (ImageView) itemView.findViewById(R.id.gorsel);
            pb=(ProgressBar)itemView.findViewById(R.id.progress);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            myClickListener.onItemClick(getAdapterPosition(), view);
        }
    }

    @Override
    public EtkinlikHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_card_etkinlik, parent, false);
        return new EtkinlikHolder(view);
    }

    public interface MyClickListener {
        void onItemClick(int position, View v);
    }

    public void setOnItemClickListener(MyClickListener dinleyici) {
        myClickListener = dinleyici;
    }


    @Override
    public void onBindViewHolder(@NonNull final EtkinlikHolder holder, int position) {
        final Etkinlik etkinlik = etkinlikler.get(position);

        Glide.with(holder.gorsel.getContext())
                .load(ETKINLIK_IMAGE_DIR + etkinlik.getURL())
                .signature(new StringSignature(etkinlik.getSon_duzenleme()))
                .listener(new RequestListener<String, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                        holder.pb.setVisibility(View.GONE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        holder.pb.setVisibility(View.GONE);
                        return false;
                    }
                })
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .skipMemoryCache(true)
                .crossFade()
                .into(holder.gorsel);

        holder.baslik.setText(etkinlik.getBaslik());

        String aciklama = etkinlik.getAciklama();
        if(aciklama.length() > 100){
            aciklama=aciklama.substring(0,100) + "...";
        }
        holder.aciklama.setText(aciklama);

        String sure = String.valueOf(etkinlik.getSure()) + " dk";
        holder.sure.setText(sure);
        holder.tarih.setText(etkinlik.getTarih());
    }

    @NonNull
    public List<Etkinlik> getList(){
        return etkinlikler;
    }

    public Etkinlik getEtkinlik(int position){
        return etkinlikler.get(position);
    }

    public void addItem(Etkinlik etkinlik) {
        etkinlikler.add(etkinlik);
        notifyItemInserted(etkinlikler.size()-1);
    }

    public void clearList() {
        int size = getItemCount();
        etkinlikler.clear();
        notifyItemRangeRemoved(0,size);
    }

    @Override
    public int getItemCount() {
        return etkinlikler.size();
    }
}