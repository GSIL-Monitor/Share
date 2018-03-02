package com.beta.utils.statis;

/**
 * Created by mike on 2017/9/27.
 */
public class Describe {
    private double max;
    private double min;
    private double mean;
    private double sum;
    private double std;
    private int count;

    public Describe() {

    }

    public Describe(Builder builder) {
        this.max = builder.max;
        this.min = builder.min;
        this.mean = builder.mean;
        this.sum = builder.sum;
        this.std = builder.std;
        this.count = builder.count;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public double getMax() {
        return max;
    }

    public void setMax(double max) {
        this.max = max;
    }

    public double getMin() {
        return min;
    }

    public void setMin(double min) {
        this.min = min;
    }

    public double getMean() {
        return mean;
    }

    public void setMean(double mean) {
        this.mean = mean;
    }

    public double getSum() {
        return sum;
    }

    public void setSum(double sum) {
        this.sum = sum;
    }

    public double getStd() {
        return std;
    }

    public void setStd(double std) {
        this.std = std;
    }

    public static class Builder {
        private double max;
        private double min;
        private double mean;
        private double sum;
        private double std;
        private int count;

        public Builder(int count) {
            this.count = count;
        }

        public Builder values(double max, double min) {
            this.max = max;
            this.min = min;
            return this;
        }

        public Builder mean(double mean) {
            this.mean = mean;
            return this;
        }

        public Builder std(double std) {
            this.std = std;
            return this;
        }

        public Builder sum(double sum) {
            this.sum = sum;
            return this;
        }

        public Describe build() {
            return new Describe(this);
        }
    }
}
