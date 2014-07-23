/**
 @author: amarch
 */

package com.jetbrains.integration.youtrack.utils;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.LinkedList;

@XmlRootElement(name = "ownedFieldBundle")
public class OwnedFieldBundleValues extends BundleValues {

    @XmlElement(name = "ownedField", type = OwnedFieldValue.class)
    public LinkedList<OwnedFieldValue> bundleValues;

    public LinkedList<OwnedFieldValue> getOwnedFields() {
        return bundleValues;
    }

    @Override
    public LinkedList<String> getValues() {
        LinkedList<String> values = new LinkedList<String>();
        if (bundleValues != null) {
            for (BundleValue value : bundleValues) {
                values.add(value.getValue());
            }
        }
        return values;
    }
}
