package com.example.udacity.surfconnect;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.accountkit.AccessToken;
import com.facebook.accountkit.AccountKit;
import com.facebook.accountkit.AccountKitLoginResult;
import com.facebook.accountkit.ui.AccountKitActivity;
import com.facebook.accountkit.ui.AccountKitConfiguration;
import com.facebook.accountkit.ui.LoginType;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

public class LoginActivity extends AppCompatActivity {

    public static final int APP_REQUEST_CODE = 1;
    LoginButton fbLoginButton;
    CallbackManager fbCallbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        FontHelper.setCustomTypeface(findViewById(R.id.view_root));

        fbLoginButton = (LoginButton) findViewById(R.id.facebook_login_button);
        fbLoginButton.setReadPermissions("email");

        // Login Button callback registration
        fbCallbackManager = CallbackManager.Factory.create();
        fbLoginButton.registerCallback(fbCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                launchAccountActivity();
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {
                // Display error
                Toast.makeText(LoginActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

        // Check if Access Token already exists
        AccessToken akAccessToken = AccountKit.getCurrentAccessToken();
        com.facebook.AccessToken fbAccessToken = com.facebook.AccessToken.getCurrentAccessToken();
        if (akAccessToken != null || fbAccessToken != null){
            launchAccountActivity();
        }
    }

    private void onLogIn(final LoginType loginType){
        final Intent intent = new Intent(this, AccountKitActivity.class);

        AccountKitConfiguration.AccountKitConfigurationBuilder accountKitConfigurationBuilder =
                new AccountKitConfiguration.AccountKitConfigurationBuilder(
                        loginType,
                        AccountKitActivity.ResponseType.TOKEN
                );

        final AccountKitConfiguration configuration = accountKitConfigurationBuilder.build();

        intent.putExtra(AccountKitActivity.ACCOUNT_KIT_ACTIVITY_CONFIGURATION, configuration);
        startActivityForResult(intent, APP_REQUEST_CODE);
    }

    public void onPhoneLogIn(View view){

        onLogIn(LoginType.PHONE);
    }

    public void onEmailLogIn(View view){

        onLogIn(LoginType.EMAIL);
    }

    private void launchAccountActivity() {
        Intent intent = new Intent(this, AccountActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == APP_REQUEST_CODE){
            AccountKitLoginResult loginResult = data.getParcelableExtra(AccountKitLoginResult.RESULT_KEY);
            if (loginResult.getError() != null){
                // Display login error
                Toast.makeText(this, loginResult.getError().getErrorType().getMessage(), Toast.LENGTH_LONG).show();
            } else if (loginResult.getAccessToken() != null){
                // On successful login
                launchAccountActivity();
            }
        }

        fbCallbackManager.onActivityResult(requestCode,resultCode,data);
    }
}
