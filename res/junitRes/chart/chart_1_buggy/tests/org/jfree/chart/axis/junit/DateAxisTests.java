package org.jfree.chart.axis.junit;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.jfree.chart.axis.AxisState;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.DateTick;
import org.jfree.chart.axis.DateTickMarkPosition;
import org.jfree.chart.axis.DateTickUnit;
import org.jfree.chart.axis.DateTickUnitType;
import org.jfree.chart.axis.SegmentedTimeline;
import org.jfree.chart.util.RectangleEdge;
import org.jfree.data.time.DateRange;
import org.jfree.data.time.Day;
import org.jfree.data.time.Hour;
import org.jfree.data.time.Millisecond;
import org.jfree.data.time.Month;
import org.jfree.data.time.Second;
import org.jfree.data.time.Year;
/** 
 * Tests for the             {@link DateAxis} class.
 */
public class DateAxisTests extends TestCase {
static class MyDateAxis extends DateAxis {
    /** 
 * Creates a new instance.
 * @param label  the label.
 */
    public MyDateAxis(    String label){
      super(label);
    }
    public Date previousStandardDate(    Date d,    DateTickUnit unit){
      return super.previousStandardDate(d,unit);
    }
  }
  /** 
 * Returns the tests as a test suite.
 * @return The test suite.
 */
  public static Test suite(){
    return new TestSuite(DateAxisTests.class);
  }
  /** 
 * Constructs a new set of tests.
 * @param name  the name of the tests.
 */
  public DateAxisTests(  String name){
    super(name);
  }
  /** 
 * Confirm that the equals method can distinguish all the required fields.
 */
  public void testEquals(){
    DateAxis a1=new DateAxis("Test");
    DateAxis a2=new DateAxis("Test");
    assertTrue(a1.equals(a2));
    assertFalse(a1.equals(null));
    assertFalse(a1.equals("Some non-DateAxis object"));
    a1.setTickUnit(new DateTickUnit(DateTickUnitType.DAY,7));
    assertFalse(a1.equals(a2));
    a2.setTickUnit(new DateTickUnit(DateTickUnitType.DAY,7));
    assertTrue(a1.equals(a2));
    a1.setDateFormatOverride(new SimpleDateFormat("yyyy"));
    assertFalse(a1.equals(a2));
    a2.setDateFormatOverride(new SimpleDateFormat("yyyy"));
    assertTrue(a1.equals(a2));
    a1.setTickMarkPosition(DateTickMarkPosition.END);
    assertFalse(a1.equals(a2));
    a2.setTickMarkPosition(DateTickMarkPosition.END);
    assertTrue(a1.equals(a2));
    a1.setTimeline(SegmentedTimeline.newMondayThroughFridayTimeline());
    assertFalse(a1.equals(a2));
    a2.setTimeline(SegmentedTimeline.newMondayThroughFridayTimeline());
    assertTrue(a1.equals(a2));
  }
  /** 
 * A test for bug report 1472942.  The DateFormat.equals() method is not checking the range attribute.
 */
  public void test1472942(){
    DateAxis a1=new DateAxis("Test");
    DateAxis a2=new DateAxis("Test");
    assertTrue(a1.equals(a2));
    a1.setRange(new Date(1L),new Date(2L));
    assertFalse(a1.equals(a2));
    a2.setRange(new Date(1L),new Date(2L));
    assertTrue(a1.equals(a2));
  }
  /** 
 * Two objects that are equal are required to return the same hashCode.
 */
  public void testHashCode(){
    DateAxis a1=new DateAxis("Test");
    DateAxis a2=new DateAxis("Test");
    assertTrue(a1.equals(a2));
    int h1=a1.hashCode();
    int h2=a2.hashCode();
    assertEquals(h1,h2);
  }
  /** 
 * Confirm that cloning works.
 */
  public void testCloning(){
    DateAxis a1=new DateAxis("Test");
    DateAxis a2=null;
    try {
      a2=(DateAxis)a1.clone();
    }
 catch (    CloneNotSupportedException e) {
      e.printStackTrace();
    }
    assertTrue(a1 != a2);
    assertTrue(a1.getClass() == a2.getClass());
    assertTrue(a1.equals(a2));
  }
  /** 
 * Test that the setRange() method works.
 */
  public void testSetRange(){
    DateAxis axis=new DateAxis("Test Axis");
    Calendar calendar=Calendar.getInstance();
    calendar.set(1999,Calendar.JANUARY,3);
    Date d1=calendar.getTime();
    calendar.set(1999,Calendar.JANUARY,31);
    Date d2=calendar.getTime();
    axis.setRange(d1,d2);
    DateRange range=(DateRange)axis.getRange();
    assertEquals(d1,range.getLowerDate());
    assertEquals(d2,range.getUpperDate());
  }
  /** 
 * Test that the setMaximumDate() method works.
 */
  public void testSetMaximumDate(){
    DateAxis axis=new DateAxis("Test Axis");
    Date date=new Date();
    axis.setMaximumDate(date);
    assertEquals(date,axis.getMaximumDate());
    Date d1=new Date();
    Date d2=new Date(d1.getTime() + 1);
    Date d0=new Date(d1.getTime() - 1);
    axis.setMaximumDate(d2);
    axis.setMinimumDate(d1);
    axis.setMaximumDate(d1);
    assertEquals(d0,axis.getMinimumDate());
  }
  /** 
 * Test that the setMinimumDate() method works.
 */
  public void testSetMinimumDate(){
    DateAxis axis=new DateAxis("Test Axis");
    Date d1=new Date();
    Date d2=new Date(d1.getTime() + 1);
    axis.setMaximumDate(d2);
    axis.setMinimumDate(d1);
    assertEquals(d1,axis.getMinimumDate());
    Date d3=new Date(d2.getTime() + 1);
    axis.setMinimumDate(d2);
    assertEquals(d3,axis.getMaximumDate());
  }
  /** 
 * Tests two doubles for 'near enough' equality.
 * @param d1  number 1.
 * @param d2  number 2.
 * @param tolerance  maximum tolerance.
 * @return A boolean.
 */
  private boolean same(  double d1,  double d2,  double tolerance){
    return (Math.abs(d1 - d2) < tolerance);
  }
  /** 
 * Test the translation of Java2D values to data values.
 */
  public void testJava2DToValue(){
    DateAxis axis=new DateAxis();
    axis.setRange(50.0,100.0);
    Rectangle2D dataArea=new Rectangle2D.Double(10.0,50.0,400.0,300.0);
    double y1=axis.java2DToValue(75.0,dataArea,RectangleEdge.LEFT);
    assertTrue(same(y1,95.8333333,1.0));
    double y2=axis.java2DToValue(75.0,dataArea,RectangleEdge.RIGHT);
    assertTrue(same(y2,95.8333333,1.0));
    double x1=axis.java2DToValue(75.0,dataArea,RectangleEdge.TOP);
    assertTrue(same(x1,58.125,1.0));
    double x2=axis.java2DToValue(75.0,dataArea,RectangleEdge.BOTTOM);
    assertTrue(same(x2,58.125,1.0));
    axis.setInverted(true);
    double y3=axis.java2DToValue(75.0,dataArea,RectangleEdge.LEFT);
    assertTrue(same(y3,54.1666667,1.0));
    double y4=axis.java2DToValue(75.0,dataArea,RectangleEdge.RIGHT);
    assertTrue(same(y4,54.1666667,1.0));
    double x3=axis.java2DToValue(75.0,dataArea,RectangleEdge.TOP);
    assertTrue(same(x3,91.875,1.0));
    double x4=axis.java2DToValue(75.0,dataArea,RectangleEdge.BOTTOM);
    assertTrue(same(x4,91.875,1.0));
  }
  /** 
 * Serialize an instance, restore it, and check for equality.
 */
  public void testSerialization(){
    DateAxis a1=new DateAxis("Test Axis");
    DateAxis a2=null;
    try {
      ByteArrayOutputStream buffer=new ByteArrayOutputStream();
      ObjectOutput out=new ObjectOutputStream(buffer);
      out.writeObject(a1);
      out.close();
      ObjectInput in=new ObjectInputStream(new ByteArrayInputStream(buffer.toByteArray()));
      a2=(DateAxis)in.readObject();
      in.close();
    }
 catch (    Exception e) {
      e.printStackTrace();
    }
    boolean b=a1.equals(a2);
    assertTrue(b);
  }
  /** 
 * A basic check for the testPreviousStandardDate() method when the tick unit is 1 year.
 */
  public void testPreviousStandardDateYearA(){
    MyDateAxis axis=new MyDateAxis("Year");
    Year y2006=new Year(2006);
    Year y2007=new Year(2007);
    Date d0=new Date(y2006.getFirstMillisecond());
    Date d1=new Date(y2006.getFirstMillisecond() + 500L);
    Date d2=new Date(y2006.getMiddleMillisecond());
    Date d3=new Date(y2006.getMiddleMillisecond() + 500L);
    Date d4=new Date(y2006.getLastMillisecond());
    Date end=new Date(y2007.getLastMillisecond());
    DateTickUnit unit=new DateTickUnit(DateTickUnitType.YEAR,1);
    axis.setTickUnit(unit);
    axis.setTickMarkPosition(DateTickMarkPosition.START);
    axis.setRange(d0,end);
    Date psd=axis.previousStandardDate(d0,unit);
    Date nsd=unit.addToDate(psd,TimeZone.getDefault());
    assertTrue(psd.getTime() < d0.getTime());
    assertTrue(nsd.getTime() >= d0.getTime());
    axis.setRange(d1,end);
    psd=axis.previousStandardDate(d1,unit);
    nsd=unit.addToDate(psd,TimeZone.getDefault());
    assertTrue(psd.getTime() < d1.getTime());
    assertTrue(nsd.getTime() >= d1.getTime());
    axis.setTickMarkPosition(DateTickMarkPosition.MIDDLE);
    axis.setRange(d1,end);
    psd=axis.previousStandardDate(d1,unit);
    nsd=unit.addToDate(psd,TimeZone.getDefault());
    assertTrue(psd.getTime() < d1.getTime());
    assertTrue(nsd.getTime() >= d1.getTime());
    axis.setRange(d2,end);
    psd=axis.previousStandardDate(d2,unit);
    nsd=unit.addToDate(psd,TimeZone.getDefault());
    assertTrue(psd.getTime() < d2.getTime());
    assertTrue(nsd.getTime() >= d2.getTime());
    axis.setRange(d3,end);
    psd=axis.previousStandardDate(d3,unit);
    nsd=unit.addToDate(psd,TimeZone.getDefault());
    assertTrue(psd.getTime() < d3.getTime());
    assertTrue(nsd.getTime() >= d3.getTime());
    axis.setTickMarkPosition(DateTickMarkPosition.END);
    axis.setRange(d3,end);
    psd=axis.previousStandardDate(d3,unit);
    nsd=unit.addToDate(psd,TimeZone.getDefault());
    assertTrue(psd.getTime() < d3.getTime());
    assertTrue(nsd.getTime() >= d3.getTime());
    axis.setRange(d4,end);
    psd=axis.previousStandardDate(d4,unit);
    nsd=unit.addToDate(psd,TimeZone.getDefault());
    assertTrue(psd.getTime() < d4.getTime());
    assertTrue(nsd.getTime() >= d4.getTime());
  }
  /** 
 * A basic check for the testPreviousStandardDate() method when the tick unit is 10 years (just for the sake of having a multiple).
 */
  public void testPreviousStandardDateYearB(){
    MyDateAxis axis=new MyDateAxis("Year");
    Year y2006=new Year(2006);
    Year y2007=new Year(2007);
    Date d0=new Date(y2006.getFirstMillisecond());
    Date d1=new Date(y2006.getFirstMillisecond() + 500L);
    Date d2=new Date(y2006.getMiddleMillisecond());
    Date d3=new Date(y2006.getMiddleMillisecond() + 500L);
    Date d4=new Date(y2006.getLastMillisecond());
    Date end=new Date(y2007.getLastMillisecond());
    DateTickUnit unit=new DateTickUnit(DateTickUnitType.YEAR,10);
    axis.setTickUnit(unit);
    axis.setTickMarkPosition(DateTickMarkPosition.START);
    axis.setRange(d0,end);
    Date psd=axis.previousStandardDate(d0,unit);
    Date nsd=unit.addToDate(psd,TimeZone.getDefault());
    assertTrue(psd.getTime() < d0.getTime());
    assertTrue(nsd.getTime() >= d0.getTime());
    axis.setRange(d1,end);
    psd=axis.previousStandardDate(d1,unit);
    nsd=unit.addToDate(psd,TimeZone.getDefault());
    assertTrue(psd.getTime() < d1.getTime());
    assertTrue(nsd.getTime() >= d1.getTime());
    axis.setTickMarkPosition(DateTickMarkPosition.MIDDLE);
    axis.setRange(d1,end);
    psd=axis.previousStandardDate(d1,unit);
    nsd=unit.addToDate(psd,TimeZone.getDefault());
    assertTrue(psd.getTime() < d1.getTime());
    assertTrue(nsd.getTime() >= d1.getTime());
    axis.setRange(d2,end);
    psd=axis.previousStandardDate(d2,unit);
    nsd=unit.addToDate(psd,TimeZone.getDefault());
    assertTrue(psd.getTime() < d2.getTime());
    assertTrue(nsd.getTime() >= d2.getTime());
    axis.setRange(d3,end);
    psd=axis.previousStandardDate(d3,unit);
    nsd=unit.addToDate(psd,TimeZone.getDefault());
    assertTrue(psd.getTime() < d3.getTime());
    assertTrue(nsd.getTime() >= d3.getTime());
    axis.setTickMarkPosition(DateTickMarkPosition.END);
    axis.setRange(d3,end);
    psd=axis.previousStandardDate(d3,unit);
    nsd=unit.addToDate(psd,TimeZone.getDefault());
    assertTrue(psd.getTime() < d3.getTime());
    assertTrue(nsd.getTime() >= d3.getTime());
    axis.setRange(d4,end);
    psd=axis.previousStandardDate(d4,unit);
    nsd=unit.addToDate(psd,TimeZone.getDefault());
    assertTrue(psd.getTime() < d4.getTime());
    assertTrue(nsd.getTime() >= d4.getTime());
  }
  /** 
 * A basic check for the testPreviousStandardDate() method when the tick unit is 1 month.
 */
  public void testPreviousStandardDateMonthA(){
    MyDateAxis axis=new MyDateAxis("Month");
    Month nov2006=new Month(11,2006);
    Month dec2006=new Month(12,2006);
    Date d0=new Date(nov2006.getFirstMillisecond());
    Date d1=new Date(nov2006.getFirstMillisecond() + 500L);
    Date d2=new Date(nov2006.getMiddleMillisecond());
    Date d3=new Date(nov2006.getMiddleMillisecond() + 500L);
    Date d4=new Date(nov2006.getLastMillisecond());
    Date end=new Date(dec2006.getLastMillisecond());
    DateTickUnit unit=new DateTickUnit(DateTickUnitType.MONTH,1);
    axis.setTickUnit(unit);
    axis.setTickMarkPosition(DateTickMarkPosition.START);
    axis.setRange(d0,end);
    Date psd=axis.previousStandardDate(d0,unit);
    Date nsd=unit.addToDate(psd,TimeZone.getDefault());
    assertTrue(psd.getTime() < d0.getTime());
    assertTrue(nsd.getTime() >= d0.getTime());
    axis.setRange(d1,end);
    psd=axis.previousStandardDate(d1,unit);
    nsd=unit.addToDate(psd,TimeZone.getDefault());
    assertTrue(psd.getTime() < d1.getTime());
    assertTrue(nsd.getTime() >= d1.getTime());
    axis.setTickMarkPosition(DateTickMarkPosition.MIDDLE);
    axis.setRange(d1,end);
    psd=axis.previousStandardDate(d1,unit);
    nsd=unit.addToDate(psd,TimeZone.getDefault());
    assertTrue(psd.getTime() < d1.getTime());
    assertTrue(nsd.getTime() >= d1.getTime());
    axis.setRange(d2,end);
    psd=axis.previousStandardDate(d2,unit);
    nsd=unit.addToDate(psd,TimeZone.getDefault());
    assertTrue(psd.getTime() < d2.getTime());
    assertTrue(nsd.getTime() >= d2.getTime());
    axis.setRange(d3,end);
    psd=axis.previousStandardDate(d3,unit);
    nsd=unit.addToDate(psd,TimeZone.getDefault());
    assertTrue(psd.getTime() < d3.getTime());
    assertTrue(nsd.getTime() >= d3.getTime());
    axis.setTickMarkPosition(DateTickMarkPosition.END);
    axis.setRange(d3,end);
    psd=axis.previousStandardDate(d3,unit);
    nsd=unit.addToDate(psd,TimeZone.getDefault());
    assertTrue(psd.getTime() < d3.getTime());
    assertTrue(nsd.getTime() >= d3.getTime());
    axis.setRange(d4,end);
    psd=axis.previousStandardDate(d4,unit);
    nsd=unit.addToDate(psd,TimeZone.getDefault());
    assertTrue(psd.getTime() < d4.getTime());
    assertTrue(nsd.getTime() >= d4.getTime());
  }
  /** 
 * A basic check for the testPreviousStandardDate() method when the tick unit is 3 months (just for the sake of having a multiple).
 */
  public void testPreviousStandardDateMonthB(){
    MyDateAxis axis=new MyDateAxis("Month");
    Month nov2006=new Month(11,2006);
    Month dec2006=new Month(12,2006);
    Date d0=new Date(nov2006.getFirstMillisecond());
    Date d1=new Date(nov2006.getFirstMillisecond() + 500L);
    Date d2=new Date(nov2006.getMiddleMillisecond());
    Date d3=new Date(nov2006.getMiddleMillisecond() + 500L);
    Date d4=new Date(nov2006.getLastMillisecond());
    Date end=new Date(dec2006.getLastMillisecond());
    DateTickUnit unit=new DateTickUnit(DateTickUnitType.MONTH,3);
    axis.setTickUnit(unit);
    axis.setTickMarkPosition(DateTickMarkPosition.START);
    axis.setRange(d0,end);
    Date psd=axis.previousStandardDate(d0,unit);
    Date nsd=unit.addToDate(psd,TimeZone.getDefault());
    assertTrue(psd.getTime() < d0.getTime());
    assertTrue(nsd.getTime() >= d0.getTime());
    axis.setRange(d1,end);
    psd=axis.previousStandardDate(d1,unit);
    nsd=unit.addToDate(psd,TimeZone.getDefault());
    assertTrue(psd.getTime() < d1.getTime());
    assertTrue(nsd.getTime() >= d1.getTime());
    axis.setTickMarkPosition(DateTickMarkPosition.MIDDLE);
    axis.setRange(d1,end);
    psd=axis.previousStandardDate(d1,unit);
    nsd=unit.addToDate(psd,TimeZone.getDefault());
    assertTrue(psd.getTime() < d1.getTime());
    assertTrue(nsd.getTime() >= d1.getTime());
    axis.setRange(d2,end);
    psd=axis.previousStandardDate(d2,unit);
    nsd=unit.addToDate(psd,TimeZone.getDefault());
    assertTrue(psd.getTime() < d2.getTime());
    assertTrue(nsd.getTime() >= d2.getTime());
    axis.setRange(d3,end);
    psd=axis.previousStandardDate(d3,unit);
    nsd=unit.addToDate(psd,TimeZone.getDefault());
    assertTrue(psd.getTime() < d3.getTime());
    assertTrue(nsd.getTime() >= d3.getTime());
    axis.setTickMarkPosition(DateTickMarkPosition.END);
    axis.setRange(d3,end);
    psd=axis.previousStandardDate(d3,unit);
    nsd=unit.addToDate(psd,TimeZone.getDefault());
    assertTrue(psd.getTime() < d3.getTime());
    assertTrue(nsd.getTime() >= d3.getTime());
    axis.setRange(d4,end);
    psd=axis.previousStandardDate(d4,unit);
    nsd=unit.addToDate(psd,TimeZone.getDefault());
    assertTrue(psd.getTime() < d4.getTime());
    assertTrue(nsd.getTime() >= d4.getTime());
  }
  /** 
 * A basic check for the testPreviousStandardDate() method when the tick unit is 1 day.
 */
  public void testPreviousStandardDateDayA(){
    MyDateAxis axis=new MyDateAxis("Day");
    Day apr12007=new Day(1,4,2007);
    Day apr22007=new Day(2,4,2007);
    Date d0=new Date(apr12007.getFirstMillisecond());
    Date d1=new Date(apr12007.getFirstMillisecond() + 500L);
    Date d2=new Date(apr12007.getMiddleMillisecond());
    Date d3=new Date(apr12007.getMiddleMillisecond() + 500L);
    Date d4=new Date(apr12007.getLastMillisecond());
    Date end=new Date(apr22007.getLastMillisecond());
    DateTickUnit unit=new DateTickUnit(DateTickUnitType.DAY,1);
    axis.setTickUnit(unit);
    axis.setTickMarkPosition(DateTickMarkPosition.START);
    axis.setRange(d0,end);
    Date psd=axis.previousStandardDate(d0,unit);
    Date nsd=unit.addToDate(psd,TimeZone.getDefault());
    assertTrue(psd.getTime() < d0.getTime());
    assertTrue(nsd.getTime() >= d0.getTime());
    axis.setRange(d1,end);
    psd=axis.previousStandardDate(d1,unit);
    nsd=unit.addToDate(psd,TimeZone.getDefault());
    assertTrue(psd.getTime() < d1.getTime());
    assertTrue(nsd.getTime() >= d1.getTime());
    axis.setTickMarkPosition(DateTickMarkPosition.MIDDLE);
    axis.setRange(d1,end);
    psd=axis.previousStandardDate(d1,unit);
    nsd=unit.addToDate(psd,TimeZone.getDefault());
    assertTrue(psd.getTime() < d1.getTime());
    assertTrue(nsd.getTime() >= d1.getTime());
    axis.setRange(d2,end);
    psd=axis.previousStandardDate(d2,unit);
    nsd=unit.addToDate(psd,TimeZone.getDefault());
    assertTrue(psd.getTime() < d2.getTime());
    assertTrue(nsd.getTime() >= d2.getTime());
    axis.setRange(d3,end);
    psd=axis.previousStandardDate(d3,unit);
    nsd=unit.addToDate(psd,TimeZone.getDefault());
    assertTrue(psd.getTime() < d3.getTime());
    assertTrue(nsd.getTime() >= d3.getTime());
    axis.setTickMarkPosition(DateTickMarkPosition.END);
    axis.setRange(d3,end);
    psd=axis.previousStandardDate(d3,unit);
    nsd=unit.addToDate(psd,TimeZone.getDefault());
    assertTrue(psd.getTime() < d3.getTime());
    assertTrue(nsd.getTime() >= d3.getTime());
    axis.setRange(d4,end);
    psd=axis.previousStandardDate(d4,unit);
    nsd=unit.addToDate(psd,TimeZone.getDefault());
    assertTrue(psd.getTime() < d4.getTime());
    assertTrue(nsd.getTime() >= d4.getTime());
  }
  /** 
 * A basic check for the testPreviousStandardDate() method when the tick unit is 7 days (just for the sake of having a multiple).
 */
  public void testPreviousStandardDateDayB(){
    MyDateAxis axis=new MyDateAxis("Day");
    Day apr12007=new Day(1,4,2007);
    Day apr22007=new Day(2,4,2007);
    Date d0=new Date(apr12007.getFirstMillisecond());
    Date d1=new Date(apr12007.getFirstMillisecond() + 500L);
    Date d2=new Date(apr12007.getMiddleMillisecond());
    Date d3=new Date(apr12007.getMiddleMillisecond() + 500L);
    Date d4=new Date(apr12007.getLastMillisecond());
    Date end=new Date(apr22007.getLastMillisecond());
    DateTickUnit unit=new DateTickUnit(DateTickUnitType.DAY,7);
    axis.setTickUnit(unit);
    axis.setTickMarkPosition(DateTickMarkPosition.START);
    axis.setRange(d0,end);
    Date psd=axis.previousStandardDate(d0,unit);
    Date nsd=unit.addToDate(psd,TimeZone.getDefault());
    assertTrue(psd.getTime() < d0.getTime());
    assertTrue(nsd.getTime() >= d0.getTime());
    axis.setRange(d1,end);
    psd=axis.previousStandardDate(d1,unit);
    nsd=unit.addToDate(psd,TimeZone.getDefault());
    assertTrue(psd.getTime() < d1.getTime());
    assertTrue(nsd.getTime() >= d1.getTime());
    axis.setTickMarkPosition(DateTickMarkPosition.MIDDLE);
    axis.setRange(d1,end);
    psd=axis.previousStandardDate(d1,unit);
    nsd=unit.addToDate(psd,TimeZone.getDefault());
    assertTrue(psd.getTime() < d1.getTime());
    assertTrue(nsd.getTime() >= d1.getTime());
    axis.setRange(d2,end);
    psd=axis.previousStandardDate(d2,unit);
    nsd=unit.addToDate(psd,TimeZone.getDefault());
    assertTrue(psd.getTime() < d2.getTime());
    assertTrue(nsd.getTime() >= d2.getTime());
    axis.setRange(d3,end);
    psd=axis.previousStandardDate(d3,unit);
    nsd=unit.addToDate(psd,TimeZone.getDefault());
    assertTrue(psd.getTime() < d3.getTime());
    assertTrue(nsd.getTime() >= d3.getTime());
    axis.setTickMarkPosition(DateTickMarkPosition.END);
    axis.setRange(d3,end);
    psd=axis.previousStandardDate(d3,unit);
    nsd=unit.addToDate(psd,TimeZone.getDefault());
    assertTrue(psd.getTime() < d3.getTime());
    assertTrue(nsd.getTime() >= d3.getTime());
    axis.setRange(d4,end);
    psd=axis.previousStandardDate(d4,unit);
    nsd=unit.addToDate(psd,TimeZone.getDefault());
    assertTrue(psd.getTime() < d4.getTime());
    assertTrue(nsd.getTime() >= d4.getTime());
  }
  /** 
 * A basic check for the testPreviousStandardDate() method when the tick unit is 1 hour.
 */
  public void testPreviousStandardDateHourA(){
    MyDateAxis axis=new MyDateAxis("Hour");
    Hour h0=new Hour(12,1,4,2007);
    Hour h1=new Hour(13,1,4,2007);
    Date d0=new Date(h0.getFirstMillisecond());
    Date d1=new Date(h0.getFirstMillisecond() + 500L);
    Date d2=new Date(h0.getMiddleMillisecond());
    Date d3=new Date(h0.getMiddleMillisecond() + 500L);
    Date d4=new Date(h0.getLastMillisecond());
    Date end=new Date(h1.getLastMillisecond());
    DateTickUnit unit=new DateTickUnit(DateTickUnitType.HOUR,1);
    axis.setTickUnit(unit);
    axis.setTickMarkPosition(DateTickMarkPosition.START);
    axis.setRange(d0,end);
    Date psd=axis.previousStandardDate(d0,unit);
    Date nsd=unit.addToDate(psd,TimeZone.getDefault());
    assertTrue(psd.getTime() < d0.getTime());
    assertTrue(nsd.getTime() >= d0.getTime());
    axis.setRange(d1,end);
    psd=axis.previousStandardDate(d1,unit);
    nsd=unit.addToDate(psd,TimeZone.getDefault());
    assertTrue(psd.getTime() < d1.getTime());
    assertTrue(nsd.getTime() >= d1.getTime());
    axis.setTickMarkPosition(DateTickMarkPosition.MIDDLE);
    axis.setRange(d1,end);
    psd=axis.previousStandardDate(d1,unit);
    nsd=unit.addToDate(psd,TimeZone.getDefault());
    assertTrue(psd.getTime() < d1.getTime());
    assertTrue(nsd.getTime() >= d1.getTime());
    axis.setRange(d2,end);
    psd=axis.previousStandardDate(d2,unit);
    nsd=unit.addToDate(psd,TimeZone.getDefault());
    assertTrue(psd.getTime() < d2.getTime());
    assertTrue(nsd.getTime() >= d2.getTime());
    axis.setRange(d3,end);
    psd=axis.previousStandardDate(d3,unit);
    nsd=unit.addToDate(psd,TimeZone.getDefault());
    assertTrue(psd.getTime() < d3.getTime());
    assertTrue(nsd.getTime() >= d3.getTime());
    axis.setTickMarkPosition(DateTickMarkPosition.END);
    axis.setRange(d3,end);
    psd=axis.previousStandardDate(d3,unit);
    nsd=unit.addToDate(psd,TimeZone.getDefault());
    assertTrue(psd.getTime() < d3.getTime());
    assertTrue(nsd.getTime() >= d3.getTime());
    axis.setRange(d4,end);
    psd=axis.previousStandardDate(d4,unit);
    nsd=unit.addToDate(psd,TimeZone.getDefault());
    assertTrue(psd.getTime() < d4.getTime());
    assertTrue(nsd.getTime() >= d4.getTime());
  }
  /** 
 * A basic check for the testPreviousStandardDate() method when the tick unit is 6 hours (just for the sake of having a multiple).
 */
  public void testPreviousStandardDateHourB(){
    MyDateAxis axis=new MyDateAxis("Hour");
    Hour h0=new Hour(12,1,4,2007);
    Hour h1=new Hour(13,1,4,2007);
    Date d0=new Date(h0.getFirstMillisecond());
    Date d1=new Date(h0.getFirstMillisecond() + 500L);
    Date d2=new Date(h0.getMiddleMillisecond());
    Date d3=new Date(h0.getMiddleMillisecond() + 500L);
    Date d4=new Date(h0.getLastMillisecond());
    Date end=new Date(h1.getLastMillisecond());
    DateTickUnit unit=new DateTickUnit(DateTickUnitType.HOUR,6);
    axis.setTickUnit(unit);
    axis.setTickMarkPosition(DateTickMarkPosition.START);
    axis.setRange(d0,end);
    Date psd=axis.previousStandardDate(d0,unit);
    Date nsd=unit.addToDate(psd,TimeZone.getDefault());
    assertTrue(psd.getTime() < d0.getTime());
    assertTrue(nsd.getTime() >= d0.getTime());
    axis.setRange(d1,end);
    psd=axis.previousStandardDate(d1,unit);
    nsd=unit.addToDate(psd,TimeZone.getDefault());
    assertTrue(psd.getTime() < d1.getTime());
    assertTrue(nsd.getTime() >= d1.getTime());
    axis.setTickMarkPosition(DateTickMarkPosition.MIDDLE);
    axis.setRange(d1,end);
    psd=axis.previousStandardDate(d1,unit);
    nsd=unit.addToDate(psd,TimeZone.getDefault());
    assertTrue(psd.getTime() < d1.getTime());
    assertTrue(nsd.getTime() >= d1.getTime());
    axis.setRange(d2,end);
    psd=axis.previousStandardDate(d2,unit);
    nsd=unit.addToDate(psd,TimeZone.getDefault());
    assertTrue(psd.getTime() < d2.getTime());
    assertTrue(nsd.getTime() >= d2.getTime());
    axis.setRange(d3,end);
    psd=axis.previousStandardDate(d3,unit);
    nsd=unit.addToDate(psd,TimeZone.getDefault());
    assertTrue(psd.getTime() < d3.getTime());
    assertTrue(nsd.getTime() >= d3.getTime());
    axis.setTickMarkPosition(DateTickMarkPosition.END);
    axis.setRange(d3,end);
    psd=axis.previousStandardDate(d3,unit);
    nsd=unit.addToDate(psd,TimeZone.getDefault());
    assertTrue(psd.getTime() < d3.getTime());
    assertTrue(nsd.getTime() >= d3.getTime());
    axis.setRange(d4,end);
    psd=axis.previousStandardDate(d4,unit);
    nsd=unit.addToDate(psd,TimeZone.getDefault());
    assertTrue(psd.getTime() < d4.getTime());
    assertTrue(nsd.getTime() >= d4.getTime());
  }
  /** 
 * A basic check for the testPreviousStandardDate() method when the tick unit is 1 second.
 */
  public void testPreviousStandardDateSecondA(){
    MyDateAxis axis=new MyDateAxis("Second");
    Second s0=new Second(58,31,12,1,4,2007);
    Second s1=new Second(59,31,12,1,4,2007);
    Date d0=new Date(s0.getFirstMillisecond());
    Date d1=new Date(s0.getFirstMillisecond() + 50L);
    Date d2=new Date(s0.getMiddleMillisecond());
    Date d3=new Date(s0.getMiddleMillisecond() + 50L);
    Date d4=new Date(s0.getLastMillisecond());
    Date end=new Date(s1.getLastMillisecond());
    DateTickUnit unit=new DateTickUnit(DateTickUnitType.SECOND,1);
    axis.setTickUnit(unit);
    axis.setTickMarkPosition(DateTickMarkPosition.START);
    axis.setRange(d0,end);
    Date psd=axis.previousStandardDate(d0,unit);
    Date nsd=unit.addToDate(psd,TimeZone.getDefault());
    assertTrue(psd.getTime() < d0.getTime());
    assertTrue(nsd.getTime() >= d0.getTime());
    axis.setRange(d1,end);
    psd=axis.previousStandardDate(d1,unit);
    nsd=unit.addToDate(psd,TimeZone.getDefault());
    assertTrue(psd.getTime() < d1.getTime());
    assertTrue(nsd.getTime() >= d1.getTime());
    axis.setTickMarkPosition(DateTickMarkPosition.MIDDLE);
    axis.setRange(d1,end);
    psd=axis.previousStandardDate(d1,unit);
    nsd=unit.addToDate(psd,TimeZone.getDefault());
    assertTrue(psd.getTime() < d1.getTime());
    assertTrue(nsd.getTime() >= d1.getTime());
    axis.setRange(d2,end);
    psd=axis.previousStandardDate(d2,unit);
    nsd=unit.addToDate(psd,TimeZone.getDefault());
    assertTrue(psd.getTime() < d2.getTime());
    assertTrue(nsd.getTime() >= d2.getTime());
    axis.setRange(d3,end);
    psd=axis.previousStandardDate(d3,unit);
    nsd=unit.addToDate(psd,TimeZone.getDefault());
    assertTrue(psd.getTime() < d3.getTime());
    assertTrue(nsd.getTime() >= d3.getTime());
    axis.setTickMarkPosition(DateTickMarkPosition.END);
    axis.setRange(d3,end);
    psd=axis.previousStandardDate(d3,unit);
    nsd=unit.addToDate(psd,TimeZone.getDefault());
    assertTrue(psd.getTime() < d3.getTime());
    assertTrue(nsd.getTime() >= d3.getTime());
    axis.setRange(d4,end);
    psd=axis.previousStandardDate(d4,unit);
    nsd=unit.addToDate(psd,TimeZone.getDefault());
    assertTrue(psd.getTime() < d4.getTime());
    assertTrue(nsd.getTime() >= d4.getTime());
  }
  /** 
 * A basic check for the testPreviousStandardDate() method when the tick unit is 5 seconds (just for the sake of having a multiple).
 */
  public void testPreviousStandardDateSecondB(){
    MyDateAxis axis=new MyDateAxis("Second");
    Second s0=new Second(58,31,12,1,4,2007);
    Second s1=new Second(59,31,12,1,4,2007);
    Date d0=new Date(s0.getFirstMillisecond());
    Date d1=new Date(s0.getFirstMillisecond() + 50L);
    Date d2=new Date(s0.getMiddleMillisecond());
    Date d3=new Date(s0.getMiddleMillisecond() + 50L);
    Date d4=new Date(s0.getLastMillisecond());
    Date end=new Date(s1.getLastMillisecond());
    DateTickUnit unit=new DateTickUnit(DateTickUnitType.SECOND,5);
    axis.setTickUnit(unit);
    axis.setTickMarkPosition(DateTickMarkPosition.START);
    axis.setRange(d0,end);
    Date psd=axis.previousStandardDate(d0,unit);
    Date nsd=unit.addToDate(psd,TimeZone.getDefault());
    assertTrue(psd.getTime() < d0.getTime());
    assertTrue(nsd.getTime() >= d0.getTime());
    axis.setRange(d1,end);
    psd=axis.previousStandardDate(d1,unit);
    nsd=unit.addToDate(psd,TimeZone.getDefault());
    assertTrue(psd.getTime() < d1.getTime());
    assertTrue(nsd.getTime() >= d1.getTime());
    axis.setTickMarkPosition(DateTickMarkPosition.MIDDLE);
    axis.setRange(d1,end);
    psd=axis.previousStandardDate(d1,unit);
    nsd=unit.addToDate(psd,TimeZone.getDefault());
    assertTrue(psd.getTime() < d1.getTime());
    assertTrue(nsd.getTime() >= d1.getTime());
    axis.setRange(d2,end);
    psd=axis.previousStandardDate(d2,unit);
    nsd=unit.addToDate(psd,TimeZone.getDefault());
    assertTrue(psd.getTime() < d2.getTime());
    assertTrue(nsd.getTime() >= d2.getTime());
    axis.setRange(d3,end);
    psd=axis.previousStandardDate(d3,unit);
    nsd=unit.addToDate(psd,TimeZone.getDefault());
    assertTrue(psd.getTime() < d3.getTime());
    assertTrue(nsd.getTime() >= d3.getTime());
    axis.setTickMarkPosition(DateTickMarkPosition.END);
    axis.setRange(d3,end);
    psd=axis.previousStandardDate(d3,unit);
    nsd=unit.addToDate(psd,TimeZone.getDefault());
    assertTrue(psd.getTime() < d3.getTime());
    assertTrue(nsd.getTime() >= d3.getTime());
    axis.setRange(d4,end);
    psd=axis.previousStandardDate(d4,unit);
    nsd=unit.addToDate(psd,TimeZone.getDefault());
    assertTrue(psd.getTime() < d4.getTime());
    assertTrue(nsd.getTime() >= d4.getTime());
  }
  /** 
 * A basic check for the testPreviousStandardDate() method when the tick unit is 1 millisecond.
 */
  public void testPreviousStandardDateMillisecondA(){
    MyDateAxis axis=new MyDateAxis("Millisecond");
    Millisecond m0=new Millisecond(458,58,31,12,1,4,2007);
    Millisecond m1=new Millisecond(459,58,31,12,1,4,2007);
    Date d0=new Date(m0.getFirstMillisecond());
    Date end=new Date(m1.getLastMillisecond());
    DateTickUnit unit=new DateTickUnit(DateTickUnitType.MILLISECOND,1);
    axis.setTickUnit(unit);
    axis.setTickMarkPosition(DateTickMarkPosition.START);
    axis.setRange(d0,end);
    Date psd=axis.previousStandardDate(d0,unit);
    Date nsd=unit.addToDate(psd,TimeZone.getDefault());
    assertTrue(psd.getTime() < d0.getTime());
    assertTrue(nsd.getTime() >= d0.getTime());
    axis.setTickMarkPosition(DateTickMarkPosition.MIDDLE);
    axis.setRange(d0,end);
    psd=axis.previousStandardDate(d0,unit);
    nsd=unit.addToDate(psd,TimeZone.getDefault());
    assertTrue(psd.getTime() < d0.getTime());
    assertTrue(nsd.getTime() >= d0.getTime());
    axis.setTickMarkPosition(DateTickMarkPosition.END);
    axis.setRange(d0,end);
    psd=axis.previousStandardDate(d0,unit);
    nsd=unit.addToDate(psd,TimeZone.getDefault());
    assertTrue(psd.getTime() < d0.getTime());
    assertTrue(nsd.getTime() >= d0.getTime());
  }
  /** 
 * A basic check for the testPreviousStandardDate() method when the tick unit is 10 milliseconds (just for the sake of having a multiple).
 */
  public void testPreviousStandardDateMillisecondB(){
    MyDateAxis axis=new MyDateAxis("Millisecond");
    Millisecond m0=new Millisecond(458,58,31,12,1,4,2007);
    Millisecond m1=new Millisecond(459,58,31,12,1,4,2007);
    Date d0=new Date(m0.getFirstMillisecond());
    Date end=new Date(m1.getLastMillisecond());
    DateTickUnit unit=new DateTickUnit(DateTickUnitType.MILLISECOND,10);
    axis.setTickUnit(unit);
    axis.setTickMarkPosition(DateTickMarkPosition.START);
    axis.setRange(d0,end);
    Date psd=axis.previousStandardDate(d0,unit);
    Date nsd=unit.addToDate(psd,TimeZone.getDefault());
    assertTrue(psd.getTime() < d0.getTime());
    assertTrue(nsd.getTime() >= d0.getTime());
    axis.setTickMarkPosition(DateTickMarkPosition.MIDDLE);
    axis.setRange(d0,end);
    psd=axis.previousStandardDate(d0,unit);
    nsd=unit.addToDate(psd,TimeZone.getDefault());
    assertTrue(psd.getTime() < d0.getTime());
    assertTrue(nsd.getTime() >= d0.getTime());
    axis.setTickMarkPosition(DateTickMarkPosition.END);
    axis.setRange(d0,end);
    psd=axis.previousStandardDate(d0,unit);
    nsd=unit.addToDate(psd,TimeZone.getDefault());
    assertTrue(psd.getTime() < d0.getTime());
    assertTrue(nsd.getTime() >= d0.getTime());
  }
  /** 
 * A test to reproduce bug 2201869.
 */
  public void testBug2201869(){
    TimeZone tz=TimeZone.getTimeZone("GMT");
    GregorianCalendar c=new GregorianCalendar(tz,Locale.UK);
    DateAxis axis=new DateAxis("Date",tz,Locale.UK);
    SimpleDateFormat sdf=new SimpleDateFormat("d-MMM-yyyy",Locale.UK);
    sdf.setCalendar(c);
    axis.setTickUnit(new DateTickUnit(DateTickUnitType.MONTH,1,sdf));
    Day d1=new Day(1,3,2008);
    d1.peg(c);
    Day d2=new Day(30,6,2008);
    d2.peg(c);
    axis.setRange(d1.getStart(),d2.getEnd());
    BufferedImage image=new BufferedImage(200,100,BufferedImage.TYPE_INT_ARGB);
    Graphics2D g2=image.createGraphics();
    Rectangle2D area=new Rectangle2D.Double(0.0,0.0,200,100);
    axis.setTickMarkPosition(DateTickMarkPosition.END);
    List ticks=axis.refreshTicks(g2,new AxisState(),area,RectangleEdge.BOTTOM);
    assertEquals(3,ticks.size());
    DateTick t1=(DateTick)ticks.get(0);
    assertEquals("31-Mar-2008",t1.getText());
    DateTick t2=(DateTick)ticks.get(1);
    assertEquals("30-Apr-2008",t2.getText());
    DateTick t3=(DateTick)ticks.get(2);
    assertEquals("31-May-2008",t3.getText());
    ticks=axis.refreshTicks(g2,new AxisState(),area,RectangleEdge.LEFT);
    assertEquals(3,ticks.size());
    t1=(DateTick)ticks.get(0);
    assertEquals("31-Mar-2008",t1.getText());
    t2=(DateTick)ticks.get(1);
    assertEquals("30-Apr-2008",t2.getText());
    t3=(DateTick)ticks.get(2);
    assertEquals("31-May-2008",t3.getText());
  }
}
