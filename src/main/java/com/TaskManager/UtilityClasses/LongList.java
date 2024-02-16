package com.TaskManager.UtilityClasses;

import java.util.List;

public class LongList {
    private List<Long> idList;

    public LongList(){}

    public LongList(List<Long> idList) {
        this.idList = idList;
    }

    public List<Long> getIdList() {
        return idList;
    }

    public void setIdList(List<Long> idList) {
        this.idList = idList;
    }
}
