package li.itcc.hackathon15.poimap;


import android.content.Context;
import android.database.Cursor;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.support.v4.app.LoaderManager;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;

import li.itcc.hackathon15.R;
import li.itcc.hackathon15.TitleHolder;
import li.itcc.hackathon15.database.DatabaseContract;
import li.itcc.hackathon15.gps.GPSDeliverer;
import li.itcc.hackathon15.gps.GPSLocationListener;

/**
 * Created by Arthur on 12.09.2015.
 */
public class PoiMapFragment extends SupportMapFragment implements GPSLocationListener, LoaderManager.LoaderCallbacks<Cursor> {
    private GoogleMap fMap;
    private int fFunction;
    private GPSDeliverer fGpsDeliverer;
    private int fPointCounter;
    private Marker fMarker;
    private ArrayList<Marker> fMarkers = new ArrayList<Marker>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = super.onCreateView(inflater, container, savedInstanceState);
        fMap = getMap();
        return v;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //if (item.getItemId() == R.id.action_example) {
        //    exampleAction();
        //    return true;
        //}
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof TitleHolder) {
            ((TitleHolder)context).setTitleId(R.string.title_overview_map);
        }
    }


    private void exampleAction() {
        fFunction++;
        int func = 1;
        if (fFunction == func++) {
            LatLng eschen = new LatLng(47.209953, 9.528884);

            //fMap.setMyLocationEnabled(true);
            fMap.moveCamera(CameraUpdateFactory.newLatLngZoom(eschen, 19));
            fMap.addMarker(new MarkerOptions()
                    .title("Sydney")
                    .snippet("The most populous city in Australia.")
                    .position(eschen));
        }
        else if (fFunction == func++) {
            fMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        }

        else if (fFunction == func++) {
            PolylineOptions rectOptions = new PolylineOptions()
                    .add(new LatLng(47.209953,9.528884))
                    .add(new LatLng(47.209900,9.528800))
                    .color(0xFFFF0000);
            Polyline polyline = fMap.addPolyline(rectOptions);
        }
        else if (fFunction == func++) {
            if (fMarker != null) {
                fMarker.remove();
                fMarker = null;
                fPointCounter = 0;
            }
            fGpsDeliverer = new GPSDeliverer(getActivity(), 0L);
            fGpsDeliverer.setListener(this);
            fGpsDeliverer.setAutoReset(false);
            fGpsDeliverer.startDelivery();
        }
        else if (fFunction == func++) {
            fGpsDeliverer.reset();
            fMarker.remove();
            fMarker = null;
        }
        else if (fFunction == func++) {
            fGpsDeliverer.stopDelivery();
            fGpsDeliverer = null;
            fFunction = 2;
        }
    }

    @Override
    public void onLocation(Location location) {
        LatLng loc = new LatLng(location.getLatitude(), location.getLongitude());
        String title = Integer.toString(fPointCounter++);
        if (fMarker == null) {
            fMarker = fMap.addMarker(new MarkerOptions()
                    .title(title)
                    .position(loc));
        }
        else {
            fMarker.setPosition(loc);
            fMarker.setTitle(title);
        }
    }

    @Override
    public void onLocationSensorSearching() {

    }

    @Override
    public void onLocationSensorEnabled() {

    }

    @Override
    public void onLocationSensorDisabled() {

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = null;
        String where = null;
        String[] whereArgs = null;
        String sortOrder = null;
        Uri queryUri = DatabaseContract.Pois.CONTENT_URI;
        CursorLoader loader = new CursorLoader(getActivity(), queryUri, projection, where, whereArgs, sortOrder);
        return loader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        clearAllMarkers();
        int longitudeCol = data.getColumnIndex(DatabaseContract.Pois.POI_LONGITUDE);
        int latitudeCol = data.getColumnIndex(DatabaseContract.Pois.POI_LATITUDE);
        int nameCol = data.getColumnIndex(DatabaseContract.Pois.POI_NAME);
        if (data.moveToFirst()) {
            do {
                double longitude = data.getDouble(longitudeCol);
                double latitude = data.getDouble(latitudeCol);
                String name = data.getString(nameCol);
                LatLng loc = new LatLng(latitude, longitude);
                Marker marker = fMap.addMarker(new MarkerOptions()
                        .title(name)
                        .position(loc));
                fMarkers.add(marker);
            } while (data.moveToNext());

        }
    }

    private void clearAllMarkers() {
        for (Marker marker: fMarkers) {
            marker.remove();
        }
        fMarkers.clear();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}