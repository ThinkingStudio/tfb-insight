package com.pixolut.teb.model;

import act.Act;
import act.db.morphia.MorphiaQuery;
import org.osgl.util.E;

import java.util.List;
import java.util.Map;

/**
 * The test types.
 */
public enum TestType {
    db() {
        @Override
        public Map<String, List<Test.Result>> fetch(Test.Result.RawData rawData) {
            return rawData.db;
        }
    },
    fortune() {
        @Override
        public Map<String, List<Test.Result>> fetch(Test.Result.RawData rawData) {
            return rawData.fortune;
        }
    },
    json() {
        @Override
        public Map<String, List<Test.Result>> fetch(Test.Result.RawData rawData) {
            return rawData.json;
        }

        @Override
        public boolean isDbTest() {
            return false;
        }
    },
    plaintext() {
        @Override
        public Map<String, List<Test.Result>> fetch(Test.Result.RawData rawData) {
            return rawData.plaintext;
        }

        @Override
        public Test.Result pickOne(List<Test.Result> results) {
            return results.get(results.size() - 1);
        }

        @Override
        public boolean isDbTest() {
            return false;
        }
    },
    query() {
        @Override
        public Map<String, List<Test.Result>> fetch(Test.Result.RawData rawData) {
            return rawData.query;
        }
    },
    update() {
        @Override
        public Map<String, List<Test.Result>> fetch(Test.Result.RawData rawData) {
            return rawData.update;
        }

        @Override
        public Test.Result pickOne(List<Test.Result> results) {
            return results.get(results.size() - 1);
        }
    },
    density() {
        @Override
        public Map<String, List<Test.Result>> fetch(Test.Result.RawData rawData) {
            throw E.unsupport();
        }
    };

    public abstract Map<String, List<Test.Result>> fetch(Test.Result.RawData rawData);

    public MorphiaQuery<Project> applyTo(MorphiaQuery<Project> query) {
        query.orderBy("-tests.bestResult." + name() + ".totalRequests");
        return query;
    }

    public Test.Result pickOne(List<Test.Result> results) {
        return Test.Result.bestOf(results);
    }

    public boolean isDbTest() {
        return true;
    }

    public String label(Project project, Test test) {
        if (!isDbTest()) {
            return project.framework;
        }
        return project.framework + " with " + test.database;
    }

    public float densityWeight() {
        String s = (String) Act.appConfig().get("density." + name());
        return Float.parseFloat(s);
    }
}
