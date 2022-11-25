package com.cefriel.util;

import java.util.Collections;
import java.util.List;

public class ChimeraResourcesBean {
    private List<ChimeraResourceBean> resources;

    public  ChimeraResourcesBean() {
        resources = Collections.emptyList();
    }

    public ChimeraResourcesBean(List<ChimeraResourceBean> resources) {
        this.resources = resources;
    }

    public List<ChimeraResourceBean> getResources() {
        return resources;
    }

    public void setResources(List<ChimeraResourceBean> resources) {
        this.resources = resources;
    }
}
