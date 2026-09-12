package com.oibsip.unitconverter;

import android.os.Bundle;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

import java.util.*;

public class MainActivity extends AppCompatActivity {
    EditText value;
    Spinner category, from, to;
    TextView result;
    final String[] cats = {"Length", "Weight", "Volume", "Temperature"};
    final Map<String, String[]> units = new HashMap<>();

    @Override
    public void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_main);
        value = findViewById(R.id.valueInput);
        category = findViewById(R.id.categorySpinner);
        from = findViewById(R.id.fromSpinner);
        to = findViewById(R.id.toSpinner);
        result = findViewById(R.id.resultText);
        units.put("Length", new String[]{"Centimetres", "Metres", "Kilometres", "Inches", "Feet", "Miles"});
        units.put("Weight", new String[]{"Grams", "Kilograms", "Pounds", "Ounces"});
        units.put("Volume", new String[]{"Millilitres", "Litres", "Gallons", "Cups"});
        units.put("Temperature", new String[]{"Celsius", "Fahrenheit", "Kelvin"});
        category.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, cats));
        category.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onNothingSelected(AdapterView<?> p) {
            }

            public void onItemSelected(AdapterView<?> p, android.view.View v, int pos, long id) {
                setUnits(cats[pos]);
            }
        });
        findViewById(R.id.convertButton).setOnClickListener(v -> convert());
    }

    void setUnits(String c) {
        String[] a = units.get(c);
        ArrayAdapter<String> ad = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, a);
        from.setAdapter(ad);
        to.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, a));
        if (a.length > 1) to.setSelection(1);
    }

    void convert() {
        String s = value.getText().toString().trim();
        if (s.isEmpty()) {
            value.setError("Enter a value");
            return;
        }
        double x;
        try {
            x = Double.parseDouble(s);
        } catch (Exception e) {
            value.setError("Enter a valid number");
            return;
        }
        String c = cats[category.getSelectedItemPosition()];
        String f = from.getSelectedItem().toString(), t = to.getSelectedItem().toString();
        double y = convertValue(x, c, f, t);
        result.setText(String.format(Locale.getDefault(), "%.4f %s", y, t));
    }

    double convertValue(double x, String c, String f, String t) {
        if (f.equals(t)) return x;
        if (c.equals("Length")) {
            double m = toBase(x, f, new double[]{.01, 1, 1000, .0254, .3048, 1609.344});
            return m / fromFactor(t, new double[]{.01, 1, 1000, .0254, .3048, 1609.344});
        }
        if (c.equals("Weight")) {
            double g = toBase(x, f, new double[]{1, 1000, 453.59237, 28.349523125});
            return g / fromFactor(t, new double[]{1, 1000, 453.59237, 28.349523125});
        }
        if (c.equals("Volume")) {
            double l = toBase(x, f, new double[]{.001, 1, 3.785411784, .2365882365});
            return l / fromFactor(t, new double[]{.001, 1, 3.785411784, .2365882365});
        }
        double celsius;
        if (f.equals("Celsius")) celsius = x;
        else if (f.equals("Fahrenheit")) celsius = (x - 32) * 5 / 9;
        else celsius = x - 273.15;
        if (t.equals("Celsius")) return celsius;
        if (t.equals("Fahrenheit")) return celsius * 9 / 5 + 32;
        return celsius + 273.15;
    }

    double toBase(double x, String u, double[] f) {
        String[] a = units.get(cats[category.getSelectedItemPosition()]);
        return x * f[Arrays.asList(a).indexOf(u)];
    }

    double fromFactor(String u, double[] f) {
        String[] a = units.get(cats[category.getSelectedItemPosition()]);
        return f[Arrays.asList(a).indexOf(u)];
    }
}
