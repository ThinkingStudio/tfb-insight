package com.pixolut.teb.model;

import act.data.annotation.Data;
import act.util.SimpleBean;

@Data
public class TestConfig implements SimpleBean {
    public String name;
    public String json_url;
    public String db_url;
    public String query_url;
    public String fortune_url;
    public String update_url;
    public String plaintext_url;
    public String database;
    public String language;
    public String orm;
    public String os;
    public String classification;
}
