package com.bdf.bluetoothdevicefinder;


import android.Manifest;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static com.bdf.bluetoothdevicefinder.ListDevice.context;


/**
 * A simple {@link Fragment} subclass.
 */
public class Export extends Fragment {
    int days;


    public Export() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_export, container, false);
        v.findViewById(R.id.export).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int permission = getActivity().checkSelfPermission("Manifest.permission.WRITE_EXTERNAL_STORAGE");
                permission+= getActivity().checkSelfPermission("Manifest.permission.READ_EXTERNAL_STORAGE");
                if(permission!=0){
                    getActivity().requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE} , 1001);
                }
                try {
                    createPDf();
                }catch (Exception e){
                    Log.d("Tag" , e.toString());
                }
            }
        });
        Spinner spinner = v.findViewById(R.id.spinner);
        List<String> dayCount = new ArrayList<>();
        dayCount.add("1");
        dayCount.add("2");
        dayCount.add("3");
        dayCount.add("4");
        dayCount.add("5");
        dayCount.add("6");
        dayCount.add("7");
        dayCount.add("8");
        dayCount.add("9");
        dayCount.add("10");
        dayCount.add("11");
        dayCount.add("12");
        dayCount.add("13");
        dayCount.add("14");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(v.getContext(), android.R.layout.simple_spinner_item, dayCount);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                days = position+1;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        return v;
    }

    void createPDf() throws Exception{
        String text = "\t"+setTextWithSpan("Antar Setu: Contact History "+days+" day(s)", new StyleSpan(Typeface.BOLD))+"\n\n";
        ListDevice.setContext(getActivity());
        ListDevice.load();
        text = text.concat("Name :"+ Settings.Global.getString(getActivity().getApplicationContext().getContentResolver(), "device_name")+"\n"
                +"History Generated On: "+ getCalculatedDate("dd/MM/yyyy", 0) + "\n"+"\n");
        for (int i = 0;i<ListDevice.deviceName.size();i++){
            String deadLineDate = getCalculatedDate("dd/MM/yyyy", -days);
            String currentDate = getCalculatedDate("dd/MM/yyyy", 0);
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/mm/yyyy");
            Date date1 = simpleDateFormat.parse(ListDevice.getDate(i));
            Date date2 = simpleDateFormat.parse(deadLineDate);
            Date date3 = simpleDateFormat.parse(currentDate);
            if(date1.after(date2)) {
                text = text.concat((i + 1) + ". " + ListDevice.getDeviceNameAtPosition(i) + "\n" + "\tStrongest Connection on: " + ListDevice.getDateTimeAtPosition(i) + "\n"
                        + "\t" + ListDevice.getStrongestConnectionAtPos(i) + " dBm" + "\n"+"\t"+ListDevice.getMac(i)+ "\n\n");
            }
        }
        Document document = new Document();

        String folder_main = "AntarSetu";
        File f = new File(Environment.getExternalStorageDirectory(), folder_main);
        if (!f.exists()) {
            f.mkdirs();
        }
        String outPath = Environment.getExternalStorageDirectory()+ "/AntarSetu/Contact_History_"+days+"_Days.pdf";
        try {
            PdfWriter.getInstance(document, new FileOutputStream(outPath));
            document.open();
            document.add(new Paragraph(text));
            document.close();

        }catch (Exception e){
            Log.d("Error", e.toString());
        }


    }

    public static String getCalculatedDate(String dateFormat, int days) {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat s = new SimpleDateFormat(dateFormat);
        cal.add(Calendar.DAY_OF_YEAR, days);
        return s.format(new Date(cal.getTimeInMillis()));
    }

    String setTextWithSpan(String text, StyleSpan style) {
        SpannableStringBuilder sb = new SpannableStringBuilder(text);
        sb.setSpan(style, 0, text.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        return sb.toString();
    }

}
