package com.blocktopus.jaqueduct.filters;

import com.blocktopus.jaqueduct.exceptions.JAqueductException;
import com.sun.javafx.property.adapter.JavaBeanQuickAccessor;

import java.util.Optional;

public class FilterFactory {

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
        int endChar;
        Filter currentFilter;
        if(bracketIndex==-1||atIndex<bracketIndex){
            //find next finishing char i.e. &, |, (last char ')')
            endChar = getMinIndexToChar(filterString,1,'&','|',')');

            currentFilter = getPredicateFilter(filterString.substring(1,endChar));
            System.out.println(filterString.substring(1,endChar));
        } else {
            int bracketCounter =0;
            endChar = 0;
            for(int i=bracketIndex+1;i<filterString.length();i++){
                if(filterString.charAt(i)=='('){
                    bracketCounter++;
                }
                if(filterString.charAt(i)==')'){
                    if(bracketCounter==0){
                        endChar=i+1;
                        break;
                    }
                    bracketCounter--;
                }
            }
            if(endChar==0){
                throw new JAqueductException("Matching bracket not found");
            }
            currentFilter = getBracketFilter(filterString.substring(bracketIndex,endChar));
        }
        //do and or
         if(filterString.charAt(endChar)=='&'){
            String rhs = filterString.substring(endChar+2,filterString.length()-1).trim();
            if(rhs.charAt(0)=='@'){
                Filter rhsFilter = getPredicateFilter(rhs);
                currentFilter = new AndFilter(currentFilter,rhsFilter);
            } else if(rhs.charAt(0)=='('){
                Filter rhsFilter = getBracketFilter(rhs);
                currentFilter = new AndFilter(currentFilter,rhsFilter);
            }
        } else if(filterString.charAt(endChar)=='|'){
             String rhs = filterString.substring(endChar+2,filterString.length()-1).trim();
             if(rhs.charAt(0)=='@'){
                 Filter rhsFilter = getPredicateFilter(rhs);
                 currentFilter = new OrFilter(currentFilter,rhsFilter);
             } else if(rhs.charAt(0)=='('){
                 Filter rhsFilter = getBracketFilter(rhs);
                 currentFilter = new OrFilter(currentFilter,rhsFilter);
             }
         }
        System.out.println(currentFilter);
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
        String propName = predicateString.substring(2,opChar).trim();
        String opString;
        int valueStart;
        if(filterString.charAt(opChar+1)=='='||filterString.charAt(opChar+1)=='~'){
            opString = filterString.substring(opChar,opChar+2);
            valueStart = opChar+2;
        } else {
            opString = ""+filterString.charAt(opChar);
            valueStart = opChar+1;
        }
        Optional<PropertyFilter.Operator> operator = PropertyFilter.Operator.getByValue(opString);
        if(!operator.isPresent()){
            throw new JAqueductException("Invalid operator: "+opString);
        }
        //here we need to ignore stringy stuff and see if there's a & or | coming up

        int minIndex = getMinIndexToControlChar(filterString,valueStart,'|','&');

        String valueString;
        if(minIndex!=Integer.MAX_VALUE){
            valueString = filterString.substring(valueStart,minIndex);
            Object value = getValue(valueString);
            Filter aFilter = new PropertyFilter(propName, value, operator.get());
            String rhs = filterString.substring(minIndex+2).trim();
            if(filterString.charAt(minIndex)=='&') {
                if (rhs.charAt(0) == '@') {
                    Filter rhsFilter = getPredicateFilter(rhs);
                    return new AndFilter(aFilter, rhsFilter);
                } else if (rhs.charAt(0) == '(') {
                    Filter rhsFilter = getBracketFilter(rhs);
                    return new AndFilter(aFilter, rhsFilter);
                } else {
                    throw new JAqueductException("Syntax error!");
                }
            } else {
                if (rhs.charAt(0) == '@') {
                    Filter rhsFilter = getPredicateFilter(rhs);
                    return new OrFilter(aFilter,rhsFilter);
                } else if (rhs.charAt(0) == '(') {
                    Filter rhsFilter = getBracketFilter(rhs);
                    return new OrFilter(aFilter,rhsFilter);
                } else {
                    throw new JAqueductException("Syntax error!");
                }
            }
        } else {
            valueString = filterString.substring(valueStart);
            Object value = getValue(valueString);
            return new PropertyFilter(propName, value, operator.get());
        }

    }

    private static Object getValue(String valueString){
        String trimmedValue = valueString.trim();
        System.out.println(trimmedValue);
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
            int i = Integer.parseInt(trimmedValue);
            return i;

        } catch (NumberFormatException nfe) {
            try {
                double d = Double.parseDouble(trimmedValue);
                return d;
            } catch (NumberFormatException nfe2) {
                throw new JAqueductException("Could not parse " + trimmedValue + " as a valid value (quoted string, integer, double or boolean)");
            }
        }
    }


    private static int getMinIndexToChar(String s, int startIndex, char... chars){
        int min =Integer.MAX_VALUE;
        for(char c:chars){
            int i = s.indexOf(c,startIndex);
            min = Math.min(min,i==-1?Integer.MAX_VALUE:i);
        }
        return min;
    }

    private static int getMinIndexToControlChar(String s, int startIndex, char... chars){
        boolean inString = false;
        boolean isEscape = false;

        for(int i=startIndex;i<s.length()-1;i++){
            if(s.charAt(i)=='\\'){
                isEscape=true;
            }
            if(s.charAt(i)=='\''||s.charAt(i)=='\"'){
                if(!isEscape) {
                    inString = !inString;
                } else {
                    isEscape=false;
                }
            }
            if(!inString){
                for(char c:chars){
                    if(c==s.charAt(i)){
                        return i;
                    }
                }
            }

        }
        return Integer.MAX_VALUE;
    }
}
