package com.example.godlife7;


import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

//import org.checkerframework.checker.units.qual.A;

import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class RankingActivity extends AppCompatActivity implements View.OnClickListener{
    private FirebaseAuth mFirebaseAuth; //파이어베이스 인증
    private DatabaseReference mDatabase; //실시간 데이터베이스
    private DatabaseReference mockUsersDatabase;
    private DatabaseReference realUsersDatabase;
    private FirebaseUser currentUser;

    private HashMap<String, Long> exercise_daily = new HashMap<>();
    private HashMap<String, Long> exercise_weekly = new HashMap<>();
    private HashMap<String, Long> read_daily = new HashMap<>();
    private HashMap<String, Long> read_weekly = new HashMap<>();
    private HashMap<String, Long> study_daily = new HashMap<>();
    private HashMap<String, Long> study_weekly = new HashMap<>();
    private HashMap<String, Long> sleep_daily = new HashMap<>();
    private HashMap<String, Long> sleep_weekly = new HashMap<>();

    private HashMap<String, Long> exercise_daily_friends = new HashMap<>();
    private HashMap<String, Long> exercise_weekly_friends = new HashMap<>();
    private HashMap<String, Long> read_daily_friends = new HashMap<>();
    private HashMap<String, Long> read_weekly_friends = new HashMap<>();
    private HashMap<String, Long> study_daily_friends = new HashMap<>();
    private HashMap<String, Long> study_weekly_friends = new HashMap<>();
    private HashMap<String, Long> sleep_daily_friends = new HashMap<>();
    private HashMap<String, Long> sleep_weekly_friends = new HashMap<>();

    private LinearLayout weeklyRankingLayout;
    private LinearLayout dailyRankingLayout;

    private static Boolean ACTIVATED = true;
    private static Boolean DEACTIVATED = false;

    private HashMap<String, Boolean> category = new HashMap<>();
    private HashMap<String, Boolean> range = new HashMap<>();

    private LinearLayout categoryStudy ;
    private LinearLayout categoryExercise;
    private LinearLayout categoryRead;
    private LinearLayout categorySleep;
    private TextView rangeAll;
    private TextView rangeFriends;

    private TextView weeklyRankingTitle;
    private TextView dailyRankingTitle;
    private TextView myWeeklyRanking;
    private TextView myWeeklyUsername;
    private TextView myWeeklyTime;
    private TextView myDailyRanking;
    private TextView myDailyUsername;
    private TextView myDailyTime;


    private Button showAddFriendDialogButton;
    private AlertDialog addFriendDialog;

    private Button addFriendBtn;
    private EditText friendEmailInput;

    private ShowFriendDialogListenerClass showFriendDialogListener;
    private AddFriendListenerClass addFriendListenerClass;

    private TextView usernameTv;

    private static long friendSize = 0;
    private Set<String> friendList = new HashSet<>();
    private String currentDate;
    private List<String> aWeekDateList = new ArrayList<>();

    private Map<String, Integer> rankingUsernameMapWeekly = new HashMap<>();
    private Map<String, Integer> rankingUsernameMapDaily = new HashMap<>();

    public RankingActivity(){
        category.put("STUDY", true);
        category.put("EXERCISE", false);
        category.put("READ", false);
        category.put("SLEEP", false);

        range.put("ALL", true);
        range.put("FRIENDS", false);
    }

    public void setCurrentDate(){
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        String dateAndTime = timestamp.toString(); // 2022-11-29 17:30:30:323
        currentDate = dateAndTime.split(" ")[0];
    }

    public void setOneWeekAgoDate(){
        aWeekDateList.clear();
        DecimalFormat df = new DecimalFormat("00");
        Calendar currentCalendar = Calendar.getInstance();
        //일주일 전 날짜 구하기
        for(int i = 1; i<8; i++){
            currentCalendar.add(currentCalendar.DATE, -1);
            String strYear7 = Integer.toString(currentCalendar.get(Calendar.YEAR));
            String strMonth7 = df.format(currentCalendar.get(Calendar.MONTH) + 1);
            String strDay7 = df.format(currentCalendar.get(Calendar.DATE));
            String strDate7 = strYear7 + "-" + strMonth7 + "-" + strDay7;
            aWeekDateList.add(strDate7);
        }

    }

    public static HashMap<String, Long> sortByValue(HashMap<String, Long> hm){
        // 1
        List<Map.Entry<String, Long> > list =
                new LinkedList<Map.Entry<String, Long>>(hm.entrySet());
        // 2
        Collections.sort(list, new Comparator<Map.Entry<String, Long> >() {
            public int compare(Map.Entry<String, Long> o1,
                               Map.Entry<String, Long> o2)
            {
                return (o2.getValue()).compareTo(o1.getValue());
            }
        });
        // 3
        HashMap<String, Long> temp = new LinkedHashMap<String, Long>();
        for (Map.Entry<String, Long> aa : list) {
            temp.put(aa.getKey(), aa.getValue());
        }
        // 4
        return temp;
    }

    private void setFriendList(){
        DatabaseReference friendsDBRef = realUsersDatabase.child(currentUser.getUid()).child("friends");

        friendsDBRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    String friendUID = dataSnapshot.getKey();
                    friendList.add(friendUID);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void saveFriendsWeeklyDataCallback(DataSnapshot snapshot){
        setOneWeekAgoDate();
        // DataSnapshot : UserAccount
        for (DataSnapshot dataSnapshot : snapshot.getChildren()){ // 유저마다
            String friendUID = dataSnapshot.getKey();
            String friendName = snapshot.child(friendUID).child("name").getValue(String.class);
            Long studyTimeWeekly = 0L;
            Long exerciseTimeWeekly = 0L;
            Long bookTimeWeekly = 0L;
            Long sleepTimeWeekly = 0L;

            for(DataSnapshot timeDataSnapshot : dataSnapshot.child("Data").child("study").getChildren()){ // 날짜마다
                if (timeDataSnapshot.getKey() == null) continue;
                if (aWeekDateList.contains(timeDataSnapshot.getKey())){
                    String timeDataStr = timeDataSnapshot.getValue(String.class);
                    studyTimeWeekly += Long.parseLong(timeDataStr);
                }
            }

            for(DataSnapshot timeDataSnapshot : dataSnapshot.child("Data").child("exercise").getChildren()){
                if (timeDataSnapshot.getKey() == null) continue;
                if (aWeekDateList.contains(timeDataSnapshot.getKey())){
                    String timeDataStr = timeDataSnapshot.getValue(String.class);
                    exerciseTimeWeekly += Long.parseLong(timeDataStr);
                }
            }

            for(DataSnapshot timeDataSnapshot : dataSnapshot.child("Data").child("book").getChildren()){
                if (timeDataSnapshot.getKey() == null) continue;
                if (aWeekDateList.contains(timeDataSnapshot.getKey())){
                    String timeDataStr = timeDataSnapshot.getValue(String.class);
                    bookTimeWeekly += Long.parseLong(timeDataStr);
                }
            }

            for(DataSnapshot timeDataSnapshot : dataSnapshot.child("Data").child("sleep").getChildren()){
                if (timeDataSnapshot.getKey() == null) continue;
                if (aWeekDateList.contains(timeDataSnapshot.getKey())){
                    String timeDataStr = timeDataSnapshot.getValue(String.class);
                    sleepTimeWeekly += Long.parseLong(timeDataStr);
                }
            }

            study_weekly.put(friendName, studyTimeWeekly);
            exercise_weekly.put(friendName, exerciseTimeWeekly);
            read_weekly.put(friendName, bookTimeWeekly);
            sleep_weekly.put(friendName, sleepTimeWeekly);

            study_weekly_friends.put(friendName, studyTimeWeekly);
            exercise_weekly_friends.put(friendName, exerciseTimeWeekly);
            read_weekly_friends.put(friendName, bookTimeWeekly);
            sleep_weekly_friends.put(friendName, sleepTimeWeekly);
        }


        exercise_weekly_friends = sortByValue(exercise_weekly_friends);
        read_weekly_friends = sortByValue(read_weekly_friends);
        study_weekly_friends = sortByValue(study_weekly_friends);
        sleep_weekly_friends = sortByValue(sleep_weekly_friends);
    }

    private void saveFriendsDailyDataCallback(DataSnapshot snapshot) {
        setCurrentDate();
        for (DataSnapshot dataSnapshot : snapshot.getChildren()) { //유저마다
            String friendUID = dataSnapshot.getKey();
            String friendName = snapshot.child(friendUID).child("name").getValue(String.class);
            Long studyTimeDaily = 0L;
            Long exerciseTimeDaily = 0L;
            Long bookTimeDaily = 0L;
            Long sleepTimeDaily = 0L;

            String[] categoryList = new String[]{"study", "exercise", "book", "sleep"};
            for (String category : categoryList) {
                if (dataSnapshot.child("Data").child(category).child(currentDate).getValue(String.class) == null)
                    continue;
                String timeDataStr = dataSnapshot.child("Data").child(category).child(currentDate).getValue(String.class);

                if (category.equals("study")) studyTimeDaily += Long.parseLong(timeDataStr);
                else if (category.equals("exercise"))
                    exerciseTimeDaily += Long.parseLong(timeDataStr);
                else if (category.equals("book")) bookTimeDaily += Long.parseLong(timeDataStr);
                else if (category.equals("sleep")) sleepTimeDaily += Long.parseLong(timeDataStr);
            }

            study_daily.put(friendName, studyTimeDaily);
            exercise_daily.put(friendName, exerciseTimeDaily);
            read_daily.put(friendName, bookTimeDaily);
            sleep_daily.put(friendName, sleepTimeDaily);

            study_daily_friends.put(friendName, studyTimeDaily);
            exercise_daily_friends.put(friendName, exerciseTimeDaily);
            read_daily_friends.put(friendName, bookTimeDaily);
            sleep_daily_friends.put(friendName, sleepTimeDaily);
        }

        study_daily_friends = sortByValue(study_daily_friends);
        read_daily_friends = sortByValue(read_daily_friends);
        exercise_daily_friends = sortByValue(exercise_daily_friends);
        sleep_daily_friends = sortByValue(sleep_daily_friends);
    }


    private void saveMockUserData(DataSnapshot snapshot){
        for(DataSnapshot dataSnapshot : snapshot.getChildren()) { // 유저마다
            String username = (String) dataSnapshot.child("username").getValue();

            Long exercise_daily_time = (Long) dataSnapshot.child("exercise").child("daily").getValue();
            exercise_daily.put(username, exercise_daily_time);

            Long exercise_weekly_time = (Long) dataSnapshot.child("exercise").child("weekly").getValue();
            exercise_weekly.put(username, exercise_weekly_time);

            Long read_daily_time = (Long) dataSnapshot.child("read").child("daily").getValue();
            read_daily.put(username, read_daily_time);

            Long read_weekly_time = (Long) dataSnapshot.child("read").child("weekly").getValue();
            read_weekly.put(username, read_weekly_time);

            Long study_daily_time = (Long) dataSnapshot.child("study").child("daily").getValue();
            study_daily.put(username, study_daily_time);

            Long study_weekly_time = (Long) dataSnapshot.child("study").child("weekly").getValue();
            study_weekly.put(username, study_weekly_time);

            Long sleep_daily_time = (Long) dataSnapshot.child("sleep").child("daily").getValue();
            sleep_daily.put(username, sleep_daily_time);

            Long sleep_weekly_time = (Long) dataSnapshot.child("sleep").child("weekly").getValue();
            sleep_weekly.put(username, sleep_weekly_time);
        }

        exercise_daily = sortByValue(exercise_daily);
        exercise_weekly = sortByValue(exercise_weekly);
        read_daily = sortByValue(read_daily);
        read_weekly = sortByValue(read_weekly);
        study_daily = sortByValue(study_daily);
        study_weekly = sortByValue(study_weekly);
        sleep_daily = sortByValue(sleep_daily);
        sleep_weekly = sortByValue(sleep_weekly);
    }

    private String formatRank(int rank){
        String rankStr = String.valueOf(rank);
        String ret;
        if(rank < 10){
            ret = "  " + rankStr;
        }else{
            ret = rankStr;
        }
        return ret + ".";
    }

    private String formatNumber(Long num){
        String ret;
        int numInt = num.intValue();
        if (numInt < 10){
            ret  = "0" + String.valueOf(numInt);
        }else{
            ret = String.valueOf(numInt);
        }
        return ret;
    }

    private String formatTime(Long time){
        Long hour = time/3600;
        time -= hour*3600;
        Long minute = time/60;
        time -= minute*60;
        Long second = time;

        String hourStr = formatNumber(hour);
        String minuteStr = formatNumber(minute);
        String secondStr = formatNumber(second);

        return hourStr+":"+minuteStr+":"+secondStr;

    }

    public void resetStatus(HashMap<String, Boolean> map){
        for (String key : map.keySet()){
            if (map.get(key) == Boolean.TRUE){
                map.put(key, Boolean.FALSE);
            }
        }
    }

    public void constructWeeklyRanking(HashMap<String, Long> dataMap){
        rankingUsernameMapWeekly.clear();
        Iterator<String> keys = dataMap.keySet().iterator();
        int rank = 0;
        while(keys.hasNext()){
            rank++;
            String username = keys.next();
            rankingUsernameMapWeekly.put(username, rank);
            Long time = dataMap.get(username);

            LinearLayout weeklyRankingEachLayout = new LinearLayout(this);
            weeklyRankingEachLayout.setOrientation(LinearLayout.HORIZONTAL);
            LinearLayout.LayoutParams weeklyRankingEachLp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    110
            );
            weeklyRankingEachLayout.setOrientation(LinearLayout.HORIZONTAL);
            weeklyRankingEachLayout.setGravity(Gravity.CENTER_VERTICAL);
            weeklyRankingEachLayout.setBackground(this.getResources().getDrawable(R.drawable.border_bottom));

            // Rank TextView
            TextView rankTv = new TextView(this);
            LinearLayout.LayoutParams rankTvLp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.MATCH_PARENT
            );
            rankTv.setText(formatRank(rank));
            rankTv.setGravity(Gravity.CENTER_VERTICAL);
            rankTv.setPadding(36,0, 0,0);
            rankTv.setTextSize(18);
            rankTv.setTextColor(Color.rgb(0, 0, 0));

            // Name TextView
            TextView nameTv = new TextView(this);
            LinearLayout.LayoutParams nameTvLp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.MATCH_PARENT
            );
            nameTv.setText(username);
            nameTv.setGravity(Gravity.CENTER_VERTICAL);
            nameTv.setPadding(36, 0, 0, 0);
            nameTv.setTextSize(18);
            nameTv.setTextColor(Color.rgb(0, 0, 0));

            // Empty Button
            Button emptyBtn = new Button(this);
            LinearLayout.LayoutParams emptyBtnLp = new LinearLayout.LayoutParams(
                    0,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    1
            );
            emptyBtn.setBackground(this.getResources().getDrawable(R.drawable.back_tranparent));


            // Time TextView
            TextView timeTv = new TextView(this);
            LinearLayout.LayoutParams timeTvLp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.MATCH_PARENT
            );
            timeTv.setText(formatTime(time));
            timeTv.setGravity(Gravity.CENTER_VERTICAL);
            timeTv.setPadding(0,0,0,0);
            timeTv.setTextSize(18);
            timeTv.setTextColor(Color.rgb(0, 0, 0));
            timeTvLp.setMargins(0, 0, 30, 0);


            weeklyRankingEachLayout.addView(rankTv, rankTvLp);
            weeklyRankingEachLayout.addView(nameTv, nameTvLp);
            weeklyRankingEachLayout.addView(emptyBtn, emptyBtnLp);
            weeklyRankingEachLayout.addView(timeTv, timeTvLp);

            weeklyRankingLayout.addView(weeklyRankingEachLayout, weeklyRankingEachLp);
        }


        // 상단 내 정보
        realUsersDatabase.child(currentUser.getUid()).child("name").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String currentUsername = dataSnapshot.getValue(String.class);
                myWeeklyRanking.setText(rankingUsernameMapWeekly.get(currentUsername) + ".");
                myWeeklyUsername.setText(currentUsername);
                myWeeklyTime.setText(formatTime(dataMap.get(currentUsername)));
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Log.e("MainActivity", String.valueOf(databaseError.toException())); // 에러문 출력
            }
        });


        // 뭔가 너무 붙어있어서 사이에 빈줄 넣음
        LinearLayout weeklyRankingEachLayout = new LinearLayout(this);
        weeklyRankingEachLayout.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams weeklyRankingEachLp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                110
        );
        weeklyRankingEachLayout.setOrientation(LinearLayout.HORIZONTAL);
        weeklyRankingLayout.addView(weeklyRankingEachLayout, weeklyRankingEachLp);
    }

    public void constructDailyRanking(HashMap<String, Long> dataMap){
        rankingUsernameMapDaily.clear();
        Iterator<String> keys = dataMap.keySet().iterator();
        int rank = 0;
        while(keys.hasNext()){
            rank++;
            String username = keys.next();
            rankingUsernameMapDaily.put(username, rank);
            Long time = dataMap.get(username);

            LinearLayout dailyRankingEachLayout = new LinearLayout(this);
            dailyRankingEachLayout.setOrientation(LinearLayout.HORIZONTAL);
            LinearLayout.LayoutParams dailyRankingEachLp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    110
            );
            dailyRankingEachLayout.setOrientation(LinearLayout.HORIZONTAL);
            dailyRankingEachLayout.setGravity(Gravity.CENTER_VERTICAL);
            dailyRankingEachLayout.setBackground(this.getResources().getDrawable(R.drawable.border_bottom));

            // Rank TextView
            TextView rankTv = new TextView(this);
            LinearLayout.LayoutParams rankTvLp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.MATCH_PARENT
            );
            rankTv.setText(formatRank(rank));
            rankTv.setGravity(Gravity.CENTER_VERTICAL);
            rankTv.setPadding(36,0, 0,0);
            rankTv.setTextSize(18);
            rankTv.setTextColor(Color.rgb(0, 0, 0));

            // Name TextView
            TextView nameTv = new TextView(this);
            LinearLayout.LayoutParams nameTvLp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.MATCH_PARENT
            );
            nameTv.setText(username);
            nameTv.setGravity(Gravity.CENTER_VERTICAL);
            nameTv.setPadding(36, 0, 0, 0);
            nameTv.setTextSize(18);
            nameTv.setTextColor(Color.rgb(0, 0, 0));

            // Empty Button
            Button emptyBtn = new Button(this);
            LinearLayout.LayoutParams emptyBtnLp = new LinearLayout.LayoutParams(
                    0,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    1
            );
            emptyBtn.setBackground(this.getResources().getDrawable(R.drawable.back_tranparent));

            // Time TextView
            TextView timeTv = new TextView(this);
            LinearLayout.LayoutParams timeTvLp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.MATCH_PARENT
            );
            timeTv.setText(formatTime(time));
            timeTv.setGravity(Gravity.CENTER_VERTICAL);
            timeTv.setPadding(0,0,0,0);
            timeTv.setTextSize(18);
            timeTv.setTextColor(Color.rgb(0, 0, 0));
            timeTvLp.setMargins(0, 0, 30, 0);


            dailyRankingEachLayout.addView(rankTv, rankTvLp);
            dailyRankingEachLayout.addView(nameTv, nameTvLp);
            dailyRankingEachLayout.addView(emptyBtn, emptyBtnLp);
            dailyRankingEachLayout.addView(timeTv, timeTvLp);

            dailyRankingLayout.addView(dailyRankingEachLayout, dailyRankingEachLp);
        }

        // 상단 내 정보
        realUsersDatabase.child(currentUser.getUid()).child("name").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String currentUsername = dataSnapshot.getValue(String.class);
                myDailyRanking.setText(rankingUsernameMapDaily.get(currentUsername) + ".");
                myDailyUsername.setText(currentUsername);
                myDailyTime.setText(formatTime(dataMap.get(currentUsername)));
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Log.e("MainActivity", String.valueOf(databaseError.toException())); // 에러문 출력
            }
        });


        // 친구추가 버튼때문에 마지막 두개 순위가 안보여서 아래 두칸 빈거 집어넣기
        for (int i=0; i<2; i++){
            LinearLayout dailyRankingEachLayout = new LinearLayout(this);
            dailyRankingEachLayout.setOrientation(LinearLayout.HORIZONTAL);
            LinearLayout.LayoutParams dailyRankingEachLp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    110
            );
            dailyRankingEachLayout.setOrientation(LinearLayout.HORIZONTAL);
            dailyRankingLayout.addView(dailyRankingEachLayout, dailyRankingEachLp);
        }
    }


    public void setVariablesInFriendDialog(Button addFriendBtn, EditText friendEmailInput){
        this.addFriendBtn = addFriendBtn;
        this.friendEmailInput = friendEmailInput;
        this.addFriendBtn.setOnClickListener(addFriendListenerClass);
    }

    public void constructTitle(String category, String range){
        if(category.equals("STUDY") && range.equals("ALL")){
            weeklyRankingTitle.setText(R.string.weekly_title_study_all);
            dailyRankingTitle.setText(R.string.daily_title_study_all);
        }
        else if(category.equals("EXERCISE") && range.equals("ALL")){
            weeklyRankingTitle.setText(R.string.weekly_title_exercise_all);
            dailyRankingTitle.setText(R.string.daily_title_exercise_all);
        }
        else if(category.equals("READ") && range.equals("ALL")){
            weeklyRankingTitle.setText(R.string.weekly_title_read_all);
            dailyRankingTitle.setText(R.string.daily_title_read_all);
        }
        else if (category.equals("SLEEP") && range.equals("ALL")){
            weeklyRankingTitle.setText(R.string.weekly_title_sleep_all);
            dailyRankingTitle.setText(R.string.daily_title_sleep_all);
        }

        else if(category.equals("STUDY") && range.equals("FRIENDS")){
            weeklyRankingTitle.setText(R.string.weekly_title_study_friends);
            dailyRankingTitle.setText(R.string.daily_title_study_friends);
        }
        else if(category.equals("EXERCISE") && range.equals("FRIENDS")){
            weeklyRankingTitle.setText(R.string.weekly_title_exercise_friends);
            dailyRankingTitle.setText(R.string.daily_title_exercise_friends);
        }
        else if(category.equals("READ") && range.equals("FRIENDS")){
            weeklyRankingTitle.setText(R.string.weekly_title_read_friends);
            dailyRankingTitle.setText(R.string.daily_title_read_friends);
        }
        else if (category.equals("SLEEP") && range.equals("FRIENDS")){
            weeklyRankingTitle.setText(R.string.weekly_title_sleep_friends);
            dailyRankingTitle.setText(R.string.daily_title_sleep_friends);
        }
    }

    public void constructRanking(){
        if(category.get("STUDY") == true && range.get("ALL") == true){
            constructWeeklyRanking(study_weekly);
            constructDailyRanking(study_daily);
            constructTitle("STUDY", "ALL");
        }
        else if(category.get("EXERCISE") == true && range.get("ALL") == true){
            constructWeeklyRanking(exercise_weekly);
            constructDailyRanking(exercise_daily);
            constructTitle("EXERCISE", "ALL");
        }
        else if(category.get("READ") == true  && range.get("ALL") == true){
            constructWeeklyRanking(read_weekly);
            constructDailyRanking(read_daily);
            constructTitle("READ", "ALL");
        }
        else if(category.get("SLEEP") == true  && range.get("ALL") == true){
            constructWeeklyRanking(sleep_weekly);
            constructDailyRanking(sleep_daily);
            constructTitle("SLEEP", "ALL");
        }

        else if(category.get("STUDY") == true && range.get("FRIENDS") == true){
            constructWeeklyRanking(study_weekly_friends);
            constructDailyRanking(study_daily_friends);
            constructTitle("STUDY", "FRIENDS");
        }
        else if(category.get("EXERCISE") == true && range.get("FRIENDS") == true){
            constructWeeklyRanking(exercise_weekly_friends);
            constructDailyRanking(exercise_daily_friends);
            constructTitle("EXERCISE", "FRIENDS");
        }
        else if(category.get("READ") == true  && range.get("FRIENDS") == true){
            constructWeeklyRanking(read_weekly_friends);
            constructDailyRanking(read_daily_friends);
            constructTitle("READ", "FRIENDS");
        }
        else if(category.get("SLEEP") == true  && range.get("FRIENDS") == true){
            constructWeeklyRanking(sleep_weekly_friends);
            constructDailyRanking(sleep_daily_friends);
            constructTitle("SLEEP", "FRIENDS");
        }
    }

    public void updateActivated(){
//        categoryStudy.setBackgroundTintList(getResources().getColorStateList(R.color.custom_white));
//        categoryExercise.setBackgroundTintList(getResources().getColorStateList(R.color.custom_white));
//        categoryRead.setBackgroundTintList(getResources().getColorStateList(R.color.custom_white));
//        categorySleep.setBackgroundTintList(getResources().getColorStateList(R.color.custom_white));
//        rangeAll.setBackgroundTintList(getResources().getColorStateList(R.color.custom_white));
//        rangeFriends.setBackgroundTintList(getResources().getColorStateList(R.color.custom_white));

        // HashMap인 category, range를 순회하면서 true인 것을 activated 활성화
        Set<String> keysCategory = category.keySet();
        Iterator<String> itCategory = keysCategory.iterator();
        while(itCategory.hasNext()){
            String key = itCategory.next();
            if(category.get(key) == true){
                // 그 버튼의 backgroundTint를 purple로 설정
                if (key.equals("STUDY")){
                    categoryStudy.setBackgroundTintList(getResources().getColorStateList(R.color.custom_purple));
                    break;
                }else if(key.equals("EXERCISE")){
                    categoryExercise.setBackgroundTintList(getResources().getColorStateList(R.color.custom_purple));
                    break;
                }else if(key.equals("READ")){
                    categoryRead.setBackgroundTintList(getResources().getColorStateList(R.color.custom_purple));
                    break;
                }else if(key.equals("SLEEP")){
                    categorySleep.setBackgroundTintList(getResources().getColorStateList(R.color.custom_purple));
                    break;
                }
            }
        }

        Set<String> keysRange = range.keySet();
        Iterator<String> itRange = keysRange.iterator();
        while(itRange.hasNext()){
            String key = itRange.next();
            if(range.get(key) == true){
                // 그 버튼의 backgroundTint를 purple로 설정
                if(key.equals("ALL")){
                    rangeAll.setBackgroundTintList(getResources().getColorStateList(R.color.custom_purple));
                    break;
                }else if(key.equals("FRIENDS")){
                    rangeFriends.setBackgroundTintList(getResources().getColorStateList(R.color.custom_purple));
                    break;
                }
            }
        }
    }

    public void resetActivated(){
        // HashMap인 category, range를 순회하면서 true인 것을 activated 비활성화
        Set<String> keysCategory = category.keySet();
        Iterator<String> itCategory = keysCategory.iterator();
        while(itCategory.hasNext()){
            String key = itCategory.next();
            if(category.get(key) == true){
                // 그 버튼의 backgroundTint를 purple로 설정
                if (key.equals("STUDY")){
                    categoryStudy.setBackgroundTintList(null);
                    break;
                }else if(key.equals("EXERCISE")){
                    categoryExercise.setBackgroundTintList(null);
                    break;
                }else if(key.equals("READ")){
                    categoryRead.setBackgroundTintList(null);
                    break;
                }else if(key.equals("SLEEP")){
                    categorySleep.setBackgroundTintList(null);
                    break;
                }
            }
        }

        Set<String> keysRange = range.keySet();
        Iterator<String> itRange = keysRange.iterator();
        while(itRange.hasNext()){
            String key = itRange.next();
            if(range.get(key) == true){
                // 그 버튼의 backgroundTint를 purple로 설정
                if(key.equals("ALL")){
                    rangeAll.setBackgroundTintList(null);
                    break;
                }else if(key.equals("FRIENDS")){
                    rangeFriends.setBackgroundTintList(null);
                    break;
                }
            }
        }
    }

    public void manageAddFriendBtn(){
        if (range.get("FRIENDS") == true){
            showAddFriendDialogButton.setVisibility(View.VISIBLE);
        }else{
            showAddFriendDialogButton.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onClick(View view){
        resetActivated();
        if(view == categoryStudy || view == categoryExercise || view == categoryRead || view == categorySleep){
            resetStatus(category);
        }

        if(view == rangeAll || view == rangeFriends){
            resetStatus(range);
        }

        if(view == categoryStudy){
            category.put("STUDY", true);
        }else if(view == categoryExercise){
            category.put("EXERCISE", true);
        }else if(view == categoryRead){
            category.put("READ", true);
        }else if(view == categorySleep){
            category.put("SLEEP", true);
        }else if(view == rangeAll){
            range.put("ALL", true);
        }else if(view == rangeFriends){
            range.put("FRIENDS", true);
        }

        weeklyRankingLayout.removeAllViews();
        dailyRankingLayout.removeAllViews();
        updateActivated();
        constructRanking();
        manageAddFriendBtn();
    }


    class ShowFriendDialogListenerClass implements View.OnClickListener{
        public void onClick(View v){
            AlertDialog.Builder builder = new AlertDialog.Builder(RankingActivity.this);
            LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
            View showAddFriendDialogButtonView = inflater.inflate(R.layout.friend_dialog_layout, null);
            builder.setView(showAddFriendDialogButtonView);

            addFriendDialog = builder.create();
            addFriendDialog.show();

            setVariablesInFriendDialog(addFriendDialog.findViewById(R.id.add_friend_btn), addFriendDialog.findViewById(R.id.friend_email_input));
        }
    }

    class AddFriendListenerClass implements View.OnClickListener{
        public void onClick(View v){
            String friendEmail = friendEmailInput.getText().toString();

            realUsersDatabase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String friendUID = "";
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                        if (dataSnapshot.child("emailId").getValue(String.class).equals(friendEmail)){
                            friendUID = dataSnapshot.getKey();
                            break;
                        }
                    }

                    if (!friendUID.equals("")){
                        realUsersDatabase.child(currentUser.getUid()).child("friends").child(friendUID).setValue(true);
                        realUsersDatabase.child(friendUID).child("friends").child(currentUser.getUid()).setValue(true);
                        Toast.makeText(RankingActivity.this, "친구가 추가되었습니다.", Toast.LENGTH_SHORT).show();
                        friendList.add(friendUID);
                    }else{
                        Toast.makeText(RankingActivity.this, "해당 사용자가 존재하지 않습니다.", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
        }
    }

    public void calculateFriendSize(){
        realUsersDatabase.child(currentUser.getUid()).child("friends").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                friendSize = snapshot.getChildrenCount();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void initSelectCategory(){
        categoryStudy.setBackgroundTintList(getResources().getColorStateList(R.color.custom_purple));
        rangeAll.setBackgroundTintList(getResources().getColorStateList(R.color.custom_purple));
    }

    public void setActivityUsername(){
        realUsersDatabase.child(currentUser.getUid()).child("name").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String currentUsername = dataSnapshot.getValue(String.class);
                usernameTv.setText(currentUsername);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Log.e("MainActivity", String.valueOf(databaseError.toException())); // 에러문 출력
            }
        });
    }


    public void setMockActivityRanking(){
        mockUsersDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                saveMockUserData(snapshot);
                constructWeeklyRanking(study_weekly);
                constructDailyRanking(study_daily);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                System.out.println("데이터를 불러오지 못함요...");
            }
        });
    }

    public void saveFriendsWeeklyData(){
        realUsersDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                saveFriendsWeeklyDataCallback(snapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void saveFriendsDailyData(){
        realUsersDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                saveFriendsDailyDataCallback(snapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ranking);

        mFirebaseAuth = FirebaseAuth.getInstance();
        currentUser = mFirebaseAuth.getCurrentUser();

        usernameTv = findViewById(R.id.ranking_username);

        weeklyRankingLayout = findViewById(R.id.weekly_ranking_layout);
        dailyRankingLayout = findViewById(R.id.daily_ranking_layout);

        mDatabase = FirebaseDatabase.getInstance("https://godlife-a1b20-default-rtdb.asia-southeast1.firebasedatabase.app").getReference();
        mockUsersDatabase = mDatabase.child("mock_users");
        realUsersDatabase = mDatabase.child("godlife").child("UserAccount");

        setCurrentDate();
        setOneWeekAgoDate();
        setActivityUsername();
        calculateFriendSize();
        setFriendList();
        saveFriendsWeeklyData();
        saveFriendsDailyData();
        setMockActivityRanking();

        categoryStudy = findViewById(R.id.category_study);
        categoryExercise = findViewById(R.id.category_exercise);
        categoryRead = findViewById(R.id.category_read);
        categorySleep = findViewById(R.id.category_sleep);
        rangeAll = findViewById(R.id.category_all);
        rangeFriends = findViewById(R.id.category_friends);

        categoryStudy.setOnClickListener(this);
        categoryExercise.setOnClickListener(this);
        categoryRead.setOnClickListener(this);
        categorySleep.setOnClickListener(this);
        rangeAll.setOnClickListener(this);
        rangeFriends.setOnClickListener(this);

        weeklyRankingTitle = findViewById(R.id.weekly_ranking_title);
        dailyRankingTitle = findViewById(R.id.daily_ranking_title);

        myWeeklyRanking = findViewById(R.id.my_weekly_ranking);
        myWeeklyUsername = findViewById(R.id.my_weekly_username);
        myWeeklyTime = findViewById(R.id.my_weekly_time);
        myDailyRanking = findViewById(R.id.my_daily_ranking);
        myDailyUsername = findViewById(R.id.my_daily_username);
        myDailyTime = findViewById(R.id.my_weekly_time);

        showAddFriendDialogButton = findViewById(R.id.show_add_friend_dialog_btn);
        showFriendDialogListener = new ShowFriendDialogListenerClass();
        showAddFriendDialogButton.setOnClickListener(showFriendDialogListener);

        addFriendListenerClass = new AddFriendListenerClass();

        initSelectCategory();
    }
}