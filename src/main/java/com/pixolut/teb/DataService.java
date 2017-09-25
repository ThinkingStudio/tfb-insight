package com.pixolut.teb;

import act.controller.annotation.UrlContext;
import act.db.morphia.MorphiaQuery;
import act.inject.DefaultValue;
import com.pixolut.teb.model.*;
import com.pixolut.teb.util.ColorCaculator;
import org.osgl.$;
import org.osgl.mvc.annotation.GetAction;
import org.osgl.util.C;
import org.osgl.util.S;

import java.util.*;
import java.util.regex.Pattern;
import javax.inject.Inject;
import javax.validation.constraints.NotNull;

/**
 * Provides data service for UI.
 */
@UrlContext("api/v1")
public class DataService {

    @Inject
    private Project.Dao projectDao;

    /**
     * Get framework list indexed by language
     * @return
     *      framework list indexed by language
     */
    @GetAction("framework")
    public Map<String, SortedSet<String>> frameworkListByLanguage() {
        SortedMap<String, SortedSet<String>> retVal = new TreeMap<>((o1, o2) -> {
                if (o1.equals("Others")) {
                    return 1;
                } else if (o2.equals("Others")) {
                    return -1;
                }
                return o1.compareTo(o2);
        });
        Map<String, Integer> counter = new HashMap<>();
        for (Project project : projectDao.q()) {
            String language = project.language;
            SortedSet<String> frameworks = retVal.computeIfAbsent(language, k -> new TreeSet<>());
            frameworks.add(project.framework);
            int count = counter.computeIfAbsent(language, k -> 0);
            counter.put(language, ++count);
        }
        SortedSet<String> others = new TreeSet<>();
        Set<String> toBeRemoved = new HashSet<>();
        for (Map.Entry<String, SortedSet<String>> entry : retVal.entrySet()) {
            SortedSet<String> list = entry.getValue();
            if (list.size() < 5) {
                toBeRemoved.add(entry.getKey());
                others.addAll(list);
            }
        }
        if (!others.isEmpty()) {
            for (String key : toBeRemoved) {
                retVal.remove(key);
            }
            retVal.put("Others", others);
        }
        return retVal;
    }

    /**
     * Query chart data for framework benchmark
     *
     * @return
     *      chart framework benchmark data
     */
    @GetAction("chart/framework")
    public ChartData frameworkBenchmark(
            @NotNull TestType test,
            String language,
            String technology,
            Test.Classification classification,
            String orm,
            @DefaultValue("20") Integer top
    ) {
        MorphiaQuery<Project> query = projectDao.q();
        query = test.applyTo(query);
        if (S.notBlank(language)) {
            query.filter("language", language);
        } else if (S.notBlank(technology)) {
            query.filter("technology", technology);
        }
        if (null != classification) {
            query.filter("classification", classification);
        }
        if (S.notBlank(orm)) {
            query.filter("tests.orm", Pattern.compile(orm, Pattern.CASE_INSENSITIVE));
        }
        query.limit(top);
        C.List<Project> projects = C.list(query.fetchAsList());
        if (test == TestType.density) {
            return codeDensity(projects);
        }
        SortedMap<Number, $.T2<String, String>> chartData = new TreeMap<>(Collections.reverseOrder());

        for (Project project : projects) {
            $.T2<Test, Test.Result> best = project.bestOf(test);
            if (null == best._2) {
                continue;
            }
            String backgroundColor = best._1.color;
            String label = test.label(project, best._1);
            chartData.put(best._2.throughput(), $.T2(label, backgroundColor));
        }
        ChartData data = new ChartData(C.list(chartData.values()).map((t2) -> t2._1));
        data.datasets = new ArrayList<>();
        ChartData.Dataset dataset = new ChartData.Dataset("", C.list(chartData.keySet()), C.list(chartData.values()).map((t2) -> t2._2));
        data.datasets.add(dataset);
        return data;
    }

    /**
     * Query chart data for language benchmark
     *
     * @return
     *      chart language benchmark data
     */
    @GetAction("chart/language")
    public ChartData languageBenchmark(
            @NotNull TestType test,
            LanguageBenchmark.Dao dao
    ) {
        C.List<LanguageBenchmark> benchmarks = C.list(dao.q("test", test).orderBy("-avg").fetchAsList());
        ChartData data = new ChartData(benchmarks.map((lb) -> lb.language));
        data.datasets = new ArrayList<>();

        List<String> avgBackgroundColors = benchmarks.map((lb) -> {return ColorCaculator.colorOf(lb.language, false);});
        ChartData.Dataset avg = new ChartData.Dataset("average", benchmarks.map((lb) -> lb.avg), avgBackgroundColors);
        data.datasets.add(avg);

        List<String> topBackgroundColors = benchmarks.map((lb) -> {return ColorCaculator.colorOf(lb.language, true);});
        ChartData.Dataset top = new ChartData.Dataset("top", benchmarks.map((lb) -> lb.top), topBackgroundColors);
        data.datasets.add(top);
        return data;
    }

    private ChartData codeDensity(List<Project> projects) {
        List<Float> densities = C.list(projects).map((p) -> p.density).filter((n) -> n > 0);
        projects = C.list(projects).take(densities.size());
        ChartData chartData = new ChartData(C.list(projects).map(Project::getLabel));
        chartData.datasets = new ArrayList<>();
        chartData.datasets.add(new ChartData.Dataset("", densities, C.list(projects).map((p) -> p.color)));
        return chartData;
    }
}
