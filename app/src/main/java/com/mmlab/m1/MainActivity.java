package com.mmlab.m1;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.net.http.HttpResponseCache;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.github.amlcurran.showcaseview.ShowcaseView;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.mmlab.m1.adapter.AOIAdapter;
import com.mmlab.m1.adapter.LOIAdapter;
import com.mmlab.m1.adapter.MyFavoriteAdapter;
import com.mmlab.m1.adapter.POIAdapter;
import com.mmlab.m1.adapter.SOIAdapter;
import com.mmlab.m1.constant.MSN;
import com.mmlab.m1.decoration.DividerItemDecoration;
import com.mmlab.m1.helper.ExternalStorage;
import com.mmlab.m1.helper.Helper;
import com.mmlab.m1.helper.Preset;
import com.mmlab.m1.model.DEHUser;
import com.mmlab.m1.model.LOIModel;
import com.mmlab.m1.model.LOISequenceModel;
import com.mmlab.m1.model.MyFavorite;
import com.mmlab.m1.model.POIModel;
import com.mmlab.m1.network.CacheService;
import com.mmlab.m1.network.ProxyService;
import com.mmlab.m1.service.Filter;
import com.mmlab.m1.widget.AboutDialog;
import com.mmlab.m1.widget.ExitDialog;
import com.mmlab.m1.widget.FilterDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Locale;

import io.realm.Realm;
import io.realm.RealmResults;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;

/*import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;*/

//import com.mmlab.m1.widget.HotspotDialog;
//import com.mmlab.m1.widget.SettingDialog;
//import com.mmlab.m1.widget.NetworkDialog;

public class MainActivity extends AppCompatActivity implements  OnMapReadyCallback {
    private static int DEFAULT_DISTANCE = 10000;
    private static int DEFAULT_NUMBER = 50;
    private static final String TAG = "MainActivity";
    private Toolbar toolbar = null;

    /**
     * V2 Start
     */
//    private View layout_disconnect = null;
//    private Button button_reconnect = null;
    /**
     * V2 End
     **/
    private ListView group_list;
    private boolean check_group_info = false;
    String url_for_search_group = "http://deh.csie.ncku.edu.tw:8080/api/v1/groups/search";
    String urlForProjectGroup = "http://140.116.82.130:8080/api/v1/docent/groups/search";
    String urlGroupCreate = "http://140.116.82.130:8080/api/v1/docent/groups/add";
    String urlGroupDelete = "http://140.116.82.130:8080/api/v1/docent/groups/disband?group_id=";
    private static ProxyService mServer = null;
    private ServerReceiver serverReceiver = null;
    //private ClientReceiver clientReceiver = null;
    public Bitmap bmp;
    private android.app.FragmentManager fragmentManager = null;
    private MyApplication globalVariable;
    private CoordinatorLayout coordinatorLayout;
    private int number;
    private Double distance;
    private double radius;
    private String mType;
    private Double lat;
    private Double lng;
    private static String Token;
    private static boolean flag = false;
    private static boolean move = false;
    private boolean notShow = false, fromPoint=false;
    private String api;
    private boolean taskRunning;
    private POIModel pm ;
    private static String hint,Type = " ";
    private Menu mMenu;
    private String identifier;
    private String media;
    private String category;
    private String url;
    private EditText accountInput;
    private TextView passwordInput;
    private Realm realm;
    private RealmResults<DEHUser> userResult;
    private static ArrayList<String> groups_id_list = new ArrayList<String>();
    private static ArrayList<String> groups_leader_name_list = new ArrayList<String>();
    private static ArrayList<String> groups_name_list = new ArrayList<String>();
    private static ArrayList<String> groups_role_list = new ArrayList<String>();
    private static ArrayList<String> groups_show = new ArrayList<String>();
    private static String userId,language,userpw;
    private boolean change_location;
    private boolean search_address;
    private boolean search_range;
    private boolean search_center;
    private boolean checkBox_setting;
    private boolean change_location_status;
    private boolean search_address_status;
    private boolean search_range_status;
    private boolean checkbox_setting_status = false;
    private boolean button_Search_range_status;
    private boolean search_center_status;
    private boolean login_status;
    private boolean include_menu_log_status;
    private boolean include_my_site_staus;
    private boolean include_search_status;
    private boolean button_my_site_status;
    private boolean from_where = false;

    private Button button_description;
    private Button button_about;
    private Button button_setting;
    private Button button_sign_in;

    private Button button_my_fav;

    private int marker_number=0;
    private ShowcaseView showcaseView;
    private POIAdapter poiAdapter;
    private DrawerLayout drawer;

    private GoogleMap mMap;
    private Marker marker,cmarker;;
    private LatLng gps;
    private POIModel poiModel;

    private String g_name;
    private String g_id;
    private View r;
    private Context context;
    private LocationManager mLocationManager;
    private Location curlocation;
    private LatLng center_location;
    private LatLng after_search_location;
    private LocationListener mLocationListener;
    private RecyclerView mRecyclerView = null;
    private RecyclerView MemberRecyclerView = null;
    private LinearLayout mLinearLayout;
    private LinearLayout include_menu_log;
    private LinearLayout include_my_site;
    private LinearLayout include_search;
    private RelativeLayout v;
    private LOIAdapter loiAdapter;
    private AOIAdapter aoiAdapter;
    private SOIAdapter soiAdapter;
    private MyFavoriteAdapter myFavoriteAdapter;

    private LOISequenceModel lm;
    private Button button_change_location;
    private Button button_search_address;
    private ToggleButton button_search_range;
    private Button button_search_center;

    private ArrayList<MyFavorite> mFavorites;
    View item;
    private Activity activity;
    private int choose = 2;
    private int group_number = -1;
    private Socket mSocket;
    private View myview;
    private int L_or_A = 0;
    private OkHttpClient client = new OkHttpClient();
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private String group_Name = "";
    private String fileName = "SaveLeaderMode";
    String temp = "http://192.168.137.1:8001/";
    {
        try {
            mSocket = IO.socket(temp);
        } catch (URISyntaxException e) {
            Log.d("OKHTTP", "QQ");
        }
    }

    private Emitter.Listener Data = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            if(choose==1) {
                Log.d("KAMO","Data");
                int flag = Integer.parseInt((String) args[0]);
                if (flag == 0) {
                    myview = (View) findViewById(R.id.button_search_POI);
                    preSetting(myview);
                } else if (flag == 1) {
                    myview = (View) findViewById(R.id.button_search_LOI);
                    preSetting(myview);
                    L_or_A = 0;
                } else if (flag == 2) {
                    myview = (View) findViewById(R.id.button_search_AOI);
                    preSetting(myview);
                    L_or_A = 1;
                }
            }
        }
    };
    private Emitter.Listener lng_Data = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {

            if(choose==1) {
                Log.d("KAMO","lng");
                lng = Double.parseDouble((String)args[0]);
            }
        }
    };
    private Emitter.Listener lat_Data = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            if(choose==1) {
                Log.d("KAMO","lat");
                lat = Double.parseDouble((String) args[0]);
            }
        }
    };
    private Emitter.Listener GetPosition = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            Log.d("KAMO",(String)args[0]);
            if(choose==1) {
                poiAdapter.setposition(Integer.parseInt((String)args[0]));
            }
        }
    };
    private Emitter.Listener LoiGetPosition = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            Log.d("KAMO123",(String)args[0]);
            if(choose==1) {
                for(int i = 0; i < loiAdapter.getItemCount() ; i++){
                    if(loiAdapter.getmModels().get(i).getLOIId().equals((String)args[0])){
                        loiAdapter.setLOIModel(loiAdapter.getmModels().get(i));
                        break;
                    }
                }
                Log.d("KAMOTEST",loiAdapter.getLOIModel().getLOIId());
                L_or_A=0;
            }
        }
    };

    private Emitter.Listener AoiGetPosition = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            Log.d("KAMO456",(String)args[0]);
            if(choose==1) {
                for(int i = 0; i < aoiAdapter.getItemCount() ; i++){
                    if(aoiAdapter.getmModels().get(i).getLOIId().equals((String)args[0])){
                        aoiAdapter.setLOIModel(aoiAdapter.getmModels().get(i));
                        break;
                    }
                }
                Log.d("KAMOTEST",aoiAdapter.getLOIModel().getLOIId());
                L_or_A=1;
            }
        }
    };

    private Emitter.Listener ChangeView = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            if(choose==1) {
                String temp = (String)args[0];
                String temp2;
                //Log.d("KAMOhi",);
                for(int i = 0; i < poiAdapter.getPOIList().size();i++) {
                    temp2 = poiAdapter.getPOIList().get(i).getPOIId();
                    if (temp2.equals(temp)) {
                        pm = poiAdapter.getPOIList().get(i);
                        break;
                    }
                }
                from_where = false;
                Intent intent = new Intent(activity, POIActivity.class);
                Bundle bundle = new Bundle();
                bundle.putParcelable("POI-Content", pm);
                bundle.putString("type", "Normal");
                bundle.putString("flag", "Member");
                intent.putExtras(bundle);
                startActivity(intent);
            }
        }
    };

    private Emitter.Listener LoiChange = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            if(choose==1 && L_or_A==0) {
                Log.d("KAMO","LOICHANGE");
                Log.d("KAMO",(String)args[0]);
                ArrayList<LOISequenceModel> lois;
                for(int i=0;i < loiAdapter.getmModels().size();i++){
                    lois = loiAdapter.getmModels().get(i).getmPOIs();
                    for(int j=0;j<lois.size();j++){
                        if(lois.get(j).getPOIId().equals((String) args[0])){
                            lm = lois.get(j);
                            break;
                        }
                    }
                }
                Log.d("KAMO",lm.getPOIId());
                Intent intent = new Intent(activity, POIActivity.class);
                Bundle bundle = new Bundle();
                bundle.putParcelable("POI-Content", lm);
                bundle.putString("type", "POI-Id");
                bundle.putString("flag", "Member");
                intent.putExtras(bundle);
                startActivity(intent);
            }else if(choose==1 && L_or_A==1) {
                int target=0;
                ArrayList<LOISequenceModel> lois;
                for(int i=0;i < aoiAdapter.getmModels().size();i++){
                    lois = aoiAdapter.getmModels().get(i).getmPOIs();
                    for(int j=0;j<lois.size();j++){
                        if(lois.get(j).getPOIId().equals((String) args[0])){
                            lm = lois.get(j);
                            break;
                        }
                    }
                }
                Intent intent = new Intent(activity, POIActivity.class);
                Bundle bundle = new Bundle();
                bundle.putParcelable("POI-Content", lm);
                bundle.putString("type", "POI-Id");
                bundle.putString("flag", "Member");
                intent.putExtras(bundle);
                startActivity(intent);
            }
        }
    };


    public void clearFileDirectory(File fileOrDirectory) {
        if (fileOrDirectory.isDirectory())
            for (File child : fileOrDirectory.listFiles())
                clearFileDirectory(child);
        fileOrDirectory.delete();

    }

    public void dialog_status(){
        new AlertDialog.Builder(MainActivity.this)
                .setMessage("Choose your status")
                .setPositiveButton("Leader", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        choose = 0;
                    }
                })
                .setNegativeButton("Member", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        choose = 1;
                    }
                })
                .show();
    }

    public void check(){
        FileInputStream infso = null;
        BufferedInputStream buffered = null;
        String getGroupId;
        try {
            infso = openFileInput(fileName);
            buffered = new BufferedInputStream(infso);
            byte[] buffbyte = new byte[20];
            int length = buffered.read(buffbyte);
            Log.d("KAMOnumber",Integer.toString(length));
            if(length==0){

            }else{
                byte[] temp = new byte[length];
                for(int i = 0;i<length;i++){
                    temp[i]=buffbyte[i];
                }
                Log.d("KAMOnumber","4747");
                getGroupId = new String(temp, StandardCharsets.UTF_8);
                group_number = Integer.parseInt(getGroupId);
                Log.d("KAMOnumber",getGroupId+" 789");
            }
            buffered.close();
            infso.close();
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Emitter.Listener GetSearch = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            Log.d("GetSearch111",(String)args[0]);
            String ThisLat = "";
            String ThisLon = "";
            String ThisId = "";
            String type = "";
            try{
                JSONObject getData = new JSONObject((String)args[0]);
                ThisLat = getData.getString("latitude");
                ThisLon = getData.getString("longtitude");
                ThisId = getData.getString("id");
                type = getData.getString("type");
            }catch (JSONException e){

            }
            Log.d("GetStart",ThisLat);
            Log.d("GetStart",ThisLon);
            Log.d("GetStart",ThisId);
            Log.d("GetStart",type);
            if(ThisId.equals(Integer.toString(group_number)) && choose==1){
                lat = Double.parseDouble(ThisLat);
                lng = Double.parseDouble(ThisLon);
                switch (Integer.parseInt(type)){
                    case 0:
                        myview = (View) findViewById(R.id.button_search_POI);
                        preSetting(myview);
                        break;
                    case 1:
                        myview = (View) findViewById(R.id.button_search_LOI);
                        preSetting(myview);
                        L_or_A = 0;
                        break;
                    case 2:
                        myview = (View) findViewById(R.id.button_search_AOI);
                        preSetting(myview);
                        L_or_A = 1;
                        break;
                }
            }
        }
    };

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //check();
        item = LayoutInflater.from(this).inflate(R.layout.group, null);
        //createGroup();
        Log.d(TAG, "onCreate()...");
        Log.d("Login","qwe"+login_status );
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_main);

        globalVariable = (MyApplication) getApplicationContext();
       /* Intent intent = new Intent(this, IntroActivity.class);
        startActivity(intent);*/
        r = (View) findViewById(R.id.mapp);
        realm = Realm.getInstance(this);
        mSocket.connect();
        Log.d("KAMO","onCreate");
        mSocket.on("lng_data", lng_Data);
        mSocket.on("lat_data", lat_Data);
        mSocket.on("type_data", Data);
        mSocket.on("get_search",GetSearch);
        //mSocket.on("GetPosition", GetPosition);
        //mSocket.on("LoiGetPosition", LoiGetPosition);
        //mSocket.on("AoiGetPosition", AoiGetPosition);
        mSocket.on("ChangeView", ChangeView);
        mSocket.on("LoiChange", LoiChange);
        activity = this;
        try {
            File mediaCacheDir = new File(this.getCacheDir(), "media-cache");
            long mediaCacheSize = 150 * 1024 * 1024; // 150 MiB
            HttpResponseCache.install(mediaCacheDir, mediaCacheSize);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Preset.clearPreferences(getApplicationContext());
        /*if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2);

        }*/
        clearFileDirectory(new File(ExternalStorage.BASE_ROOT + File.separator + ExternalStorage.TARGET_DIRECTORY));

        fragmentManager = getFragmentManager();

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setTitle("DEH Mini II");

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);

        //Chenyi20180130: set IpAddress
        WifiManager wifiMan = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInf = wifiMan.getConnectionInfo();
        long ip = wifiInf.getIpAddress();
        globalVariable.setIp(String.valueOf(ip));

        toggle.syncState();
        mLinearLayout = (LinearLayout) findViewById(R.id.lr_view);
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        // 設置Item增加移除動畫
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        // 設置分割線
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));
        //
        //navigationDrawer = new NavigationDrawer(this, toolbar);

        //RealmResults<User> userResult = realm.where(User.class).findAll();
        /*if (!userResult.isEmpty()) {
            identityDialog.show(getFragmentManager(), "identityDialog");
        }*/

        init();

        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);


        startService();

        identifier = "all";
        media = "all";
        category = "all";

        include_menu_log = (LinearLayout) findViewById(R.id.include_menu_log);
        include_my_site = (LinearLayout) findViewById(R.id.include_my_site);
        include_search = (LinearLayout) findViewById(R.id.include_search);
        include_menu_log.setVisibility(View.GONE);
        include_menu_log_status = false;
        include_my_site.setVisibility(View.GONE);
        include_my_site_staus = false;
        include_search.setVisibility(View.GONE);
        include_search_status = false;

        menuLog();

        search();

        button_change_location = (Button) findViewById(R.id.button_change_location);
        button_search_address = (Button) findViewById(R.id.button_search_address);
        button_search_range = (ToggleButton) findViewById(R.id.button_search_range);
        button_search_center = (Button) findViewById(R.id.button_search_center);

        button_search_address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 根據point取得該經緯度所對應的地址/地標
                String address = Helper.getAddressByLatLng(center_location);
                if (address == null) {
                    Toast.makeText(context, "Not found !", Toast.LENGTH_SHORT)
                            .show();
                } else {
                    Toast.makeText(context, address, Toast.LENGTH_SHORT).show();
                }
            }


        });

        GetUserPreference();
        CheckButtonStatus();

        distance = (double) DEFAULT_DISTANCE;
        number = DEFAULT_NUMBER;

    }


    public void menuLog(){
        button_about = (Button) findViewById(R.id.button_about);
        button_setting = (Button) findViewById(R.id.button_setting);
        button_sign_in = (Button) findViewById(R.id.button_sign_in);
        button_description = (Button)findViewById(R.id.button_description);
        button_description.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this,IntroActivity.class));
            }
        });

        button_about.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                AboutDialog dialog = new AboutDialog();
                dialog.show(getFragmentManager(), "aboutDialog");
                include_menu_log.setVisibility(View.GONE);
                include_menu_log_status = false;
            }
        });

        button_setting.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                include_menu_log.setVisibility(View.GONE);
                include_menu_log_status = false;
                MaterialDialog dialog = new MaterialDialog.Builder(MainActivity.this)
                        .title(R.string.setting_deh)
                        .customView(R.layout.dialog_setting, true)
                        .widgetColor(getResources().getColor(R.color.colorPrimary))
                        .positiveText(R.string.confirm)
                        .negativeText(R.string.cancel)
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            public void onClick(@NonNull MaterialDialog materialDialog,@NonNull DialogAction dialogAction) {
                                if (change_location){
                                    button_change_location.setVisibility(View.VISIBLE);
                                    change_location_status = true;
                                } else {
                                    button_change_location.setVisibility(View.GONE);
                                    change_location_status = false;
                                }
                                if (search_address){
                                    button_search_address.setVisibility(View.VISIBLE);
                                    search_address_status = true;
                                } else {
                                    button_search_address.setVisibility(View.GONE);
                                    search_address_status = false;
                                }
                                if (search_range){
                                    button_search_range.setVisibility(View.VISIBLE);
                                    search_range_status = true;
                                } else {
                                    button_search_range.setVisibility(View.GONE);
                                    search_range_status = false;
                                }
                                if (search_center){
                                    button_search_center.setVisibility(View.VISIBLE);
                                    search_center_status = true;
                                } else {
                                    button_search_center.setVisibility(View.GONE);
                                    search_center_status = false;
                                }
                                if(checkBox_setting){
                                    checkbox_setting_status = true;
                                } else {
                                    checkbox_setting_status = false;
                                }
                            }
                        })
                        .build();

                //final ToggleButton tButton_change_location = (ToggleButton) dialog.getCustomView().findViewById(R.id.toggleButton);
                //tButton_change_location.setChecked(change_location_status);
                final ToggleButton tButton_search_address = (ToggleButton) dialog.getCustomView().findViewById(R.id.toggleButton2);
                tButton_search_address.setChecked(search_address_status);
                final ToggleButton tButton_search_range = (ToggleButton) dialog.getCustomView().findViewById(R.id.toggleButton3);
                tButton_search_range.setChecked(search_range_status);
                final ToggleButton tButton_search_center = (ToggleButton) dialog.getCustomView().findViewById(R.id.toggleButton4);
                tButton_search_center.setChecked(search_center_status);

                final CheckBox checkBox_set = (CheckBox)  dialog.getCustomView().findViewById(R.id.checkBox_set);
                checkBox_set.setChecked(checkbox_setting_status);

                /*tButton_change_location.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if(isChecked){
                            change_location = true;
                        } else {
                            change_location = false;
                        }
                    }
                });*/
                tButton_search_address.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if(isChecked){
                            search_address = true;
                        } else {
                            search_address = false;
                        }
                    }

                });
                tButton_search_range.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if(isChecked){
                            search_range = true;
                        } else {
                            search_range = false;
                        }
                    }

                });
                tButton_search_center.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if(isChecked){
                            search_center = true;
                        } else {
                            search_center = false;
                        }
                    }

                });

                checkBox_set.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if(isChecked){
                            checkBox_setting = true;
                        } else {
                            checkBox_setting = false;
                        }
                    }

                });

                SeekBar seekbarDistance = (SeekBar) dialog.getCustomView().findViewById(R.id.seekBar_distance);
                seekbarDistance.setProgress(DEFAULT_DISTANCE);
                distance = (double) DEFAULT_DISTANCE;
                final TextView textViewDistance = (TextView) dialog.getCustomView().findViewById(R.id.text_distance);
                textViewDistance.setText(new DecimalFormat("#0.0").format(DEFAULT_DISTANCE / 1000) + " " + getString(R.string.kilometer));
                seekbarDistance.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        distance = (double) progress;
                        DEFAULT_DISTANCE = progress;
                    }
                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                    }
                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        textViewDistance.setText(new DecimalFormat("#0.0").format(distance / 1000) + " " + getString(R.string.kilometer));
                    }
                });

                SeekBar seekbarNumber = (SeekBar) dialog.getCustomView().findViewById(R.id.seekBar_number);
                seekbarNumber.setProgress(DEFAULT_NUMBER);
                number = DEFAULT_NUMBER;
                final TextView textViewNumber = (TextView) dialog.getCustomView().findViewById(R.id.text_number);
                textViewNumber.setText(seekbarNumber.getProgress() + " " + getResources().getString(R.string.unit));
                seekbarNumber.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        number = progress;
                        DEFAULT_NUMBER = progress;
                    }
                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                    }
                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        textViewNumber.setText(number + " " + getResources().getString(R.string.unit));
                    }
                });
                dialog.show();
            }
        });

        button_sign_in.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                userResult = realm.where(DEHUser.class)
                        .findAll();
                for (DEHUser user : userResult) {
                    userId = user.getId();
                }
                include_menu_log.setVisibility(View.GONE);
                include_menu_log_status = false;
                if (!userResult.isEmpty()) {
                    new MaterialDialog.Builder(MainActivity.this)
                            .title(R.string.deh_logout)
                            .content(userId + " , " + getString(R.string.deh_logout_detail))
                            .positiveText(R.string.confirm)
                            .negativeText(R.string.cancel)
                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog materialDialog, @NonNull DialogAction dialogAction) {
                                    realm.beginTransaction();
                                    realm.clear(DEHUser.class);
                                    realm.commitTransaction();
                                    for (DEHUser user : userResult) {
                                        userId = user.getId();
                                    }
                                    mMenu.findItem(R.id.my_site).setVisible(false);
                                    login_status = false;
                                    include_menu_log.setVisibility(View.GONE);
                                    include_menu_log_status = false;
                                    button_sign_in.setText(R.string.sign_in);
                                    choose = 2;
                                }
                            })
                            .show();
                } else {
                    MaterialDialog dialog = new MaterialDialog.Builder(MainActivity.this)
                            .title(R.string.deh_sign_in)
                            .customView(R.layout.dialog_deh_sign_in, true)
                            .positiveText(R.string.confirm)
                            .negativeText(android.R.string.cancel)
                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog materialDialog, @NonNull DialogAction dialogAction) {
                                    String account = accountInput.getText().toString();
                                    String password = passwordInput.getText().toString();
                                    if (!account.matches("") && !password.matches("")) {
                                        mType = "DEH log in";
                                        url = getResources().getString(R.string.api_dehLogIn);
                                        mServer.logIn(mType, url, account, password);
                                    } else {
                                        Toast.makeText(MainActivity.this, "帳號密碼不可為空", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            })
                            .build();

                    accountInput = (EditText) dialog.getCustomView().findViewById(R.id.account);
                    passwordInput = (EditText) dialog.getCustomView().findViewById(R.id.password);

                    // Toggling the show password CheckBox will mask or unmask the password input EditText
                    CheckBox checkbox = (CheckBox) dialog.getCustomView().findViewById(R.id.showPassword);
                    checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            passwordInput.setInputType(!isChecked ? InputType.TYPE_TEXT_VARIATION_PASSWORD : InputType.TYPE_CLASS_TEXT);
                            passwordInput.setTransformationMethod(!isChecked ? PasswordTransformationMethod.getInstance() : null);
                        }
                    });
                    dialog.show();
                }
            }
        });
    }



    public void preSetting(View v){
        final String arrayCategory[] = {"古蹟、歷史建築、聚落", "遺址", "人文景觀", "自然景觀",
                "傳統藝術", "民俗及有關文物", "古物", "食衣住行育樂", "其他"};
        if (number != 0 && distance != 0) {
            notShow = false;
            move = false;
            if(choose==1){

            }else if(search_center_status) {
                lat = center_location.latitude;
                lng = center_location.longitude;
            }else {
                if(curlocation!=null) {
                    lat = curlocation.getLatitude();
                    lng = curlocation.getLongitude();
                }else {
                    lat = 22.997241;
                    lng = 120.2216;
                }
            }
            if (v.getId() == R.id.button_search_SOI) {
                mType = "SOI";
                api = getResources().getString(R.string.api_nearbySOIs);
            } else if (v.getId() == R.id.button_search_AOI){
                if(choose==0) {
                    searchSend("2");
                    /*mSocket.emit("lng_data", String.valueOf(lng));
                    mSocket.emit("lat_data", String.valueOf(lat));
                    mSocket.emit("type_data", "2");*/
                    mSocket.emit("ios_group_id", String.valueOf(group_number));
                    mSocket.emit("ios_send_longtitude", String.valueOf(lng));
                    mSocket.emit("ios_send_latitude", String.valueOf(lat));
                    mSocket.emit("ios_send_type", "2");

                }
                mType = "AOI";
                api = getResources().getString(R.string.api_nearbyAOIs);
            } else if (v.getId() == R.id.button_search_LOI){
                if(choose==0) {
                    searchSend("1");
                    /*mSocket.emit("lng_data", String.valueOf(lng));
                    mSocket.emit("lat_data", String.valueOf(lat));
                    mSocket.emit("type_data", "1");*/
                    mSocket.emit("ios_group_id", String.valueOf(group_number));
                    mSocket.emit("ios_send_longtitude", String.valueOf(lng));
                    mSocket.emit("ios_send_latitude", String.valueOf(lat));
                    mSocket.emit("ios_send_type", "1");
                }
                Log.d("KAMO","LOICLICK");
                mType = "LOI";
                api = getResources().getString(R.string.api_nearbyLOIs);
            } else if(v.getId() == R.id.button_search_POI){
                if(choose==0) {
                    searchSend("0");
                    /*mSocket.emit("lng_data", String.valueOf(lng));
                    mSocket.emit("lat_data", String.valueOf(lat));
                    mSocket.emit("type_data", "0");//0為參數*/
                    mSocket.emit("ios_group_id", String.valueOf(group_number));
                    mSocket.emit("ios_send_longtitude", String.valueOf(lng));
                    mSocket.emit("ios_send_latitude", String.valueOf(lat));
                    mSocket.emit("ios_send_type", "0");
                }
                Log.d("KAMO","POICLICK");
                mType = "POI";
                api = getResources().getString(R.string.api_nearbyPOIs);
            } else if (v.getId() == R.id.button_my_POI) {
                mType = "MyPOI";
                // api = getResources().getString(R.string.api_userPOIs);
                api = "http://deh.csie.ncku.edu.tw:8080/api/v1/users/pois";
            } else if (v.getId() == R.id.button_my_LOI){
                mType = "MyLOI";
                api = getResources().getString(R.string.api_userLOIs);
            } else if (v.getId() == R.id.button_my_AOI){
                mType = "MyAOI";
                api = getResources().getString(R.string.api_userAOIs);
            }else if(v.getId() == R.id.button_my_SOI){
                mType = "MySOI";
                api = getResources().getString(R.string.api_userSOIs);
            }
            globalVariable.setStatus(mType);
            language = Locale.getDefault().getDisplayLanguage();
            if (language.equals("English"))
                language = "/en?";
            else if (language.equals("中文"))
                language = "/tw?";
            else if (language.equals("日本語"))
                language = "/jp?";
            String tp="",fmt = "";
            Log.d("lang", language);
            if (v.getId() == R.id.button_search_SOI || v.getId() == R.id.button_search_AOI ||
                    v.getId() == R.id.button_search_LOI || v.getId() == R.id.button_search_POI  ) {
                if(v.getId() == R.id.button_search_POI){
                    if(identifier.equals("user"))
                        api = getResources().getString(R.string.player_poi);
                    else if(identifier.equals("expert"))
                        api = getResources().getString(R.string.expert_poi);
                    else if(identifier.equals("narrator"))
                        api = getResources().getString(R.string.docent_poi);

                    if(media.equals("photo"))
                        tp = "&tp=1";
                    else if(media.equals("audio"))
                        tp = "&tp=2";
                    else if(media.equals("movie"))
                        tp = "&tp=4";
                    else if(media.equals("audio tour"))
                        tp = "&tp=8";
                    for(int i =0;i<9;i++) {
                        if(arrayCategory[i].equals("all"))
                            break;
                        if (arrayCategory[i].equals(category)) {
                            fmt = "&fmt=" + (i + 1);
                            break;
                        }
                    }
                }
                url = api + language + "lat=" + lat + "&lng=" + lng + "&dis=" + distance + "&num=" +
                        number+ tp +fmt;
            }
            if (v.getId() == R.id.button_my_POI || v.getId() == R.id.button_my_LOI
                    || v.getId() == R.id.button_my_AOI|| v.getId() == R.id.button_my_SOI) {
                url = api + "?lat=" + lat + "&lng=" + lng + "&dis=" + distance + "&num=" +
                        number; /*+ "&did=" + globalVariable.getDeviceID() +
                                                    "&appver=mini200&ulat=22.9942&ulng=120.1659"*/;
            }

            Log.d("url", url);
            Log.d("type", mType);

            if (!taskRunning) {
                if(Token==null)
                    mServer.Authorization();
                mServer.Search(mType, url);
                Log.d("KAMO","Serach");

                //fabProgressCircle.show();
                taskRunning = true;
            }
        }
        radius = distance;
        if(choose==0 || choose==2)
            mMap.clear();
        after_search_location = center_location;
        if (button_Search_range_status){
            Log.d("KAMO","draw");
            drawCircle();
        }
        mMenu.findItem(R.id.filter).setVisible(true);

        if(search_center_status){
            Log.d("KAMO","build1");
            mMap.addMarker(new MarkerOptions()
                    .position(after_search_location)
                    .title("Your search center")
                    .draggable(false)
                    .anchor(0.5f, 0.5f)
                    .icon(BitmapDescriptorFactory.fromResource(android.R.drawable.ic_menu_add)));
        } else {
            Log.d("KAMO","build2");
            if(choose==0 || choose==2)
            mMap.addMarker(new MarkerOptions()
                    .position(gps)
                    .title("Your search center")
                    .draggable(false)
                    .anchor(0.5f, 0.5f)
                    .icon(BitmapDescriptorFactory.fromResource(android.R.drawable.ic_menu_add)));
        }
        distance = (double) DEFAULT_DISTANCE;
        number = DEFAULT_NUMBER;
    }

    public void searchSend(String type){
        JSONObject send = new JSONObject();
        try {
            send.put("longtitude",String.valueOf(lng));
            send.put("latitude",String.valueOf(lat));
            send.put("id",Integer.toString(group_number));
            send.put("type",type);
        }catch(JSONException e){
        }
        Log.d("GetSearch",send.toString());
        mSocket.emit("search",send);
    }

    public void btn_Site(final View vv){
        include_my_site.setVisibility(View.GONE);
        include_my_site_staus = false;
        if (globalVariable.checkInternet()) {
            userResult = realm.where(DEHUser.class)
                    .findAll();
            for (DEHUser user : userResult) {
                userId = user.getId();
            }
            preSetting(vv);
        } else {
            Toast.makeText(MainActivity.this, getResources().getString(R.string.connect_network), Toast.LENGTH_SHORT).show();
        }
    }

    public void search(){
        button_my_fav = (Button) findViewById(R.id.button_my_fav);

        button_my_fav.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                include_search.setVisibility(View.GONE);
                include_search_status = false;
                myFavoriteAdapter = new MyFavoriteAdapter(MainActivity.this,mServer, mFavorites);
                mRecyclerView.setAdapter(myFavoriteAdapter);
                drawer.openDrawer(GravityCompat.START);
                mMenu.findItem(R.id.filter).setVisible(true);
            }
        });
    }

    public void btn_Search(final View vv){
        Log.d("KAMO","click");
        include_search.setVisibility(View.GONE);
        include_search_status = false;
        if (globalVariable.checkInternet()) {
            userResult = realm.where(DEHUser.class)
                    .findAll();
            for (DEHUser user : userResult) {
                userId = user.getId();
            }
            if (checkbox_setting_status) {
                MaterialDialog dialog = new MaterialDialog.Builder(MainActivity.this)
                        .title(R.string.search_settings)
                        .customView(R.layout.dialog_search, true)
                        .positiveText(R.string.search)
                        .negativeText(R.string.cancel)
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog materialDialog, @NonNull DialogAction dialogAction) {
                                preSetting(vv);
                            }
                        })
                        .build();

                SeekBar seekbarDistance = (SeekBar) dialog.getCustomView().findViewById(R.id.seekBar_distance);
                seekbarDistance.setProgress(DEFAULT_DISTANCE);
                distance = (double) DEFAULT_DISTANCE;
                final TextView textViewDistance = (TextView) dialog.getCustomView().findViewById(R.id.text_distance);
                textViewDistance.setText(new DecimalFormat("#0.0").format(DEFAULT_DISTANCE / 1000) + " " + getString(R.string.kilometer));
                seekbarDistance.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        distance = (double) progress;
                        DEFAULT_DISTANCE = progress;
                    }
                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                    }
                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        textViewDistance.setText(new DecimalFormat("#0.0").format(distance / 1000) + " " + getString(R.string.kilometer));
                    }
                });

                SeekBar seekbarNumber = (SeekBar) dialog.getCustomView().findViewById(R.id.seekBar_number);
                seekbarNumber.setProgress(DEFAULT_NUMBER);
                number = DEFAULT_NUMBER;
                final TextView textViewNumber = (TextView) dialog.getCustomView().findViewById(R.id.text_number);
                textViewNumber.setText(seekbarNumber.getProgress() + " " + getResources().getString(R.string.unit));

                seekbarNumber.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        number = progress;
                        DEFAULT_NUMBER = progress;
                    }
                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                    }
                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        textViewNumber.setText(number + " " + getResources().getString(R.string.unit));
                    }
                });
                dialog.show();
            } else {
                Log.d("KAKMO","here");
                preSetting(vv);
            }


        } else {
            Toast.makeText(MainActivity.this, getResources().getString(R.string.connect_network), Toast.LENGTH_SHORT).show();
        }
    }

    public void init() {

        context = this;
        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        //mLocationListener = new MapActivity.MyLocationListener();
        openGPS(context);
        poiModel = (POIModel) getIntent().getParcelableExtra("Single-POI");

        setUpMapIfNeeded();



        //globalVariable.createDrawer(navigationDrawer);

        //fabProgressCircle = (FABProgressCircle) findViewById(R.id.fabProgressCircle);
        //fabProgressCircle.attachListener(this);
        //fab = (FloatingActionButton) findViewById(R.id.fab);

        /*showcaseView = new ShowcaseView.Builder(this)
                .setTarget(new ViewTarget(findViewById(R.id.fab)))
                .setContentTitle(R.string.search)
                .setContentText(R.string.description_search)
                .singleShot(42)
                .build();*/
    }
    public void exitDEH() {
        finish();
    }

    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop()...");

        try {
            unregisterReceiver(serverReceiver);
        } catch (Exception e) {
        }
        try {
            unbindService(serverConnection);
        } catch (Exception e) {

        }


        //unregisterReceiver(mBroadcastReceiver);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume()...");
        //if(!language.equals(Locale.getDefault().getDisplayLanguage()))
        //   re
        RealmResults<MyFavorite> myFavorites = realm.where(MyFavorite.class)
                .findAll();

        Log.d("My favorite", myFavorites.toString());
        mFavorites = new ArrayList<>();

        for (MyFavorite favorite: myFavorites) {
            if (favorite.isValid())
                mFavorites.add(favorite);
        }
        Type = " ";
        if(from_where){
            Type = "FAV";
            if(globalVariable.isFavoriteRemove()) {
                v.setVisibility(View.INVISIBLE);
                marker.remove();
                myFavoriteAdapter = new MyFavoriteAdapter(MainActivity.this, mServer, mFavorites);
                mRecyclerView.setAdapter(myFavoriteAdapter);
                globalVariable.setFavoriteRemove(false);
            } else {
                if(Type.equals("POI" ) ) {
                    //RelativeLayout v = (RelativeLayout) findViewById(R.id.bubble);
                    v.setVisibility(View.VISIBLE);
                    marker = mMap.addMarker(new MarkerOptions().position(new LatLng(pm.getPOILat(), pm.getPOILong())));
                }
            }
        } else if(Type.equals("POI" ) ) {
            RelativeLayout v = (RelativeLayout) findViewById(R.id.bubble);
            v.setVisibility(View.VISIBLE);
            marker = mMap.addMarker(new MarkerOptions().position(new LatLng(pm.getPOILat(), pm.getPOILong())));

        }

        AppEventsLogger.activateApp(this);

        startService();
        /*if(Type.equals("POI" ) ) {
            //RelativeLayout v = (RelativeLayout) findViewById(R.id.bubble);
            v.setVisibility(View.VISIBLE);
            marker = mMap.addMarker(new MarkerOptions().position(new LatLng(mServer.getPOImodel().getPOILat(), mServer.getPOImodel().getPOILong())));
        }*/
//        Log.d(TAG, "refresh_drawer : " + getIntent().getBooleanExtra("refresh_drawer", false));
//
//        if (getIntent().getBooleanExtra("refresh_drawer", false) == true) {
//            globalVariable.createDrawer(navigationDrawer);
//
//        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        StoreUserPreference();
        AppEventsLogger.deactivateApp(this);
        //stopProxyService();
    }

    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart()...");

        Log.d(TAG, "refresh_drawer : " + getIntent().getBooleanExtra("refresh_drawer", false));

        MSN.identity = Preset.loadPreferences(getApplicationContext());

            /*if (viewPager.getCurrentItem() == PAGE_SITE) {
                Fragment page = getSupportFragmentManager().findFragmentByTag("android:switcher:" + R.id.viewpager + ":" + viewPager.getCurrentItem());
                if (mClient != null)
                    ((SiteFragment) page).updateMemberSites(mClient);
            }
*/
        //IntentFilter intentFilter = new IntentFilter();

        /*intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        intentFilter.addAction(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION);
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(mBroadcastReceiver, intentFilter);*/
        /** V2 End **/
    }

    protected void onDestroy() {
        StoreUserPreference();
        super.onDestroy();
        HttpResponseCache cache = HttpResponseCache.getInstalled();
        if (cache != null) {
            cache.flush();
        }
    }

    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            ExitDialog dialog = new ExitDialog();
            dialog.show(getFragmentManager(), "exitDialog");
        }
    }

    ServiceConnection serverConnection = new ServiceConnection() {

        public void onServiceDisconnected(ComponentName name) {
//            Toast.makeText(MainActivity.this, "Service is disconnected", Toast.LENGTH_SHORT).show();
            mServer = null;
        }

        public void onServiceConnected(ComponentName name, IBinder service) {
//            Toast.makeText(MainActivity.this, "Service is connected", Toast.LENGTH_SHORT).show();
            ProxyService.ProxyBinder mLocalBinder = (ProxyService.ProxyBinder) service;
            mServer = mLocalBinder.getProxyInstance();
            Log.d("service", "Service is connected");
            if(Token==null)
                mServer.Authorization();
            if (globalVariable.checkInternet()) {
                mServer.retrieveIp();
            }
        }
    };

    public void searchSite(String type, String url) {
        mServer.Search(type, url);
    }

    /*public void onIdentityChanged() {
        if (viewPager.getCurrentItem() == 0) {
            Log.d(TAG, "onIdentityChanged()...");
            Fragment page = getSupportFragmentManager().findFragmentByTag("android:switcher:" + R.id.viewpager + ":" + viewPager.getCurrentItem());
        }


        if (Preset.loadPreferences(getApplicationContext()) == IDENTITY.PROXY) {
            if (Preset.loadModePreference(getApplicationContext()) == IDENTITY.MODE_INDIVIDIUAL) {
                mServer.hangService();
            } else {
                mServer.restartService();
            }
        }
    }*/
    public void startService() {
        startProxyService();
        //startService(new Intent(MainActivity.this, CacheService.class));

    }
    public void startProxyService() {
//     onDisConnectMessage(false);

        try {
            unregisterReceiver(serverReceiver);
        } catch (Exception e) {
            e.printStackTrace();
        }
        onRegisteProxyReceiver();

        Intent intent = new Intent(MainActivity.this, ProxyService.class);
        startService(intent);
        Intent intent1 = new Intent(MainActivity.this, ProxyService.class);
        bindService(intent1, serverConnection, BIND_AUTO_CREATE);
    }


    public void stopProxyService() {
        try {
            if (mServer != null) mServer.stopProxyService();
            Intent intent = new Intent(MainActivity.this, ProxyService.class);
            stopService(intent);
            stopService(new Intent(MainActivity.this, CacheService.class));
            unbindService(serverConnection);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            unregisterReceiver(serverReceiver);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void CheckButtonStatus(){
        if (change_location_status){
            button_change_location.setVisibility(View.VISIBLE);
        } else {
            button_change_location.setVisibility(View.GONE);
        }
        if (search_address_status){
            button_search_address.setVisibility(View.VISIBLE);
        } else {
            button_search_address.setVisibility(View.GONE);
        }
        if (search_range_status){
            button_search_range.setVisibility(View.VISIBLE);
        } else {
            button_search_range.setVisibility(View.GONE);
        }
        if (search_center_status){
            button_search_center.setVisibility(View.VISIBLE);
        } else {
            button_search_center.setVisibility(View.GONE);
        }

    }

    private void GetUserPreference(){
        SharedPreferences settings = getSharedPreferences("PREF_DATA", 0);
        change_location = settings.getBoolean("cl", change_location);
        search_address  = settings.getBoolean("sa", search_address);
        search_range = settings.getBoolean("sr", search_range);
        search_center = settings.getBoolean("sc", search_center);
        checkBox_setting = settings.getBoolean("cs", checkBox_setting);
        change_location_status = settings.getBoolean("cls", change_location_status);
        search_address_status  = settings.getBoolean("sas", search_address_status);
        search_range_status = settings.getBoolean("srs", search_range_status);
        search_center_status = settings.getBoolean("scs", search_center_status);
        checkbox_setting_status = settings.getBoolean("css", checkbox_setting_status);
        DEFAULT_DISTANCE = settings.getInt("DD", DEFAULT_DISTANCE);
        DEFAULT_NUMBER = settings.getInt("DN", DEFAULT_NUMBER);


    }
    private void StoreUserPreference(){
        SharedPreferences settings = getSharedPreferences("PREF_DATA", 0);
        settings.edit().putBoolean("cl", change_location).apply();
        settings.edit().putBoolean("sa", search_address).apply();
        settings.edit().putBoolean("sr", search_range).apply();
        settings.edit().putBoolean("sc", search_center).apply();
        settings.edit().putBoolean("cs", checkBox_setting).apply();
        settings.edit().putBoolean("cls", change_location_status).apply();
        settings.edit().putBoolean("sas", search_address_status).apply();
        settings.edit().putBoolean("srs", search_range_status).apply();
        settings.edit().putBoolean("scs", search_center_status).apply();
        settings.edit().putBoolean("css", checkbox_setting_status).apply();
        settings.edit().putInt("DD", DEFAULT_DISTANCE).apply();
        settings.edit().putInt("DN", DEFAULT_NUMBER).apply();
        settings.edit().putBoolean("login", login_status).apply();
    }

    public void onRegisteProxyReceiver() {
        serverReceiver = new ServerReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ProxyService.GETLOGIN_ACTION);
        intentFilter.addAction(ProxyService.GETPOI_ACTION);
        intentFilter.addAction(ProxyService.GETLOI_ACTION);
        intentFilter.addAction(ProxyService.GETAOI_ACTION);
        intentFilter.addAction(ProxyService.GETSOI_ACTION);
        intentFilter.addAction(ProxyService.FILE_COMPLETE__ACTION);
        intentFilter.addAction(ProxyService.POI_GOGO);
        intentFilter.addAction(ProxyService.GETTOKEN);
        intentFilter.addAction(ProxyService.LOI_GOGO);
        intentFilter.addAction(ProxyService.AOI_GOGO);
        intentFilter.addAction(ProxyService.SOI_GOGO);
        intentFilter.addAction(ProxyService.FAV_GOGO);
        intentFilter.addAction(ProxyService.Exception_GOGO);
        registerReceiver(serverReceiver, intentFilter);
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

    /*public void onRegisteMemberReceiver() {
        clientReceiver = new ClientReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(MemberService.CONNECT_ACTION);
        intentFilter.addAction(MemberService.VIDEO_START_ACTION);
        intentFilter.addAction(MemberService.CONNECT_TO_PROXY);
        intentFilter.addAction(MemberService.DISCONNECT_FROM_PROXY);
        registerReceiver(clientReceiver, intentFilter);
    }*/


    public class ServerReceiver extends BroadcastReceiver {

        public ServerReceiver() {
        }

        public void onReceive(Context context, Intent intent) {
            if (ProxyService.GETLOGIN_ACTION.equals(intent.getAction())) {

                if (mServer.getLogInStatus()) {
                    hint = getResources().getString(R.string.login_success);
                    mMenu.findItem(R.id.my_site).setVisible(true);
                    login_status = true;
                    include_menu_log.setVisibility(View.GONE);
                    include_menu_log_status = false;
                    button_sign_in.setText(R.string.sign_out);
                    //dialog_status();
                    if(group_number != -1)
                        choose = 0;
                    Log.d("KamoGroupttt",Integer.toString(choose)+" "+Integer.toString(group_number));
                } else
                    hint = getResources().getString(R.string.login_failure);

                Snackbar.make(coordinatorLayout, hint, Snackbar.LENGTH_SHORT).show();
            }

            //if (viewPager.getCurrentItem() == PAGE_SITE) {
            taskRunning = false;
            //fabProgressCircle.beginFinalAnimation();
            boolean found = false;
            // if(page==null)
            //    Log.d(TAG,"fail!!!!!!!!!!!!!!!!!!");
            //.findFragmentByTag("android:switcher:" + R.id.viewpager + ":" + viewPager.getCurrentItem());
            if (ProxyService.GETPOI_ACTION.equals(intent.getAction())) {
                Log.d("KAMO","Pro");
                updateSites(mServer,0,mType);
                if(mType.equals("POI")) {
                    Log.d("KAMO","Pro0in1");
                    found = filtering(identifier, media, category, found);
                }
                else if(mServer.getPOIList().size()!=0){
                    Log.d("KAMO","Pro0in2");
                    found = true;
                    hint = getString(R.string.find) + " " + mServer.getPOIList().size()+ " " +getString(R.string.lines);
                }else {
                    found = false;
                    hint = getResources().getString(R.string.not_found);
                }
                ProxyService.type = 0;
                //mMenu.findItem(R.id.filter).setVisible(true);

            } else if (ProxyService.GETLOI_ACTION.equals(intent.getAction())) {
                Log.d("KAMO","Pro1");
                updateSites(mServer, 1, mType);
                ProxyService.type = 1;
                //mMenu.findItem(R.id.filter).setVisible(false);
                if(mServer.getLOIList().size()!=0) {
                    hint = getString(R.string.find) + " " + mServer.getLOIList().size() + " " + getString(R.string.lines);
                    found = true;
                }else{
                    found = false;
                    hint = getResources().getString(R.string.not_found_LOI);
                }

            } else if (ProxyService.GETAOI_ACTION.equals(intent.getAction())) {
                updateSites(mServer, 2, mType);
                ProxyService.type = 2;
                //mMenu.findItem(R.id.filter).setVisible(false);
                if(mServer.getAOIList().size()!=0) {
                    found = true;
                    hint = getString(R.string.find) + " " + mServer.getAOIList().size() + " " + getString(R.string.areas);
                }else{
                    found = false;
                    hint = getResources().getString(R.string.not_found_AOI);
                }
            }else if (ProxyService.GETSOI_ACTION.equals(intent.getAction())){
                updateSites(mServer,3,mType);
                ProxyService.type = 3;
                if(mServer.getSOIList().size()!=0){
                    found = true;
                    hint = getString(R.string.find)+ " " + mServer.getSOIList().size() + " " + getString(R.string.story);
                }else{
                    found = false;
                    hint = getResources().getString(R.string.not_found_SOI);
                }
            }else if (ProxyService.POI_GOGO.equals(intent.getAction())||ProxyService.LOI_GOGO.equals(intent.getAction())||ProxyService.AOI_GOGO.equals(intent.getAction())||ProxyService.SOI_GOGO.equals(intent.getAction()) || ProxyService.FAV_GOGO.equals(intent.getAction())) {
                try {
                    if(ProxyService.POI_GOGO.equals(intent.getAction())) {
                        //mSocket.emit("position",poiAdapter.getposition());
                        drawpoint(1);
                    }
                    else if(ProxyService.LOI_GOGO.equals(intent.getAction())) {
                        //mSocket.emit("LOI_position",loiAdapter.getposition());
                        Log.d("KAMO","LOI_to_map");
                        drawpoint(2);
                    }
                    else if(ProxyService.AOI_GOGO.equals(intent.getAction())) {
                        //mSocket.emit("AOI_position",aoiAdapter.getposition());
                        Log.d("KAMO","AOI_to_map");
                        drawpoint(3);
                    }
                    else if(ProxyService.SOI_GOGO.equals(intent.getAction()))
                        drawpoint(4);
                    else
                        drawpoint(0);
                    fromPoint = true;
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }else if(ProxyService.GETTOKEN.equals(intent.getAction())){
                Token = mServer.getToken();
                globalVariable.setToken(Token);
                Log.d("Token",""+Token);
            }
            if(ProxyService.Exception_GOGO.equals(intent.getAction())){
                if(!globalVariable.checkInternet())
                    Toast.makeText(MainActivity.this, getResources().getString(R.string.connect_network), Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(MainActivity.this,getResources().getString(R.string.server_busy), Toast.LENGTH_SHORT).show();
            }
            if(!ProxyService.GETTOKEN.equals(intent.getAction())) {
                Type = mServer.getType();
            }
            if(ProxyService.GETPOI_ACTION.equals(intent.getAction())||ProxyService.GETLOI_ACTION.equals(intent.getAction())||ProxyService.GETAOI_ACTION.equals(intent.getAction())||ProxyService.GETSOI_ACTION.equals(intent.getAction())){
                if(choose==0||choose==2)
                    Toast.makeText(MainActivity.this,hint,Toast.LENGTH_SHORT).show();
                if(search_center_status){
                    if(!notShow&&found&&choose==0||choose==2){
                        drawer.openDrawer(GravityCompat.START);
                    }
                } else {
                    if(found&&choose==0||choose==2)
                        drawer.openDrawer(GravityCompat.START);
                }
            }

        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        mMenu = menu;
        //menu.findItem(R.id.filter).setVisible(false);
        SharedPreferences settings = getSharedPreferences("PREF_DATA", 0) ;
        login_status = settings.getBoolean("login",login_status);
        //menu.findItem(R.id.filter).setVisible(false);
        if(login_status) {
            mMenu.findItem(R.id.my_site).setVisible(true);
            button_sign_in.setText(R.string.sign_out);
        }
        else
            menu.findItem(R.id.my_site).setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        v = (RelativeLayout) findViewById(R.id.bubble);
        RelativeLayout v2 = (RelativeLayout) findViewById(R.id.bubble2);
        v.setVisibility(View.INVISIBLE);
        v2.setVisibility(View.INVISIBLE);
        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
            return true;
        }

        if(id == R.id.filter){
            include_search.setVisibility(View.GONE);
            include_menu_log.setVisibility(View.GONE);
            include_my_site.setVisibility(View.GONE);
            MaterialDialog dialog = new MaterialDialog.Builder(MainActivity.this)
                    .title(R.string.notice)
                    .customView(R.layout.dialog_filter_comfirm, true)
                    .widgetColor(getResources().getColor(R.color.colorPrimary))
                    .positiveText(R.string.confirm)
                    .negativeText(R.string.cancel)
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        public void onClick(@NonNull MaterialDialog materialDialog,@NonNull DialogAction dialogAction) {
                            FilterDialog dialog = new FilterDialog();
                            dialog.show(getFragmentManager(), "filterDialog");
                            include_menu_log.setVisibility(View.GONE);
                            include_menu_log_status = false;
                        }
                    })
                    .build();
            dialog.show();
        }

        if(id == R.id.menu_log){

            if(include_menu_log_status && !include_my_site_staus && !include_search_status){
                include_menu_log.setVisibility(View.GONE);
                include_menu_log_status = false;
            } else if(include_my_site_staus && !include_menu_log_status && !include_search_status) {
                include_my_site.setVisibility(View.GONE);
                include_my_site_staus = false;
                include_menu_log.setVisibility(View.VISIBLE);
                include_menu_log_status = true;
            } else if(!include_menu_log_status && !include_my_site_staus && include_search_status){
                include_search.setVisibility(View.GONE);
                include_search_status = false;
                include_menu_log.setVisibility(View.VISIBLE);
                include_menu_log_status = true;
            } else {
                include_menu_log.setVisibility(View.VISIBLE);
                include_menu_log_status = true;
            }
        }
        if(id == R.id.my_site){
            if(include_my_site_staus && !include_menu_log_status && !include_search_status){
                include_my_site.setVisibility(View.GONE);
                include_my_site_staus = false;
            } else if(include_menu_log_status && !include_my_site_staus && !include_search_status) {
                include_menu_log.setVisibility(View.GONE);
                include_menu_log_status = false;
                include_my_site.setVisibility(View.VISIBLE);
                include_my_site_staus = true;
            } else if(!include_my_site_staus && !include_menu_log_status && include_search_status){
                include_search.setVisibility(View.GONE);
                include_search_status = false;
                include_my_site.setVisibility(View.VISIBLE);
                include_my_site_staus = true;
            } else {
                include_my_site.setVisibility(View.VISIBLE);
                include_my_site_staus = true;
            }
        }
        if (id == R.id.fab){
            if(include_search_status && !include_menu_log_status && !include_my_site_staus){
                include_search.setVisibility(View.GONE);
                include_search_status = false;
            } else if(!include_menu_log_status && include_my_site_staus && !include_search_status){
                include_my_site.setVisibility(View.GONE);
                include_my_site_staus = false;
                include_search.setVisibility(View.VISIBLE);
                include_search_status = true;
            } else if(include_menu_log_status && !include_my_site_staus && !include_search_status){
                include_menu_log.setVisibility(View.GONE);
                include_menu_log_status = false;
                include_search.setVisibility(View.VISIBLE);
                include_search_status = true;
            } else {
                include_search.setVisibility(View.VISIBLE);
                include_search_status = true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void drawCircle() {
        CircleOptions options = new CircleOptions();
        options.center(after_search_location);
        options.radius(radius);
        options.strokeWidth(5);
        options.strokeColor(Color.BLUE);
        options.fillColor(Color.argb(100, 0, 0, 255));
        options.zIndex(3);

        mMap.addCircle(options);
    }

    public void startFilter(String identifier, String media, String category) {
        this.identifier = identifier;
        this.media = media;
        this.category = category;
        hint = null;
        Boolean found = false;
        if(search_center_status){
            if(move) {
                notShow = true;
            }
        } else {
            notShow = false;
        }
        if(!move && !notShow&&mType.equals("POI")) {
            found = filtering(identifier,media,category,found);
            if(found)
                drawer.openDrawer(GravityCompat.START);
        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }


    public void openGPS(Context context) {
        boolean gps = mLocationManager
                .isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean network = mLocationManager
                .isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        //Toast.makeText(context, "GPS : " + gps + ", Network : " + network,
        //Toast.LENGTH_SHORT).show();
        if (gps || network) {
            return;
        } else {
            // 開啟手動GPS設定畫面
            startActivityForResult(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS), 0);

        }
    }

    public void onClick(View view) {
        openGPS(context);
    }

    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.google_map);
            mapFragment.getMapAsync(this);

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                        this,
                        new String[] {ACCESS_FINE_LOCATION,
                                ACCESS_COARSE_LOCATION},1
                );


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

    public void onMapReady(final GoogleMap googleMap) {
        mMap = googleMap;
        if (mMap != null) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
                mMap.getUiSettings().setAllGesturesEnabled(true);
                mMap.getUiSettings().setZoomControlsEnabled(true);
                mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {

                    @Override
                    public boolean onMarkerClick(Marker marker) {
                        flag = false;
                        move = false;
                        if (marker.getTitle().equals("Your search center"))
                            return true;
                        marker.hideInfoWindow();
                        Button b1 = (Button) findViewById(R.id.poi_inform2);
                        if (!mServer.getType().equals("POI")) {
                            lm = mServer.getLOImodel().getmPOIs().get(Integer.parseInt(marker.getSnippet()));
                            if (mServer.getType().equals("SOI")) {
                                if (lm.getType().equals("LOI")) {
                                    b1.setText("景線資訊");
                                } else if (lm.getType().equals("AOI")) {

                                    b1.setText("景區資訊");
                                } else
                                    b1.setText("景點資訊");
                            }
                            RelativeLayout v2 = (RelativeLayout) findViewById(R.id.bubble2);
                            v2.setVisibility(View.VISIBLE);
                            TextView t1 = (TextView) findViewById(R.id.title1);
                            t1.setText(mServer.getLOImodel().getLOIName());
                            TextView t2 = (TextView) findViewById(R.id.title2);
                            t2.setText(marker.getTitle());
                        } else {
                            RelativeLayout v = (RelativeLayout) findViewById(R.id.bubble);
                            v.setVisibility(View.VISIBLE);
                            TextView t = (TextView) findViewById(R.id.title);
                            t.setVisibility(View.VISIBLE);
                            t.setText(mServer.getPOImodel().getPOIName());
                        }
                        // marker.showInfoWindow();
                        CameraPosition camerPosition = new CameraPosition.Builder()
                                .target(new LatLng(marker.getPosition().latitude, marker.getPosition().longitude))
                                .zoom(googleMap.getCameraPosition().zoom)
                                .build();
                        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(camerPosition));
                    /*mMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
                        @Override
                        public void onCameraChange(CameraPosition cameraPosition) {
                            center_location = cameraPosition.target;

                        }
                    });*/
                        return true;

                    }

                });
                mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                    @Override
                    public void onMapClick(LatLng latLng) {
                        View v = findViewById(R.id.bubble2);
                        v.setVisibility(View.GONE);
                        View v2 = findViewById(R.id.bubble);
                        v2.setVisibility(View.GONE);
                        include_search.setVisibility(View.GONE);
                        include_search_status = false;
                        include_menu_log.setVisibility(View.GONE);
                        include_menu_log_status = false;
                        include_my_site.setVisibility(View.GONE);
                        include_my_site_staus = false;
                    }
                });

                mLocationListener = new LocationListener() {
                    @Override
                    public void onLocationChanged(Location location) {
                        if (location != null) {
                        }
                    }

                    @Override
                    public void onProviderDisabled(String provider) {
                        startActivityForResult(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS), 0);
                    }

                    @Override
                    public void onProviderEnabled(String provider) {
                    }

                    @Override
                    public void onStatusChanged(String provider, int status, Bundle extras) {
                    }
                };
                if (mLocationManager
                        .isProviderEnabled(LocationManager.GPS_PROVIDER)) {

                    mLocationManager.requestLocationUpdates(
                            LocationManager.GPS_PROVIDER,
                            60000,
                            10, mLocationListener);
                    curlocation = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    Log.d("GPS", "" + curlocation);
                }
                if (mLocationManager
                        .isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                    //mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
                    mLocationManager.requestLocationUpdates(
                            LocationManager.NETWORK_PROVIDER,
                            60000,
                            10, mLocationListener);
                    curlocation = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    Log.d("network", "" + curlocation);
                }
                if (curlocation != null)
                    gps = new LatLng(curlocation.getLatitude(), curlocation.getLongitude());
                else
                    gps = new LatLng(22.997241, 120.2216);

                button_search_range.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked) {
                            if (after_search_location == null) {
                                Toast.makeText(MainActivity.this, "請搜尋後再試", Toast.LENGTH_SHORT).show();
                                button_search_range.setChecked(false);
                            } else {
                                drawCircle();
                                button_Search_range_status = true;
                            }
                        } else {
                            mMap.clear();
                            button_Search_range_status = false;
                            mMap.addMarker(new MarkerOptions()
                                    .position(after_search_location)
                                    .title("center")
                                    .draggable(false)
                                    .anchor(0.5f, 0.5f)
                                    .icon(BitmapDescriptorFactory.fromResource(android.R.drawable.ic_menu_add)))
                            ;
                        }
                    }

                });


                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(gps)
                        .zoom(14)
                        .build();
                mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                mMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
                    @Override
                    public void onCameraChange(CameraPosition cameraPosition) {
                        center_location = cameraPosition.target;

                        if (fromPoint) {
                            move = false;
                            fromPoint = false;
                        } else
                            move = true;

                        include_search.setVisibility(View.GONE);
                        include_menu_log.setVisibility(View.GONE);
                        include_my_site.setVisibility(View.GONE);
                        if (!flag) {
                            flag = true;
                        } else if (flag) {
                            View v = findViewById(R.id.bubble2);
                            v.setVisibility(View.GONE);
                            View v2 = findViewById(R.id.bubble);
                            v2.setVisibility(View.GONE);
                        }
                    }
                });
            }
        }

    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        //updateTable();
        switch(requestCode) {
            case 1:
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                    if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2);

                    }
                    onMapReady(mMap);
                }
                else {
                    Toast.makeText(this, "請開啟定位權限", Toast.LENGTH_LONG).show();

                }
                break;


        }
    }
    /*private void updateTable() {
        location.setText(String.valueOf(canAccessLocation()));
        camera.setText(String.valueOf(canAccessCamera()));
        internet.setText(String.valueOf(hasPermission(Manifest.permission.INTERNET)));
        contacts.setText(String.valueOf(canAccessContacts()));
        storage.setText(String.valueOf(hasPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)));
    }*/

    public void updateSites(ProxyService service, int type, String status) {
            switch (type) {
                case 0:
                    poiAdapter = new POIAdapter(this, service, status);
                    mRecyclerView.setAdapter(poiAdapter);
                    if(choose==1)
                        mRecyclerView.setVisibility(View.INVISIBLE);
                    else
                        mRecyclerView.setVisibility(View.VISIBLE);
                    break;
                case 1:
                    Log.d("KAMO","LOILeft");
                    loiAdapter = new LOIAdapter(this, service.getLOIList(), service);
                    mRecyclerView.setAdapter(loiAdapter);
                    if(choose==1)
                        mRecyclerView.setVisibility(View.INVISIBLE);
                    else
                        mRecyclerView.setVisibility(View.VISIBLE);
                    break;
                case 2:
                    aoiAdapter = new AOIAdapter(this, service.getAOIList(), service);
                    mRecyclerView.setAdapter(aoiAdapter);
                    break;
                case 3:
                    soiAdapter = new SOIAdapter(this, service.getSOIList(), service);
                    mRecyclerView.setAdapter(soiAdapter);
                    break;
                case 4:
                    myFavoriteAdapter = new MyFavoriteAdapter(this, service, mFavorites);
                    mRecyclerView.setAdapter(myFavoriteAdapter);
                    break;
            }
    }
    public void updatePOI(ArrayList<POIModel> poiModels) {
        ArrayList<POIModel> NewPm = poiAdapter.animateTo(poiModels);
        POIAdapter pA = new POIAdapter(this,NewPm,mServer);
        mRecyclerView.scrollToPosition(0);
        mRecyclerView.setAdapter(pA);
    }



    public void resetList(ProxyService service, String status,String token){
        poiAdapter = new POIAdapter(this, service, status);
    }
    public void connect(String data){

        POIModel qm = new POIModel();
        try {
            JSONObject mJson = new JSONObject(data);
            qm.setJsonObject(mJson);
            qm.setPOIId(mJson.getString("POI_id"));
            qm.setPOIName(mJson.getString("POI_title"));
            double temp = Double.parseDouble(mJson.getString("distance"));
            BigDecimal b = new BigDecimal(temp);
            temp = b.setScale(0,BigDecimal.ROUND_HALF_UP).doubleValue();
            qm.setPOIDistance(String.valueOf(temp)+".0");
            qm.setPOISubject(mJson.getString("subject"));
            qm.setPOIType1(mJson.getString("format"));
            qm.setPOIAddress(mJson.getString("POI_address"));
            qm.setPOIInfo(mJson.getString("POI_description"));
            qm.setPOILat(Double.parseDouble(mJson.getString("latitude")));
            qm.setPOILong(Double.parseDouble(mJson.getString("longitude")));
            qm.setIdentifier(mJson.getString("identifier"));
            qm.setOpen(mJson.getString("open"));
            ArrayList<String> temp1 = new ArrayList<String>();
            temp1.add(mJson.getString("keyword1"));
            qm.setPOIKeywords(temp1);
            ArrayList<String> temp2 = new ArrayList<String>();
            ArrayList<String> url = new ArrayList<String>();
            ArrayList<String> pic = new ArrayList<String>();
            ArrayList<String> audio = new ArrayList<String>();
            ArrayList<String> movie = new ArrayList<String>();
            ArrayList<String> AudioTours = new ArrayList<String>();
            Log.d("KAMO","hello");
            if (!mJson.isNull("media_set")) {
                for (int i = 0; i < mJson.getJSONArray("media_set").length(); i++) {

                    JSONObject temp3 = mJson.getJSONArray("media_set").getJSONObject(i);
                    switch (Integer.parseInt(temp3.getString("media_format"))) {
                        case 1:
                            temp2.add("photo");
                            url.add(temp3.getString("media_url"));
                            pic.add(temp3.getString("media_url"));
                            break;
                        case 2:
                            temp2.add("audio");
                            url.add(temp3.getString("media_url"));
                            audio.add(temp3.getString("media_url"));
                            break;
                        case 4:
                            temp2.add("movie");
                            url.add(temp3.getString("media_url"));
                            movie.add(temp3.getString("media_url"));
                            break;
                    }
                }
            }
            qm.setMedia(temp2);
            qm.setUrl(url);
            qm.setPOIPics(pic);
            qm.setPOIAudios(audio);
            qm.setPOIMovies(movie);
            qm.setPOIAudioTours(AudioTours);
        }catch (Throwable t) {
            Log.d("KAMO","QQ");
        }

        Intent intent = new Intent(activity, POIActivity.class);
        Bundle bundle = new Bundle();
        bundle.putParcelable("POI-Content", qm);
        bundle.putString("type", "Normal");
        intent.putExtras(bundle);
        startActivity(intent);
    }
    public void drawpoint(final int x) throws IOException, InterruptedException {
        Log.d("KAMO","drawpoint");
        mMap.clear();
        flag = false;
        if (drawer.isDrawerOpen(GravityCompat.START))
            drawer.closeDrawer(GravityCompat.START);
        RelativeLayout v2 = (RelativeLayout) findViewById(R.id.bubble2);
        v = (RelativeLayout) findViewById(R.id.bubble);
        v.setVisibility(View.INVISIBLE);
        v2.setVisibility(View.INVISIBLE);
        TextView t = (TextView) findViewById(R.id.title);
        FacebookSdk.sdkInitialize(getApplicationContext());
        final CallbackManager callbackManager = CallbackManager.Factory.create();
        final ShareDialog shareDialog = new ShareDialog(this);
        if (x==1) {
            marker = mMap.addMarker(new MarkerOptions().title(" ")
                    .position(new LatLng(mServer.getPOImodel().getPOILat(), mServer.getPOImodel().getPOILong())));
            v.setVisibility(View.VISIBLE);
            t.setVisibility(View.VISIBLE);
            t.setText(mServer.getPOImodel().getPOIName());
            pm = poiAdapter.getPOIList().get(poiAdapter.getposition());
            Button inform = (Button) findViewById(R.id.poi_inform);
            String lng = Locale.getDefault().getDisplayLanguage();

            inform.setOnClickListener(new Button.OnClickListener() {
                @Override
                public void onClick(View view) {
                    from_where = false;
                    Intent intent = new Intent(view.getContext(), POIActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putParcelable("POI-Content", pm);
                    bundle.putString("type", "Normal");
                    bundle.putString("flag", "POI");
                    intent.putExtras(bundle);
                    view.getContext().startActivity(intent);
                }
            });
            Button share = (Button) findViewById(R.id.poi_share);
            share.setOnClickListener(new Button.OnClickListener() {
                @Override
                public void onClick(View view) {
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
                                .setContentTitle(mServer.getPOImodel().getPOIName())
                                .setContentDescription(mServer.getPOImodel().getPOIAddress())
                                .setContentUrl(Uri.parse("http://deh.csie.ncku.edu.tw/poi_detail/"+mServer.getPOImodel().getPOIId()))
                                .build();
                        shareDialog.show(linkContent);
                    }

                }
            });
            Button guide = (Button) findViewById(R.id.poi_guide);

            if(lng.equals("English")) {
                inform.setTextSize(8);
                guide.setTextSize(9);
            }
            guide.setOnClickListener(new Button.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String from = "saddr=" + curlocation.getLatitude() + "," + curlocation.getLongitude();
                    String to = "daddr=" + mServer.getPOImodel().getPOILat() + "," + mServer.getPOImodel().getPOILong();
                    Log.d("To:", to);
                    String uri = "https://maps.google.com/maps?f=d&" + from + "&" + to + "&hl=tw";
                    Log.d("Uri", uri);
                    Uri i = Uri.parse(uri);
                    Intent intent = new Intent(android.content.Intent.ACTION_VIEW, i);
                    //intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
                    startActivity(intent);
                }
            });

            CameraPosition camerPosition = new CameraPosition.Builder()
                    .target(new LatLng(mServer.getPOImodel().getPOILat(), mServer.getPOImodel().getPOILong()))
                    .zoom(14)
                    .build();
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(camerPosition));

        }
        else if(x>=2){
            final ArrayList<LOISequenceModel> lois;
            if(x==2)
                lois = mServer.getLOImodel().getmPOIs();
            else if(x==3)
                lois = mServer.getAOImodel().getmPOIs();
            else
                lois = mServer.getSOImodel().getmPOIs();
            if(lois.size()==0)
                Toast.makeText(MainActivity.this,"No Data", Toast.LENGTH_SHORT).show();
            else {
                for (int i = 0; i < lois.size(); i++) {
                    lm = lois.get(i);
                    if (mServer.getType().equals("LOI")) {
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
                        v2.setVisibility(View.VISIBLE);
                        TextView t1 = (TextView) findViewById(R.id.title1);
                        t1.setText(mServer.getLOImodel().getLOIName());
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
                if (!mServer.getType().equals("POI")) {
                    Log.d("KAMO","getlm");
                    lm = lois.get(0);
                }
                if (mServer.getType().equals("SOI")) {
                    if (lm.getType().equals("AOI")) {
                        b1.setText(R.string.aoi_info);
                    } else if (lm.getType().equals("POI")) {
                        b1.setText(R.string.poi_info);
                    }
                    else if (lm.getType().equals("LOI"))
                        b1.setText(R.string.loi_info);
                }

                b1.setOnClickListener(new Button.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.d("KAMO","gohere2");
                        if (lm.getType().equals("LOI")) {
                            Log.d("KAMO","LOI");
                            LOIModel lm2 = new LOIModel(lm.getPOIId(), lm.getPOIName(), lm.getPOIDescription(), lm.getContributor(), lm.getIdentifier());
                            Intent intent2 = new Intent(v.getContext(), MapActivity.class);
                            intent2.putExtra("LOI-Content", lm2);
                            intent2.putExtra("POI_id",lm.getPOIId());
                            Bundle bundle = new Bundle();
                            //globalVariable.setType("LOI-Id");
                            intent2.putExtras(bundle);
                            v.getContext().startActivity(intent2);
                            /*LOIModel lm2 = new LOIModel(lm.getPOIId(), lm.getPOIName(), lm.getPOIDescription(), lm.getContributor(), lm.getIdentifier());
                            Intent intent = new Intent(v.getContext(), LOIActivity.class);
                            Bundle bundle = new Bundle();
                            globalVariable.setType("LOI-Id");
                            bundle.putParcelable("LOI-Content", lm2);
                            intent.putExtras(bundle);
                            v.getContext().startActivity(intent);*/
                        } else if (lm.getType().equals("AOI")) {
                            Log.d("KAMO","AOI");
                            LOIModel am = new LOIModel(lm.getPOIId(), lm.getPOIName(), lm.getPOIDescription(), lm.getContributor(), lm.getIdentifier());
                            Intent intent = new Intent(v.getContext(), AOIActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putParcelable("AOI-Content", am);
                            globalVariable.setType("AOI-Id");
                            bundle.putString("POI-name", lm.getPOIName());
                            bundle.putString("POI-Info", lm.getPOIDescription());
                            bundle.putString("POI-id", lm.getPOIId());
                            bundle.putString("POI-identifier", lm.getIdentifier());
                            bundle.putString("type", "AOI-Id");
                           // bundle.putString("flag", "AOI_to_POI");
                            intent.putExtras(bundle);
                            v.getContext().startActivity(intent);
                        } else {
                            Log.d("KAMO",lm.getPOIId());
                            Intent intent = new Intent(v.getContext(), POIActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putParcelable("POI-Content", lm);
                            bundle.putString("type", "POI-Id");
                            bundle.putString("flag", "LOI_to_POI");
                            intent.putExtras(bundle);
                            v.getContext().startActivity(intent);
                        }
                    }

                });
                Button b2 = (Button) findViewById(R.id.loi_inform);
                if (mServer.getType().equals("AOI"))
                    b2.setText(R.string.aoi_info);
                else if (mServer.getType().equals("SOI"))
                    b2.setText(R.string.soi_info);
                else
                    b2.setText(R.string.loi_info);
                b2.setOnClickListener(new Button.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mServer.getType().equals("LOI")) {
                            Intent intent = new Intent(v.getContext(), LOIActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putParcelable("LOI-Content", mServer.getLOImodel());
                            globalVariable.setType("LOI");
                            intent.putExtras(bundle);
                            v.getContext().startActivity(intent);
                        } else if (mServer.getType().equals("AOI")) {
                            Log.d("KAMO","AOI_inform");
                            Intent intent = new Intent(v.getContext(), AOIActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putParcelable("AOI-Content", mServer.getAOImodel());
                            globalVariable.setType("AOI");
                            intent.putExtras(bundle);
                            v.getContext().startActivity(intent);
                        } else if (mServer.getType().equals("SOI")) {
                            Intent intent = new Intent(v.getContext(), SOIActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putParcelable("SOI-Content", mServer.getSOImodel());
                            globalVariable.setType("SOI");
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
                                    .setContentUrl(Uri.parse("http://deh.csie.ncku.edu.tw/poi_detail/"+lm.getPOIId()))
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
                    if (mServer.getType().equals("SOI"))
                        b2.setTextSize(9);
                } else if (lng.equals("English")) {
                    b1.setTextSize(8);
                    b2.setTextSize(8);
                    b4.setTextSize(8);
                }
                b4.setOnClickListener(new Button.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String from = "saddr=" + curlocation.getLatitude() + "," + curlocation.getLongitude();
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




        } else if(x == 0){
            marker = mMap.addMarker(new MarkerOptions().position(new LatLng(mServer.getFAVmodel().getFavlat(), mServer.getFAVmodel().getFavlong())));
            v.setVisibility(View.VISIBLE);
            t.setVisibility(View.VISIBLE);
            t.setText(mServer.getFAVmodel().getTitle());

            Button inform = (Button) findViewById(R.id.poi_inform);
            inform.setOnClickListener(new Button.OnClickListener() {
                @Override
                public void onClick(View view) {
                    from_where = true;
                    Intent intent = new Intent(view.getContext(), POIActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("title", mServer.getFAVmodel().getTitle());
                    bundle.putString("id", mServer.getFAVmodel().getId());
                    bundle.putString("type", "POI-Id");
                    intent.putExtras(bundle);
                    view.getContext().startActivity(intent);
                }
            });
            Button share = (Button) findViewById(R.id.poi_share);
            final String url4 = "http://deh.csie.ncku.edu.tw/poi_detail/"+mServer.getFAVmodel().getId();
            share.setOnClickListener(new Button.OnClickListener() {
                @Override
                public void onClick(View view) {

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
                                .setContentTitle(mServer.getFAVmodel().getTitle())
                                .setContentDescription(mServer.getFAVmodel().getAddress())
                                .setContentUrl(Uri.parse(url4))
                                .build();
                        shareDialog.show(linkContent);
                    }

                }
            });
            Button guide = (Button) findViewById(R.id.poi_guide);
            if(lng.equals("English")) {
                inform.setTextSize(8);
                guide.setTextSize(9);
            }
            guide.setOnClickListener(new Button.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String from = "saddr=" + curlocation.getLatitude() + "," + curlocation.getLongitude();
                    String to = "daddr=" + mServer.getFAVmodel().getFavlat() + "," + mServer.getFAVmodel().getFavlong();
                    Log.d("To:", to);
                    String uri = "https://maps.google.com/maps?f=d&" + from + "&" + to + "&hl=tw";
                    Log.d("Uri", uri);
                    Uri i = Uri.parse(uri);
                    Intent intent = new Intent(android.content.Intent.ACTION_VIEW, i);
                    //intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
                    startActivity(intent);
                }
            });

            CameraPosition camerPosition = new CameraPosition.Builder()
                    .target(new LatLng(mServer.getFAVmodel().getFavlat(), mServer.getFAVmodel().getFavlong()))
                    .zoom(16)
                    .build();
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(camerPosition));

        }

    }



    public Bitmap resizeMapIcons(Bitmap bmp,int width, int height){
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(bmp, width, height, false);
        return resizedBitmap;
    }
    public static String getToken(){
        return Token;
    }
    private Boolean filtering(String identifier,String media,String category,boolean found){
        Filter filter = new Filter(getApplicationContext());
        final ArrayList<POIModel> filteredModelList, filteredModelList1, filteredModelList2;
        if (identifier != null && media != null && category != null) {
            if (identifier.equals("all") && media.equals("all") && category.equals("all")) {
                found = true;
                hint = getString(R.string.find) + " " + mServer.getPOIList().size() + " " + getString(R.string.sites);
            } else if (!identifier.equals("all") && media.equals("all") && category.equals("all")) {
                filteredModelList = filter.identifierFilter(mServer.getPOIList(), identifier.toLowerCase());
                updatePOI(filteredModelList);
                found = true;
                if (filteredModelList.size() == 0) {
                    Toast.makeText(MainActivity.this, getResources().getString(R.string.not_match), Toast.LENGTH_SHORT).show();
                    found = false;
                }
                else {
                    found = true;
                    hint = getString(R.string.find) + " " + filteredModelList.size() + " " + getString(R.string.sites);

                }

            } else if (identifier.equals("all") && !media.equals("all") && category.equals("all")) {
                filteredModelList = filter.mediaFilter(mServer.getPOIList(), media.toLowerCase());
                updatePOI(filteredModelList);
                found = true;
                if (filteredModelList.size() == 0) {
                    Toast.makeText(MainActivity.this, getResources().getString(R.string.not_match), Toast.LENGTH_SHORT).show();
                    found = false;
                }
                else {
                    found = true;
                    hint = getString(R.string.find) + " " + filteredModelList.size() + " " + getString(R.string.sites);

                }
            } else if (identifier.equals("all") && media.equals("all") && !category.equals("all")) {
                filteredModelList = filter.categoryFilter(mServer.getPOIList(), category.toLowerCase());
                updatePOI(filteredModelList);

                if (filteredModelList.size() == 0) {
                    Toast.makeText(MainActivity.this, getResources().getString(R.string.not_match), Toast.LENGTH_SHORT).show();
                    found = false;
                }
                else {
                    found = true;
                    hint = getString(R.string.find) + " " + filteredModelList.size() + " " + getString(R.string.sites);

                }
            } else if (!identifier.equals("all") && !media.equals("all") && category.equals("all")) {
                filteredModelList = filter.identifierFilter(mServer.getPOIList(), identifier.toLowerCase());
                filteredModelList1 = filter.mediaFilter(filteredModelList, media.toLowerCase());
                updatePOI(filteredModelList1);

                if (filteredModelList1.size() == 0) {
                    Toast.makeText(MainActivity.this, getResources().getString(R.string.not_match), Toast.LENGTH_SHORT).show();
                    found = false;
                }
                else {
                    found = true;
                    hint = getString(R.string.find) + " " + filteredModelList1.size() + " " + getString(R.string.sites);
                }

            } else if (!identifier.equals("all") && media.equals("all") && !category.equals("all")) {
                filteredModelList = filter.identifierFilter(mServer.getPOIList(), identifier.toLowerCase());
                filteredModelList1 = filter.categoryFilter(filteredModelList, category.toLowerCase());
                updatePOI(filteredModelList1);

                if (filteredModelList1.size() == 0) {
                    Toast.makeText(MainActivity.this, getResources().getString(R.string.not_match), Toast.LENGTH_SHORT).show();
                    found = false;
                }
                else {
                    found = true;
                    hint = getString(R.string.find) + " " + filteredModelList1.size() + " " + getString(R.string.sites);
                }
            } else if (identifier.equals("all") && !media.equals("all") && !category.equals("all")) {
                filteredModelList = filter.mediaFilter(mServer.getPOIList(), media.toLowerCase());
                filteredModelList1 = filter.categoryFilter(filteredModelList, category.toLowerCase());
                updatePOI(filteredModelList1);

                if (filteredModelList1.size() == 0) {
                    Toast.makeText(MainActivity.this, getResources().getString(R.string.not_match), Toast.LENGTH_SHORT).show();
                    found = false;
                }
                else {
                    found = true;
                    hint = getString(R.string.find) + " " + filteredModelList1.size() + " " + getString(R.string.sites);
                }
            } else {
                filteredModelList = filter.identifierFilter(mServer.getPOIList(), identifier.toLowerCase());
                filteredModelList1 = filter.mediaFilter(filteredModelList, media.toLowerCase());
                filteredModelList2 = filter.categoryFilter(filteredModelList1, category.toLowerCase());
                updatePOI(filteredModelList2);

                if (filteredModelList2.size() == 0) {
                    found = false;
                    Toast.makeText(MainActivity.this, getResources().getString(R.string.not_match), Toast.LENGTH_SHORT).show();
                }
                else {
                    found = true;
                    hint = getString(R.string.find) + " " + filteredModelList2.size() + " " + getString(R.string.sites);
                }
            }

        }
        return found;
    }
    public static ProxyService getServer(){
        return mServer;
    }

    public void btn_Group(View v){
        //Log.d("KamoGrouptt","test2");
        include_my_site.setVisibility(View.GONE);
        include_my_site_staus = false;
        if (globalVariable.checkInternet()) {
            userResult = realm.where(DEHUser.class)
                    .findAll();
            for (DEHUser user : userResult) {
                userId = user.getId();
            }
            preGroupSetting(v);
        } else {
            Toast.makeText(MainActivity.this, getResources().getString(R.string.connect_network), Toast.LENGTH_SHORT).show();
        }
    }
    public void preGroupSetting(View v){
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.dialog_choose_group,(ViewGroup)findViewById(R.id.group_linearLayout));
        group_list = (ListView)layout.findViewById(R.id.group_list);
        userResult = realm.where(DEHUser.class)
                .findAll();
        for (DEHUser user : userResult) {
            userId = user.getId();
        }
        String lang = getLanguage();
        searchGroup(userId,lang);
        //Chenyi20180130:等到所有權組資訊都撈到後
        while(!check_group_info){
            try {
                Thread.sleep(100);
                Log.d("Check","group");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        /*ArrayAdapter group_adapter = new ArrayAdapter(MainActivity.this, android.R.layout.simple_expandable_list_item_1, groups_show);
        group_list.setAdapter(group_adapter);
        group_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent();
                intent.setClass(MainActivity.this,GroupMainActivity.class);
                intent.putExtra("name",groups_name_list.get(i));
                intent.putExtra("id",groups_id_list.get(i));
                activity.startActivity(intent);

            }
        });*/
        DialogInterface.OnClickListener ListClick = new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialog, int which) {
                JoinGroup(which);
            }
        };
        String[] ListStr = groups_show.toArray(new String[0]);
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        if(group_Name==""){
            builder.setTitle(R.string.choose_group);
        }else {
            builder.setTitle("你的群組:"+group_Name);
        }
        //builder.setView(layout);
        builder.setItems(ListStr, ListClick);
        builder.setNeutralButton("Delete Group", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                deleteWhich();
            }
        });
        builder.setPositiveButton(getString(R.string.close_dialog), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        }).show();
    }

    public void deleteWhich(){
        final ArrayList<String> group_show_name_collection = new ArrayList<String>();
        final ArrayList<String> group_id_collection = new ArrayList<String>();
        for(int i=0;i<groups_leader_name_list.size();i++){
            if(groups_leader_name_list.get(i).equals(userId)){
                group_show_name_collection.add(groups_name_list.get(i));
                group_id_collection.add(groups_id_list.get(i));
            }
        }
        DialogInterface.OnClickListener ListClick = new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialog, int which) {
                deleteGroup(which,group_id_collection);
            }
        };
        String[] ListStr = group_show_name_collection.toArray(new String[0]);
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Which one do you want to delete?");
        builder.setItems(ListStr, ListClick);
        builder.setPositiveButton(getString(R.string.close_dialog), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        }).show();
    }

    public void deleteGroup(final int which,final ArrayList<String> group_id_collection){
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("警告!確定刪除?");
        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int whichOne) {
                deleteWithApi(group_id_collection.get(which));
                Toast.makeText(MainActivity.this, "刪除了群組", Toast.LENGTH_LONG).show();
            }
        });
        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }

    public void deleteWithApi(final String ID){
        final Request request = new Request.Builder()
                .url(urlGroupDelete+ID)
                //.addHeader("Authorization","Token f4184284-eb4e-4ed5-a54b-7ac8361ad12b")
                //.header("Authorization","Token f4184284-eb4e-4ed5-a54b-7ac8361ad12b")
                .get()
                //.post(body)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("OKHTTP", "Failed Connect_search");
                //check_group_info = true;
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String jsonAll = response.body().string();
                Log.d("OKHTTPGroup", jsonAll);
                if(Integer.parseInt(ID) == group_number){
                    group_number = -1;
                    group_Name = "";
                    choose = 2;
                }
                searchGroup("a","b");
            }
        });
    }

    public void JoinGroup(int which){
        if(which==0){
            CreateGroupInformation();
        }else{
            group_number = Integer.parseInt(groups_id_list.get(which - 1));
            group_Name = groups_name_list.get(which - 1);
            if(userId.equals(groups_leader_name_list.get(which-1))){
                choose = 0;
                Toast.makeText(MainActivity.this, "導覽員,歡迎回到" + group_Name + "群組", Toast.LENGTH_LONG).show();
            }else {
                choose = 1;
                Toast.makeText(MainActivity.this, "加入了" + group_Name + "群組", Toast.LENGTH_LONG).show();
            }
            Log.d("KamoGrouptt",group_Name+" "+Integer.toString(group_number)+" "+Integer.toString(choose));
        }
    }

    public void CreateGroupInformation(){
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("請輸入群組名稱");
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT );
        builder.setView(input);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                createGroup(input.getText().toString());
                Toast.makeText(MainActivity.this, "建立了"+input.getText().toString()+"群組", Toast.LENGTH_LONG).show();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }

    public void createGroup(final String groupName){
        JSONObject postDataInner = new JSONObject();
        try {
            postDataInner.put("group_name", groupName);
            postDataInner.put("leader_name",userId);
            postDataInner.put("language","中文");
            postDataInner.put("coi_name","deh");
        }catch (JSONException e){

        }
        Log.d("KAMOGroup",postDataInner.toString());
        FormBody.Builder formBody = new FormBody.Builder();
        formBody.add("docent_group",postDataInner.toString());
        final Request request = new Request.Builder()
                .url(urlGroupCreate)
                //.addHeader("Authorization","Token f4184284-eb4e-4ed5-a54b-7ac8361ad12b")
                //.header("Authorization","Token f4184284-eb4e-4ed5-a54b-7ac8361ad12b")
                .post(formBody.build())
                //.post(body)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("OKHTTP", "Failed Connect_search");
                //check_group_info = true;
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String jsonAll = response.body().string();
                Log.d("OKHTTPGroup", jsonAll);
                try {
                    JSONObject json = new JSONObject(jsonAll);
                    String groupId = json.getString("group_id");
                    group_number = Integer.parseInt(groupId);
                    choose = 0;
                    group_Name = groupName;
                    searchGroup("a","b");
                    //Toast.makeText(MainActivity.this, "建立了"+groupName+"群組", Toast.LENGTH_LONG).show();
                    /*FileOutputStream fos = openFileOutput(fileName, Context.MODE_PRIVATE);
                    BufferedOutputStream buffout = new BufferedOutputStream(fos);
                    buffout.write(groupId.getBytes());
                    buffout.close();
                    fos.close();*/
                }catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void searchGroup(String username,String  group_language){
        Log.d("KamoGrouptt",group_Name+" "+Integer.toString(group_number)+" "+Integer.toString(choose));
        final ArrayList<String> group_name_collection = new ArrayList<String>();
        //final ArrayList<String> group_role_collection = new ArrayList<String>();
        //final ArrayList<String> group_info_collection = new ArrayList<String>();
        final ArrayList<String> group_id_collection = new ArrayList<String>();
        final ArrayList<String> group_show_name_collection = new ArrayList<String>();
        final ArrayList<String> group_leader_name_collection = new ArrayList<String>();
        //final ArrayList<Integer> group_type_icons_collection = new ArrayList<Integer>();
        /*FormBody.Builder formBody = new FormBody.Builder();
        formBody.add("username",username);
        formBody.add("language",group_language);*/
        Log.d("groupNumberTest",Integer.toString(group_number));
            final Request request = new Request.Builder()
                    .url(urlForProjectGroup)
                    //.addHeader("Authorization","Token f4184284-eb4e-4ed5-a54b-7ac8361ad12b")
                    //.header("Authorization","Token f4184284-eb4e-4ed5-a54b-7ac8361ad12b")
                    //.post(formBody.build())
                    .get()
                    .build();
            Log.d("OkHttp", "Test");
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.d("OKHTTP", "Failed Connect_search");
                    check_group_info = true;
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String jsonAll = response.body().string();
                    Log.d("OKHTTPGroup", jsonAll);
                    try {
                        group_show_name_collection.add("Create Group");
                        JSONObject jsA = new JSONObject(jsonAll);
                        String jsArr = jsA.getString("docent_group_id");
                        JSONArray ja = new JSONArray(jsArr);
                        for (int i = 0; i < ja.length(); i++) {
                            //JSONObject js = ja.getJSONObject(i);
                            //group_name_collection.add(js.getString("group_name"));
                            //group_role_collection.add(js.getString("role"));
                            group_id_collection.add(Integer.toString(ja.getInt(i)));
                            //group_show_name_collection.add(js.getString("group_name") + " <" +js.getString("role")+ "> ");
                            //System.out.println(js.getString("group_name"));
                        }
                        String jsArrB = jsA.getString("docent_group_name");
                        JSONArray jb = new JSONArray(jsArrB);
                        for (int i = 0; i < jb.length(); i++) {
                            //JSONObject js = jb.getJSONObject(i);
                            group_name_collection.add(jb.getString(i));
                            group_show_name_collection.add(jb.getString(i) + " <" + group_id_collection.get(i) + "> ");
                        }
                        String jsArrC = jsA.getString("docent_group_leader_name");
                        JSONArray jc = new JSONArray(jsArrC);
                        for (int i = 0; i < jc.length(); i++) {
                            //JSONObject js = jb.getJSONObject(i);
                            group_leader_name_collection.add(jc.getString(i));
                        }
                        check_group_info = true;
                    } catch (JSONException e) {
                        e.printStackTrace();
                        check_group_info = true;
                    }
                    //Log.d("OKHTTPGroup",group_name_collection.get(0));
                    groups_id_list = group_id_collection;
                    groups_name_list = group_name_collection;
                    //groups_role_list = group_role_collection;
                    groups_show = group_show_name_collection;
                    groups_leader_name_list = group_leader_name_collection;
                }
            });
    }

    //Make Sure the Language
    public String getLanguage(){
        Resources res = MainActivity.this.getResources();
        Configuration conf = res.getConfiguration();
        String language = conf.locale.getLanguage();
        String group_lang="中文";
        if(language.equals("en"))
            group_lang="英文";
        if(language.equals("ja"))
            group_lang="日文";
        return group_lang;
    }

}
