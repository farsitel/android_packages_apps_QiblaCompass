package com.farsitel.qiblacompass.util;

import android.content.Context;
import android.location.Location;

import com.farsitel.qiblacompass.R;

public enum LocationEnum {
    MENU_ARAK(1, 34.08, 49.7), MENU_ARDABIL(2, 38.25, 48.28), MENU_ORUMIYEH(3,
            37.53, 45d), MENU_ESFEHAN(4, 32.65, 51.67), MENU_AHVAZ(5, 31.52,
            48.68), MENU_ILAM(6, 33.63, 46.42), MENU_BOJNURD(7, 37.47, 57.33), MENU_BANDAR_ABAS(
            8, 27.18, 56.27), MENU_BUSHEHR(9, 28.96, 50.84), MENU_BIRJAND(10,
            32.88, 59.22), MENU_TABRIZ(11, 38.08d, 46.3), MENU_TEHRAN(12,
            35.68, 51.42), MENU_KHORAM_ABAD(13, 33.48, 48.35), MENU_RASHT(14,
            37.3, 49.63), MENU_ZAHEDAN(15, 29.5, 60.85), MENU_ZANJAN(16, 36.67,
            48.48), MENU_SARI(17, 36.55, 53.1), MENU_SEMNAN(18, 35.57, 53.38), MENU_SANANDAJ(
            19, 35.3, 47.02), MENU_SHAHREKORD(20, 32.32, 50.85), MENU_SHIRAZ(
            21, 29.62, 52.53), MENU_GHAZVIN(22, 36.45, 50), MENU_GHOM(23,
            34.65, 50.95), MENU_KARAJ(24, 35.82, 50.97), MENU_KERMAN(25, 30.28,
            57.06), MENU_KERMANSHAH(26, 34.32, 47.06), MENU_GORGAN(27, 36.83,
            54.48), MENU_MASHHAD(28, 34.3, 59.57), MENU_HAMEDAN(29, 34.77,
            48.58), MENU_YASUJ(30, 30.82, 51.68), MENU_YAZD(31, 31.90, 54.37);

    private int id;
    private Location location;

    LocationEnum(int id, double latitude, double longitude) {
        Location l = new Location("GPS");
        l.setLatitude(latitude);
        l.setLongitude(longitude);
        this.location = l;
        this.id = id;

    }

    public int getId() {
        return this.id;
    }

    public Location getLocation() {
        return this.location;
    }

    public String getName(Context context) {
        return context.getResources().getStringArray(R.array.state_names)[this.id - 1];
    }
}
