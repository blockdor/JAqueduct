package com.blocktopus.jaqueduct.filters;

import com.blocktopus.jaqueduct.JsonObject;
import com.blocktopus.jaqueduct.exceptions.JAqueductException;

import java.util.Optional;

public class PropertyFilter implements Filter {

    public enum Operator {
        EQ("=="),
        NEQ("!="),
        LT("<"),
        LTE("<="),
        GT(">"),
        GTE(">="),
        REGEX("=~");

        public String value;

        Operator(String value) {
            this.value = value;
        }

        public static Optional<Operator> getByValue(String value) {
            for (Operator op : Operator.values()) {
                if (op.value.equals(value)) {
                    return Optional.of(op);
                }
            }
            return Optional.empty();
        }
    }

    private String name;
    private Object value;
    private Operator op;

    public String getName() {
        return name;
    }

    public Object getValue() {
        return value;
    }


    public Operator getOp() {
        return op;
    }

    public PropertyFilter(String name, Object value, Operator op) {
        this.name = name;
        this.value = value;
        this.op = op;
    }

    @Override
    public boolean evaluate(JsonObject jo) {
        Object o = jo.getProperty(name);
        switch (op) {
            case EQ:
                return value.equals(o);
            case GT:
                try {
                    Comparable c = (Comparable) o;
                    return c.compareTo(value) > 0;
                } catch (ClassCastException cce) {
                    throw new JAqueductException("Un-comparable values and > in filter", cce);
                }
            case LT:
                try {
                    Comparable c = (Comparable) o;
                    return c.compareTo(value) < 0;
                } catch (ClassCastException cce) {
                    throw new JAqueductException("Un-comparable values and < in filter", cce);
                }
            case GTE:
                try {
                    Comparable c = (Comparable) o;
                    return c.compareTo(value) >= 0;
                } catch (ClassCastException cce) {
                    throw new JAqueductException("Un-comparable values and >= in filter", cce);
                }
            case LTE:
                try {
                    Comparable c = (Comparable) o;
                    return c.compareTo(value) <= 0;
                } catch (ClassCastException cce) {
                    throw new JAqueductException("Un-comparable values and <= in filter", cce);
                }
            case NEQ:
                return !value.equals(o);
            case REGEX:
                String s = (String) o;
                String regex = (String) value;
                return s.matches(regex);
        }
        return false;
    }

    @Override
    public String toString() {
        return "PropertyFilter{" +
                "name='" + name + '\'' +
                ", value=" + value +
                ", op=" + op +
                '}';
    }
}
