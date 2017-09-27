package com.pixolut.tfb_insight.model;

import act.data.annotation.Data;
import act.util.SimpleBean;
import org.osgl.$;
import org.osgl.util.C;

import java.util.ArrayList;
import java.util.List;

/**
 * Describe a set of data to plot chart.
 */
@Data
public class ChartData implements SimpleBean {

    /**
     * A data set of a chart.
     */
    @Data
    public static class Dataset implements SimpleBean, Comparable<Dataset> {
        public String label;
        public String type = "horizontalBar";
        public List<Number> data = new ArrayList<>();
        public List<String> backgroundColor = new ArrayList<>();
        public List<String> borderColor = new ArrayList<>();
        public List<Integer> borderDash = new ArrayList<>();
        public boolean fill = true;
        public String test;

        public Dataset(String label, Number number, String backgroundColor) {
            this.label = label;
            this.data = C.list(number);
            this.backgroundColor = C.list(backgroundColor);
        }

        public Dataset(String label, List<? extends Number> numbers, List<String> backgroundColor) {
            this.label = label;
            this.data = C.list(numbers);
            this.backgroundColor = backgroundColor;
            this.borderColor = C.list(backgroundColor.get(0));
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

    public List<String> labels = new ArrayList<>();

    public List<Dataset> datasets = new ArrayList<>();

    public ChartData(List<String> labels) {
        this.labels = $.notNull(labels);
    }

    public List<String> getLabels() {
        return labels;
    }

}
