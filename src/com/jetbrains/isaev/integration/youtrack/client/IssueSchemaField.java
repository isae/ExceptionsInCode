/**
 * @author: Alexander Marchuk
 */

package com.jetbrains.isaev.integration.youtrack.client;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import java.util.LinkedList;

public class IssueSchemaField {

    public static final String TYPE_LINK_FIELD = "LinkField";

    public static final String TYPE_SINGLE_FIELD = "SingleField";

    public static final String TYPE_CUSTOM_FIELD = "CustomField";

    public static final String TYPE_CUSTOM_FIELD_SINCE_5_1 = "CustomFieldValue";

    public static final String TYPE_MULTIUSER_FIELD = "MultiUserField";

    @XmlElement(name = "value")
    private LinkedList<IssueSchemaValue> values;

    @XmlAttribute(name = "name")
    private String name;

    @XmlAttribute(name = "type", namespace = "http://www.w3.org/2001/XMLSchema-instance")
    private String type;

    public LinkedList<IssueSchemaValue> getValues() {
        return values;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public LinkedList<String> getStringValues() {
        LinkedList<String> stringValues = new LinkedList<String>();
        for (IssueSchemaValue value : values) {
            stringValues.add(value.getValue());
        }
        return stringValues;
    }
}
