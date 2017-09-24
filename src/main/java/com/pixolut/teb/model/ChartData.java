package com.pixolut.teb.model;

import act.data.annotation.Data;
import act.util.SimpleBean;
import org.osgl.util.C;

import java.util.List;
import java.util.SortedSet;

/**
 * Describe a set of data to plot chart.
 */
@Data
public class ChartData implements SimpleBean {

    /**
     * A data set of a chart.
     */
    public static class Dataset implements Comparable<Dataset> {
        public String label;
        public List<Number> data;

        public String getLabel() {
            return label;
        }

        @Override
        public int compareTo(Dataset o) {
            float hisValue = o.data.get(0).floatValue();
            float myValue = data.get(0).floatValue();
            return (int) (hisValue * 100 - myValue * 100);
        }
    }

    public SortedSet<Dataset> datasets;

    public List<String> getLabels() {
        return C.list(datasets).map(Dataset::getLabel);
    }

}
