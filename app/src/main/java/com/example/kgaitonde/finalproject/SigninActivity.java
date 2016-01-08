package com.example.kgaitonde.finalproject;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.parse.LogInCallback;
import com.parse.ParseException;
;
import com.parse.ParseFacebookUtils;
import com.parse.ParseUser;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SigninActivity extends AppCompatActivity {

    private EditText etUsername, etPassword;
    private Button btSignIn, btSignUp;
    private ImageView ivFacebook, ivTwitter;
    CallbackManager callbackManager;
    LoginButton btnFacebookLogin;

    public static final List<String> permissions = new ArrayList<String>(){{
        add("public_profile");
        add("email");
    }};


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode,resultCode,data);
//        btnTwitterLogin.onActivityResult(requestCode,resultCode,data);
    }




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        callbackManager = CallbackManager.Factory.create();
        etUsername = (EditText) findViewById(R.id.editTextUsername);
        etPassword = (EditText) findViewById(R.id.editTextPasswordLogin);
        btSignIn = (Button) findViewById(R.id.buttonSignIn);
        btSignUp = (Button) findViewById(R.id.buttonSignUp);
        btnFacebookLogin= (LoginButton) findViewById(R.id.login_button);
        btnFacebookLogin.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                final AccessToken accessToken = loginResult.getAccessToken();
                ParseFacebookUtils.logInInBackground(accessToken, new LogInCallback() {
                    @Override
                    public void done(ParseUser user, ParseException e) {
                        if (user.isNew()) {
                            Log.d("MyApp", "new Facebook User");
                            GraphRequest request = GraphRequest.newMeRequest(accessToken,
                                    new GraphRequest.GraphJSONObjectCallback() {
                                        @Override
                                        public void onCompleted(JSONObject object, GraphResponse response) {
                                            ParseUser user = ParseUser.getCurrentUser();
                                            try {
                                                //private EditText etFirstName, etLastName, etEmail, etPassword, etConfirmPassword;
                                                //private ToggleButton gender;
                                                user.setEmail(response.getJSONObject().getString("email"));
                                                user.setUsername(response.getJSONObject().getString("email"));
                                                user.setPassword("null");
                                                user.put("firstName", response.getJSONObject().getString("first_name"));
                                                user.put("lastName", response.getJSONObject().getString("last_name"));
                                                user.put("gender", response.getJSONObject().getString("gender"));
                                                user.put("isPrivate", false);
                                                user.put("disablePush",false);
                                                //user.put("profilePic","");
                                            } catch (JSONException e1) {
                                                e1.printStackTrace();
                                            }
                                            user.saveInBackground();
                                            Intent intent = new Intent(SigninActivity.this, MainActivity.class);
                                            intent.putExtra("NEWUSER", true);
                                            //intent.putExtra("UserName",ParseUser.getCurrentUser().getString("FirstName"));
                                            startActivity(intent);
                                            finish();
                                        }
                                    });

                            Bundle parameters = new Bundle();
                            parameters.putString("fields", "id,email,picture,first_name,name,last_name,gender");
                            request.setParameters(parameters);
                            request.executeAsync();
                        } else {
                            Log.d("MyApp", "User loggedIn again with Facebook");
                            Intent intent = new Intent(SigninActivity.this, MainActivity.class);
                            //intent.putExtra("UserName",ParseUser.getCurrentUser().getString("FirstName"));
                            startActivity(intent);
                            //Log.d("currentUser", ParseUser.getCurrentUser().getString("FirstName"));
                           /* Intent intent;
                            intent = new Intent(MainActivity.this, TabViewActivity.class);
                            startActivity(intent);*/
                            finish();
                        }

                    }
                });
            }

            @Override
            public void onCancel() {
                Log.d("MyApp", "User cancelled the Facebook login");
            }

            @Override
            public void onError(FacebookException e) {
                Log.d("MyApp", "There was some error while Facebook login");
            }
        });

        ParseUser currentUser = ParseUser.getCurrentUser();

        if (currentUser != null) {
            Toast.makeText(SigninActivity.this, "Active User Session", Toast.LENGTH_SHORT).show();
            Intent intent=new Intent(SigninActivity.this, MainActivity.class);
            startActivity(intent);
            finish();

        } else {
            btSignIn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Toast.makeText(MainActivity.this,"Clicked",Toast.LENGTH_SHORT).show();
                    if(etUsername.getText().toString().isEmpty() || etPassword.getText().toString().isEmpty()){
                        Toast.makeText(SigninActivity.this,"Username and Password fields can't be empty",Toast.LENGTH_SHORT).show();
                    }
                    else{
                        ParseUser.logInInBackground(etUsername.getText().toString(), etPassword.getText().toString(), new LogInCallback() {
                            @Override
                            public void done(ParseUser user, ParseException e) {
                                if (user != null) {
                                    Toast.makeText(SigninActivity.this, "Login successful", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(SigninActivity.this, MainActivity.class);
                                    startActivity(intent);
                                    finish();

                                } else {
                                    Toast.makeText(SigninActivity.this, "Signin Error! Re-enter Username and Password.", Toast.LENGTH_SHORT).show();
                                }

                            }
                        });

                    }

                }
            });

            btSignUp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(SigninActivity.this, SignupActivity.class);
                    startActivity(intent);
                }
            });

        /*  ivFacebook.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    ParseFacebookUtils.logInWithReadPermissionsInBackground(SigninActivity.this, permissions, new LogInCallback() {
                        @Override
                        public void done(ParseUser user, ParseException err) {
                            if (user == null) {
                                Log.d("MyApp", "Uh oh. The user cancelled the Facebook login.");
                            } else if (user.isNew()) {
                                Log.d("MyApp", "User signed up and logged in through Facebook!");
                            } else {
                                Log.d("MyApp", "User logged in through Facebook!");
                            }
                        }
                    });

                }
            });*/

        }
    }

}
