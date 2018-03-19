package com.blocktopus.jaqueduct.filters;

import com.blocktopus.jaqueduct.JsonObject;

public class NotFilter implements Filter{

    private Filter f1;

    public NotFilter(Filter f1) {
        this.f1 = f1;
    }

    @Override
    public boolean evaluate(JsonObject jo) {
        return !f1.evaluate(jo);
    }
}
