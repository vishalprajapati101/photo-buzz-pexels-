package com.imagevault.wallpapersinfinity.activity;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.imagevault.wallpapersinfinity.R;
import com.imagevault.wallpapersinfinity.adpter.ViewPagerAdapter;
import com.imagevault.wallpapersinfinity.model.FavoritesManager;
import com.imagevault.wallpapersinfinity.model.PexelsResponse;

public class MainActivity extends AppCompatActivity  {
    private DrawerLayout drawerLayout;
    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private void toggleFavorite(PexelsResponse.Photo photo) {
        if (FavoritesManager.getFavorites().contains(photo)) {
            FavoritesManager.removeFavorite(photo, this);
        } else {
            FavoritesManager.addFavorite(photo, this);
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        NavigationView navigationView = findViewById(R.id.navigation_view);
        navigationView.setItemIconTintList(ColorStateList.valueOf(getResources().getColor(R.color.white)));
        FavoritesManager.init(this);
        // Set up Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Create TextView programmatically
        TextView title = new TextView(this);

        // Set custom font (make sure to place the font in res/assets/fonts folder)
        Typeface customFont = ResourcesCompat.getFont(this, R.font.pacifico);
        title.setTypeface(customFont);

        // Set the title text, color, and size
        title.setText(R.string.app_name);
        title.setTextColor(Color.WHITE);
        title.setTextSize(20);  // Adjust size as needed
        title.setGravity(Gravity.CENTER);

        // Set LayoutParams for the title to make it fit inside the toolbar
        Toolbar.LayoutParams params = new Toolbar.LayoutParams(
                Toolbar.LayoutParams.MATCH_PARENT,
                Toolbar.LayoutParams.WRAP_CONTENT
        );
        title.setLayoutParams(params);

        // Add the title TextView to the toolbar
        toolbar.addView(title);
        ImageView search =findViewById(R.id.searchimge);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent searchi =new Intent(MainActivity.this,SearchActivity.class);
               startActivity(searchi);
            }
        });
        // Set up DrawerLayout
        drawerLayout = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        toggle.getDrawerArrowDrawable().setColor(getResources().getColor(android.R.color.white));
        // Set up NavigationView
        navigationView.setItemIconTintList(ContextCompat.getColorStateList(this, R.color.navigation_item_color));
        navigationView.setItemTextColor(ContextCompat.getColorStateList(this, R.color.navigation_item_color));

        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_fav) {
                // Handle New Option
                // Perform the action you want for this new option
                Intent newOptionIntent = new Intent(MainActivity.this, FavoriteWallActivity.class);
                startActivity(newOptionIntent);
            }

            // Close the drawer when an item is clicked
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });

        // Set up ViewPager and TabLayout
        tabLayout = findViewById(R.id.tab_layout);
        viewPager = findViewById(R.id.view_pager);

        ViewPagerAdapter adapter = new ViewPagerAdapter(this);
        viewPager.setAdapter(adapter);

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {

            switch (position) {
                case 0:
                    tab.setText("Daily");
                   
                    break;
                case 1:
                    tab.setText("Latest");
                    break;
                case 2:
                    tab.setText("Category");
                    break;
            }
        }).attach();
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}
