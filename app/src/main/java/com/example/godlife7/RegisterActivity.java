package com.example.godlife7;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {

    private FirebaseAuth mFirebaseAuth; //파이어베이스 인증
    private DatabaseReference mdatabaseRef; //실시간 데이터베이스
    private EditText mEtEmail, mEtPwd, mEtName,mEtPwdCheck;
    private Button mBtnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_register);

        mFirebaseAuth = FirebaseAuth.getInstance();
        mdatabaseRef = FirebaseDatabase.getInstance().getReference( "godlife" );

        mEtEmail = findViewById(R.id.et_email);
        mEtPwd = findViewById(R.id.et_pwd);
        mEtPwdCheck = findViewById(R.id.et_pwdcheck);
        mEtName  = findViewById(R.id.et_name);
        mBtnRegister = findViewById(R.id.btn_register);

        mBtnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //회원가입 처리시작
                String strEmail = mEtEmail.getText().toString();
                String strPwd = mEtPwd.getText().toString();
                String strPwdCheck = mEtPwdCheck.getText().toString();
                String strName = mEtName.getText().toString();
                //FirebaseAuth  진행
                if(strEmail.equals("")||strName.equals("")||strPwd.equals("")||strPwdCheck.equals("")){
                    Toast.makeText(RegisterActivity.this, "모든 항목을 입력해주세요",Toast.LENGTH_SHORT).show();
                }
                else{
                    mFirebaseAuth.createUserWithEmailAndPassword(strEmail,strPwd).addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if(task.isSuccessful()&& strPwd.equals(strPwdCheck) ){
                                FirebaseUser firebaseUser = mFirebaseAuth.getCurrentUser();
                                UserAccount account = new UserAccount();
                                account.setIdToken(firebaseUser.getUid());
                                account.setEmailId(firebaseUser.getEmail());
                                account.setPassword(strPwd);
                                account.setName(strName);
                                //setValue : db에 삽입메소드
                                mdatabaseRef.child("UserAccount").child(firebaseUser.getUid()).setValue(account);
                                Toast.makeText(RegisterActivity.this, "회원가입에 성공하셨습니다",Toast.LENGTH_SHORT).show();
                            }else {
                                Toast.makeText(RegisterActivity.this, "회원가입에 실패하셨습니다",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });}
            }
        });
        Button btn_to_login = findViewById(R.id.btn_to_login);
        btn_to_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //로그인 화면으로 이동
                Intent intent = new Intent(RegisterActivity.this , LoginActivity.class);
                startActivity(intent);
            }
        });
    }
}