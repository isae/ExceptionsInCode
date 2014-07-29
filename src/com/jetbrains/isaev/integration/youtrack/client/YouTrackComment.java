/**
 @author: Alexander Marchuk
 */

package com.jetbrains.isaev.integration.youtrack.client;

import javax.xml.bind.annotation.XmlAttribute;
import java.util.Date;

public class YouTrackComment {

    private String text;

    @XmlAttribute(name = "author")
    private String author;

    private String id;

    private String created;

    @XmlAttribute(name = "text")
    //  @Produces(MediaType.APPLICATION_XML + ";charset=utf-8")
    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getAuthorName() {
        return author;
    }

    public void setAuthorName(String author) {
        this.author = author;
    }

    @XmlAttribute(name = "id")
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @XmlAttribute(name = "created")
    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public Date getCreationDate() {
        return new Date(Long.parseLong(created));
    }

}