package com.uais.uais.academicMaterials;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;
import com.uais.uais.LoginActivity;
import com.uais.uais.R;
import com.uais.uais.academicMaterials.common.data.AbstractExpandableDataProvider;
import com.uais.uais.academicMaterials.common.fragment.AssignmentDataProviderFragment;
import com.uais.uais.academicMaterials.common.fragment.ExampleExpandableDataProviderFragment;

import java.util.ArrayList;
import java.util.List;

public class AcademicActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    SharedPreferences sharedPrefs;
    public String sid, fname_, mname_, sname_;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_academic);

        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        sid = sharedPrefs.getString("student_id","");
        fname_ = sharedPrefs.getString("fname","");
        mname_ = sharedPrefs.getString("mname","");
        sname_ = sharedPrefs.getString("sname","");

        Toast.makeText(this,sid+" "+fname_+" "+mname_+" "+sname_,Toast.LENGTH_LONG).show();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.nav_am);
        //header view
        View v = navigationView.getHeaderView(0);
        TextView tv = (TextView)v.findViewById(R.id.session_name);
        tv.setText(fname_+" "+sname_);
        ImageView iv = (ImageView)v.findViewById(R.id.imageView);

        Picasso.with(this)
                .load("http://liziio0aq-site.1tempurl.com/uploads/"+fname_+"_"+mname_+"_"+sname_+".jpg")  //http://i.imgur.com/DvpvklR.png
                .placeholder(R.drawable.empty_photo)
                .error(R.drawable.empty_photo)
                .resize(150, 150)
                .centerCrop()
                .transform(new CropCircleTransformation())
                .into(iv);
        //imgLoader.DisplayImage("http://www.uais.co.nf/dist/img/"+fname_+"_"+mname_+"_"+sname_+".jpg", iv);

        initViewPagerAndTabs(sid);
        //set statusBar transparent
        setTransparent(this);
    }

    public AbstractExpandableDataProvider getDataProvider() {
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(AdFragment.FRAGMENT_TAG_DATA_PROVIDER);
        return ((ExampleExpandableDataProviderFragment) fragment).getDataProvider();
    }
    public AbstractExpandableDataProvider getDataProviderAssignment() {
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(AssignmentFragment.FRAGMENT_TAG_DATA_PROVIDER);
        return ((AssignmentDataProviderFragment) fragment).getDataProvider();
    }

    private void initViewPagerAndTabs(String id_) {
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewPager);
        PagerAdapter pagerAdapter = new PagerAdapter(getSupportFragmentManager());
        pagerAdapter.addFragment(AdFragment.createInstance_(id_), getString(R.string.tab_1));
        pagerAdapter.addFragment(AcademicFragment.createInstance(id_), getString(R.string.tab_3));
        pagerAdapter.addFragment(AssignmentFragment.createInstanceTwo(id_), getString(R.string.tab_2));

        viewPager.setAdapter(pagerAdapter);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(viewPager);

    }
    public static class PagerAdapter extends FragmentPagerAdapter {

        private final List<Fragment> fragmentList = new ArrayList<>();
        private final List<String> fragmentTitleList = new ArrayList<>();

        public PagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        public void addFragment(Fragment fragment, String title) {
            fragmentList.add(fragment);
            fragmentTitleList.add(title);
        }

        @Override
        public Fragment getItem(int position) {
            return fragmentList.get(position);
        }

        @Override
        public int getCount() {
            return fragmentList.size();
        }
        @Override
        public CharSequence getPageTitle(int position) {
            return fragmentTitleList.get(position);
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            finish();
            super.onBackPressed();
        }
    }

    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.academic, menu);
        return true;
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
    }*/

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_am) {
            // Handle the camera action
        } else if (id == R.id.nav_sms) {

        } else if (id == R.id.nav_profile) {

        } else if (id == R.id.nav_logout) {
            sharedPrefs.edit().remove("student_id").apply();
            Intent i = new Intent(AcademicActivity.this,LoginActivity.class);
            startActivity(i);

            finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public static void setTransparent(Activity activity) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            return;
        }
        // set flags
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            activity.getWindow().setStatusBarColor(Color.TRANSPARENT);
        } else {
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        // get root content of system window

        ViewGroup contentView = (ViewGroup) activity.findViewById(android.R.id.content);
        if (contentView.getChildCount() > 1) {
            contentView.removeViewAt(1);
        }

        // get status bar height
        int res = activity.getResources().getIdentifier("status_bar_height", "dimen", "android");
        int height = 0;
        if (res != 0)
            height = activity.getResources().getDimensionPixelSize(res);

        // create new imageview and set resource id
        ImageView image = new ImageView(activity);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height);
        image.setLayoutParams(params);
        image.setBackgroundColor(Color.parseColor("#20000000"));
        image.setScaleType(ImageView.ScaleType.MATRIX);

        // add image view to content view
        contentView.addView(image);
        // rootView.setFitsSystemWindows(true);

    }
    public class CropCircleTransformation implements Transformation {
        @Override
        public Bitmap transform(Bitmap source) {
            Bitmap result = getCroppedBitmapDrawable(source);
            if (result != source) {
                source.recycle();
            }
            return result;
        }

        @Override
        public String key() { return "square()"; }
        //convert bitmap to circle
        Bitmap getCroppedBitmapDrawable(Bitmap bitmap) {
            Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                    bitmap.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(output);

            final int color = 0xff424242;
            final Paint paint = new Paint();
            final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

            paint.setAntiAlias(true);
            canvas.drawARGB(0, 0, 0, 0);
            paint.setColor(color);
            // canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
            canvas.drawCircle(bitmap.getWidth() / 2, bitmap.getHeight() / 2,
                    bitmap.getWidth() / 2, paint);
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
            canvas.drawBitmap(bitmap, rect, rect, paint);

            return output;
        }
    }
}
