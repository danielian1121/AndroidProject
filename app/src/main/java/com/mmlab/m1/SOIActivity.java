package com.mmlab.m1;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.mmlab.m1.adapter.LOISequenceAdapter;
import com.mmlab.m1.helper.Preset;
import com.mmlab.m1.model.LOIModel;
import com.mmlab.m1.model.LOISequenceModel;
import com.mmlab.m1.save_data.SaveSOISequence;
import com.mmlab.m1.service.HttpAsyncTask;
import com.mmlab.m1.service.TaskCompleted;
// com.mmnlab.m1.R;
import com.ms.square.android.expandabletextview.ExpandableTextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class SOIActivity extends AppCompatActivity implements TaskCompleted {

    private MyApplication globalVariable;
    private RecyclerView mRecyclerView;
    private LOISequenceAdapter mAdapter;
    private ArrayList<LOISequenceModel> soiSequenceList = new ArrayList<>();
    private LOIModel model;
    private Menu mMenu;
    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_soi);

        model = getIntent().getParcelableExtra("SOI-Content");
        globalVariable = (MyApplication) getApplicationContext();
        token = MainActivity.getToken();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(model.getLOIName());

        ExpandableTextView mDescription = (ExpandableTextView) findViewById(R.id.expand_text_view);
        mDescription.setText(model.getLOIInfo());
        TextView docentName = (TextView) findViewById(R.id.docent_name);


//		if (model.getIdentifier().equals("docent")) {
//
//			try {
//				docentName.setText(model.getContributorDetail().getString("name"));
//				docentName.setOnClickListener(new View.OnClickListener() {
//					@Override
//					public void onClick(View view) {
//						MaterialDialog materialDialog;
//						String alertMessage = "";
//						JSONObject obj = model.getContributorDetail();
//						materialDialog = new MaterialDialog.Builder(view.getContext())
//								.title("導覽員資訊")
//								.positiveText(R.string.confirm)
//								.onPositive(new MaterialDialog.SingleButtonCallback() {
//									@Override
//									public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
//										// TODO
//									}
//								}).build();
//						try {
//							alertMessage += "姓名:" + obj.getString("name");
//							alertMessage += "\n電話: " + obj.getString("telphone");
//							alertMessage += "\n手機: " + obj.getString("cellphone");
//							alertMessage += "\nEmail: " + obj.getString("email");
//							alertMessage += "\n地址: " + obj.getString("user_address");
//							alertMessage += "\n社群帳號: " + obj.getString("social_id");
//							alertMessage += "\n導覽解說地區: " + obj.getString("docent_area");
//							alertMessage += "\n導覽解說使用語言: " + obj.getString("docent_language");
//							alertMessage += "\n收費標準: " + obj.getString("charge");
//							alertMessage += "\n自我介紹: " + obj.getString("introduction");
//						} catch (JSONException e) {
//							e.printStackTrace();
//						}
//						materialDialog.setMessage(alertMessage);
//						materialDialog.show();
//					}
//				});
//			} catch (JSONException e) {
//				e.printStackTrace();
//			}
//		}

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        soiSequenceList = MainActivity.getServer().getSOImodel().getmPOIs();

        /*for (int i = 0; i < soiSequenceList.size(); ++i) {
            Log.d("id:",soiSequenceList.get(i).getIdentifier());
            //if (soiSequenceList.get(i).getIdentifier().equals("docent")) {
                Log.d("name:"," "+soiSequenceList.get(i).getContributor());
                new HttpAsyncTask(SOIActivity.this, "contributor-detail:" + Integer.toString(i),0)
                        .execute(getResources().getString(R.string.api_docent_info) + soiSequenceList.get(i).getContributor());
            //}
        }*/
        if(model.getIdentifier().equals("docent"))
            new HttpAsyncTask(SOIActivity.this, "contributor-detail:",0)
                    .execute(getResources().getString(R.string.api_docent_info) + model.getContributor()
                    );
        Log.d("contributor",""+model.getContributor());
        mAdapter = new LOISequenceAdapter(this, soiSequenceList,"SOI-Sequence", model.getIdentifier(),model.getContributor());
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mAdapter);


        String hint = getString(R.string.find) + " " + soiSequenceList.size() + " " + getString(R.string.sites);
        Snackbar.make(mRecyclerView, hint, Snackbar.LENGTH_SHORT).show();


    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_poi_list, menu);

        return true;
    }




    @Override
    public void onTaskComplete(String response, String type) {

        if (type.equals("IP")) {
            globalVariable.setIp(response.replaceAll("\n", ""));
            Log.d("ip", globalVariable.getIp());

        } else if (globalVariable.getIp() != null) {

            if (type.equals("SOI-Sequence")) {
                //String result = Encryption.decode(this, response, globalVariable.getIp());

                SaveSOISequence saveSOISequence = new SaveSOISequence(response);
                soiSequenceList = saveSOISequence.getSOISequenceList();
                for (int i = 0; i < soiSequenceList.size(); ++i) {
                    Log.d("id:",soiSequenceList.get(i).getIdentifier());
                    if (soiSequenceList.get(i).getIdentifier().equals("docent")) {
                        Log.d("name:"," "+soiSequenceList.get(i).getContributor());
                        new HttpAsyncTask(SOIActivity.this, "contributor-detail:" + Integer.toString(i),0)
                                .execute(getResources().getString(R.string.api_docent_info) + soiSequenceList.get(i).getContributor());
                    }
                }
                mAdapter = new LOISequenceAdapter(this, soiSequenceList, type, model.getIdentifier(),model.getContributor());
                mRecyclerView.setHasFixedSize(true);
                mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
                mRecyclerView.setAdapter(mAdapter);


                String hint = getString(R.string.find) + " " + soiSequenceList.size() + " " + getString(R.string.sites);
                Snackbar.make(mRecyclerView, hint, Snackbar.LENGTH_SHORT).show();
            } else if (type.contains("contributor-detail")) {
                //String result = Encryption.decode(this, response, globalVariable.getIp());
                Log.d("result:",response);
                try {
                    JSONObject obj = new JSONObject(response);
                    MainActivity.getServer().getSOImodel().setContributorDetail(obj);
                    Log.d("model:","~~"+model.getLOIName());
                    //model.setContributorDetail(obj);
                    Log.d("model::","~"+model.getContributorDetail());
                    /*int index = Integer.valueOf(type.substring("contributor-detail:".length()));

                    soiSequenceList.get(index).setContributorDetail(obj);*/
                } catch (JSONException ex) {
                    ex.printStackTrace();
                }

            }
        }
    }
}

