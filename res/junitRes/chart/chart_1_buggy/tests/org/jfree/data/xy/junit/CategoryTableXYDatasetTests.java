package org.jfree.data.xy.junit;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.jfree.chart.util.PublicCloneable;
import org.jfree.data.xy.CategoryTableXYDataset;
/** 
 * Tests for the             {@link CategoryTableXYDataset} class.
 */
public class CategoryTableXYDatasetTests extends TestCase {
  /** 
 * Returns the tests as a test suite.
 * @return The test suite.
 */
  public static Test suite(){
    return new TestSuite(CategoryTableXYDatasetTests.class);
  }
  /** 
 * Constructs a new set of tests.
 * @param name  the name of the tests.
 */
  public CategoryTableXYDatasetTests(  String name){
    super(name);
  }
  /** 
 * Confirm that the equals method can distinguish all the required fields.
 */
  public void testEquals(){
    CategoryTableXYDataset d1=new CategoryTableXYDataset();
    d1.add(1.0,1.1,"Series 1");
    d1.add(2.0,2.2,"Series 1");
    CategoryTableXYDataset d2=new CategoryTableXYDataset();
    d2.add(1.0,1.1,"Series 1");
    d2.add(2.0,2.2,"Series 1");
    assertTrue(d1.equals(d2));
    assertTrue(d2.equals(d1));
    d1.add(3.0,3.3,"Series 1");
    assertFalse(d1.equals(d2));
    d2.add(3.0,3.3,"Series 1");
    assertTrue(d1.equals(d2));
  }
  /** 
 * Confirm that cloning works.
 */
  public void testCloning(){
    CategoryTableXYDataset d1=new CategoryTableXYDataset();
    d1.add(1.0,1.1,"Series 1");
    d1.add(2.0,2.2,"Series 1");
    CategoryTableXYDataset d2=null;
    try {
      d2=(CategoryTableXYDataset)d1.clone();
    }
 catch (    CloneNotSupportedException e) {
      e.printStackTrace();
    }
    assertTrue(d1 != d2);
    assertTrue(d1.getClass() == d2.getClass());
    assertTrue(d1.equals(d2));
    d1.add(3.0,3.3,"Series 1");
    assertFalse(d1.equals(d2));
    d2.add(3.0,3.3,"Series 1");
    assertTrue(d1.equals(d2));
    d1.setIntervalPositionFactor(0.33);
    assertFalse(d1.equals(d2));
    d2.setIntervalPositionFactor(0.33);
    assertTrue(d1.equals(d2));
  }
  /** 
 * Another check for cloning - making sure it works for a customised interval delegate.
 */
  public void testCloning2(){
    CategoryTableXYDataset d1=new CategoryTableXYDataset();
    d1.add(1.0,1.1,"Series 1");
    d1.add(2.0,2.2,"Series 1");
    d1.setIntervalWidth(1.23);
    CategoryTableXYDataset d2=null;
    try {
      d2=(CategoryTableXYDataset)d1.clone();
    }
 catch (    CloneNotSupportedException e) {
      e.printStackTrace();
    }
    assertTrue(d1 != d2);
    assertTrue(d1.getClass() == d2.getClass());
    assertTrue(d1.equals(d2));
    d1.add(3.0,3.3,"Series 1");
    assertFalse(d1.equals(d2));
    d2.add(3.0,3.3,"Series 1");
    assertTrue(d1.equals(d2));
    d1.setIntervalPositionFactor(0.33);
    assertFalse(d1.equals(d2));
    d2.setIntervalPositionFactor(0.33);
    assertTrue(d1.equals(d2));
  }
  /** 
 * Verify that this class implements             {@link PublicCloneable}.
 */
  public void testPublicCloneable(){
    CategoryTableXYDataset d1=new CategoryTableXYDataset();
    assertTrue(d1 instanceof PublicCloneable);
  }
  /** 
 * Serialize an instance, restore it, and check for equality.
 */
  public void testSerialization(){
    CategoryTableXYDataset d1=new CategoryTableXYDataset();
    d1.add(1.0,1.1,"Series 1");
    d1.add(2.0,2.2,"Series 1");
    CategoryTableXYDataset d2=null;
    try {
      ByteArrayOutputStream buffer=new ByteArrayOutputStream();
      ObjectOutput out=new ObjectOutputStream(buffer);
      out.writeObject(d1);
      out.close();
      ObjectInput in=new ObjectInputStream(new ByteArrayInputStream(buffer.toByteArray()));
      d2=(CategoryTableXYDataset)in.readObject();
      in.close();
    }
 catch (    Exception e) {
      e.printStackTrace();
    }
    assertEquals(d1,d2);
  }
  private static final double EPSILON=0.0000000001;
  /** 
 * This is a test for bug 1312066 - adding a new series should trigger a recalculation of the interval width, if it is being automatically calculated.
 */
  public void testAddSeries(){
    CategoryTableXYDataset d1=new CategoryTableXYDataset();
    d1.setAutoWidth(true);
    d1.add(3.0,1.1,"Series 1");
    d1.add(7.0,2.2,"Series 1");
    assertEquals(3.0,d1.getXValue(0,0),EPSILON);
    assertEquals(7.0,d1.getXValue(0,1),EPSILON);
    assertEquals(1.0,d1.getStartXValue(0,0),EPSILON);
    assertEquals(5.0,d1.getStartXValue(0,1),EPSILON);
    assertEquals(5.0,d1.getEndXValue(0,0),EPSILON);
    assertEquals(9.0,d1.getEndXValue(0,1),EPSILON);
    d1.add(7.5,1.1,"Series 2");
    d1.add(9.0,2.2,"Series 2");
    assertEquals(3.0,d1.getXValue(1,0),EPSILON);
    assertEquals(7.0,d1.getXValue(1,1),EPSILON);
    assertEquals(7.5,d1.getXValue(1,2),EPSILON);
    assertEquals(9.0,d1.getXValue(1,3),EPSILON);
    assertEquals(7.25,d1.getStartXValue(1,2),EPSILON);
    assertEquals(8.75,d1.getStartXValue(1,3),EPSILON);
    assertEquals(7.75,d1.getEndXValue(1,2),EPSILON);
    assertEquals(9.25,d1.getEndXValue(1,3),EPSILON);
    assertEquals(2.75,d1.getStartXValue(0,0),EPSILON);
    assertEquals(6.75,d1.getStartXValue(0,1),EPSILON);
    assertEquals(3.25,d1.getEndXValue(0,0),EPSILON);
    assertEquals(7.25,d1.getEndXValue(0,1),EPSILON);
  }
}
