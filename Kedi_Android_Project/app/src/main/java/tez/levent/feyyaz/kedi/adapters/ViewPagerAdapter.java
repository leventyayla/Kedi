package tez.levent.feyyaz.kedi.adapters;

// Created by Levent on 5.12.2016.

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;

import tez.levent.feyyaz.kedi.fragments.DuyuruFragment;
import tez.levent.feyyaz.kedi.fragments.EtkinlikFragment;
import tez.levent.feyyaz.kedi.fragments.KulupFragment;

public class ViewPagerAdapter extends FragmentPagerAdapter {

    private DuyuruFragment duyurular = null;
    private EtkinlikFragment etkinlikler = null;

    public ViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Nullable
    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return new KulupFragment();
            case 1:
                etkinlikler = new EtkinlikFragment();
                return etkinlikler;
            case 2:
                duyurular = new DuyuruFragment();
                return duyurular;
            default:
                return null;
        }
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "Kulüpler";
            case 1:
                return "Etkinlikler";
            case 2:
                return "Duyurular";
        }
        return null;
    }

    @Override
    public int getCount() {
        return 3;
    }

    public DuyuruFragment getDuyurular() {
        return duyurular;
    }

    public EtkinlikFragment getEtkinlikler() {
        return etkinlikler;
    }
}
