package hugo.alberto.community.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Toast;

import com.parse.ParseUser;

import hugo.alberto.community.Fragments.Map_Fragment;
import hugo.alberto.community.Fragments.MyPosts_Fragment;
import hugo.alberto.community.Fragments.Settings_Fragment;
import hugo.alberto.community.R;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        displayView(1);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        displayView(item.getItemId());
        return true;
    }

    public void displayView(int viewId) {

        Fragment fragment = null;
        String title = null;

        switch (viewId) {
            default:
                fragment = new Map_Fragment();
                title = "Map";
                break;
            case R.id.nav_map:
                fragment = new Map_Fragment();
                title = "Map";
                break;
            case R.id.nav_myposts:
                fragment = new MyPosts_Fragment();
                title = "My Posts";
                break;
            case R.id.nav_manage:
                fragment = new Settings_Fragment();
                title = "Settings";
                break;
            case R.id.nav_logout:
                ParseUser.logOut();
                Intent intent = new Intent(MainActivity.this, DispatchActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                title = "Logout";
                break;
            case R.id.nav_share:
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("text/plain");
                i.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name));
                String sAux = getString(R.string.shared_link);
                i.putExtra(Intent.EXTRA_TEXT, sAux);
                startActivity(Intent.createChooser(i, "choose one"));
                break;
            case R.id.nav_send:
                Intent send = new Intent(Intent.ACTION_SEND);
                send.setType("message/rfc822");
                send.putExtra(Intent.EXTRA_EMAIL, new String[]{getString(R.string.send_email)});
                send.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.email_subject));
                send.putExtra(Intent.EXTRA_TEXT, getString(R.string.email_text));
                try {
                    startActivity(Intent.createChooser(send, "Send mail..."));
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(this, R.string.no_email_client, Toast.LENGTH_SHORT).show();
                }
                break;
        }

        if (fragment != null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.content_frame, fragment);
            ft.commit();
        }

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(title);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
    }
}
