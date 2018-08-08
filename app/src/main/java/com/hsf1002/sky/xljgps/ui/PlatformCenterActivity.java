package com.hsf1002.sky.xljgps.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.hsf1002.sky.xljgps.R;
import com.hsf1002.sky.xljgps.adapter.MainRecycleAdapter;
import com.hsf1002.sky.xljgps.util.DividerItemDecoration;
import com.hsf1002.sky.xljgps.util.SharedPreUtils;

import java.util.ArrayList;
import java.util.List;

import static android.view.KeyEvent.KEYCODE_DPAD_CENTER;
import static com.hsf1002.sky.xljgps.util.Constant.RELATION_NAME;
import static com.hsf1002.sky.xljgps.util.Constant.RELATION_NUMBER;
import static com.hsf1002.sky.xljgps.util.Constant.RELATION_NUMBER_COUNT;
import static com.hsf1002.sky.xljgps.util.Constant.RELATION_NUMBER_DEFAULT;

/**
*  author:  hefeng
*  created: 18-7-31 下午2:07
*  desc:   之前的设计是一个孝老平台中心号码+3个亲情号码, 现在3个亲情号码不在此地设置, 从SOS模块中获取
*/
public class PlatformCenterActivity extends Activity {
    private static final String TAG = "RelationNumberActivity";
    private RecyclerView recyclerView;
    private MainRecycleAdapter adapter;
    private List<String> items = new ArrayList<String>();
    private List<String> relationNumbers = new ArrayList<String>();

    private EditText relationNumberEt;
    @Deprecated
    private static int relationNumberCount = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        recyclerView = (RecyclerView) findViewById(R.id.main_rv);

        initItems();
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MainRecycleAdapter(items);
        adapter.refreshItem(0);
        adapter.setOnItemClickListener(new MainRecycleAdapter.onItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                setRelationNumberAlert(position);
            }

            @Override
            public void onItemLongClick(View view, int position) {
                dialRelationNumber(position);
            }
        });
        recyclerView.setAdapter(adapter);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode)
        {
            case KEYCODE_DPAD_CENTER:
                setRelationNumberAlert(0);
                break;
        }

        Log.i(TAG, "onKeyDown: keyCode = " + keyCode);

        return super.onKeyDown(keyCode, event);
    }

    private void setRelationNumberAlert(final int position)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.set_relation_number, null);
        relationNumberEt = (EditText) view.findViewById(R.id.relation_number_et);

        if (relationNumbers.get(position) != null)
        {
            relationNumberEt.setText(relationNumbers.get(position).toString());
            relationNumberEt.setSelection(relationNumbers.get(position).toString().length());
        }
        builder.setTitle(getString(R.string.set_number));
        builder.setView(view);
        builder.setPositiveButton(getString(R.string.sure), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if (position == 0 && TextUtils.isEmpty(relationNumberEt.getText().toString()))
                {
                    Toast.makeText(PlatformCenterActivity.this, getString(R.string.center_number_cannot_empty), Toast.LENGTH_SHORT).show();
                    return;
                }
                setRelationNumber(position);
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

    private void dialRelationNumber(int position)
    {
        String number = relationNumbers.get(position);

        Intent intent = new Intent(Intent.ACTION_DIAL);  // ACTION_CALL -require user verify permission
        Uri data = Uri.parse("tel:" + number);
        intent.setData(data);
        startActivity(intent);
    }

    private void getPresetRelationNumber()
    {
        String[]  names = getResources().getStringArray(R.array.relation_item_name);
        int nameCount = 0;//SharedPreUtils.getInstance().getInt(RELATION_NAME_COUNT, 0);
        int numberCount = SharedPreUtils.getInstance().getInt(RELATION_NUMBER_COUNT, 0);

        relationNumbers.clear();
        items.clear();

        Log.i(TAG, "getPresetRelationNumber: nameCount = " + nameCount + ", numberCount = " + numberCount);

        if (nameCount == 0)
        {
            relationNumberCount = 0;
            nameCount = names.length;

            for (int i=0; i<nameCount; ++i)
            {
                String itemName = names[i];
                String relationNumberStr = SharedPreUtils.getInstance().getString(RELATION_NUMBER, RELATION_NUMBER_DEFAULT);
                SharedPreUtils.getInstance().putString(RELATION_NAME + i, itemName);

                if (!TextUtils.isEmpty(relationNumberStr))
                {
                    itemName += ": " + relationNumberStr;
                    relationNumberCount++;
                }
                relationNumbers.add(i, relationNumberStr);
                items.add(itemName);
            }
            //SharedPreUtils.getInstance().putInt(RELATION_NAME_COUNT, nameCount);
        }
        else
        {
            setDataUpdate();
        }
    }

    private void setDataUpdate()
    {
        int nameCount = 1;// SharedPreUtils.getInstance().getInt(RELATION_NAME_COUNT, 0);
        relationNumbers.clear();
        items.clear();

        for (int i=0; i<nameCount; ++i)
        {
            String itemName = SharedPreUtils.getInstance().getString(RELATION_NAME + i, "");
            String relationNumberStr = SharedPreUtils.getInstance().getString(RELATION_NUMBER, "");

            if (!TextUtils.isEmpty(relationNumberStr))
            {
                itemName += ":  " + relationNumberStr;
            }
            relationNumbers.add(i, relationNumberStr);
            items.add(itemName);
        }
    }

    private void initItems()
    {
        getPresetRelationNumber();
    }

    private void setRelationNumber(int position)
    {
        String currentNumberStr = relationNumberEt.getText().toString();
        relationNumbers.set(position, currentNumberStr);
        SharedPreUtils.getInstance().putString(RELATION_NUMBER, currentNumberStr);

        setDataUpdate();
        if (!TextUtils.isEmpty(currentNumberStr)) {
            Toast.makeText(PlatformCenterActivity.this, getString(R.string.set_relation_number_success), Toast.LENGTH_SHORT).show();
        }
        adapter.notifyItemChanged(position);
    }

    @Deprecated
    private void saveRelationNumberCount()
    {
        relationNumberCount = 0;

        for (int i=0; i<4; ++i)
        {
            String relationNumberStr = SharedPreUtils.getInstance().getString(RELATION_NUMBER + i, "");

            if (!TextUtils.isEmpty(relationNumberStr))
            {
                relationNumberCount++;
            }
        }
        SharedPreUtils.getInstance().putInt(RELATION_NUMBER_COUNT, relationNumberCount);
        Log.i(TAG, "saveRelationNumberCount: relationNumberCount = " + relationNumberCount);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(TAG, "onPause: save real number count............");
        saveRelationNumberCount();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "onResume: refreshing.......................");
    }
}
