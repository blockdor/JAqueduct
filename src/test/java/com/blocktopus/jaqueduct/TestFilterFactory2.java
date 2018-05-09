package com.blocktopus.jaqueduct;

import com.blocktopus.jaqueduct.filters.*;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.assertEquals;

public class TestFilterFactory2 {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Before
    public void setup() {

    }

    @Test
    public void testSimpleStringFilter(){
        Filter f = FilterFactory2.getFilter("(@.age==\"bob\")");
        assertEquals(PropertyFilter.class,f.getClass());
        PropertyFilter pf = (PropertyFilter)f;
        assertEquals("age",pf.getName());
        assertEquals(PropertyFilter.Operator.EQ,pf.getOp());
        assertEquals("bob",pf.getValue());
    }

    @Test
    public void testSimpleIntegerFilter(){
        Filter f = FilterFactory2.getFilter("(@.age==10)");
        assertEquals(PropertyFilter.class,f.getClass());
        PropertyFilter pf = (PropertyFilter)f;
        assertEquals("age",pf.getName());
        assertEquals(PropertyFilter.Operator.EQ,pf.getOp());
        assertEquals(10,pf.getValue());
    }

    @Test
    public void testSimpleDoubleFilter(){
        Filter f = FilterFactory2.getFilter("(@.age==10.1)");
        assertEquals(PropertyFilter.class,f.getClass());
        PropertyFilter pf = (PropertyFilter)f;
        assertEquals("age",pf.getName());
        assertEquals(PropertyFilter.Operator.EQ,pf.getOp());
        assertEquals(10.1,pf.getValue());
    }
    @Test
    public void testSimpleStringFilterSpaces(){
        Filter f = FilterFactory2.getFilter("(@.age == 'bob')");
        assertEquals(PropertyFilter.class,f.getClass());
        PropertyFilter pf = (PropertyFilter)f;
        assertEquals("age",pf.getName());
        assertEquals(PropertyFilter.Operator.EQ,pf.getOp());
        assertEquals("bob",pf.getValue());
    }

    @Test
    public void testSimpleStringFilterWithEscapedChars(){
        Filter f = FilterFactory.getFilter("(@.age == \"bob is \"cool\" \")");
        assertEquals(PropertyFilter.class,f.getClass());
        PropertyFilter pf = (PropertyFilter)f;
        assertEquals("age",pf.getName());
        assertEquals(PropertyFilter.Operator.EQ,pf.getOp());
        assertEquals("bob is \"cool\"",pf.getValue());
    }


    @Test
    public void testSimpleIntegerFilterSpaces(){
        Filter f = FilterFactory2.getFilter("(@.age == 10)");
        assertEquals(PropertyFilter.class,f.getClass());
        PropertyFilter pf = (PropertyFilter)f;
        assertEquals("age",pf.getName());
        assertEquals(PropertyFilter.Operator.EQ,pf.getOp());
        assertEquals(10,pf.getValue());
    }

    @Test
    public void testSimpleDoubleFilterSpaces(){
        Filter f = FilterFactory2.getFilter("(@.age == 10.1)");
        assertEquals(PropertyFilter.class,f.getClass());
        PropertyFilter pf = (PropertyFilter)f;
        assertEquals("age",pf.getName());
        assertEquals(PropertyFilter.Operator.EQ,pf.getOp());
        assertEquals(10.1,pf.getValue());
    }

    @Test
    public void testAllOps(){
        for (PropertyFilter.Operator operator : PropertyFilter.Operator.values()) {
            System.out.println(operator);
            Filter f = FilterFactory2.getFilter("(@.age "+operator.value+" 'bob')");
            assertEquals(PropertyFilter.class,f.getClass());
            PropertyFilter pf = (PropertyFilter)f;
            assertEquals("age",pf.getName());
            assertEquals(operator,pf.getOp());
            assertEquals("bob",pf.getValue());
        }

    }
    @Test
    public void testAndFilter(){
        Filter f = FilterFactory2.getFilter("(@.age == 10.1 && @.name == 'bob' )");
        assertEquals(AndFilter.class,f.getClass() );
        AndFilter af = (AndFilter)f;
        assertEquals(PropertyFilter.class,af.getLeftFilter().getClass() );
        assertEquals(PropertyFilter.class,af.getRightFilter().getClass() );

        PropertyFilter pf = (PropertyFilter)af.getLeftFilter();
        assertEquals("age",pf.getName());
        assertEquals(PropertyFilter.Operator.EQ,pf.getOp());
        assertEquals(10.1,pf.getValue());

        PropertyFilter pf2 = (PropertyFilter)af.getRightFilter();
        assertEquals("name",pf2.getName());
        assertEquals(PropertyFilter.Operator.EQ,pf2.getOp());
        assertEquals("bob",pf2.getValue());
    }
    @Test
    public void testOrFilter(){
        Filter f = FilterFactory2.getFilter("(@.age == 10.1 || @.name == 'bob' )");
        assertEquals(OrFilter.class,f.getClass() );
        OrFilter af = (OrFilter)f;
        assertEquals(PropertyFilter.class,af.getLeftFilter().getClass() );
        assertEquals(PropertyFilter.class,af.getRightFilter().getClass() );

        PropertyFilter pf = (PropertyFilter)af.getLeftFilter();
        assertEquals("age",pf.getName());
        assertEquals(PropertyFilter.Operator.EQ,pf.getOp());
        assertEquals(10.1,pf.getValue());

        PropertyFilter pf2 = (PropertyFilter)af.getRightFilter();
        assertEquals("name",pf2.getName());
        assertEquals(PropertyFilter.Operator.EQ,pf2.getOp());
        assertEquals("bob",pf2.getValue());
    }


    @Test
    public void testManyAndFilter(){
        Filter f = FilterFactory2.getFilter("(@.age>21&&@.name == 'bob'&&@.weight<10.1)");
        assertEquals(AndFilter.class,f.getClass() );
        AndFilter af = (AndFilter)f;
        assertEquals(AndFilter.class,af.getRightFilter().getClass() );
        assertEquals(PropertyFilter.class,af.getLeftFilter().getClass() );

        AndFilter af2 = (AndFilter)af.getRightFilter();
        assertEquals(PropertyFilter.class,af2.getLeftFilter().getClass() );
        assertEquals(PropertyFilter.class,af2.getRightFilter().getClass() );

        PropertyFilter pf = (PropertyFilter)af.getLeftFilter();
        assertEquals("age",pf.getName());
        assertEquals(PropertyFilter.Operator.GT,pf.getOp());
        assertEquals(21,pf.getValue());

        PropertyFilter pf2 = (PropertyFilter)af2.getLeftFilter();
        assertEquals("name",pf2.getName());
        assertEquals(PropertyFilter.Operator.EQ,pf2.getOp());
        assertEquals("bob",pf2.getValue());

        PropertyFilter pf3 = (PropertyFilter)af2.getRightFilter();
        assertEquals("weight",pf3.getName());
        assertEquals(PropertyFilter.Operator.LT,pf3.getOp());
        assertEquals(10.1,pf3.getValue());

    }

    @Test
    public void testManyOrFilter(){
        Filter f = FilterFactory2.getFilter("(@.age>21 || @.name == 'bob' || @.weight < 10.1)");
        assertEquals(OrFilter.class,f.getClass() );
        OrFilter af = (OrFilter)f;

        assertEquals(PropertyFilter.class,af.getLeftFilter().getClass() );
        assertEquals(OrFilter.class,af.getRightFilter().getClass() );

        OrFilter af2 = (OrFilter)af.getRightFilter();
        assertEquals(PropertyFilter.class,af2.getLeftFilter().getClass() );
        assertEquals(PropertyFilter.class,af2.getRightFilter().getClass() );

        PropertyFilter pf = (PropertyFilter)af.getLeftFilter();
        assertEquals("age",pf.getName());
        assertEquals(PropertyFilter.Operator.GT,pf.getOp());
        assertEquals(21,pf.getValue());

        PropertyFilter pf2 = (PropertyFilter)af2.getLeftFilter();
        assertEquals("name",pf2.getName());
        assertEquals(PropertyFilter.Operator.EQ,pf2.getOp());
        assertEquals("bob",pf2.getValue());

        PropertyFilter pf3 = (PropertyFilter)af2.getRightFilter();
        assertEquals("weight",pf3.getName());
        assertEquals(PropertyFilter.Operator.LT,pf3.getOp());
        assertEquals(10.1,pf3.getValue());
    }

    @Test
    public void testManyMixedFilter(){
        Filter f = FilterFactory2.getFilter("(@.age>21 || @.name == 'bob' && @.weight < 10.1)");
        assertEquals(OrFilter.class,f.getClass());
        OrFilter af = (OrFilter)f;
        assertEquals(AndFilter.class,af.getRightFilter().getClass());
        assertEquals(PropertyFilter.class,af.getLeftFilter().getClass());

        AndFilter af2 = (AndFilter)af.getRightFilter();
        assertEquals(PropertyFilter.class,af2.getLeftFilter().getClass() );
        assertEquals(PropertyFilter.class,af2.getRightFilter().getClass() );

        PropertyFilter pf = (PropertyFilter)af.getLeftFilter();
        assertEquals("age",pf.getName());
        assertEquals(PropertyFilter.Operator.GT,pf.getOp());
        assertEquals(21,pf.getValue());

        PropertyFilter pf2 = (PropertyFilter)af2.getLeftFilter();
        assertEquals("name",pf2.getName());
        assertEquals(PropertyFilter.Operator.EQ,pf2.getOp());
        assertEquals("bob",pf2.getValue());

        PropertyFilter pf3 = (PropertyFilter)af2.getRightFilter();
        assertEquals("weight",pf3.getName());
        assertEquals(PropertyFilter.Operator.LT,pf3.getOp());
        assertEquals(10.1,pf3.getValue());
    }

    @Test
    public void testManyMixedFilter2(){
        Filter f = FilterFactory2.getFilter("(@.age>21 && @.name == 'bob' || @.weight < 10.1)");
        assertEquals(AndFilter.class,f.getClass());
        AndFilter af = (AndFilter)f;
        assertEquals(OrFilter.class,af.getRightFilter().getClass());
        assertEquals(PropertyFilter.class,af.getLeftFilter().getClass());

        OrFilter af2 = (OrFilter)af.getRightFilter();
        assertEquals(PropertyFilter.class,af2.getLeftFilter().getClass() );
        assertEquals(PropertyFilter.class,af2.getRightFilter().getClass() );

        PropertyFilter pf = (PropertyFilter)af.getLeftFilter();
        assertEquals("age",pf.getName());
        assertEquals(PropertyFilter.Operator.GT,pf.getOp());
        assertEquals(21,pf.getValue());

        PropertyFilter pf2 = (PropertyFilter)af2.getLeftFilter();
        assertEquals("name",pf2.getName());
        assertEquals(PropertyFilter.Operator.EQ,pf2.getOp());
        assertEquals("bob",pf2.getValue());

        PropertyFilter pf3 = (PropertyFilter)af2.getRightFilter();
        assertEquals("weight",pf3.getName());
        assertEquals(PropertyFilter.Operator.LT,pf3.getOp());
        assertEquals(10.1,pf3.getValue());
    }
    @Test
    public void testManyMixedFilterBrackets(){
        Filter f = FilterFactory2.getFilter("(@.age>21 && (@.name == 'bob' || @.weight < 10.1))");
        assertEquals(AndFilter.class,f.getClass());
        AndFilter af = (AndFilter)f;
        assertEquals(PropertyFilter.class,af.getLeftFilter().getClass());
        assertEquals(OrFilter.class,af.getRightFilter().getClass());

        OrFilter af2 = (OrFilter)af.getRightFilter();
        assertEquals(PropertyFilter.class,af2.getLeftFilter().getClass() );
        assertEquals(PropertyFilter.class,af2.getRightFilter().getClass() );

        PropertyFilter pf = (PropertyFilter)af.getLeftFilter();
        assertEquals("age",pf.getName());
        assertEquals(PropertyFilter.Operator.GT,pf.getOp());
        assertEquals(21,pf.getValue());

        PropertyFilter pf2 = (PropertyFilter)af2.getLeftFilter();
        assertEquals("name",pf2.getName());
        assertEquals(PropertyFilter.Operator.EQ,pf2.getOp());
        assertEquals("bob",pf2.getValue());

        PropertyFilter pf3 = (PropertyFilter)af2.getRightFilter();
        assertEquals("weight",pf3.getName());
        assertEquals(PropertyFilter.Operator.LT,pf3.getOp());
        assertEquals(10.1,pf3.getValue());
    }

    @Test
    public void testManyMixedFilterBrackets2(){
        Filter f = FilterFactory2.getFilter("((@.age>21 && @.name == 'bob') || @.weight < 10.1)");
        assertEquals(OrFilter.class,f.getClass());
        OrFilter af = (OrFilter)f;
        assertEquals(AndFilter.class,af.getLeftFilter().getClass());
        assertEquals(PropertyFilter.class,af.getRightFilter().getClass());

        AndFilter af2 = (AndFilter)af.getLeftFilter();
        assertEquals(PropertyFilter.class,af2.getLeftFilter().getClass() );
        assertEquals(PropertyFilter.class,af2.getRightFilter().getClass() );

        PropertyFilter pf = (PropertyFilter)af2.getLeftFilter();
        assertEquals("age",pf.getName());
        assertEquals(PropertyFilter.Operator.GT,pf.getOp());
        assertEquals(21,pf.getValue());

        PropertyFilter pf2 = (PropertyFilter)af2.getRightFilter();
        assertEquals("name",pf2.getName());
        assertEquals(PropertyFilter.Operator.EQ,pf2.getOp());
        assertEquals("bob",pf2.getValue());

        PropertyFilter pf3 = (PropertyFilter)af.getRightFilter();
        assertEquals("weight",pf3.getName());
        assertEquals(PropertyFilter.Operator.LT,pf3.getOp());
        assertEquals(10.1,pf3.getValue());
    }
}
