package org.jfree.data.xy;
import java.io.Serializable;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.jfree.chart.util.ObjectUtilities;
import org.jfree.data.general.Series;
import org.jfree.data.event.SeriesChangeEvent;
import org.jfree.data.general.SeriesException;
/** 
 * Represents a sequence of zero or more data items in the form (x, y).  By default, items in the series will be sorted into ascending order by x-value, and duplicate x-values are permitted.  Both the sorting and duplicate defaults can be changed in the constructor.  Y-values can be <code>null</code> to represent missing values.
 */
public class XYSeries extends Series implements Cloneable, Serializable {
  /** 
 * For serialization. 
 */
  static final long serialVersionUID=-5908509288197150436L;
  /** 
 * Storage for the data items in the series. 
 */
  protected List data;
  /** 
 * The maximum number of items for the series. 
 */
  private int maximumItemCount=Integer.MAX_VALUE;
  /** 
 * A flag that controls whether the items are automatically sorted (by x-value ascending).
 */
  private boolean autoSort;
  /** 
 * A flag that controls whether or not duplicate x-values are allowed. 
 */
  private boolean allowDuplicateXValues;
  /** 
 * The lowest x-value in the series, excluding Double.NaN values. 
 */
  private double minX;
  /** 
 * The highest x-value in the series, excluding Double.NaN values. 
 */
  private double maxX;
  /** 
 * The lowest y-value in the series, excluding Double.NaN values. 
 */
  private double minY;
  /** 
 * The highest y-value in the series, excluding Double.NaN values. 
 */
  private double maxY;
  /** 
 * Creates a new empty series.  By default, items added to the series will be sorted into ascending order by x-value, and duplicate x-values will be allowed (these defaults can be modified with another constructor.
 * @param key  the series key (<code>null</code> not permitted).
 */
  public XYSeries(  Comparable key){
    this(key,true,true);
  }
  /** 
 * Constructs a new empty series, with the auto-sort flag set as requested, and duplicate values allowed.
 * @param key  the series key (<code>null</code> not permitted).
 * @param autoSort  a flag that controls whether or not the items in theseries are sorted.
 */
  public XYSeries(  Comparable key,  boolean autoSort){
    this(key,autoSort,true);
  }
  /** 
 * Constructs a new xy-series that contains no data.  You can specify whether or not duplicate x-values are allowed for the series.
 * @param key  the series key (<code>null</code> not permitted).
 * @param autoSort  a flag that controls whether or not the items in theseries are sorted.
 * @param allowDuplicateXValues  a flag that controls whether duplicatex-values are allowed.
 */
  public XYSeries(  Comparable key,  boolean autoSort,  boolean allowDuplicateXValues){
    super(key);
    this.data=new java.util.ArrayList();
    this.autoSort=autoSort;
    this.allowDuplicateXValues=allowDuplicateXValues;
    this.minX=Double.NaN;
    this.maxX=Double.NaN;
    this.minY=Double.NaN;
    this.maxY=Double.NaN;
  }
  /** 
 * Returns the smallest x-value in the series, ignoring any Double.NaN values.  This method returns Double.NaN if there is no smallest x-value (for example, when the series is empty).
 * @return The smallest x-value.
 * @see #getMaxX()
 * @since 1.0.13
 */
  public double getMinX(){
    return this.minX;
  }
  /** 
 * Returns the largest x-value in the series, ignoring any Double.NaN values.  This method returns Double.NaN if there is no largest x-value (for example, when the series is empty).
 * @return The largest x-value.
 * @see #getMinX()
 * @since 1.0.13
 */
  public double getMaxX(){
    return this.maxX;
  }
  /** 
 * Returns the smallest y-value in the series, ignoring any null and Double.NaN values.  This method returns Double.NaN if there is no smallest y-value (for example, when the series is empty).
 * @return The smallest y-value.
 * @see #getMaxY()
 * @since 1.0.13
 */
  public double getMinY(){
    return this.minY;
  }
  /** 
 * Returns the largest y-value in the series, ignoring any Double.NaN values.  This method returns Double.NaN if there is no largest y-value (for example, when the series is empty).
 * @return The largest y-value.
 * @see #getMinY()
 * @since 1.0.13
 */
  public double getMaxY(){
    return this.maxY;
  }
  /** 
 * Updates the cached values for the minimum and maximum data values.
 * @param item  the item added (<code>null</code> not permitted).
 * @since 1.0.13
 */
  private void updateBoundsForAddedItem(  XYDataItem item){
    double x=item.getXValue();
    this.minX=minIgnoreNaN(this.minX,x);
    this.maxX=maxIgnoreNaN(this.maxX,x);
    if (item.getY() != null) {
      double y=item.getYValue();
      this.minY=minIgnoreNaN(this.minY,y);
      this.maxY=maxIgnoreNaN(this.maxY,y);
    }
  }
  /** 
 * Updates the cached values for the minimum and maximum data values on the basis that the specified item has just been removed.
 * @param item  the item added (<code>null</code> not permitted).
 * @since 1.0.13
 */
  private void updateBoundsForRemovedItem(  XYDataItem item){
    boolean itemContributesToXBounds=false;
    boolean itemContributesToYBounds=false;
    double x=item.getXValue();
    if (!Double.isNaN(x)) {
      if (x <= this.minX || x >= this.maxX) {
        itemContributesToXBounds=true;
      }
    }
    if (item.getY() != null) {
      double y=item.getYValue();
      if (!Double.isNaN(y)) {
        if (y <= this.minY || y >= this.maxY) {
          itemContributesToYBounds=true;
        }
      }
    }
    if (itemContributesToYBounds) {
      findBoundsByIteration();
    }
 else {
      if (itemContributesToXBounds) {
        if (getAutoSort()) {
          this.minX=getX(0).doubleValue();
          this.maxX=getX(getItemCount() - 1).doubleValue();
        }
 else {
          findBoundsByIteration();
        }
      }
    }
  }
  /** 
 * Finds the bounds of the x and y values for the series, by iterating through all the data items.
 * @since 1.0.13
 */
  private void findBoundsByIteration(){
    this.minX=Double.NaN;
    this.maxX=Double.NaN;
    this.minY=Double.NaN;
    this.maxY=Double.NaN;
    Iterator iterator=this.data.iterator();
    while (iterator.hasNext()) {
      XYDataItem item=(XYDataItem)iterator.next();
      updateBoundsForAddedItem(item);
    }
  }
  /** 
 * Returns the flag that controls whether the items in the series are automatically sorted.  There is no setter for this flag, it must be defined in the series constructor.
 * @return A boolean.
 */
  public boolean getAutoSort(){
    return this.autoSort;
  }
  /** 
 * Returns a flag that controls whether duplicate x-values are allowed. This flag can only be set in the constructor.
 * @return A boolean.
 */
  public boolean getAllowDuplicateXValues(){
    return this.allowDuplicateXValues;
  }
  /** 
 * Returns the number of items in the series.
 * @return The item count.
 * @see #getItems()
 */
  public int getItemCount(){
    return this.data.size();
  }
  /** 
 * Returns the list of data items for the series (the list contains                                                                                                                                                                  {@link XYDataItem} objects and is unmodifiable).
 * @return The list of data items.
 */
  public List getItems(){
    return Collections.unmodifiableList(this.data);
  }
  /** 
 * Returns the maximum number of items that will be retained in the series. The default value is <code>Integer.MAX_VALUE</code>.
 * @return The maximum item count.
 * @see #setMaximumItemCount(int)
 */
  public int getMaximumItemCount(){
    return this.maximumItemCount;
  }
  /** 
 * Sets the maximum number of items that will be retained in the series. If you add a new item to the series such that the number of items will exceed the maximum item count, then the first element in the series is automatically removed, ensuring that the maximum item count is not exceeded. <p> Typically this value is set before the series is populated with data, but if it is applied later, it may cause some items to be removed from the series (in which case a                                                                                                                                                                   {@link SeriesChangeEvent} will be sent toall registered listeners).
 * @param maximum  the maximum number of items for the series.
 */
  public void setMaximumItemCount(  int maximum){
    this.maximumItemCount=maximum;
    int remove=this.data.size() - maximum;
    if (remove > 0) {
      this.data.subList(0,remove).clear();
      findBoundsByIteration();
      fireSeriesChanged();
    }
  }
  /** 
 * Adds a data item to the series and sends a                                                                                                                                                                   {@link SeriesChangeEvent} toall registered listeners.
 * @param item  the (x, y) item (<code>null</code> not permitted).
 */
  public void add(  XYDataItem item){
    add(item,true);
  }
  /** 
 * Adds a data item to the series and sends a                                                                                                                                                                   {@link SeriesChangeEvent} toall registered listeners.
 * @param x  the x value.
 * @param y  the y value.
 */
  public void add(  double x,  double y){
    add(new Double(x),new Double(y),true);
  }
  /** 
 * Adds a data item to the series and, if requested, sends a                                                                                                                                                                  {@link SeriesChangeEvent} to all registered listeners.
 * @param x  the x value.
 * @param y  the y value.
 * @param notify  a flag that controls whether or not a{@link SeriesChangeEvent} is sent to all registeredlisteners.
 */
  public void add(  double x,  double y,  boolean notify){
    add(new Double(x),new Double(y),notify);
  }
  /** 
 * Adds a data item to the series and sends a                                                                                                                                                                   {@link SeriesChangeEvent} toall registered listeners.  The unusual pairing of parameter types is to make it easier to add <code>null</code> y-values.
 * @param x  the x value.
 * @param y  the y value (<code>null</code> permitted).
 */
  public void add(  double x,  Number y){
    add(new Double(x),y);
  }
  /** 
 * Adds a data item to the series and, if requested, sends a                                                                                                                                                                  {@link SeriesChangeEvent} to all registered listeners.  The unusualpairing of parameter types is to make it easier to add null y-values.
 * @param x  the x value.
 * @param y  the y value (<code>null</code> permitted).
 * @param notify  a flag that controls whether or not a{@link SeriesChangeEvent} is sent to all registeredlisteners.
 */
  public void add(  double x,  Number y,  boolean notify){
    add(new Double(x),y,notify);
  }
  /** 
 * Adds a new data item to the series (in the correct position if the <code>autoSort</code> flag is set for the series) and sends a                                                                                                                                                                  {@link SeriesChangeEvent} to all registered listeners.<P> Throws an exception if the x-value is a duplicate AND the allowDuplicateXValues flag is false.
 * @param x  the x-value (<code>null</code> not permitted).
 * @param y  the y-value (<code>null</code> permitted).
 * @throws SeriesException if the x-value is a duplicate and the<code>allowDuplicateXValues</code> flag is not set for this series.
 */
  public void add(  Number x,  Number y){
    add(x,y,true);
  }
  /** 
 * Adds new data to the series and, if requested, sends a                                                                                                                                                                  {@link SeriesChangeEvent} to all registered listeners.<P> Throws an exception if the x-value is a duplicate AND the allowDuplicateXValues flag is false.
 * @param x  the x-value (<code>null</code> not permitted).
 * @param y  the y-value (<code>null</code> permitted).
 * @param notify  a flag the controls whether or not a{@link SeriesChangeEvent} is sent to all registeredlisteners.
 */
  public void add(  Number x,  Number y,  boolean notify){
    XYDataItem item=new XYDataItem(x,y);
    add(item,notify);
  }
  /** 
 * Adds a data item to the series and, if requested, sends a                                                                                                                                                                  {@link SeriesChangeEvent} to all registered listeners.
 * @param item  the (x, y) item (<code>null</code> not permitted).
 * @param notify  a flag that controls whether or not a{@link SeriesChangeEvent} is sent to all registeredlisteners.
 */
  public void add(  XYDataItem item,  boolean notify){
    if (item == null) {
      throw new IllegalArgumentException("Null 'item' argument.");
    }
    item=(XYDataItem)item.clone();
    if (this.autoSort) {
      int index=Collections.binarySearch(this.data,item);
      if (index < 0) {
        this.data.add(-index - 1,item);
      }
 else {
        if (this.allowDuplicateXValues) {
          int size=this.data.size();
          while (index < size && item.compareTo(this.data.get(index)) == 0) {
            index++;
          }
          if (index < this.data.size()) {
            this.data.add(index,item);
          }
 else {
            this.data.add(item);
          }
        }
 else {
          throw new SeriesException("X-value already exists.");
        }
      }
    }
 else {
      if (!this.allowDuplicateXValues) {
        int index=indexOf(item.getX());
        if (index >= 0) {
          throw new SeriesException("X-value already exists.");
        }
      }
      this.data.add(item);
    }
    updateBoundsForAddedItem(item);
    if (getItemCount() > this.maximumItemCount) {
      XYDataItem removed=(XYDataItem)this.data.remove(0);
      updateBoundsForRemovedItem(removed);
    }
    if (notify) {
      fireSeriesChanged();
    }
  }
  /** 
 * Deletes a range of items from the series and sends a                                                                                                                                                                  {@link SeriesChangeEvent} to all registered listeners.
 * @param start  the start index (zero-based).
 * @param end  the end index (zero-based).
 */
  public void delete(  int start,  int end){
    this.data.subList(start,end + 1).clear();
    findBoundsByIteration();
    fireSeriesChanged();
  }
  /** 
 * Removes the item at the specified index and sends a                                                                                                                                                                  {@link SeriesChangeEvent} to all registered listeners.
 * @param index  the index.
 * @return The item removed.
 */
  public XYDataItem remove(  int index){
    XYDataItem removed=(XYDataItem)this.data.remove(index);
    updateBoundsForRemovedItem(removed);
    fireSeriesChanged();
    return removed;
  }
  /** 
 * Removes an item with the specified x-value and sends a                                                                                                                                                                  {@link SeriesChangeEvent} to all registered listeners.  Note that whena series permits multiple items with the same x-value, this method could remove any one of the items with that x-value.
 * @param x  the x-value.
 * @return The item removed.
 */
  public XYDataItem remove(  Number x){
    return remove(indexOf(x));
  }
  /** 
 * Removes all data items from the series and sends a                                                                                                                                                                  {@link SeriesChangeEvent} to all registered listeners.
 */
  public void clear(){
    if (this.data.size() > 0) {
      this.data.clear();
      this.minX=Double.NaN;
      this.maxX=Double.NaN;
      this.minY=Double.NaN;
      this.maxY=Double.NaN;
      fireSeriesChanged();
    }
  }
  /** 
 * Return the data item with the specified index.
 * @param index  the index.
 * @return The data item with the specified index.
 */
  public XYDataItem getDataItem(  int index){
    XYDataItem item=(XYDataItem)this.data.get(index);
    return (XYDataItem)item.clone();
  }
  /** 
 * Return the data item with the specified index.
 * @param index  the index.
 * @return The data item with the specified index.
 * @since 1.0.14
 */
  XYDataItem getRawDataItem(  int index){
    return (XYDataItem)this.data.get(index);
  }
  /** 
 * Returns the x-value at the specified index.
 * @param index  the index (zero-based).
 * @return The x-value (never <code>null</code>).
 */
  public Number getX(  int index){
    return getRawDataItem(index).getX();
  }
  /** 
 * Returns the y-value at the specified index.
 * @param index  the index (zero-based).
 * @return The y-value (possibly <code>null</code>).
 */
  public Number getY(  int index){
    return getRawDataItem(index).getY();
  }
  /** 
 * Updates the value of an item in the series and sends a                                                                                                                                                                  {@link SeriesChangeEvent} to all registered listeners.
 * @param index  the item (zero based index).
 * @param y  the new value (<code>null</code> permitted).
 * @since 1.0.1
 */
  public void updateByIndex(  int index,  Number y){
    XYDataItem item=getRawDataItem(index);
    boolean iterate=false;
    double oldY=item.getYValue();
    if (!Double.isNaN(oldY)) {
      iterate=oldY <= this.minY || oldY >= this.maxY;
    }
    item.setY(y);
    if (iterate) {
      findBoundsByIteration();
    }
 else {
      if (y != null) {
        double yy=y.doubleValue();
        this.minY=minIgnoreNaN(this.minY,yy);
        this.maxY=maxIgnoreNaN(this.maxY,yy);
      }
    }
    fireSeriesChanged();
  }
  /** 
 * A function to find the minimum of two values, but ignoring any Double.NaN values.
 * @param a  the first value.
 * @param b  the second value.
 * @return The minimum of the two values.
 */
  private double minIgnoreNaN(  double a,  double b){
    if (Double.isNaN(a)) {
      return b;
    }
 else {
      if (Double.isNaN(b)) {
        return a;
      }
 else {
        return Math.min(a,b);
      }
    }
  }
  /** 
 * A function to find the maximum of two values, but ignoring any Double.NaN values.
 * @param a  the first value.
 * @param b  the second value.
 * @return The maximum of the two values.
 */
  private double maxIgnoreNaN(  double a,  double b){
    if (Double.isNaN(a)) {
      return b;
    }
 else {
      if (Double.isNaN(b)) {
        return a;
      }
 else {
        return Math.max(a,b);
      }
    }
  }
  /** 
 * Updates an item in the series.
 * @param x  the x-value (<code>null</code> not permitted).
 * @param y  the y-value (<code>null</code> permitted).
 * @throws SeriesException if there is no existing item with the specifiedx-value.
 */
  public void update(  Number x,  Number y){
    int index=indexOf(x);
    if (index < 0) {
      throw new SeriesException("No observation for x = " + x);
    }
 else {
      updateByIndex(index,y);
    }
  }
  /** 
 * Adds or updates an item in the series and sends a                                                                                                                                                                  {@link SeriesChangeEvent} to all registered listeners.
 * @param x  the x-value.
 * @param y  the y-value.
 * @return The item that was overwritten, if any.
 * @since 1.0.10
 */
  public XYDataItem addOrUpdate(  double x,  double y){
    return addOrUpdate(new Double(x),new Double(y));
  }
  /** 
 * Adds or updates an item in the series and sends a                                                                                                                                                                  {@link SeriesChangeEvent} to all registered listeners.
 * @param x  the x-value (<code>null</code> not permitted).
 * @param y  the y-value (<code>null</code> permitted).
 * @return A copy of the overwritten data item, or <code>null</code> if noitem was overwritten.
 */
  public XYDataItem addOrUpdate(  Number x,  Number y){
    return addOrUpdate(new XYDataItem(x,y));
  }
  /** 
 * Adds or updates an item in the series and sends a                                                                                                                                                                  {@link SeriesChangeEvent} to all registered listeners.
 * @param x  the x-value (<code>null</code> not permitted).
 * @param y  the y-value (<code>null</code> permitted).
 * @return A copy of the overwritten data item, or <code>null</code> if noitem was overwritten.
 * @since 1.0.14
 */
  public XYDataItem addOrUpdate(  XYDataItem item){
    if (item == null) {
      throw new IllegalArgumentException("Null 'item' argument.");
    }
    if (this.allowDuplicateXValues) {
      add(item);
      return null;
    }
    XYDataItem overwritten=null;
    int index=indexOf(item.getX());
    if (index >= 0) {
      XYDataItem existing=(XYDataItem)this.data.get(index);
      overwritten=(XYDataItem)existing.clone();
      boolean iterate=false;
      double oldY=existing.getYValue();
      if (!Double.isNaN(oldY)) {
        iterate=oldY <= this.minY || oldY >= this.maxY;
      }
      existing.setY(item.getY());
      if (iterate) {
        findBoundsByIteration();
      }
 else {
        if (item.getY() != null) {
          double yy=item.getY().doubleValue();
          this.minY=minIgnoreNaN(this.minY,yy);
          this.maxY=minIgnoreNaN(this.maxY,yy);
        }
      }
    }
 else {
      item=(XYDataItem)item.clone();
      if (this.autoSort) {
        this.data.add(-index - 1,item);
      }
 else {
        this.data.add(item);
      }
      updateBoundsForAddedItem(item);
      if (getItemCount() > this.maximumItemCount) {
        XYDataItem removed=(XYDataItem)this.data.remove(0);
        updateBoundsForRemovedItem(removed);
      }
    }
    fireSeriesChanged();
    return overwritten;
  }
  /** 
 * Returns the index of the item with the specified x-value, or a negative index if the series does not contain an item with that x-value.  Be aware that for an unsorted series, the index is found by iterating through all items in the series.
 * @param x  the x-value (<code>null</code> not permitted).
 * @return The index.
 */
  public int indexOf(  Number x){
    if (this.autoSort) {
      return Collections.binarySearch(this.data,new XYDataItem(x,null));
    }
 else {
      for (int i=0; i < this.data.size(); i++) {
        XYDataItem item=(XYDataItem)this.data.get(i);
        if (item.getX().equals(x)) {
          return i;
        }
      }
      return -1;
    }
  }
  /** 
 * Returns a new array containing the x and y values from this series.
 * @return A new array containing the x and y values from this series.
 * @since 1.0.4
 */
  public double[][] toArray(){
    int itemCount=getItemCount();
    double[][] result=new double[2][itemCount];
    for (int i=0; i < itemCount; i++) {
      result[0][i]=this.getX(i).doubleValue();
      Number y=getY(i);
      if (y != null) {
        result[1][i]=y.doubleValue();
      }
 else {
        result[1][i]=Double.NaN;
      }
    }
    return result;
  }
  /** 
 * Returns a clone of the series.
 * @return A clone of the series.
 * @throws CloneNotSupportedException if there is a cloning problem.
 */
  public Object clone() throws CloneNotSupportedException {
    XYSeries clone=(XYSeries)super.clone();
    clone.data=(List)ObjectUtilities.deepClone(this.data);
    return clone;
  }
  /** 
 * Creates a new series by copying a subset of the data in this time series.
 * @param start  the index of the first item to copy.
 * @param end  the index of the last item to copy.
 * @return A series containing a copy of this series from start until end.
 * @throws CloneNotSupportedException if there is a cloning problem.
 */
  public XYSeries createCopy(  int start,  int end) throws CloneNotSupportedException {
    XYSeries copy=(XYSeries)super.clone();
    copy.data=new java.util.ArrayList();
    if (this.data.size() > 0) {
      for (int index=start; index <= end; index++) {
        XYDataItem item=(XYDataItem)this.data.get(index);
        XYDataItem clone=(XYDataItem)item.clone();
        try {
          copy.add(clone);
        }
 catch (        SeriesException e) {
          System.err.println("Unable to add cloned data item.");
        }
      }
    }
    return copy;
  }
  /** 
 * Tests this series for equality with an arbitrary object.
 * @param obj  the object to test against for equality(<code>null</code> permitted).
 * @return A boolean.
 */
  public boolean equals(  Object obj){
    if (obj == this) {
      return true;
    }
    if (!(obj instanceof XYSeries)) {
      return false;
    }
    if (!super.equals(obj)) {
      return false;
    }
    XYSeries that=(XYSeries)obj;
    if (this.maximumItemCount != that.maximumItemCount) {
      return false;
    }
    if (this.autoSort != that.autoSort) {
      return false;
    }
    if (this.allowDuplicateXValues != that.allowDuplicateXValues) {
      return false;
    }
    if (!ObjectUtilities.equal(this.data,that.data)) {
      return false;
    }
    return true;
  }
  /** 
 * Returns a hash code.
 * @return A hash code.
 */
  public int hashCode(){
    int result=super.hashCode();
    int count=getItemCount();
    if (count > 0) {
      XYDataItem item=getRawDataItem(0);
      result=29 * result + item.hashCode();
    }
    if (count > 1) {
      XYDataItem item=getRawDataItem(count - 1);
      result=29 * result + item.hashCode();
    }
    if (count > 2) {
      XYDataItem item=getRawDataItem(count / 2);
      result=29 * result + item.hashCode();
    }
    result=29 * result + this.maximumItemCount;
    result=29 * result + (this.autoSort ? 1 : 0);
    result=29 * result + (this.allowDuplicateXValues ? 1 : 0);
    return result;
  }
}
