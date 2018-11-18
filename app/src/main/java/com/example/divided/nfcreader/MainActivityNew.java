package com.example.divided.nfcreader;

import android.app.PendingIntent;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.NfcV;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

public class MainActivityNew extends AppCompatActivity {

    NfcAdapter mNfcAdapter;
    PendingIntent mPendingIntent;
    NfcV nfcvTag;
    RelativeLayout configSegment;
    RelativeLayout initLayout;
    RelativeLayout infoSegment;
    LinearLayout spinnersSegment;
    Button mSingle;
    Button mMulti;
    Button mStartMeasure;
    EditText measureCount;
    EditText measureTimeStamp;
    TextView textViewSerialNumber;
    TextView textViewTechAvailable;
    Spinner measureTimeUnit;
    boolean isSingleChoosen = false;
    boolean isMuliChoosen = false;


    @Override
    protected void onNewIntent(Intent intent) {
        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction()) || NfcAdapter.ACTION_TECH_DISCOVERED.equals(intent.getAction())) {

            Tag currentTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            byte[] id = currentTag.getId();

            String currentNfcId = Utils.byteArrayToHexString(id);
            String[] idSplitted = Utils.splitToNChar(currentNfcId, 2);
            StringBuilder idBuilder = new StringBuilder();
            for (int i = 0; i < idSplitted.length; i++) {
                if (i < idSplitted.length - 1) {
                    idBuilder.append(idSplitted[i]).append(":");
                } else {
                    idBuilder.append(idSplitted[i]);
                }

            }

            textViewSerialNumber.setText(idBuilder.toString());

            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < currentTag.getTechList().length; i++) {
                if (i < currentTag.getTechList().length - 1) {
                    sb.append(currentTag.getTechList()[i]).append(", ");
                } else {
                    sb.append(currentTag.getTechList()[i]);
                }
            }
            textViewTechAvailable.setText(sb.toString());

            for (String tech : currentTag.getTechList()) {

                if (tech.equals(NfcV.class.getName())) {
                    nfcvTag = NfcV.get(currentTag);

                    try {
                        nfcvTag.connect();
                        initLayout.setVisibility(View.GONE);
                        configSegment.setVisibility(View.VISIBLE);
                        infoSegment.setVisibility(View.VISIBLE);
                    } catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(), "Could not open a connection!", Toast.LENGTH_SHORT).show();
                        return;
                    }


                }
            }
        }

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_new);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        initLayout = findViewById(R.id.init_layout);
        configSegment = findViewById(R.id.config_layout);
        configSegment.setVisibility(View.GONE);
        infoSegment = findViewById(R.id.nfc_info_segment);
        infoSegment.setVisibility(View.GONE);
        TextView nfcSerialLabel = findViewById(R.id.nfc_serial).findViewById(R.id.text_view_label);
        nfcSerialLabel.setText("Serial number");
        TextView nfcTechLabel = findViewById(R.id.nfc_technologies).findViewById(R.id.text_view_label);
        nfcTechLabel.setText("Technologies available");
        textViewSerialNumber = findViewById(R.id.nfc_serial).findViewById(R.id.text_view_info);
        textViewTechAvailable = findViewById(R.id.nfc_technologies).findViewById(R.id.text_view_info);




        spinnersSegment = findViewById(R.id.spinners_layout);
        spinnersSegment.setVisibility(View.GONE);
        mSingle = findViewById(R.id.button_single);
        mMulti = findViewById(R.id.button_multi);
        mStartMeasure = findViewById(R.id.button_start);
        mStartMeasure.setVisibility(View.GONE);
        measureCount = findViewById(R.id.edit_text_count);
        measureTimeStamp = findViewById(R.id.edit_text_time_stamp);
        measureTimeUnit = findViewById(R.id.spinner_time_unit);
        measureTimeUnit.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, getResources().getStringArray(R.array.config_spinner_time_unit)));

        mSingle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSingle.setSelected(true);
                mMulti.setSelected(false);
                isSingleChoosen = true;
                isMuliChoosen = false;
                spinnersSegment.setVisibility(View.GONE);
                mStartMeasure.setVisibility(View.VISIBLE);
            }
        });

        mMulti.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMulti.setSelected(true);
                mSingle.setSelected(false);
                isSingleChoosen = false;
                isMuliChoosen = true;
                spinnersSegment.setVisibility(View.VISIBLE);
                mStartMeasure.setVisibility(View.VISIBLE);
            }
        });

        mStartMeasure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isMuliChoosen) {
                    final boolean status = verifyInputData();
                }else if(isSingleChoosen){

                }
            }
        });


        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (mNfcAdapter == null) {
            Toast.makeText(this, "Device don't have NFC adapter", Toast.LENGTH_LONG).show();
        }

        mPendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass())
                .addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);

    }

    private boolean verifyInputData() {
        int inputCount;
        int inputTimeStamp;

        if (!measureCount.getText().toString().trim().isEmpty() && !measureTimeStamp.getText().toString().trim().isEmpty()) {
            inputCount = Integer.parseInt(measureCount.getText().toString());
            inputTimeStamp = Integer.parseInt(measureTimeStamp.getText().toString());
        } else {
            measureCount.getText().clear();
            measureTimeStamp.getText().clear();
            measureCount.requestFocus();
            Toast.makeText(this, "Input data is invalid", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (inputCount > 2 && inputCount < 200 && inputTimeStamp > 0) {
            return true;
        }

        Toast.makeText(this, "Input data is invalid", Toast.LENGTH_SHORT).show();
        measureCount.getText().clear();
        measureTimeStamp.getText().clear();
        measureCount.requestFocus();
        return false;
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mNfcAdapter != null) {
            mNfcAdapter.disableForegroundDispatch(this);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mNfcAdapter.enableForegroundDispatch(this, mPendingIntent, null, null);
    }
}

