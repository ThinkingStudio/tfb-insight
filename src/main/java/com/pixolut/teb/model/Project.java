package com.pixolut.teb.model;

import act.db.morphia.MorphiaAdaptiveRecord;
import act.db.morphia.MorphiaDao;
import act.util.Stateless;
import com.alibaba.fastjson.JSON;
import org.mongodb.morphia.annotations.Entity;
import org.osgl.util.E;
import org.osgl.util.S;

import java.io.File;
import java.util.*;

@Entity("prj")
public class Project extends MorphiaAdaptiveRecord<Project> {

    public String framework;
    public Set<String> db;
    public List<TestConfig> tests;
    public int loc;
    public String language;
    public String projectRoot;

    public Project(BenchmarkConfig config, File projectRoot) {
        this.projectRoot = projectRoot.getAbsolutePath();
        this.init(config);
    }

    private void init(BenchmarkConfig config) {
        this.framework = config.framework;
        this.tests = new ArrayList<>();
        this.db = new HashSet<>();
        for (Map<String, TestConfig> map : config.tests) {
            TestConfig testConfig = map.values().iterator().next();
            String testName = map.keySet().iterator().next();
            testConfig.name = testName;
            this.tests.add(testConfig);
            String database = testConfig.database;
            if (S.notBlank(database)) {
                this.db.add(database);
            }
        }
        this.language = this.tests.get(0).language;
        if (null == this.language) {
            throw E.unexpected("Language not found for %s", JSON.toJSONString(config));
        }
    }

    @Stateless
    public static class Service extends MorphiaDao<Project> {
    }


}
