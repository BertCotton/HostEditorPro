package com.treb.hosts.pro.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import com.treb.hosts.pro.R;
import com.treb.hosts.pro.events.Event;
import com.treb.hosts.pro.events.EventBus;

public class SearchFragment extends Fragment {

    private final EventBus bus = EventBus.getInstance();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.search, container, false);

        final EditText searchBox = (EditText)view.findViewById(R.id.search_term);

        Button btnSearch = (Button)view.findViewById(R.id.search_okButton);

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bus.fireEvent(Event.SEARCH_GO, searchBox.getText().toString());
                ((InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(searchBox.getWindowToken(), 0);
            }
        });

        Button cancelSearch = (Button)view.findViewById(R.id.search_cancelButton);
        cancelSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bus.fireEvent(Event.SEARCH_CANCEL);
                ((InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(searchBox.getWindowToken(), 0);
            }
        });
        return view;
    }
}
