package com.example.divided.nfcreader;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.divided.nfcreader.model.TempMeasurement;

public class SingleTempFragment extends android.app.Fragment implements ResultActivity.ListenerFromResultActivity {
    TempMeasurement tempMeasurement;
    TextView temp;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.temp_single_measurement_layout, container, false);

        tempMeasurement = getArguments().getParcelable("fragment_data");

        ((ResultActivity) getActivity()).setResultActivityListener(this);
        temp = view.findViewById(R.id.text_view_temp_value);
        temp.setText(String.format("%.1f", tempMeasurement.getTemperatureCelsius()) + Utils.tempUnitToString(Utils.UNIT_CELSIUS));
        return view;
    }

    @Override
    public void refreshFragmentUI(int unit) {
        if (unit == Utils.UNIT_KELVIN) {
            temp.setText(String.format("%.1f", tempMeasurement.getTemperatureKelvin()) + Utils.tempUnitToString(Utils.UNIT_KELVIN));
        } else if (unit == Utils.UNIT_FAHRENHEIT) {
            temp.setText(String.format("%.1f", tempMeasurement.getTemperatureCelsius()) + Utils.tempUnitToString(Utils.UNIT_FAHRENHEIT));
        } else {
            temp.setText(String.format("%.1f", tempMeasurement.getTemperatureCelsius()) + Utils.tempUnitToString(Utils.UNIT_CELSIUS));
        }
    }

}
