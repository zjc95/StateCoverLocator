package org.jfree.data.statistics;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.jfree.chart.event.DatasetChangeInfo;
import org.jfree.chart.util.ObjectUtilities;
import org.jfree.chart.util.PublicCloneable;
import org.jfree.data.DomainOrder;
import org.jfree.data.event.DatasetChangeEvent;
import org.jfree.data.xy.AbstractIntervalXYDataset;
import org.jfree.data.xy.IntervalXYDataset;
import org.jfree.data.xy.SelectableXYDataset;
import org.jfree.data.xy.XYDatasetSelectionState;
/** 
 * A dataset used for creating simple histograms with custom defined bins.
 * @see HistogramDataset
 */
public class SimpleHistogramDataset extends AbstractIntervalXYDataset implements IntervalXYDataset, XYDatasetSelectionState, SelectableXYDataset, Cloneable, PublicCloneable, Serializable {
  /** 
 * For serialization. 
 */
  private static final long serialVersionUID=7997996479768018443L;
  /** 
 * The series key. 
 */
  private Comparable key;
  /** 
 * The bins. 
 */
  private List bins;
  /** 
 * A flag that controls whether or not the bin count is divided by the bin size.
 */
  private boolean adjustForBinSize;
  /** 
 * Creates a new histogram dataset.  Note that the <code>adjustForBinSize</code> flag defaults to <code>true</code>.
 * @param key  the series key (<code>null</code> not permitted).
 */
  public SimpleHistogramDataset(  Comparable key){
    if (key == null) {
      throw new IllegalArgumentException("Null 'key' argument.");
    }
    this.key=key;
    this.bins=new ArrayList();
    this.adjustForBinSize=true;
    setSelectionState(this);
  }
  /** 
 * Returns a flag that controls whether or not the bin count is divided by the bin size in the                                                                                                                                                                    {@link #getXValue(int,int)} method.
 * @return A boolean.
 * @see #setAdjustForBinSize(boolean)
 */
  public boolean getAdjustForBinSize(){
    return this.adjustForBinSize;
  }
  /** 
 * Sets the flag that controls whether or not the bin count is divided by the bin size in the                                                                                                                                                                    {@link #getYValue(int,int)} method, and sends a{@link DatasetChangeEvent} to all registered listeners.
 * @param adjust  the flag.
 * @see #getAdjustForBinSize()
 */
  public void setAdjustForBinSize(  boolean adjust){
    this.adjustForBinSize=adjust;
    fireDatasetChanged(new DatasetChangeInfo());
  }
  /** 
 * Returns the number of series in the dataset (always 1 for this dataset).
 * @return The series count.
 */
  public int getSeriesCount(){
    return 1;
  }
  /** 
 * Returns the key for a series.  Since this dataset only stores a single series, the <code>series</code> argument is ignored.
 * @param series  the series (zero-based index, ignored in this dataset).
 * @return The key for the series.
 */
  public Comparable getSeriesKey(  int series){
    return this.key;
  }
  /** 
 * Returns the order of the domain (or X) values returned by the dataset.
 * @return The order (never <code>null</code>).
 */
  public DomainOrder getDomainOrder(){
    return DomainOrder.ASCENDING;
  }
  /** 
 * Returns the number of items in a series.  Since this dataset only stores a single series, the <code>series</code> argument is ignored.
 * @param series  the series index (zero-based, ignored in this dataset).
 * @return The item count.
 */
  public int getItemCount(  int series){
    return this.bins.size();
  }
  /** 
 * Adds a bin to the dataset.  An exception is thrown if the bin overlaps with any existing bin in the dataset.
 * @param bin  the bin (<code>null</code> not permitted).
 * @see #removeAllBins()
 */
  public void addBin(  SimpleHistogramBin bin){
    Iterator iterator=this.bins.iterator();
    while (iterator.hasNext()) {
      SimpleHistogramBin existingBin=(SimpleHistogramBin)iterator.next();
      if (bin.overlapsWith(existingBin)) {
        throw new RuntimeException("Overlapping bin");
      }
    }
    this.bins.add(bin);
    Collections.sort(this.bins);
  }
  /** 
 * Adds an observation to the dataset (by incrementing the item count for the appropriate bin).  A runtime exception is thrown if the value does not fit into any bin.
 * @param value  the value.
 */
  public void addObservation(  double value){
    addObservation(value,true);
  }
  /** 
 * Adds an observation to the dataset (by incrementing the item count for the appropriate bin).  A runtime exception is thrown if the value does not fit into any bin.
 * @param value  the value.
 * @param notify  send {@link DatasetChangeEvent} to listeners?
 */
  public void addObservation(  double value,  boolean notify){
    boolean placed=false;
    Iterator iterator=this.bins.iterator();
    while (iterator.hasNext() && !placed) {
      SimpleHistogramBin bin=(SimpleHistogramBin)iterator.next();
      if (bin.accepts(value)) {
        bin.setItemCount(bin.getItemCount() + 1);
        placed=true;
      }
    }
    if (!placed) {
      throw new RuntimeException("No bin.");
    }
    if (notify) {
      fireDatasetChanged(new DatasetChangeInfo());
    }
  }
  /** 
 * Adds a set of values to the dataset and sends a                                                                                                                                                                   {@link DatasetChangeEvent} to all registered listeners.
 * @param values  the values (<code>null</code> not permitted).
 * @see #clearObservations()
 */
  public void addObservations(  double[] values){
    for (int i=0; i < values.length; i++) {
      addObservation(values[i],false);
    }
    fireDatasetChanged(new DatasetChangeInfo());
  }
  /** 
 * Removes all current observation data and sends a                                                                                                                                                                   {@link DatasetChangeEvent} to all registered listeners.
 * @since 1.0.6
 * @see #addObservations(double[])
 * @see #removeAllBins()
 */
  public void clearObservations(){
    Iterator iterator=this.bins.iterator();
    while (iterator.hasNext()) {
      SimpleHistogramBin bin=(SimpleHistogramBin)iterator.next();
      bin.setItemCount(0);
    }
    fireDatasetChanged(new DatasetChangeInfo());
  }
  /** 
 * Removes all bins and sends a                                                                                                                                                                    {@link DatasetChangeEvent} to allregistered listeners.
 * @since 1.0.6
 * @see #addBin(SimpleHistogramBin)
 */
  public void removeAllBins(){
    this.bins=new ArrayList();
    fireDatasetChanged(new DatasetChangeInfo());
  }
  /** 
 * Returns the x-value for an item within a series.  The x-values may or may not be returned in ascending order, that is up to the class implementing the interface.
 * @param series  the series index (zero-based).
 * @param item  the item index (zero-based).
 * @return The x-value (never <code>null</code>).
 */
  public Number getX(  int series,  int item){
    return new Double(getXValue(series,item));
  }
  /** 
 * Returns the x-value (as a double primitive) for an item within a series.
 * @param series  the series index (zero-based).
 * @param item  the item index (zero-based).
 * @return The x-value.
 */
  public double getXValue(  int series,  int item){
    SimpleHistogramBin bin=(SimpleHistogramBin)this.bins.get(item);
    return (bin.getLowerBound() + bin.getUpperBound()) / 2.0;
  }
  /** 
 * Returns the y-value for an item within a series.
 * @param series  the series index (zero-based).
 * @param item  the item index (zero-based).
 * @return The y-value (possibly <code>null</code>).
 */
  public Number getY(  int series,  int item){
    return new Double(getYValue(series,item));
  }
  /** 
 * Returns the y-value (as a double primitive) for an item within a series.
 * @param series  the series index (zero-based).
 * @param item  the item index (zero-based).
 * @return The y-value.
 * @see #getAdjustForBinSize()
 */
  public double getYValue(  int series,  int item){
    SimpleHistogramBin bin=(SimpleHistogramBin)this.bins.get(item);
    if (this.adjustForBinSize) {
      return bin.getItemCount() / (bin.getUpperBound() - bin.getLowerBound());
    }
 else {
      return bin.getItemCount();
    }
  }
  /** 
 * Returns the starting X value for the specified series and item.
 * @param series  the series index (zero-based).
 * @param item  the item index (zero-based).
 * @return The value.
 */
  public Number getStartX(  int series,  int item){
    return new Double(getStartXValue(series,item));
  }
  /** 
 * Returns the start x-value (as a double primitive) for an item within a series.
 * @param series  the series (zero-based index).
 * @param item  the item (zero-based index).
 * @return The start x-value.
 */
  public double getStartXValue(  int series,  int item){
    SimpleHistogramBin bin=(SimpleHistogramBin)this.bins.get(item);
    return bin.getLowerBound();
  }
  /** 
 * Returns the ending X value for the specified series and item.
 * @param series  the series index (zero-based).
 * @param item  the item index (zero-based).
 * @return The value.
 */
  public Number getEndX(  int series,  int item){
    return new Double(getEndXValue(series,item));
  }
  /** 
 * Returns the end x-value (as a double primitive) for an item within a series.
 * @param series  the series index (zero-based).
 * @param item  the item index (zero-based).
 * @return The end x-value.
 */
  public double getEndXValue(  int series,  int item){
    SimpleHistogramBin bin=(SimpleHistogramBin)this.bins.get(item);
    return bin.getUpperBound();
  }
  /** 
 * Returns the starting Y value for the specified series and item.
 * @param series  the series index (zero-based).
 * @param item  the item index (zero-based).
 * @return The value.
 */
  public Number getStartY(  int series,  int item){
    return getY(series,item);
  }
  /** 
 * Returns the start y-value (as a double primitive) for an item within a series.
 * @param series  the series index (zero-based).
 * @param item  the item index (zero-based).
 * @return The start y-value.
 */
  public double getStartYValue(  int series,  int item){
    return getYValue(series,item);
  }
  /** 
 * Returns the ending Y value for the specified series and item.
 * @param series  the series index (zero-based).
 * @param item  the item index (zero-based).
 * @return The value.
 */
  public Number getEndY(  int series,  int item){
    return getY(series,item);
  }
  /** 
 * Returns the end y-value (as a double primitive) for an item within a series.
 * @param series  the series index (zero-based).
 * @param item  the item index (zero-based).
 * @return The end y-value.
 */
  public double getEndYValue(  int series,  int item){
    return getYValue(series,item);
  }
  /** 
 * Returns <code>true</code> if the specified item is selected, and <code>false</code> otherwise.
 * @param series  the series index.
 * @param item  the item index.
 * @since 1.2.0
 */
  public boolean isSelected(  int series,  int item){
    SimpleHistogramBin bin=(SimpleHistogramBin)this.bins.get(item);
    return bin.isSelected();
  }
  /** 
 * Sets the selection state of the specified item and sends a                                                                                                                                                                   {@link DatasetChangeEvent} to all registered listeners.
 * @since 1.2.0
 */
  public void setSelected(  int series,  int item,  boolean selected){
    setSelected(series,item,selected,true);
  }
  /** 
 * Sets the selection state of the specified item and, if requested, sends a                                                                                                                                                                    {@link DatasetChangeEvent} to all registered listeners.
 * @param series  the series index.
 * @param item  the item index.
 * @param selected  the selection state.
 * @param notify  notify listeners?
 * @since 1.2.0
 */
  public void setSelected(  int series,  int item,  boolean selected,  boolean notify){
    SimpleHistogramBin bin=(SimpleHistogramBin)this.bins.get(item);
    bin.setSelected(selected);
    if (notify) {
      fireSelectionEvent();
    }
  }
  /** 
 * Clears the selection state of all items in the dataset and sends a                                                                                                                                                                   {@link DatasetChangeEvent} to all registered listeners.
 * @since 1.2.0
 */
  public void clearSelection(){
    Iterator iterator=this.bins.iterator();
    boolean changed=false;
    while (iterator.hasNext()) {
      SimpleHistogramBin bin=(SimpleHistogramBin)iterator.next();
      if (bin.isSelected()) {
        bin.setSelected(false);
        changed=true;
      }
    }
    if (changed) {
      fireSelectionEvent();
    }
  }
  /** 
 * Sends an event to all registered listeners to indicate that the selection has changed.
 * @since 1.2.0
 */
  public void fireSelectionEvent(){
    fireDatasetChanged(new DatasetChangeInfo());
  }
  /** 
 * Compares the dataset for equality with an arbitrary object.
 * @param obj  the object (<code>null</code> permitted).
 * @return A boolean.
 */
  public boolean equals(  Object obj){
    if (obj == this) {
      return true;
    }
    if (!(obj instanceof SimpleHistogramDataset)) {
      return false;
    }
    SimpleHistogramDataset that=(SimpleHistogramDataset)obj;
    if (!this.key.equals(that.key)) {
      return false;
    }
    if (this.adjustForBinSize != that.adjustForBinSize) {
      return false;
    }
    if (!this.bins.equals(that.bins)) {
      return false;
    }
    return true;
  }
  /** 
 * Returns a clone of the dataset.
 * @return A clone.
 * @throws CloneNotSupportedException not thrown by this class, but maybeby subclasses (if any).
 */
  public Object clone() throws CloneNotSupportedException {
    SimpleHistogramDataset clone=(SimpleHistogramDataset)super.clone();
    clone.bins=(List)ObjectUtilities.deepClone(this.bins);
    return clone;
  }
}
