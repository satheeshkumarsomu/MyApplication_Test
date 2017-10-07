package com.example.admin.myapplication;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Display;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener, AsyncTaskCompleteListener, Response.ErrorListener {
    Activity activity;
    SQLiteDatabase db;
    ArrayList<ItemModel> item_model = new ArrayList<ItemModel>();
    int page = 1;
    private SwipeRefreshLayout swipeRefreshLayout;
    private Handler mHandler;
    Item_Adapter mAdapter;
    RequestQueue requestQueue;
    private boolean loadmore = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        activity=this;
        requestQueue = Volley.newRequestQueue(activity);
        createDataBase();

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.rv_View);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setColorSchemeResources(
                R.color.colorPrimary,
                R.color.colorPrimary,
                R.color.colorPrimary);
        swipeRefreshLayout.setOnRefreshListener(this);
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int height = size.y;
        swipeRefreshLayout.setProgressViewOffset(false, -200, ((height / 9) + 100));
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(false);
                String count = "SELECT count(*) FROM item";
                Cursor mcursor = db.rawQuery(count, null);
                mcursor.moveToFirst();
                int icount = mcursor.getInt(0);
                if(icount>0){
                    swipeRefreshLayout.setRefreshing(false);
                    new HomeAsync().execute();
                }else{
                    new LoadAsync().execute();
                }
            }
        });

        recyclerView.setHasFixedSize(true);
        LinearLayoutManager manager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(manager);

        mAdapter = new Item_Adapter(activity, item_model, recyclerView);
        recyclerView.setAdapter(mAdapter);
//		recyclerView.addItemDecoration(new SimpleDividerItemDecoration(this));
        mAdapter.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                //add null , so the adapter will check view_type and show progress bar at bottom
                if (!loadmore) {
                    item_model.add(null);
                    mAdapter.notifyItemInserted(item_model.size() - 1);
                    mHandler = new Handler();
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            //   remove progress item
                            Log.d("ttt", "load");
                            getProductScroll(page);

                        }
                    }, 2000);
                }

            }
        });

    }
    public void createDataBase() {
        db = openOrCreateDatabase(DataBaseManager.DATABASE_NAME, MODE_PRIVATE, null);
        try {
            db.execSQL(DataBaseManager.CREATE_TABLE);

            db.delete(DataBaseManager.CREATE_TABLE, null, null);
        } catch (Exception e) {
            // TODO: handle exception
        }
    }
    public void getProductScroll(int page) {

        if (!AnyUtils.isNetworkAvailable(activity)) {
            AnyUtils.showToast("no_internet", activity);
            item_model.remove(item_model.size() - 1);
            mAdapter.notifyItemRemoved(item_model.size());
            mAdapter.notifyItemInserted(item_model.size());
            mAdapter.setLoaded();
            return;
        }
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("url", "https://www.anapioficeandfire.com/api/characters?page="+page+"&pageSize=10");
        requestQueue.add(new VolleyHttpRequest(Request.Method.GET, map, 2, this, this));

    }

    public void getProduct(int page) {
        if (!AnyUtils.isNetworkAvailable(activity)) {
            AnyUtils.showToast("no_internet", activity);
            swipeRefreshLayout.setRefreshing(false);
            return;
        }
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("url", "https://www.anapioficeandfire.com/api/characters?pageSize=100");
        requestQueue.add(new VolleyHttpRequest(Request.Method.GET, map, 1, this, this));

    }
    class LoadAsync extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
//			AnyUtils.startDialog(activity);
        }

        @Override
        protected Void doInBackground(Void... params) {
            // TODO Auto-generated method stub
            getProduct(page);
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
        }
    }



    @Override
    public void onRefresh() {
        loadmore = false;
        mHandler = new Handler();
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (swipeRefreshLayout.isRefreshing()) {
                    swipeRefreshLayout.setRefreshing(false);
                    swipeRefreshLayout.setRefreshing(true);
                }
                page = 1;

                getProduct(page);

            }
        }, 2000);
    }
    @Override
    public void onErrorResponse(VolleyError error) {
        // TODO Auto-generated method stub
//		AnyUtils.showserver_popup(getContext());
        Log.d("VolleyError", "error=" + error);


    }
    @Override
    public void onTaskCompleted(String response, int serviceCode) {
        // TODO Auto-generated method stub

       if (swipeRefreshLayout.isRefreshing()) {
            swipeRefreshLayout.setRefreshing(false);
        }
        switch (serviceCode) {
            case 1:
                try {
                    page++;
                    item_model.clear();
                    db.delete(DataBaseManager.TABLE, null, null);
                    JSONArray jarr = new JSONArray(response);
                    String name="";
                    for (int i = 0; i < jarr.length(); i++) {
                        JSONObject obj = jarr.getJSONObject(i);
                        name=obj.getString("name");
                        if(name.matches("")){
                            JSONArray arr=obj.getJSONArray("aliases");
                            name=arr.getString(0);
                            Log.d("ttt","s="+name);

                        }
                        String gender=obj.getString("gender");
                        ContentValues values = new ContentValues();
                        values.put("name", name);
                        values.put("gender", gender);
                        db.insert(DataBaseManager.TABLE, null, values);

                        ItemModel model = new ItemModel(name,gender);
                        item_model.add(model);
                    }

                    mAdapter.notifyDataSetChanged();


                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                break;
            case 2:
                item_model.remove(item_model.size() - 1);
                mAdapter.notifyItemRemoved(item_model.size());
                try {
                    page++;
                    JSONArray jarr = new JSONArray(response);
                    String name="";
                    for (int i = 0; i < jarr.length(); i++) {
                        JSONObject obj = jarr.getJSONObject(i);
                        name=obj.getString("name");
                        if(name.matches("")){
                            JSONArray arr=obj.getJSONArray("aliases");
                            name=arr.getString(0);
                            Log.d("ttt","s="+name);

                        }
                        String gender=obj.getString("gender");
                        ContentValues values = new ContentValues();
                        values.put("name", name);
                        values.put("gender", gender);
                        db.insert(DataBaseManager.TABLE, null, values);

                        ItemModel model = new ItemModel(name,gender);
                        item_model.add(model);
                    }
                    mAdapter.notifyItemInserted(item_model.size());
                    mAdapter.setLoaded();
                    mAdapter.notifyDataSetChanged();

                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                break;
            default:
                break;
        }


    }
    class HomeAsync extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            // TODO Auto-generated method stub
            Cursor cs = db.rawQuery("select * from item", null);
            if (cs.moveToFirst()) {
                do {
                    String name = cs.getString(1);
                    String gender = cs.getString(2);

                    ItemModel model = new ItemModel(name,gender);
                    item_model.add(model);

                } while (cs.moveToNext());
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mAdapter.notifyDataSetChanged();
                }
            });

        }
    }
}
