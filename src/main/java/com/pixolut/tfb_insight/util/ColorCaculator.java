package com.pixolut.tfb_insight.util;

import com.pixolut.tfb_insight.model.Project;
import com.pixolut.tfb_insight.model.Test;
import org.osgl.$;
import org.osgl.util.S;

/**
 * Utility to calculate color for tests.
 */
public class ColorCaculator {

    public static String colorOf(String language, boolean top) {
        int r = rgbOf(language + language + language);
        int g = rgbOf(language.hashCode() * language.length());
        int b = rgbOf(language.hashCode() * r / g);
        if (top) {
            r = rgbOf(r + 20);
            g = rgbOf(g + 40);
            b = rgbOf(b + 60);
        }
        return S.fmt("rgb(%s, %s, %s)", r, g, b);
    }

    public static String colorOfDataBase(String database) {
        if ("mysql".equalsIgnoreCase(database)) {
            return "Cyan";
        } else if ("postgres".equalsIgnoreCase(database)) {
            return "Coral";
        } else if ("mongodb".equalsIgnoreCase(database)) {
            return "Lime";
        } else if ("sqlserver".equalsIgnoreCase(database)) {
            return "LightYellow";
        } else {
            return "LawnGreen";
        }
    }

    public static String colorOfClassification(Test.Classification classification) {
        switch (classification) {
            case Fullstack:
                return "SPRINGGREEN";
            case Micro:
                return "YELLOW";
            default:
                return "GHOSTWHITE";
        }
    }

    public static String patternOfClassfication(Test.Classification classification) {
        switch (classification) {
            case Fullstack:
                return "square";
            case Micro:
                return "diagonal";
            default:
                return "";
        }
    }

    /**
     * Calculate the color of a framework
     *
     * @param project the project
     * @return the color of the project framework
     */
    public static String colorOf(Project project) {
        Float density = project.density;
        if (null == density) {
            density = 0.1f;
        }
        int tests = 0;
        for (Test test : project.tests) {
            tests += test.results.size();
        }
        int r = rgbOf($.hc(project.framework.hashCode()));
        int g = rgbOf($.hc(tests, project.loc, density));
        int b = rgbOf($.hc(project.loc, density, project.framework));
        return S.fmt("rgb(%s, %s, %s)", r, g, b);
    }

    /**
     * Returns color string calculated from project and test
     *
     * @param project the project
     * @param test    the test
     * @return the color string
     */
    public static String colorOf(Project project, Test test) {
        return colorOf(project);
    }

    private static int rgbOf(Object obj) {
        int hc = Math.abs(obj.hashCode());
        int n = Math.abs(hc % 350);
        if (n > 255) {
            n = 255 - (n - 255) / 3;
        }
        if (n < 1) {
            n = hc % 255 + 1;
        }
        return n;
    }

}
