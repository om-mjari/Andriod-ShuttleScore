package com.example.shuttlescore;

import static androidx.core.app.ActivityCompat.invalidateOptionsMenu;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Fragmentotp#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Fragmentotp extends Fragment {

    Button btnlogin;
    EditText loginotp;
    String receivedOtp,rotp,contact;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public Fragmentotp() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment otp.
     */
    // TODO: Rename and change types and number of parameters
    public static Fragmentotp newInstance(String param1, String param2) {
        Fragmentotp fragment = new Fragmentotp();
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

        View vn = inflater.inflate(R.layout.fragment_otp, container, false);
        btnlogin = vn.findViewById(R.id.btnlogin);
        loginotp = vn.findViewById(R.id.loginotp);

        if (getArguments() != null) {
            receivedOtp = getArguments().getString("otp_key"); // Default 0 if missing
        }
        Bundle bb = getArguments();
        rotp = bb.getString("otp_key");
        contact = bb.getString("contact");
        onclick();
        return vn;

    }
    private void onclick(){
        btnlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String otp = loginotp.getText().toString();
                if(otp.isEmpty()){
                    Toast.makeText(getContext(),"Enter OTP",Toast.LENGTH_SHORT).show();
                }else{
                    if(otp.equals(rotp)) {
                        SharedPreferences sh = requireActivity().getSharedPreferences("login", Context.MODE_PRIVATE);
                        SharedPreferences.Editor ed = sh.edit();

                        ed.putString("contact",contact);
                        ed.commit();
                        Intent i = new Intent(requireActivity(),MainActivity.class);
                        startActivity(i);
                    }else {
                        Toast.makeText(getContext(),"Invalid OTP",Toast.LENGTH_SHORT).show();
                    }
                }
            }
            });

   }
}