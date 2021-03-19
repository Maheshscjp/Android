package com.example.login.util;

import android.text.InputFilter;
import android.text.Spanned;

public class InputValidation {


    public static InputFilter[] inputFilterAllowAlpha = {new InputFilter() {
        @Override
        public CharSequence filter(CharSequence cs, int start,
                                   int end, Spanned spanned, int dStart, int dEnd) {


            if (cs.toString().matches("[a-zA-Z ]+")) {
                return cs;
            }
            return "";
        }
    }
    };


    public static InputFilter[] inputFilterAllowAlphaCaps = {new InputFilter.AllCaps(), new InputFilter() {
        @Override
        public CharSequence filter(CharSequence cs, int start,
                                   int end, Spanned spanned, int dStart, int dEnd) {


            if (cs.toString().matches("[a-zA-Z ]+")) {
                return cs;
            }
            return "";
        }
    }};

    public static InputFilter[] inputFilterAllowAlphaNumCaps = {new InputFilter.AllCaps(), new InputFilter() {
        @Override
        public CharSequence filter(CharSequence cs, int start,
                                   int end, Spanned spanned, int dStart, int dEnd) {

            if (cs.toString().matches("[a-zA-Z0-9 ]+")) {
                return cs;
            }
            return "";
        }
    }};
}
