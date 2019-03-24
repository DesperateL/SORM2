package com.xiaojiaqi.bean;


import java.util.List;
import java.util.Map;

/**
 * 存储表信息
 */
public class TableInfo {

    /**
     * 表名
     */
    private String tname;

    /**
     * 所有字段的信息
     */
    private Map<String,ColumnInfo> columns;

    /**
     * 唯一主键（目前我们只能处理表中有且只有一个主键的情况）
     */
    private ColumnInfo onlyPriKey;

    /**
     * 联合主键
     */
    private List<ColumnInfo> priKeys;   //联合主键

    public TableInfo(String tname, List<ColumnInfo> priKeys,Map<String, ColumnInfo> columns) {
        super();
        this.tname = tname;
        this.columns = columns;
        //this.onlyPriKey = onlyPriKey;
        this.priKeys = priKeys;
    }

    public TableInfo() {
    }

    public List<ColumnInfo> getPriKeys() {
        return priKeys;
    }

    public void setPriKeys(List<ColumnInfo> priKeys) {
        this.priKeys = priKeys;
    }

    public String getTname() {
        return tname;
    }

    public void setTname(String tname) {
        this.tname = tname;
    }

    public Map<String, ColumnInfo> getColumns() {
        return columns;
    }

    public void setColumns(Map<String, ColumnInfo> columns) {
        this.columns = columns;
    }

    public ColumnInfo getOnlyPriKey() {
        return onlyPriKey;
    }

    public void setOnlyPriKey(ColumnInfo onlyPriKey) {
        this.onlyPriKey = onlyPriKey;
    }
}
