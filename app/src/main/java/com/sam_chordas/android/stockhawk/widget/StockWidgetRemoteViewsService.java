package com.sam_chordas.android.stockhawk.widget;

import android.annotation.TargetApi;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.data.QuoteColumns;
import com.sam_chordas.android.stockhawk.data.QuoteProvider;
import com.sam_chordas.android.stockhawk.ui.StockDetailActivity;

/**
 * Created by relferreira on 8/6/16.
 */
public class StockWidgetRemoteViewsService extends RemoteViewsService {

    private static final String[] QUOTES_PROJECTION = new String[]{
            QuoteColumns._ID, QuoteColumns.SYMBOL, QuoteColumns.BIDPRICE};

    private static final int COLUMN_ID_INDEX = 0;
    private static final int COLUMN_SYMBOL_INDEX = 1;
    private static final int COLUMN_BIDPRICE_INDEX = 2;

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new RemoteViewsFactory() {
            private Cursor data = null;

            @Override
            public void onCreate() {

            }

            @Override
            public void onDataSetChanged() {
                if (data != null) {
                    data.close();
                }

                final long identityToken = Binder.clearCallingIdentity();

                data = getContentResolver().query(QuoteProvider.Quotes.CONTENT_URI,
                        QUOTES_PROJECTION,
                        QuoteColumns.ISCURRENT + " = ?",
                        new String[]{"1"},
                        null);

                Binder.restoreCallingIdentity(identityToken);
            }

            @Override
            public void onDestroy() {
                if (data != null) {
                    data.close();
                }
            }

            @Override
            public int getCount() {
                return data == null ? 0 : data.getCount();
            }

            @Override
            public RemoteViews getViewAt(int position) {
                if (data == null || !data.moveToPosition(position)) {
                    return null;
                }

                RemoteViews views = new RemoteViews(getPackageName(), R.layout.widget_stock_list_item);

                views.setTextViewText(R.id.stock_symbol, data.getString(COLUMN_SYMBOL_INDEX));
                views.setTextViewText(R.id.bid_price, data.getString(COLUMN_BIDPRICE_INDEX));

                final Intent fillInIntent = new Intent();
                fillInIntent.putExtra(StockDetailActivity.ARG_SYMBOL, data.getString(COLUMN_SYMBOL_INDEX));
                views.setOnClickFillInIntent(R.id.widget_list_item, fillInIntent);

                return views;
            }

            @Override
            public RemoteViews getLoadingView() {
                return new RemoteViews(getPackageName(), R.layout.widget_stock_list_item);
            }

            @Override
            public int getViewTypeCount() {
                return 1;
            }

            @Override
            public long getItemId(int position) {
                if (data.moveToPosition(position))
                    return data.getLong(COLUMN_ID_INDEX);
                return position;
            }

            @Override
            public boolean hasStableIds() {
                return true;
            }
        };
    }
}
