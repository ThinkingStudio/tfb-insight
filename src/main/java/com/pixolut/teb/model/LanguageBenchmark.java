package com.pixolut.teb.model;

import act.cli.Command;
import act.db.morphia.MorphiaAdaptiveRecord;
import act.db.morphia.MorphiaDao;
import org.mongodb.morphia.annotations.Entity;
import org.osgl.$;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Entity("lang")
public class LanguageBenchmark extends MorphiaAdaptiveRecord<LanguageBenchmark> {

    public String language;
    public TestType test;
    public Float avg;
    public Float top;

    @Command(name = "calculate.lang", help = "calculate language benchmark")
    public static void calculateLanguageBenchmark() {
        Project.Dao projectDao = Project.dao();
        List<Project> projects = projectDao.findAllAsList();
        Map<$.T2<String, TestType>, $.T2<Number, Integer>> avgs = new HashMap<>();
        Map<$.T2<String, TestType>, Number> tops = new HashMap<>();
        for (Project project : projects) {
            String language = project.language;

            Float density = project.density;
            if (null != density && density > 0) {
                $.T2<String, TestType> key = $.T2(language, TestType.density);
                $.T2<Number, Integer> t2 = avgs.get(key);
                if (null == t2) {
                    t2 = $.T2(density, 1);
                } else {
                    t2 = $.T2(t2._1.floatValue() + density, t2._2 + 1);
                }
                avgs.put(key, t2);
                Number n = tops.get(key);
                if (null == n) {
                    n = density;
                } else {
                    n = Math.max(n.floatValue(), density);
                }
                tops.put(key, n);
            }

            for (Test test : project.tests) {
                for (Map.Entry<TestType, Test.Result> entry : test.bestResult.entrySet()) {
                    $.T2<String, TestType> key = $.T2(language, entry.getKey());
                    int value = entry.getValue().throughput();
                    $.T2<Number, Integer> t2 = avgs.get(key);
                    if (null == t2) {
                        t2 = $.T2(value, 1);
                    } else {
                        t2 = $.T2(t2._1.intValue() + value, t2._2 + 1);
                    }
                    avgs.put(key, t2);
                    Number n = tops.get(key);
                    if (null == n) {
                        n = value;
                    } else {
                        n = Math.max(n.intValue(), value);
                    }
                    tops.put(key, n);
                }
            }
        }

        Dao dao = dao();
        dao.drop();
        Map<$.T2<String, TestType>, LanguageBenchmark> benchmarkMap = new HashMap<>();
        for (Map.Entry<$.T2<String, TestType>, $.T2<Number, Integer>> entry : avgs.entrySet()) {
            LanguageBenchmark lb = new LanguageBenchmark();
            lb.language = entry.getKey()._1;
            lb.test = entry.getKey()._2;
            $.T2<Number, Integer> t2 = entry.getValue();
            Number num = t2._1;
            if (num instanceof Integer) {
                lb.avg = (float) (num.intValue() / t2._2);
            } else {
                lb.avg = num.floatValue() / t2._2;
            }
            benchmarkMap.put(entry.getKey(), lb);
        }

        for (Map.Entry<$.T2<String, TestType>, Number> entry : tops.entrySet()) {
            LanguageBenchmark lb = benchmarkMap.get(entry.getKey());
            lb.top = entry.getValue().floatValue();
            dao.save(lb);
        }
    }

    public static class Dao extends MorphiaDao<LanguageBenchmark> {
    }

}
