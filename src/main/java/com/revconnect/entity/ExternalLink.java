package com.revconnect.entity;

import jakarta.persistence.Embeddable;

@Embeddable
public class ExternalLink {
    private String label;
    private String url;

    public ExternalLink() {
    }

    public ExternalLink(String label, String url) {
        this.label = label;
        this.url = url;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
