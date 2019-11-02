package com.example.appnews.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.appnews.CheckInternetConnection;
import com.example.appnews.R;
import com.example.appnews.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import es.dmoral.toasty.Toasty;

public class SignUp extends AppCompatActivity {
    private TextInputLayout InputPhone, InputName, Inputpassword;
    private EditText txtPhone, txtName, txtPassword;
    private DatabaseReference table_user;
    FirebaseAuth auth;
    ProgressDialog dialog;
    CheckInternetConnection connection;
    RelativeLayout relativeLayout;
    int lenghtPhone = 10;
    int lengthPass =1;
    int lengthName =1;
    Button btn_Register;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_signup);
        AddEvent();
        connection = new CheckInternetConnection();
    }

    private void AddEvent() {
        btn_Register = (Button)findViewById(R.id.btn_register);
        relativeLayout = (RelativeLayout) findViewById(R.id.relative);
        txtPhone = (EditText) findViewById(R.id.editPhone);
        txtName = (EditText) findViewById(R.id.editName);
        txtPassword = (EditText) findViewById(R.id.editPass);
        InputPhone = (TextInputLayout) findViewById(R.id.phone_input_signUp);
        InputName = (TextInputLayout) findViewById(R.id.Name_input_signUp);
        Inputpassword = (TextInputLayout) findViewById(R.id.Pass_input_signUp);
        auth = FirebaseAuth.getInstance();
        dialog = new ProgressDialog(this);

        //Init firebase
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        table_user = database.getReference("User");

        txtPhone.addTextChangedListener(new SignUp.MyTextWatcher(InputPhone));
        txtName.addTextChangedListener(new SignUp.MyTextWatcher(InputName));
        txtPassword.addTextChangedListener(new SignUp.MyTextWatcher(Inputpassword));
    }

    private boolean ValidatePhone() {
        String phone = txtPhone.getText().toString().trim();

        if (phone.isEmpty()) {
            InputPhone.setError(getString(R.string.err_msg_phone));
            return false;
        }
        if (!isValidPhone(phone) || phone.length() > 10) {
            InputPhone.setError(getString(R.string.err_msg_val_phone));
            requestFocus(InputPhone);
            return false;
        } else {
            InputPhone.setErrorEnabled(false);
        }

        return true;
    }

    private boolean ValidateName() {
        String name = txtName.getText().toString().trim();

        if (name.isEmpty()) {
            InputName.setError(getString(R.string.err_msg_name));
            return false;
        }
        if (name.length() > 30) {
            InputName.setError(getString(R.string.err_msg_val_name));
            requestFocus(InputName);
            return false;
        } else {
            InputName.setErrorEnabled(false);
        }

        return true;
    }

    private boolean ValidatePassword() {
        String password = txtPassword.getText().toString().trim();
        if (password.isEmpty()) {
            Inputpassword.setError(getString(R.string.err_msg_password));
            requestFocus(Inputpassword);
            return false;
        } else {
            Inputpassword.setErrorEnabled(false);
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
    private void submitForm() {
        if (connection.isConnectedToNetwork(getBaseContext())) {
            dialog.setMessage("Đang đăng ký. Vui lòng chờ...");
            dialog.show();
            if (!ValidatePhone()) {
                dialog.dismiss();
                return;
            }
            if (!ValidateName()) {
                dialog.dismiss();
                return;
            }
            if (!ValidatePassword()) {
                dialog.dismiss();
                return;
            }
            final String phone = txtPhone.getText().toString();
            final String name = txtName.getText().toString();
            final String pass = txtPassword.getText().toString();

            table_user.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (!dataSnapshot.child(phone).exists())
                    {
                        User user = new User(name, pass);
                        table_user.child(phone).setValue(user);
                        Toasty.success(getApplicationContext(), "Đăng ký thành công", Toast.LENGTH_SHORT, true).show();
                        finish();
                    }
                    else {
                        dialog.dismiss();
                        Toasty.error(getApplicationContext(), "Số điện thoại đã đăng ký", Toast.LENGTH_SHORT, true).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        } else {
            dialog.dismiss();
            final Snackbar snackbar = Snackbar
                    .make(relativeLayout, "Mất kết nối internet!!!", Snackbar.LENGTH_LONG)
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

    public void SignUp(View v){
            submitForm();
    }

    private class MyTextWatcher implements TextWatcher {

        private View view;

        private MyTextWatcher(View view) {
            this.view = view;
        }

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable  editable) {
            switch (view.getId()) {
                case R.id.phone_input_signUp:
                    ValidatePhone();
                    listenerChangeLengthChar();
                    break;
                case R.id.Name_input_signUp:
                    ValidateName();
                    listenerChangeLengthChar();
                    break;
                case R.id.Pass_input_signUp:
                    ValidatePassword();
                    listenerChangeLengthChar();
                    break;
            }
        }

    }

    private void listenerChangeLengthChar() {
        if (txtPhone.getText().length() >= lenghtPhone && txtName.getText().length() >= lengthName && txtPassword.getText().length() >= lengthPass ) {
            btn_Register.setAlpha(1f);
            btn_Register.setEnabled(true);
        } else {
            btn_Register.setAlpha(0.4f);
            btn_Register.setEnabled(false);
        }
    }

    public void Back(View v) {
        finish();
    }
}
