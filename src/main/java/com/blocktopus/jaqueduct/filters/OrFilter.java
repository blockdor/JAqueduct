package com.blocktopus.jaqueduct.filters;

import com.blocktopus.jaqueduct.JsonObject;

public class OrFilter implements Filter{

    private Filter leftFilter;
    private Filter rightFilter;

    public OrFilter(Filter f1, Filter f2) {
        this.leftFilter = f1;
        this.rightFilter = f2;
    }

    public Filter getLeftFilter() {
        return leftFilter;
    }

    public Filter getRightFilter() {
        return rightFilter;
    }

    @Override
    public boolean evaluate(JsonObject jo) {
        return leftFilter.evaluate(jo)|| rightFilter.evaluate(jo);
    }

    @Override
    public String toString() {
        return "OrFilter{" +
                "leftFilter=" + leftFilter +
                ", rightFilter=" + rightFilter +
                '}';
    }
}
