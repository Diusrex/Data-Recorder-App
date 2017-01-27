package com.diusrex.sleepingdata;

import android.content.Context;
import android.util.Log;

public class Question {
    public static Question FromTypeDisplayString(String name, String type, Context context) {
        Log.w("INFO", "Trying " + type + " with display str");
        return new Question(name, getType(type, context));
    }
    
    public static Question FromTypeEnumValue(String name, String enumValue) {
        Log.w("INFO", "Trying " + enumValue + " with enumval");
        return new Question(name, Type.valueOf(enumValue));
    }

    private static Type getType(String type, Context context) {
        for (Type t : Type.values()) {
            if (context.getString(t.stringID).equals(type)) {
                return t;
            }
        }

        Log.e("INFO", "Didn't find a match for '" + type + "'");
        return Type.SINGLE_LINE;
    }

    public Question(String name) {
        this.name = name;
        this.type = Type.SINGLE_LINE;
    }

    private Question(String name, Type type) {
        this.name = name;
        this.type = type;
    }

    public final String name;

    public enum Type {
        SINGLE_LINE(R.string.question_type_single_line), PARAGRAPH(R.string.question_type_paragraph), TEN_SCALE(
                R.string.question_type_ten_scale), TIME(R.string.question_type_time), YES_NO(
                R.string.question_type_yes_no);

        private Type(int stringID) {
            this.stringID = stringID;
        }

        public final int stringID;
    };

    public final Type type;
}
