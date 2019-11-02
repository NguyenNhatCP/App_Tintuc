package com.example.appnews.activity;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatCheckBox;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.appnews.MainActivity;
import com.example.appnews.R;
import com.example.appnews.CheckInternetConnection;
import com.example.appnews.model.Common;
import com.example.appnews.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import es.dmoral.toasty.Toasty;
import io.paperdb.Paper;

public class SignIn extends AppCompatActivity {
    private BroadcastReceiver mNetWorkReceiver;
    private TextInputLayout InputLayoutPhone, InputLayoutPassword;
    private EditText InputPhone, Inputpassword;
    private DatabaseReference table_user;
    private Button btnLogin;
    private AppCompatCheckBox ckRemember;
    FirebaseAuth auth;
    ProgressDialog dialog;
    //String language[];
    int lenghtPhone = 10;
    int lenghtPass = 1;
    static LinearLayout linearLayout;
    CheckInternetConnection connection;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_signin);
        AddEvent();
        //Init Paper
        Paper.init(this);
        //Check user was login
        CheckUser();
        mNetWorkReceiver = new CheckInternetConnection();
        registerNetworkBroadcastForNougat();
        connection = new CheckInternetConnection();
    }


    public static void dialog(boolean value)
    {
        if(value)
        {
            //Connect internet
        }
        else
        {
            final Snackbar snackbar = Snackbar
                    .make(linearLayout, "Mất kết nối internet!!!", Snackbar.LENGTH_LONG)
                    .setAction("Thử lại", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                        }
                    });
            snackbar.setActionTextColor(Color.RED);
            View sbView = snackbar.getView();
            TextView textView = (TextView) sbView.findViewById(R.id.snackbar_text);
            textView.setTextColor(Color.YELLOW);
            snackbar.show();
        }

    }
    private void registerNetworkBroadcastForNougat() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            registerReceiver(mNetWorkReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            registerReceiver(mNetWorkReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        }
    }

    protected void unregisterNetworkChanges() {
        try {
            unregisterReceiver(mNetWorkReceiver);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }
    private void CheckUser() {
        //Check user
        String phone = Paper.book().read(Common.Phone_Key);
        String pass = Paper.book().read(Common.PassWord_Key);
        if (phone != null && pass != null) {
            if (!phone.isEmpty() && !pass.isEmpty()) {
                InputPhone.setText(phone);
                Inputpassword.setText(pass);
                login(phone, pass);
            }
        }

    }

    private void login(final String phone, final String pass) {
            dialog.setMessage("Vui lòng chờ...");
            dialog.show();
            table_user.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.child(phone).exists()) {
                        dialog.dismiss();
                        //Get information User
                        User user = dataSnapshot.child(phone).getValue(User.class);
                        if (user.getPassword().equals(pass)) {
                            Intent i = new Intent(getBaseContext(), MainActivity.class);
                            Common.currentUser = user;
                            startActivity(i);
                            finish();
                        }
                    } else {
                        dialog.dismiss();
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
    }


    public void AddEvent() {
        linearLayout = (LinearLayout) findViewById(R.id.linear);
        ckRemember = (AppCompatCheckBox) findViewById(R.id.checkboxRemember);
        InputLayoutPhone = (TextInputLayout) findViewById(R.id.phone_input_editText);
        InputLayoutPassword = (TextInputLayout) findViewById(R.id.password_input_editText);
        InputPhone = (EditText) findViewById(R.id.InputPhone);
        Inputpassword = (EditText) findViewById(R.id.InputPassword);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        auth = FirebaseAuth.getInstance();
        dialog = new ProgressDialog(this);

        //Init firebase
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        table_user = database.getReference("User");

        InputPhone.addTextChangedListener(new MyTextWatcher(InputPhone));
        Inputpassword.addTextChangedListener(new MyTextWatcher(Inputpassword));
    }


    private boolean ValidatePhone() {
        String phone = InputPhone.getText().toString().trim();

        if (phone.isEmpty()) {
            InputLayoutPhone.setError(getString(R.string.err_msg_phone));
            return false;
        }
        if (!isValidPhone(phone) || phone.length() > 10) {
            InputLayoutPhone.setError(getString(R.string.err_msg_val_phone));
            requestFocus(InputPhone);
            return false;
        } else {
            InputLayoutPhone.setErrorEnabled(false);
        }

        return true;
    }

    private boolean ValidatePassword() {
        String password = Inputpassword.getText().toString().trim();
        if (password.isEmpty()) {
            InputLayoutPassword.setError(getString(R.string.err_msg_password));
            requestFocus(Inputpassword);
            return false;
        } else {
            InputLayoutPassword.setErrorEnabled(false);
        }
        return true;
    }

    private static boolean isValidPhone(String phone) {
        return !TextUtils.isEmpty(phone) && Patterns.PHONE.matcher(phone).matches();
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    /**
     * Validating form
     */
    private void submitForm() {
        if(connection.isConnectedToNetwork(getBaseContext())) {
            dialog.setMessage("Đang đăng nhập. Vui lòng chờ...");
            dialog.show();
            if (!ValidatePhone()) {
                return;
            }
            if (!ValidatePassword()) {
                return;
            }
            final String phone = InputPhone.getText().toString();
            final String password = Inputpassword.getText().toString();
            //save user
            if (ckRemember.isChecked()) {
                Paper.book().write(Common.Phone_Key, phone);
                Paper.book().write(Common.PassWord_Key, password);
            }

            table_user.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    //check if user exists in database
                    if (dataSnapshot.child(phone).exists()) {
                        dialog.dismiss();
                        //Get information User
                        User user = dataSnapshot.child(phone).getValue(User.class);
                        if (user.getPassword().equals(password)) {
                            Toasty.success(getApplicationContext(), "Đăng nhập thành công",Toast.LENGTH_SHORT,true).show();
                            Intent i = new Intent(getBaseContext(), MainActivity.class);
                            Common.currentUser = user;
                            startActivity(i);
                            finish();
                        } else {
                            Toasty.error(getApplicationContext(), "Sai mật khẩu",Toast.LENGTH_SHORT,true).show();
                        }
                    } else {
                        dialog.dismiss();
                        Toasty.error(getApplicationContext(), "Số điện thoại không tồn tại", Toast.LENGTH_SHORT,true).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
        else
        {
            dialog.dismiss();
            final Snackbar snackbar = Snackbar
                    .make(linearLayout, "Mất kết nối internet!!!", Snackbar.LENGTH_LONG)
                    .setAction("Thử lại", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                        }
                    });
            snackbar.setActionTextColor(Color.RED);
            View sbView = snackbar.getView();
            TextView textView = (TextView) sbView.findViewById(R.id.snackbar_text);
            textView.setTextColor(Color.YELLOW);
            snackbar.show();
        }
    }

    public void SignIn(View v) {
        submitForm();
    }

    public void Link_SignUpCar(View v) {
        Intent i = new Intent(SignIn.this, SignUp.class);
        startActivity(i);
    }

    public void Back(View v) {
        finish();
        moveTaskToBack(true);
    }

    private class MyTextWatcher implements TextWatcher {

        private View view;

        private MyTextWatcher(View view) {
            this.view = view;
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void afterTextChanged(Editable editable) {
            switch (view.getId()) {
                case R.id.InputPhone:
                    ValidatePhone();
                    listenerChangeLengthChar();
                    break;
                case R.id.InputPassword:
                    ValidatePassword();
                    listenerChangeLengthChar();
                    break;
            }
        }
    }

    private void listenerChangeLengthChar() {
        if (InputPhone.getText().length() >= lenghtPhone && Inputpassword.getText().length() >= lenghtPass) {
            btnLogin.setAlpha(1f);
            btnLogin.setEnabled(true);
        } else {
            btnLogin.setAlpha(0.4f);
            btnLogin.setEnabled(false);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterNetworkChanges();
    }
}

