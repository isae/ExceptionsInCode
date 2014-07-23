/**
 @author: amarch
 */

package com.jetbrains.integration.youtrack.utils;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.LinkedList;

@XmlRootElement(name = "savedSearches")
public class SavedSearches {

    private LinkedList<SavedSearch> searches;

    @XmlElement(name = "search")
    public LinkedList<SavedSearch> getSearches() {
        return searches;
    }

    public void setSearches(LinkedList<SavedSearch> searches) {
        this.searches = searches;
    }

    public LinkedList<String> getSearchTexts() {
        LinkedList<String> texts = new LinkedList<String>();
        for (SavedSearch search : searches) {
            texts.add(search.getSearchText());
        }
        return texts;
    }

    public LinkedList<String> getSearchNames() {
        LinkedList<String> names = new LinkedList<String>();
        for (SavedSearch search : searches) {
            names.add(search.getName());
        }
        return names;
    }
}
