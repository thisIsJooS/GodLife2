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

    //minDate는 달력에서 선택 가능한 최소날짜, maxDate는 달력에서 선택 가능한 최대날짜를 설정하기 위한 Calendar 객체
    Calendar minDate_study = Calendar.getInstance();
    Calendar maxDate_study = Calendar.getInstance();
    Calendar minDate_exercise = Calendar.getInstance();
    Calendar maxDate_exercise = Calendar.getInstance();
    Calendar minDate_book = Calendar.getInstance();
    Calendar maxDate_book = Calendar.getInstance();
    Calendar minDate_sleep = Calendar.getInstance();
    Calendar maxDate_sleep = Calendar.getInstance();

    ProgressDialog customProgressDialog; // 로딩창의 animation을 구현하기 위한 ProgressDialog 객체 생성

    int year1=now.get(Calendar.YEAR),year2=now.get(Calendar.YEAR);
    int month1=now.get(Calendar.MONTH)+1,month2=now.get(Calendar.MONTH)+1;
    int day_one=now.get(Calendar.DAY_OF_MONTH),day_two=now.get(Calendar.DAY_OF_MONTH);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //FireBase 사용자 인증
        FirebaseUser firebaseUser = mFirebaseAuth.getCurrentUser();
        mdatabaseRef = FirebaseDatabase.getInstance().getReference( "godlife" );
        mdatabaseRef.child("UserAccount").child(firebaseUser.getUid()).child("name").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userName = dataSnapshot.getValue(String.class);

            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Log.e("MainActivity", String.valueOf(databaseError.toException())); // 에러문 출력
            }
        });

        setContentView(R.layout.activity_statistics); // activity_statistics 레이아웃 생성
        TextView day_1_study,day_2_study,day_1_exercise,day_2_exercise,day_1_book,day_2_book,day_1_sleeping,day_2_sleeping;

        // 조회 결과에 대한 텍스트뷰 객체 생성
        TextView result_study = findViewById(R.id.text_result_study);
        TextView result_exercise = findViewById(R.id.text_result_exercise);
        TextView result_book = findViewById(R.id.text_result_book);
        TextView result_sleep = findViewById(R.id.text_result_sleep);

        //홈버튼 터치시 이벤트 구현. 이전 화면으로 돌아감
        btn_to_home = findViewById(R.id.btn_home);
        btn_to_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(StatisticsActivity.this,LoginActivity.class);
                startActivity(intent);
            }
        });

        // 화살표 버튼 터치시 이벤트 구현. 이전 화면으로 돌아감
        btn_go_back = findViewById(R.id.btn_back);
        btn_go_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(StatisticsActivity.this,RankingActivity.class);
                startActivity(intent);
            }
        });

        // tab1 indicator에 들어갈 뷰. 리니어 레이아웃을 자바에서 구현하여 아이콘으로 사용
        LinearLayout linear_study = new LinearLayout(this);
        linear_study.setOrientation(LinearLayout.VERTICAL); //리니어레이아웃의 orientation 설정
        linear_study.setGravity(Gravity.CENTER); //리니어 레이아웃의 gravity 설정
        linear_study.setBackgroundColor(Color.TRANSPARENT); // 리니어 레이아웃의 배경 색 설정
        TextView ivv_study = new TextView(this); //텍스트뷰를 자바코드에서 생성
        ivv_study.setText("공부"); // 텍스트뷰 텍스트 설정
        ivv_study.setBackgroundColor(Color.TRANSPARENT); //텍스트뷰 백그라운드 색 설정
        ivv_study.setTextColor(Color.parseColor("#000000")); //텍스트뷰 텍스트 색 설정
        ivv_study.setTextSize(12); //텍스트뷰 글자크기 설정
        ivv_study.setPadding(0,0,0,0); // 텍스트 뷰 패딩 설정
        ivv_study.setGravity(Gravity.BOTTOM); //텍스트 뷰 gravity 설정
        ImageView iv_study = new ImageView(this); //이미지뷰를 자바코드에서 생성
        iv_study.setImageResource(R.drawable.study); // 이미지뷰 이미지 리소스 설정
        iv_study.setBackgroundColor(Color.TRANSPARENT); //이미지뷰 백그라운드 색 설정
        LinearLayout.LayoutParams param_study = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        param_study.setMargins(0,20,0,5); //이미지 마진(왼쪽, 위, 오른쪽, 아래) 설정
        linear_study.addView(iv_study, param_study); //리니어 레이아웃 객체에 자식 뷰로 텍스트뷰 생성
        linear_study.addView(ivv_study,param_study); //리니어 레이아웃 객체에 자식 뷰로 이미지 뷰 생성
        linear_study.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.linear_background_ripple, null));//해당 뷰에 ripple(물결효과) 설정

        // tab2 indicator에 들어갈 뷰. 리니어 레이아웃을 자바에서 구현하여 아이콘으로 사용
        LinearLayout linear_exercise = new LinearLayout(this);
        linear_exercise.setOrientation(LinearLayout.VERTICAL);
        linear_exercise.setGravity(Gravity.CENTER);
        linear_exercise.setBackgroundColor(Color.TRANSPARENT);
        TextView ivv_exercise = new TextView(this); //텍스트뷰를 자바코드에서 생성
        ivv_exercise.setText("운동");
        ivv_exercise.setBackgroundColor(Color.TRANSPARENT);
        ivv_exercise.setTextColor(Color.parseColor("#000000"));
        ivv_exercise.setTextSize(12);
        ivv_exercise.setPadding(0,0,0,0);
        ImageView iv_exercise = new ImageView(this);
        iv_exercise.setImageResource(R.drawable.exercise); // 이미지 리소스
        iv_exercise.setBackgroundColor(Color.TRANSPARENT);
        LinearLayout.LayoutParams param_exercise = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        param_exercise.setMargins(0,20,0,5); //이미지 마진(왼쪽, 위, 오른쪽, 아래)
        linear_exercise.addView(iv_exercise, param_exercise);
        linear_exercise.addView(ivv_exercise,param_exercise);
        linear_exercise.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.linear_background_ripple, null));

        // tab3 indicator에 들어갈 뷰. 리니어 레이아웃을 자바에서 구현하여 아이콘으로 사용
        LinearLayout linear_book = new LinearLayout(this);
        linear_book.setOrientation(LinearLayout.VERTICAL);
        linear_book.setGravity(Gravity.CENTER);
        linear_book.setBackgroundColor(Color.TRANSPARENT);
        TextView ivv_book = new TextView(this); // 텍스트뷰를 자바코드에서 생성
        ivv_book.setText("독서");
        ivv_book.setBackgroundColor(Color.TRANSPARENT);
        ivv_book.setTextColor(Color.parseColor("#000000"));
        ivv_book.setTextSize(12);
        ImageView iv_book = new ImageView(this);
        iv_book.setImageResource(R.drawable.book); // 이미지 리소스
        iv_book.setBackgroundColor(Color.TRANSPARENT);
        LinearLayout.LayoutParams param_book = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        param_book.setMargins(0,20,0,5); //이미지 마진(왼쪽, 위, 오른쪽, 아래)
        linear_book.addView(iv_book, param_book);
        linear_book.addView(ivv_book,param_book);
        linear_book.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.linear_background_ripple, null));

        // tab4 indicator에 들어갈 뷰. 리니어 레이아웃을 자바코드에서 생성하여 아이콘으로 사용
        LinearLayout linear_sleep = new LinearLayout(this);
        linear_sleep.setOrientation(LinearLayout.VERTICAL);
        linear_sleep.setGravity(Gravity.CENTER);
        linear_sleep.setBackgroundColor(Color.TRANSPARENT);
        TextView ivv_sleep = new TextView(this); //텍스트뷰를 자바코드에서 생성
        ivv_sleep.setText("수면");
        ivv_sleep.setBackgroundColor(Color.TRANSPARENT);
        ivv_sleep.setTextColor(Color.parseColor("#000000"));
        ivv_sleep.setTextSize(12);
        ivv_sleep.setPadding(0,0,0,5);
        ImageView iv_sleep = new ImageView(this);
        iv_sleep.setImageResource(R.drawable.sleep); // 이미지 리소스
        iv_sleep.setBackgroundColor(Color.TRANSPARENT);
        LinearLayout.LayoutParams param_sleep = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        param_sleep.setMargins(0,20,0,0); //이미지 마진(왼쪽, 위, 오른쪽, 아래)
        linear_sleep.addView(iv_sleep, param_sleep);
        linear_sleep.addView(ivv_sleep,param_sleep);
        linear_sleep.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.linear_background_ripple, null));

        //TabHost 구현
        TabHost tabHost = findViewById(R.id.host);
        tabHost.setup();
        TabHost.TabSpec spec = tabHost.newTabSpec("tab1");
        spec.setIndicator(linear_study);    // Indicator를 뷰로 설정
        spec.setContent(R.id.tab_content1);
        tabHost.addTab(spec);
        spec = tabHost.newTabSpec("tab2");
        spec.setIndicator(linear_exercise); // Indicator를 뷰로 설정
        spec.setContent(R.id.tab_content2);
        tabHost.addTab(spec);
        spec = tabHost.newTabSpec("tab3");
        spec.setIndicator(linear_book);     // Indicator를 뷰로 설정
        spec.setContent(R.id.tab_content3);
        tabHost.addTab(spec);
        spec = tabHost.newTabSpec("tab4");
        spec.setContent(R.id.tab_content4);
        spec.setIndicator(linear_sleep);    // Indicator를 뷰로 설정
        tabHost.addTab(spec);
        tabHost.setCurrentTab(0);           // 초기 탭 화면

        //날짜 표시해줄 TextView 객체 생성
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

        //탭 변경에 반응하여 이전 탭 화면을 디폴트로 변경하기 위한 리스너 설정
        tabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String s) {

                //SimpleDateFormat을 활용하여 yyyy-MM-dd 형태의 날짜표기
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat();
                simpleDateFormat.applyPattern("yyyy-MM-dd");
                Calendar c = Calendar.getInstance();
                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH);
                int dayOfMonth = c.get(Calendar.DAY_OF_MONTH);

                //'공부' 관련 항목 디폴트로 변경
                maxDate_study.set(year,month,dayOfMonth);
                minDate_study.set(year,month,dayOfMonth);
                c_after_study.set(year,month,dayOfMonth);
                c_before_study.set(year,month,dayOfMonth);
                String simple_study1 = simpleDateFormat.format(c_before_study.getTime());
                String simple_study2 = simpleDateFormat.format(c_after_study.getTime());
                day_1_study.setText(simple_study1);
                day_2_study.setText(simple_study2);

                //'운동' 관련 항목 디폴트로 변경
                maxDate_exercise.set(year,month,dayOfMonth);
                minDate_exercise.set(year,month,dayOfMonth);
                c_after_exercise.set(year,month,dayOfMonth);
                c_before_exercise.set(year,month,dayOfMonth);
                String simple_exercise1 = simpleDateFormat.format(c_before_exercise.getTime());
                String simple_exercise2 = simpleDateFormat.format(c_after_exercise.getTime());
                day_1_exercise.setText(simple_exercise1);
                day_2_exercise.setText(simple_exercise2);

                //'독서' 관련 항목 디폴트로 변경
                maxDate_book.set(year,month,dayOfMonth);
                minDate_book.set(year,month,dayOfMonth);
                c_after_book.set(year,month,dayOfMonth);
                c_before_book.set(year,month,dayOfMonth);
                String simple_book1 = simpleDateFormat.format(c_before_book.getTime());
                String simple_book2 = simpleDateFormat.format(c_after_book.getTime());
                day_1_book.setText(simple_book1);
                day_2_book.setText(simple_book2);

                //'수면' 관련 항목 디폴트로 변경
                maxDate_sleep.set(year,month,dayOfMonth);
                minDate_sleep.set(year,month,dayOfMonth);
                c_after_sleep.set(year,month,dayOfMonth);
                c_before_sleep.set(year,month,dayOfMonth);
                String simple_sleep1 = simpleDateFormat.format(c_before_sleep.getTime());
                String simple_sleep2 = simpleDateFormat.format(c_after_sleep.getTime());
                day_1_sleeping.setText(simple_sleep1);
                day_2_sleeping.setText(simple_sleep2);

                //결과 화면 띄우기 전의 화면으로 변경
                FrameLayout01_book.setVisibility(View.VISIBLE);
                FrameLayout01_study.setVisibility(View.VISIBLE);
                FrameLayout01_sleeping.setVisibility(View.VISIBLE);
                FrameLayout01_exercise.setVisibility(View.VISIBLE);
            }
        });

        //'공부' 화면의 '당일' 버튼 터치시 이벤트 구현. 당일 ~ 당일 날짜 설정
        btn_today_study = findViewById(R.id.period_today_study);
        btn_today_study.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar c = Calendar.getInstance(); // java.util.Calendar
                int year = c.get(Calendar.YEAR); //당일 연도
                int month = c.get(Calendar.MONTH);//당일 월
                int dayOfMonth = c.get(Calendar.DAY_OF_MONTH); //당일 일
                maxDate_study.set(year,month,dayOfMonth); //달력 선택시 선택 가능한 최대 날짜
                minDate_study.set(year,month,dayOfMonth); //달력 선택시 선택 가능한 최소 날짜
                c_after_study.set(year,month,dayOfMonth); //이전 날짜를 당일 날짜로 설정
                c_before_study.set(year,month,dayOfMonth);//이후 날짜를 당일 날짜로 설정

                //SimpleDateForm 사용하지 않고 yyyy-mm-dd 형식 만들기 위한 if문
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

        //'공부'화면의 '1주일' 버튼 터치시 이벤트 구현. 당일 ~ 1주일 전 까지의 날짜 설정
        btn_week_study = findViewById(R.id.period_week_study);
        btn_week_study.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar c = Calendar.getInstance(); // java.util.Calendar
                int year_2 = c.get(Calendar.YEAR);
                int month_2 = c.get(Calendar.MONTH);
                int dayOfMonth_2 = c.get(Calendar.DAY_OF_MONTH);
                maxDate_study.set(year_2,month_2,dayOfMonth_2); //첫번째(왼쪽) 달력에서 설정 가능한 최대 날짜 설정
                c_after_study.set(year_2,month_2,dayOfMonth_2); //두번째(오른쪽) 날짜를 오늘로 설정
                Calendar c1 = Calendar.getInstance();
                c1.add(Calendar.DAY_OF_MONTH,-6);
                int dayOfMonth_1 = c1.get(Calendar.DAY_OF_MONTH);
                int month_1 = c1.get(Calendar.MONTH);
                int year_1 = c1.get(Calendar.YEAR);
                minDate_study.set(year_1,month_1,dayOfMonth_1); //두번째(오른쪽) 달력에서 선택가능한 최소 날짜 설정
                c_before_study.set(year_1,month_1,dayOfMonth_1); //첫번째(왼쪽) 날짜를 오늘로 설정

                //SimpleDateForm 사용하지 않고 yyyy-mm-dd 형식 만들기 위한 if문
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

        //'공부' 화면의 '1개월' 버튼 터치시 이벤트 구현. 당일 ~ 1개월 전까지의 날짜 설정
        btn_month_study = findViewById(R.id.period_month_study);
        btn_month_study.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar c = Calendar.getInstance(); // java.util.Calendar
                int year_2 = c.get(Calendar.YEAR);
                int month_2 = c.get(Calendar.MONTH);
                int dayOfMonth_2 = c.get(Calendar.DAY_OF_MONTH);
                c_after_study.set(year_2,month_2,dayOfMonth_2); //두번째(오른쪽) 날짜를 오늘로 설정
                maxDate_study.set(year_2,month_2,dayOfMonth_2); //첫번째(왼쪽) 달력에서 설정 가능한 최대 날짜 설정
                Calendar c1 = Calendar.getInstance();
                c1.add(Calendar.MONTH,-1);
                c1.add(Calendar.DAY_OF_MONTH,1);
                int dayOfMonth_1 = c1.get(Calendar.DAY_OF_MONTH);
                int month_1 = c1.get(Calendar.MONTH);
                int year_1 = c1.get(Calendar.YEAR);
                c_before_study.set(year_1,month_1,dayOfMonth_1); //첫번째(왼쪽) 날짜를 1개월 전으로 설정
                minDate_study.set(year_1,month_1,dayOfMonth_1); //두번째(오른쪽) 달력에서 선택가능한 최소 날짜 설정

                //SimpleDateForm 사용하지 않고 yyyy-mm-dd 형식 만들기 위한 if문
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

        //'공부' 화면의 첫번째(왼쪽) 달력 버튼 터치시 이벤트 구현
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
                        minDate_study.set(year,month,dayOfMonth); // 두번째 달력에서 설정 할수 있는 최소 날짜
                        c_before_study.set(year,month,dayOfMonth); // 선택한 날짜를 첫번째(왼쪽) 날짜로 설정
                        showToast(year + "-" + (month+1)  + "-" + dayOfMonth);

                        //SimpleDateForm 사용하지 않고 yyyy-mm-dd 형식 만들기 위한 if문
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
                dateDialog.getDatePicker().setMaxDate(maxDate_study.getTime().getTime()); //최대 날짜까지의 설정만 할 수 있음
                dateDialog.show();
            }
        });

        //'공부' 화면의 두번쩨(오른쪽) 달력 터치 시 이벤트 구현
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
                        maxDate_study.set(year,month,dayOfMonth); // 첫번째 달력에서 설정 가능한 최대 날짜
                        c_after_study.set(year,month,dayOfMonth); // 선택한 날짜를 두번째(왼쪽) 날짜로 설정
                        showToast(year + "-" + (month+1)  + "-" + dayOfMonth);

                        //SimpleDateForm 사용하지 않고 yyyy-mm-dd 형식 만들기 위한 if문
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
                dateDialog.getDatePicker().setMinDate(minDate_study.getTime().getTime()); // 최소 날짜 까지만 선택 가능
                dateDialog.getDatePicker().setMaxDate(c.getTime().getTime()); // 선택 가능한 최대 날짜는 오늘
                dateDialog.show();
            }
        });

        //'운동' 화면의 '당일' 버튼 터치시 이벤트 구현. 당일 ~ 당일 날짜 설정
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

        //'운동' 화면의 '1주일' 버튼 터치시 이벤트 구현. 1주일 전 ~ 당일 날짜 설정
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

        //'운동' 화면의 '1개월' 버튼 터치시 이벤트 구현. 1개월 전 ~ 당일 날짜 설정
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

        //'운동' 화면의 첫번째(왼쪽) 달력 터치 시 이벤트 구현
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

        //'운동' 화면의 두번째(오른쪽) 달력 터치 시 이벤트 구현
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

        //'독서' 화면의 '당일' 버튼 터치시 이벤트 구현. 당일 ~ 당일 날짜 설정
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

        //'독서' 화면의 '1주일' 버튼 터치시 이벤트 구현. 1주일 전 ~ 당일 날짜 설정
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

        //'독서' 화면의 '1개월' 버튼 터치시 이벤트 구현. 1개월 전 ~ 당일 날짜 설정
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

        //'독서' 화면의 첫번째(왼쪽) 달력 터치 시 이벤트 구현
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

        //'독서' 화면의 두번째(오른쪽) 달력 터치 시 이벤트 구현
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

        //'수면' 화면의 '당일' 버튼 터치시 이벤트 구현. 당일 ~ 당일 날짜 설정
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

        //'수면' 화면의 '1주일' 버튼 터치시 이벤트 구현. 1주일 전 ~ 당일 날짜 설정
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

        //'수면' 화면의 '1개월' 버튼 터치시 이벤트 구현. 1개월 전 ~ 당일 날짜 설정
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

        //'수면' 화면의 첫번째(왼쪽) 달력 터치 시 이벤트 구현
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

        //'수면' 화면의 두번째(오른쪽) 달력 터치 시 이벤트 구현
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

        //'조회' 버튼 객체 생성
        btn_Lookup_study = findViewById(R.id.lookUP_study);
        btn_Lookup_exercise = findViewById(R.id.lookUP_exercise);
        btn_Lookup_book = findViewById(R.id.lookUP_book);
        btn_Lookup_sleeping = findViewById(R.id.lookUP_sleeping);

        // 조회 이전의 화면 객체 생성
        FrameLayout01_study = (FrameLayout) findViewById(R.id.white_background_study);
        FrameLayout01_exercise = (FrameLayout) findViewById(R.id.white_background_exercise);
        FrameLayout01_book = (FrameLayout) findViewById(R.id.white_background_book);
        FrameLayout01_sleeping = (FrameLayout) findViewById(R.id.white_background_sleeping);

        // 로딩을 위한 애니메이션 객체 생성
        customProgressDialog = new ProgressDialog(StatisticsActivity.this); //로딩창 구현을 위한 ProgressDialog 객체 생성
        customProgressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT)); //로딩화면 투명도 설정
        customProgressDialog.setCancelable(false); //사용자가 로딩창을 터치로 없앨 수 없음

        //공부 화면의 '조회' 버튼 터치시 이벤트 구현
        btn_Lookup_study.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                customProgressDialog.show(); //로딩 창 생성
                accumulate_study = 0; // 시간 누적을 위한 변수
                //yyyy-mm-dd 형태의 날짜를 생성하기 위한 SimpleDateFormat 객체 생성
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat();
                simpleDateFormat.applyPattern("yyyy-MM-dd");
                int length_userName = userName.length();//조회결과에 대한 텍스트 스타일을 변경하기 위해 사용자 이름의 길이 입력받기
                String tempo = simpleDateFormat.format(c_before_study.getTime()); // 결과 TextView의 setText에 쓰일 날짜 스트링
                formatted2_study = simpleDateFormat.format(c_after_study.getTime()); // 결과 TextView의 setText에 쓰일 날짜 스트링
                int check = 0; // while문 이후에 이전의 날짜를 다시 원래 입력받은 날짜로 변경하기 위해 사용될 변수
                while(c_before_study.compareTo(c_after_study)<=0){ // 이전의 날짜가 이후의 날짜와 같아질떼 까지 일별 시간 누적
                    check--;
                    formatted_study = simpleDateFormat.format(c_before_study.getTime()); // 데이터베이스의 일별 시간데이터의 태그가 yyyy-mm-dd 꼴이므로 날짜를 yyyy-mm-dd꼴의 String으로 변환

                    // 파이어베이스 데이터베이스에서 일별 시간정보 받아오기
                    mdatabaseRef.child("UserAccount").child(firebaseUser.getUid()).child("Data").child("study").child(formatted_study).addValueEventListener(new ValueEventListener()
                    {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            temp_study = dataSnapshot.getValue(String.class);
                            if(temp_study == null)
                                temp_study = "0"; // 해당 날짜의 시간 데이터가 존재하지 않을경우
                            accumulate_study += Integer.valueOf(temp_study); // String을 정수로 변환하여 저장
                            long hours = 0, minutes = 0, seconds, temp =0;
                            hours = accumulate_study / 3600;
                            temp = accumulate_study % 3600;
                            minutes = temp / 60;
                            seconds = temp % 60;

                            //결과에 대한 텍스트 뷰의 텍스트를 변경
                            result_study.setText("\n\n\n\n"+userName+" 님은  "+tempo+" ~ "+formatted2_study+"  동안\n\n\n\n\n" +
                                    " 총   " + Long.toString(hours)+"시간 "+Long.toString(minutes)+"분 " +Long.toString(seconds)+"초   "+"공부하셨습니다." );

                            //결과 텍스트 일부분의 스타일을 변경해주기 위한 코드
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
                           // Log.e("MainActivity", String.valueOf(databaseError.toException())); // 에러문 출력
                        }
                    });
                    c_before_study.add(c_before_study.DAY_OF_MONTH,1);
                }
                c_before_study.add(c_before_study.DAY_OF_MONTH,check);//while문 탈출 시 원래 입력받은 날짜로 다시 설정
                changeImageStudy();//결과 화면 띄우기
            }
        });

        //운동 화면의 '조회' 버튼 터치시 이벤트 구현
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
                            result_exercise.setText("\n\n\n\n"+userName+" 님은  "+tempo+" ~ "+formatted2_exercise +"  동안\n\n\n\n\n" +
                                    " 총   " +Long.toString(hours)+"시간 "+Long.toString(minutes)+"분 " +Long.toString(seconds)+"초   " + "운동하셨습니다." );
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
                            // Log.e("MainActivity", String.valueOf(databaseError.toException())); // 에러문 출력
                        }
                    });
                    c_before_exercise.add(c_before_exercise.DAY_OF_MONTH,1);
                }
                c_before_exercise.add(c_before_exercise.DAY_OF_MONTH,check);
                        changeImageExercise();
            }
        });

        //독서 화면의 '조회' 버튼 터치시 이벤트 구현
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
                            result_book.setText("\n\n\n\n" + userName+" 님은  "+tempo+" ~ "+formatted2_book +"  동안\n\n\n\n\n" +
                                    " 총   " + Long.toString(hours)+"시간 "+Long.toString(minutes)+"분 " +Long.toString(seconds)+"초   " + "독서하셨습니다." );
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
                            // Log.e("MainActivity", String.valueOf(databaseError.toException())); // 에러문 출력
                        }
                    });
                    c_before_book.add(c_before_book.DAY_OF_MONTH,1);
                }
                c_before_book.add(c_before_book.DAY_OF_MONTH,check);
                        changeImageBook();
            }
        });

        //독서 화면의 '조회' 버튼 터치시 이벤트 구현
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
                            result_sleep.setText("\n\n\n\n" + userName+" 님은  "+tempo+" ~ "+formatted2_sleep +"  동안\n\n\n\n\n" +
                                    " 총   " + Long.toString(hours)+"시간 "+Long.toString(minutes)+"분 " +Long.toString(seconds)+"초" + "   수면하셨습니다." );
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
                            // Log.e("MainActivity", String.valueOf(databaseError.toException())); // 에러문 출력
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
    // 토스트 생성 메소드
    private void showToast(String message){
        Toast toast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
        toast.show();
    }

    // 공부 조회결과 화면 띄우기 메소드
    private void changeImageStudy() {
        FrameLayout01_study.setVisibility(View.VISIBLE);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run()
            {
                FrameLayout01_study.setVisibility(View.INVISIBLE);
                customProgressDialog.dismiss(); //로딩창 지우기
            }
            }, 1000);
    }

    // 운동 조회결과 화면 띄우기 메소드
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

    // 독서 조회결과 화면 띄우기 메소드
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

    // 수면 조회결과 화면 띄우기 메소드
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