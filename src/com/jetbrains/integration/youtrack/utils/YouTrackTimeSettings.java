package com.jetbrains.integration.youtrack.utils;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "timesettings")
public class YouTrackTimeSettings {

    @XmlElement(name = "hoursADay")
    private int hoursPerDay;
    @XmlElement(name = "daysAWeek")
    private int daysInWeek;

    public int getHoursSettings() {
        return hoursPerDay;
    }

    public int getDaysSettings() {
        return daysInWeek;
    }


}
