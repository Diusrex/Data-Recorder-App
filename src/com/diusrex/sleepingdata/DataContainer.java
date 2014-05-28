package com.diusrex.sleepingdata;

import java.util.ArrayList;
import java.util.List;

import android.util.Log;

public class DataContainer {
	public int positionInDataReached;
	public List<String> itemsLoaded;
	public String[] oldItems;
	
	DataContainer()
	{
	    positionInDataReached = 0;
		itemsLoaded = new ArrayList<String>();
		oldItems = new String[0];
	}
	
	DataContainer(String line)
    {
	    Log.w("DataContainer", "String is " + line);
	    oldItems = line.split(", ");
        positionInDataReached = oldItems.length;
        itemsLoaded = new ArrayList<String>();
    }
	
	void AddInput(String input)
	{
	    itemsLoaded.add(input);
	    ++positionInDataReached;
	}
}
