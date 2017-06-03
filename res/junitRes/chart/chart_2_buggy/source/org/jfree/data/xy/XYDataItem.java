package org.jfree.data.xy;
import java.io.Serializable;
import org.jfree.chart.util.HashUtilities;
import org.jfree.chart.util.ObjectUtilities;
/** 
 * Represents one (x, y) data item for an                                                                                                                                                                    {@link XYSeries}.  Note that subclasses are REQUIRED to support cloning.
 */
public class XYDataItem implements Cloneable, Comparable, Serializable {
  /** 
 * For serialization. 
 */
  private static final long serialVersionUID=2751513470325494890L;
  /** 
 * The x-value (<code>null</code> not permitted). 
 */
  private Number x;
  /** 
 * The y-value. 
 */
  private Number y;
  /** 
 * A flag that indicates whether or not the item is "selected".
 * @since 1.2.0
 */
  private boolean selected;
  /** 
 * Constructs a new data item.
 * @param x  the x-value (<code>null</code> NOT permitted).
 * @param y  the y-value (<code>null</code> permitted).
 */
  public XYDataItem(  Number x,  Number y){
    if (x == null) {
      throw new IllegalArgumentException("Null 'x' argument.");
    }
    this.x=x;
    this.y=y;
    this.selected=false;
  }
  /** 
 * Constructs a new data item.
 * @param x  the x-value.
 * @param y  the y-value.
 */
  public XYDataItem(  double x,  double y){
    this(new Double(x),new Double(y));
  }
  /** 
 * Returns the x-value.
 * @return The x-value (never <code>null</code>).
 */
  public Number getX(){
    return this.x;
  }
  /** 
 * Returns the x-value as a double primitive.
 * @return The x-value.
 * @see #getX()
 * @see #getYValue()
 * @since 1.0.9
 */
  public double getXValue(){
    return this.x.doubleValue();
  }
  /** 
 * Returns the y-value.
 * @return The y-value (possibly <code>null</code>).
 */
  public Number getY(){
    return this.y;
  }
  /** 
 * Returns the y-value as a double primitive.
 * @return The y-value.
 * @see #getY()
 * @see #getXValue()
 * @since 1.0.9
 */
  public double getYValue(){
    double result=Double.NaN;
    if (this.y != null) {
      result=this.y.doubleValue();
    }
    return result;
  }
  /** 
 * Sets the y-value for this data item.  Note that there is no corresponding method to change the x-value.
 * @param y  the new y-value.
 */
  public void setY(  double y){
    setY(new Double(y));
  }
  /** 
 * Sets the y-value for this data item.  Note that there is no corresponding method to change the x-value.
 * @param y  the new y-value (<code>null</code> permitted).
 */
  public void setY(  Number y){
    this.y=y;
  }
  /** 
 * Returns <code>true</code> if the data item is selected, and <code>false</code> otherwise.
 * @return A boolean.
 * @see #setSelected(boolean)
 * @since 1.2.0
 */
  public boolean isSelected(){
    return this.selected;
  }
  /** 
 * Sets the selection state for this item.
 * @param selected  the new selection state.
 * @see #isSelected()
 * @since 1.2.0
 */
  public void setSelected(  boolean selected){
    this.selected=selected;
  }
  /** 
 * Returns an integer indicating the order of this object relative to another object. <P> For the order we consider only the x-value: negative == "less-than", zero == "equal", positive == "greater-than".
 * @param o1  the object being compared to.
 * @return An integer indicating the order of this data pair objectrelative to another object.
 */
  public int compareTo(  Object o1){
    int result;
    if (o1 instanceof XYDataItem) {
      XYDataItem dataItem=(XYDataItem)o1;
      double compare=this.x.doubleValue() - dataItem.getX().doubleValue();
      if (compare > 0.0) {
        result=1;
      }
 else {
        if (compare < 0.0) {
          result=-1;
        }
 else {
          result=0;
        }
      }
    }
 else {
      result=1;
    }
    return result;
  }
  /** 
 * Returns a clone of this object.
 * @return A clone.
 */
  public Object clone(){
    Object clone=null;
    try {
      clone=super.clone();
    }
 catch (    CloneNotSupportedException e) {
      e.printStackTrace();
    }
    return clone;
  }
  /** 
 * Tests if this object is equal to another.
 * @param obj  the object to test against for equality (<code>null</code>permitted).
 * @return A boolean.
 */
  public boolean equals(  Object obj){
    if (obj == this) {
      return true;
    }
    if (!(obj instanceof XYDataItem)) {
      return false;
    }
    XYDataItem that=(XYDataItem)obj;
    if (!this.x.equals(that.x)) {
      return false;
    }
    if (!ObjectUtilities.equal(this.y,that.y)) {
      return false;
    }
    if (this.selected != that.selected) {
      return false;
    }
    return true;
  }
  /** 
 * Returns a hash code.
 * @return A hash code.
 */
  public int hashCode(){
    int result;
    result=this.x.hashCode();
    result=29 * result + (this.y != null ? this.y.hashCode() : 0);
    result=HashUtilities.hashCode(result,this.selected);
    return result;
  }
  /** 
 * Returns a string representing this instance, primarily for debugging use.
 * @return A string.
 */
  public String toString(){
    return "[" + getXValue() + ", "+ getYValue()+ "]";
  }
}
