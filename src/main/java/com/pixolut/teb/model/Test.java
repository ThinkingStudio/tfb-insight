package com.pixolut.teb.model;

import act.app.conf.AutoConfig;
import act.data.annotation.Data;
import act.util.SimpleBean;
import org.osgl.$;
import org.osgl.util.C;
import org.osgl.util.Const;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@AutoConfig
public class Test implements SimpleBean {

    /**
     * The test types.
     */
    public enum Type {
        db() {
            @Override
            public Map<String, List<Result>> fetch(Result.RawData rawData) {
                return rawData.db;
            }
        },
        fortune() {
            @Override
            public Map<String, List<Result>> fetch(Result.RawData rawData) {
                return rawData.fortune;
            }
        },
        json() {
            @Override
            public Map<String, List<Result>> fetch(Result.RawData rawData) {
                return rawData.json;
            }
        },
        plaintext() {
            @Override
            public Map<String, List<Result>> fetch(Result.RawData rawData) {
                return rawData.plaintext;
            }
        },
        query() {
            @Override
            public Map<String, List<Result>> fetch(Result.RawData rawData) {
                return rawData.query;
            }
        },
        update() {
            @Override
            public Map<String, List<Result>> fetch(Result.RawData rawData) {
                return rawData.update;
            }
        };

        public abstract Map<String, List<Result>> fetch(Result.RawData rawData);
    }

    /**
     * The time duration on seconds for a test.
     */
    public static final Const<Integer> TEST_DURATION = $.constant(15);

    /**
     * Test concurrency level.
     */
    public static final List<Integer> CONCURRENCY_LEVEL = C.list(8, 16, 32, 64, 128, 256);

    /**
     * PlaintText test copncurrency level.
     */
    public static final List<Integer> PLAINTEXT_CONCURRENCY_LEVEL = C.list(256, 1024, 4096, 16384);

    @Data
    public static class Result implements SimpleBean, Comparable<Result> {

        /**
         * The wrapper class used to fetch raw data from TEB.
         *
         * There are the following tests result listed:
         * 1. db
         * 2. fortune
         * 3. json
         * 4. plaintext
         * 5. query
         * 6. update
         *
         * For each test, it contains a map of test results:
         * * key - framework test name
         * * value - list of results indexed by concurrency level
         */
        public static class RawData {
            public Map<String, List<Result>> db;
            public Map<String, List<Result>> fortune;
            public Map<String, List<Result>> json;
            public Map<String, List<Result>> plaintext;
            public Map<String, List<Result>> query;
            public Map<String, List<Result>> update;
        }

        /**
         * The wrapper class used to fetch report from TEB
         */
        public static class RawReport {
            public String name;
            public RawData rawData;
        }

        public String latencyAvg;
        public String latencyMax;
        public int totalRequests;

        @Override
        public int compareTo(Result o) {
            return o.totalRequests - totalRequests;
        }

        /**
         * Number of requests processed per seconds
         *
         * @return
         *      the throughput, ie. number of requests processed in seconds
         */
        public int throughput() {
            return totalRequests / TEST_DURATION.get();
        }

        /**
         * Find the best results from a list of results
         * @param results
         *      A list of results
         * @return
         *      The best one in the list
         */
        public static Result bestOf(List<Result> results) {
            List<Result> copy = C.newList(results).sorted();
            return copy.get(0);
        }
    }

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

    public Map<Type, List<Result>> results = new HashMap<>();
    public Map<Type, Result> bestResult = new HashMap<>();
}
