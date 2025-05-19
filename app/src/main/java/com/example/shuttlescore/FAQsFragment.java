package com.example.shuttlescore;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FAQsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FAQsFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public FAQsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FAQsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FAQsFragment newInstance(String param1, String param2) {
        FAQsFragment fragment = new FAQsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_f_a_qs, container, false);

        LinearLayout q1layout = view.findViewById(R.id.q1layout);
        TextView answerQ1 = view.findViewById(R.id.answer_q1);
        ImageView iconQ1 = view.findViewById(R.id.icon_q1);

        LinearLayout q2layout = view.findViewById(R.id.q2layout);
        TextView answerQ2 = view.findViewById(R.id.answer_q2);
        ImageView iconQ2 = view.findViewById(R.id.icon_q2);

        LinearLayout q3layout = view.findViewById(R.id.q3layout);
        TextView answerQ3 = view.findViewById(R.id.answer_q3);
        ImageView iconQ3 = view.findViewById(R.id.icon_q3);

        LinearLayout q4layout = view.findViewById(R.id.q4layout);
        TextView answerQ4 = view.findViewById(R.id.answer_q4);
        ImageView iconQ4 = view.findViewById(R.id.icon_q4);


        // Set Click Listener
        q1layout.setOnClickListener(v -> {
            if (answerQ1.getVisibility() == View.VISIBLE) {
                answerQ1.setVisibility(View.GONE);
                iconQ1.setImageResource(android.R.drawable.arrow_down_float);
            } else {
                answerQ1.setVisibility(View.VISIBLE);
                iconQ1.setImageResource(android.R.drawable.arrow_up_float);
            }
        });
        q2layout.setOnClickListener(v -> {
            if (answerQ2.getVisibility() == View.VISIBLE) {
                answerQ2.setVisibility(View.GONE);
                iconQ2.setImageResource(android.R.drawable.arrow_down_float);
            } else {
                answerQ2.setVisibility(View.VISIBLE);
                iconQ2.setImageResource(android.R.drawable.arrow_up_float);
            }
        });

        q3layout.setOnClickListener(v -> {
            if (answerQ3.getVisibility() == View.VISIBLE) {
                answerQ3.setVisibility(View.GONE);
                iconQ3.setImageResource(android.R.drawable.arrow_down_float);
            } else {
                answerQ3.setVisibility(View.VISIBLE);
                iconQ3.setImageResource(android.R.drawable.arrow_up_float);
            }
        });

        q4layout.setOnClickListener(v -> {
            if (answerQ4.getVisibility() == View.VISIBLE) {
                answerQ4.setVisibility(View.GONE);
                iconQ4.setImageResource(android.R.drawable.arrow_down_float);
            } else {
                answerQ4.setVisibility(View.VISIBLE);
                iconQ4.setImageResource(android.R.drawable.arrow_up_float);
            }
        });
        return view;
    }
}