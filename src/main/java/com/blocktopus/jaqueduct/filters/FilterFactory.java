package com.blocktopus.jaqueduct.filters;

import com.blocktopus.jaqueduct.exceptions.JAqueductException;

import java.util.Optional;

public class FilterFactory {

    public static Filter getFilter(String filterString) {

        int inBracket = 0;
        boolean isProp = false;
        boolean inString = false;
        boolean isCheck = false;
        boolean isStringValue = false;

        boolean isAnd = false;
        boolean isOR = false;

        boolean wasJustAnd = false;
        boolean wasJustOr = false;

        StringBuilder propName = new StringBuilder();

        StringBuilder op = new StringBuilder();

        StringBuilder value = new StringBuilder();

        Filter currentFilter = null;

        StringBuilder inBracketString = new StringBuilder();

        for (int i = 0; i < filterString.length(); i++) {


            char c = filterString.charAt(i);

            switch (c) {
                case '(':
                    inBracket++;
                    if(inBracket>1) {
                        inBracketString.append(c);
                    }
                    break;
                case ')':
                   // System.out.println(inBracket);
                    if(inBracket==1) {
                        Filter newFilter;
                        if(propName.length()!=0){
                            newFilter = makePropertyFilter(propName.toString(), value.toString(), op.toString(), isStringValue);
                            propName = new StringBuilder();
                            value = new StringBuilder();
                            op = new StringBuilder();
                            isStringValue = false;
                        } else {
                            newFilter = getFilter(inBracketString.toString());
                        }
                        if (isAnd) {
                            currentFilter = new AndFilter(currentFilter, newFilter);
                            isAnd = false;
                        } else {
                            if (isOR) {
                                currentFilter = new OrFilter(currentFilter, newFilter);
                                isOR = false;
                            } else {
                                currentFilter = newFilter;
                            }
                        }
                        inBracket--;
                    } else {
                        inBracketString.append(c);
                        inBracket--;
                    }

                    break;
                default:
                    //System.out.println(c);

                    if (inBracket > 1) {
                        inBracketString.append(c);

                    }else {
                        switch (c) {
                            case '!':
                                if (inString) {
                                    value.append(c);
                                } else {
                                    isCheck = true;
                                    break;
                                }
                            case '@':
                                if (inString) {
                                    value.append(c);
                                } else {
                                    isProp = true;
                                    break;
                                }
                            case '.':
                                if (!isProp) {
                                    value.append(c);
                                }
                                break;
                            case '<':
                                if (inString) {
                                    value.append(c);
                                } else {
                                    isProp = false;
                                    op.append(c);
                                }
                                break;
                            case '>':
                                if (inString) {
                                    value.append(c);
                                } else {
                                    isProp = false;
                                    op.append(c);
                                }
                                break;
                            case '~':
                                if (inString) {
                                    value.append(c);
                                } else {
                                    isProp = false;
                                    op.append(c);
                                }
                                break;
                            case '=':
                                if (inString) {
                                    value.append(c);
                                } else {
                                    if (isCheck) {
                                        op.append('!');
                                        isCheck = false;
                                    }

                                    isProp = false;
                                    op.append(c);
                                }
                                break;
                            case '&':
                                if (!wasJustAnd) {
                                    Filter newFilter;
                                    if(propName.length()!=0) {
                                        newFilter = makePropertyFilter(propName.toString(), value.toString(), op.toString(), isStringValue);
                                        propName = new StringBuilder();
                                        value = new StringBuilder();
                                        op = new StringBuilder();
                                        isStringValue = false;
                                    } else {
                                        newFilter = getFilter(inBracketString.toString());
                                    }
                                    if (isAnd) {
                                        currentFilter = new AndFilter(currentFilter, newFilter);
                                    } else {
                                        if (isOR) {
                                            //ERK
                                            currentFilter = new OrFilter(currentFilter, newFilter);
                                            isOR = false;
                                        } else {
                                            currentFilter = newFilter;
                                        }
                                    }

                                    isAnd = true;
                                    wasJustAnd = true;
                                } else {
                                    wasJustAnd = false;
                                }
                                break;
                            case '|':
                                if (!wasJustOr) {
                                    Filter newFilter;
                                    if(propName.length()!=0) {
                                        newFilter = makePropertyFilter(propName.toString(), value.toString(), op.toString(), isStringValue);
                                        propName = new StringBuilder();
                                        value = new StringBuilder();
                                        op = new StringBuilder();
                                        isStringValue = false;
                                    } else {
                                        newFilter = getFilter(inBracketString.toString());
                                    }
                                    if (isAnd) {
                                        //ERK
                                        currentFilter = new AndFilter(currentFilter, newFilter);
                                        isAnd = false;
                                    } else {
                                        if (isOR) {
                                            //ERK
                                            currentFilter = new OrFilter(currentFilter, newFilter);
                                            isOR = false;
                                        } else {
                                            currentFilter = newFilter;
                                        }
                                    }

                                    isOR = true;
                                    wasJustOr = true;
                                } else {
                                    wasJustOr = false;
                                }
                                break;

                            case ' ':
                                if (inString) {
                                    value.append(c);
                                }
                                break;
                            case '\'':
                            case '\"':
                                isStringValue = true;
                                inString = !inString;
                                break;
                            default:
                                if (isProp) {
                                    propName.append(c);
                                } else {
                                    //must be the value
                                    value.append(c);
                                }
                        }
                    }
            }
        }
        return currentFilter;
    }


    public static PropertyFilter makePropertyFilter(String name, String valueString, String op, boolean isStringValue) {
        Optional<PropertyFilter.Operator> operator = PropertyFilter.Operator.getByValue(op);
        if (!operator.isPresent()) {
            throw new JAqueductException(op + " is not a valid operator");
        }
        Object value;
        if (isStringValue) {
            value = valueString;
        } else {
            try {
                int i = Integer.parseInt(valueString);
                value = i;

            } catch (NumberFormatException nfe) {
                try {
                    double d = Double.parseDouble(valueString);
                    value = d;
                } catch (NumberFormatException nfe2) {
                    throw new JAqueductException("Could not parse " + valueString + " as a valid number");
                }
            }

        }
        return new PropertyFilter(name, value, operator.get());
    }
}
