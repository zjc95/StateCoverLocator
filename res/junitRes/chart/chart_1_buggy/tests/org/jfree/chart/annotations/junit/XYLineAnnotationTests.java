package org.jfree.chart.annotations.junit;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Stroke;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.jfree.chart.annotations.XYLineAnnotation;
import org.jfree.chart.util.PublicCloneable;
/** 
 * Tests for the             {@link XYLineAnnotation} class.
 */
public class XYLineAnnotationTests extends TestCase {
  /** 
 * Returns the tests as a test suite.
 * @return The test suite.
 */
  public static Test suite(){
    return new TestSuite(XYLineAnnotationTests.class);
  }
  /** 
 * Constructs a new set of tests.
 * @param name  the name of the tests.
 */
  public XYLineAnnotationTests(  String name){
    super(name);
  }
  /** 
 * Confirm that the equals method can distinguish all the required fields.
 */
  public void testEquals(){
    Stroke stroke=new BasicStroke(2.0f);
    XYLineAnnotation a1=new XYLineAnnotation(10.0,20.0,100.0,200.0,stroke,Color.blue);
    XYLineAnnotation a2=new XYLineAnnotation(10.0,20.0,100.0,200.0,stroke,Color.blue);
    assertTrue(a1.equals(a2));
    assertTrue(a2.equals(a1));
    a1=new XYLineAnnotation(11.0,20.0,100.0,200.0,stroke,Color.blue);
    assertFalse(a1.equals(a2));
    a2=new XYLineAnnotation(11.0,20.0,100.0,200.0,stroke,Color.blue);
    assertTrue(a1.equals(a2));
    a1=new XYLineAnnotation(11.0,21.0,100.0,200.0,stroke,Color.blue);
    assertFalse(a1.equals(a2));
    a2=new XYLineAnnotation(11.0,21.0,100.0,200.0,stroke,Color.blue);
    assertTrue(a1.equals(a2));
    a1=new XYLineAnnotation(11.0,21.0,101.0,200.0,stroke,Color.blue);
    assertFalse(a1.equals(a2));
    a2=new XYLineAnnotation(11.0,21.0,101.0,200.0,stroke,Color.blue);
    assertTrue(a1.equals(a2));
    a1=new XYLineAnnotation(11.0,21.0,101.0,201.0,stroke,Color.blue);
    assertFalse(a1.equals(a2));
    a2=new XYLineAnnotation(11.0,21.0,101.0,201.0,stroke,Color.blue);
    assertTrue(a1.equals(a2));
    Stroke stroke2=new BasicStroke(0.99f);
    a1=new XYLineAnnotation(11.0,21.0,101.0,200.0,stroke2,Color.blue);
    assertFalse(a1.equals(a2));
    a2=new XYLineAnnotation(11.0,21.0,101.0,200.0,stroke2,Color.blue);
    assertTrue(a1.equals(a2));
    GradientPaint g1=new GradientPaint(1.0f,2.0f,Color.red,3.0f,4.0f,Color.white);
    GradientPaint g2=new GradientPaint(1.0f,2.0f,Color.red,3.0f,4.0f,Color.white);
    a1=new XYLineAnnotation(11.0,21.0,101.0,200.0,stroke2,g1);
    assertFalse(a1.equals(a2));
    a2=new XYLineAnnotation(11.0,21.0,101.0,200.0,stroke2,g2);
    assertTrue(a1.equals(a2));
  }
  /** 
 * Two objects that are equal are required to return the same hashCode.
 */
  public void testHashCode(){
    Stroke stroke=new BasicStroke(2.0f);
    XYLineAnnotation a1=new XYLineAnnotation(10.0,20.0,100.0,200.0,stroke,Color.blue);
    XYLineAnnotation a2=new XYLineAnnotation(10.0,20.0,100.0,200.0,stroke,Color.blue);
    assertTrue(a1.equals(a2));
    int h1=a1.hashCode();
    int h2=a2.hashCode();
    assertEquals(h1,h2);
  }
  /** 
 * Confirm that cloning works.
 */
  public void testCloning(){
    Stroke stroke=new BasicStroke(2.0f);
    XYLineAnnotation a1=new XYLineAnnotation(10.0,20.0,100.0,200.0,stroke,Color.blue);
    XYLineAnnotation a2=null;
    try {
      a2=(XYLineAnnotation)a1.clone();
    }
 catch (    CloneNotSupportedException e) {
      System.err.println("Failed to clone.");
    }
    assertTrue(a1 != a2);
    assertTrue(a1.getClass() == a2.getClass());
    assertTrue(a1.equals(a2));
  }
  /** 
 * Checks that this class implements PublicCloneable.
 */
  public void testPublicCloneable(){
    Stroke stroke=new BasicStroke(2.0f);
    XYLineAnnotation a1=new XYLineAnnotation(10.0,20.0,100.0,200.0,stroke,Color.blue);
    assertTrue(a1 instanceof PublicCloneable);
  }
  /** 
 * Serialize an instance, restore it, and check for equality.
 */
  public void testSerialization(){
    Stroke stroke=new BasicStroke(2.0f);
    XYLineAnnotation a1=new XYLineAnnotation(10.0,20.0,100.0,200.0,stroke,Color.blue);
    XYLineAnnotation a2=null;
    try {
      ByteArrayOutputStream buffer=new ByteArrayOutputStream();
      ObjectOutput out=new ObjectOutputStream(buffer);
      out.writeObject(a1);
      out.close();
      ObjectInput in=new ObjectInputStream(new ByteArrayInputStream(buffer.toByteArray()));
      a2=(XYLineAnnotation)in.readObject();
      in.close();
    }
 catch (    Exception e) {
      e.printStackTrace();
    }
    assertEquals(a1,a2);
  }
}
