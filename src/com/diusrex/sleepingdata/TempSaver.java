package com.diusrex.sleepingdata;

import com.google.common.base.Joiner;

public class TempSaver {

    private final static String tempSaveSeparator = ", ";

    // The purpose of this is to avoid having extra data be deleted
    private final static String extraAddedToEnd = "~";

    public static String[] split(String data) {
        if (data.equals(""))
            return new String[0];

        String[] asArray = data.split(tempSaveSeparator);

        // This item has been effected by extraAddedToEnd
        String finalItem = asArray[asArray.length - 1];
        finalItem = finalItem.substring(0, finalItem.length() - extraAddedToEnd.length());

        asArray[asArray.length - 1] = finalItem;
        return asArray;
    }

    public static String join(String[] data) {
        if (data.length == 0)
            return "";

        // To help with saving
        data[data.length - 1] += extraAddedToEnd;

        return Joiner.on(tempSaveSeparator).join(data);
    }
}
