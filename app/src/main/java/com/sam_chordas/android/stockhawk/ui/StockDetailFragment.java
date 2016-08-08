package com.sam_chordas.android.stockhawk.ui;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.db.chart.model.LineSet;
import com.db.chart.view.LineChartView;
import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.data.QuoteColumns;
import com.sam_chordas.android.stockhawk.data.QuoteProvider;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by relferreira on 8/5/16.
 */
public class StockDetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final String ARG_SYMBOL = "arg_symbol";
    public static final String ARG_DATE = "arg_date";
    private static final int LOADER_ID = 1;
    private static final String[] QUOTES_PROJECTION = new String[]{
            QuoteColumns._ID, QuoteColumns.SYMBOL, QuoteColumns.BIDPRICE,
            QuoteColumns.PERCENT_CHANGE, QuoteColumns.CHANGE, QuoteColumns.ISUP, QuoteColumns.CREATED};
    private LineChartView graph;
    private SimpleDateFormat dateFormater;

    public static StockDetailFragment newInstance(String symbol, Date date) {
        StockDetailFragment frag = new StockDetailFragment();
        Bundle params = new Bundle();
        params.putString(ARG_SYMBOL, symbol);
        params.putLong(ARG_DATE, date.getTime());
        frag.setArguments(params);
        return frag;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_stock_detail, container, false);
        graph = (LineChartView) view.findViewById(R.id.linechart);
        dateFormater = new SimpleDateFormat(getString(R.string.detail_date_format));
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getLoaderManager().initLoader(LOADER_ID, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String selectedSymbol = getArguments().getString(ARG_SYMBOL);
        Date selectedDate = new Date(getArguments().getLong(ARG_DATE));
        Calendar c = Calendar.getInstance();
        c.setTime(selectedDate);
        c.add(Calendar.DAY_OF_MONTH, 1);
        String[] selection = { selectedSymbol, String.valueOf(selectedDate.getTime()), String.valueOf(c.getTime().getTime()) };
        return new CursorLoader(
                getActivity(),
                QuoteProvider.Quotes.CONTENT_URI,
                QUOTES_PROJECTION,
                QuoteColumns.SYMBOL + " = ? AND " + QuoteColumns.CREATED + " >= ? AND " + QuoteColumns.CREATED + " < ?" ,
                selection,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        List<Float> prices = new ArrayList<>(cursor.getCount());
        List<String> labels = new ArrayList<>(cursor.getCount());
        while(cursor.moveToNext()){
            Float price = Float.valueOf(cursor.getString(cursor.getColumnIndex("bid_price")));
            Date date = new Date(Long.valueOf(cursor.getString(cursor.getColumnIndex("created"))));
            String label = dateFormater.format(date);
            prices.add(price);
            labels.add(label);
        }

        if (prices.size() == 0 || labels.size() == 0)
            return;

        float[] finalPrices = new float[prices.size()];
        float maxValue = prices.get(0);
        float minValue = prices.get(0);
        for (int i = 0; i < prices.size(); i++) {
            finalPrices[i] = prices.get(i);

            if(finalPrices[i] < minValue) {
                minValue = finalPrices[i];
            }

            if(finalPrices[i] > maxValue) {
                maxValue = finalPrices[i];
            }

        }

        LineSet dataset = new LineSet(labels.toArray(new String[labels.size()]), finalPrices);
        graph.addData(dataset);
        graph.setAxisBorderValues((int)Math.floor(minValue) - 1, Math.round(maxValue) + 1);
        graph.show();

        cursor.moveToPosition(-1);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
