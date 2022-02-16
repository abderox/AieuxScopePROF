package com.example.testchat.Views;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.testchat.Adapters.PrefConfig;
import com.example.testchat.R;

public class prise extends AppCompatActivity {

        Button btnwin;
        Button closewin;
        Dialog dialog;
        Button verre1;
        Button verre2;
        Button verre3;
        Button verre4;
        Button verre5;
        Button verre6;
        Button reset;
        TextView textdone1;
        TextView textdone2;
        TextView textdone3;
        TextView textdone4;
        TextView textdone5;
        TextView textdone6;
        Button res1;
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_prise);
            final SharedPreferences prefs = getSharedPreferences("sharedPreferences", Context.MODE_PRIVATE);

            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            textdone1 = findViewById(R.id.done1);
            textdone2 = findViewById(R.id.done2);
            textdone3 = findViewById(R.id.done3);
            textdone4 = findViewById(R.id.done4);
            textdone5 = findViewById(R.id.done5);
            textdone6 = findViewById(R.id.done6);
            verre1 = findViewById(R.id.verre1);
            verre2 = findViewById(R.id.verre2);
            verre3 = findViewById(R.id.verre3);
            verre4 = findViewById(R.id.verre4);
            verre5 = findViewById(R.id.verre5);
            verre6 = findViewById(R.id.verre6);
            res1 = findViewById(R.id.verre1i);


            Button closewin = findViewById(R.id.buttonclose);
            dialog = new Dialog(this);
            textdone1.setVisibility(prefs.getBoolean("isTextdone1Visible", false) ? View.VISIBLE : View.INVISIBLE);
            textdone2.setVisibility(prefs.getBoolean("isTextdone2Visible", false) ? View.VISIBLE : View.INVISIBLE);
            textdone3.setVisibility(prefs.getBoolean("isTextdone3Visible", false) ? View.VISIBLE : View.INVISIBLE);
            textdone4.setVisibility(prefs.getBoolean("isTextdone4Visible", false) ? View.VISIBLE : View.INVISIBLE);
            textdone5.setVisibility(prefs.getBoolean("isTextdone5Visible", false) ? View.VISIBLE : View.INVISIBLE);
            textdone6.setVisibility(prefs.getBoolean("isTextdone6Visible", false) ? View.VISIBLE : View.INVISIBLE);




            verre1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    prefs.edit().putBoolean("isTextdone1Visible", true).apply();
                    textdone1.setVisibility(View.VISIBLE);
                    textdone1.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            prefs.edit().clear();
                            prefs.edit().commit();

                        }
                    });

                }
            });


            verre2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    prefs.edit().putBoolean("isTextdone2Visible", true).apply();
                    textdone2.setVisibility(View.VISIBLE);



                }
            });
            verre3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    prefs.edit().putBoolean("isTextdone3Visible", true).apply();
                    textdone3.setVisibility(View.VISIBLE);
                    textdone3.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            prefs.edit().clear();
                            prefs.edit().commit();

                        }
                    });


                }
            });
            verre4.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    prefs.edit().putBoolean("isTextdone4Visible", true).apply();
                    textdone4.setVisibility(View.VISIBLE);
                    textdone4.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            prefs.edit().clear();
                            prefs.edit().commit();

                        }
                    });


                }
            });
            verre5.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    prefs.edit().putBoolean("isTextdone6Visible", true).apply();
                    textdone6.setVisibility(View.VISIBLE);
                    textdone6.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            prefs.edit().clear();
                            prefs.edit().commit();

                        }
                    });
                }
            });
            verre6.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    prefs.edit().putBoolean("isTextdone5Visible", true).apply();
                    textdone5.setVisibility(View.VISIBLE);
                    openWinDialog();
                    }
            });
res1.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        delete(getApplicationContext());
    }
});

        }


    private void delete(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear();
        editor.commit();
        context = null;
    }


    private void openWinDialog() {
            dialog.setContentView(R.layout.congratlayout);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            ImageView imageViewClose=dialog.findViewById(R.id.imageViewClose);
            Button closewin=dialog.findViewById(R.id.buttonclose);
            closewin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            dialog.show();
        }
    public void resetData(View view){
        PrefConfig.removeDate(this);
    }

}

