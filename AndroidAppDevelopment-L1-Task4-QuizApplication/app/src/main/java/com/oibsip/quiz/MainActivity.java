package com.oibsip.quiz;

import android.os.Bundle;
import android.graphics.Color;
import android.view.*;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

import java.util.*;

public class MainActivity extends AppCompatActivity {
    TextView q, counter, score;
    RadioGroup group;
    Button next, start;
    RadioButton[] ops;
    android.view.View quizPanel;
    List<Question> qs;
    int idx = 0, points = 0;
    boolean answered = false;

    static class Question {
        String q;
        String[] a;
        int correct;

        Question(String q, String[] a, int c) {
            this.q = q;
            this.a = a;
            correct = c;
        }
    }

    public void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_main);
        q = findViewById(R.id.question);
        counter = findViewById(R.id.counter);
        score = findViewById(R.id.score);
        group = findViewById(R.id.options);
        next = findViewById(R.id.next);
        start = findViewById(R.id.start);
        quizPanel = findViewById(R.id.quizPanel);
        ops = new RadioButton[]{findViewById(R.id.op1), findViewById(R.id.op2), findViewById(R.id.op3), findViewById(R.id.op4)};
        build();
        start.setOnClickListener(v -> {
            start.setVisibility(View.GONE);
            quizPanel.setVisibility(View.VISIBLE);
            Collections.shuffle(qs);
            show();
        });
        group.setOnCheckedChangeListener((g, id) -> {
            if (id == -1) return;
            if (!answered) {
                RadioButton r = findViewById(id);
                int pos = group.indexOfChild(r);
                answered = true;
                if (pos == qs.get(idx).correct) {
                    points++;
                    r.setTextColor(Color.rgb(0, 140, 0));
                } else {
                    r.setTextColor(Color.RED);
                    ops[qs.get(idx).correct].setTextColor(Color.rgb(0, 140, 0));
                }
                for (RadioButton o : ops) o.setEnabled(false);
            }
        });
        next.setOnClickListener(v -> {
            if (!answered) {
                Toast.makeText(this, "Please select an answer", Toast.LENGTH_SHORT).show();
                return;
            }
            idx++;
            if (idx >= qs.size()) finishQuiz();
            else show();
        });
    }

    void build() {
        qs = new ArrayList<>();
        qs.add(new Question("What is the capital of India?", new String[]{"Mumbai", "New Delhi", "Kolkata", "Chennai"}, 1));
        qs.add(new Question("Which planet is known as the Red Planet?", new String[]{"Earth", "Mars", "Jupiter", "Venus"}, 1));
        qs.add(new Question("Who wrote Hamlet?", new String[]{"William Shakespeare", "Charles Dickens", "Leo Tolstoy", "Mark Twain"}, 0));
        qs.add(new Question("How many continents are there?", new String[]{"5", "6", "7", "8"}, 2));
        qs.add(new Question("Which gas do plants absorb?", new String[]{"Oxygen", "Nitrogen", "Carbon Dioxide", "Hydrogen"}, 2));
        qs.add(new Question("What is the largest ocean?", new String[]{"Atlantic", "Indian", "Arctic", "Pacific"}, 3));
        qs.add(new Question("Which is the fastest land animal?", new String[]{"Lion", "Cheetah", "Horse", "Tiger"}, 1));
        qs.add(new Question("What is H2O commonly called?", new String[]{"Salt", "Water", "Hydrogen", "Oxygen"}, 1));
        qs.add(new Question("Which language is primarily used for Android apps in this assignment?", new String[]{"Java", "SQL", "HTML", "CSS"}, 0));
        qs.add(new Question("What is 12 × 8?", new String[]{"86", "96", "108", "112"}, 1));
    }

    void show() {
        Question x = qs.get(idx);
        counter.setText("Question " + (idx + 1) + " of " + qs.size());
        q.setText(x.q);
        group.clearCheck();
        answered = false;
        for (int i = 0; i < 4; i++) {
            ops[i].setText(x.a[i]);
            ops[i].setTextColor(Color.DKGRAY);
            ops[i].setEnabled(true);
        }
    }

    void finishQuiz() {
        counter.setText("Quiz Completed");
        q.setText("Well done!");
        group.setVisibility(View.GONE);
        next.setText("RESTART QUIZ");
        next.setVisibility(View.VISIBLE);
        score.setText("Score: " + points + " / " + qs.size() + "\nCorrect: " + points + "\nIncorrect: " + (qs.size() - points));
        next.setOnClickListener(v -> {
            idx = 0;
            points = 0;
            group.setVisibility(View.VISIBLE);
            score.setText("");
            next.setText("NEXT");
            Collections.shuffle(qs);
            show();
        });
    }
}
