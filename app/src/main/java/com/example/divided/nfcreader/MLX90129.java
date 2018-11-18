package com.example.divided.nfcreader;

import android.nfc.tech.NfcV;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;

import com.example.divided.nfcreader.model.WriteCommand;

import java.util.List;

public class MLX90129 {
    public static final String TAG = "NFC READER";
    public static final int byteA = 0;
    public static final int byteB = 1;

    public static byte[] tranceiveReadEEPROM(NfcV nfcVTag, byte flag, byte actionToTake, byte addr) {
        byte[] response = {(byte) 0xFF};
        try {
            byte[] WriteSingleBlockFrame = {
                    flag, actionToTake,
                    addr
            };
            response = nfcVTag.transceive(WriteSingleBlockFrame);
            if (!(response[0] == (byte) 0x00 || response[0] == (byte) 0x01)) //response 01 = error sent back by tag (new Android 4.2.2) or BC
            {
                Log.e("NFCCommand,Tranceive", "**ERROR** Read data:" + String.valueOf(response[0]));
            }
            return response;
        } catch (Exception e) {
            Log.e("NFCCommand,Tranceive", "Some Exception Error" + String.valueOf(response[0]));
            return response;
        }
    }

    static void tranceiveWrites(final NfcV nfcTag, final byte flag, final byte actionToTake, final List<WriteCommand> writeCommandList) {
        Handler handler = new Handler();
        for (int i = 1; i <= writeCommandList.size(); i++) {
            final int index = i - 1;
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    WriteCommand currentWriteCommand = writeCommandList.get(index);
                    tranceiveWriteEEPROM(nfcTag, currentWriteCommand.getAddr(), currentWriteCommand.getCommand());
                }
            }, 20 * i);  // 50 ms delay after each write request
        }

    }

    public static byte[] tranceiveWriteEEPROM(NfcV nfcVTag, byte address, byte[] command) {
        byte[] response = {(byte) 0xFF};
        if (nfcVTag != null) {
            if (nfcVTag.isConnected()) {
                try {
                    byte[] tagID = nfcVTag.getTag().getId();
                    //                                 flag,       action,
                    byte[] WriteSingleBlockFrame = {(byte) 0x43, (byte) 0x21, address, command[byteA], command[byteB]};
                    response = nfcVTag.transceive(WriteSingleBlockFrame);
                    if (response[0] == (byte) 0x00) {
                        Log.i(TAG, "Success: Write EEPROM");
                    } else {
                        Log.e(TAG, "Fail: Write EEPROM, error code: " + String.valueOf((response[0] & 0xFF)));
                    }
                    return response;
                } catch (Exception e) {
                    Log.e(TAG, "Fail: Write EEPROM Exception");
                    return response;
                }
            }
        }
        return response;
    }

    public static void readEEPROM(NfcV nfcvTag, TextView terminal) {
        if (nfcvTag != null) {
            if (nfcvTag.isConnected()) {
                byte[] address = {(byte) 0x09};
                terminal.setText("");
                for (byte i = 0x09; i < 0x21; i++) {
                    byte[] responseRead = MLX90129.tranceiveReadEEPROM(nfcvTag, (byte) 0x02, (byte) 0x20, i);
                    Log.e("#0x" + Utils.byteArrayToHexString(address) + ": " + "EEPROM ", Utils.byteArrayToHexString(responseRead));
                    terminal.append("#0x" + Utils.byteArrayToHexString(address) + ": " + "EEPROM "+ Utils.byteArrayToHexString(responseRead)+"\n");
                    address[0]++;
                }
            }
        }
    }


    public static byte[] tranceiveReadInternal(NfcV nfcVTag, byte addr) {
        byte[] response = {(byte)0xFF};
        try{
            byte[] ReadSingleBlockFrame = {(byte)0x03, (byte)0xA2, (byte) 0x1F, addr};
            response = nfcVTag.transceive(ReadSingleBlockFrame);
            if(response[0] == (byte)0x00) {
                Log.i(TAG, "Success: Read Internal Memory");
            } else {
                Log.i(TAG, "Fail: Read Internal Memory");
            }
            return response;
        } catch (Exception e) {
            Log.e(TAG, "NFC Tranceive Error");
            return response;
        }
    }
}
