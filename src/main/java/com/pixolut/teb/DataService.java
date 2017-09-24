package com.pixolut.teb;

import act.controller.annotation.UrlContext;
import act.db.morphia.MorphiaQuery;
import act.inject.DefaultValue;
import com.pixolut.teb.model.ChartData;
import com.pixolut.teb.model.Project;
import com.pixolut.teb.model.Test;
import com.pixolut.teb.model.TestType;
import org.osgl.mvc.annotation.GetAction;
import org.osgl.util.E;
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
     * Query chart data.
     *
     * @return
     *      chart data
     */
    @GetAction("chart")
    public ChartData chart(
            @NotNull TestType test,
            String language,
            String technology,
            Test.Classification classification,
            String orm,
            @DefaultValue("10") int top
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
            query.filter("orm", Pattern.compile(orm, Pattern.CASE_INSENSITIVE));
        }
        query.limit(top);
        List<Project> projects = query.fetchAsList();
        ChartData data = new ChartData();
        data.datasets = new TreeSet<>();
        for (Project project : projects) {

        }
        throw E.tbd();
    }
}
