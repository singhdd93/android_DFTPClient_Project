package com.singhdd.dftpclient;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.singhdd.dftpclient.common.view.SlidingTabLayout;


public class MainActivity extends ActionBarActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {


    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    RemoteFragment tab1;
    LocalFragment tab2;


    /**
     * A custom {@link ViewPager} title strip which looks much like Tabs present in Android v4.0 and
     * above, but is designed to give continuous feedback to the user when scrolling.
     */
    private SlidingTabLayout mSlidingTabLayout;

    /**
     * A {@link ViewPager} which will be used in conjunction with the {@link SlidingTabLayout} above.
     */
    private ViewPager mViewPager;

    static final String LOG_TAG = "SlidingTabsMainActivity";

    CharSequence Titles[]={"Remote","Local"};
    int Numboftabs =2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);




        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));


        ViewPagerAdapter adapter =  new ViewPagerAdapter(getSupportFragmentManager(),Titles,Numboftabs);




        if(findViewById(R.id.sliding_tabs) != null) {

            // Get the ViewPager and set it's PagerAdapter so that it can display items
            mViewPager = (ViewPager) findViewById(R.id.viewpager);
            mViewPager.setAdapter(adapter);


            // Give the SlidingTabLayout the ViewPager, this must be done AFTER the ViewPager has had
            // it's PagerAdapter set.
            mSlidingTabLayout = (SlidingTabLayout) findViewById(R.id.sliding_tabs);
            mSlidingTabLayout.setViewPager(mViewPager);

        }
        else {
            if(savedInstanceState == null) {
                tab1 = new RemoteFragment();
                tab2 = new LocalFragment();

                getSupportFragmentManager().beginTransaction().replace(R.id.container_remote, tab1,"f1").commit();
                getSupportFragmentManager().beginTransaction().replace(R.id.container_local, tab2,"f2").commit();
            }
            else
            {
                tab1 = (RemoteFragment) getSupportFragmentManager().findFragmentByTag("f1");
                tab2 = (LocalFragment) getSupportFragmentManager().findFragmentByTag("f2");
            }
        }

    }

    public boolean drawerVisible() {
        Log.d("MENU",""+mNavigationDrawerFragment.isMenuVisible());
        Log.d("MENU2",""+mNavigationDrawerFragment.isVisible());
        return mNavigationDrawerFragment.isMenuVisible();
    }

    public void showDrawer(){
        mNavigationDrawerFragment.setMenuVisibility(true);
    }


    @Override
    public void onNavigationDrawerItemSelected(final String host, final String uName, String password, final String port) {
        // update the main content by replacing fragments
        /*FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, RemoteFragment.newInstance(position + 1))
                .commit();*/
        //Toast.makeText(MainActivity.this,"Selected Position = "+(position+1), Toast.LENGTH_SHORT).show();

        if(password == null){
            AlertDialog.Builder passwordAlert = new AlertDialog.Builder(this);
            passwordAlert.setTitle("Password");
            passwordAlert.setMessage("Enter the Password & Press OK");

            final View v = getLayoutInflater().inflate(R.layout.popup_password,null);

            final EditText passInput = (EditText) v.findViewById(R.id.popup_pass_input);

            passwordAlert.setView(v);

            passwordAlert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String pass = passInput.getText().toString();

                    if (tab1 != null) {
                        tab1.connectFTP(host, uName, pass, port);
                    }
                }
            });

            passwordAlert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Toast.makeText(getBaseContext(), "Login Cancelled", Toast.LENGTH_SHORT).show();
                }
            });

            passwordAlert.show();
        }else {

            if (tab1 != null) {

                tab1.connectFTP(host, uName, password, port);

            }
        }

    }

    @Override
    public void disconnectFTPServer() {
        if (tab1 != null) {

            tab1.disconnectFTPServer();

        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        if (!mNavigationDrawerFragment.isDrawerOpen()) {
//            // Only show items in the action bar relevant to this screen
//            // if the drawer is not showing. Otherwise, let the drawer
//            // decide what to show in the action bar.
//            getMenuInflater().inflate(R.menu.main, menu);
//            restoreActionBar();
//            return true;
//        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    class ViewPagerAdapter extends FragmentStatePagerAdapter {

        CharSequence Titles[]; // This will Store the Titles of the Tabs which are Going to be passed when ViewPagerAdapter is created
        int NumbOfTabs; // Store the number of tabs, this will also be passed when the ViewPagerAdapter is created


        // Build a Constructor and assign the passed Values to appropriate values in the class
        public ViewPagerAdapter(FragmentManager fm,CharSequence mTitles[], int mNumbOfTabsumb) {
            super(fm);

            this.Titles = mTitles;
            this.NumbOfTabs = mNumbOfTabsumb;

        }

        //This method return the fragment for the every position in the View Pager
        @Override
        public Fragment getItem(int position) {

            if(position == 0) // if the position is 0 we are returning the First tab
            {
                tab1 = new RemoteFragment();
                return tab1;
            }
            else             // As we are having 2 tabs if the position is now 0 it must be 1 so we are returning second tab
            {
                tab2 = new LocalFragment();
                return tab2;
            }


        }

        // This method return the titles for the Tabs in the Tab Strip

        @Override
        public CharSequence getPageTitle(int position) {
            return Titles[position];
        }

        // This method return the Number of tabs for the tabs Strip

        @Override
        public int getCount() {
            return NumbOfTabs;
        }
    }


    public void reloadLocalFiles(){
        if(tab2 != null) {
            tab2.reloadFiles();
        }
    }

    public void reloadFTPFiles(){
        if(tab1 != null) {
            tab1.reloadFiles();
        }
    }



}
