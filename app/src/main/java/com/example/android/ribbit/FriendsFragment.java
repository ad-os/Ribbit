package com.example.android.ribbit;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by adhyan on 7/7/15.
 */
public class FriendsFragment extends ListFragment{

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //analogous to setContentView method in the Activity.
        View rootView = inflater.inflate(R.layout.fragment_friends, container, false);
        return rootView;
    }


}
