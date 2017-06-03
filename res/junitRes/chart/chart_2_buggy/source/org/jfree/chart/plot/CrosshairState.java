package org.jfree.chart.plot;
import java.awt.geom.Point2D;
/** 
 * Maintains state information about crosshairs on a plot between successive calls to the renderer's draw method.  This class is used internally by JFreeChart - it is not intended for external use.
 */
public class CrosshairState {
  /** 
 * A flag that controls whether the distance is calculated in data space or Java2D space.
 */
  private boolean calculateDistanceInDataSpace=false;
  /** 
 * The x-value (in data space) for the anchor point. 
 */
  private double anchorX;
  /** 
 * The y-value (in data space) for the anchor point. 
 */
  private double anchorY;
  /** 
 * The anchor point in Java2D space - if null, don't update crosshair. 
 */
  private Point2D anchor;
  /** 
 * The x-value for the current crosshair point. 
 */
  private double crosshairX;
  /** 
 * The y-value for the current crosshair point. 
 */
  private double crosshairY;
  /** 
 * The dataset index that the crosshair point relates to (this determines the axes that the crosshairs will be plotted against).
 * @since 1.0.11
 */
  private int datasetIndex;
  /** 
 * The index of the domain axis that the crosshair x-value is measured against.
 * @since 1.0.4
 */
  private int domainAxisIndex;
  /** 
 * The index of the range axis that the crosshair y-value is measured against.
 * @since 1.0.4
 */
  private int rangeAxisIndex;
  /** 
 * The smallest distance (so far) between the anchor point and a data point.
 */
  private double distance;
  /** 
 * Creates a new <code>CrosshairState</code> instance that calculates distance in Java2D space.
 */
  public CrosshairState(){
    this(false);
  }
  /** 
 * Creates a new <code>CrosshairState</code> instance.
 * @param calculateDistanceInDataSpace  a flag that controls whether thedistance is calculated in data space or Java2D space.
 */
  public CrosshairState(  boolean calculateDistanceInDataSpace){
    this.calculateDistanceInDataSpace=calculateDistanceInDataSpace;
  }
  /** 
 * Returns the distance between the anchor point and the current crosshair point.
 * @return The distance.
 * @see #setCrosshairDistance(double)
 * @since 1.0.3
 */
  public double getCrosshairDistance(){
    return this.distance;
  }
  /** 
 * Sets the distance between the anchor point and the current crosshair point.  As each data point is processed, its distance to the anchor point is compared with this value and, if it is closer, the data point becomes the new crosshair point.
 * @param distance  the distance.
 * @see #getCrosshairDistance()
 */
  public void setCrosshairDistance(  double distance){
    this.distance=distance;
  }
  /** 
 * Evaluates a data point and if it is the closest to the anchor point it becomes the new crosshair point. <P> To understand this method, you need to know the context in which it will be called.  An instance of this class is passed to an                                                                                                                                                                    {@link org.jfree.chart.renderer.xy.XYItemRenderer} aseach data point is plotted.  As the point is plotted, it is passed to this method to see if it should be the new crosshair point.
 * @param x  x coordinate (measured against the domain axis).
 * @param y  y coordinate (measured against the range axis).
 * @param transX  x translated into Java2D space.
 * @param transY  y translated into Java2D space.
 * @param orientation  the plot orientation.
 * @deprecated Use {@link #updateCrosshairPoint(double,double,int,int,double,double,PlotOrientation)}.  See bug report 1086307.
 */
  public void updateCrosshairPoint(  double x,  double y,  double transX,  double transY,  PlotOrientation orientation){
    updateCrosshairPoint(x,y,0,0,transX,transY,orientation);
  }
  /** 
 * Evaluates a data point and if it is the closest to the anchor point it becomes the new crosshair point. <P> To understand this method, you need to know the context in which it will be called.  An instance of this class is passed to an                                                                                                                                                                    {@link org.jfree.chart.renderer.xy.XYItemRenderer} aseach data point is plotted.  As the point is plotted, it is passed to this method to see if it should be the new crosshair point.
 * @param x  x coordinate (measured against the domain axis).
 * @param y  y coordinate (measured against the range axis).
 * @param domainAxisIndex  the index of the domain axis for this point.
 * @param rangeAxisIndex  the index of the range axis for this point.
 * @param transX  x translated into Java2D space.
 * @param transY  y translated into Java2D space.
 * @param orientation  the plot orientation.
 * @since 1.0.4
 */
  public void updateCrosshairPoint(  double x,  double y,  int domainAxisIndex,  int rangeAxisIndex,  double transX,  double transY,  PlotOrientation orientation){
    if (this.anchor != null) {
      double d=0.0;
      if (this.calculateDistanceInDataSpace) {
        d=(x - this.anchorX) * (x - this.anchorX) + (y - this.anchorY) * (y - this.anchorY);
      }
 else {
        double xx=this.anchor.getX();
        double yy=this.anchor.getY();
        if (orientation == PlotOrientation.HORIZONTAL) {
          double temp=yy;
          yy=xx;
          xx=temp;
        }
        d=(transX - xx) * (transX - xx) + (transY - yy) * (transY - yy);
      }
      if (d < this.distance) {
        this.crosshairX=x;
        this.crosshairY=y;
        this.domainAxisIndex=domainAxisIndex;
        this.rangeAxisIndex=rangeAxisIndex;
        this.distance=d;
      }
    }
  }
  /** 
 * Evaluates an x-value and if it is the closest to the anchor x-value it becomes the new crosshair value. <P> Used in cases where only the x-axis is numerical.
 * @param candidateX  x position of the candidate for the new crosshairpoint.
 * @deprecated Use {@link #updateCrosshairX(double,int)}.  See bug report 1086307.
 */
  public void updateCrosshairX(  double candidateX){
    updateCrosshairX(candidateX,0);
  }
  /** 
 * Evaluates an x-value and if it is the closest to the anchor x-value it becomes the new crosshair value. <P> Used in cases where only the x-axis is numerical.
 * @param candidateX  x position of the candidate for the new crosshairpoint.
 * @param domainAxisIndex  the index of the domain axis for this x-value.
 * @since 1.0.4
 */
  public void updateCrosshairX(  double candidateX,  int domainAxisIndex){
    double d=Math.abs(candidateX - this.anchorX);
    if (d < this.distance) {
      this.crosshairX=candidateX;
      this.domainAxisIndex=domainAxisIndex;
      this.distance=d;
    }
  }
  /** 
 * Evaluates a y-value and if it is the closest to the anchor y-value it becomes the new crosshair value. <P> Used in cases where only the y-axis is numerical.
 * @param candidateY  y position of the candidate for the new crosshairpoint.
 * @deprecated Use {@link #updateCrosshairY(double,int)}.  See bug report 1086307.
 */
  public void updateCrosshairY(  double candidateY){
    updateCrosshairY(candidateY,0);
  }
  /** 
 * Evaluates a y-value and if it is the closest to the anchor y-value it becomes the new crosshair value. <P> Used in cases where only the y-axis is numerical.
 * @param candidateY  y position of the candidate for the new crosshairpoint.
 * @param rangeAxisIndex  the index of the range axis for this y-value.
 * @since 1.0.4
 */
  public void updateCrosshairY(  double candidateY,  int rangeAxisIndex){
    double d=Math.abs(candidateY - this.anchorY);
    if (d < this.distance) {
      this.crosshairY=candidateY;
      this.rangeAxisIndex=rangeAxisIndex;
      this.distance=d;
    }
  }
  /** 
 * Returns the anchor point.
 * @return The anchor point.
 * @see #setAnchor(Point2D)
 * @since 1.0.3
 */
  public Point2D getAnchor(){
    return this.anchor;
  }
  /** 
 * Sets the anchor point.  This is usually the mouse click point in a chart panel, and the crosshair point will often be the data item that is closest to the anchor point. <br><br> Note that the x and y coordinates (in data space) are not updated by this method - the caller is responsible for ensuring that this happens in sync.
 * @param anchor  the anchor point (<code>null</code> permitted).
 * @see #getAnchor()
 */
  public void setAnchor(  Point2D anchor){
    this.anchor=anchor;
  }
  /** 
 * Returns the x-coordinate (in data space) for the anchor point.
 * @return The x-coordinate of the anchor point.
 * @since 1.0.3
 */
  public double getAnchorX(){
    return this.anchorX;
  }
  /** 
 * Sets the x-coordinate (in data space) for the anchor point.  Note that this does NOT update the anchor itself - the caller is responsible for ensuring this is done in sync.
 * @param x  the x-coordinate.
 * @since 1.0.3
 */
  public void setAnchorX(  double x){
    this.anchorX=x;
  }
  /** 
 * Returns the y-coordinate (in data space) for the anchor point.
 * @return The y-coordinate of teh anchor point.
 * @since 1.0.3
 */
  public double getAnchorY(){
    return this.anchorY;
  }
  /** 
 * Sets the y-coordinate (in data space) for the anchor point.  Note that this does NOT update the anchor itself - the caller is responsible for ensuring this is done in sync.
 * @param y  the y-coordinate.
 * @since 1.0.3
 */
  public void setAnchorY(  double y){
    this.anchorY=y;
  }
  /** 
 * Get the x-value for the crosshair point.
 * @return The x position of the crosshair point.
 * @see #setCrosshairX(double)
 */
  public double getCrosshairX(){
    return this.crosshairX;
  }
  /** 
 * Sets the x coordinate for the crosshair.  This is the coordinate in data space measured against the domain axis.
 * @param x the coordinate.
 * @see #getCrosshairX()
 * @see #setCrosshairY(double)
 * @see #updateCrosshairPoint(double,double,double,double,PlotOrientation)
 */
  public void setCrosshairX(  double x){
    this.crosshairX=x;
  }
  /** 
 * Get the y-value for the crosshair point.  This is the coordinate in data space measured against the range axis.
 * @return The y position of the crosshair point.
 * @see #setCrosshairY(double)
 */
  public double getCrosshairY(){
    return this.crosshairY;
  }
  /** 
 * Sets the y coordinate for the crosshair.
 * @param y  the y coordinate.
 * @see #getCrosshairY()
 * @see #setCrosshairX(double)
 * @see #updateCrosshairPoint(double,double,double,double,PlotOrientation)
 */
  public void setCrosshairY(  double y){
    this.crosshairY=y;
  }
  /** 
 * Returns the dataset index that the crosshair values relate to.  The dataset is mapped to specific axes, and this is how the crosshairs are mapped also.
 * @return The dataset index.
 * @see #setDatasetIndex(int)
 * @since 1.0.11
 */
  public int getDatasetIndex(){
    return this.datasetIndex;
  }
  /** 
 * Sets the dataset index that the current crosshair values relate to.
 * @param index  the dataset index.
 * @see #getDatasetIndex()
 * @since 1.0.11
 */
  public void setDatasetIndex(  int index){
    this.datasetIndex=index;
  }
  /** 
 * Returns the domain axis index for the crosshair x-value.
 * @return The domain axis index.
 * @since 1.0.4
 * @deprecated As of version 1.0.11, the domain axis should be determinedusing the dataset index.
 */
  public int getDomainAxisIndex(){
    return this.domainAxisIndex;
  }
  /** 
 * Returns the range axis index for the crosshair y-value.
 * @return The range axis index.
 * @since 1.0.4
 * @deprecated As of version 1.0.11, the domain axis should be determinedusing the dataset index.
 */
  public int getRangeAxisIndex(){
    return this.rangeAxisIndex;
  }
}
