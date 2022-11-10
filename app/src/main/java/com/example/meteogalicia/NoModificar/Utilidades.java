package com.example.meteogalicia.NoModificar;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;


public class Utilidades {

    public static boolean existeConexionInternet(Context contexto) {
        ConnectivityManager connectivityManager = (ConnectivityManager) contexto.getSystemService(Context.CONNECTIVITY_SERVICE);
        Network network = connectivityManager.getActiveNetwork();
        NetworkInfo infoRed = connectivityManager.getNetworkInfo(network);

        return infoRed != null && infoRed.isAvailable() && infoRed.isConnected();
    }
}
