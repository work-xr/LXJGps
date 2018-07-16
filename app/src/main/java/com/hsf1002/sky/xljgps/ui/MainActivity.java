package com.hsf1002.sky.xljgps.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.*;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.hsf1002.sky.xljgps.*;
import com.hsf1002.sky.xljgps.ReturnMsg.ResultMsg;
import com.hsf1002.sky.xljgps.adapter.MainRecycleAdapter;
import com.hsf1002.sky.xljgps.ReturnMsg.ReceiveMsgBean;
import com.hsf1002.sky.xljgps.presenter.RxjavaHttpPresenterImpl;
import com.hsf1002.sky.xljgps.util.DividerItemDecoration;
import com.hsf1002.sky.xljgps.util.SprdCommonUtils;
import com.hsf1002.sky.xljgps.view.BaseView;

import java.util.ArrayList;
import java.util.List;

import static com.hsf1002.sky.xljgps.util.Constant.DOWNLOAD_INFO_FROM_PLATFORM_INDEX;
import static com.hsf1002.sky.xljgps.util.Constant.REPORT_INFO_TO_PLATFORM_INDEX;
import static com.hsf1002.sky.xljgps.util.Constant.SET_RELATION_NUMBER_INDEX;
import static com.hsf1002.sky.xljgps.util.Constant.UPLOAD_INFO_TO_PLATFORM_INDEX;

public class MainActivity extends AppCompatActivity implements BaseView{
    private static final String TAG = "MainActivity";
    private RecyclerView recyclerView;
    private MainRecycleAdapter adapter;
    private List<String> items = new ArrayList<>();
    private RxjavaHttpPresenterImpl presenter = new RxjavaHttpPresenterImpl(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.main_rv);
        initItems();
        recyclerView.addItemDecoration(new com.hsf1002.sky.xljgps.util.DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MainRecycleAdapter(items);
        adapter.setOnItemClickListener(new MainRecycleAdapter.onItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                switch (position)
                {
                    case SET_RELATION_NUMBER_INDEX:
                        startActivity(new Intent(MainActivity.this, SetRelationNumberActivity.class));
                        break;
                    case UPLOAD_INFO_TO_PLATFORM_INDEX:
                        uploadInfoToPlatform();
                        break;
                    case DOWNLOAD_INFO_FROM_PLATFORM_INDEX:
                        downloadInfoFromPlatform();
                        break;
                    case REPORT_INFO_TO_PLATFORM_INDEX:
                        reportSosGpsInfoToPlatform();
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        });
        recyclerView.setAdapter(adapter);
        requestPermission();
    }

    private void requestPermission()
    {
        List<String> permissionList = new ArrayList<>();

        if (ContextCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            permissionList.add(android.Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (ContextCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED)
        {
            permissionList.add(android.Manifest.permission.READ_PHONE_STATE);
        }
        if (ContextCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
        {
            permissionList.add(android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }

        if (!permissionList.isEmpty())
        {
            String [] permissions = permissionList.toArray(new String[permissionList.size()]);
            ActivityCompat.requestPermissions(MainActivity.this, permissions, 1);
        }
        else
        {
            //requestLocation();
        }
    }

    private void initItems()
    {
        String[]  names = getResources().getStringArray(R.array.main_item_name);

        for (int i=0; i<names.length; ++i)
        {
            items.add(names[i]);
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

    private void downloadInfoFromPlatform()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle(getString(R.string.download_title));
        builder.setMessage(getString(R.string.download_content));
        builder.setPositiveButton(getString(R.string.sure), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                downloadInfo();
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
        presenter.uploadInfo();
    }

    private void downloadInfo()
    {
        presenter.downloadInfo();
    }

    private void reportSosGpsInfoToPlatform()
    {
        presenter.reportInfo();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode)
        {
            case 1:
                if (grantResults.length > 0)
                {
                    for (int result:grantResults)
                    {
                        if (result != PackageManager.PERMISSION_GRANTED)
                        {
                            Toast.makeText(this, getString(R.string.get_gps_permission_fail), Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }
                }
                break;
            default:
                break;
        }
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
    public void downloadSuccess(ResultMsg<ReceiveMsgBean> resultMsg) {
        Toast.makeText(this, getString(R.string.download_success), Toast.LENGTH_SHORT).show();

        String relationName = resultMsg.getData().getRelationship();
        String relationNumber = resultMsg.getData().getPhone();

        Log.d(TAG, "downloadSuccess: relationName = " + relationName);
        Log.d(TAG, "downloadSuccess: relationNumber = " + relationNumber);

        SprdCommonUtils.getInstance().setRelationNumberNames(relationName);
        SprdCommonUtils.getInstance().setRelationNumber(relationNumber);
    }

    @Override
    public void downloadFailed(String resultMsg) {
        Toast.makeText(this, getString(R.string.download_failed), Toast.LENGTH_SHORT).show();
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
