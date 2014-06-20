package com.diusrex.sleepingdata;


interface ChangeInfo
{
    public int loadFromArray(String[] words, int position);
    public String[] applyToData(String[] data);
}

class AddData implements ChangeInfo
{
    public static final String IDENTIFIER = "ADD";
    
    AddData()
    {
    }
    
    AddData(String addToData, int position)
    {
        this.addToData = addToData;
        this.position = position;
    }
    
    String addToData;
    int position;
    
    @Override
    public String[] applyToData(String[] data)
    {
        String[] newData = new String[data.length + 1];
        
        for (int i = 0; i < position; ++i) {
            newData[i] = data[i];
        }
        
        newData[position] = addToData;
        
        for (int i = position; i < data.length; ++i) {
            newData[i + 1] = data[i];
        }
        
        return newData;
    }
    
    @Override
    public int loadFromArray(String[] words, int positionInArray) {
        addToData = words[positionInArray++];
        position = Integer.parseInt(words[positionInArray++]);
        
        return positionInArray;
    }
    
    @Override
    public String toString()
    {
        return IDENTIFIER + " " + addToData + " " + position;
    }
}

class DeleteData implements ChangeInfo
{
    public static final String IDENTIFIER = "DELETE";
    
    DeleteData()
    {
    }
    
    DeleteData(int position)
    {
        this.position = position;
    }
    
    int position;
    
    @Override
    public int loadFromArray(String[] words, int position) {
        this.position = Integer.parseInt(words[position++]);
        return position;
    }

    @Override
    public String[] applyToData(String[] data)
    {
        String[] newData = new String[data.length - 1];
        
        for (int i = 0; i < position; ++i) {
            newData[i] = data[i];
        }
        
        for (int i = position + 1; i < data.length; ++i) {
            newData[i - 1] = data[i];
        }
        
        return newData;
    }
    
    @Override
    public String toString()
    {
        return IDENTIFIER + " " + position;
    }
}