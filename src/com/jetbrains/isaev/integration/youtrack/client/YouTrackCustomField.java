/**
 * @author: amarch
 */

package com.jetbrains.isaev.integration.youtrack.client;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.net.URL;
import java.util.Date;
import java.util.LinkedList;
import java.util.NoSuchElementException;

@XmlRootElement(name = "projectCustomField")
public class YouTrackCustomField {

    @XmlElement(name = "param")
    public LinkedList<CustomFieldParam> params;
    private String name;
    private String type;
    private URL fullURL;
    private String emptyText;
    private boolean canBeEmpty;
    private LinkedList<String> defaultValues;
    private YouTrackCustomFieldBundle bundle;

    @XmlAttribute(name = "name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @XmlAttribute(name = "type")
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @XmlAttribute(name = "url")
    public URL getFullURL() {
        return fullURL;
    }

    public void setFullURL(URL fullURL) {
        this.fullURL = fullURL;
    }

    @XmlAttribute(name = "emptyText")
    public String getEmptyText() {
        return emptyText;
    }

    public void setEmptyText(String emptyText) {
        this.emptyText = emptyText;
    }

    public YouTrackCustomFieldBundle findBundle() throws NoSuchElementException {
        for (CustomFieldParam param : params) {
            if ("bundle".equals(param.getName())) {
                this.setBundle(new YouTrackCustomFieldBundle(param.getValue()));
                bundle.setType(this.type);
                return bundle;
            }
        }
        throw new NoSuchElementException("No found bundles.");
    }

    public YouTrackCustomFieldBundle getBundle() {
        return bundle;
    }

    public void setBundle(YouTrackCustomFieldBundle bundle) {
        this.bundle = bundle;
    }

    public boolean isSingle() {
        return YouTrackCustomFieldType.getTypeByName(type).singleField();
    }

    @XmlAttribute(name = "canBeEmpty")
    public boolean isCanBeEmpty() {
        return canBeEmpty;
    }

    public void setCanBeEmpty(boolean canBeEmpty) {
        this.canBeEmpty = canBeEmpty;
    }

    @XmlElement(name = "defaultValue")
    public LinkedList<String> getDefaultValues() {
        return defaultValues;
    }

    public void setDefaultValues(LinkedList<String> defaultValues) {
        this.defaultValues = defaultValues;
    }



}
