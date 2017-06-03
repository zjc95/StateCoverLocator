package org.jfree.data.time;
import java.io.Serializable;
import java.util.Date;
/** 
 * An arbitrary period of time, measured to millisecond precision using <code>java.util.Date</code>. <p> This class is intentionally immutable (that is, once constructed, you cannot alter the start and end attributes).
 */
public class SimpleTimePeriod implements TimePeriod, Comparable, Serializable {
  /** 
 * For serialization. 
 */
  private static final long serialVersionUID=8684672361131829554L;
  /** 
 * The start date/time. 
 */
  private long start;
  /** 
 * The end date/time. 
 */
  private long end;
  /** 
 * Creates a new time allocation.
 * @param start  the start date/time in milliseconds.
 * @param end  the end date/time in milliseconds.
 */
  public SimpleTimePeriod(  long start,  long end){
    if (start > end) {
      throw new IllegalArgumentException("Requires start <= end.");
    }
    this.start=start;
    this.end=end;
  }
  /** 
 * Creates a new time allocation.
 * @param start  the start date/time (<code>null</code> not permitted).
 * @param end  the end date/time (<code>null</code> not permitted).
 */
  public SimpleTimePeriod(  Date start,  Date end){
    this(start.getTime(),end.getTime());
  }
  /** 
 * Returns the start date/time.
 * @return The start date/time (never <code>null</code>).
 */
  public Date getStart(){
    return new Date(this.start);
  }
  /** 
 * Returns the start date/time in milliseconds.
 * @return The start.
 * @since 1.0.10.
 */
  public long getStartMillis(){
    return this.start;
  }
  /** 
 * Returns the end date/time.
 * @return The end date/time (never <code>null</code>).
 */
  public Date getEnd(){
    return new Date(this.end);
  }
  /** 
 * Returns the end date/time in milliseconds.
 * @return The end.
 * @since 1.0.10.
 */
  public long getEndMillis(){
    return this.end;
  }
  /** 
 * Tests this time period instance for equality with an arbitrary object. The object is considered equal if it is an instance of                                                                                                                                                                    {@link TimePeriod}and it has the same start and end dates.
 * @param obj  the other object (<code>null</code> permitted).
 * @return A boolean.
 */
  public boolean equals(  Object obj){
    if (obj == this) {
      return true;
    }
    if (!(obj instanceof TimePeriod)) {
      return false;
    }
    TimePeriod that=(TimePeriod)obj;
    if (!this.getStart().equals(that.getStart())) {
      return false;
    }
    if (!this.getEnd().equals(that.getEnd())) {
      return false;
    }
    return true;
  }
  /** 
 * Returns an integer that indicates the relative ordering of two time periods.
 * @param obj  the object (<code>null</code> not permitted).
 * @return An integer.
 * @throws ClassCastException if <code>obj</code> is not an instance of{@link TimePeriod}.
 */
  public int compareTo(  Object obj){
    TimePeriod that=(TimePeriod)obj;
    long t0=getStart().getTime();
    long t1=getEnd().getTime();
    long m0=t0 + (t1 - t0) / 2L;
    long t2=that.getStart().getTime();
    long t3=that.getEnd().getTime();
    long m1=t2 + (t3 - t2) / 2L;
    if (m0 < m1) {
      return -1;
    }
 else {
      if (m0 > m1) {
        return 1;
      }
 else {
        if (t0 < t2) {
          return -1;
        }
 else {
          if (t0 > t2) {
            return 1;
          }
 else {
            if (t1 < t3) {
              return -1;
            }
 else {
              if (t1 > t3) {
                return 1;
              }
 else {
                return 0;
              }
            }
          }
        }
      }
    }
  }
  /** 
 * Returns a hash code for this object instance.  The approach described by Joshua Bloch in "Effective Java" has been used here - see: <p> <code>http://developer.java.sun.com/ developer/Books/effectivejava/Chapter3.pdf</code>
 * @return A hash code.
 */
  public int hashCode(){
    int result=17;
    result=37 * result + (int)this.start;
    result=37 * result + (int)this.end;
    return result;
  }
}
