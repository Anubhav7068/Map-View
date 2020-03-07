package soft.gen.mapview;

import androidx.fragment.app.FragmentActivity;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;
import soft.gen.mapview.Service.Serverurl;
import soft.gen.mapview.Utility.Commonhelper;
import soft.gen.mapview.Utility.GPSTracker;

@SuppressWarnings("ConstantConditions")
public class MainActivity extends FragmentActivity implements OnMapReadyCallback {

    GoogleMap map;
    private Commonhelper commonhelper;
    private GPSTracker gps;
    private double mlatitude, mlongitude;
    private JSONArray jsarr_main;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        commonhelper = new Commonhelper(this);
        gps = new GPSTracker(this);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // Check if GPS enabled
        if (gps.canGetLocation()) {
            mlatitude = gps.getLatitude();
            mlongitude = gps.getLongitude();
//            commonhelper.setSharedPreferences(SharedPreference.sp_mylati, String.valueOf(mlatitude));
//            commonhelper.setSharedPreferences(SharedPreference.sp_mylongi, String.valueOf(mlongitude));


            ExeGETOFFICE();
        } else {
            gps.showSettingsAlert();

        }

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        map = googleMap;
//        LatLng Lucknow = new LatLng(26.8467, 80.9462);
//        map.addMarker(new MarkerOptions().position(Lucknow).title("LUCKNOW"));
//        map.moveCamera(CameraUpdateFactory.newLatLng(Lucknow));
    }

    private void ExeGETOFFICE() {
        AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
        asyncHttpClient.setTimeout(50000);
        asyncHttpClient.addHeader("Accept", "application/json");
        asyncHttpClient.addHeader("Content-Type", "application/json");
        asyncHttpClient.addHeader("Authorization", "");

        RequestParams requestParams = new RequestParams();
        requestParams.put("lat", "27.280256");
        requestParams.put("longt", "77.940646");

        asyncHttpClient.get(Serverurl.NearByOffice, requestParams, new JsonHttpResponseHandler() {

            @Override
            public void onStart() {
                commonhelper.ShowLoader();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                try {
                    commonhelper.HideLoader();
                    jsarr_main = response;
                    fillonmap();


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                commonhelper.HideLoader();
                Log.e("tag", errorResponse.toString());
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                commonhelper.HideLoader();
                Log.e("tag", throwable.toString());
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                commonhelper.HideLoader();
                Log.e("tag", throwable.toString());
            }

            @Override
            public void onProgress(long bytesWritten, long totalSize) {
                long percentage = (bytesWritten / totalSize) * 100;
                //progressDialog.setProgress(((int) percentage));
            }

        });
    }

    private void fillonmap() {
        try {
            for (int i = 0; i < jsarr_main.length(); i++) {
                JSONObject jsobj = jsarr_main.getJSONObject(i);
                String MachineID = jsobj.getString("machineId");
                JSONObject jsobjgeo = jsobj.getJSONObject("geoLocation");
                JSONArray strtemp = jsobjgeo.getJSONArray("coordinates");

                LatLng Lucknow = new LatLng(strtemp.getDouble(0), strtemp.getDouble(1));
                BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.locations);
                map.addMarker(new MarkerOptions().position(Lucknow).title(MachineID).icon(icon));
                map.moveCamera(CameraUpdateFactory.newLatLng(Lucknow));

            }
        } catch (Exception e) {

        }


    }
}
