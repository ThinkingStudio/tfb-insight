package com.pixolut.teb.model;

import act.db.morphia.MorphiaAdaptiveRecord;
import act.db.morphia.MorphiaDao;
import act.util.Stateless;
import com.alibaba.fastjson.JSON;
import com.pixolut.teb.util.DensityCalculator;
import org.mongodb.morphia.annotations.Entity;
import org.osgl.util.C;
import org.osgl.util.E;
import org.osgl.util.S;

import java.io.File;
import java.util.*;

@Entity("prj")
public class Project extends MorphiaAdaptiveRecord<Project> {

    public enum Technology {
        JVM("Java", "Kotlin", "Scala", "Closure", "Groovy"), DOT_NET("C#"), OTHER;
        private Set<String> languages;
        Technology(String ... languages) {
            this.languages = C.setOf(languages);
        }

        public boolean testLanguage(String language) {
            if (languages.contains(language)) {
                return true;
            }
            if (languages.isEmpty()) {
                return !JVM.testLanguage(language) && !DOT_NET.testLanguage(language);
            }
            return false;
        }
    }

    public String framework;
    public Set<String> db;
    public List<Test> tests;
    public Test.Classification classification;
    public Technology technology;
    public int loc;
    public String language;
    public String projectRoot;
    public float density;

    public Project(BenchmarkConfig config, File projectRoot) {
        this.projectRoot = projectRoot.getAbsolutePath();
        this.init(config);
    }

    public void calculateDensity() {
        this.density = DensityCalculator.calculate(this);
    }

    public boolean hasEffectiveTests() {
        return !tests.isEmpty();
    }

    private void init(BenchmarkConfig config) {
        this.framework = config.framework;
        this.tests = new ArrayList<>();
        this.db = new HashSet<>();
        for (Map<String, Test> map : config.tests) {
            for (Map.Entry<String, Test> entry : map.entrySet()) {
                String testName = entry.getKey();
                Test test = entry.getValue();
                if (test.approach == Test.Approach.Stripped) {
                    // ignore stripped test
                    continue;
                }
                test.name = testName;
                this.tests.add(test);
                String database = test.database;
                if (S.notBlank(database) && !"none".equalsIgnoreCase(database)) {
                    this.db.add(database);
                }
            }
        }
        if (this.tests.isEmpty()) {
            return;
        }
        this.language = this.tests.get(0).language;
        if (null == this.language) {
            throw E.unexpected("Language not found for %s", JSON.toJSONString(config));
        }
        this.classification = this.tests.get(0).classification;
        this.technology = findTechnologyByLanguage(language);
    }

    private Technology findTechnologyByLanguage(String language) {
        for (Technology tech : Technology.values()) {
            if (tech.testLanguage(language)) {
                return tech;
            }
        }
        return Technology.OTHER;
    }

    @Stateless
    public static class Dao extends MorphiaDao<Project> {
    }


}
