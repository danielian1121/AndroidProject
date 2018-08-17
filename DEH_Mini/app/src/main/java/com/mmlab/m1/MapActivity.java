package com.mmlab.m1;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.mmlab.m1.adapter.LOISequenceAdapter;
import com.mmlab.m1.model.LOIModel;
import com.mmlab.m1.model.LOISequenceModel;
import com.mmlab.m1.model.POIModel;
import com.mmlab.m1.save_data.SaveLOISequence;
import com.mmlab.m1.service.HttpAsyncTask;
import com.mmlab.m1.service.TaskCompleted;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Locale;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback, TaskCompleted {

    private GoogleMap mMap;
    private Marker marker;
    private POIModel poiModel;
    private LOIModel loiModel;
    private ArrayList<LOISequenceModel> lois = new ArrayList<>();
    private ArrayList<Marker> markers = new ArrayList<>();
    private ToggleButton button_search_range;
    private Button button_change_location;
    private Button button_search_address;
    private int marker_number = 0;
    public Bitmap bmp;
    LOISequenceModel lm;

    public String title;
    private MyApplication globalVariable;
    private String token;
    private RecyclerView mRecyclerView;
    private LOISequenceAdapter mAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        button_search_range = (ToggleButton) findViewById(R.id.button_search_range);
        button_change_location = (Button) findViewById(R.id.button_change_location);
        button_search_address = (Button) findViewById(R.id.button_search_address);

        token = MainActivity.getToken();

        button_search_range.setVisibility(View.GONE);
        button_change_location.setVisibility(View.GONE);
        button_search_address.setVisibility(View.GONE);
        Bundle bundle = getIntent().getExtras();
        loiModel = bundle.getParcelable("LOI-Content");
        globalVariable = (MyApplication) getApplicationContext();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setTitle(getIntent().getStringExtra("title"));

        setUpMapIfNeeded();
    }

    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.google_map))
                    .getMapAsync(this);


            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }

        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_map, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        RelativeLayout v2 = (RelativeLayout) findViewById(R.id.bubble2);
        v2.setVisibility(View.INVISIBLE);
        if (id == R.id.close) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (mMap != null) {
            mMap.clear();
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            mMap.setMyLocationEnabled(true);
            mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {

                @Override
                public boolean onMarkerClick(Marker marker) {
                    if (marker.getTitle().equals("center")) {
                        marker.hideInfoWindow();
                        return true;
                    } else {
                        marker.showInfoWindow();
                        return false;
                    }

                }

            });
            mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {

                @Override
                public boolean onMarkerClick(Marker marker) {
                    if (marker.getTitle().equals("Your search center"))
                        return true;
                    marker.hideInfoWindow();
                    lm = lois.get(Integer.parseInt(marker.getSnippet()));
                    Button b1 = (Button) findViewById(R.id.poi_inform2);
                    RelativeLayout v2 = (RelativeLayout) findViewById(R.id.bubble2);
                    v2.setVisibility(View.VISIBLE);
                    TextView t1 = (TextView) findViewById(R.id.title1);
                    t1.setText(title);
                    TextView t2 = (TextView) findViewById(R.id.title2);
                    t2.setText(marker.getTitle());

                    CameraPosition camerPosition = new CameraPosition.Builder()
                            .target(new LatLng(marker.getPosition().latitude, marker.getPosition().longitude))
                            .zoom(14)
                            .build();
                    mMap.animateCamera(CameraUpdateFactory.newCameraPosition(camerPosition));

                    return true;
                }

            });

            mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                @Override
                public void onMapClick(LatLng latLng) {
                    View v = findViewById(R.id.bubble2);
                    v.setVisibility(View.GONE);

                }
            });

            String type2 = "LOI-Sequence";
            String api = getResources().getString(R.string.api_loi_seq);
            String url = api + /*"id=" +*/
                    loiModel.getLOIId(); //+ "&did=" + globalVariable.getDeviceID()
            //+ "&appver=mini200&ulat=22.9942&ulng=120.1659&clang=" + globalVariable.getLanguage();

            Log.d("url", url);

            if (globalVariable.checkInternet()) {
               // new HttpAsyncTask(MapActivity.this, "IP", 0).execute(getResources().getString(R.string.api_clientIP));
                new HttpAsyncTask(MapActivity.this, type2, 0, token).execute(url);
            }
            /*mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);

            mRecyclerView.setHasFixedSize(true);
            mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
            mRecyclerView.setAdapter(new LOISequenceAdapter(this, lois, "LOI-Sequence", model.getIdentifier()));
*/
        }

    }

    public void drawpoint(final int x) throws IOException, InterruptedException {
        mMap.clear();

        final ShareDialog shareDialog = new ShareDialog(this);
        final CallbackManager callbackManager = CallbackManager.Factory.create();
        for (int i = 0; i < lois.size(); i++) {
            lm = lois.get(i);
            if (x == 2) {
                Thread thread = new Thread(mutiThread);
                marker_number = i + 1;
                thread.start();
                thread.join();
                bmp = resizeMapIcons(bmp, 63, 102);
                marker = mMap.addMarker(new MarkerOptions().position(new LatLng(lm.getPOILat(), lm.getPOILong()))
                        .title(lm.getPOIName())
                        .snippet("" + i)
                        .icon(BitmapDescriptorFactory.fromBitmap(bmp)));
            } else
                marker = mMap.addMarker(new MarkerOptions().position(new LatLng(lm.getPOILat(), lm.getPOILong()))
                        .snippet("" + i)
                        .title(lm.getPOIName()));
            marker.hideInfoWindow();
            if (i == 0) {
                RelativeLayout v2 = (RelativeLayout) findViewById(R.id.bubble2);
                v2.setVisibility(View.VISIBLE);
                TextView t1 = (TextView) findViewById(R.id.title1);
                t1.setText(title);
                TextView t2 = (TextView) findViewById(R.id.title2);
                t2.setText(lm.getPOIName());
                CameraPosition camerPosition = new CameraPosition.Builder()
                        .target(new LatLng(lm.getPOILat(), lm.getPOILong()))
                        .zoom(14)
                        .build();
                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(camerPosition));
            }
        }
        Button b1 = (Button) findViewById(R.id.poi_inform2);
        //final LOISequenceModel lm2 = lois.get(0);

        b1.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), POIActivity.class);
                Bundle bundle = new Bundle();
                bundle.putParcelable("POI-Content", lm);
                bundle.putString("type", "POI-Id");
                intent.putExtras(bundle);
                v.getContext().startActivity(intent);

            }

        });
        Button b2 = (Button) findViewById(R.id.loi_inform);
        /*if (mServer.getType().equals("AOI"))
            b2.setText(R.string.aoi_info);
        else if (mServer.getType().equals("SOI"))
            b2.setText(R.string.soi_info);
        else
            b2.setText(R.string.loi_info);*/
        b2.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (x == 2) {
                    Intent intent = new Intent(v.getContext(), LOIActivity.class);
                    Bundle bundle = new Bundle();
                    globalVariable.setType("LOI-Id");
                    bundle.putParcelable("LOI-Content", loiModel);
                    intent.putExtras(bundle);
                    v.getContext().startActivity(intent);
                } else if (x == 3) {
                    Intent intent = new Intent(v.getContext(), AOIActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putParcelable("AOI-Content", loiModel);
                    globalVariable.setType("AOI");
                    intent.putExtras(bundle);
                    v.getContext().startActivity(intent);
                }
            }

        });
        Button b3 = (Button) findViewById(R.id.poi_share2);

        b3.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareDialog.registerCallback(callbackManager, new FacebookCallback<Sharer.Result>() {
                    @Override
                    public void onSuccess(Sharer.Result result) {

                    }

                    @Override
                    public void onCancel() {

                    }

                    @Override
                    public void onError(FacebookException error) {

                    }
                });
                if (shareDialog.canShow(ShareLinkContent.class)) {
                    ShareLinkContent linkContent = new ShareLinkContent.Builder()
                            .setContentTitle(lm.getPOIName())
                            .setContentDescription(lm.getPOIDescription())
                            .setContentUrl(Uri.parse("http://deh.csie.ncku.edu.tw/poi_detail/" + lm.getPOIId()))
                            .build();
                    shareDialog.show(linkContent);
                }
            }
        });
        Button b4 = (Button) findViewById(R.id.poi_guide2);
        String lng = Locale.getDefault().getDisplayLanguage();
        if (lng.equals("日本語")) {
            b1.setTextSize(10);
            b3.setTextSize(10);
        } else if (lng.equals("English")) {
            b1.setTextSize(8);
            b2.setTextSize(8);
            b4.setTextSize(8);
        }
        globalVariable.getCurrentLocation();
        b4.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                String from = "saddr=" + globalVariable.getLatitude() + "," + globalVariable.getLongitude();
                String to = "daddr=" + lm.getPOILat() + "," + lm.getPOILong();
                Log.d("To:", to);
                String uri = "https://maps.google.com/maps?f=d&" + from + "&" + to + "&hl=tw";
                Log.d("Uri", uri);
                Uri i = Uri.parse(uri);
                Intent intent = new Intent(android.content.Intent.ACTION_VIEW, i);
                //intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
                startActivity(intent);
            }
        });
    }



    private Runnable mutiThread = new Runnable() {
        @Override
        public void run() {
            URL url = null;
            try {
                url = new URL("http://chart.apis.google.com/chart?chst=d_map_pin_letter&chld="+marker_number+"|FE7569");
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            try {
                bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    };
    public Bitmap resizeMapIcons(Bitmap bmp,int width, int height){
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(bmp, width, height, false);
        return resizedBitmap;
    }

    @Override
    public void onTaskComplete(String response, String type) {
        if (type.equals("IP")) {
            globalVariable.setIp(response.replaceAll("\n", ""));
            Log.d("ip", globalVariable.getIp());

        } else if (globalVariable.getIp() != null) {
            if (type.equals("LOI-Sequence")) {
                JSONObject jsonResponse ;
                Log.d("test", response);
                try {
                    jsonResponse = new JSONObject(response);
                    JSONArray results = jsonResponse.getJSONArray("results");
                    title = results.getJSONObject(0).getString("LOI_title");
                    Log.d("title","??:"+title);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                //loiSequenceList = MainActivity.getServer().getLOImodel().getmPOIs();
                SaveLOISequence saveLOISequence = new SaveLOISequence(response,"SOI");
                lois = saveLOISequence.getLOISequenceList();
                for (int i = 0; i < lois.size(); ++i) {
                    if (lois.get(i).getIdentifier().equals("docent")) {
                        new HttpAsyncTask(MapActivity.this, "contributor-detail:" + Integer.toString(i),0)
                                .execute(getResources().getString(R.string.api_docent_info) + lois.get(i).getContributor());
                    }
                }
                /*mAdapter = new LOISequenceAdapter(this, lois, type, loiModel.getIdentifier(),loiModel.getContributor(),MainActivity.getServer());
                mRecyclerView.setHasFixedSize(true);
                mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
                mRecyclerView.setAdapter(mAdapter);*/


                //String hint = getString(R.string.find) + " " + lois.size() + " " + getString(R.string.sites);
                //Snackbar.make(mRecyclerView, hint, Snackbar.LENGTH_SHORT).show();
                try {
                    drawpoint(2);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            } else if (type.contains("contributor-detail")) {
                try {
                    //loiSequenceList = MainActivity.getServer().getLOImodel().getmPOIs();
                    JSONObject obj = new JSONObject(response);
                    int index = Integer.valueOf(type.substring("contributor-detail:".length()));

                    MainActivity.getServer().getLOImodel().getmPOIs().get(index).setContributorDetail(obj);
                    Log.d("test","OEE"+MainActivity.getServer().getLOImodel().getmPOIs().get(index).getmContributorDetail());
                    //loiSequenceList.get(index).setContributorDetail(obj);
                } catch (JSONException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }
}
