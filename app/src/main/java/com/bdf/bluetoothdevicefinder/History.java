package com.bdf.bluetoothdevicefinder;


import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;



/**
 * A simple {@link Fragment} subclass.
 */
public class History extends Fragment {
    HistoryAdapter historyAdapter;
    List<String> names;
    List<String> deviceNames = new ArrayList<>();
    ArrayAdapter<String> listAdapter;
    AutoCompleteTextView autoCompleteTextView;
    RadioGroup radioGroup;
    ArrayAdapter<String> searchAdapter;
    int mode = 1;
    ListView listView;
    int namesHolder = 0;


    public History() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View v = inflater.inflate(R.layout.fragment_history, container, false);
        ListDevice.setContext(getActivity());
        ListDevice.load();
        names = new ArrayList<>();
        radioGroup = v.findViewById(R.id.radioGroup);
        autoCompleteTextView = v.findViewById(R.id.editText);
        deviceNames = ListDevice.getDeviceName();
        searchAdapter = new ArrayAdapter<String>(v.getContext(), android.R.layout.simple_list_item_1, deviceNames);
        autoCompleteTextView.setAdapter(searchAdapter);
        names = ListDevice.getTop();
        final List<String> names2 = ListDevice.getNames();
        final List<String> names3 = names;
        namesHolder = ListDevice.getNamesSize();
        listView = v.findViewById(R.id.history_list);
        listAdapter = new ArrayAdapter<String>(v.getContext(), android.R.layout.simple_list_item_1, names);
        listView.setAdapter(listAdapter);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.deviceName:
                        deviceNames = ListDevice.getDeviceName();
                        searchAdapter = new ArrayAdapter<String>(v.getContext(), android.R.layout.simple_list_item_1, deviceNames);
                        autoCompleteTextView.setAdapter(searchAdapter);
                        autoCompleteTextView.setHint("Search By Device Name");
                        break;
                    case R.id.bluetoothId:
                        deviceNames = ListDevice.getMac();
                        searchAdapter = new ArrayAdapter<String>(v.getContext(), android.R.layout.simple_list_item_1, deviceNames);
                        autoCompleteTextView.setAdapter(searchAdapter);
                        autoCompleteTextView.setHint("Search By Bluetooth Id");
                        break;
                    case R.id.time:
                        deviceNames = ListDevice.getSearchDate();
                        searchAdapter = new ArrayAdapter<String>(v.getContext(), android.R.layout.simple_list_item_1, deviceNames);
                        autoCompleteTextView.setAdapter(searchAdapter);
                        autoCompleteTextView.setHint("Search By Date");
                        break;
                }
            }
        });
        ImageView imageView = v.findViewById(R.id.search);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                names = new ArrayList<>();
                String name = autoCompleteTextView.getText().toString();
                name = name.trim();
                if(name.equals("")){
                    names = names3;
                }else {
                    switch (radioGroup.getCheckedRadioButtonId()) {
                        case R.id.deviceName:
                            mode = 1;
                            names.add(ListDevice.getTop(ListDevice.isPresentAtPos(name)));
                            break;
                        case R.id.bluetoothId:
                            mode = 2;
                            names.add(ListDevice.getTop(ListDevice.isPresentAtPosMac(name)));
                            break;
                        case R.id.time:
                            mode = 3;
                            names.add(ListDevice.getTop(ListDevice.isPresentAtPosSearchDate(name)));
                            break;
                    }
                }
                listAdapter = new ArrayAdapter<String>(v.getContext(), android.R.layout.simple_list_item_1, names);
                listView.setAdapter(listAdapter);
            }
        });
        return v;
    }

    @Override
    public void onResume() {
        if(ListDevice.updateSize()){
            historyAdapter.notifyDataSetChanged();
        }
        super.onResume();
    }


}
