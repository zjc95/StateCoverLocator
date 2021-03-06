package org.jfree.chart.renderer.xy.junit;
import java.awt.Color;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.Date;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.jfree.chart.renderer.xy.HighLowRenderer;
import org.jfree.chart.util.PublicCloneable;
import org.jfree.data.Range;
import org.jfree.data.xy.DefaultOHLCDataset;
import org.jfree.data.xy.OHLCDataItem;
import org.jfree.data.xy.OHLCDataset;
/** 
 * Tests for the             {@link HighLowRenderer} class.
 */
public class HighLowRendererTests extends TestCase {
  /** 
 * Returns the tests as a test suite.
 * @return The test suite.
 */
  public static Test suite(){
    return new TestSuite(HighLowRendererTests.class);
  }
  /** 
 * Constructs a new set of tests.
 * @param name  the name of the tests.
 */
  public HighLowRendererTests(  String name){
    super(name);
  }
  /** 
 * Check that the equals() method distinguishes all fields.
 */
  public void testEquals(){
    HighLowRenderer r1=new HighLowRenderer();
    HighLowRenderer r2=new HighLowRenderer();
    assertEquals(r1,r2);
    r1.setDrawOpenTicks(false);
    assertFalse(r1.equals(r2));
    r2.setDrawOpenTicks(false);
    assertTrue(r1.equals(r2));
    r1.setDrawCloseTicks(false);
    assertFalse(r1.equals(r2));
    r2.setDrawCloseTicks(false);
    assertTrue(r1.equals(r2));
    r1.setOpenTickPaint(Color.red);
    assertFalse(r1.equals(r2));
    r2.setOpenTickPaint(Color.red);
    assertTrue(r1.equals(r2));
    r1.setCloseTickPaint(Color.blue);
    assertFalse(r1.equals(r2));
    r2.setCloseTickPaint(Color.blue);
    assertTrue(r1.equals(r2));
    r1.setTickLength(99.9);
    assertFalse(r1.equals(r2));
    r2.setTickLength(99.9);
    assertTrue(r1.equals(r2));
  }
  /** 
 * Two objects that are equal are required to return the same hashCode.
 */
  public void testHashcode(){
    HighLowRenderer r1=new HighLowRenderer();
    HighLowRenderer r2=new HighLowRenderer();
    assertTrue(r1.equals(r2));
    int h1=r1.hashCode();
    int h2=r2.hashCode();
    assertEquals(h1,h2);
  }
  /** 
 * Confirm that cloning works.
 */
  public void testCloning(){
    HighLowRenderer r1=new HighLowRenderer();
    r1.setCloseTickPaint(Color.green);
    HighLowRenderer r2=null;
    try {
      r2=(HighLowRenderer)r1.clone();
    }
 catch (    CloneNotSupportedException e) {
      e.printStackTrace();
    }
    assertTrue(r1 != r2);
    assertTrue(r1.getClass() == r2.getClass());
    assertTrue(r1.equals(r2));
  }
  /** 
 * Verify that this class implements             {@link PublicCloneable}.
 */
  public void testPublicCloneable(){
    HighLowRenderer r1=new HighLowRenderer();
    assertTrue(r1 instanceof PublicCloneable);
  }
  /** 
 * Serialize an instance, restore it, and check for equality.
 */
  public void testSerialization(){
    HighLowRenderer r1=new HighLowRenderer();
    r1.setCloseTickPaint(Color.green);
    HighLowRenderer r2=null;
    try {
      ByteArrayOutputStream buffer=new ByteArrayOutputStream();
      ObjectOutput out=new ObjectOutputStream(buffer);
      out.writeObject(r1);
      out.close();
      ObjectInput in=new ObjectInputStream(new ByteArrayInputStream(buffer.toByteArray()));
      r2=(HighLowRenderer)in.readObject();
      in.close();
    }
 catch (    Exception e) {
      e.printStackTrace();
    }
    assertEquals(r1,r2);
  }
  /** 
 * Some checks for the findRangeBounds() method.
 */
  public void testFindRangeBounds(){
    HighLowRenderer renderer=new HighLowRenderer();
    OHLCDataItem item1=new OHLCDataItem(new Date(1L),2.0,4.0,1.0,3.0,100);
    OHLCDataset dataset=new DefaultOHLCDataset("S1",new OHLCDataItem[]{item1});
    Range range=renderer.findRangeBounds(dataset);
    assertEquals(new Range(1.0,4.0),range);
    OHLCDataItem item2=new OHLCDataItem(new Date(1L),-1.0,3.0,-1.0,3.0,100);
    dataset=new DefaultOHLCDataset("S1",new OHLCDataItem[]{item1,item2});
    range=renderer.findRangeBounds(dataset);
    assertEquals(new Range(-1.0,4.0),range);
    dataset=new DefaultOHLCDataset("S1",new OHLCDataItem[]{});
    range=renderer.findRangeBounds(dataset);
    assertNull(range);
    range=renderer.findRangeBounds(null);
    assertNull(range);
  }
}
