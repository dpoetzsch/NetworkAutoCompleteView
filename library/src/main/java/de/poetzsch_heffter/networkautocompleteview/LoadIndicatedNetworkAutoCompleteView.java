/*
 * Copyright (C) 2015 David Poetzsch-Heffter <davidpoetzsch@gmx.net>
 */

package de.poetzsch_heffter.networkautocompleteview;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.android.volley.toolbox.NetworkImageView;

/**
 * Created by David Poetzsch-Heffter on 04.03.15.
 */
public class LoadIndicatedNetworkAutoCompleteView<T extends NetworkAutoCompleteTextView<?>> extends LinearLayout {
    private T autoCompleteTextView;

    public LoadIndicatedNetworkAutoCompleteView(Context context) {
        super(context);
        init(null);
    }

    public LoadIndicatedNetworkAutoCompleteView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public void setHint(CharSequence hint) {
        getNetworkAutoCompleteTextView().setHint(hint);
    }

    public CharSequence getHint() {
        return getNetworkAutoCompleteTextView().getHint();
    }

    public T getNetworkAutoCompleteTextView() {
        return autoCompleteTextView;
    }

    protected int getLayoutResource() {
        return R.layout.load_indicated_network_autocomplete_view;
    }

    private void init(AttributeSet attrs) {
        LayoutInflater.from(getContext()).inflate(getLayoutResource(), this, true);

        autoCompleteTextView = (T) findViewById(R.id.network_autocomplete_view);
        getNetworkAutoCompleteTextView().setLoadingIndicator((ProgressBar) findViewById(R.id.load_indicator));

        if (attrs != null) {
            TypedArray arr = getContext().obtainStyledAttributes(attrs, R.styleable.LoadIndicatedNetworkAutoCompleteView);
            CharSequence hint = arr.getString(R.styleable.LoadIndicatedNetworkAutoCompleteView_hint);
            if (hint != null) {
                setHint(hint);
            }
            arr.recycle();
        }
    }
}
