package com.example.shuttlescore;

import android.content.Context;
import android.widget.ArrayAdapter;

import java.util.List;

public class FeedbackAdapter extends ArrayAdapter<String> {

    public FeedbackAdapter(Context context, List<String> feedbackList) {
        super(context, android.R.layout.simple_list_item_1, feedbackList);
    }
}

