package tez.levent.feyyaz.kedi.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import tez.levent.feyyaz.kedi.R;
import tez.levent.feyyaz.kedi.models.Favori;

/**
 * Created by Levent on 12.12.2016.
 */

public class FavoriRecyclerViewAdapter extends RecyclerView.Adapter<FavoriRecyclerViewAdapter.FavoriHolder> {

    private List<Favori> favoriler =new ArrayList<>();
    private MyClickListener myClickListener;

    public FavoriRecyclerViewAdapter(){}

    class FavoriHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView ad,tur,tarih;

        FavoriHolder(@NonNull View itemView) {
            super(itemView);
            ad = (TextView) itemView.findViewById(R.id.ad);
            tur = (TextView) itemView.findViewById(R.id.tur);
            tarih = (TextView) itemView.findViewById(R.id.tarih);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            myClickListener.onItemClick(getAdapterPosition(), view);
        }
    }

    @Override
    public FavoriHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_card_favori, parent, false);
        return new FavoriHolder(view);
    }

    public interface MyClickListener {
        void onItemClick(int position, View v);
    }

    public void setOnItemClickListener(MyClickListener dinleyici) {
        myClickListener = dinleyici;
    }


    @Override
    public void onBindViewHolder(@NonNull FavoriHolder holder, int position) {
        Favori favori = favoriler.get(position);

        if (favori.getType() == Favori.DUYURU){
            holder.ad.setText(favori.getDuyuru_adi());
            holder.tur.setText("Duyuru");
            holder.tarih.setText(favori.getTarih());
        }else {
            holder.ad.setText(favori.getEtkinlik_adi());
            holder.tur.setText("Etkinlik");
            holder.tarih.setText(favori.getTarih());
        }
    }

    public List<Favori> getList(){
        return favoriler;
    }

    public Favori getFavori(int position){
        return favoriler.get(position);
    }

    public void addItem(Favori fav) {
        favoriler.add(fav);
        notifyItemInserted(favoriler.size()-1);
    }

    public void clearList() {
        int size = getItemCount();
        favoriler.clear();
        notifyItemRangeRemoved(0,size);
    }

    @Override
    public int getItemCount() {
        return favoriler.size();
    }
}
