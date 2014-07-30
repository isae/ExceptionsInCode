/**
 * @author: amarch
 */

package com.jetbrains.isaev.integration.youtrack.client;

import com.jetbrains.isaev.integration.youtrack.client.YouTrackCustomField.YouTrackCustomFieldType;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;

@XmlRootElement(name = "project")
public class YouTrackProject {

    private String projectFullName;

    private String projectShortName;
    private HashMap<String, YouTrackCustomField> customFieldsMap =
            new HashMap<String, YouTrackCustomField>();
    private Date customFieldsUpdatedDate;
    private boolean customFieldsUpdated = false;

    public static String getShortNameFromBoth(String both) {
        if (both.contains("[") && both.contains("]")) {
            return both.substring(both.indexOf("[") + 1, both.indexOf("]"));
        } else {
            return both;
        }
    }

    @XmlAttribute(name = "name")
    public String getProjectFullName() {
        return projectFullName;
    }

    public void setProjectFullName(String projectFullName) {
        this.projectFullName = projectFullName;
    }

    @XmlAttribute(name = "shortName")
    public String getProjectShortName() {
        return projectShortName;
    }

    public void setProjectShortName(String projectShortName) {
        this.projectShortName = projectShortName;
    }

    public HashMap<String, YouTrackCustomField> getCustomFieldsMap() {
        return customFieldsMap;
    }

    public void setCustomFieldsMap(HashMap<String, YouTrackCustomField> customFields) {
        customFieldsMap = customFields;
    }

    public Collection<YouTrackCustomField> getCustomFields() {
        return customFieldsMap.values();
    }

    public void addCustomField(YouTrackCustomField field) {
        if (customFieldsMap != null) {
            customFieldsMap.put(field.getName(), field);
        } else {
            customFieldsMap = new HashMap<String, YouTrackCustomField>();
            customFieldsMap.put(field.getName(), field);
        }
    }

    public boolean isCustomFieldsUpdated() {
        return customFieldsUpdated;
    }

    public void setCustomFieldsUpdated(boolean customFieldsUpdated) {
        this.customFieldsUpdated = customFieldsUpdated;
    }

    public Date getCustomFieldsUpdatedDate() {
        return customFieldsUpdatedDate;
    }

    public void setCustomFieldsUpdatedDate(Date customFieldsUpdatedDate) {
        this.customFieldsUpdatedDate = customFieldsUpdatedDate;
    }

    public synchronized void updateCustomFields(final YouTrackClient client) {

        if (client != null) {

            setCustomFieldsMap(new HashMap<String, YouTrackCustomField>());
            if (projectShortName != null && !projectShortName.equals("")) {
                for (YouTrackCustomField field : client.getProjectCustomFields(projectShortName)) {
                    YouTrackCustomField fullField =
                            client.getProjectCustomField(projectShortName, field.getName());

                    if (!YouTrackCustomFieldType.getTypeByName(fullField.getType()).isSimple()) {
                        fullField.findBundle();
                        fullField.getBundle().setBundleValues(
                                fullField.getBundle().getBundleValuesFromClient(client));
                    }
                    addCustomField(fullField);
                }
            } else {
                updateCustomFields(client);
            }

            setCustomFieldsUpdated(true);
            setCustomFieldsUpdatedDate(new Date());
        }
    }

    public String getBothNames() {
        if (getProjectFullName() != null) {
            if (getProjectShortName() != null) {
                return getProjectFullName() + " [" + getProjectShortName() + "]";
            } else {
                return getProjectFullName();
            }
        } else {
            return "";
        }
    }

    public boolean isCustomField(String field) {
        return getCustomFieldsMap().keySet().contains(field);
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        YouTrackProject that = (YouTrackProject) o;

        if (projectFullName != null ? !projectFullName.equals(that.projectFullName) : that.projectFullName != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        return projectFullName != null ? projectFullName.hashCode() : 0;
    }
}
