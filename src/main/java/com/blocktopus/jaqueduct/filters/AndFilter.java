package com.blocktopus.jaqueduct.filters;

import com.blocktopus.jaqueduct.JsonObject;

public class AndFilter implements Filter {

    private Filter leftFilter;
    private Filter rightFilter;

    public Filter getLeftFilter() {
        return leftFilter;
    }

    public Filter getRightFilter() {
        return rightFilter;
    }

    public AndFilter(Filter leftFilter, Filter f2) {
        this.leftFilter = leftFilter;
        this.rightFilter = f2;
    }

    @Override
    public boolean evaluate(JsonObject jo) {
        return leftFilter.evaluate(jo) && rightFilter.evaluate(jo);
    }

    @Override
    public String toString() {
        return "AndFilter{" +
                "leftFilter=" + leftFilter +
                ", rightFilter=" + rightFilter +
                '}';
    }
}
