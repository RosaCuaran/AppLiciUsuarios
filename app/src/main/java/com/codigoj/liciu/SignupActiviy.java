package com.codigoj.liciu;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
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
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.Checked;
import com.mobsandgeeks.saripaar.annotation.Email;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.mobsandgeeks.saripaar.annotation.Password;
import com.mobsandgeeks.saripaar.annotation.Past;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SignupActiviy extends AppCompatActivity implements Validator.ValidationListener {
    // Constant
    public final static String REF_USER = "users";
    // Attributes

    //Facebook
    private LoginButton facebookLoginButton;
    private CallbackManager callbackManager;
    // Firebase
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase database;
    private FirebaseUser firebaseUser;
    // local
    private ProgressDialog progressDialog;
    private User user;
    private User userFacebook;
    //Data
    private AppPreferences appPreferences;
    // Validator
    private Validator validator;

    // UI references and validations
    @NotEmpty
    @Email(messageResId = R.string.txtValidateEmail)
    private EditText inputEmail;
    @Password(min = 6, scheme = Password.Scheme.ALPHA_NUMERIC_MIXED_CASE, messageResId = R.string.txtValidatePassword)
    private EditText inputPassword;
    @NotEmpty(messageResId = R.string.txtValidateName)
    private EditText inputName;
    @NotEmpty
    @Past(messageResId = R.string.txtValidateBirthdate, dateFormatResId = R.string.txtValidateFormatBirthdate)
    private EditText inputBirthdate;
    private Spinner spGender;
    private Button btnLogin;
    @Checked(messageResId = R.string.txtValidatePolitics)
    private CheckBox checkboxPolitics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        facebookLoginButton = (LoginButton) findViewById(R.id.facebook_button_signup);
        facebookLoginButton.setReadPermissions("email","public_profile");
        callbackManager = CallbackManager.Factory.create();
        facebookLoginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d("LoginResponseS", loginResult.toString());
                progressDialog.setMessage(getString(R.string.loading));
                progressDialog.show();
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Toast.makeText(SignupActiviy.this, R.string.cancel_login, Toast.LENGTH_SHORT).show();
                Log.d("LoginResponseC", "Cancelado");
            }

            @Override
            public void onError(FacebookException error) {
                Toast.makeText(SignupActiviy.this, R.string.error_login, Toast.LENGTH_SHORT).show();
                Log.d("LoginResponseE","login:"+error.getMessage());
            }
        });
        inputEmail = (EditText) findViewById(R.id.email);
        inputPassword = (EditText) findViewById(R.id.password);
        inputName = (EditText) findViewById(R.id.name);
        inputBirthdate = (EditText) findViewById(R.id.birthdate);
        spGender = (Spinner) findViewById(R.id.gender);
        btnLogin = (Button) findViewById(R.id.btn_login);
        checkboxPolitics = (CheckBox) findViewById(R.id.cbPolitics);
        validator = new Validator(this);
        validator.setValidationListener(this);
        progressDialog = new ProgressDialog(this);
        //Local data
        appPreferences = new AppPreferences(this);
        //Instance firebase variables
        firebaseAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        checkboxPolitics.setText(Html.fromHtml("Al continuar aceptas nuestras "+
        "<a href='com.codigoj.liciu.PoliticsActivity://Kode'>politicas.</a>"));
        checkboxPolitics.setMovementMethod(LinkMovementMethod.getInstance());

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validator.validate();
            }
        });

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                firebaseUser = firebaseAuth.getCurrentUser();
                if (firebaseUser != null ){
                    String token = FirebaseInstanceId.getInstance().getToken();
                    String id_user = firebaseUser.getUid();
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
                    Query ref1 = database.getReference().child(Utils.REF_USERS).child(id_user);
                    ref1.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            progressDialog.dismiss();
                            if (dataSnapshot.hasChild(Utils.REF_CATEGORY) && dataSnapshot.hasChild(Utils.REF_SUBCATEGORY)) {
                                //PROBAR SI GUARDA EN SHAREDPREFERENCES
                                appPreferences.saveDataString(Utils.KEY_CATEGORY, dataSnapshot.child(Utils.REF_CATEGORY).getValue(String.class));
                                appPreferences.saveDataString(Utils.KEY_SUBCATEGORY, dataSnapshot.child(Utils.REF_SUBCATEGORY).getValue(String.class));
                                goMainScreen();
                            } else {
                                goUserProfile();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            }
        };


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
                    name += object.getString("last_name");
                if (object.has("email"))
                    usr.setEmail(object.getString("email"));
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

    private void goUserProfile() {
        Intent intent = new Intent(this, ProfileActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void handleFacebookAccessToken(final AccessToken accessToken) {
        String token = accessToken.getToken();
        GraphRequest request = GraphRequest.newMeRequest(accessToken, new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(JSONObject object, GraphResponse response) {
                //Get facebook data from login
                userFacebook = getFacebookData(object);
                if (userFacebook != null) {
                    //Login in firebase
                    AuthCredential credential = FacebookAuthProvider.getCredential(accessToken.getToken());
                    progressDialog.setMessage(getString(R.string.login_facebook_msg));
                    firebaseAuth.signInWithCredential(credential).addOnCompleteListener(SignupActiviy.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (!task.isSuccessful()) {
                                Toast.makeText(SignupActiviy.this, R.string.error_login_with_credential, Toast.LENGTH_SHORT).show();
                                LoginManager.getInstance().logOut();
                            }
                        }
                    });
                } else {
                    progressDialog.dismiss();
                    Toast.makeText(SignupActiviy.this, R.string.error_login_email_and_adults, Toast.LENGTH_SHORT).show();
                }
            }
        });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id, first_name, last_name, email,gender, age_range");
        request.setParameters(parameters);
        request.executeAsync();
    }

    @Override
    public void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            firebaseAuth.removeAuthStateListener(mAuthListener);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }


    @Override
    public void onValidationSucceeded() {
        progressDialog.setMessage(getString(R.string.signin_progress));
        progressDialog.show();
        final String email = inputEmail.getText().toString().trim();
        String pass = inputPassword.getText().toString().trim();
        final String name = inputName.getText().toString().trim();
        final String birthdate = inputBirthdate.getText().toString().trim();
        final String gender;
        int genderSelection = spGender.getSelectedItemPosition();
        if (genderSelection == 0)
            gender = "male";
        else
            gender = "female";
        firebaseAuth.createUserWithEmailAndPassword(email,pass).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (!task.isSuccessful()) {
                    Log.d("Error", task.getException().getMessage());
                    if (task.getException().getMessage().equals("The email address is already in use by another account.")){
                        Toast.makeText(SignupActiviy.this, R.string.auth_failed_email_on_use,
                                Toast.LENGTH_SHORT).show();
                    } else if (task.getException().getMessage().contains("A network error")){
                        Toast.makeText(getApplicationContext(), R.string.auth_failed_not_conection, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(SignupActiviy.this, R.string.auth_failed,Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    Toast.makeText(SignupActiviy.this, R.string.auth_successful,
                            Toast.LENGTH_SHORT).show();
                    DatabaseReference ref = database.getReference();
                    firebaseUser = firebaseAuth.getCurrentUser();
                    user = new User(firebaseUser.getUid(), name, birthdate, gender, email);
                    firebaseUser.updateProfile(new UserProfileChangeRequest.Builder().setDisplayName(name).build());
                    Map<String, Object> values = user.toMap();
                    Map<String, Object> childUpdates = new HashMap<>();
                    childUpdates.put("/" + REF_USER + "/" + user.getId_user(),values);
                    ref.updateChildren(childUpdates);
                }
                progressDialog.dismiss();
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
