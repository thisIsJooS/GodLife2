package com.example.godlife7;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;



public class StatisticsActivity extends AppCompatActivity {
    private FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
    private DatabaseReference mdatabaseRef;
    FrameLayout FrameLayout01_study,FrameLayout01_exercise,FrameLayout01_book,FrameLayout01_sleeping;
    Button btn_Lookup_study,btn_Lookup_exercise,btn_Lookup_book,btn_Lookup_sleeping;
    ImageButton cal_before_study,cal_after_study,cal_before_exercise,cal_after_exercise,cal_before_book,cal_after_book,cal_before_sleeping,cal_after_sleeping;
    Button btn_today_study,btn_week_study,btn_month_study,btn_today_exercise,btn_week_exercise,btn_month_exercise,
    btn_today_book,btn_week_book,btn_month_book,btn_today_sleeping,btn_week_sleeping,btn_month_sleeping;
    ImageButton btn_to_home, btn_go_back;
    long accumulate_study=0,accumulate_exercise=0,accumulate_book=0,accumulate_sleep=0;
    Calendar now = Calendar.getInstance();
    Calendar c_before_study= Calendar.getInstance();
    Calendar c_after_study= Calendar.getInstance();
    Calendar c_before_exercise= Calendar.getInstance();
    Calendar c_after_exercise= Calendar.getInstance();
    Calendar c_before_book= Calendar.getInstance();
    Calendar c_after_book= Calendar.getInstance();
    Calendar c_before_sleep= Calendar.getInstance();
    Calendar c_after_sleep= Calendar.getInstance();
    String formatted_study, temp_study, formatted2_study,formatted_exercise, temp_exercise, formatted2_exercise,
    formatted_book, temp_book, formatted2_book,formatted_sleep, temp_sleep, formatted2_sleep;
    String userName;

    //minDate??? ???????????? ?????? ????????? ????????????, maxDate??? ???????????? ?????? ????????? ??????????????? ???????????? ?????? Calendar ??????
    Calendar minDate_study = Calendar.getInstance();
    Calendar maxDate_study = Calendar.getInstance();
    Calendar minDate_exercise = Calendar.getInstance();
    Calendar maxDate_exercise = Calendar.getInstance();
    Calendar minDate_book = Calendar.getInstance();
    Calendar maxDate_book = Calendar.getInstance();
    Calendar minDate_sleep = Calendar.getInstance();
    Calendar maxDate_sleep = Calendar.getInstance();

    ProgressDialog customProgressDialog; // ???????????? animation??? ???????????? ?????? ProgressDialog ?????? ??????

    int year1=now.get(Calendar.YEAR),year2=now.get(Calendar.YEAR);
    int month1=now.get(Calendar.MONTH)+1,month2=now.get(Calendar.MONTH)+1;
    int day_one=now.get(Calendar.DAY_OF_MONTH),day_two=now.get(Calendar.DAY_OF_MONTH);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //FireBase ????????? ??????
        FirebaseUser firebaseUser = mFirebaseAuth.getCurrentUser();
        mdatabaseRef = FirebaseDatabase.getInstance().getReference( "godlife" );
        mdatabaseRef.child("UserAccount").child(firebaseUser.getUid()).child("name").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userName = dataSnapshot.getValue(String.class);

            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Log.e("MainActivity", String.valueOf(databaseError.toException())); // ????????? ??????
            }
        });

        setContentView(R.layout.activity_statistics); // activity_statistics ???????????? ??????
        TextView day_1_study,day_2_study,day_1_exercise,day_2_exercise,day_1_book,day_2_book,day_1_sleeping,day_2_sleeping;

        // ?????? ????????? ?????? ???????????? ?????? ??????
        TextView result_study = findViewById(R.id.text_result_study);
        TextView result_exercise = findViewById(R.id.text_result_exercise);
        TextView result_book = findViewById(R.id.text_result_book);
        TextView result_sleep = findViewById(R.id.text_result_sleep);

        //????????? ????????? ????????? ??????. ?????? ???????????? ?????????
        btn_to_home = findViewById(R.id.btn_home);
        btn_to_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(StatisticsActivity.this,LoginActivity.class);
                startActivity(intent);
            }
        });

        // ????????? ?????? ????????? ????????? ??????. ?????? ???????????? ?????????
        btn_go_back = findViewById(R.id.btn_back);
        btn_go_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(StatisticsActivity.this,RankingActivity.class);
                startActivity(intent);
            }
        });

        // tab1 indicator??? ????????? ???. ????????? ??????????????? ???????????? ???????????? ??????????????? ??????
        LinearLayout linear_study = new LinearLayout(this);
        linear_study.setOrientation(LinearLayout.VERTICAL); //???????????????????????? orientation ??????
        linear_study.setGravity(Gravity.CENTER); //????????? ??????????????? gravity ??????
        linear_study.setBackgroundColor(Color.TRANSPARENT); // ????????? ??????????????? ?????? ??? ??????
        TextView ivv_study = new TextView(this); //??????????????? ?????????????????? ??????
        ivv_study.setText("??????"); // ???????????? ????????? ??????
        ivv_study.setBackgroundColor(Color.TRANSPARENT); //???????????? ??????????????? ??? ??????
        ivv_study.setTextColor(Color.parseColor("#000000")); //???????????? ????????? ??? ??????
        ivv_study.setTextSize(12); //???????????? ???????????? ??????
        ivv_study.setPadding(0,0,0,0); // ????????? ??? ?????? ??????
        ivv_study.setGravity(Gravity.BOTTOM); //????????? ??? gravity ??????
        ImageView iv_study = new ImageView(this); //??????????????? ?????????????????? ??????
        iv_study.setImageResource(R.drawable.study); // ???????????? ????????? ????????? ??????
        iv_study.setBackgroundColor(Color.TRANSPARENT); //???????????? ??????????????? ??? ??????
        LinearLayout.LayoutParams param_study = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        param_study.setMargins(0,20,0,5); //????????? ??????(??????, ???, ?????????, ??????) ??????
        linear_study.addView(iv_study, param_study); //????????? ???????????? ????????? ?????? ?????? ???????????? ??????
        linear_study.addView(ivv_study,param_study); //????????? ???????????? ????????? ?????? ?????? ????????? ??? ??????
        linear_study.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.linear_background_ripple, null));//?????? ?????? ripple(????????????) ??????

        // tab2 indicator??? ????????? ???. ????????? ??????????????? ???????????? ???????????? ??????????????? ??????
        LinearLayout linear_exercise = new LinearLayout(this);
        linear_exercise.setOrientation(LinearLayout.VERTICAL);
        linear_exercise.setGravity(Gravity.CENTER);
        linear_exercise.setBackgroundColor(Color.TRANSPARENT);
        TextView ivv_exercise = new TextView(this); //??????????????? ?????????????????? ??????
        ivv_exercise.setText("??????");
        ivv_exercise.setBackgroundColor(Color.TRANSPARENT);
        ivv_exercise.setTextColor(Color.parseColor("#000000"));
        ivv_exercise.setTextSize(12);
        ivv_exercise.setPadding(0,0,0,0);
        ImageView iv_exercise = new ImageView(this);
        iv_exercise.setImageResource(R.drawable.exercise); // ????????? ?????????
        iv_exercise.setBackgroundColor(Color.TRANSPARENT);
        LinearLayout.LayoutParams param_exercise = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        param_exercise.setMargins(0,20,0,5); //????????? ??????(??????, ???, ?????????, ??????)
        linear_exercise.addView(iv_exercise, param_exercise);
        linear_exercise.addView(ivv_exercise,param_exercise);
        linear_exercise.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.linear_background_ripple, null));

        // tab3 indicator??? ????????? ???. ????????? ??????????????? ???????????? ???????????? ??????????????? ??????
        LinearLayout linear_book = new LinearLayout(this);
        linear_book.setOrientation(LinearLayout.VERTICAL);
        linear_book.setGravity(Gravity.CENTER);
        linear_book.setBackgroundColor(Color.TRANSPARENT);
        TextView ivv_book = new TextView(this); // ??????????????? ?????????????????? ??????
        ivv_book.setText("??????");
        ivv_book.setBackgroundColor(Color.TRANSPARENT);
        ivv_book.setTextColor(Color.parseColor("#000000"));
        ivv_book.setTextSize(12);
        ImageView iv_book = new ImageView(this);
        iv_book.setImageResource(R.drawable.book); // ????????? ?????????
        iv_book.setBackgroundColor(Color.TRANSPARENT);
        LinearLayout.LayoutParams param_book = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        param_book.setMargins(0,20,0,5); //????????? ??????(??????, ???, ?????????, ??????)
        linear_book.addView(iv_book, param_book);
        linear_book.addView(ivv_book,param_book);
        linear_book.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.linear_background_ripple, null));

        // tab4 indicator??? ????????? ???. ????????? ??????????????? ?????????????????? ???????????? ??????????????? ??????
        LinearLayout linear_sleep = new LinearLayout(this);
        linear_sleep.setOrientation(LinearLayout.VERTICAL);
        linear_sleep.setGravity(Gravity.CENTER);
        linear_sleep.setBackgroundColor(Color.TRANSPARENT);
        TextView ivv_sleep = new TextView(this); //??????????????? ?????????????????? ??????
        ivv_sleep.setText("??????");
        ivv_sleep.setBackgroundColor(Color.TRANSPARENT);
        ivv_sleep.setTextColor(Color.parseColor("#000000"));
        ivv_sleep.setTextSize(12);
        ivv_sleep.setPadding(0,0,0,5);
        ImageView iv_sleep = new ImageView(this);
        iv_sleep.setImageResource(R.drawable.sleep); // ????????? ?????????
        iv_sleep.setBackgroundColor(Color.TRANSPARENT);
        LinearLayout.LayoutParams param_sleep = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        param_sleep.setMargins(0,20,0,0); //????????? ??????(??????, ???, ?????????, ??????)
        linear_sleep.addView(iv_sleep, param_sleep);
        linear_sleep.addView(ivv_sleep,param_sleep);
        linear_sleep.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.linear_background_ripple, null));

        //TabHost ??????
        TabHost tabHost = findViewById(R.id.host);
        tabHost.setup();
        TabHost.TabSpec spec = tabHost.newTabSpec("tab1");
        spec.setIndicator(linear_study);    // Indicator??? ?????? ??????
        spec.setContent(R.id.tab_content1);
        tabHost.addTab(spec);
        spec = tabHost.newTabSpec("tab2");
        spec.setIndicator(linear_exercise); // Indicator??? ?????? ??????
        spec.setContent(R.id.tab_content2);
        tabHost.addTab(spec);
        spec = tabHost.newTabSpec("tab3");
        spec.setIndicator(linear_book);     // Indicator??? ?????? ??????
        spec.setContent(R.id.tab_content3);
        tabHost.addTab(spec);
        spec = tabHost.newTabSpec("tab4");
        spec.setContent(R.id.tab_content4);
        spec.setIndicator(linear_sleep);    // Indicator??? ?????? ??????
        tabHost.addTab(spec);
        tabHost.setCurrentTab(0);           // ?????? ??? ??????

        //?????? ???????????? TextView ?????? ??????
        day_1_study = (TextView)findViewById(R.id.day1_study);
        day_1_study.setText(year1+ "-"+month1+"-"+day_one);
        day_2_study = (TextView)findViewById(R.id.day2_study);
        day_2_study.setText(year2+ "-"+month2+"-"+day_two);
        day_1_exercise = (TextView)findViewById(R.id.day1_exercise);
        day_1_exercise.setText(year1+ "-"+month1+"-"+day_one);
        day_2_exercise = (TextView)findViewById(R.id.day2_exercise);
        day_2_exercise.setText(year2+ "-"+month2+"-"+day_two);
        day_1_book = (TextView)findViewById(R.id.day1_book);
        day_1_book.setText(year1+ "-"+month1+"-"+day_one);
        day_2_book = (TextView)findViewById(R.id.day2_book);
        day_2_book.setText(year2+ "-"+month2+"-"+day_two);
        day_1_sleeping = (TextView)findViewById(R.id.day1_sleeping);
        day_1_sleeping.setText(year1+ "-"+month1+"-"+day_one);
        day_2_sleeping = (TextView)findViewById(R.id.day2_sleeping);
        day_2_sleeping.setText(year2+ "-"+month2+"-"+day_two);

        //??? ????????? ???????????? ?????? ??? ????????? ???????????? ???????????? ?????? ????????? ??????
        tabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String s) {

                //SimpleDateFormat??? ???????????? yyyy-MM-dd ????????? ????????????
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat();
                simpleDateFormat.applyPattern("yyyy-MM-dd");
                Calendar c = Calendar.getInstance();
                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH);
                int dayOfMonth = c.get(Calendar.DAY_OF_MONTH);

                //'??????' ?????? ?????? ???????????? ??????
                maxDate_study.set(year,month,dayOfMonth);
                minDate_study.set(year,month,dayOfMonth);
                c_after_study.set(year,month,dayOfMonth);
                c_before_study.set(year,month,dayOfMonth);
                String simple_study1 = simpleDateFormat.format(c_before_study.getTime());
                String simple_study2 = simpleDateFormat.format(c_after_study.getTime());
                day_1_study.setText(simple_study1);
                day_2_study.setText(simple_study2);

                //'??????' ?????? ?????? ???????????? ??????
                maxDate_exercise.set(year,month,dayOfMonth);
                minDate_exercise.set(year,month,dayOfMonth);
                c_after_exercise.set(year,month,dayOfMonth);
                c_before_exercise.set(year,month,dayOfMonth);
                String simple_exercise1 = simpleDateFormat.format(c_before_exercise.getTime());
                String simple_exercise2 = simpleDateFormat.format(c_after_exercise.getTime());
                day_1_exercise.setText(simple_exercise1);
                day_2_exercise.setText(simple_exercise2);

                //'??????' ?????? ?????? ???????????? ??????
                maxDate_book.set(year,month,dayOfMonth);
                minDate_book.set(year,month,dayOfMonth);
                c_after_book.set(year,month,dayOfMonth);
                c_before_book.set(year,month,dayOfMonth);
                String simple_book1 = simpleDateFormat.format(c_before_book.getTime());
                String simple_book2 = simpleDateFormat.format(c_after_book.getTime());
                day_1_book.setText(simple_book1);
                day_2_book.setText(simple_book2);

                //'??????' ?????? ?????? ???????????? ??????
                maxDate_sleep.set(year,month,dayOfMonth);
                minDate_sleep.set(year,month,dayOfMonth);
                c_after_sleep.set(year,month,dayOfMonth);
                c_before_sleep.set(year,month,dayOfMonth);
                String simple_sleep1 = simpleDateFormat.format(c_before_sleep.getTime());
                String simple_sleep2 = simpleDateFormat.format(c_after_sleep.getTime());
                day_1_sleeping.setText(simple_sleep1);
                day_2_sleeping.setText(simple_sleep2);

                //?????? ?????? ????????? ?????? ???????????? ??????
                FrameLayout01_book.setVisibility(View.VISIBLE);
                FrameLayout01_study.setVisibility(View.VISIBLE);
                FrameLayout01_sleeping.setVisibility(View.VISIBLE);
                FrameLayout01_exercise.setVisibility(View.VISIBLE);
            }
        });

        //'??????' ????????? '??????' ?????? ????????? ????????? ??????. ?????? ~ ?????? ?????? ??????
        btn_today_study = findViewById(R.id.period_today_study);
        btn_today_study.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar c = Calendar.getInstance(); // java.util.Calendar
                int year = c.get(Calendar.YEAR); //?????? ??????
                int month = c.get(Calendar.MONTH);//?????? ???
                int dayOfMonth = c.get(Calendar.DAY_OF_MONTH); //?????? ???
                maxDate_study.set(year,month,dayOfMonth); //?????? ????????? ?????? ????????? ?????? ??????
                minDate_study.set(year,month,dayOfMonth); //?????? ????????? ?????? ????????? ?????? ??????
                c_after_study.set(year,month,dayOfMonth); //?????? ????????? ?????? ????????? ??????
                c_before_study.set(year,month,dayOfMonth);//?????? ????????? ?????? ????????? ??????

                //SimpleDateForm ???????????? ?????? yyyy-mm-dd ?????? ????????? ?????? if???
                if(dayOfMonth <=9 && dayOfMonth>=1) {
                    if (month + 1 >= 1 && month + 1 <= 9){
                        day_1_study.setText(year + "-0" + (month + 1) + "-0" + dayOfMonth);
                        day_2_study.setText(year + "-0" + (month + 1) + "-0" + dayOfMonth);}
                    else {
                        day_1_study.setText(year + "-" + (month + 1) + "-0" + dayOfMonth);
                        day_2_study.setText(year + "-" + (month + 1) + "-0" + dayOfMonth);
                    }
                }
                else{
                    if(month+1>=1 && month+1<=9){
                        day_1_study.setText(year+ "-0"+(month+1)+"-"+dayOfMonth);
                        day_2_study.setText(year+ "-0"+(month+1)+"-"+dayOfMonth);}
                    else{
                        day_1_study.setText(year+ "-"+(month+1)+"-"+dayOfMonth);
                        day_2_study.setText(year+ "-"+(month+1)+"-"+dayOfMonth);}
                }
            }
        });

        //'??????'????????? '1??????' ?????? ????????? ????????? ??????. ?????? ~ 1?????? ??? ????????? ?????? ??????
        btn_week_study = findViewById(R.id.period_week_study);
        btn_week_study.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar c = Calendar.getInstance(); // java.util.Calendar
                int year_2 = c.get(Calendar.YEAR);
                int month_2 = c.get(Calendar.MONTH);
                int dayOfMonth_2 = c.get(Calendar.DAY_OF_MONTH);
                maxDate_study.set(year_2,month_2,dayOfMonth_2); //?????????(??????) ???????????? ?????? ????????? ?????? ?????? ??????
                c_after_study.set(year_2,month_2,dayOfMonth_2); //?????????(?????????) ????????? ????????? ??????
                Calendar c1 = Calendar.getInstance();
                c1.add(Calendar.DAY_OF_MONTH,-6);
                int dayOfMonth_1 = c1.get(Calendar.DAY_OF_MONTH);
                int month_1 = c1.get(Calendar.MONTH);
                int year_1 = c1.get(Calendar.YEAR);
                minDate_study.set(year_1,month_1,dayOfMonth_1); //?????????(?????????) ???????????? ??????????????? ?????? ?????? ??????
                c_before_study.set(year_1,month_1,dayOfMonth_1); //?????????(??????) ????????? ????????? ??????

                //SimpleDateForm ???????????? ?????? yyyy-mm-dd ?????? ????????? ?????? if???
                if(dayOfMonth_2 <=9 && dayOfMonth_2>=1) {
                    if(month_2+1>=1 && month_2+1<=9)
                        day_2_study.setText(year_2+ "-0"+(month_2+1)+"-0"+dayOfMonth_2);
                    else
                        day_2_study.setText(year_2+ "-"+(month_2+1)+"-0"+dayOfMonth_2);
                }
                else{
                    if(month_2+1>=1 && month_2+1<=9)
                        day_2_study.setText(year_2+ "-0"+(month_2+1)+"-"+dayOfMonth_2);
                    else
                        day_2_study.setText(year_2+ "-"+(month_2+1)+"-"+dayOfMonth_2);
                }
                if(dayOfMonth_1 <=9 && dayOfMonth_1>=1) {
                    if(month_1+1>=1 && month_1+1<=9)
                        day_2_study.setText(year_1+ "-0"+(month_2+1)+"-0"+dayOfMonth_1);
                    else
                        day_2_study.setText(year_1+ "-"+(month_2+1)+"-0"+dayOfMonth_1);
                }
                else{
                    if(month_1+1>=1 && month_1+1<=9)
                        day_1_study.setText(year_1+ "-0"+(month_1+1)+"-"+dayOfMonth_1);
                    else
                        day_1_study.setText(year_1+ "-"+(month_1+1)+"-"+dayOfMonth_1);
                }
            }
        });

        //'??????' ????????? '1??????' ?????? ????????? ????????? ??????. ?????? ~ 1?????? ???????????? ?????? ??????
        btn_month_study = findViewById(R.id.period_month_study);
        btn_month_study.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar c = Calendar.getInstance(); // java.util.Calendar
                int year_2 = c.get(Calendar.YEAR);
                int month_2 = c.get(Calendar.MONTH);
                int dayOfMonth_2 = c.get(Calendar.DAY_OF_MONTH);
                c_after_study.set(year_2,month_2,dayOfMonth_2); //?????????(?????????) ????????? ????????? ??????
                maxDate_study.set(year_2,month_2,dayOfMonth_2); //?????????(??????) ???????????? ?????? ????????? ?????? ?????? ??????
                Calendar c1 = Calendar.getInstance();
                c1.add(Calendar.MONTH,-1);
                c1.add(Calendar.DAY_OF_MONTH,1);
                int dayOfMonth_1 = c1.get(Calendar.DAY_OF_MONTH);
                int month_1 = c1.get(Calendar.MONTH);
                int year_1 = c1.get(Calendar.YEAR);
                c_before_study.set(year_1,month_1,dayOfMonth_1); //?????????(??????) ????????? 1?????? ????????? ??????
                minDate_study.set(year_1,month_1,dayOfMonth_1); //?????????(?????????) ???????????? ??????????????? ?????? ?????? ??????

                //SimpleDateForm ???????????? ?????? yyyy-mm-dd ?????? ????????? ?????? if???
                if(dayOfMonth_2 <=9 && dayOfMonth_2>=1) {
                    if(month_2+1>=1 && month_2+1<=9)
                        day_2_study.setText(year_2+ "-0"+(month_2+1)+"-0"+dayOfMonth_2);
                    else
                        day_2_study.setText(year_2+ "-"+(month_2+1)+"-0"+dayOfMonth_2);
                }
                else{
                    if(month_2+1>=1 && month_2+1<=9)
                        day_2_study.setText(year_2+ "-0"+(month_2+1)+"-"+dayOfMonth_2);
                    else
                        day_2_study.setText(year_2+ "-"+(month_2+1)+"-"+dayOfMonth_2);
                }
                if(dayOfMonth_1 <=9 && dayOfMonth_1>=1) {
                    if(month_1+1>=1 && month_1+1<=9)
                        day_2_study.setText(year_1+ "-0"+(month_2+1)+"-0"+dayOfMonth_1);
                    else
                        day_2_study.setText(year_1+ "-"+(month_2+1)+"-0"+dayOfMonth_1);
                }
                else{
                    if(month_1+1>=1 && month_1+1<=9)
                        day_1_study.setText(year_1+ "-0"+(month_1+1)+"-"+dayOfMonth_1);
                    else
                        day_1_study.setText(year_1+ "-"+(month_1+1)+"-"+dayOfMonth_1);
                }

            }
        });

        //'??????' ????????? ?????????(??????) ?????? ?????? ????????? ????????? ??????
        cal_before_study = findViewById(R.id.day_before_study);
        cal_before_study.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar c = Calendar.getInstance(); // java.util.Calendar
                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH);
                int dayOfMonth = c.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog dateDialog = new DatePickerDialog( StatisticsActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        minDate_study.set(year,month,dayOfMonth); // ????????? ???????????? ?????? ?????? ?????? ?????? ??????
                        c_before_study.set(year,month,dayOfMonth); // ????????? ????????? ?????????(??????) ????????? ??????
                        showToast(year + "-" + (month+1)  + "-" + dayOfMonth);

                        //SimpleDateForm ???????????? ?????? yyyy-mm-dd ?????? ????????? ?????? if???
                        if(dayOfMonth <=9 && dayOfMonth>=1){
                            if(month+1>=10)
                                day_1_study.setText(year+ "-"+(month+1)+"-0"+dayOfMonth);
                            else
                                day_1_study.setText(year+ "-0"+(month+1)+"-0"+dayOfMonth);
                        }
                        else{
                            if(month+1>=10)
                                day_1_study.setText(year+ "-"+(month+1)+"-"+dayOfMonth);
                            else
                                day_1_study.setText(year+ "-0"+(month+1)+"-"+dayOfMonth);
                        }
                    }
                }, year, month, dayOfMonth);
                dateDialog.getDatePicker().setMaxDate(maxDate_study.getTime().getTime()); //?????? ??????????????? ????????? ??? ??? ??????
                dateDialog.show();
            }
        });

        //'??????' ????????? ?????????(?????????) ?????? ?????? ??? ????????? ??????
        cal_after_study = findViewById(R.id.day_after_study);
        cal_after_study.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar c = Calendar.getInstance(); // java.util.Calendar
                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH);
                int dayOfMonth = c.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog dateDialog = new DatePickerDialog( StatisticsActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        maxDate_study.set(year,month,dayOfMonth); // ????????? ???????????? ?????? ????????? ?????? ??????
                        c_after_study.set(year,month,dayOfMonth); // ????????? ????????? ?????????(??????) ????????? ??????
                        showToast(year + "-" + (month+1)  + "-" + dayOfMonth);

                        //SimpleDateForm ???????????? ?????? yyyy-mm-dd ?????? ????????? ?????? if???
                        if(dayOfMonth <=9 && dayOfMonth>=1){
                            if(month+1>=10)
                                day_2_study.setText(year+ "-"+(month+1)+"-0"+dayOfMonth);
                            else
                                day_2_study.setText(year+ "-0"+(month+1)+"-0"+dayOfMonth);
                        }
                        else{
                            if(month+1>=10)
                                day_2_study.setText(year+ "-"+(month+1)+"-"+dayOfMonth);
                            else
                                day_2_study.setText(year+ "-0"+(month+1)+"-"+dayOfMonth);
                        }
                    }
                }, year, month, dayOfMonth);
                dateDialog.getDatePicker().setMinDate(minDate_study.getTime().getTime()); // ?????? ?????? ????????? ?????? ??????
                dateDialog.getDatePicker().setMaxDate(c.getTime().getTime()); // ?????? ????????? ?????? ????????? ??????
                dateDialog.show();
            }
        });

        //'??????' ????????? '??????' ?????? ????????? ????????? ??????. ?????? ~ ?????? ?????? ??????
        btn_today_exercise = findViewById(R.id.period_today_exercise);
        btn_today_exercise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar c = Calendar.getInstance(); // java.util.Calendar
                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH);
                int dayOfMonth = c.get(Calendar.DAY_OF_MONTH);
                maxDate_exercise.set(year,month,dayOfMonth);
                minDate_exercise.set(year,month,dayOfMonth);
                c_after_exercise.set(year,month,dayOfMonth);
                c_before_exercise.set(year,month,dayOfMonth);
                if(dayOfMonth <=9 && dayOfMonth>=1) {
                    if (month + 1 >= 1 && month + 1 <= 9){
                        day_1_exercise.setText(year + "-0" + (month + 1) + "-0" + dayOfMonth);
                        day_2_exercise.setText(year + "-0" + (month + 1) + "-0" + dayOfMonth);}
                    else {
                        day_1_exercise.setText(year + "-" + (month + 1) + "-0" + dayOfMonth);
                        day_2_exercise.setText(year + "-" + (month + 1) + "-0" + dayOfMonth);
                    }
                }
                else{
                    if(month+1>=1 && month+1<=9){
                        day_1_exercise.setText(year+ "-0"+(month+1)+"-"+dayOfMonth);
                        day_2_exercise.setText(year+ "-0"+(month+1)+"-"+dayOfMonth);}
                    else{
                        day_1_exercise.setText(year+ "-"+(month+1)+"-"+dayOfMonth);
                        day_2_exercise.setText(year+ "-"+(month+1)+"-"+dayOfMonth);}
                }
            }
        });

        //'??????' ????????? '1??????' ?????? ????????? ????????? ??????. 1?????? ??? ~ ?????? ?????? ??????
        btn_week_exercise = findViewById(R.id.period_week_exercise);
        btn_week_exercise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar c = Calendar.getInstance(); // java.util.Calendar
                int year_2 = c.get(Calendar.YEAR);
                int month_2 = c.get(Calendar.MONTH);
                int dayOfMonth_2 = c.get(Calendar.DAY_OF_MONTH);
                maxDate_exercise.set(year_2,month_2,dayOfMonth_2);
                c_after_exercise.set(year_2,month_2,dayOfMonth_2);
                Calendar c1 = Calendar.getInstance();
                c1.add(Calendar.DAY_OF_MONTH,-6);
                int dayOfMonth_1 = c1.get(Calendar.DAY_OF_MONTH);
                int month_1 = c1.get(Calendar.MONTH);
                int year_1 = c1.get(Calendar.YEAR);
                minDate_exercise.set(year_1,month_1,dayOfMonth_1);
                c_before_exercise.set(year_1,month_1,dayOfMonth_1);
                if(dayOfMonth_2 <=9 && dayOfMonth_2>=1) {
                    if(month_2+1>=1 && month_2+1<=9)
                        day_2_exercise.setText(year_2+ "-0"+(month_2+1)+"-0"+dayOfMonth_2);
                    else
                        day_2_exercise.setText(year_2+ "-"+(month_2+1)+"-0"+dayOfMonth_2);
                }
                else{
                    if(month_2+1>=1 && month_2+1<=9)
                        day_2_exercise.setText(year_2+ "-0"+(month_2+1)+"-"+dayOfMonth_2);
                    else
                        day_2_exercise.setText(year_2+ "-"+(month_2+1)+"-"+dayOfMonth_2);
                }
                if(dayOfMonth_1 <=9 && dayOfMonth_1>=1) {
                    if(month_1+1>=1 && month_1+1<=9)
                        day_2_exercise.setText(year_1+ "-0"+(month_2+1)+"-0"+dayOfMonth_1);
                    else
                        day_2_exercise.setText(year_1+ "-"+(month_2+1)+"-0"+dayOfMonth_1);
                }
                else{
                    if(month_1+1>=1 && month_1+1<=9)
                        day_1_exercise.setText(year_1+ "-0"+(month_1+1)+"-"+dayOfMonth_1);
                    else
                        day_1_exercise.setText(year_1+ "-"+(month_1+1)+"-"+dayOfMonth_1);
                }
            }
        });

        //'??????' ????????? '1??????' ?????? ????????? ????????? ??????. 1?????? ??? ~ ?????? ?????? ??????
        btn_month_exercise = findViewById(R.id.period_month_exercise);
        btn_month_exercise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar c = Calendar.getInstance(); // java.util.Calendar
                int year_2 = c.get(Calendar.YEAR);
                int month_2 = c.get(Calendar.MONTH);
                int dayOfMonth_2 = c.get(Calendar.DAY_OF_MONTH);
                maxDate_exercise.set(year_2,month_2,dayOfMonth_2);
                c_after_exercise.set(year_2,month_2,dayOfMonth_2);
                Calendar c1 = Calendar.getInstance();
                c1.add(Calendar.MONTH,-1);
                c1.add(Calendar.DAY_OF_MONTH,1);
                int dayOfMonth_1 = c1.get(Calendar.DAY_OF_MONTH);
                int month_1 = c1.get(Calendar.MONTH);
                int year_1 = c1.get(Calendar.YEAR);
                minDate_exercise.set(year_1,month_1,dayOfMonth_1);
                c_before_exercise.set(year_1,month_1,dayOfMonth_1);
                if(dayOfMonth_2 <=9 && dayOfMonth_2>=1) {
                    if(month_2+1>=1 && month_2+1<=9)
                        day_2_exercise.setText(year_2+ "-0"+(month_2+1)+"-0"+dayOfMonth_2);
                    else
                        day_2_exercise.setText(year_2+ "-"+(month_2+1)+"-0"+dayOfMonth_2);
                }
                else{
                    if(month_2+1>=1 && month_2+1<=9)
                        day_2_exercise.setText(year_2+ "-0"+(month_2+1)+"-"+dayOfMonth_2);
                    else
                        day_2_exercise.setText(year_2+ "-"+(month_2+1)+"-"+dayOfMonth_2);
                }
                if(dayOfMonth_1 <=9 && dayOfMonth_1>=1) {
                    if(month_1+1>=1 && month_1+1<=9)
                        day_2_exercise.setText(year_1+ "-0"+(month_2+1)+"-0"+dayOfMonth_1);
                    else
                        day_2_exercise.setText(year_1+ "-"+(month_2+1)+"-0"+dayOfMonth_1);
                }
                else{
                    if(month_1+1>=1 && month_1+1<=9)
                        day_1_exercise.setText(year_1+ "-0"+(month_1+1)+"-"+dayOfMonth_1);
                    else
                        day_1_exercise.setText(year_1+ "-"+(month_1+1)+"-"+dayOfMonth_1);
                }

            }
        });

        //'??????' ????????? ?????????(??????) ?????? ?????? ??? ????????? ??????
        cal_before_exercise = findViewById(R.id.day_before_exercise);
        cal_before_exercise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar c = Calendar.getInstance(); // java.util.Calendar
                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH);
                int dayOfMonth = c.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog dateDialog = new DatePickerDialog( StatisticsActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        minDate_exercise.set(year,month,dayOfMonth);
                        c_before_exercise.set(year,month,dayOfMonth);
                        showToast(year + "-" + (month+1)  + "-" + dayOfMonth);
                        if(dayOfMonth <=9 && dayOfMonth>=1){
                            if(month+1>=10)
                                day_1_exercise.setText(year+ "-"+(month+1)+"-0"+dayOfMonth);
                            else
                                day_1_exercise.setText(year+ "-0"+(month+1)+"-0"+dayOfMonth);
                        }
                        else{
                            if(month+1>=10)
                                day_1_exercise.setText(year+ "-"+(month+1)+"-"+dayOfMonth);
                            else
                                day_1_exercise.setText(year+ "-0"+(month+1)+"-"+dayOfMonth);
                        }
                    }
                }, year, month, dayOfMonth);
                dateDialog.getDatePicker().setMaxDate(maxDate_exercise.getTime().getTime());
                dateDialog.show();
            }
        });

        //'??????' ????????? ?????????(?????????) ?????? ?????? ??? ????????? ??????
        cal_after_exercise = findViewById(R.id.day_after_exercise);
        cal_after_exercise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar c = Calendar.getInstance(); // java.util.Calendar
                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH);
                int dayOfMonth = c.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog dateDialog = new DatePickerDialog( StatisticsActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        maxDate_exercise.set(year,month,dayOfMonth);
                        c_after_exercise.set(year,month,dayOfMonth);
                        showToast(year + "-" + (month+1)  + "-" + dayOfMonth);
                        if(dayOfMonth <=9 && dayOfMonth>=1){
                            if(month+1>=10)
                                day_2_exercise.setText(year+ "-"+(month+1)+"-0"+dayOfMonth);
                            else
                                day_2_exercise.setText(year+ "-0"+(month+1)+"-0"+dayOfMonth);
                        }
                        else{
                            if(month+1>=10)
                                day_2_exercise.setText(year+ "-"+(month+1)+"-"+dayOfMonth);
                            else
                                day_2_exercise.setText(year+ "-0"+(month+1)+"-"+dayOfMonth);
                        }
                    }
                }, year, month, dayOfMonth);
                dateDialog.getDatePicker().setMinDate(minDate_exercise.getTime().getTime());
                dateDialog.getDatePicker().setMaxDate(c.getTime().getTime());
                dateDialog.show();
            }
        });

        //'??????' ????????? '??????' ?????? ????????? ????????? ??????. ?????? ~ ?????? ?????? ??????
        btn_today_book = findViewById(R.id.period_today_book);
        btn_today_book.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar c = Calendar.getInstance(); // java.util.Calendar
                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH);
                int dayOfMonth = c.get(Calendar.DAY_OF_MONTH);
                minDate_book.set(year,month,dayOfMonth);
                maxDate_book.set(year,month,dayOfMonth);
                c_after_book.set(year,month,dayOfMonth);
                c_before_book.set(year,month,dayOfMonth);
                if(dayOfMonth <=9 && dayOfMonth>=1) {
                    if (month + 1 >= 1 && month + 1 <= 9){
                        day_1_book.setText(year + "-0" + (month + 1) + "-0" + dayOfMonth);
                        day_2_book.setText(year + "-0" + (month + 1) + "-0" + dayOfMonth);}
                    else {
                        day_1_book.setText(year + "-" + (month + 1) + "-0" + dayOfMonth);
                        day_2_book.setText(year + "-" + (month + 1) + "-0" + dayOfMonth);
                    }
                }
                else{
                    if(month+1>=1 && month+1<=9){
                        day_1_book.setText(year+ "-0"+(month+1)+"-"+dayOfMonth);
                        day_2_book.setText(year+ "-0"+(month+1)+"-"+dayOfMonth);}
                    else{
                        day_1_book.setText(year+ "-"+(month+1)+"-"+dayOfMonth);
                        day_2_book.setText(year+ "-"+(month+1)+"-"+dayOfMonth);}
                }
            }
        });

        //'??????' ????????? '1??????' ?????? ????????? ????????? ??????. 1?????? ??? ~ ?????? ?????? ??????
        btn_week_book = findViewById(R.id.period_week_book);
        btn_week_book.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar c = Calendar.getInstance(); // java.util.Calendar
                int year_2 = c.get(Calendar.YEAR);
                int month_2 = c.get(Calendar.MONTH);
                int dayOfMonth_2 = c.get(Calendar.DAY_OF_MONTH);
                maxDate_book.set(year_2,month_2,dayOfMonth_2);
                c_after_book.set(year_2,month_2,dayOfMonth_2);
                Calendar c1 = Calendar.getInstance();
                c1.add(Calendar.DAY_OF_MONTH,-6);
                int dayOfMonth_1 = c1.get(Calendar.DAY_OF_MONTH);
                int month_1 = c1.get(Calendar.MONTH);
                int year_1 = c1.get(Calendar.YEAR);
                minDate_book.set(year_1,month_1,dayOfMonth_1);
                c_before_book.set(year_1,month_1,dayOfMonth_1);
                if(dayOfMonth_2 <=9 && dayOfMonth_2>=1) {
                    if(month_2+1>=1 && month_2+1<=9)
                        day_2_book.setText(year_2+ "-0"+(month_2+1)+"-0"+dayOfMonth_2);
                    else
                        day_2_book.setText(year_2+ "-"+(month_2+1)+"-0"+dayOfMonth_2);
                }
                else{
                    if(month_2+1>=1 && month_2+1<=9)
                        day_2_book.setText(year_2+ "-0"+(month_2+1)+"-"+dayOfMonth_2);
                    else
                        day_2_book.setText(year_2+ "-"+(month_2+1)+"-"+dayOfMonth_2);
                }
                if(dayOfMonth_1 <=9 && dayOfMonth_1>=1) {
                    if(month_1+1>=1 && month_1+1<=9)
                        day_2_book.setText(year_1+ "-0"+(month_2+1)+"-0"+dayOfMonth_1);
                    else
                        day_2_book.setText(year_1+ "-"+(month_2+1)+"-0"+dayOfMonth_1);
                }
                else{
                    if(month_1+1>=1 && month_1+1<=9)
                        day_1_book.setText(year_1+ "-0"+(month_1+1)+"-"+dayOfMonth_1);
                    else
                        day_1_book.setText(year_1+ "-"+(month_1+1)+"-"+dayOfMonth_1);
                }
            }
        });

        //'??????' ????????? '1??????' ?????? ????????? ????????? ??????. 1?????? ??? ~ ?????? ?????? ??????
        btn_month_book = findViewById(R.id.period_month_book);
        btn_month_book.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar c = Calendar.getInstance(); // java.util.Calendar
                int year_2 = c.get(Calendar.YEAR);
                int month_2 = c.get(Calendar.MONTH);
                int dayOfMonth_2 = c.get(Calendar.DAY_OF_MONTH);
                maxDate_book.set(year_2,month_2,dayOfMonth_2);
                c_after_book.set(year_2,month_2,dayOfMonth_2);
                Calendar c1 = Calendar.getInstance();
                c1.add(Calendar.MONTH,-1);
                c1.add(Calendar.DAY_OF_MONTH,1);
                int dayOfMonth_1 = c1.get(Calendar.DAY_OF_MONTH);
                int month_1 = c1.get(Calendar.MONTH);
                int year_1 = c1.get(Calendar.YEAR);
                minDate_book.set(year_1,month_1,dayOfMonth_1);
                c_before_book.set(year_1,month_1,dayOfMonth_1);
                if(dayOfMonth_2 <=9 && dayOfMonth_2>=1) {
                    if(month_2+1>=1 && month_2+1<=9)
                        day_2_book.setText(year_2+ "-0"+(month_2+1)+"-0"+dayOfMonth_2);
                    else
                        day_2_book.setText(year_2+ "-"+(month_2+1)+"-0"+dayOfMonth_2);
                }
                else{
                    if(month_2+1>=1 && month_2+1<=9)
                        day_2_book.setText(year_2+ "-0"+(month_2+1)+"-"+dayOfMonth_2);
                    else
                        day_2_book.setText(year_2+ "-"+(month_2+1)+"-"+dayOfMonth_2);
                }
                if(dayOfMonth_1 <=9 && dayOfMonth_1>=1) {
                    if(month_1+1>=1 && month_1+1<=9)
                        day_2_book.setText(year_1+ "-0"+(month_2+1)+"-0"+dayOfMonth_1);
                    else
                        day_2_book.setText(year_1+ "-"+(month_2+1)+"-0"+dayOfMonth_1);
                }
                else{
                    if(month_1+1>=1 && month_1+1<=9)
                        day_1_book.setText(year_1+ "-0"+(month_1+1)+"-"+dayOfMonth_1);
                    else
                        day_1_book.setText(year_1+ "-"+(month_1+1)+"-"+dayOfMonth_1);
                }

            }
        });

        //'??????' ????????? ?????????(??????) ?????? ?????? ??? ????????? ??????
        cal_before_book = findViewById(R.id.day_before_book);
        cal_before_book.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar c = Calendar.getInstance(); // java.util.Calendar
                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH);
                int dayOfMonth = c.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog dateDialog = new DatePickerDialog( StatisticsActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        minDate_book.set(year,month,dayOfMonth);
                        c_before_book.set(year,month,dayOfMonth);
                        showToast(year + "-" + (month+1)  + "-" + dayOfMonth);
                        if(dayOfMonth <=9 && dayOfMonth>=1){
                            if(month+1>=10)
                                day_1_book.setText(year+ "-"+(month+1)+"-0"+dayOfMonth);
                            else
                                day_1_book.setText(year+ "-0"+(month+1)+"-0"+dayOfMonth);
                        }
                        else{
                            if(month+1>=10)
                                day_1_book.setText(year+ "-"+(month+1)+"-"+dayOfMonth);
                            else
                                day_1_book.setText(year+ "-0"+(month+1)+"-"+dayOfMonth);
                        }
                    }
                }, year, month, dayOfMonth);
                dateDialog.getDatePicker().setMaxDate(maxDate_book.getTime().getTime());
                dateDialog.show();
            }
        });

        //'??????' ????????? ?????????(?????????) ?????? ?????? ??? ????????? ??????
        cal_after_book = findViewById(R.id.day_after_book);
        cal_after_book.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar c = Calendar.getInstance(); // java.util.Calendar
                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH);
                int dayOfMonth = c.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog dateDialog = new DatePickerDialog( StatisticsActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        maxDate_book.set(year,month,dayOfMonth);
                        c_after_book.set(year,month,dayOfMonth);
                        showToast(year + "-" + (month+1)  + "-" + dayOfMonth);
                        if(dayOfMonth <=9 && dayOfMonth>=1){
                            if(month+1>=10)
                                day_2_book.setText(year+ "-"+(month+1)+"-0"+dayOfMonth);
                            else
                                day_2_book.setText(year+ "-0"+(month+1)+"-0"+dayOfMonth);
                        }
                        else{
                            if(month+1>=10)
                                day_2_book.setText(year+ "-"+(month+1)+"-"+dayOfMonth);
                            else
                                day_2_book.setText(year+ "-0"+(month+1)+"-"+dayOfMonth);
                        }
                    }
                }, year, month, dayOfMonth);
                dateDialog.getDatePicker().setMaxDate(c.getTime().getTime());
                dateDialog.getDatePicker().setMinDate(minDate_book.getTime().getTime());
                dateDialog.show();
            }
        });

        //'??????' ????????? '??????' ?????? ????????? ????????? ??????. ?????? ~ ?????? ?????? ??????
        btn_today_sleeping = findViewById(R.id.period_today_sleeping);
        btn_today_sleeping.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar c = Calendar.getInstance(); // java.util.Calendar
                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH);
                int dayOfMonth = c.get(Calendar.DAY_OF_MONTH);
                c_after_sleep.set(year,month,dayOfMonth);
                c_before_sleep.set(year,month,dayOfMonth);
                maxDate_sleep.set(year,month,dayOfMonth);
                minDate_sleep.set(year,month,dayOfMonth);
                if(dayOfMonth <=9 && dayOfMonth>=1) {
                    if (month + 1 >= 1 && month + 1 <= 9){
                        day_1_sleeping.setText(year + "-0" + (month + 1) + "-0" + dayOfMonth);
                        day_2_sleeping.setText(year + "-0" + (month + 1) + "-0" + dayOfMonth);}
                    else {
                        day_1_sleeping.setText(year + "-" + (month + 1) + "-0" + dayOfMonth);
                        day_2_sleeping.setText(year + "-" + (month + 1) + "-0" + dayOfMonth);
                    }
                }
                else{
                    if(month+1>=1 && month+1<=9){
                        day_1_sleeping.setText(year+ "-0"+(month+1)+"-"+dayOfMonth);
                        day_2_sleeping.setText(year+ "-0"+(month+1)+"-"+dayOfMonth);}
                    else{
                        day_1_sleeping.setText(year+ "-"+(month+1)+"-"+dayOfMonth);
                        day_2_sleeping.setText(year+ "-"+(month+1)+"-"+dayOfMonth);}
                }
            }
        });

        //'??????' ????????? '1??????' ?????? ????????? ????????? ??????. 1?????? ??? ~ ?????? ?????? ??????
        btn_week_sleeping = findViewById(R.id.period_week_sleeping);
        btn_week_sleeping.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar c = Calendar.getInstance(); // java.util.Calendar
                int year_2 = c.get(Calendar.YEAR);
                int month_2 = c.get(Calendar.MONTH);
                int dayOfMonth_2 = c.get(Calendar.DAY_OF_MONTH);
                maxDate_sleep.set(year_2,month_2,dayOfMonth_2);
                c_after_sleep.set(year_2,month_2,dayOfMonth_2);
                Calendar c1 = Calendar.getInstance();
                c1.add(Calendar.DAY_OF_MONTH,-6);
                int dayOfMonth_1 = c1.get(Calendar.DAY_OF_MONTH);
                int month_1 = c1.get(Calendar.MONTH);
                int year_1 = c1.get(Calendar.YEAR);
                minDate_sleep.set(year_1,month_1,dayOfMonth_1);
                c_before_sleep.set(year_1,month_1,dayOfMonth_1);
                if(dayOfMonth_2 <=9 && dayOfMonth_2>=1) {
                    if(month_2+1>=1 && month_2+1<=9)
                        day_2_sleeping.setText(year_2+ "-0"+(month_2+1)+"-0"+dayOfMonth_2);
                    else
                        day_2_sleeping.setText(year_2+ "-"+(month_2+1)+"-0"+dayOfMonth_2);
                }
                else{
                    if(month_2+1>=1 && month_2+1<=9)
                        day_2_sleeping.setText(year_2+ "-0"+(month_2+1)+"-"+dayOfMonth_2);
                    else
                        day_2_sleeping.setText(year_2+ "-"+(month_2+1)+"-"+dayOfMonth_2);
                }
                if(dayOfMonth_1 <=9 && dayOfMonth_1>=1) {
                    if(month_1+1>=1 && month_1+1<=9)
                        day_2_sleeping.setText(year_1+ "-0"+(month_2+1)+"-0"+dayOfMonth_1);
                    else
                        day_2_sleeping.setText(year_1+ "-"+(month_2+1)+"-0"+dayOfMonth_1);
                }
                else{
                    if(month_1+1>=1 && month_1+1<=9)
                        day_1_sleeping.setText(year_1+ "-0"+(month_1+1)+"-"+dayOfMonth_1);
                    else
                        day_1_sleeping.setText(year_1+ "-"+(month_1+1)+"-"+dayOfMonth_1);
                }
            }
        });

        //'??????' ????????? '1??????' ?????? ????????? ????????? ??????. 1?????? ??? ~ ?????? ?????? ??????
        btn_month_sleeping = findViewById(R.id.period_month_sleeping);
        btn_month_sleeping.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar c = Calendar.getInstance(); // java.util.Calendar
                int year_2 = c.get(Calendar.YEAR);
                int month_2 = c.get(Calendar.MONTH);
                int dayOfMonth_2 = c.get(Calendar.DAY_OF_MONTH);
                maxDate_sleep.set(year_2,month_2,dayOfMonth_2);
                c_after_sleep.set(year_2,month_2,dayOfMonth_2);
                Calendar c1 = Calendar.getInstance();
                c1.add(Calendar.MONTH,-1);
                c1.add(Calendar.DAY_OF_MONTH,1);
                int dayOfMonth_1 = c1.get(Calendar.DAY_OF_MONTH);
                int month_1 = c1.get(Calendar.MONTH);
                int year_1 = c1.get(Calendar.YEAR);
                minDate_sleep.set(year_1,month_1,dayOfMonth_1);
                c_before_sleep.set(year_1,month_1,dayOfMonth_1);
                if(dayOfMonth_2 <=9 && dayOfMonth_2>=1) {
                    if(month_2+1>=1 && month_2+1<=9)
                        day_2_sleeping.setText(year_2+ "-0"+(month_2+1)+"-0"+dayOfMonth_2);
                    else
                        day_2_sleeping.setText(year_2+ "-"+(month_2+1)+"-0"+dayOfMonth_2);
                }
                else{
                    if(month_2+1>=1 && month_2+1<=9)
                        day_2_sleeping.setText(year_2+ "-0"+(month_2+1)+"-"+dayOfMonth_2);
                    else
                        day_2_sleeping.setText(year_2+ "-"+(month_2+1)+"-"+dayOfMonth_2);
                }
                if(dayOfMonth_1 <=9 && dayOfMonth_1>=1) {
                    if(month_1+1>=1 && month_1+1<=9)
                        day_2_sleeping.setText(year_1+ "-0"+(month_2+1)+"-0"+dayOfMonth_1);
                    else
                        day_2_sleeping.setText(year_1+ "-"+(month_2+1)+"-0"+dayOfMonth_1);
                }
                else{
                    if(month_1+1>=1 && month_1+1<=9)
                        day_1_sleeping.setText(year_1+ "-0"+(month_1+1)+"-"+dayOfMonth_1);
                    else
                        day_1_sleeping.setText(year_1+ "-"+(month_1+1)+"-"+dayOfMonth_1);
                }

            }
        });

        //'??????' ????????? ?????????(??????) ?????? ?????? ??? ????????? ??????
        cal_before_sleeping = findViewById(R.id.day_before_sleeping);
        cal_before_sleeping.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar c = Calendar.getInstance(); // java.util.Calendar
                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH);
                int dayOfMonth = c.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog dateDialog = new DatePickerDialog( StatisticsActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        minDate_sleep.set(year,month,dayOfMonth);
                        c_before_sleep.set(year,month,dayOfMonth);
                        showToast(year + "-" + (month+1)  + "-" + dayOfMonth);
                        if(dayOfMonth <=9 && dayOfMonth>=1){
                            if(month+1>=10)
                                day_1_sleeping.setText(year+ "-"+(month+1)+"-0"+dayOfMonth);
                            else
                                day_1_sleeping.setText(year+ "-0"+(month+1)+"-0"+dayOfMonth);
                        }
                        else{
                            if(month+1>=10)
                                day_1_sleeping.setText(year+ "-"+(month+1)+"-"+dayOfMonth);
                            else
                                day_1_sleeping.setText(year+ "-0"+(month+1)+"-"+dayOfMonth);
                        };
                    }
                }, year, month, dayOfMonth);
                dateDialog.getDatePicker().setMaxDate(maxDate_sleep.getTime().getTime());
                dateDialog.show();
            }
        });

        //'??????' ????????? ?????????(?????????) ?????? ?????? ??? ????????? ??????
        cal_after_sleeping = findViewById(R.id.day_after_sleeping);
        cal_after_sleeping.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar c = Calendar.getInstance(); // java.util.Calendar
                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH);
                int dayOfMonth = c.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog dateDialog = new DatePickerDialog( StatisticsActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        maxDate_sleep.set(year,month,dayOfMonth);
                        c_after_sleep.set(year,month,dayOfMonth);
                        showToast(year + "-" + (month+1)  + "-" + dayOfMonth);
                        if(dayOfMonth <=9 && dayOfMonth>=1){
                            if(month+1>=10)
                                day_2_sleeping.setText(year+ "-"+(month+1)+"-0"+dayOfMonth);
                            else
                                day_2_sleeping.setText(year+ "-0"+(month+1)+"-0"+dayOfMonth);
                        }
                        else{
                            if(month+1>=10)
                                day_2_sleeping.setText(year+ "-"+(month+1)+"-"+dayOfMonth);
                            else
                                day_2_sleeping.setText(year+ "-0"+(month+1)+"-"+dayOfMonth);
                        }
                    }
                }, year, month, dayOfMonth);
                dateDialog.getDatePicker().setMaxDate(c.getTime().getTime());
                dateDialog.getDatePicker().setMinDate(minDate_sleep.getTime().getTime());
                dateDialog.show();
            }
        });

        //'??????' ?????? ?????? ??????
        btn_Lookup_study = findViewById(R.id.lookUP_study);
        btn_Lookup_exercise = findViewById(R.id.lookUP_exercise);
        btn_Lookup_book = findViewById(R.id.lookUP_book);
        btn_Lookup_sleeping = findViewById(R.id.lookUP_sleeping);

        // ?????? ????????? ?????? ?????? ??????
        FrameLayout01_study = (FrameLayout) findViewById(R.id.white_background_study);
        FrameLayout01_exercise = (FrameLayout) findViewById(R.id.white_background_exercise);
        FrameLayout01_book = (FrameLayout) findViewById(R.id.white_background_book);
        FrameLayout01_sleeping = (FrameLayout) findViewById(R.id.white_background_sleeping);

        // ????????? ?????? ??????????????? ?????? ??????
        customProgressDialog = new ProgressDialog(StatisticsActivity.this); //????????? ????????? ?????? ProgressDialog ?????? ??????
        customProgressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT)); //???????????? ????????? ??????
        customProgressDialog.setCancelable(false); //???????????? ???????????? ????????? ?????? ??? ??????

        //?????? ????????? '??????' ?????? ????????? ????????? ??????
        btn_Lookup_study.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                customProgressDialog.show(); //?????? ??? ??????
                accumulate_study = 0; // ?????? ????????? ?????? ??????
                //yyyy-mm-dd ????????? ????????? ???????????? ?????? SimpleDateFormat ?????? ??????
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat();
                simpleDateFormat.applyPattern("yyyy-MM-dd");
                int length_userName = userName.length();//??????????????? ?????? ????????? ???????????? ???????????? ?????? ????????? ????????? ?????? ????????????
                String tempo = simpleDateFormat.format(c_before_study.getTime()); // ?????? TextView??? setText??? ?????? ?????? ?????????
                formatted2_study = simpleDateFormat.format(c_after_study.getTime()); // ?????? TextView??? setText??? ?????? ?????? ?????????
                int check = 0; // while??? ????????? ????????? ????????? ?????? ?????? ???????????? ????????? ???????????? ?????? ????????? ??????
                while(c_before_study.compareTo(c_after_study)<=0){ // ????????? ????????? ????????? ????????? ???????????? ?????? ?????? ?????? ??????
                    check--;
                    formatted_study = simpleDateFormat.format(c_before_study.getTime()); // ????????????????????? ?????? ?????????????????? ????????? yyyy-mm-dd ???????????? ????????? yyyy-mm-dd?????? String?????? ??????

                    // ?????????????????? ???????????????????????? ?????? ???????????? ????????????
                    mdatabaseRef.child("UserAccount").child(firebaseUser.getUid()).child("Data").child("study").child(formatted_study).addValueEventListener(new ValueEventListener()
                    {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            temp_study = dataSnapshot.getValue(String.class);
                            if(temp_study == null)
                                temp_study = "0"; // ?????? ????????? ?????? ???????????? ???????????? ????????????
                            accumulate_study += Integer.valueOf(temp_study); // String??? ????????? ???????????? ??????
                            long hours = 0, minutes = 0, seconds, temp =0;
                            hours = accumulate_study / 3600;
                            temp = accumulate_study % 3600;
                            minutes = temp / 60;
                            seconds = temp % 60;

                            //????????? ?????? ????????? ?????? ???????????? ??????
                            result_study.setText("\n\n\n\n"+userName+" ??????  "+tempo+" ~ "+formatted2_study+"  ??????\n\n\n\n\n" +
                                    " ???   " + Long.toString(hours)+"?????? "+Long.toString(minutes)+"??? " +Long.toString(seconds)+"???   "+"?????????????????????." );

                            //?????? ????????? ???????????? ???????????? ??????????????? ?????? ??????
                            int length = result_study.getText().length();
                            int start = length_userName +46;
                            int end = length-11;
                            String content = result_study.getText().toString();
                            SpannableString spannableString = new SpannableString(content);
                            spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#FF6702")), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                            spannableString.setSpan(new StyleSpan(Typeface.BOLD_ITALIC), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                            spannableString.setSpan(new RelativeSizeSpan(1.15f), start, end, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);
                            spannableString.setSpan(new RelativeSizeSpan(1.15f), 4, 4+length_userName, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);
                            spannableString.setSpan(new StyleSpan(Typeface.BOLD_ITALIC), 4, 4+length_userName, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                            result_study.setText(spannableString);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                           // Log.e("MainActivity", String.valueOf(databaseError.toException())); // ????????? ??????
                        }
                    });
                    c_before_study.add(c_before_study.DAY_OF_MONTH,1);
                }
                c_before_study.add(c_before_study.DAY_OF_MONTH,check);//while??? ?????? ??? ?????? ???????????? ????????? ?????? ??????
                changeImageStudy();//?????? ?????? ?????????
            }
        });

        //?????? ????????? '??????' ?????? ????????? ????????? ??????
        btn_Lookup_exercise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                customProgressDialog.show();
                accumulate_exercise = 0;
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat();
                simpleDateFormat.applyPattern("yyyy-MM-dd");
                int length_userName = userName.length();
                formatted_exercise = simpleDateFormat.format(c_before_exercise.getTime());
                formatted2_exercise = simpleDateFormat.format(c_after_exercise.getTime());
                String tempo = simpleDateFormat.format(c_before_exercise.getTime());
                int check = 0;
                while(c_before_exercise.compareTo(c_after_exercise)<=0){
                    check--;
                    formatted_exercise = simpleDateFormat.format(c_before_exercise.getTime());
                    mdatabaseRef.child("UserAccount").child(firebaseUser.getUid()).child("Data").child("exercise").child(formatted_exercise).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            temp_exercise = dataSnapshot.getValue(String.class);
                            if(temp_exercise == null)
                                temp_exercise = "0";
                            accumulate_exercise += Integer.valueOf(temp_exercise);
                            long hours = 0, minutes = 0, seconds, temp =0;
                            hours = accumulate_exercise / 3600;
                            temp = accumulate_exercise % 3600;
                            minutes = temp / 60;
                            seconds = temp % 60;
                            result_exercise.setText("\n\n\n\n"+userName+" ??????  "+tempo+" ~ "+formatted2_exercise +"  ??????\n\n\n\n\n" +
                                    " ???   " +Long.toString(hours)+"?????? "+Long.toString(minutes)+"??? " +Long.toString(seconds)+"???   " + "?????????????????????." );
                            int length = result_exercise.getText().length();
                            int start = length_userName +46;
                            int end = length-11;
                            String content = result_exercise.getText().toString();
                            SpannableString spannableString = new SpannableString(content);
                            spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#FF6702")), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                            spannableString.setSpan(new StyleSpan(Typeface.BOLD_ITALIC), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                            spannableString.setSpan(new RelativeSizeSpan(1.15f), start, end, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);
                            spannableString.setSpan(new RelativeSizeSpan(1.15f), 4, 4+length_userName, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);
                            spannableString.setSpan(new StyleSpan(Typeface.BOLD_ITALIC), 4, 4+length_userName, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                            result_exercise.setText(spannableString);
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            // Log.e("MainActivity", String.valueOf(databaseError.toException())); // ????????? ??????
                        }
                    });
                    c_before_exercise.add(c_before_exercise.DAY_OF_MONTH,1);
                }
                c_before_exercise.add(c_before_exercise.DAY_OF_MONTH,check);
                        changeImageExercise();
            }
        });

        //?????? ????????? '??????' ?????? ????????? ????????? ??????
        btn_Lookup_book.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                customProgressDialog.show();
                int length_userName = userName.length();
                accumulate_book = 0;
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat();
                simpleDateFormat.applyPattern("yyyy-MM-dd");
                formatted_book = simpleDateFormat.format(c_before_book.getTime());
                formatted2_book = simpleDateFormat.format(c_after_book.getTime());
                String tempo = simpleDateFormat.format(c_before_book.getTime());
                int check = 0;
                while(c_before_book.compareTo(c_after_book)<=0){
                    check--;
                    formatted_book = simpleDateFormat.format(c_before_book.getTime());
                    mdatabaseRef.child("UserAccount").child(firebaseUser.getUid()).child("Data").child("book").child(formatted_book).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            temp_book = dataSnapshot.getValue(String.class);
                            if(temp_book == null)
                                temp_book = "0";
                            accumulate_book += Integer.valueOf(temp_book);
                            long hours = 0, minutes = 0, seconds, temp =0;
                            hours = accumulate_book / 3600;
                            temp = accumulate_book % 3600;
                            minutes = temp / 60;
                            seconds = temp % 60;
                            result_book.setText("\n\n\n\n" + userName+" ??????  "+tempo+" ~ "+formatted2_book +"  ??????\n\n\n\n\n" +
                                    " ???   " + Long.toString(hours)+"?????? "+Long.toString(minutes)+"??? " +Long.toString(seconds)+"???   " + "?????????????????????." );
                            int length = result_book.getText().length();
                            int start = length_userName +46;
                            int end = length-11;
                            String content = result_book.getText().toString();
                            SpannableString spannableString = new SpannableString(content);
                            spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#FF6702")), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                            spannableString.setSpan(new StyleSpan(Typeface.BOLD_ITALIC), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                            spannableString.setSpan(new RelativeSizeSpan(1.15f), start, end, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);
                            spannableString.setSpan(new RelativeSizeSpan(1.15f), 4, 4+length_userName, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);
                            spannableString.setSpan(new StyleSpan(Typeface.BOLD_ITALIC), 4, 4+length_userName, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                            result_book.setText(spannableString);
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            // Log.e("MainActivity", String.valueOf(databaseError.toException())); // ????????? ??????
                        }
                    });
                    c_before_book.add(c_before_book.DAY_OF_MONTH,1);
                }
                c_before_book.add(c_before_book.DAY_OF_MONTH,check);
                        changeImageBook();
            }
        });

        //?????? ????????? '??????' ?????? ????????? ????????? ??????
        btn_Lookup_sleeping.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                customProgressDialog.show();
                int length_userName = userName.length();
                accumulate_sleep = 0;
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat();
                simpleDateFormat.applyPattern("yyyy-MM-dd");
                formatted_sleep = simpleDateFormat.format(c_before_sleep.getTime());
                formatted2_sleep = simpleDateFormat.format(c_after_sleep.getTime());
                String tempo = simpleDateFormat.format(c_before_sleep.getTime());
                int check = 0;
                while(c_before_sleep.compareTo(c_after_sleep)<=0){
                    check--;
                    formatted_sleep = simpleDateFormat.format(c_before_sleep.getTime());
                    mdatabaseRef.child("UserAccount").child(firebaseUser.getUid()).child("Data").child("sleep").child(formatted_sleep).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            temp_sleep = dataSnapshot.getValue(String.class);
                            if(temp_sleep == null)
                                temp_sleep = "0";
                            accumulate_sleep += Integer.valueOf(temp_sleep);
                            long hours = 0, minutes = 0, seconds, temp =0;
                            hours = accumulate_sleep / 3600;
                            temp = accumulate_sleep % 3600;
                            minutes = temp / 60;
                            seconds = temp % 60;
                            result_sleep.setText("\n\n\n\n" + userName+" ??????  "+tempo+" ~ "+formatted2_sleep +"  ??????\n\n\n\n\n" +
                                    " ???   " + Long.toString(hours)+"?????? "+Long.toString(minutes)+"??? " +Long.toString(seconds)+"???" + "   ?????????????????????." );
                            int length = result_sleep.getText().length();
                            int start = length_userName +46;
                            int end = length-11;
                            String content = result_sleep.getText().toString();
                            SpannableString spannableString = new SpannableString(content);
                            spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#FF6702")), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                            spannableString.setSpan(new StyleSpan(Typeface.BOLD_ITALIC), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                            spannableString.setSpan(new RelativeSizeSpan(1.15f), start, end, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);
                            spannableString.setSpan(new RelativeSizeSpan(1.15f), 4, 4+length_userName, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);
                            spannableString.setSpan(new StyleSpan(Typeface.BOLD_ITALIC), 4, 4+length_userName, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                            result_sleep.setText(spannableString);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            // Log.e("MainActivity", String.valueOf(databaseError.toException())); // ????????? ??????
                        }
                    });
                    c_before_sleep.add(c_before_sleep.DAY_OF_MONTH,1);
                }
                c_before_sleep.add(c_before_sleep.DAY_OF_MONTH,check);
                //result_sleep.setText(formatted_sleep + "~" + formatted2_sleep );
                changeImageSleeping();
            }
        });
    }
    // ????????? ?????? ?????????
    private void showToast(String message){
        Toast toast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
        toast.show();
    }

    // ?????? ???????????? ?????? ????????? ?????????
    private void changeImageStudy() {
        FrameLayout01_study.setVisibility(View.VISIBLE);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run()
            {
                FrameLayout01_study.setVisibility(View.INVISIBLE);
                customProgressDialog.dismiss(); //????????? ?????????
            }
            }, 1000);
    }

    // ?????? ???????????? ?????? ????????? ?????????
    private void changeImageExercise() {
        FrameLayout01_exercise.setVisibility(View.VISIBLE);
            new Handler().postDelayed(new Runnable()
            {
                @Override
                public void run()
                {
                    FrameLayout01_exercise.setVisibility(View.INVISIBLE);
                    customProgressDialog.dismiss();
                }
            }, 600);

    }

    // ?????? ???????????? ?????? ????????? ?????????
    private void changeImageBook() {
        FrameLayout01_book.setVisibility(View.VISIBLE);
            new Handler().postDelayed(new Runnable()
            {
                @Override
                public void run()
                {
                    FrameLayout01_book.setVisibility(View.INVISIBLE);
                    customProgressDialog.dismiss();
                }
            }, 600);
    }

    // ?????? ???????????? ?????? ????????? ?????????
    private void changeImageSleeping() {
        FrameLayout01_sleeping.setVisibility(View.VISIBLE);
        new Handler().postDelayed(new Runnable()
            {
                @Override
                public void run()
                {
                    FrameLayout01_sleeping.setVisibility(View.INVISIBLE);
                    customProgressDialog.dismiss();
                }
            }, 600);
    }
}