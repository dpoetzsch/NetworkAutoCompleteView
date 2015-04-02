/*
 * Copyright (C) 2015 David Poetzsch-Heffter <david.poetzsch-heffter@gmx.net>
 */
package de.poetzsch_heffter.networkautocompleteview;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.ProgressBar;

import java.lang.ref.WeakReference;

/**
 * An AutoCompleteTextView that supports delayed autocomplete suggestions and a loading indicator.
 * It is suggested to subclass the NetworkAutoCompleteAdapter for an adapter.
 * @param <T>    The type of the items of the adapter
 */
public class NetworkAutoCompleteTextView<T> extends AutoCompleteTextView {
    private static final int MESSAGE_TEXT_CHANGED = 100; /// internally used for delayed autocomplete
    private static final int DEFAULT_AUTOCOMPLETE_DELAY = 750;
    private static final int MIN_PATTERN_LENGTH = 3; /// The minimal length of a pattern such that search is enabled

    private T selectedItem = null;
    private ProgressBar loadingIndicator = null;

    public NetworkAutoCompleteTextView(Context context) {
        super(context);
        setUp();
    }

    public NetworkAutoCompleteTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setUp();
    }

    public NetworkAutoCompleteTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setUp();
    }

    private void setUp() {
        // only do this if not in the developer preview screen
        if (!this.isInEditMode()) {
            this.setThreshold(MIN_PATTERN_LENGTH);

            setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                    NetworkAutoCompleteTextView.this.onItemClick(adapterView, view, position, id);
                }
            });

            addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    setSelectedItem(null);
                }

                @Override
                public void afterTextChanged(Editable s) {
                }
            });
        }
    }

    /**
     * Called when an item was selected. This sets the selectedItem. Subclasses can override this
     * method to do additional things.
     * @param adapterView
     * @param view
     * @param position
     * @param id
     */
    protected void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        // TODO what if user continues typing? then selectedMovie has to be nulled again
        setSelectedItem((T) adapterView.getItemAtPosition(position));
    }

    protected T getSelectedItem() {
        return selectedItem;
    }

    protected void setSelectedItem(T item) {
        selectedItem = item;
    }

    public void setLoadingIndicator(ProgressBar progressBar) {
        loadingIndicator = progressBar;
    }

    // this complex construction with WeakReferences is done for enabling garbage collection
    private final Handler delayedFilterHandler = new DelayedFilterHandler(new WeakReference<NetworkAutoCompleteTextView<?>>(this));

    static class DelayedFilterHandler extends Handler {
        private final WeakReference<NetworkAutoCompleteTextView<?>> nactv;

        public DelayedFilterHandler(WeakReference<NetworkAutoCompleteTextView<?>> nactv) {
            this.nactv = nactv;
        }

        @Override
        public void handleMessage(Message msg) {
            nactv.get().superPerformFiltering((CharSequence) msg.obj, msg.arg1);
        }
    }

    private void superPerformFiltering(CharSequence text, int keyCode) {
        super.performFiltering(text, keyCode);
    }

    @Override
    protected void performFiltering(CharSequence text, int keyCode) {
        if (loadingIndicator != null) {
            loadingIndicator.setVisibility(View.VISIBLE);
        }
        delayedFilterHandler.removeMessages(MESSAGE_TEXT_CHANGED);
        delayedFilterHandler.sendMessageDelayed(
                delayedFilterHandler.obtainMessage(MESSAGE_TEXT_CHANGED, text),
                DEFAULT_AUTOCOMPLETE_DELAY); // TODO make customizable
    }

    @Override
    public void onFilterComplete(int count) {
        if (loadingIndicator != null) {
            loadingIndicator.setVisibility(View.GONE);
        }

        super.onFilterComplete(count);
    }
}
