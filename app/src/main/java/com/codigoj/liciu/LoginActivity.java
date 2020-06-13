package com.codigoj.liciu;


import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.codigoj.liciu.data.AppPreferences;
import com.codigoj.liciu.modelo.User;
import com.codigoj.liciu.utils.Utils;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.Email;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.mobsandgeeks.saripaar.annotation.Password;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Console;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity implements Validator.ValidationListener {

    private FirebaseDatabase database;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;
    private FirebaseAuth.AuthStateListener firebaseAuthListener;
    private FirebaseUser user;
    private User userFacebook;
    private LoginButton facebookLoginButton;
    private CallbackManager callbackManager;
    //validator
    Validator validator;
    //Data
    private AppPreferences appPreferences;

    // UI references and validations
    @NotEmpty
    @Email(messageResId = R.string.txtValidateEmail)
    private EditText inputEmail;
    @Password(min = 6, scheme = Password.Scheme.ALPHA_NUMERIC_MIXED_CASE, messageResId = R.string.txtValidatePassword)
    private EditText inputPassword;
    private Button btnLogin;
    private TextView title_login, title_email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        facebookLoginButton = (LoginButton) findViewById(R.id.facebook_button_login);
        facebookLoginButton.setReadPermissions("email","public_profile");
        callbackManager = CallbackManager.Factory.create();
        facebookLoginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                progressDialog.setMessage(getString(R.string.loading));
                progressDialog.show();
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Toast.makeText(LoginActivity.this, R.string.cancel_login, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException error) {
                Toast.makeText(LoginActivity.this, R.string.error_login, Toast.LENGTH_SHORT).show();
                Log.d("error-login","login:"+error.getMessage());
            }
        });
        //Local data
        appPreferences = new AppPreferences(this);
        //Instance of firebase
        database = FirebaseDatabase.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                user = firebaseAuth.getCurrentUser();
                if (user != null ){
                    String token = FirebaseInstanceId.getInstance().getToken();
                    String id_user = user.getUid();
                    DatabaseReference ref = database.getReference().child(Utils.REF_USERS).child(id_user);
                    //Save the token for this user for FCM
                    HashMap<String, Object> result = new HashMap<>();
                    result.put("/token", token);
                    ref.updateChildren(result);
                    if (userFacebook != null) {
                        //Data for upload
                        userFacebook.setId_user(id_user);
                        Map<String, Object> childUpdates = userFacebook.toMap();
                        childUpdates.put("token", token);
                        ref.updateChildren(childUpdates);
                    }
                    //Save the user data in shared preferences
                    appPreferences.saveDataString(Utils.KEY_ID_USER, id_user);
                    //Search if the user have selected preferences
                    Query ref1 = database.getReference().child(Utils.REF_USERS).child(id_user).child(Utils.REF_CATEGORY);
                    ref1.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            progressDialog.dismiss();
                            if (dataSnapshot.exists()){
                                String catlist = "";
                                Gson gson = new Gson();
                                Map<String,Object> map = new HashMap<>();
                                for (DataSnapshot cat : dataSnapshot.getChildren()) {
                                    catlist += cat.getKey() + ",";
                                    String subCatKey = "/"+cat.getKey()+"/subcategory";
                                    String subCatValue = cat.child(Utils.REF_SUBCATEGORY).getValue(String.class);
                                    map.put(subCatKey,subCatValue);
                                }
                                String subCategoriesJSON = gson.toJson(map);
                                //Remove the last character from the categories, it mean the ","
                                String category = catlist.substring(0,catlist.length()-1);
                                //Save in SHAREDPREFERENCES
                                appPreferences.saveDataString(Utils.KEY_CATEGORY, category);
                                appPreferences.saveDataString(Utils.KEY_SUBCATEGORY, subCategoriesJSON);
                                goMainScreen();
                            } else {
                                goProfileScreen();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            }
        };
        // Set up the login form.
        inputEmail = (EditText) findViewById(R.id.input_email);
        inputPassword = (EditText) findViewById(R.id.input_password);
        btnLogin = (Button) findViewById(R.id.btn_login);
        title_login = (TextView) findViewById(R.id.tv_title_main);
        title_email = (TextView) findViewById(R.id.tv_title_email);

        progressDialog = new ProgressDialog(this);

        //load the typeface
        Typeface berlinSansFB= Typeface.createFromAsset(getAssets(),"fonts/BRLNSR.TTF");
        title_login.setTypeface(berlinSansFB);
        title_email.setTypeface(berlinSansFB);
        inputEmail.setTypeface(berlinSansFB);
        inputPassword.setTypeface(berlinSansFB);
        btnLogin.setTypeface(berlinSansFB);
        validator = new Validator(this);
        validator.setValidationListener(this);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validator.validate();
            }
        });
    }

    private void handleFacebookAccessToken(final AccessToken accessToken) {
        String token = accessToken.getToken();
        GraphRequest request = GraphRequest.newMeRequest(accessToken, new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(JSONObject object, GraphResponse response) {
                Log.d("LoginResponse", response.toString());
                //Get facebook data from login
                userFacebook = getFacebookData(object);
                if (userFacebook != null) {
                    //Login in firebase
                    AuthCredential credential = FacebookAuthProvider.getCredential(accessToken.getToken());
                    progressDialog.setMessage(getString(R.string.login_facebook_msg));
                    firebaseAuth.signInWithCredential(credential).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (!task.isSuccessful()){
                                Toast.makeText(LoginActivity.this, R.string.error_login_with_credential, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    progressDialog.dismiss();
                    Toast.makeText(LoginActivity.this, "El acceso es autorizado solo para mayores de edad" +
                            " y tu cuenta de facebook debe tener un correo registrado.", Toast.LENGTH_SHORT).show();
                    LoginManager.getInstance().logOut();
                }
            }
        });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id, first_name, last_name, email,gender, age_range");
        request.setParameters(parameters);
        request.executeAsync();
    }

    private void goProfileScreen() {
        Intent intent = new Intent(this, ProfileActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private User getFacebookData(JSONObject object) {
        User usr = null;
        try {
            //Validate the age range
            if (object.has("age_range") && object.getJSONObject("age_range").getInt("min") >= 18){
                usr = new User();
                String name = "";
                if (object.has("first_name"))
                    name = object.getString("first_name");
                if (object.has("last_name"))
                    name += " "+object.getString("last_name");
                if (object.has("email"))
                    usr.setEmail(object.getString("email"));
                    if (usr.getEmail().isEmpty()){
                        return null;
                    }
                if (object.has("gender"))
                    usr.setGender(object.getString("gender"));

                usr.setName(name);
            }
        }
        catch(JSONException e) {
            Log.d("LoginResponseException","Error parsing JSON");
        }
        return usr;
    }

    private void goMainScreen() {
        Intent intent = new Intent(this, TabsPublication.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    protected void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(firebaseAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (firebaseAuthListener != null) {
            firebaseAuth.removeAuthStateListener(firebaseAuthListener);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }


    @Override
    public void onValidationSucceeded() {
        String email = inputEmail.getText().toString().trim();
        String pass = inputPassword.getText().toString().trim();
        progressDialog.setMessage(getString(R.string.logging_in));
        progressDialog.show();

        firebaseAuth.signInWithEmailAndPassword(email, pass)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful())
                        {
                            if (task.getException().getMessage().contains("A network error")){
                                Toast.makeText(getApplicationContext(), "Verifique su conexion a internet", Toast.LENGTH_SHORT).show();
                            }
                            else{
                                Toast.makeText(getApplicationContext(), "Datos invalidos", Toast.LENGTH_SHORT).show();
                            }
                            progressDialog.dismiss();
                        }
                    }
                });
    }

    @Override
    public void onValidationFailed(List<ValidationError> errors) {
        for (ValidationError error : errors) {
            View view = error.getView();
            String message = error.getCollatedErrorMessage(this);

            // Display error messages ;)
            if (view instanceof EditText) {
                ((EditText) view).setError(message);
            } else {
                Toast.makeText(this, message, Toast.LENGTH_LONG).show();
            }
        }
    }
}

