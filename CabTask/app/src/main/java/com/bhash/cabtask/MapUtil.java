package com.bhash.cabtask;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;



public class MapUtil {

    private final static String TAG = MapUtil.class.getName();

    public static String getFromLocation(Context context, Location location) {
        String addressString = "";
        if (location.getLatitude() != 0 && location.getLongitude() != 0) {
            try {
                Geocoder geocoder = new Geocoder(context);
                List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                if (addresses.size() > 0) {
                    Address address = addresses.get(0);
                    return getAddress(address);
                }

            } catch (Exception e) {
                Log.d(TAG, "Could not get address for lat/lon" + location.getLatitude() + " " +location.getLongitude());
                e.printStackTrace();
            }
        }
        return addressString;
    }

    public static String getAddressFromLatLng(Context context, LatLng latLng) {
        String addressString = "";
        if (latLng.latitude > 0 && latLng.longitude > 0) {
            try {
                Geocoder geocoder = new Geocoder(context);
                List<Address> addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
                if (addresses.size() > 0) {
                    Address address = addresses.get(0);
                    return getAddress(address);
                }

            } catch (Exception e) {
                Log.d(TAG, "Could not get address for lat/lon" + latLng.latitude + " " +latLng.longitude);
                e.printStackTrace();
            }
        }
        return addressString;
    }

    public static String getAddress(Address address) {

        StringBuilder stringBuilder = new StringBuilder();

        if (address != null) {
            String addressLine = address.getAddressLine(0);
            String city = address.getLocality();
            String state = address.getAdminArea();
            String country = address.getCountryName();
            String postalCode = address.getPostalCode();
            String knownName = address.getFeatureName();

            if (!StringUtil.isEmpty(addressLine)) {
                stringBuilder.append(addressLine).append(" ");
            }
            if (!StringUtil.isEmpty(city)) {
                stringBuilder.append(city).append(" ");
            }
            if (!StringUtil.isEmpty(state)) {
                stringBuilder.append(country).append(" ");
            }
            if (!StringUtil.isEmpty(country)) {
                stringBuilder.append(country).append(" ");
            }
            if (!StringUtil.isEmpty(postalCode)) {
                stringBuilder.append(postalCode).append(" ");
            }
            Log.d(TAG, "address = " + address + ", city = " + city + ", country = " + country + "Total address:"+stringBuilder.toString());
        }
        return stringBuilder.toString();
    }
}
