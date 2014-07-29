/**
 @author: amarch
 */

package com.jetbrains.isaev.integration.youtrack.utils;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.LinkedList;

@XmlRootElement(name = "stateBundle")
public class StateBundleValues extends BundleValues {

    @XmlElement(name = "state", type = StateValue.class)
    private LinkedList<StateValue> bundleValues;

    public LinkedList<StateValue> getStateValues() {
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
