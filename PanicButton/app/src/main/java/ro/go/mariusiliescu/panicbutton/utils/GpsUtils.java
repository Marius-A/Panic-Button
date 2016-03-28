package ro.go.mariusiliescu.panicbutton.utils;

import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by maryus on 26.03.2016.
 */
public class GpsUtils extends Service implements LocationListener {

    private final Context context;

    private boolean isNetworkEnabled;
    private boolean isGPSEnabled;
    private boolean canGetLocation;

    Location location;

    private static final long MIN_DISTANCE_VHANGE_FOR_UPDATES = 30;
    private static final long MIN_TIME_BW_UPDATES = 1000 * 60 ;

    protected LocationManager locationManager;

    public GpsUtils(Context context) {
        isNetworkEnabled = false;
        isGPSEnabled = false;
        canGetLocation = false;
        this.context = context;
        getLocation();

        Log.d("X", "net "+isGPSEnabled);
        Log.d("X", "gps " + isNetworkEnabled);
    }

    public Location getLocation() {
        try {
            locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);
            isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (!isGPSEnabled && !isNetworkEnabled) {

            } else {
                this.canGetLocation = true;

                if (isGPSEnabled) {
                    if (location == null) {
                        locationManager.requestLocationUpdates(
                                LocationManager.GPS_PROVIDER,
                                MIN_TIME_BW_UPDATES,
                                MIN_DISTANCE_VHANGE_FOR_UPDATES, this);

                        if (locationManager != null) {
                            location = locationManager.getLastKnownLocation(locationManager.GPS_PROVIDER);
                        }
                    }
                }

                if(isNetworkEnabled) {
                    locationManager.requestLocationUpdates(
                            LocationManager.NETWORK_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_VHANGE_FOR_UPDATES, this);

                    if (locationManager != null) {
                        location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    }
                }
            }
        } catch (SecurityException e) {
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
        }
        return location;
    }

    public void stopGPSTracking() {
        try {
            if (locationManager != null) {
                locationManager.removeUpdates(GpsUtils.this);
            }
        }catch(SecurityException e){
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    public boolean canGetLocation(){
        return  this.canGetLocation;
    }
    public double getLatitude(){
        double latitude = 0.0;
        if(location!=null){
            latitude = location.getLatitude();
        }
        return latitude;
    }
    public double getLongitude(){
        double longitude = 0.0;
        if(location!=null){
            longitude = location.getLongitude();
        }
        return longitude;
    }
    public boolean getIsNetworkEnabled(){
        return  this.isNetworkEnabled;
    }

    public void showSettingsAlert(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);

        alertDialog.setTitle("GPS is setting");
        alertDialog.setMessage("GPS is not enabled Do you want to go to settings menu?");

        alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                context.startActivity(intent);
            }
        });
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        alertDialog.show();

    }
    public void showDataSettingsAlert(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);

        alertDialog.setTitle("Data is setting");
        alertDialog.setMessage("Data is not enabled Do you want to go to settings menu?");

        alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_WIFI_SETTINGS);
                context.startActivity(intent);
            }
        });
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        alertDialog.show();

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
    private void toggleGPS(boolean enable) {
        String provider = Settings.Secure.getString(getContentResolver(),
                Settings.Secure.LOCATION_PROVIDERS_ALLOWED);

        if(provider.contains("gps") == enable) {
            return; // the GPS is already in the requested state
        }

        final Intent poke = new Intent();
        poke.setClassName("com.android.settings",
                "com.android.settings.widget.SettingsAppWidgetProvider");
        poke.addCategory(Intent.CATEGORY_ALTERNATIVE);
        poke.setData(Uri.parse("3"));
        context.sendBroadcast(poke);
    }
    // automatic turn off the gps
    public void turnGPSOff()
    {
        String provider = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
        if(provider.contains("gps")){ //if gps is enabled
            final Intent poke = new Intent();
            poke.setClassName("com.android.settings", "com.android.settings.widget.SettingsAppWidgetProvider");
            poke.addCategory(Intent.CATEGORY_ALTERNATIVE);
            poke.setData(Uri.parse("3"));
            this.context.sendBroadcast(poke);
        }
    }
}
