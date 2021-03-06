package org.jfree.data.xy;
import org.jfree.data.ComparableObjectItem;
import org.jfree.data.ComparableObjectSeries;
/** 
 * A list of (x, y, y-low, y-high) data items.
 * @since 1.0.3
 * @see YIntervalSeriesCollection
 */
public class YIntervalSeries extends ComparableObjectSeries {
  /** 
 * Creates a new empty series.  By default, items added to the series will be sorted into ascending order by x-value, and duplicate x-values will be allowed (these defaults can be modified with another constructor.
 * @param key  the series key (<code>null</code> not permitted).
 */
  public YIntervalSeries(  Comparable key){
    this(key,true,true);
  }
  /** 
 * Constructs a new xy-series that contains no data.  You can specify whether or not duplicate x-values are allowed for the series.
 * @param key  the series key (<code>null</code> not permitted).
 * @param autoSort  a flag that controls whether or not the items in theseries are sorted.
 * @param allowDuplicateXValues  a flag that controls whether duplicatex-values are allowed.
 */
  public YIntervalSeries(  Comparable key,  boolean autoSort,  boolean allowDuplicateXValues){
    super(key,autoSort,allowDuplicateXValues);
  }
  /** 
 * Adds a data item to the series.
 * @param x  the x-value.
 * @param y  the y-value.
 * @param yLow  the lower bound of the y-interval.
 * @param yHigh  the upper bound of the y-interval.
 */
  public void add(  double x,  double y,  double yLow,  double yHigh){
    super.add(new YIntervalDataItem(x,y,yLow,yHigh),true);
  }
  /** 
 * Returns the x-value for the specified item.
 * @param index  the item index.
 * @return The x-value (never <code>null</code>).
 */
  public Number getX(  int index){
    YIntervalDataItem item=(YIntervalDataItem)getDataItem(index);
    return item.getX();
  }
  /** 
 * Returns the y-value for the specified item.
 * @param index  the item index.
 * @return The y-value.
 */
  public double getYValue(  int index){
    YIntervalDataItem item=(YIntervalDataItem)getDataItem(index);
    return item.getYValue();
  }
  /** 
 * Returns the lower bound of the Y-interval for the specified item in the series.
 * @param index  the item index.
 * @return The lower bound of the Y-interval.
 * @since 1.0.5
 */
  public double getYLowValue(  int index){
    YIntervalDataItem item=(YIntervalDataItem)getDataItem(index);
    return item.getYLowValue();
  }
  /** 
 * Returns the upper bound of the y-interval for the specified item in the series.
 * @param index  the item index.
 * @return The upper bound of the y-interval.
 * @since 1.0.5
 */
  public double getYHighValue(  int index){
    YIntervalDataItem item=(YIntervalDataItem)getDataItem(index);
    return item.getYHighValue();
  }
  /** 
 * Returns the data item at the specified index.
 * @param index  the item index.
 * @return The data item.
 */
  public ComparableObjectItem getDataItem(  int index){
    return super.getDataItem(index);
  }
}
