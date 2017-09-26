package com.pixolut.tfb_insight.model;

import act.data.annotation.Data;
import act.util.SimpleBean;
import org.osgl.$;
import org.osgl.util.C;

import java.util.List;

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
        public List<String> backgroundColor;

        public Dataset(String label, Number number, String backgroundColor) {
            this.label = label;
            this.data = C.list(number);
            this.backgroundColor = C.list(backgroundColor);
        }

        public Dataset(String label, List<? extends Number> numbers, List<String> backgroundColor) {
            this.label = label;
            this.data = C.list(numbers);
            this.backgroundColor = backgroundColor;
        }

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

    public List<String> labels;

    public List<Dataset> datasets;

    public ChartData(List<String> labels) {
        this.labels = $.notNull(labels);
    }

    public List<String> getLabels() {
        return labels;
    }

}
