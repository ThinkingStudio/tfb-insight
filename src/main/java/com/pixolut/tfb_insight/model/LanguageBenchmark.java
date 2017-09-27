package com.pixolut.tfb_insight.model;

import act.cli.Command;
import act.db.morphia.MorphiaAdaptiveRecord;
import act.db.morphia.MorphiaDao;
import org.mongodb.morphia.annotations.Entity;
import org.osgl.$;
import org.osgl.util.C;

import java.util.*;

@Entity("lang")
public class LanguageBenchmark extends MorphiaAdaptiveRecord<LanguageBenchmark> {

    public String language;
    public TestType test;
    public Float median;
    public List<Float> medianList;
    public Float top;
    public List<Float> topList;

    @Command(name = "calculate.lang", help = "calculate language benchmark")
    public static void calculateLanguageBenchmark() {
        Project.Dao projectDao = Project.dao();
        List<Project> projects = projectDao.findAllAsList();
        Map<$.T2<String, TestType>, List<List<Float>>> resultsMedians = new HashMap<>();
        Map<$.T2<String, TestType>, List<Float>> bestResultMedian = new HashMap<>();
        Map<$.T2<String, TestType>, List<Float>> resultsTops = new HashMap<>();
        Map<$.T2<String, TestType>, Float> bestResultTops = new HashMap<>();
        for (Project project : projects) {
            String language = project.language;

            Float density = project.density;
            if (null != density && density > 0) {
                $.T2<String, TestType> key = $.T2(language, TestType.density);
                List<Float> densities = bestResultMedian.get(key);
                if (null == densities) {
                    densities = C.newList(density);
                    bestResultMedian.put(key, densities);
                } else {
                    densities.add(density);
                }
                Float n = bestResultTops.get(key);
                if (null == n) {
                    n = density;
                } else {
                    n = Math.max(n.floatValue(), density);
                }
                bestResultTops.put(key, n);
            }

            for (Test test : project.tests) {
                for (Map.Entry<TestType, Test.Result> entry : test.bestResult.entrySet()) {
                    $.T2<String, TestType> key = $.T2(language, entry.getKey());
                    float throughput = entry.getValue().throughput();
                    List<Float> numbers = bestResultMedian.get(key);
                    if (null == numbers) {
                        numbers = C.newList(throughput);
                    } else {
                        numbers.add(throughput);
                    }
                    bestResultMedian.put(key, numbers);
                    Float n = bestResultTops.get(key);
                    if (null == n) {
                        n = throughput;
                    } else {
                        n = Math.max(n.intValue(), throughput);
                    }
                    bestResultTops.put(key, n);
                }
                for (Map.Entry<TestType, List<Test.Result>> entry : test.results.entrySet()) {
                    $.T2<String, TestType> key = $.T2(language, entry.getKey());
                    C.List<Float> throughputs = C.list(entry.getValue()).map((result) -> (float)result.throughput());
                    List<List<Float>> lists = resultsMedians.get(key);
                    if (null == lists) {
                        lists = C.newList();
                        for (Float f : throughputs) {
                            lists.add(C.newList(f));
                        }
                        resultsMedians.put(key, lists);
                    } else {
                        for (int i = 0; i < throughputs.size(); ++i) {
                            lists.get(i).add(throughputs.get(i));
                        }
                    }
                    List<Float> numList = resultsTops.get(key);
                    if (null == numList) {
                        numList = new ArrayList<>();
                        numList.addAll(throughputs);
                        resultsTops.put(key, numList);
                    } else {
                        for (int i = 0; i < throughputs.size(); ++i) {
                            numList.set(i, Math.max(numList.get(i).intValue(), throughputs.get(i)));
                        }
                    }
                }
            }
        }

        Dao dao = dao();
        dao.drop();
        Map<$.T2<String, TestType>, LanguageBenchmark> benchmarkMap = new HashMap<>();
        for (Map.Entry<$.T2<String, TestType>, List<Float>> entry : bestResultMedian.entrySet()) {
            LanguageBenchmark lb = new LanguageBenchmark();
            lb.language = entry.getKey()._1;
            lb.test = entry.getKey()._2;
            List<Float> numbers = entry.getValue();
            lb.median = median(numbers);
            benchmarkMap.put(entry.getKey(), lb);
            dao.save(lb);
        }
        for (Map.Entry<$.T2<String, TestType>, List<List<Float>>> entry : resultsMedians.entrySet()) {
            LanguageBenchmark lb = benchmarkMap.get(entry.getKey());
            List<List<Float>> medianList = entry.getValue();
            lb.medianList = new ArrayList<>(medianList.size());
            for (List<Float> floats : medianList) {
                lb.medianList.add(median(floats));
            }
            dao.save(lb);
        }

        for (Map.Entry<$.T2<String, TestType>, Float> entry : bestResultTops.entrySet()) {
            LanguageBenchmark lb = benchmarkMap.get(entry.getKey());
            lb.top = entry.getValue().floatValue();
            dao.save(lb);
        }
        for (Map.Entry<$.T2<String, TestType>, List<Float>> entry : resultsTops.entrySet()) {
            LanguageBenchmark lb = benchmarkMap.get(entry.getKey());
            List<Float> floats = new ArrayList<>();
            for (Float n : entry.getValue()) {
                floats.add(n.floatValue());
            }
            lb.topList = floats;
            dao.save(lb);
        }
    }

    public static class Dao extends MorphiaDao<LanguageBenchmark> {
    }

    private static Float median(List<Float> numbers) {
        Collections.sort(numbers);
        int len = numbers.size();
        if (len % 2 == 0)
            return (numbers.get(len/2) + numbers.get(len/2 - 1))/2;
        else
            return numbers.get(len/2);
    }
}
