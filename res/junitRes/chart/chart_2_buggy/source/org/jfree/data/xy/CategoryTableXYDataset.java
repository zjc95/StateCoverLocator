package org.jfree.data.xy;
import org.jfree.chart.event.DatasetChangeInfo;
import org.jfree.chart.util.PublicCloneable;
import org.jfree.data.DefaultKeyedValues2D;
import org.jfree.data.DomainInfo;
import org.jfree.data.Range;
import org.jfree.data.event.DatasetChangeEvent;
import org.jfree.data.general.DatasetUtilities;
/** 
 * An implementation variant of the                                                                                                                                                                    {@link TableXYDataset} where every seriesshares the same x-values (required for generating stacked area charts). This implementation uses a  {@link DefaultKeyedValues2D} Object as backendimplementation and is hence more "category oriented" than the  {@link DefaultTableXYDataset} implementation.<p> This implementation provides no means to remove data items yet. This is due to the lack of such facility in the DefaultKeyedValues2D class. <p> This class also implements the  {@link IntervalXYDataset} interface, but thisimplementation is provisional.
 */
public class CategoryTableXYDataset extends AbstractIntervalXYDataset implements TableXYDataset, IntervalXYDataset, DomainInfo, PublicCloneable {
  /** 
 * The backing data structure.
 */
  private DefaultKeyedValues2D values;
  /** 
 * A delegate for controlling the interval width. 
 */
  private IntervalXYDelegate intervalDelegate;
  /** 
 * Creates a new empty CategoryTableXYDataset.
 */
  public CategoryTableXYDataset(){
    this.values=new DefaultKeyedValues2D(true);
    this.intervalDelegate=new IntervalXYDelegate(this);
    addChangeListener(this.intervalDelegate);
  }
  /** 
 * Adds a data item to this dataset and sends a                                                                                                                                                                    {@link DatasetChangeEvent}to all registered listeners.
 * @param x  the x value.
 * @param y  the y value.
 * @param seriesName  the name of the series to add the data item.
 */
  public void add(  double x,  double y,  String seriesName){
    add(new Double(x),new Double(y),seriesName,true);
  }
  /** 
 * Adds a data item to this dataset and, if requested, sends a                                                                                                                                                                   {@link DatasetChangeEvent} to all registered listeners.
 * @param x  the x value.
 * @param y  the y value.
 * @param seriesName  the name of the series to add the data item.
 * @param notify  notify listeners?
 */
  public void add(  Number x,  Number y,  String seriesName,  boolean notify){
    this.values.addValue(y,(Comparable)x,seriesName);
    if (notify) {
      fireDatasetChanged(new DatasetChangeInfo());
    }
  }
  /** 
 * Removes a value from the dataset.
 * @param x  the x-value.
 * @param seriesName  the series name.
 */
  public void remove(  double x,  String seriesName){
    remove(new Double(x),seriesName,true);
  }
  /** 
 * Removes an item from the dataset.
 * @param x  the x-value.
 * @param seriesName  the series name.
 * @param notify  notify listeners?
 */
  public void remove(  Number x,  String seriesName,  boolean notify){
    this.values.removeValue((Comparable)x,seriesName);
    if (notify) {
      fireDatasetChanged(new DatasetChangeInfo());
    }
  }
  /** 
 * Returns the number of series in the collection.
 * @return The series count.
 */
  public int getSeriesCount(){
    return this.values.getColumnCount();
  }
  /** 
 * Returns the key for a series.
 * @param series  the series index (zero-based).
 * @return The key for a series.
 */
  public Comparable getSeriesKey(  int series){
    return this.values.getColumnKey(series);
  }
  /** 
 * Returns the number of x values in the dataset.
 * @return The item count.
 */
  public int getItemCount(){
    return this.values.getRowCount();
  }
  /** 
 * Returns the number of items in the specified series. Returns the same as                                                                                                                                                                    {@link CategoryTableXYDataset#getItemCount()}.
 * @param series  the series index (zero-based).
 * @return The item count.
 */
  public int getItemCount(  int series){
    return getItemCount();
  }
  /** 
 * Returns the x-value for the specified series and item.
 * @param series  the series index (zero-based).
 * @param item  the item index (zero-based).
 * @return The value.
 */
  public Number getX(  int series,  int item){
    return (Number)this.values.getRowKey(item);
  }
  /** 
 * Returns the starting X value for the specified series and item.
 * @param series  the series index (zero-based).
 * @param item  the item index (zero-based).
 * @return The starting X value.
 */
  public Number getStartX(  int series,  int item){
    return this.intervalDelegate.getStartX(series,item);
  }
  /** 
 * Returns the ending X value for the specified series and item.
 * @param series  the series index (zero-based).
 * @param item  the item index (zero-based).
 * @return The ending X value.
 */
  public Number getEndX(  int series,  int item){
    return this.intervalDelegate.getEndX(series,item);
  }
  /** 
 * Returns the y-value for the specified series and item.
 * @param series  the series index (zero-based).
 * @param item  the item index (zero-based).
 * @return The y value (possibly <code>null</code>).
 */
  public Number getY(  int series,  int item){
    return this.values.getValue(item,series);
  }
  /** 
 * Returns the starting Y value for the specified series and item.
 * @param series  the series index (zero-based).
 * @param item  the item index (zero-based).
 * @return The starting Y value.
 */
  public Number getStartY(  int series,  int item){
    return getY(series,item);
  }
  /** 
 * Returns the ending Y value for the specified series and item.
 * @param series  the series index (zero-based).
 * @param item  the item index (zero-based).
 * @return The ending Y value.
 */
  public Number getEndY(  int series,  int item){
    return getY(series,item);
  }
  /** 
 * Returns the minimum x-value in the dataset.
 * @param includeInterval  a flag that determines whether or not thex-interval is taken into account.
 * @return The minimum value.
 */
  public double getDomainLowerBound(  boolean includeInterval){
    return this.intervalDelegate.getDomainLowerBound(includeInterval);
  }
  /** 
 * Returns the maximum x-value in the dataset.
 * @param includeInterval  a flag that determines whether or not thex-interval is taken into account.
 * @return The maximum value.
 */
  public double getDomainUpperBound(  boolean includeInterval){
    return this.intervalDelegate.getDomainUpperBound(includeInterval);
  }
  /** 
 * Returns the range of the values in this dataset's domain.
 * @param includeInterval  a flag that determines whether or not thex-interval is taken into account.
 * @return The range.
 */
  public Range getDomainBounds(  boolean includeInterval){
    if (includeInterval) {
      return this.intervalDelegate.getDomainBounds(includeInterval);
    }
 else {
      return DatasetUtilities.iterateDomainBounds(this,includeInterval);
    }
  }
  /** 
 * Returns the interval position factor.
 * @return The interval position factor.
 */
  public double getIntervalPositionFactor(){
    return this.intervalDelegate.getIntervalPositionFactor();
  }
  /** 
 * Sets the interval position factor. Must be between 0.0 and 1.0 inclusive. If the factor is 0.5, the gap is in the middle of the x values. If it is lesser than 0.5, the gap is farther to the left and if greater than 0.5 it gets farther to the right.
 * @param d  the new interval position factor.
 */
  public void setIntervalPositionFactor(  double d){
    this.intervalDelegate.setIntervalPositionFactor(d);
    fireDatasetChanged(new DatasetChangeInfo());
  }
  /** 
 * Returns the full interval width.
 * @return The interval width to use.
 */
  public double getIntervalWidth(){
    return this.intervalDelegate.getIntervalWidth();
  }
  /** 
 * Sets the interval width to a fixed value, and sends a                                                                                                                                                                   {@link DatasetChangeEvent} to all registered listeners.
 * @param d  the new interval width (must be > 0).
 */
  public void setIntervalWidth(  double d){
    this.intervalDelegate.setFixedIntervalWidth(d);
    fireDatasetChanged(new DatasetChangeInfo());
  }
  /** 
 * Returns whether the interval width is automatically calculated or not.
 * @return whether the width is automatically calculated or not.
 */
  public boolean isAutoWidth(){
    return this.intervalDelegate.isAutoWidth();
  }
  /** 
 * Sets the flag that indicates whether the interval width is automatically calculated or not.
 * @param b  the flag.
 */
  public void setAutoWidth(  boolean b){
    this.intervalDelegate.setAutoWidth(b);
    fireDatasetChanged(new DatasetChangeInfo());
  }
  /** 
 * Tests this dataset for equality with an arbitrary object.
 * @param obj  the object (<code>null</code> permitted).
 * @return A boolean.
 */
  public boolean equals(  Object obj){
    if (!(obj instanceof CategoryTableXYDataset)) {
      return false;
    }
    CategoryTableXYDataset that=(CategoryTableXYDataset)obj;
    if (!this.intervalDelegate.equals(that.intervalDelegate)) {
      return false;
    }
    if (!this.values.equals(that.values)) {
      return false;
    }
    return true;
  }
  /** 
 * Returns an independent copy of this dataset.
 * @return A clone.
 * @throws CloneNotSupportedException if there is some reason that cloningcannot be performed.
 */
  public Object clone() throws CloneNotSupportedException {
    CategoryTableXYDataset clone=(CategoryTableXYDataset)super.clone();
    clone.values=(DefaultKeyedValues2D)this.values.clone();
    clone.intervalDelegate=new IntervalXYDelegate(clone);
    clone.intervalDelegate.setFixedIntervalWidth(getIntervalWidth());
    clone.intervalDelegate.setAutoWidth(isAutoWidth());
    clone.intervalDelegate.setIntervalPositionFactor(getIntervalPositionFactor());
    return clone;
  }
}
