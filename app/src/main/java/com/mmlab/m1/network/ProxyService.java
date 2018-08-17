package com.mmlab.m1.network;


import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.util.Log;


import com.mmlab.m1.MyApplication;
import com.mmlab.m1.R;
import com.mmlab.m1.adapter.LOISequenceAdapter;
import com.mmlab.m1.model.DEHUser;
import com.mmlab.m1.model.LOIModel;
import com.mmlab.m1.model.LOISequenceModel;
import com.mmlab.m1.model.MyFavorite;
import com.mmlab.m1.model.POIModel;
import com.mmlab.m1.save_data.SaveAOISequence;
import com.mmlab.m1.save_data.SaveLOISequence;
import com.mmlab.m1.save_data.SaveSOI;
import com.mmlab.m1.save_data.SaveAOI;
import com.mmlab.m1.save_data.SaveLOI;
import com.mmlab.m1.save_data.SavePOI;
import com.mmlab.m1.save_data.SaveSOISequence;
import com.mmlab.m1.service.Encryption;
import com.mmlab.m1.service.HttpAsyncTask;
import com.mmlab.m1.service.TaskCompleted;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;


import io.realm.Realm;
import io.realm.RealmResults;

public class ProxyService extends Service implements TaskCompleted {

    public static final String GETPOI_ACTION = "ProxyService.GETPOI_ACTION";
    public static final String GETLOI_ACTION = "ProxyService.GETLOI_ACTION";
    public static final String GETAOI_ACTION = "ProxyService.GETAOI_ACTION";
    public static final String POI_GOGO = "ProxyService.POI";
    public static final String LOI_GOGO = "ProxyService.LOI";
    public static final String AOI_GOGO = "ProxyService.AOI";
    public static final String SOI_GOGO = "ProxyService.SOI";
    public static final String FAV_GOGO = "ProxyService.FAV";
    public static final String Exception_GOGO = "ProxyService.Exception";
    public static final String GETSOI_ACTION = "ProxyService.GETSOI_ACTION";
    public static final String GETTOKEN = "ProxyService.GETTOKEN_ACTION";
    public static final String MEMBER_ACTION = "ProxyService.MEMBER_ACTION";
    public static final String FILE_COMPLETE__ACTION = "ProxyService.FILE_COMPLETE_ACTION";
    public static final String CREATE_SERVER_ACTION = "ProxyService.CREATE_SERVER_ACTION";
    private static final String TAG = "ProxyService";
    public static final String GETLOGIN_ACTION = "ProxyService.GETLOGIN_ACTION";
    public static ServerThread threadServer = null;
    public static Handler pHandler = null;
    private HandlerThread pHandlerThread = null;
    private static ArrayList<POIModel> poiList = new ArrayList<POIModel>();
    private static ArrayList<LOIModel> loiList = new ArrayList<LOIModel>();
    private static ArrayList<LOIModel> aoiList = new ArrayList<LOIModel>();
    private static ArrayList<LOIModel> soiList = new ArrayList<LOIModel>();
    private ArrayList<LOISequenceModel> loiSequenceList = new ArrayList<>();
    private ArrayList<LOISequenceModel> aoiSequenceList = new ArrayList<>();
    private ArrayList<LOISequenceModel> soiSequenceList = new ArrayList<>();
    private String account;
    private String password;
    private String mType;
    private POIModel poim;
    private MyFavorite favm;
    private static LOIModel loim;
    private Realm realm;
    private LOISequenceAdapter mAdapter;
    public static int type = 0;
    public static String status,docent_info;

    private ProxyBinder binder = new ProxyBinder();
    private MyApplication globalVariable;
    private VideoService videoService = null;
    private VideoService.VideoBinder videoBinder = null;
    private RealmResults<DEHUser> userResult;
    private static String AToken = null;
    private ServiceConnection videoConnection = new ServiceConnection() {

        public void onServiceDisconnected(ComponentName name) {
            Log.d(TAG, "onServiceDisconnected()...");
        }

        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d(TAG, "onServiceConnected()...");
            videoBinder = (VideoService.VideoBinder) service;
            videoService = videoBinder.getVideoInstance();
        }
    };
    private Context mContext;
    private boolean isLogIn;

    public ProxyService() {

    }

    public void retrieveIp() {
        pHandler.sendEmptyMessage(0);
    }

    public void onCreate() {
        super.onCreate();

        globalVariable = (MyApplication) getApplicationContext();

        pHandlerThread = new HandlerThread("Server");
        pHandlerThread.start();
        pHandler = new ProcessHandler(ProxyService.this, pHandlerThread.getLooper());

        startServer();
        // start service
        Intent startIntent = new Intent(ProxyService.this, VideoService.class);
        startService(startIntent);
        // bind service
        Intent bindIntent = new Intent(ProxyService.this, VideoService.class);
        bindService(bindIntent, videoConnection, BIND_AUTO_CREATE);
        Log.d(TAG, "onCreate()...");

    }

    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy()...");
        stopServer();
        // stop service
        Intent intent = new Intent(ProxyService.this, VideoService.class);
        stopService(intent);
        // unbind service
        unbindService(videoConnection);
    }

    /**
     * START_STICKY：sticky的意思是“黏性的”。使用這個返回值時，
     * 我們啟動的服務跟應用程序"黏"在一起，如果在執行完onStartCommand後，
     * 服務被異常kill掉，系统會自動重起該服務。
     * 當再次啟動服務時，傳入的第一個参數將為null;
     *
     * @param intent
     * @param flags
     * @param startId
     * @return
     */
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand()...");
        // return super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }

    public boolean onUnbind(Intent intent) {
        Log.d(TAG, "onUnBind()...");
        return super.onUnbind(intent);
    }

    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind()...");
        return binder;
    }

    @Override
    public void onTaskComplete(String response, String type) {
        if (!response.contains("Unable to connect")) {

            if (type.equals("IP")) {
                globalVariable.setIp(response.replaceAll("\n", ""));
                Log.d("ip", globalVariable.getIp());
            } else if (type.equals("DEH log in")) {
                Log.d("result ", response);


                Message message = pHandler.obtainMessage();
                Bundle bundle = new Bundle();
                bundle.putString("response", response);
                message.setData(bundle);
                message.what = 2;
                pHandler.sendMessage(message);


            } else if(type.equals("Authorization")){
                try {
                    JSONObject jb = new JSONObject(response);
                    AToken = jb.getString("token");
                    Message m = pHandler.obtainMessage();
                    m.what = 6;
                    pHandler.sendMessage(m);
                }catch (JSONException e) {
                    e.printStackTrace();
                }
            } if (type.equals("LOI-Sequence")) {

                // String result = Encryption.decode(this, response, globalVariable.getIp());
                Log.d("test", response);
                SaveLOISequence saveLOISequence = new SaveLOISequence(response,"LOI");
                loiSequenceList = saveLOISequence.getLOISequenceList();

                LOISequenceAdapter lm2 = new LOISequenceAdapter(this, loiSequenceList, type, loim.getIdentifier());

                Message message = pHandler.obtainMessage();
                message.what = 7;
                pHandler.sendMessage(message);


            }else if (type.equals("AOI-Sequence")) {
                //String result = Encryption.decode(this, response, globalVariable.getIp());

                SaveAOISequence saveAOISequence = new SaveAOISequence(response,"AOI");
                aoiSequenceList = saveAOISequence.getAOISequenceList();

                Message message = pHandler.obtainMessage();
                message.what = 8;
                pHandler.sendMessage(message);

            }else if (type.equals("SOI-Sequence")) {
                //String result = Encryption.decode(this, response, globalVariable.getIp());

                SaveSOISequence saveSOISequence = new SaveSOISequence(response);
                soiSequenceList = saveSOISequence.getSOISequenceList();

                Message message = pHandler.obtainMessage();
                message.what = 9;
                pHandler.sendMessage(message);
            }else if(type.equals("Docent Info")){
                docent_info = response;
            }

            else if (globalVariable.getIp() != null && !type.equals("DEH log in")) {

                //String result = Encryption.decode(this, response, globalVariable.getIp());

                Log.d("result ", response);
                Message message = pHandler.obtainMessage();
                Bundle bundle = new Bundle();
                bundle.putString("type", type);
                bundle.putString("result", response);
                message.setData(bundle);
                message.what = 3;
                pHandler.sendMessage(message);
            }

        }

    }

    public void sendException(){
        Message message = pHandler.obtainMessage();
        message.what = 12;
        pHandler.sendMessage(message);
    }

    public Boolean getLogInStatus() {
        return isLogIn;
    }

    public void setPOImodel(POIModel p){
        Log.d("KAMO",p.getPOIName());
        this.poim = p;
        this.mType = "POI";
        Message message = pHandler.obtainMessage();
        message.what = 5;
        pHandler.sendMessage(message);
    }

    public void setFAVmodel(MyFavorite p){
        this.favm = p;
        Message message = pHandler.obtainMessage();
        message.what = 11;
        pHandler.sendMessage(message);
    }

    public void setLOImodel(LOIModel l){
        Log.d("KAMO",l.getLOIName());
        this.loim = l;
        this.mType = "LOI";
        loiSequenceList = l.getmPOIs();
        Message message = pHandler.obtainMessage();
        message.what = 7;
        pHandler.sendMessage(message);
    }
    public LOIModel getLOImodel(){
        return loim;
    }
    public void setAOImodel(LOIModel l){
        this.loim = l;
        this.mType = "AOI";
        loiSequenceList = l.getmPOIs();
        Message message = pHandler.obtainMessage();
        message.what = 8;
        pHandler.sendMessage(message);

    }
    public LOIModel getAOImodel(){
        return loim;
    }
    public void setSOImodel(LOIModel l){
        this.loim = l;
        this.mType = "SOI";
        soiSequenceList = l.getmPOIs();
        Message message = pHandler.obtainMessage();
        message.what = 9;
        pHandler.sendMessage(message);

    }
    public LOIModel getSOImodel(){
        return loim;
    }
    public ArrayList<LOISequenceModel> getLOIsmodel(){
        if(mType.equals("LOI"))
            return loiSequenceList;
        else if(mType.equals("AOI"))
            return aoiSequenceList;
        else
            return soiSequenceList;
    }

    public void setContext(Context context) {
        mContext = context;
    }
    public String getToken(){return AToken;}
    public POIModel getPOImodel(){
        return poim;
    }

    public MyFavorite getFAVmodel(){
        return favm;
    }

    public String getType(){return mType;}
    public void sendCommandPlayback(long mediaLength, String remoteUri) {
        Message message = pHandler.obtainMessage(7);
        Bundle bundle = new Bundle();
        bundle.putLong("mediaLength", mediaLength);
        bundle.putString("remoteUri", remoteUri);
        message.setData(bundle);
        pHandler.sendMessage(message);
    }

    public void sendResponsePlayback(String host, long mediaLength, String remoteUri) {
        Message message = pHandler.obtainMessage(8);
        Bundle bundle = new Bundle();
        bundle.putString("host", host);
        bundle.putLong("mediaLength", mediaLength);
        bundle.putString("remoteUri", remoteUri);
        message.setData(bundle);
        pHandler.sendMessage(message);
    }

    public void Search(String type, String url) {
        if (globalVariable.getIp() == null)
            pHandler.sendEmptyMessage(0);
        Message message = pHandler.obtainMessage();
        Bundle bundle = new Bundle();
        bundle.putString("type", type);
        bundle.putString("url", url);
        message.setData(bundle);
        message.what = 1;
        pHandler.sendMessage(message);
    }
    public void Authorization(){
        String url = "http://deh.csie.ncku.edu.tw:8080/api/v1/grant";
        Message message = pHandler.obtainMessage();
        Bundle bundle = new Bundle();
        bundle.putString("type","Authorization");
        bundle.putString("url",url);
        message.setData(bundle);
        message.what = 10;
        pHandler.sendMessage(message);
    }
    public void logIn(String type, String url, String account, String password) {
        this.account = account;
        this.password = password;
        Message message = pHandler.obtainMessage();
        Bundle bundle = new Bundle();
        bundle.putString("type", type);
        bundle.putString("url", url);
        bundle.putString("account",account);
        bundle.putString("password",password);
        message.setData(bundle);
        message.what = 1;
        pHandler.sendMessage(message);
    }
    public ArrayList<POIModel> getPOIList() {
        return poiList;
    }

    public ArrayList<LOIModel> getLOIList() {
        return loiList;
    }

    public ArrayList<LOIModel> getAOIList() {
        return aoiList;
    }
    public ArrayList<LOIModel> getSOIList(){
        return soiList;
    }

    public void sendSinglePOI(POIModel poiModel) {
        if (pHandler != null && threadServer != null)
            pHandler.sendMessage(pHandler.obtainMessage(4, poiModel));
    }

    public void sendSingleFAV(MyFavorite favModel) {
        if (pHandler != null && threadServer != null)
            pHandler.sendMessage(pHandler.obtainMessage(4, favModel));
    }

    public void sendPhoto() {
        if (pHandler != null && threadServer != null)
            pHandler.sendEmptyMessage(9);
    }

    protected void startServer() {
        if (threadServer == null) {
            threadServer = new ServerThread();
            threadServer.start();
        } else {
            threadServer.interrupt();
            threadServer = new ServerThread();
            threadServer.start();
        }
    }

    public void stopProxyService() {
        videoService.stopProxyService();
    }

    public void restartService() {
        if (threadServer != null) {
            threadServer.interrupt();
            startServer();
        }
    }

    public void hangService() {
        if (threadServer != null) {
            threadServer.interrupt();
        }
    }

    protected void stopServer() {
        if (threadServer != null)
            threadServer.interrupt();
    }

    public Handler getpHandler() {
        return pHandler;
    }

    public void setpHandler(Handler pHandler) {
        this.pHandler = pHandler;
    }

    public class ProxyBinder extends Binder {
        public ProxyService getProxyInstance() {
            return ProxyService.this;
        }
    }

    public class ServerThread extends Thread {

        private static final String TAG = "Server";
        private static final int PORT = 9001;

        private ServerSocket serverSocket = null;
        private Socket socket = null;
        private ConcurrentHashMap<Socket, ObjectOutputStream> members = new ConcurrentHashMap<>();

        public ServerThread() {

        }


        /**
         * 關閉ServerSocket，block在accept method的地方會throw a SocketException
         */
        public void interrupt() {
            super.interrupt();
            try {
                if (serverSocket != null && !serverSocket.isClosed()) {
                    serverSocket.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            clearClients();
        }

        public void clearClients() {
            Iterator<Socket> iterator = members.keySet().iterator();
            while (iterator.hasNext()) {
                Socket member = iterator.next();
                try {
                    member.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }


        public void run() {
            serverSocket = null;

            try {
                serverSocket = new ServerSocket(PORT);
                Log.i(TAG, "The server is running");

                // Accept the incoming socket connection
                while (!Thread.currentThread().isInterrupted()) {

                    /*if (Preset.loadModePreference(getApplicationContext()) == IDENTITY.MODE_GUIDE) {
                        socket = serverSocket.accept();
                        ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
                        sendConfirmPackage(objectOutputStream);
                        members.put(socket, objectOutputStream);
                        new ServerHandler(socket).start();
                        Log.i(TAG, "accept client");
                    }*/
                }
            } catch (IOException e) {
                Log.i(TAG, "ServerSocket accept fail: " + e.toString());
                e.printStackTrace();
            } finally {
                if (serverSocket != null) {
                    try {
                        serverSocket.close();
                    } catch (IOException e) {
                        Log.i(TAG, "SocketServer close fail: " + e.toString());
                        e.printStackTrace();
                    }
                }
            }
            Log.i(TAG, "close server socket successfully");
        }
/*
        public void sendCommandPlayback(long mediaLength, String remoteUri) {

            if (serverSocket != null && !serverSocket.isClosed()) {
                Log.d(TAG, "Number of Member : " + members.size());
                // 確認當前還有多少成員維持連線
                Iterator<Socket> iterator = members.keySet().iterator();
                while (iterator.hasNext()) {
                    Socket member = iterator.next();
                    try {
                        ObjectOutputStream writer = members.get(member);
                        writer.write(1);
                        writer.writeObject(new Package(Package.TAG_COMMAND, Package.TYPE_NONE, "", Utils.stringToByteArray("start video" + " " + String.valueOf(mediaLength) + " " + remoteUri)));
                    } catch (IOException e) {
                        members.remove(member);
                        e.printStackTrace();
                    }
                }
            }
        }

        public void sendResponsePlayback(String host, long mediaLength, String remoteUri) {
            Log.d(TAG, "Number of Member : " + members.size());
            // 確認當前還有多少成員維持連線
            Iterator<Socket> iterator = members.keySet().iterator();
            while (iterator.hasNext()) {
                Socket member = iterator.next();
                Log.d(TAG, "remote socket address : " + member.getRemoteSocketAddress().toString());
                if (member.getRemoteSocketAddress().toString().equals(host))
                    try {
                        ObjectOutputStream writer = members.get(member);
                        writer.write(1);
                        writer.writeObject(new Package(Package.TAG_COMMAND, Package.TYPE_NONE, "", Utils.stringToByteArray("request video" + " " + String.valueOf(mediaLength) + " " + remoteUri)));
                    } catch (IOException e) {
                        members.remove(member);
                        e.printStackTrace();
                    }
            }
        }

        public synchronized void sendPackageToClient(int cnt, Package[] packages) {
            if (serverSocket != null && !serverSocket.isClosed()) {
                Log.d(TAG, "Number of Member : " + members.size());
                // 確認當前還有多少成員維持連線
                Iterator<Socket> iterator = members.keySet().iterator();
                while (iterator.hasNext()) {
                    Socket member = iterator.next();
                    try {
                        ObjectOutputStream writer = members.get(member);
                        writer.write(cnt);
                        // Check whether the data length is correct
                        if (cnt == packages.length) {
                            for (int i = 0; i < cnt; ++i) {
                                writer.writeObject(packages[i]);
                            }
                        }
                    } catch (IOException e) {
                        members.remove(member);
                        e.printStackTrace();
                    }
                }
            }
        }

        public void sendPackageToClient(ArrayList<Package> packages) {

            if (serverSocket != null && !serverSocket.isClosed()) {
                Log.d(TAG, "Number of Member : " + members.size());
                // 確認當前還有多少成員維持連線
                Iterator<Socket> iterator = members.keySet().iterator();
                while (iterator.hasNext()) {
                    Socket member = iterator.next();
                    try {
                        ObjectOutputStream writer = members.get(member);
                        writer.write(packages.size());
                        // Check whether the data length is correct
                        for (int i = 0; i < packages.size(); ++i) {
                            writer.writeObject(packages.get(i));
                        }
                    } catch (IOException e) {
                        members.remove(member);
                        e.printStackTrace();
                    }
                }
            }
        }

        private void sendConfirmPackage(ObjectOutputStream writer) {

            if (serverSocket != null && !serverSocket.isClosed())
                try {
                    writer.write(1);
                    writer.writeObject(new Package(Package.TAG_NONE, Package.TYPE_NONE, "", Utils.stringToByteArray("Hello this is server")));

                    Log.i(TAG, "Send package successfully");
                } catch (IOException e) {
                    Log.i(TAG, "Send package fail: " + e.toString());
                    e.printStackTrace();
                }
        }

        private class ServerHandler extends Thread {

            private static final String TAG = "ServerHandler";
            private Socket socket;
            private ObjectInputStream objectInputStream;

            public ServerHandler(Socket socket) {
                this.socket = socket;
            }

            public void run() {
                Log.i(TAG, "ServerHandler is running");
                try {

                    objectInputStream = new ObjectInputStream(socket.getInputStream());
                    while (!Thread.currentThread().isInterrupted()) {
                        int objectNumber = objectInputStream.read();
                        if (objectNumber < 0) break;


                        // Read the location information from client and send  to the web server
                        for (int i = 0; i < objectNumber; ++i) {
                            try {
                                Package pack = (Package) objectInputStream.readObject();

                                switch (pack.tag) {
                                    case Package.TAG_COMMAND:
                                        break;
                                    case Package.TAG_DATA:
                                        break;
                                    case Package.TAG_NONE:
                                        break;
                                }
                            } catch (ClassNotFoundException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                } catch (SocketException e) {
                    Log.i(TAG, "Socket Exception: " + e.toString());
                    e.printStackTrace();
                } catch (IOException e) {
                    Log.i(TAG, "input fail: " + e.toString());
                    e.printStackTrace();
                } finally {
                    if (objectInputStream != null) {
                        try {
                            objectInputStream.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    if (socket != null) {
                        try {
                            socket.close();
                        } catch (IOException e) {
                            Log.i(TAG, "Socket close fail: " + e.toString());
                            e.printStackTrace();
                        }
                    }
                    Log.i(TAG, "close serverHandler properly");
                }
            }
        }
    }
*/
    }
    /**
     * 處理費時任務
     */
    private class ProcessHandler extends Handler {

        private WeakReference<Service> weakReference = null;
        private Intent intent;

        public ProcessHandler(Service service, Looper looper) {
            super(looper);
            weakReference = new WeakReference<Service>((ProxyService) service);
        }

        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    // get ip
                   // new HttpAsyncTask(ProxyService.this, "IP",msg.what).execute(getResources().getString(R.string.api_clientIP));
                    break;
                case 1:
                    String item = msg.getData().getString("type");
                    String url = msg.getData().getString("url");

                    if(item.equals("DEH log in")) {
                        String account = msg.getData().getString("account");
                        String password = msg.getData().getString("password");
                        Log.d("Type",item);
                        new HttpAsyncTask(ProxyService.this, item, 2,account,password,AToken).execute(url);
                    }
                    else if(item.equals("Authorization"))
                        new HttpAsyncTask(ProxyService.this, item,10).execute(url);
                    else if(item.equals("MyPOI")||item.equals("MyLOI")||item.equals("MyAOI")||item.equals("MySOI")){
                        realm = Realm.getInstance(getApplicationContext());
                        userResult = realm.where(DEHUser.class)
                                .findAll();
                        for (DEHUser user : userResult) {
                            account = user.getId();
                            password = user.getPw();
                        }
                        new HttpAsyncTask(ProxyService.this,item,3,account,password,AToken).execute(url);
                    }
                    else
                        new HttpAsyncTask(ProxyService.this, item,msg.what,AToken).execute(url);
                    break;
                case 2:
                    // get log in status
                    String response = msg.getData().getString("response");
                    if (!response.contains("Not found")) {
                        realm = Realm.getInstance(getApplicationContext());
                        realm.beginTransaction();
                        DEHUser user = realm.createObject(DEHUser.class);
                        user.setId(account);
                        user.setPw(password);
                        realm.commitTransaction();
                        userResult = realm.where(DEHUser.class)
                                .findAll();
                        isLogIn = true;
                    } else {
                        isLogIn = false;
                    }

                    intent = new Intent();
                    intent.setAction(GETLOGIN_ACTION);


                    if (weakReference.get() != null)
                        weakReference.get().sendBroadcast(intent);

                    break;
                case 3:
                    // 處理點資訊
                    String type = msg.getData().getString("type");
                    String result = msg.getData().getString("result");
                    intent = new Intent();

                    Log.d("Test", type + result);

                    status = type;

                    if (type.equals("POI") || type.equals("MyPOI")) {
                        SavePOI savePOI = new SavePOI();
                        savePOI.parsePoiListJSONObject(result);
//                        savePOI.parsePoiListJSONObject(result, status);
                        poiList.clear();
                        poiList = savePOI.getPOIList();
                        if (poiList == null)
                            poiList = new ArrayList<>();

                        intent.setAction(GETPOI_ACTION);
                    } else if (type.equals("LOI") || type.equals("MyLOI")) {
                        SaveLOI saveLOI = new SaveLOI(result);
                        loiList.clear();
                        loiList = saveLOI.getLOIList();
                        if (loiList == null)
                            loiList = new ArrayList<>();
                        for (int i = 0; i < loiList.size(); ++i) {
                            LOIModel md = loiList.get(i);
                            if (md.getIdentifier().equals("docent")) {
                                String contributor = null;
                                if (type.equals("MyLOI")) {
                                    realm = Realm.getInstance(getApplicationContext());
                                    contributor = realm.where(DEHUser.class).findAll().first().getId();
                                } else {
                                    contributor = md.getContributor();
                                }
                                new HttpAsyncTask(ProxyService.this, "loi-contributor-detail:" + Integer.toString(i),msg.what).execute(
                                        getResources().getString(R.string.api_docent_info) + contributor
                                );
                            }
                        }
                        intent.setAction(GETLOI_ACTION);
                    } else if (type.equals("AOI") || type.equals("MyAOI")) {
                        SaveAOI saveAOI = new SaveAOI(result);
                        aoiList.clear();
                        aoiList = saveAOI.getAOIList();
                        if (aoiList == null)
                            aoiList = new ArrayList<>();
                        for (int i = 0; i < aoiList.size(); ++i) {
                            LOIModel md = aoiList.get(i);
                            if (md.getIdentifier().equals("docent")) {
                                String contributor = null;
                                if (type.equals("MyAOI")) {
                                    realm = Realm.getInstance(getApplicationContext());
                                    contributor = realm.where(DEHUser.class).findAll().first().getId();
                                } else {
                                    contributor = md.getContributor();
                                }
                                /*new HttpAsyncTask(ProxyService.this, "aoi-contributor-detail:" + Integer.toString(i),msg.what).execute(
                                        getResources().getString(R.string.api_docent_info) + contributor
                                );*/
                            }
                        }
                        intent.setAction(GETAOI_ACTION);
                    } else if (type.contains("contributor-detail")) {
                        try {
                            result = Encryption.decode(ProxyService.this, result, globalVariable.getIp());
                            JSONObject obj = (new JSONObject(result)).getJSONArray("results").getJSONObject(0);
                            int index;
                            if (type.contains("loi")) {
                                index = Integer.valueOf(type.substring("loi-contributor-detail:".length()));
                                loiList.get(index).setContributorDetail(obj);
                            } else if (type.contains("aoi")) {
                                index = Integer.valueOf(type.substring("aoi-contributor-detail:".length()));
                                aoiList.get(index).setContributorDetail(obj);
                            }
                        } catch (JSONException ex) {
                            ex.printStackTrace();
                        }
                    }else if(type.equals("SOI")||type.equals("MySOI")){
                        SaveSOI saveSOI = new SaveSOI(result);
                        soiList.clear();
                        soiList = saveSOI.getSOIList();
                        if(soiList==null)
                            soiList = new ArrayList<>();

                        intent.setAction(GETSOI_ACTION);
                    }

                    if (weakReference.get() != null)
                        weakReference.get().sendBroadcast(intent);

                    break;
                case 5:
                    intent = new Intent();
                    intent.setAction(POI_GOGO);

                    if (weakReference.get() != null)
                        weakReference.get().sendBroadcast(intent);
                    break;
                case 6:
                    intent = new Intent();
                    intent.setAction(GETTOKEN);
                    if (weakReference.get() != null)
                        weakReference.get().sendBroadcast(intent);
                    break;
                case 7:
                    intent = new Intent();
                    intent.setAction(LOI_GOGO);
                    if (weakReference.get() != null) {
                        weakReference.get().sendBroadcast(intent);
                    }
                    break;
                case 8:
                    intent = new Intent();
                    intent.setAction(AOI_GOGO);
                    if (weakReference.get() != null)
                        weakReference.get().sendBroadcast(intent);
                    break;
                case 9:
                    intent = new Intent();
                    intent.setAction(SOI_GOGO);
                    if (weakReference.get() != null)
                        weakReference.get().sendBroadcast(intent);
                    break;
                case 10:
                    String it = msg.getData().getString("type");
                    String ur = msg.getData().getString("url");

                    new HttpAsyncTask(ProxyService.this, it,msg.what).execute(ur);
                    break;
                case 11:
                    Log.d("1","2");
                    intent = new Intent();
                    intent.setAction(FAV_GOGO);

                    if (weakReference.get() != null)
                        weakReference.get().sendBroadcast(intent);
                    break;
                case 12:
                    intent = new Intent();
                    intent.setAction(Exception_GOGO);

                    if (weakReference.get() != null)
                        weakReference.get().sendBroadcast(intent);
                    break;
                default:
            }
        }
    }

    class CreateGroup extends AsyncTask<String, Void, Void> {

        private WeakReference<Service> weakReference = null;
        private HttpURLConnection connection = null;
        private InputStream inputStream = null;

        public CreateGroup(Service service) {
            weakReference = new WeakReference<Service>(service);
        }

        protected Void doInBackground(String... strings) {

            return null;
        }
    }
}
