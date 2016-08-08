package com.sam_chordas.android.stockhawk.ui;

import android.content.Loader;
import android.content.res.Resources;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.db.chart.model.LineSet;
import com.db.chart.view.LineChartView;
import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.data.QuoteColumns;
import com.sam_chordas.android.stockhawk.data.QuoteDatabase;
import com.sam_chordas.android.stockhawk.data.QuoteProvider;
import com.sam_chordas.android.stockhawk.rest.TabsAdapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by relferreira on 8/3/16.
 */
public class StockDetailActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final String ARG_SYMBOL = "arg_symbol";
    private static final int LOADER_ID = 1;
    private static final String[] QUOTES_PROJECTION = new String[]{
            QuoteColumns._ID, QuoteColumns.SYMBOL, QuoteColumns.CREATED};

    private String selectedSymbol;
    private LineChartView graph;
    private SimpleDateFormat dateFormater;
    private TabsAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_line_graph);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        Resources resources = getResources();
        adapter = new TabsAdapter(getSupportFragmentManager());
        selectedSymbol = getIntent().getStringExtra(ARG_SYMBOL);

        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setAdapter(adapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        dateFormater = new SimpleDateFormat(getString(R.string.detail_date_format));

        getSupportLoaderManager().initLoader(LOADER_ID, null, this);

    }


    @Override
    public android.support.v4.content.Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] selection = { selectedSymbol };
        return new CursorLoader(
                this,
                QuoteProvider.Quotes.CONTENT_URI,
                QUOTES_PROJECTION,
                QuoteColumns.SYMBOL + " = ?",
                selection,
                QuoteColumns.CREATED + " DESC");
    }

    @Override
    public void onLoadFinished(android.support.v4.content.Loader<Cursor> loader, Cursor cursor) {
        List<Date> dates = new ArrayList<>(cursor.getCount());
        while(cursor.moveToNext()){
            Date date = new Date(Long.valueOf(cursor.getString(cursor.getColumnIndex("created"))));
            Calendar c = Calendar.getInstance();
            c.setTime(date);
            c.set(Calendar.HOUR_OF_DAY, 0);
            c.set(Calendar.MINUTE, 0);
            c.set(Calendar.SECOND, 0);
            c.set(Calendar.MILLISECOND, 0);
            if(!dates.contains(c.getTime()))
                dates.add(c.getTime());
        }

        for (Date date : dates) {
            adapter.add(StockDetailFragment.newInstance(selectedSymbol, date), dateFormater.format(date));
        }

        adapter.notifyDataSetChanged();
        cursor.moveToPosition(-1);

    }

    @Override
    public void onLoaderReset(android.support.v4.content.Loader<Cursor> loader) {

    }

}
