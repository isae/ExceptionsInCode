package com.jetbrains.isaev.integration.youtrack.utils;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "userGroup")
public class UserGroupValue extends BundleValue {

    @XmlAttribute(name = "name")
    private String value;

    public String getValue() {
        return value;
    }
}
