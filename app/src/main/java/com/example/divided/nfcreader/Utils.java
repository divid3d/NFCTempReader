package com.example.divided.nfcreader;

import com.example.divided.nfcreader.model.Temp;
import com.example.divided.nfcreader.model.TempMeasurement;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Utils {
    public final static int UNIT_CELSIUS = 0;
    public final static int UNIT_KELVIN = 1;
    public final static int UNIT_FAHRENHEIT = 2;
    public final static int UNIT_SECOND = 3;
    public final static int UNIT_MINUTE = 4;

    private final static char[] hexArray = "0123456789ABCDEF".toCharArray();

    public static String byteArrayToHexString(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] bytes = new byte[len / 2];

        for (int i = 0; i < len; i += 2) {
            bytes[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character.digit(s.charAt(i + 1), 16));
        }

        return bytes;
    }

    public static int hexStringToInteger(String hex) {
        return Integer.parseInt(hex, 16);
    }

    public static float celsiusToKelvin(float celsius) {
        return celsius + 273.15f;
    }

    public static float celsiusToFahrenheit(float celsius) {
        return 32 + (celsius * 9 / 5);
    }

    public static String[] splitToNChar(String text, int size) {
        List<String> parts = new ArrayList<>();
        int length = text.length();
        for (int i = 0; i < length; i += size) {
            parts.add(text.substring(i, Math.min(length, i + size)));
        }
        return parts.toArray(new String[0]);
    }

    public static ArrayList<TempMeasurement> generateTempData(float max, float min, int count, int timeUnit) {
        ArrayList<TempMeasurement> tempMeasurements = new ArrayList<>();
        Random r = new Random();
        r.setSeed(System.currentTimeMillis());

        for (int i = 0; i < count; i++) {
            tempMeasurements.add(new TempMeasurement(r.nextFloat() * (max - min) + min, i * 5f, timeUnit));
        }

        return tempMeasurements;
    }

    public static Temp getMinTemp(ArrayList<TempMeasurement> measurments) {
        float minTemp = measurments.get(0).getTemperatureCelsius();

        for (int i = 1; i < measurments.size(); i++) {
            if (measurments.get(i).getTemperatureCelsius() < minTemp) {
                minTemp = measurments.get(i).getTemperatureCelsius();
            }
        }
        return new Temp(minTemp);
    }

    public static Temp getMaxTemp(ArrayList<TempMeasurement> measurments) {
        float maxTemp = measurments.get(0).getTemperatureCelsius();

        for (int i = 1; i < measurments.size(); i++) {
            if (measurments.get(i).getTemperatureCelsius() > maxTemp) {
                maxTemp = measurments.get(i).getTemperatureCelsius();
            }
        }
        return new Temp(maxTemp);
    }

    public static Temp getAverageTemp(ArrayList<TempMeasurement> measurments) {
        float averageCelsiusTemp = 0;

        for (TempMeasurement measurment : measurments) {
            averageCelsiusTemp += measurment.getTemperatureCelsius();
        }

        averageCelsiusTemp = averageCelsiusTemp / measurments.size();
        return new Temp(averageCelsiusTemp);
    }

    public static String prepareStringData(List<TempMeasurement> measurements, String header) {
        StringBuilder sb = new StringBuilder();
        sb.append(header).append("\n\n");

        for (int i = 0; i < measurements.size(); i++) {
            TempMeasurement currentMeasurement = measurements.get(i);
            sb.append(String.valueOf(i + 1)).append(".\t")
                    .append(currentMeasurement.getTimeStamp()).append(timeUnitToString(currentMeasurement.getTimeUnit())).append("\t")
                    .append(String.format("%.1f", currentMeasurement.getTemperatureCelsius())).append(" °C").append("\t")
                    .append(String.format("%.1f", currentMeasurement.getTemperatureKelvin())).append(" K").append("\t")
                    .append(String.format("%.1f", currentMeasurement.getTemperatureFahrenheit())).append(" F")
                    .append("\r\n");
        }
        return sb.toString();
    }

    public static String timeUnitToString(int timeUnit) {
        if (timeUnit == UNIT_SECOND) {
            return "s";
        } else if (timeUnit == UNIT_MINUTE) {
            return "m";
        } else {
            return "";
        }
    }

    public static String tempUnitToString(int tempUnit) {
        if (tempUnit == UNIT_CELSIUS) {
            return "°C";
        } else if (tempUnit == UNIT_KELVIN) {
            return "K";
        } else {
            return "F";
        }
    }

    public static int bytesWordToIntAlt(byte[] b) {
        return ((b[1] & 0xFF) << 8) | (b[0] & 0xFF);
    }
}
