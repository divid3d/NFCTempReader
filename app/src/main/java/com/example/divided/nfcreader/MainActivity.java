package com.example.divided.nfcreader;

import android.app.PendingIntent;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.NfcV;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.divided.nfcreader.model.WriteCommand;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    NfcAdapter mNfcAdapter;
    PendingIntent mPendingIntent;
    TextView mNfcData;
    EditText mNfcCommand;
    Button mNfcSendCommand;
    Button mLoadConfig;
    Button mNewMain;
    Button mReadData;
    NfcV nfcvTag;
    Button mFragmentTest;
    RelativeLayout mNfcInfoSegment;
    String currentNfcId;


    @Override
    protected void onNewIntent(Intent intent) {
        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction()) || NfcAdapter.ACTION_TECH_DISCOVERED.equals(intent.getAction())) {

            mNfcInfoSegment.setVisibility(View.VISIBLE);
            RelativeLayout nfcSerialView = mNfcInfoSegment.findViewById(R.id.nfc_serial);
            TextView nfcSerialViewLabel = nfcSerialView.findViewById(R.id.text_view_label);
            nfcSerialViewLabel.setText("Serial number");
            TextView nfcSerialViewValue = nfcSerialView.findViewById(R.id.text_view_info);

            RelativeLayout nfcTechnologiesView = mNfcInfoSegment.findViewById(R.id.nfc_technologies);
            TextView nfcTechnologiesViewLabel = nfcTechnologiesView.findViewById(R.id.text_view_label);
            nfcTechnologiesViewLabel.setText("Technologies available");
            TextView nfcTechnologiesViewValue = nfcTechnologiesView.findViewById(R.id.text_view_info);

            Tag currentTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            byte[] id = currentTag.getId();

            currentNfcId = Utils.byteArrayToHexString(id);
            String[] idSplitted = Utils.splitToNChar(currentNfcId, 2);
            StringBuilder idBuilder = new StringBuilder();
            for (int i = 0; i < idSplitted.length; i++) {
                if (i < idSplitted.length - 1) {
                    idBuilder.append(idSplitted[i]).append(":");
                } else {
                    idBuilder.append(idSplitted[i]);
                }

            }

            nfcSerialViewValue.setText(idBuilder.toString());
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < currentTag.getTechList().length; i++) {
                if (i < currentTag.getTechList().length - 1) {
                    sb.append(currentTag.getTechList()[i]).append(", ");
                } else {
                    sb.append(currentTag.getTechList()[i]);
                }
            }
            nfcTechnologiesViewValue.setText(sb.toString());
            for (String tech : currentTag.getTechList()) {

                if (tech.equals(NfcV.class.getName())) {
                    nfcvTag = NfcV.get(currentTag);

                    try {
                        nfcvTag.connect();
                        //txtType.setText("Hello NFC!");
                    } catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(), "Could not open a connection!", Toast.LENGTH_SHORT).show();
                        return;
                    }
/*
                    try {
                        int offset = 0;  // offset of first block to read
                        int blocks = 1;  // number of blocks to read
                        byte[] cmd = new byte[]{
                                (byte)0x60,                  // flags: addressed (= UID field present)
                                (byte)0x23,                  // command: READ MULTIPLE BLOCKS
                                id[0], id[1], id[2], id[3], id[4], id[5], id[6], id[7],  // placeholder for tag UID
                                (byte)(offset & 0x0ff),      // first block number
                                (byte)((blocks - 1) & 0x0ff) // number of blocks (-1 as 0x00 means one block)
                        };
                        System.arraycopy(id, 0, cmd, 2, 8);
                        String cmd = "03A21F05";
                        byte[] response = nfcvTag.transceive(Utils.hexStringToByteArray(cmd));
                        mNfcData.setText("DATA:" + Utils.byteArrayToHexString(response));

                    } catch (IOException e) {
                        Toast.makeText(getApplicationContext(), "An error occurred while reading!", Toast.LENGTH_SHORT).show();
                        return;
                    }*/
                }
            }
        }

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mNfcData = findViewById(R.id.nfc_terminal);
        mNfcData.setMovementMethod(new ScrollingMovementMethod());
        mNfcCommand = findViewById(R.id.nfc_command);
        mNfcSendCommand = findViewById(R.id.nfc_send_command);
        mNfcInfoSegment = findViewById(R.id.nfc_info_segment);
        mLoadConfig = findViewById(R.id.button_load_config);
        mFragmentTest = findViewById(R.id.button_fragment);
        mNewMain = findViewById(R.id.button_new_main);
        mReadData = findViewById(R.id.button_read);

        mReadData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //readData();
                MLX90129.readEEPROM(nfcvTag, mNfcData);
            }
        });

        mNewMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivityNew.class);
                startActivity(intent);
                finish();
            }
        });

        mFragmentTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getApplicationContext(), ResultActivity.class);
                Bundle bundle = new Bundle();
                bundle.putParcelableArrayList("data", Utils.generateTempData(200, -200, 40, Utils.UNIT_SECOND));
                bundle.putString("nfc_id", currentNfcId);
                intent.putExtras(bundle);
                startActivity(intent);

            }
        });

        mLoadConfig.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<WriteCommand> writeCommands = new ArrayList<>();
/*
                writeCommands.add(new WriteCommand((byte) 0x09, (byte) 0x70, (byte) 0x90));  //EEPROM #09
                writeCommands.add(new WriteCommand((byte) 0x0A, (byte) 0x00, (byte) 0x00));  //EEPROM #0A
                writeCommands.add(new WriteCommand((byte) 0x0B, (byte) 0x29, (byte) 0x00));   //EEPROM #OB
                writeCommands.add(new WriteCommand((byte) 0x0C, (byte) 0xD7, (byte) 0x00));  //EEPROM #0C
                writeCommands.add(new WriteCommand((byte) 0x0F, (byte) 0xFA, (byte) 0x00));  //EEPROM #0F 250
                writeCommands.add(new WriteCommand((byte) 0x10, (byte) 0x0C, (byte) 0x00));  //EEPROM #10 ms
                writeCommands.add(new WriteCommand((byte) 0x15, (byte) 0x70, (byte) 0x00));  //EEPROM #15
                writeCommands.add(new WriteCommand((byte) 0x16, (byte) 0x00, (byte) 0x00));  //EEPROM #16
                writeCommands.add(new WriteCommand((byte) 0x17, (byte) 0x00, (byte) 0x00));  //EEPROM #17
                writeCommands.add(new WriteCommand((byte) 0x18, (byte) 0x00, (byte) 0x80));  //EEPROM #18
                writeCommands.add(new WriteCommand((byte) 0x19, (byte) 0x31, (byte) 0x02));  //EEPROM #19
                writeCommands.add(new WriteCommand((byte) 0x1A, (byte) 0x00, (byte) 0x80));  //EEPROM #1A
*/

/*
                writeCommands.add(new WriteCommand((byte) 0x09, (byte) 0x70, (byte) 0x70));  //EEPROM #09
                writeCommands.add(new WriteCommand((byte) 0x0A, (byte) 0x00, (byte) 0x00));  //EEPROM #0A
                writeCommands.add(new WriteCommand((byte) 0x0B, (byte) 0x00, (byte) 0x29));   //EEPROM #OB
                writeCommands.add(new WriteCommand((byte) 0x0C, (byte) 0x00, (byte) 0xD7));  //EEPROM #0C
                writeCommands.add(new WriteCommand((byte) 0x0F, (byte) 0xF4, (byte) 0x01));  //EEPROM #0F
                writeCommands.add(new WriteCommand((byte) 0x10, (byte) 0x04, (byte) 0x00));  //EEPROM #10  //500
                writeCommands.add(new WriteCommand((byte) 0x15, (byte) 0x70, (byte) 0x80));  //EEPROM #15  //ms
                writeCommands.add(new WriteCommand((byte) 0x16, (byte) 0x00, (byte) 0x00));  //EEPROM #16
                writeCommands.add(new WriteCommand((byte) 0x17, (byte) 0x00, (byte) 0x00));  //EEPROM #17
                writeCommands.add(new WriteCommand((byte) 0x18, (byte) 0x80, (byte) 0x00));  //EEPROM #18
                writeCommands.add(new WriteCommand((byte) 0x19, (byte) 0x02, (byte) 0x31));  //EEPROM #19
                writeCommands.add(new WriteCommand((byte) 0x1A, (byte) 0x80, (byte) 0x00));  //EEPROM #1A
*/

                writeCommands.add(new WriteCommand((byte) 0x09, (byte) 0x78, (byte) 0x10));  //EEPROM #09
                writeCommands.add(new WriteCommand((byte) 0x0A, (byte) 0x00, (byte) 0x00));  //EEPROM #0A
                writeCommands.add(new WriteCommand((byte) 0x0B, (byte) 0x29, (byte) 0x00));   //EEPROM #OB
                writeCommands.add(new WriteCommand((byte) 0x0C, (byte) 0xD7, (byte) 0x00));  //EEPROM #0C

                writeCommands.add(new WriteCommand((byte) 0x0F, (byte) 0xFA, (byte) 0x00));  //EEPROM #0F 250
                writeCommands.add(new WriteCommand((byte) 0x10, (byte) 0x0C, (byte) 0x00));  //EEPROM #10 ms

                writeCommands.add(new WriteCommand((byte) 0x15, (byte) 0x70, (byte) 0x00));  //EEPROM #15
                writeCommands.add(new WriteCommand((byte) 0x16, (byte) 0x00, (byte) 0x00));  //EEPROM #16
                writeCommands.add(new WriteCommand((byte) 0x17, (byte) 0x00, (byte) 0x00));  //EEPROM #17
                writeCommands.add(new WriteCommand((byte) 0x18, (byte) 0x00, (byte) 0x80));  //EEPROM #18
                writeCommands.add(new WriteCommand((byte) 0x19, (byte) 0x31, (byte) 0x02));  //EEPROM #19
                writeCommands.add(new WriteCommand((byte) 0x1A, (byte) 0x00, (byte) 0x80));  //EEPROM #1A

                MLX90129.tranceiveWrites(nfcvTag, (byte) 0x43, (byte) 0x21, writeCommands);
                MLX90129.readEEPROM(nfcvTag, mNfcData);


            }
        });


        mNfcCommand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mNfcCommand.getText().toString().trim().isEmpty()) {
                    mNfcCommand.getText().clear();
                }
            }
        });

        mNfcData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mNfcData.setText("");
            }
        });

        mNfcSendCommand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String inputCommand = mNfcCommand.getText().toString().toUpperCase();
                if (nfcvTag != null) {
                    if (nfcvTag.isConnected() && !inputCommand.isEmpty()) {
                        mNfcData.append(">> " + inputCommand + "\n");
                        try {
                            byte[] response = nfcvTag.transceive(Utils.hexStringToByteArray(inputCommand));
                            mNfcData.append("<< " + Utils.byteArrayToHexString(response) + "\n");
                            mNfcCommand.getText().clear();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
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

    private void readData() {
        if (nfcvTag != null) {
            if (nfcvTag.isConnected()) {
                byte[] responseRead = MLX90129.tranceiveReadInternal(nfcvTag, (byte) 0x29);
                Log.e("EEPROM", Utils.byteArrayToHexString(responseRead));
            }
        }
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
