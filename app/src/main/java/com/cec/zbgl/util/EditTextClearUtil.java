package com.cec.zbgl.util;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

public class EditTextClearUtil {
    public static void addclerListener(final EditText e1, final ImageView m1) {

        e1.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                // TODO Auto-generated method stub

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
                String s1 = s + "";
                if (s.length() > 0) {
                    m1.setVisibility(View.VISIBLE);
                } else {

                    m1.setVisibility(View.INVISIBLE);
                }

            }
        });

        m1.setOnClickListener((v)->{
            // TODO Auto-generated method stub
            e1.setText("");
        });

    }
}
