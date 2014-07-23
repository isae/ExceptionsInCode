/**
 @author: amarch
 */

package com.jetbrains.integration.youtrack.utils;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.LinkedList;

@XmlRootElement(name = "buildBundle")
public class BuildBundleValues extends BundleValues {

    @XmlElement(name = "build", type = BuildValue.class)
    private LinkedList<BuildValue> bundleValues;

    public LinkedList<BuildValue> getBuildValues() {
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
