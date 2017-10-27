package tez.levent.feyyaz.kedi.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringDef;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import tez.levent.feyyaz.kedi.R;

/**
 * Created by Levent on 8.03.2017.
 */

public class EtkinlikHakkinda extends Fragment {
    TextView aciklama,baslik;
    @Nullable
    String aciklamaStr,baslikStr;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        aciklamaStr = getArguments().getString("aciklama");
        baslikStr = getArguments().getString("baslik");
        return inflater.inflate(R.layout.frag_etkinlik_hakkinda, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        aciklama = (TextView) view.findViewById(R.id.aciklama);
        baslik = (TextView) view.findViewById(R.id.baslik);
        aciklama.setText(aciklamaStr);
        baslik.setText(baslikStr);
    }

}
