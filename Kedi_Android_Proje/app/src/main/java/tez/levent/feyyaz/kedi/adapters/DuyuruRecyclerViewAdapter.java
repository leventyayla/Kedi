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
import tez.levent.feyyaz.kedi.models.Duyuru;

/**
 * Created by Levent on 12.12.2016.
 */

public class DuyuruRecyclerViewAdapter extends RecyclerView.Adapter<DuyuruRecyclerViewAdapter.DuyuruHolder> {
    @NonNull
    private List<Duyuru> duyurular =new ArrayList<>();
    private MyClickListener myClickListener;

    public DuyuruRecyclerViewAdapter(){}

    class DuyuruHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView isim,aciklama;

        DuyuruHolder(@NonNull View itemView) {
            super(itemView);
            isim = (TextView) itemView.findViewById(R.id.isim);
            aciklama = (TextView) itemView.findViewById(R.id.aciklama);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            myClickListener.onItemClick(getAdapterPosition(), view);
        }
    }
    @NonNull
    @Override
    public DuyuruHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_card_duyuru, parent, false);
        return new DuyuruHolder(view);
    }

    public interface MyClickListener {
        void onItemClick(int position, View v);
    }

    public void setOnItemClickListener(MyClickListener dinleyici) {
        myClickListener = dinleyici;
    }


    @Override
    public void onBindViewHolder(@NonNull DuyuruHolder holder, int position) {
        Duyuru duyuru = duyurular.get(position);

        holder.isim.setText(duyuru.getBaslik());
        String aciklama = duyuru.getAciklama();
        if(aciklama.length() > 100){
            aciklama=aciklama.substring(0,100) + "...";
        }
        holder.aciklama.setText(aciklama);
    }

    @NonNull
    public List<Duyuru> getList(){
        return duyurular;
    }

    public Duyuru getDuyuru(int position){
        return duyurular.get(position);
    }

    public void addItem(Duyuru duyuru) {
        duyurular.add(duyuru);
        notifyItemInserted(duyurular.size()-1);
    }

    public void clearList() {
        int size = getItemCount();
        duyurular.clear();
        notifyItemRangeRemoved(0,size);
    }

    @Override
    public int getItemCount() {
        return duyurular.size();
    }
}
