package com.blocktopus.jaqueduct;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.blocktopus.jaqueduct.exceptions.JAqueductException;
import com.fasterxml.jackson.databind.ObjectMapper;

public final class TypeConverter {

    private TypeConverter() {

    }

    private static class ToClass<T> implements Function<Object, T> {

        public ToClass(Class<T> clazz, ObjectMapper om) {
            super();
            this.clazz = clazz;
            this.om = om;
        }

        private Class<T> clazz;
        private ObjectMapper om;

        @Override
        public T apply(Object o) {
            try {
                if (clazz.isInstance(o)) {
                    return clazz.cast(o);
                }
                return om.convertValue(o, clazz);
            } catch (Throwable t) {
                throw new JAqueductException("Cannot convert from " + o.getClass().getName() + " to " + clazz.getName(), t);
            }
        }

    }

    private static class ToGenericList<T> implements Function<Object, List<T>> {

        private Function<Object, T> convertingFunction;

        public ToGenericList(Function<Object, T> convertingFunction) {
            this.convertingFunction = convertingFunction;
        }

        @Override
        public List<T> apply(Object o) {
            @SuppressWarnings("unchecked")
            List<Object> t = List.class.cast(o);
            try {
                return t.stream().map(convertingFunction).collect(Collectors.toList());
            } catch (Throwable cce) {
                throw new JAqueductException("Could not convert members of the list", cce);
            }
        }
    }

    public static <T> Function<Object, T> toClass(Class<T> clazz, ObjectMapper objectMapper) {
        return new ToClass<>(clazz, objectMapper);
    }

    public static <T> Function<Object, List<T>> toListOf(Function<Object, T> fn) {
        return new ToGenericList<>(fn);
    }


}
