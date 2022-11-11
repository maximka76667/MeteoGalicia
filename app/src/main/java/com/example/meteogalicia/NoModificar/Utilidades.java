package com.example.meteogalicia.NoModificar;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;


public class Utilidades {

    public static boolean existeConexionInternet(Context contexto) {
        ConnectivityManager connectivityManager = (ConnectivityManager) contexto.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo infoRed = connectivityManager.getActiveNetworkInfo();

        return infoRed != null && infoRed.isAvailable() && infoRed.isConnected();
    }
}
