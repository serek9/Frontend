package com.example.dam.serflix.Controller.Activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dam.serflix.Controller.Managers.RegisterCallback;
import com.example.dam.serflix.Controller.Managers.RegisterManager;
import com.example.dam.serflix.Controller.Managers.UserLoginManager;
import com.example.dam.serflix.Controller.Managers.LoginCallback;
import com.example.dam.serflix.Model.UserDTO;
import com.example.dam.serflix.Model.UserToken;
import com.example.dam.serflix.R;

import static com.huxq17.swipecardsview.R.id.text;

public class SignUpActivity extends AppCompatActivity implements RegisterCallback, LoginCallback {

    private EditText username;
    private EditText mail;
    private EditText pass;
    private String passC;
    private EditText pass2;
    private UserDTO userDTO;
    private String coord;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        username = (EditText) findViewById(R.id.editName);
        mail = (EditText) findViewById(R.id.editMail);
        pass = (EditText) findViewById(R.id.editPass);
        pass2 = (EditText) findViewById(R.id.editRPass);

        username.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String aux = s.toString();
                if(aux.length()<8){
                    username.setError("El nombre es muy corto");
                }
            }
        });

        mail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String aux = s.toString();
                if(!Patterns.EMAIL_ADDRESS.matcher(aux).matches()){
                    mail.setError("Mail incorrecto.");
                }
            }
        });


        pass.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String aux = s.toString();
                passC = aux;
                if(aux.length()<8){
                    pass.setError("La contraseña es muy corta");
                }
            }
        });

        pass2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String aux = s.toString();
                if(!aux.equals(passC)){
                    pass2.setError("La contraseña no coincide");
                }
            }
        });

        Bundle extras = getIntent().getExtras();
        final String latlon = extras.getString("latlon");
        coord = latlon;


        Button registerButton = (Button) findViewById(R.id.signBtn);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptRegister(view);
            }
        });

        pass2.setImeActionLabel("Pass?", KeyEvent.KEYCODE_ENTER);
/*        pass.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_NULL || (keyEvent != null && (keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (id == EditorInfo.IME_ACTION_DONE)) {
                    attemptRegister(textView);
                    return true;
                }
                return false;
            }
        });
*/


    }

    private void attemptRegister(View v){
        username.setError(null);
        pass.setError(null);
        pass2.setError(null);
        mail.setError(null);

        userDTO = new UserDTO();

        String usernameDTO = username.getText().toString().toLowerCase();
        System.out.println(usernameDTO);
        String mailDTO = mail.getText().toString();
        System.out.println(mailDTO);
        String passDTO = pass.getText().toString();
        System.out.println(passDTO);
        String pass2DTO = pass2.getText().toString();
        System.out.println(pass2DTO);

        boolean cancel = false;
        View focusView = null;

        // Check for a valid baskets, if the user entered one.
        if (TextUtils.isEmpty(usernameDTO)) {
            username.setError(getString(R.string.error_field_required));
            focusView = username;
            cancel = true;
        }
        if (TextUtils.isEmpty(mailDTO)) {
            mail.setError(getString(R.string.error_field_required));
            focusView = mail;
            cancel = true;
        }
        if(passDTO.length() < 5){
            pass.setError("4 chars!");
            focusView = pass;
            cancel = true;
        }
        if (!passDTO.equals(pass2DTO)) {
            pass.setError("Incorrect.");
            focusView = pass;
            cancel = true;
        }

        userDTO.setLogin(usernameDTO);
        userDTO.setEmail(mailDTO);
        userDTO.setPassword(passDTO);

        if (cancel) {
            focusView.requestFocus();
        } else {
            showProgress(true);
            RegisterManager.getInstance(v.getContext()).registerAccount(SignUpActivity.this, userDTO);
        }
    }

    @Override
    public void onSuccessRegister() {
        UserLoginManager.getInstance().performLogin(userDTO.getLogin(), userDTO.getPassword(), SignUpActivity.this);

        ProgressDialog progressDialog = new ProgressDialog(SignUpActivity.this);
        progressDialog.setTitle("Loading");
        progressDialog.setMessage("Your account has been created, login in...");
        progressDialog.show();
    }

    @Override
    public void onFailureRegister(Throwable t) {
        showProgress(false);
    }

    @Override
    public void onSuccess(UserToken userToken) {

        Intent intent = new Intent(SignUpActivity.this, TestActivity.class);
        intent.putExtra("latlon", coord);
        startActivity(intent);
        finish();
    }


    @Override
    public void onFailure(Throwable t) {
        Log.e("LoginActivity->", "performLogin->onFailure ERROR " + t.getMessage());
        Toast.makeText(this, "Credenciales incorrectos", Toast.LENGTH_LONG).show();
    }

    /**
     * Shows the progress UI and hides the Add form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner -- ULTRAPASS.
    }
}
