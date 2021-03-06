package com.hsf1002.sky.xljgps.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;

import com.hsf1002.sky.xljgps.R;
import com.hsf1002.sky.xljgps.adapter.MainRecycleAdapter;
import com.hsf1002.sky.xljgps.presenter.NetworkPresenterImpl;
import com.hsf1002.sky.xljgps.result.ResultMsg;
import com.hsf1002.sky.xljgps.service.SocketService;
import com.hsf1002.sky.xljgps.util.DividerItemDecoration;
import com.hsf1002.sky.xljgps.util.SprdCommonUtils;
import com.hsf1002.sky.xljgps.view.BaseView;

import java.util.ArrayList;
import java.util.List;

import static android.view.KeyEvent.KEYCODE_DPAD_CENTER;
import static android.view.KeyEvent.KEYCODE_DPAD_DOWN;
import static android.view.KeyEvent.KEYCODE_DPAD_UP;
import static com.hsf1002.sky.xljgps.util.Constant.REPORT_INFO_TO_PLATFORM_INDEX;
import static com.hsf1002.sky.xljgps.util.Constant.UPLOAD_INFO_TO_PLATFORM_INDEX;

/**
*  author:  hefeng
*  created: 18-8-13 下午7:41
*  desc:    孝老平台主界面
*  param:
*  return:
*/


public class MainActivity extends Activity implements BaseView{
    private static final String TAG = "MainActivity";
    private RecyclerView recyclerView;
    private MainRecycleAdapter adapter;
    private List<String> items = new ArrayList<String>();
    private NetworkPresenterImpl presenter = new NetworkPresenterImpl(this);
    private int currentPosition = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = (RecyclerView) findViewById(R.id.main_rv);
        initItems();
        recyclerView.addItemDecoration(new com.hsf1002.sky.xljgps.util.DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MainRecycleAdapter(items);
        adapter.refreshItem(currentPosition);
        adapter.setOnItemClickListener(new MainRecycleAdapter.onItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                handlePlatformItems(position);
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        });
        recyclerView.setAdapter(adapter);
    }

    private void initItems()
    {
        String[]  names = getResources().getStringArray(R.array.main_item_name);

        for (int i=0; i<names.length; ++i)
        {
            items.add(names[i]);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        int count = getResources().getStringArray(R.array.main_item_name).length;

        switch (keyCode)
        {
            case KEYCODE_DPAD_UP:
                if (currentPosition == 0)
                {
                    currentPosition = count - 1;
                }
                else
                {
                    currentPosition--;
                }
                break;
            case KEYCODE_DPAD_DOWN:
                if (currentPosition == count - 1)
                {
                    currentPosition = 0;
                }
                else
                {
                    currentPosition++;
                }
                break;
            case KEYCODE_DPAD_CENTER:
                handlePlatformItems(currentPosition);
                break;
        }

        adapter.refreshItem(currentPosition);
        
        Log.i(TAG, "onKeyDown: keyCode = " + keyCode + ", currentPosition = " + currentPosition);

        return super.onKeyDown(keyCode, event);
    }

    private void handlePlatformItems(int position)
    {
        switch (position)
        {
            /*case SET_RELATION_NUMBER_INDEX:
                startActivity(new Intent(MainActivity.this, PlatformCenterActivity.class));
                break;*/
            case UPLOAD_INFO_TO_PLATFORM_INDEX:
                //uploadInfoToPlatform();
                SocketService.getInstance().terminatedService(true);
                break;
            case REPORT_INFO_TO_PLATFORM_INDEX:
                //reportSosGpsInfoToPlatform();
                SprdCommonUtils.getInstance().sendSosSmsBroadcast();
                break;
            default:
                break;
        }
    }

    private void uploadInfoToPlatform()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle(getString(R.string.upload_title));
        builder.setMessage(getString(R.string.upload_content));
        builder.setPositiveButton(getString(R.string.sure), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                uploadInfo();
            }
        });
        builder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setCancelable(true).create();
        builder.show();
    }

    private void uploadInfo()
    {
        presenter.uploadRelationNumber();
    }


    private void reportSosGpsInfoToPlatform()
    {
        presenter.reportSosPosition();
    }

    @Override
    public void uploadSuccess(ResultMsg resultMsg) {
        Toast.makeText(this, getString(R.string.upload_success), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void uploadFailed(String resultMsg) {
        Toast.makeText(this, getString(R.string.upload_failed), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void reportSuccess(ResultMsg resultMsg) {
        Toast.makeText(this, getString(R.string.report_success), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void reportFailed(String resultMsg) {
        Toast.makeText(this, getString(R.string.report_failed), Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
