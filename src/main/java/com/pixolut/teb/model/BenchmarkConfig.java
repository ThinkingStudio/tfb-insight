package com.pixolut.teb.model;

import act.data.annotation.Data;
import act.util.SimpleBean;

import java.util.List;
import java.util.Map;

@Data
public class BenchmarkConfig implements SimpleBean {

    public String framework;
    public List<Map<String, Test>> tests;

}
