package com.shag;

import java.util.HashMap;
import java.util.Map;

public class Driver
{
    private String id;
    private String name;
    private String secondName;
    private String thirdName;
    private String way;
    private String dateOfCreating;

    public Driver() {
    }

    public Driver(String id, String name, String secondName, String thirdName, String way) {
        this.id = id;
        this.name = name;
        this.secondName = secondName;
        this.thirdName = thirdName;
        this.way = way;
    }

    public String getId() { return id; }

    public void setId(String id) { this.id = id; }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public String getSecondName() { return secondName; }

    public void setSecondName(String secondName) { this.secondName = secondName; }

    public String getThirdName() { return thirdName; }

    public void setThirdName(String thirdName) { this.thirdName = thirdName; }

    public String getWay() { return way; }

    public void setWay(String way) { this.way = way; }

    public String getDateOfCreating() { return dateOfCreating; }

    public void setDateOfCreating(String dateOfCreating) { this.dateOfCreating = dateOfCreating; }

    public Map<String, Object> toMap()
    {
        HashMap<String, Object> result = new HashMap<>();
        result.put("name", name);
        result.put("secondName", secondName);
        result.put("thirdName", thirdName);
        result.put("id", id);
        result.put("way", way);
        return result;
    }
}
