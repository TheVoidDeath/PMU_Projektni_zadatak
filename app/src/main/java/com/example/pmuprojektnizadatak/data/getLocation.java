package com.example.pmuprojektnizadatak.data;

import static com.google.android.gms.common.util.CollectionUtils.listOf;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import java.util.List;

public class getLocation {

    public static void getLocation(Context activity) throws Exception {
        Geocoder geocoder;
        String bestProvider;
        List<Address> user = null;
        double lat;
        double lng;

        LocationManager lm = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);

        Criteria criteria = new Criteria();
        bestProvider = lm.getBestProvider(criteria, false);

        if(bestProvider==null)
        {
            Thread.sleep(1000);
            getLocation(activity);
            return ;
        }
        @SuppressLint("MissingPermission") Location location = lm.getLastKnownLocation(bestProvider);

        if (location == null){
            Toast.makeText(activity,"Location Not found",Toast.LENGTH_LONG).show();
        }else{
            geocoder = new Geocoder(activity);
            try {
                user = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                lat=(double)user.get(0).getLatitude();
                lng=(double)user.get(0).getLongitude();
                Container.location=listOf(lat,lng);
                Log.e("Location","Success location blyat");

            }catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
