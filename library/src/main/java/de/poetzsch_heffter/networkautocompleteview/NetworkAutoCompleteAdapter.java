/*
 * Copyright (C) 2015 David Poetzsch-Heffter <david.poetzsch-heffter@gmx.net>
 */
package de.poetzsch_heffter.networkautocompleteview;

import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;

import com.android.volley.Response;
import com.android.volley.toolbox.RequestFuture;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public abstract class NetworkAutoCompleteAdapter<T> extends BaseAdapter implements Filterable {
    private final Provider<T> provider;

    private List<T> items = new ArrayList<>();

    public NetworkAutoCompleteAdapter(Provider provider) {
        super();
        this.provider = provider;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public T getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO what about that
        return position;
    }

    @Override
    public Filter getFilter() {
        return new NetworkFilter();
    }

    protected class NetworkFilter extends Filter {
        @Override
        protected Filter.FilterResults performFiltering(CharSequence constraint) {
            if (constraint != null) {
                RequestFuture<List<T>> fItems = RequestFuture.newFuture();
                provider.getSuggestions(constraint.toString(), fItems, fItems);

                try {
                    items = fItems.get();
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            }

            final Filter.FilterResults filterResults = new Filter.FilterResults();
            filterResults.values = items;
            filterResults.count = items.size();

            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence constraint, Filter.FilterResults results) {
            if ((results != null) && (results.count > 0)) {
                notifyDataSetChanged();
            } else {
                notifyDataSetInvalidated();
            }
        }
    }

    public static interface Provider<T> {
        public void getSuggestions(CharSequence constraint, Response.Listener<List<T>> listener,
                                   Response.ErrorListener errorListener);
    }
}
