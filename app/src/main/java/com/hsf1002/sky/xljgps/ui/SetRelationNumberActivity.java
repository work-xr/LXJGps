package com.hsf1002.sky.xljgps.ui;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.hsf1002.sky.xljgps.util.DividerItemDecoration;
import com.hsf1002.sky.xljgps.adapter.MainRecycleAdapter;
import com.hsf1002.sky.xljgps.R;
import com.hsf1002.sky.xljgps.util.SharedPreUtils;

import java.util.ArrayList;
import java.util.List;

import static com.hsf1002.sky.xljgps.util.Const.RELATION_NUMBER;
import static com.hsf1002.sky.xljgps.util.Const.RELATION_NUMBER_COUNT;

/**
 * Created by hefeng on 18-6-6.
 */

public class SetRelationNumberActivity extends AppCompatActivity {
    private static final String TAG = "SetRelationNumberActivity";
    private RecyclerView recyclerView;
    private MainRecycleAdapter adapter;
    private List<String> items = new ArrayList<>();
    private List<String> relationNumbers = new ArrayList<>();

    //private static final String RELATION_NUMBER_1 = "relation_number_1";
    //private static final String RELATION_NUMBER_2 = "relation_number_2";
    //private static final String RELATION_NUMBER_3 = "relation_number_3";

    private EditText relationNumberEt;
    private static int relationNumberCount = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.main_rv);
        initItems();
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MainRecycleAdapter(items);
        adapter.setOnItemClickListener(new MainRecycleAdapter.onItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                setRelationNumberAlert(position);
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        });
        recyclerView.setAdapter(adapter);
    }

    private void setRelationNumberAlert(final int position)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.set_relation_number, null);
        relationNumberEt = view.findViewById(R.id.relation_number_et);

        if (relationNumbers.get(position) != null)
        {
            relationNumberEt.setText(relationNumbers.get(position).toString());
        }
        builder.setTitle(getString(R.string.set_relation_number));
        builder.setView(view);
        builder.setPositiveButton(getString(R.string.sure), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
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

    private void getPresetRelationNumber()
    {
        String[]  names = getResources().getStringArray(R.array.relation_item_name);

        relationNumbers.clear();
        items.clear();
        relationNumberCount = 0;

        for (int i=0; i<names.length; ++i)
        {
            String itemName = names[i];

            String relationNumberStr = SharedPreUtils.getInstance().getString(RELATION_NUMBER + i, "");

            if (!TextUtils.isEmpty(relationNumberStr))
            {
                itemName += ":  " + relationNumberStr;
                relationNumberCount++;
            }
            relationNumbers.add(i, relationNumberStr);
            items.add(itemName);
        }
    }

    public void setRelationNumberFromPlatform(List<String> list)
    {
        for (int i = 0; i < relationNumberCount; ++i)
        {
            String itemName = list.get(i).toString();
            SharedPreUtils.getInstance().putString(RELATION_NUMBER + i, itemName);
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
        SharedPreUtils.getInstance().putString(RELATION_NUMBER + position, currentNumberStr);
    }

    private void saveRelationNumberCount()
    {
        relationNumberCount = 0;

        for (int i=0; i<3; ++i)
        {
            String relationNumberStr = SharedPreUtils.getInstance().getString(RELATION_NUMBER + i, "");

            if (!TextUtils.isEmpty(relationNumberStr))
            {
                relationNumberCount++;
            }
        }
        SharedPreUtils.getInstance().putInt(RELATION_NUMBER_COUNT, relationNumberCount);
        Log.d(TAG, "saveRelationNumberCount: relationNumberCount = " + relationNumberCount);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause: save real number count............");
        saveRelationNumberCount();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: refreshing.......................");
    }
}
