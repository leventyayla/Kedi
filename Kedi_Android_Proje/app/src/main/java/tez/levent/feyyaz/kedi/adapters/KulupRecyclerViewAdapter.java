package tez.levent.feyyaz.kedi.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.signature.StringSignature;

import java.util.ArrayList;
import java.util.List;

import tez.levent.feyyaz.kedi.R;
import tez.levent.feyyaz.kedi.activities.SplashScreen;
import tez.levent.feyyaz.kedi.models.Kulup;

/**
 * Created by Levent on 6.12.2016.
 */

public class KulupRecyclerViewAdapter extends RecyclerView.Adapter<KulupRecyclerViewAdapter.KulupHolder> {
    private List<Kulup> kulupler;
    private MyClickListener myClickListener;
    private String KULUP_IMAGE_DIR = SplashScreen.URL + "kulup_logo/";

    public KulupRecyclerViewAdapter(){
        kulupler = new ArrayList<>();
    }

    class KulupHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView isim;
        ImageView logo,is_follow;

        KulupHolder(@NonNull View itemView) {
            super(itemView);
            isim = (TextView) itemView.findViewById(R.id.isim);
            logo = (ImageView) itemView.findViewById(R.id.logo);
            is_follow=(ImageView) itemView.findViewById(R.id.is_follow);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            myClickListener.onItemClick(getAdapterPosition(), view);
        }
    }
    @NonNull
    @Override
    public KulupHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_card_kulup, parent, false);
        return new KulupHolder(view);
    }

    public interface MyClickListener {
        void onItemClick(int position, View v);
    }

    public void setOnItemClickListener(MyClickListener dinleyici) {
        myClickListener = dinleyici;
    }


    @Override
    public void onBindViewHolder(@NonNull final KulupHolder holder, int position) {
        final Kulup kulup = kulupler.get(position);

        holder.isim.setText(kulup.getIsim());

        if (kulupler.get(position).getFollowing()){
            holder.is_follow.setVisibility(View.VISIBLE);
        }else {
            holder.is_follow.setVisibility(View.GONE);
        }

        Glide.with(holder.logo.getContext())
                .load(KULUP_IMAGE_DIR + kulup.getUrl())
                .signature(new StringSignature(kulup.getSon_duzenleme()))
                .crossFade()
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.logo);

    }

    public Kulup getKulup(int position){
        return kulupler.get(position);
    }

    public void addItem(Kulup kulup) {
        kulupler.add(kulup);
        notifyItemInserted(kulupler.size()-1);
    }

    public void clearList() {
        int size = getItemCount();
        kulupler.clear();
        notifyItemRangeRemoved(0,size);
    }

    @Override
    public int getItemCount() {
        return kulupler.size();
    }

}


