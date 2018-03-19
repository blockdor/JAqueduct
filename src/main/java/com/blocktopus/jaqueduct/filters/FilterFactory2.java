package com.blocktopus.jaqueduct.filters;

import com.blocktopus.jaqueduct.exceptions.JAqueductException;
import com.sun.javafx.property.adapter.JavaBeanQuickAccessor;

import java.util.Optional;

public class FilterFactory2 {

    public static Filter getFilter(String filterString) {
        return getBracketFilter(filterString.trim());
    }

    private static Filter getBracketFilter(String filterString){
        if(filterString.charAt(0)!='('){
            throw new JAqueductException("must start with (");
        }
        if(filterString.charAt(filterString.length()-1)!=')'){
            throw new JAqueductException("must end with )");
        }
        int bracketIndex = filterString.indexOf('(',1);
        int atIndex = filterString.indexOf('@',1);
        if(atIndex==-1){
            throw new JAqueductException("must have at least one @ in filter string");
        }
        Filter currentFilter;
        if(bracketIndex==-1||atIndex<bracketIndex){
            //find next finishing char i.e. &, |, (last char ')')
            int lastPredicateChar = getMinIndexToChar(filterString,1,'&','|',')')-1;

            currentFilter = getPredicateFilter(filterString.substring(1,lastPredicateChar));
        } else {
            int bracketCounter =0;
            int closingBracketIndex = 0;
            for(int i=bracketIndex+1;i<filterString.length();i++){
                if(filterString.charAt(i)=='('){
                    bracketCounter++;
                }
                if(filterString.charAt(i)==')'){
                    if(bracketCounter==0){
                        closingBracketIndex=i;
                        break;
                    }
                    bracketCounter--;
                }
            }
            currentFilter = getBracketFilter(filterString.substring(bracketIndex,closingBracketIndex));
        }
        //do and or


        return currentFilter;
    }


    private static Filter getPredicateFilter(String filterString){
        String predicateString = filterString.trim();
        if(predicateString.charAt(0)!='@'){
            throw new JAqueductException("predicate must start with @");
        }
        if(predicateString.charAt(1)!='.'){
            throw new JAqueductException("predicate must start with @.");
        }
        int opChar = getMinIndexToChar(predicateString,2,'=','<','>','!');
        if(opChar==Integer.MAX_VALUE){
            throw new JAqueductException("predicate must contain an operator: "+predicateString);
        }
        String propName = predicateString.substring(2,opChar-1).trim();
        String opString;
        int valueStart;
        if(filterString.charAt(opChar+1)=='='||filterString.charAt(opChar+1)=='~'){
            opString = filterString.substring(opChar,opChar+1);
            valueStart = opChar+2;
        } else {
            opString = ""+opChar;
            valueStart = opChar+1;
        }
        Optional<PropertyFilter.Operator> op = PropertyFilter.Operator.getByValue(opString);
        if(!op.isPresent()){
            throw new JAqueductException("Invalid operator: "+op);
        }




        return null;
    }

    private Object getValue(String valueString){
        String trimmedValue = valueString.trim();
        if(trimmedValue.charAt(0)=='\''||trimmedValue.charAt(0)=='\"'){
            return trimmedValue.substring(1,trimmedValue.length()-1);
        }

        if("false".equalsIgnoreCase(trimmedValue)){
            return false;
        }
        if("true".equalsIgnoreCase(trimmedValue)){
            return true;
        }
        try {
            int i = Integer.parseInt(valueString);
            return i;

        } catch (NumberFormatException nfe) {
            try {
                double d = Double.parseDouble(valueString);
                return d;
            } catch (NumberFormatException nfe2) {
                throw new JAqueductException("Could not parse " + valueString + " as a valid value (integer,double or boolean)");
            }
        }
    }


    private static int getMinIndexToChar(String s, int startIndex, char... chars){
        int min =0;
        for(char c:chars){
            int i = s.indexOf(c,startIndex);
            min = Math.min(min,i==-1?Integer.MAX_VALUE:i);
        }
        return min;
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
