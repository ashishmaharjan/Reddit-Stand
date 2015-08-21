package com.ashish.redditclient.activities;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.ashish.redditclient.R;
import com.ashish.redditclient.fragments.FragmentList;
import com.ashish.redditclient.fragments.FragmentSearch;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionButton;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionMenu;
import com.oguzdev.circularfloatingactionmenu.library.SubActionButton;

/**
 * Created by Ashish on 5/23/2015.
 */
public class MainActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {

    //Initialize toolbar
    private Toolbar toolbar;
    private SearchView searchView;

    //Initialize drawer layout and drawer toggle and nav view
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private NavigationView navigationView;

    //Initialize string arrays
    private String[] titles = {"Front", "Funny", "Sports", "Gaming", "Pics", "Music", "News", "Aww", "AskReddit"};
    private String[] subRedditUrl = {"", "r/funny/", "r/sports/", "r/gaming/", "r/pics/", "r/music/", "r/news/", "r/aww/", "r/AskReddit/"};

    //Initialize fragment manager related objects
    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;

    //Initialize applications fragments
    private FragmentList fragment;
    private FragmentSearch fragmentSearch;

    //Initialize bundle
    private Bundle bundle;


    //Initialize floating action buttons
    private FloatingActionButton actionButton;
    private SubActionButton.Builder itemBuilder;
    private FloatingActionMenu actionMenu;

    //Initialize empty params to store in the bundle later
    private String url = "";
    private String subUrl = "";

    /**
     * sets the url
     *
     * @param url
     */
    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        //to create fabs
        floatingButton();

        toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        toolbar.setLogo(R.drawable.fire);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerlayout);

        navigationView = (NavigationView) findViewById(R.id.navView);

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                invalidateOptionsMenu();
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
//                invalidateOptionsMenu();
            }

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
                toggleFloatingButton(slideOffset);
                toolbar.setAlpha(1 - slideOffset / 2);
            }
        };

        mDrawerLayout.setDrawerListener(mDrawerToggle);

        mDrawerToggle.syncState();

        fragmentManagerCall();

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                menuItem.setChecked(true);

                if (menuItem.getTitle().equals("More")) {
                    mDrawerLayout.closeDrawers();

                    fragmentManager = getFragmentManager();
                    fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentSearch = new FragmentSearch();
                    bundle = new Bundle();
                    bundle.putString("query", "/subreddits");
                    fragmentSearch.setArguments(bundle);
                    fragmentTransaction.replace(R.id.frameLayout, fragmentSearch);
                    fragmentTransaction.addToBackStack(getSupportActionBar().getTitle().toString());
                    fragmentTransaction.commit();

                    toolbar.setTitle("Popular Subreddits");
                    toolbar.setLogo(null);

                    return true;
                }

                for (int i = 0; i < titles.length; i++) {
                    if (menuItem.getTitle().equals(titles[i])) {
                        mDrawerLayout.closeDrawers();

                        FragmentManager fragmentManager = getFragmentManager();
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        FragmentList fragment = new FragmentList();
                        bundle = new Bundle();
                        MainActivity.this.setUrl(subRedditUrl[i]);
                        bundle.putString("url", subRedditUrl[i]);
                        bundle.putString("subUrl", "");

                        fragment.setArguments(bundle);
                        fragmentTransaction.replace(R.id.frameLayout, fragment);
                        fragmentTransaction.addToBackStack(getSupportActionBar().getTitle().toString());
                        fragmentTransaction.commit();

                        if (titles[i].equals("Front")) {
                            toolbar.setTitle(getString(R.string.app_name));
                            toolbar.setLogo(R.drawable.fire);
                            return true;
                        }

                        toolbar.setTitle(titles[i]);
                        toolbar.setLogo(R.drawable.fire);
                        return true;
                    }
                }

                return false;
            }
        });

        //throw exception to test custom crash library
        //throw new RuntimeException("boooom!");

    }


    /**
     * Creates floating action buttons and
     * their listeners.
     */
    public void floatingButton() {
        ImageView icon = new ImageView(this); // Create an icon
        icon.setImageResource(R.drawable.ic_add_white_24dp);

        actionButton = new FloatingActionButton.Builder(this)
                .setContentView(icon).setBackgroundDrawable(R.drawable.fab_selector)
                .build();

        ImageView popular_subs = new ImageView(this); // Create an icon
        popular_subs.setImageResource(R.drawable.fire);
        ImageView rising_subs = new ImageView(this); // Create an icon
        rising_subs.setImageResource(R.drawable.trending_up);
        ImageView new_subs = new ImageView(this); // Create an icon
        new_subs.setImageResource(R.drawable.exclamation);

        itemBuilder = new SubActionButton.Builder(this);
        itemBuilder.setBackgroundDrawable(getResources().getDrawable(R.drawable.fab_selector));
        // repeat many times:
        SubActionButton button_popular = itemBuilder.setContentView(popular_subs).build();
        SubActionButton button_new = itemBuilder.setContentView(new_subs).build();
        SubActionButton button_rising = itemBuilder.setContentView(rising_subs).build();

        button_popular.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                subUrl = "";
                fragmentManagerCall();
                toolbar.setLogo(R.drawable.fire);
                actionMenu.close(true);
            }
        });
        button_new.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                subUrl = "new/";
                fragmentManagerCall();
                toolbar.setLogo(R.drawable.exclamation);
                actionMenu.close(true);
            }
        });
        button_rising.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                subUrl = "rising/";
                fragmentManagerCall();
                toolbar.setLogo(R.drawable.trending_up);
                actionMenu.close(true);
            }
        });

        actionMenu = new FloatingActionMenu.Builder(this)
                .addSubActionView(button_new)
                .addSubActionView(button_popular)
                .addSubActionView(button_rising)
                        // ...
                .attachTo(actionButton)
                .build();

    }

    public void toggleFloatingButton(float slideOffset) {
        if (toolbar.getTitle().equals("Popular Subreddits") || toolbar.getTitle().equals("Search Results")) {
            hideFab();
        } else if (actionMenu != null) {
            if (actionMenu.isOpen()) {
                actionMenu.close(true);
            }
            actionButton.setTranslationX(slideOffset * 200);
        }
    }

    public void hideFab() {
        actionButton.setTranslationX(200);
    }

    public void showFab() {
        actionButton.setTranslationX(0);
    }

    public void fragmentManagerCall() {
        fragmentManager = getFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragment = new FragmentList();
        bundle = new Bundle();
        bundle.putString("url", url);
        bundle.putString("subUrl", subUrl);
        fragment.setArguments(bundle);
        fragmentTransaction.replace(R.id.frameLayout, fragment);
        fragmentTransaction.addToBackStack(getSupportActionBar().getTitle().toString());
        fragmentTransaction.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_view, menu);
        searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setQueryHint("Search subreddits");

        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDrawerLayout.closeDrawers();
            }
        });

        searchView.setOnQueryTextListener(this);

        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onQueryTextSubmit(String query) {

        hideFab();

        invalidateOptionsMenu();

        fragmentManager = getFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentSearch = new FragmentSearch();
        bundle = new Bundle();
        bundle.putString("query", query);
        fragmentSearch.setArguments(bundle);
        fragmentTransaction.replace(R.id.frameLayout, fragmentSearch);
        fragmentTransaction.addToBackStack(getSupportActionBar().getTitle().toString());
        toolbar.setTitle("Search Results");
        toolbar.setLogo(null);
        fragmentTransaction.commit();

        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }

//    @Override
//    public void onBackPressed() {
//        if (getFragmentManager().getBackStackEntryCount() == 0)
//            super.onBackPressed();
//        else
//            getFragmentManager().popBackStack();
//        String prevTitle = fragmentManager.getBackStackEntryAt(fragmentManager.getBackStackEntryCount() - 1).getName();
//        toolbar.setTitle(prevTitle);
//        showFab();
//    }
}