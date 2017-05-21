package com.example.hyena.lab3;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.CountDownTimer;
import android.os.Vibrator;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    Button startBtn, stopBtn, resumeBtn, resetBtn;
    MyCountDownTimer myCountDownTimer;
    TextView text;
    int workCounter = 0;
    boolean workFlag, workPending = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageView imageView = (ImageView) findViewById(R.id.imageView5);
        imageView.setImageResource(R.drawable.pomodoro2);
        startBtn = (Button) findViewById(R.id.startBtn);
        stopBtn = (Button) findViewById(R.id.stopBtn);
        resumeBtn = (Button) findViewById(R.id.resumeBtn);
        resetBtn = (Button) findViewById(R.id.resetBtn);

        text = (TextView) findViewById(R.id.text);

        stopBtn.setVisibility(View.INVISIBLE);
        resumeBtn.setVisibility(View.INVISIBLE);
        resetBtn.setVisibility(View.INVISIBLE);

        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startBtn.setVisibility(View.INVISIBLE);
                stopBtn.setVisibility(View.VISIBLE);
                resetBtn.setVisibility(View.VISIBLE);

                if(workPending){
                    startWork(20, false);
                }
                else{
                    startBreak(10, false);
                }

            }
        });

        stopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resumeBtn.setVisibility(View.VISIBLE);
                resetBtn.setVisibility(View.VISIBLE);
                stopBtn.setVisibility(View.INVISIBLE);

                myCountDownTimer.cancel();

            }
        });

        resumeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopBtn.setVisibility(View.VISIBLE);
                resetBtn.setVisibility(View.VISIBLE);
                resumeBtn.setVisibility(View.INVISIBLE);

                String[] timeStr = text.getText().toString().split(":");

                int seconds = Integer.valueOf(timeStr[1]);
                int minutes = Integer.valueOf(timeStr[0]);

                int time = seconds + minutes * 60;

                if (workFlag == true) {
                    startWork(time, true);
                } else {
                    startBreak(time, true);
                }
            }
        });

        resetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startBtn.setVisibility(View.VISIBLE);
                stopBtn.setVisibility(View.INVISIBLE);
                resumeBtn.setVisibility(View.INVISIBLE);
                resetBtn.setVisibility(View.INVISIBLE);

                text.setText("00:20");
                workPending = true;
                workCounter = 0;

                myCountDownTimer.cancel();
            }
        });

    }


    public void startWork(int time, boolean restart) {
        workFlag = true;
        workCounter++;

        text.setTextColor(Color.parseColor("#ffffff"));

        myCountDownTimer = new MyCountDownTimer(time * 1000, 1000);
        myCountDownTimer.start();
    }

    public void startBreak(int time, boolean restart) {
        workFlag = false;

        text.setTextColor(Color.parseColor("#39b54a"));

        myCountDownTimer = new MyCountDownTimer(time * 1000, 1000);
        myCountDownTimer.start();

    }


    public class MyCountDownTimer extends CountDownTimer {

        public MyCountDownTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {

            int seconds = (int) ((millisUntilFinished / 1000) % 60);
            int minutes = (int) ((millisUntilFinished / (1000 * 60)) % 60);

            text.setText(String.format("%02d:%02d", minutes, seconds));

        }

        @Override
        public void onFinish() {

            Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            vibrator.vibrate(1000);

            text.setText("00:00");

            if (workFlag == true) {
                AlertDialog.Builder builder1 = new AlertDialog.Builder(MainActivity.this);
                if (workCounter < 4) {
                    builder1.setMessage("Take break now?");
                } else if (workCounter == 4) {
                    builder1.setMessage("Take long break now?");
                }

                builder1.setCancelable(true);

                builder1.setPositiveButton(
                        "Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                if (workCounter < 4) {
                                    startBreak(10, false);
                                } else if (workCounter == 4) {
                                    workCounter = 0;
                                    startBreak(15, false);
                                }
                                dialog.cancel();
                            }
                        });

                builder1.setNegativeButton(
                        "No",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                if (workCounter < 4) {
                                    text.setText("00:10");
                                } else if (workCounter == 4) {
                                    text.setText("00:15");
                                }

                                text.setTextColor(Color.parseColor("#39b54a"));

                                stopBtn.setVisibility(View.INVISIBLE);
                                resetBtn.setVisibility(View.INVISIBLE);
                                startBtn.setVisibility(View.VISIBLE);
                                workPending = false;

                                dialog.cancel();
                            }
                        });

                AlertDialog alert = builder1.create();
                alert.show();
            } else if (workFlag == false) {
                AlertDialog.Builder builder1 = new AlertDialog.Builder(MainActivity.this);
                builder1.setMessage("Start work now?");
                builder1.setCancelable(true);

                builder1.setPositiveButton(
                        "Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                startWork(20, false);
                                dialog.cancel();
                            }
                        });

                builder1.setNegativeButton(
                        "No",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                startBtn.setVisibility(View.VISIBLE);
                                stopBtn.setVisibility(View.INVISIBLE);
                                resetBtn.setVisibility(View.INVISIBLE);

                                text.setTextColor(Color.parseColor("#ffffff"));

                                text.setText("00:20");

                                workPending = true;

                                dialog.cancel();
                            }
                        });

                AlertDialog alert = builder1.create();
                alert.show();
            }
        }
    }
}


