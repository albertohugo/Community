package hugo.alberto.community.Fragments;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import java.util.Collections;
import java.util.List;

import hugo.alberto.community.Application.Application;
import hugo.alberto.community.R;

public class Settings_Fragment extends Fragment {
    private List<Float> availableOptions = Application.getConfigHelper().getSearchDistanceAvailableOptions();
    int zoom = 100;
    View rootView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.settings_layout, container, false);
        float currentSearchDistance = Application.getSearchDistance();
        if (!availableOptions.contains(currentSearchDistance)) {
            availableOptions.add(currentSearchDistance);
        }
        Collections.sort(availableOptions);

        RadioGroup searchDistanceRadioGroup = (RadioGroup) rootView.findViewById(R.id.searchdistance_radiogroup);

        for (int index = 0; index < availableOptions.size(); index++) {
            float searchDistance = availableOptions.get(index);

            RadioButton button = new RadioButton(getContext());
            button.setId(index);
            button.setText(getString(R.string.zoom_distance_format, (int) zoom));
            searchDistanceRadioGroup.addView(button, index);
            int textColor = Color.parseColor("#3D71AF");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                button.setButtonTintList(ColorStateList.valueOf(textColor));
            }
            if (currentSearchDistance == searchDistance) {
                searchDistanceRadioGroup.check(index);
            }
            zoom=zoom-25;
        }

        searchDistanceRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                Application.setSearchDistance(availableOptions.get(checkedId));
                Snackbar.make(rootView, "Setting updated.", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }

        });

        return rootView;
    }
}