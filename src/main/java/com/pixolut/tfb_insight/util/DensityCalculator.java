package com.pixolut.tfb_insight.util;

import com.pixolut.tfb_insight.model.Project;
import com.pixolut.tfb_insight.model.Test;
import com.pixolut.tfb_insight.model.TestType;
import org.osgl.$;

import java.util.HashSet;
import java.util.Set;

/**
 * Calculate framework code density.
 *
 * Code density is a metric used to measure how
 * how many lines of code needed to implement a
 * test. The application use the following weight
 * to calculate code density for different test
 * type:
 *
 * * plaintext - 1
 * * json - 1.2
 * * db - 2.0
 * * query - 2.2
 * * update - 2.5
 * * fortune - 2.2
 *
 * The formula to calculate code density of a framework is
 *
 * ```
 * Sigma(num-of-test-for-type * weight-of-type) / loc * 100
 * ```
 *
 * Note it is able to change the density configuration via
 * `resources/density.properties` file
 *
 * ## Exclude duplicate test counts
 *
 * The following cases are considered duplicate tests:
 * 1. json and plaintext tests are counted only one time
 * 2. db, fortune, query and update tests are counted only one time per database
 *
 */
public class DensityCalculator {

    public static float calculate(Project project) {
        if (project.loc < 0) {
            return -1.0f;
        }
        int testCount = 0;
        float totalWeight = 0;
        int json = 0;
        int plaintext = 0;
        Set<$.T2<TestType, String>> typeAndDb = new HashSet<>();
        boolean counted = false;
        for (Test test : project.tests) {
            for (TestType type : test.bestResult.keySet()) {
                if (type == TestType.json && json++ > 0) {
                    continue;
                }
                if (type == TestType.plaintext && plaintext++ > 0) {
                    continue;
                }
                if (type != TestType.json && type != TestType.plaintext) {
                    $.T2<TestType, String> key = $.T2(type, test.database);
                    if (typeAndDb.contains(key)) {
                        continue;
                    }
                    typeAndDb.add(key);
                }
                totalWeight += type.densityWeight();
                testCount++;
                counted = true;
            }
        }
        project.testCount = testCount;
        return !counted ? -1.0f : totalWeight * 100F / project.loc;
    }

}
