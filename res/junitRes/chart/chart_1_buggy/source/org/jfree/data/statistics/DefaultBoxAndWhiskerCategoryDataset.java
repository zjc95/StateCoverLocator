package org.jfree.data.statistics;
import java.util.List;
import org.jfree.chart.event.DatasetChangeInfo;
import org.jfree.chart.util.ObjectUtilities;
import org.jfree.chart.util.PublicCloneable;
import org.jfree.data.KeyedObjects2D;
import org.jfree.data.Range;
import org.jfree.data.RangeInfo;
import org.jfree.data.general.AbstractDataset;
/** 
 * A convenience class that provides a default implementation of the                              {@link BoxAndWhiskerCategoryDataset} interface.
 */
public class DefaultBoxAndWhiskerCategoryDataset extends AbstractDataset implements BoxAndWhiskerCategoryDataset, RangeInfo, PublicCloneable {
  /** 
 * Storage for the data. 
 */
  protected KeyedObjects2D data;
  /** 
 * The minimum range value. 
 */
  private double minimumRangeValue;
  /** 
 * The row index for the cell that the minimum range value comes from. 
 */
  private int minimumRangeValueRow;
  /** 
 * The column index for the cell that the minimum range value comes from.
 */
  private int minimumRangeValueColumn;
  /** 
 * The maximum range value. 
 */
  private double maximumRangeValue;
  /** 
 * The row index for the cell that the maximum range value comes from. 
 */
  private int maximumRangeValueRow;
  /** 
 * The column index for the cell that the maximum range value comes from.
 */
  private int maximumRangeValueColumn;
  /** 
 * Creates a new dataset.
 */
  public DefaultBoxAndWhiskerCategoryDataset(){
    this.data=new KeyedObjects2D();
    this.minimumRangeValue=Double.NaN;
    this.minimumRangeValueRow=-1;
    this.minimumRangeValueColumn=-1;
    this.maximumRangeValue=Double.NaN;
    this.maximumRangeValueRow=-1;
    this.maximumRangeValueColumn=-1;
  }
  /** 
 * Adds a list of values relating to one box-and-whisker entity to the table.  The various median values are calculated.
 * @param list  a collection of values from which the various medians willbe calculated.
 * @param rowKey  the row key (<code>null</code> not permitted).
 * @param columnKey  the column key (<code>null</code> not permitted).
 * @see #add(BoxAndWhiskerItem,Comparable,Comparable)
 */
  public void add(  List list,  Comparable rowKey,  Comparable columnKey){
    BoxAndWhiskerItem item=BoxAndWhiskerCalculator.calculateBoxAndWhiskerStatistics(list);
    add(item,rowKey,columnKey);
  }
  /** 
 * Adds a list of values relating to one Box and Whisker entity to the table.  The various median values are calculated.
 * @param item  a box and whisker item (<code>null</code> not permitted).
 * @param rowKey  the row key (<code>null</code> not permitted).
 * @param columnKey  the column key (<code>null</code> not permitted).
 * @see #add(List,Comparable,Comparable)
 */
  public void add(  BoxAndWhiskerItem item,  Comparable rowKey,  Comparable columnKey){
    this.data.addObject(item,rowKey,columnKey);
    int r=this.data.getRowIndex(rowKey);
    int c=this.data.getColumnIndex(columnKey);
    if ((this.maximumRangeValueRow == r && this.maximumRangeValueColumn == c) || (this.minimumRangeValueRow == r && this.minimumRangeValueColumn == c)) {
      updateBounds();
    }
 else {
      double minval=Double.NaN;
      if (item.getMinOutlier() != null) {
        minval=item.getMinOutlier().doubleValue();
      }
      double maxval=Double.NaN;
      if (item.getMaxOutlier() != null) {
        maxval=item.getMaxOutlier().doubleValue();
      }
      if (Double.isNaN(this.maximumRangeValue)) {
        this.maximumRangeValue=maxval;
        this.maximumRangeValueRow=r;
        this.maximumRangeValueColumn=c;
      }
 else {
        if (maxval > this.maximumRangeValue) {
          this.maximumRangeValue=maxval;
          this.maximumRangeValueRow=r;
          this.maximumRangeValueColumn=c;
        }
      }
      if (Double.isNaN(this.minimumRangeValue)) {
        this.minimumRangeValue=minval;
        this.minimumRangeValueRow=r;
        this.minimumRangeValueColumn=c;
      }
 else {
        if (minval < this.minimumRangeValue) {
          this.minimumRangeValue=minval;
          this.minimumRangeValueRow=r;
          this.minimumRangeValueColumn=c;
        }
      }
    }
    fireDatasetChanged(new DatasetChangeInfo());
  }
  /** 
 * Removes an item from the dataset and sends a                               {@link DatasetChangeEvent}to all registered listeners.
 * @param rowKey  the row key (<code>null</code> not permitted).
 * @param columnKey  the column key (<code>null</code> not permitted).
 * @see #add(BoxAndWhiskerItem,Comparable,Comparable)
 * @since 1.0.7
 */
  public void remove(  Comparable rowKey,  Comparable columnKey){
    int r=getRowIndex(rowKey);
    int c=getColumnIndex(columnKey);
    this.data.removeObject(rowKey,columnKey);
    if ((this.maximumRangeValueRow == r && this.maximumRangeValueColumn == c) || (this.minimumRangeValueRow == r && this.minimumRangeValueColumn == c)) {
      updateBounds();
    }
    fireDatasetChanged(new DatasetChangeInfo());
  }
  /** 
 * Removes a row from the dataset and sends a                               {@link DatasetChangeEvent}to all registered listeners.
 * @param rowIndex  the row index.
 * @see #removeColumn(int)
 * @since 1.0.7
 */
  public void removeRow(  int rowIndex){
    this.data.removeRow(rowIndex);
    updateBounds();
    fireDatasetChanged(new DatasetChangeInfo());
  }
  /** 
 * Removes a row from the dataset and sends a                               {@link DatasetChangeEvent}to all registered listeners.
 * @param rowKey  the row key.
 * @see #removeColumn(Comparable)
 * @since 1.0.7
 */
  public void removeRow(  Comparable rowKey){
    this.data.removeRow(rowKey);
    updateBounds();
    fireDatasetChanged(new DatasetChangeInfo());
  }
  /** 
 * Removes a column from the dataset and sends a                               {@link DatasetChangeEvent}to all registered listeners.
 * @param columnIndex  the column index.
 * @see #removeRow(int)
 * @since 1.0.7
 */
  public void removeColumn(  int columnIndex){
    this.data.removeColumn(columnIndex);
    updateBounds();
    fireDatasetChanged(new DatasetChangeInfo());
  }
  /** 
 * Removes a column from the dataset and sends a                               {@link DatasetChangeEvent}to all registered listeners.
 * @param columnKey  the column key.
 * @see #removeRow(Comparable)
 * @since 1.0.7
 */
  public void removeColumn(  Comparable columnKey){
    this.data.removeColumn(columnKey);
    updateBounds();
    fireDatasetChanged(new DatasetChangeInfo());
  }
  /** 
 * Clears all data from the dataset and sends a                               {@link DatasetChangeEvent}to all registered listeners.
 * @since 1.0.7
 */
  public void clear(){
    this.data.clear();
    updateBounds();
    fireDatasetChanged(new DatasetChangeInfo());
  }
  /** 
 * Return an item from within the dataset.
 * @param row  the row index.
 * @param column  the column index.
 * @return The item.
 */
  public BoxAndWhiskerItem getItem(  int row,  int column){
    return (BoxAndWhiskerItem)this.data.getObject(row,column);
  }
  /** 
 * Returns the value for an item.
 * @param row  the row index.
 * @param column  the column index.
 * @return The value.
 * @see #getMedianValue(int,int)
 * @see #getValue(Comparable,Comparable)
 */
  public Number getValue(  int row,  int column){
    return getMedianValue(row,column);
  }
  /** 
 * Returns the value for an item.
 * @param rowKey  the row key.
 * @param columnKey  the columnKey.
 * @return The value.
 * @see #getMedianValue(Comparable,Comparable)
 * @see #getValue(int,int)
 */
  public Number getValue(  Comparable rowKey,  Comparable columnKey){
    return getMedianValue(rowKey,columnKey);
  }
  /** 
 * Returns the mean value for an item.
 * @param row  the row index (zero-based).
 * @param column  the column index (zero-based).
 * @return The mean value.
 * @see #getItem(int,int)
 */
  public Number getMeanValue(  int row,  int column){
    Number result=null;
    BoxAndWhiskerItem item=(BoxAndWhiskerItem)this.data.getObject(row,column);
    if (item != null) {
      result=item.getMean();
    }
    return result;
  }
  /** 
 * Returns the mean value for an item.
 * @param rowKey  the row key.
 * @param columnKey  the column key.
 * @return The mean value.
 * @see #getItem(int,int)
 */
  public Number getMeanValue(  Comparable rowKey,  Comparable columnKey){
    Number result=null;
    BoxAndWhiskerItem item=(BoxAndWhiskerItem)this.data.getObject(rowKey,columnKey);
    if (item != null) {
      result=item.getMean();
    }
    return result;
  }
  /** 
 * Returns the median value for an item.
 * @param row  the row index (zero-based).
 * @param column  the column index (zero-based).
 * @return The median value.
 * @see #getItem(int,int)
 */
  public Number getMedianValue(  int row,  int column){
    Number result=null;
    BoxAndWhiskerItem item=(BoxAndWhiskerItem)this.data.getObject(row,column);
    if (item != null) {
      result=item.getMedian();
    }
    return result;
  }
  /** 
 * Returns the median value for an item.
 * @param rowKey  the row key.
 * @param columnKey  the columnKey.
 * @return The median value.
 * @see #getItem(int,int)
 */
  public Number getMedianValue(  Comparable rowKey,  Comparable columnKey){
    Number result=null;
    BoxAndWhiskerItem item=(BoxAndWhiskerItem)this.data.getObject(rowKey,columnKey);
    if (item != null) {
      result=item.getMedian();
    }
    return result;
  }
  /** 
 * Returns the first quartile value.
 * @param row  the row index (zero-based).
 * @param column  the column index (zero-based).
 * @return The first quartile value.
 * @see #getItem(int,int)
 */
  public Number getQ1Value(  int row,  int column){
    Number result=null;
    BoxAndWhiskerItem item=(BoxAndWhiskerItem)this.data.getObject(row,column);
    if (item != null) {
      result=item.getQ1();
    }
    return result;
  }
  /** 
 * Returns the first quartile value.
 * @param rowKey  the row key.
 * @param columnKey  the column key.
 * @return The first quartile value.
 * @see #getItem(int,int)
 */
  public Number getQ1Value(  Comparable rowKey,  Comparable columnKey){
    Number result=null;
    BoxAndWhiskerItem item=(BoxAndWhiskerItem)this.data.getObject(rowKey,columnKey);
    if (item != null) {
      result=item.getQ1();
    }
    return result;
  }
  /** 
 * Returns the third quartile value.
 * @param row  the row index (zero-based).
 * @param column  the column index (zero-based).
 * @return The third quartile value.
 * @see #getItem(int,int)
 */
  public Number getQ3Value(  int row,  int column){
    Number result=null;
    BoxAndWhiskerItem item=(BoxAndWhiskerItem)this.data.getObject(row,column);
    if (item != null) {
      result=item.getQ3();
    }
    return result;
  }
  /** 
 * Returns the third quartile value.
 * @param rowKey  the row key.
 * @param columnKey  the column key.
 * @return The third quartile value.
 * @see #getItem(int,int)
 */
  public Number getQ3Value(  Comparable rowKey,  Comparable columnKey){
    Number result=null;
    BoxAndWhiskerItem item=(BoxAndWhiskerItem)this.data.getObject(rowKey,columnKey);
    if (item != null) {
      result=item.getQ3();
    }
    return result;
  }
  /** 
 * Returns the column index for a given key.
 * @param key  the column key (<code>null</code> not permitted).
 * @return The column index.
 * @see #getColumnKey(int)
 */
  public int getColumnIndex(  Comparable key){
    return this.data.getColumnIndex(key);
  }
  /** 
 * Returns a column key.
 * @param column  the column index (zero-based).
 * @return The column key.
 * @see #getColumnIndex(Comparable)
 */
  public Comparable getColumnKey(  int column){
    return this.data.getColumnKey(column);
  }
  /** 
 * Returns the column keys.
 * @return The keys.
 * @see #getRowKeys()
 */
  public List getColumnKeys(){
    return this.data.getColumnKeys();
  }
  /** 
 * Returns the row index for a given key.
 * @param key  the row key (<code>null</code> not permitted).
 * @return The row index.
 * @see #getRowKey(int)
 */
  public int getRowIndex(  Comparable key){
    return this.data.getRowIndex(key);
  }
  /** 
 * Returns a row key.
 * @param row  the row index (zero-based).
 * @return The row key.
 * @see #getRowIndex(Comparable)
 */
  public Comparable getRowKey(  int row){
    return this.data.getRowKey(row);
  }
  /** 
 * Returns the row keys.
 * @return The keys.
 * @see #getColumnKeys()
 */
  public List getRowKeys(){
    return this.data.getRowKeys();
  }
  /** 
 * Returns the number of rows in the table.
 * @return The row count.
 * @see #getColumnCount()
 */
  public int getRowCount(){
    return this.data.getRowCount();
  }
  /** 
 * Returns the number of columns in the table.
 * @return The column count.
 * @see #getRowCount()
 */
  public int getColumnCount(){
    return this.data.getColumnCount();
  }
  /** 
 * Returns the minimum y-value in the dataset.
 * @param includeInterval  a flag that determines whether or not they-interval is taken into account.
 * @return The minimum value.
 * @see #getRangeUpperBound(boolean)
 */
  public double getRangeLowerBound(  boolean includeInterval){
    return this.minimumRangeValue;
  }
  /** 
 * Returns the maximum y-value in the dataset.
 * @param includeInterval  a flag that determines whether or not they-interval is taken into account.
 * @return The maximum value.
 * @see #getRangeLowerBound(boolean)
 */
  public double getRangeUpperBound(  boolean includeInterval){
    return this.maximumRangeValue;
  }
  /** 
 * Returns the range of the values in this dataset's range.
 * @param includeInterval  a flag that determines whether or not they-interval is taken into account.
 * @return The range.
 */
  public Range getRangeBounds(  boolean includeInterval){
    return new Range(this.minimumRangeValue,this.maximumRangeValue);
  }
  /** 
 * Returns the minimum regular (non outlier) value for an item.
 * @param row  the row index (zero-based).
 * @param column  the column index (zero-based).
 * @return The minimum regular value.
 * @see #getItem(int,int)
 */
  public Number getMinRegularValue(  int row,  int column){
    Number result=null;
    BoxAndWhiskerItem item=(BoxAndWhiskerItem)this.data.getObject(row,column);
    if (item != null) {
      result=item.getMinRegularValue();
    }
    return result;
  }
  /** 
 * Returns the minimum regular (non outlier) value for an item.
 * @param rowKey  the row key.
 * @param columnKey  the column key.
 * @return The minimum regular value.
 * @see #getItem(int,int)
 */
  public Number getMinRegularValue(  Comparable rowKey,  Comparable columnKey){
    Number result=null;
    BoxAndWhiskerItem item=(BoxAndWhiskerItem)this.data.getObject(rowKey,columnKey);
    if (item != null) {
      result=item.getMinRegularValue();
    }
    return result;
  }
  /** 
 * Returns the maximum regular (non outlier) value for an item.
 * @param row  the row index (zero-based).
 * @param column  the column index (zero-based).
 * @return The maximum regular value.
 * @see #getItem(int,int)
 */
  public Number getMaxRegularValue(  int row,  int column){
    Number result=null;
    BoxAndWhiskerItem item=(BoxAndWhiskerItem)this.data.getObject(row,column);
    if (item != null) {
      result=item.getMaxRegularValue();
    }
    return result;
  }
  /** 
 * Returns the maximum regular (non outlier) value for an item.
 * @param rowKey  the row key.
 * @param columnKey  the column key.
 * @return The maximum regular value.
 * @see #getItem(int,int)
 */
  public Number getMaxRegularValue(  Comparable rowKey,  Comparable columnKey){
    Number result=null;
    BoxAndWhiskerItem item=(BoxAndWhiskerItem)this.data.getObject(rowKey,columnKey);
    if (item != null) {
      result=item.getMaxRegularValue();
    }
    return result;
  }
  /** 
 * Returns the minimum outlier (non farout) value for an item.
 * @param row  the row index (zero-based).
 * @param column  the column index (zero-based).
 * @return The minimum outlier.
 * @see #getItem(int,int)
 */
  public Number getMinOutlier(  int row,  int column){
    Number result=null;
    BoxAndWhiskerItem item=(BoxAndWhiskerItem)this.data.getObject(row,column);
    if (item != null) {
      result=item.getMinOutlier();
    }
    return result;
  }
  /** 
 * Returns the minimum outlier (non farout) value for an item.
 * @param rowKey  the row key.
 * @param columnKey  the column key.
 * @return The minimum outlier.
 * @see #getItem(int,int)
 */
  public Number getMinOutlier(  Comparable rowKey,  Comparable columnKey){
    Number result=null;
    BoxAndWhiskerItem item=(BoxAndWhiskerItem)this.data.getObject(rowKey,columnKey);
    if (item != null) {
      result=item.getMinOutlier();
    }
    return result;
  }
  /** 
 * Returns the maximum outlier (non farout) value for an item.
 * @param row  the row index (zero-based).
 * @param column  the column index (zero-based).
 * @return The maximum outlier.
 * @see #getItem(int,int)
 */
  public Number getMaxOutlier(  int row,  int column){
    Number result=null;
    BoxAndWhiskerItem item=(BoxAndWhiskerItem)this.data.getObject(row,column);
    if (item != null) {
      result=item.getMaxOutlier();
    }
    return result;
  }
  /** 
 * Returns the maximum outlier (non farout) value for an item.
 * @param rowKey  the row key.
 * @param columnKey  the column key.
 * @return The maximum outlier.
 * @see #getItem(int,int)
 */
  public Number getMaxOutlier(  Comparable rowKey,  Comparable columnKey){
    Number result=null;
    BoxAndWhiskerItem item=(BoxAndWhiskerItem)this.data.getObject(rowKey,columnKey);
    if (item != null) {
      result=item.getMaxOutlier();
    }
    return result;
  }
  /** 
 * Returns a list of outlier values for an item.
 * @param row  the row index (zero-based).
 * @param column  the column index (zero-based).
 * @return A list of outlier values.
 * @see #getItem(int,int)
 */
  public List getOutliers(  int row,  int column){
    List result=null;
    BoxAndWhiskerItem item=(BoxAndWhiskerItem)this.data.getObject(row,column);
    if (item != null) {
      result=item.getOutliers();
    }
    return result;
  }
  /** 
 * Returns a list of outlier values for an item.
 * @param rowKey  the row key.
 * @param columnKey  the column key.
 * @return A list of outlier values.
 * @see #getItem(int,int)
 */
  public List getOutliers(  Comparable rowKey,  Comparable columnKey){
    List result=null;
    BoxAndWhiskerItem item=(BoxAndWhiskerItem)this.data.getObject(rowKey,columnKey);
    if (item != null) {
      result=item.getOutliers();
    }
    return result;
  }
  /** 
 * Resets the cached bounds, by iterating over the entire dataset to find the current bounds.
 */
  private void updateBounds(){
    this.minimumRangeValue=Double.NaN;
    this.minimumRangeValueRow=-1;
    this.minimumRangeValueColumn=-1;
    this.maximumRangeValue=Double.NaN;
    this.maximumRangeValueRow=-1;
    this.maximumRangeValueColumn=-1;
    int rowCount=getRowCount();
    int columnCount=getColumnCount();
    for (int r=0; r < rowCount; r++) {
      for (int c=0; c < columnCount; c++) {
        BoxAndWhiskerItem item=getItem(r,c);
        if (item != null) {
          Number min=item.getMinOutlier();
          if (min != null) {
            double minv=min.doubleValue();
            if (!Double.isNaN(minv)) {
              if (minv < this.minimumRangeValue || Double.isNaN(this.minimumRangeValue)) {
                this.minimumRangeValue=minv;
                this.minimumRangeValueRow=r;
                this.minimumRangeValueColumn=c;
              }
            }
          }
          Number max=item.getMaxOutlier();
          if (max != null) {
            double maxv=max.doubleValue();
            if (!Double.isNaN(maxv)) {
              if (maxv > this.maximumRangeValue || Double.isNaN(this.maximumRangeValue)) {
                this.maximumRangeValue=maxv;
                this.maximumRangeValueRow=r;
                this.maximumRangeValueColumn=c;
              }
            }
          }
        }
      }
    }
  }
  /** 
 * Tests this dataset for equality with an arbitrary object.
 * @param obj  the object to test against (<code>null</code> permitted).
 * @return A boolean.
 */
  public boolean equals(  Object obj){
    if (obj == this) {
      return true;
    }
    if (obj instanceof DefaultBoxAndWhiskerCategoryDataset) {
      DefaultBoxAndWhiskerCategoryDataset dataset=(DefaultBoxAndWhiskerCategoryDataset)obj;
      return ObjectUtilities.equal(this.data,dataset.data);
    }
    return false;
  }
  /** 
 * Returns a clone of this dataset.
 * @return A clone.
 * @throws CloneNotSupportedException if cloning is not possible.
 */
  public Object clone() throws CloneNotSupportedException {
    DefaultBoxAndWhiskerCategoryDataset clone=(DefaultBoxAndWhiskerCategoryDataset)super.clone();
    clone.data=(KeyedObjects2D)this.data.clone();
    return clone;
  }
}
