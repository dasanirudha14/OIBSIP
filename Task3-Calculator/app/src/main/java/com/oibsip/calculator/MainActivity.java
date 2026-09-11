package com.oibsip.calculator;

import android.os.Bundle;
import android.graphics.Color;
import android.view.*;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

import java.util.*;

public class MainActivity extends AppCompatActivity {
    TextView d;
    StringBuilder expr = new StringBuilder();
    boolean justResult = false;

    public void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_main);
        d = findViewById(R.id.display);
        GridLayout g = findViewById(R.id.grid);
        String[] keys = {"C", "⌫", "÷", "×", "7", "8", "9", "-", "4", "5", "6", "+", "1", "2", "3", "=", "0", ".", "(", ")"};
        for (String k : keys) {
            Button btn = new Button(this);
            btn.setText(k);
            btn.setTextSize(20);
            GridLayout.LayoutParams lp = new GridLayout.LayoutParams();
            lp.width = 0;
            lp.height = 0;
            lp.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
            lp.rowSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
            btn.setLayoutParams(lp);
            btn.setOnClickListener(v -> tap(((Button) v).getText().toString()));
            g.addView(btn);
        }
    }

    void tap(String k) {
        if (k.equals("C")) {
            expr.setLength(0);
            justResult = false;
            d.setText("0");
            return;
        }
        if (k.equals("⌫")) {
            if (expr.length() > 0) expr.deleteCharAt(expr.length() - 1);
            d.setText(expr.length() == 0 ? "0" : expr.toString());
            return;
        }
        if (k.equals("=")) {
            try {
                double x = eval(expr.toString());
                if (Double.isInfinite(x) || Double.isNaN(x)) throw new ArithmeticException();
                String s = (x == Math.rint(x)) ? String.format(Locale.US, "%.0f", x) : String.format(Locale.US, "%.8f", x).replaceAll("0+$", "");
                expr.setLength(0);
                expr.append(s);
                d.setText(s);
                justResult = true;
            } catch (Exception e) {
                d.setText("Error");
                expr.setLength(0);
                justResult = true;
            }
            return;
        }
        if (justResult && !"+-×÷)".contains(k)) {
            expr.setLength(0);
            justResult = false;
        }
        if (k.equals("×")) expr.append("*");
        else if (k.equals("÷")) expr.append("/");
        else expr.append(k);
        d.setText(expr.toString());
    }

    double eval(String s) {
        if (s.isEmpty()) return 0;
        return new Parser(s).parse();
    }

    static class Parser {
        String s;
        int p = -1, ch;

        Parser(String s) {
            this.s = s;
        }

        void next() {
            ch = ++p < s.length() ? s.charAt(p) : -1;
        }

        boolean eat(int c) {
            while (ch == ' ') next();
            if (ch == c) {
                next();
                return true;
            }
            return false;
        }

        double parse() {
            next();
            double x = expr();
            if (p < s.length()) throw new RuntimeException();
            return x;
        }

        double expr() {
            double x = term();
            for (; ; ) {
                if (eat('+')) x += term();
                else if (eat('-')) x -= term();
                else return x;
            }
        }

        double term() {
            double x = factor();
            for (; ; ) {
                if (eat('*')) x *= factor();
                else if (eat('/')) {
                    double y = factor();
                    if (y == 0) throw new ArithmeticException();
                    x /= y;
                } else return x;
            }
        }

        double factor() {
            if (eat('+')) return factor();
            if (eat('-')) return -factor();
            double x;
            int start = p;
            if (eat('(')) {
                x = expr();
                if (!eat(')')) throw new RuntimeException();
            } else {
                while ((ch >= '0' && ch <= '9') || ch == '.') next();
                if (start == p) throw new RuntimeException();
                x = Double.parseDouble(s.substring(start, p));
            }
            return x;
        }
    }
}
