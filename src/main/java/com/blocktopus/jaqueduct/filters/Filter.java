package com.blocktopus.jaqueduct.filters;

import com.blocktopus.jaqueduct.JsonObject;

public interface Filter {

    boolean evaluate(JsonObject jo);

}
