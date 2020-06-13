package com.codigoj.liciu;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.codigoj.liciu.data.AppPreferences;
import com.codigoj.liciu.utils.DeleteTokenService;
import com.codigoj.liciu.utils.Utils;
import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class TabsPublication extends AppCompatActivity implements  NavigationView.OnNavigationItemSelectedListener{

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase database;
    private DatabaseReference ref;
    private ValueEventListener listenerTime;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseUser user;

    private AppPreferences appPreferences;

    private String keywords = "";

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tabs_publication);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //SharedPreferences instance
        appPreferences = new AppPreferences(getApplicationContext());

        String category = appPreferences.getDataString(Utils.KEY_CATEGORY,"");
        //Get the subcategory (several or one)
        String subcategory = appPreferences.getDataString(Utils.KEY_SUBCATEGORY,"");
        //Get the id_user from user
        String id = appPreferences.getDataString(Utils.KEY_ID_USER,"");


        if (category.isEmpty() || subcategory.isEmpty() || id.isEmpty()) {
            finish();
            Toast.makeText(this, "Selecciona una categoria y subcategoria por favor", Toast.LENGTH_SHORT).show();
        }

        firebaseAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        //Implements the navegation drawer
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                user = firebaseAuth.getCurrentUser();
                //user.reload();
                if (user == null){
                    finish();
                    //Create a new token for the next user session
                    Intent intent = new Intent(TabsPublication.this, DeleteTokenService.class);
                    startService(intent);
                    //Clean all preferences
                    appPreferences.cleanPreferences();
                    goLoginScreen();
                }
            }
        };

    }

    private void createDate(String id){
        ref = database.getReference().child(Utils.REF_USERS).child(id).child(Utils.REF_UPDATE_AT);
        listenerTime = new ValueEventListener() {
            public void onDataChange(DataSnapshot dataSnapshot) {
                appPreferences.saveDataString(Utils.KEY_UPDATE_AT, dataSnapshot.getValue().toString());
            }

            public void onCancelled(DatabaseError databaseError) { }
        };
        ref.addValueEventListener(listenerTime);
        ref.setValue(ServerValue.TIMESTAMP);
    }

    private void goProfileScreen() {
        Intent intent = new Intent(this, ProfileActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void goLoginScreen() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //IMPORTANT GET THE SERVER DATE FOR THIS USER
        //Get the id_user from user
        String id = appPreferences.getDataString(Utils.KEY_ID_USER,"");
        createDate(id);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (listenerTime != null){
            ref.removeEventListener(listenerTime);
        }
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
        if (listenerTime != null){
            ref.removeEventListener(listenerTime);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_tabs, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_search) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(TabsPublication.this);
            builder.setTitle("Búsqueda");
            final EditText input = new EditText(this);
            input.setInputType(InputType.TYPE_CLASS_TEXT);
            builder.setView(input);
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    keywords = input.getText().toString();
                    dialog.dismiss();
                    if (keywords.isEmpty())
                        Toast.makeText(TabsPublication.this, "No hay palabras de búsqueda", Toast.LENGTH_SHORT).show();
                    else
                        filterSelection(keywords);
                }
            });
            builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            builder.show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void filterSelection(final String keywords) {
        final CharSequence[] options = {"Empresa", "Publicación", "Interés"};
        final AlertDialog.Builder builder2 = new AlertDialog.Builder(TabsPublication.this);
        builder2.setTitle("Búsqueda");
        builder2.setSingleChoiceItems(options, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int selection) {
                Intent search = new Intent(TabsPublication.this, SearchActivity.class);
                switch (selection){
                    case 0:
                        search.putExtra("sbc", keywords);
                        startActivity(search);
                        break;
                    case 1:
                        search.putExtra("sbp", keywords);
                        startActivity(search);
                        break;
                    case 2:
                        search.putExtra("sbi", keywords);
                        startActivity(search);
                        break;
                }
            }
        });
        builder2.show();
    }



    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment_Interests fi = new Fragment_Interests();
            switch (position) {
                case 0:
                    return fi;
                case 1:
                    Fragment_Favorites ff = new Fragment_Favorites();
                    return ff;
                case 2:
                    Fragment_News fn = new Fragment_News();
                    return fn;
                default:
                    return fi;
            }
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Mis intereses";
                case 1:
                    return "Favoritos";
                case 2:
                    return "Novedades";
                default:
                    return "Mis intereses";
            }
        }
    }

    //--------------------------
    //Navegation Drawer
    //--------------------------

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_profile_nav) {
            startActivity(new Intent(this, ProfileActivity.class));
        } else if (id == R.id.nav_my_cuopons) {
            startActivity(new Intent(this, CouponsActivity.class));
        } else if (id == R.id.nav_politics) {
            startActivity(new Intent(this, PoliticsActivity.class));
        } else if (id == R.id.nav_about) {
            startActivity(new Intent(this, AboutActivity.class));
        } else if (id == R.id.nav_logout) {
            String provider = firebaseAuth.getCurrentUser().getProviderId();
            Log.d("provider", provider);
            for (UserInfo user : firebaseAuth.getCurrentUser().getProviderData()){
                if (user.getProviderId().equals("facebook.com")) {
                    LoginManager.getInstance().logOut();
                    Log.d("providers", provider);
                }
            }
            firebaseAuth.signOut();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
