/**
 @author: amarch
 */

package com.jetbrains.integration.youtrack.client;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.LinkedList;

@XmlRootElement(name = "projectCustomFieldRefs")
public class YouTrackCustomFieldsList {

    private LinkedList<YouTrackCustomField> customFields = new LinkedList<YouTrackCustomField>();

    @XmlElement(name = "projectCustomField", type = YouTrackCustomField.class)
    public LinkedList<YouTrackCustomField> getCustomFields() {
        return customFields;
    }

}
