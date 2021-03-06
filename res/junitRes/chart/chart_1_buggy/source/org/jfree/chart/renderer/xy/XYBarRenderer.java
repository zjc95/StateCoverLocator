package org.jfree.chart.renderer.xy;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import org.jfree.chart.LegendItem;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.entity.EntityCollection;
import org.jfree.chart.event.RendererChangeEvent;
import org.jfree.chart.labels.ItemLabelAnchor;
import org.jfree.chart.labels.ItemLabelPosition;
import org.jfree.chart.labels.XYItemLabelGenerator;
import org.jfree.chart.labels.XYSeriesLabelGenerator;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.XYCrosshairState;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.text.TextUtilities;
import org.jfree.chart.util.GradientPaintTransformer;
import org.jfree.chart.util.ObjectUtilities;
import org.jfree.chart.util.PublicCloneable;
import org.jfree.chart.util.RectangleEdge;
import org.jfree.chart.util.SerialUtilities;
import org.jfree.chart.util.ShapeUtilities;
import org.jfree.chart.util.StandardGradientPaintTransformer;
import org.jfree.data.Range;
import org.jfree.data.general.DatasetUtilities;
import org.jfree.data.xy.IntervalXYDataset;
import org.jfree.data.xy.XYDataset;
/** 
 * A renderer that draws bars on an                               {@link XYPlot} (requires an{@link IntervalXYDataset}).  The example shown here is generated by the <code>XYBarChartDemo1.java</code> program included in the JFreeChart demo collection: <br><br> <img src="../../../../../images/XYBarRendererSample.png" alt="XYBarRendererSample.png" />
 */
public class XYBarRenderer extends AbstractXYItemRenderer implements XYItemRenderer, Cloneable, PublicCloneable, Serializable {
  /** 
 * For serialization. 
 */
  private static final long serialVersionUID=770559577251370036L;
  /** 
 * The default bar painter assigned to each new instance of this renderer.
 * @since 1.0.11
 */
  private static XYBarPainter defaultBarPainter=new GradientXYBarPainter();
  /** 
 * Returns the default bar painter.
 * @return The default bar painter.
 * @since 1.0.11
 */
  public static XYBarPainter getDefaultBarPainter(){
    return XYBarRenderer.defaultBarPainter;
  }
  /** 
 * Sets the default bar painter.
 * @param painter  the painter (<code>null</code> not permitted).
 * @since 1.0.11
 */
  public static void setDefaultBarPainter(  XYBarPainter painter){
    if (painter == null) {
      throw new IllegalArgumentException("Null 'painter' argument.");
    }
    XYBarRenderer.defaultBarPainter=painter;
  }
  /** 
 * The default value for the initialisation of the shadowsVisible flag.
 */
  private static boolean defaultShadowsVisible=true;
  /** 
 * Returns the default value for the <code>shadowsVisible</code> flag.
 * @return A boolean.
 * @see #setDefaultShadowsVisible(boolean)
 * @since 1.0.13
 */
  public static boolean getDefaultShadowsVisible(){
    return XYBarRenderer.defaultShadowsVisible;
  }
  /** 
 * Sets the default value for the shadows visible flag.
 * @param visible  the new value for the default.
 * @see #getDefaultShadowsVisible()
 * @since 1.0.13
 */
  public static void setDefaultShadowsVisible(  boolean visible){
    XYBarRenderer.defaultShadowsVisible=visible;
  }
  /** 
 * The state class used by this renderer.
 */
protected class XYBarRendererState extends XYItemRendererState {
    /** 
 * Base for bars against the range axis, in Java 2D space. 
 */
    private double g2Base;
    /** 
 * Creates a new state object.
 * @param info  the plot rendering info.
 */
    public XYBarRendererState(    PlotRenderingInfo info){
      super(info);
    }
    /** 
 * Returns the base (range) value in Java 2D space.
 * @return The base value.
 */
    public double getG2Base(){
      return this.g2Base;
    }
    /** 
 * Sets the range axis base in Java2D space.
 * @param value  the value.
 */
    public void setG2Base(    double value){
      this.g2Base=value;
    }
  }
  /** 
 * The default base value for the bars. 
 */
  private double base;
  /** 
 * A flag that controls whether the bars use the y-interval supplied by the dataset.
 */
  private boolean useYInterval;
  /** 
 * Percentage margin (to reduce the width of bars). 
 */
  private double margin;
  /** 
 * A flag that controls whether or not bar outlines are drawn. 
 */
  private boolean drawBarOutline;
  /** 
 * An optional class used to transform gradient paint objects to fit each bar.
 */
  private GradientPaintTransformer gradientPaintTransformer;
  /** 
 * The shape used to represent a bar in each legend item (this should never be <code>null</code>).
 */
  private transient Shape legendBar;
  /** 
 * The fallback position if a positive item label doesn't fit inside the bar.
 */
  private ItemLabelPosition positiveItemLabelPositionFallback;
  /** 
 * The fallback position if a negative item label doesn't fit inside the bar.
 */
  private ItemLabelPosition negativeItemLabelPositionFallback;
  /** 
 * The bar painter (never <code>null</code>).
 * @since 1.0.11
 */
  private XYBarPainter barPainter;
  /** 
 * The flag that controls whether or not shadows are drawn for the bars.
 * @since 1.0.11
 */
  private boolean shadowsVisible;
  /** 
 * The x-offset for the shadow effect.
 * @since 1.0.11
 */
  private double shadowXOffset;
  /** 
 * The y-offset for the shadow effect.
 * @since 1.0.11
 */
  private double shadowYOffset;
  /** 
 * A factor used to align the bars about the x-value.
 * @since 1.0.13
 */
  private double barAlignmentFactor;
  /** 
 * The default constructor.
 */
  public XYBarRenderer(){
    this(0.0);
  }
  /** 
 * Constructs a new renderer.
 * @param margin  the percentage amount to trim from the width of each bar.
 */
  public XYBarRenderer(  double margin){
    super();
    this.margin=margin;
    this.base=0.0;
    this.useYInterval=false;
    this.gradientPaintTransformer=new StandardGradientPaintTransformer();
    this.drawBarOutline=false;
    this.legendBar=new Rectangle2D.Double(-3.0,-5.0,6.0,10.0);
    this.barPainter=getDefaultBarPainter();
    this.shadowsVisible=getDefaultShadowsVisible();
    this.shadowXOffset=4.0;
    this.shadowYOffset=4.0;
    this.barAlignmentFactor=-1.0;
  }
  /** 
 * Returns the base value for the bars.
 * @return The base value for the bars.
 * @see #setBase(double)
 */
  public double getBase(){
    return this.base;
  }
  /** 
 * Sets the base value for the bars and sends a                               {@link RendererChangeEvent}to all registered listeners.  The base value is not used if the dataset's y-interval is being used to determine the bar length.
 * @param base  the new base value.
 * @see #getBase()
 * @see #getUseYInterval()
 */
  public void setBase(  double base){
    this.base=base;
    fireChangeEvent();
  }
  /** 
 * Returns a flag that determines whether the y-interval from the dataset is used to calculate the length of each bar.
 * @return A boolean.
 * @see #setUseYInterval(boolean)
 */
  public boolean getUseYInterval(){
    return this.useYInterval;
  }
  /** 
 * Sets the flag that determines whether the y-interval from the dataset is used to calculate the length of each bar, and sends a                              {@link RendererChangeEvent} to all registered listeners.
 * @param use  the flag.
 * @see #getUseYInterval()
 */
  public void setUseYInterval(  boolean use){
    if (this.useYInterval != use) {
      this.useYInterval=use;
      fireChangeEvent();
    }
  }
  /** 
 * Returns the margin which is a percentage amount by which the bars are trimmed.
 * @return The margin.
 * @see #setMargin(double)
 */
  public double getMargin(){
    return this.margin;
  }
  /** 
 * Sets the percentage amount by which the bars are trimmed and sends a                              {@link RendererChangeEvent} to all registered listeners.
 * @param margin  the new margin.
 * @see #getMargin()
 */
  public void setMargin(  double margin){
    this.margin=margin;
    fireChangeEvent();
  }
  /** 
 * Returns a flag that controls whether or not bar outlines are drawn.
 * @return A boolean.
 * @see #setDrawBarOutline(boolean)
 */
  public boolean isDrawBarOutline(){
    return this.drawBarOutline;
  }
  /** 
 * Sets the flag that controls whether or not bar outlines are drawn and sends a                               {@link RendererChangeEvent} to all registered listeners.
 * @param draw  the flag.
 * @see #isDrawBarOutline()
 */
  public void setDrawBarOutline(  boolean draw){
    this.drawBarOutline=draw;
    fireChangeEvent();
  }
  /** 
 * Returns the gradient paint transformer (an object used to transform gradient paint objects to fit each bar).
 * @return A transformer (<code>null</code> possible).
 * @see #setGradientPaintTransformer(GradientPaintTransformer)
 */
  public GradientPaintTransformer getGradientPaintTransformer(){
    return this.gradientPaintTransformer;
  }
  /** 
 * Sets the gradient paint transformer and sends a                              {@link RendererChangeEvent} to all registered listeners.
 * @param transformer  the transformer (<code>null</code> permitted).
 * @see #getGradientPaintTransformer()
 */
  public void setGradientPaintTransformer(  GradientPaintTransformer transformer){
    this.gradientPaintTransformer=transformer;
    fireChangeEvent();
  }
  /** 
 * Returns the shape used to represent bars in each legend item.
 * @return The shape used to represent bars in each legend item (never<code>null</code>).
 * @see #setLegendBar(Shape)
 */
  public Shape getLegendBar(){
    return this.legendBar;
  }
  /** 
 * Sets the shape used to represent bars in each legend item and sends a                              {@link RendererChangeEvent} to all registered listeners.
 * @param bar  the bar shape (<code>null</code> not permitted).
 * @see #getLegendBar()
 */
  public void setLegendBar(  Shape bar){
    if (bar == null) {
      throw new IllegalArgumentException("Null 'bar' argument.");
    }
    this.legendBar=bar;
    fireChangeEvent();
  }
  /** 
 * Returns the fallback position for positive item labels that don't fit within a bar.
 * @return The fallback position (<code>null</code> possible).
 * @see #setPositiveItemLabelPositionFallback(ItemLabelPosition)
 * @since 1.0.2
 */
  public ItemLabelPosition getPositiveItemLabelPositionFallback(){
    return this.positiveItemLabelPositionFallback;
  }
  /** 
 * Sets the fallback position for positive item labels that don't fit within a bar, and sends a                               {@link RendererChangeEvent} to all registeredlisteners.
 * @param position  the position (<code>null</code> permitted).
 * @see #getPositiveItemLabelPositionFallback()
 * @since 1.0.2
 */
  public void setPositiveItemLabelPositionFallback(  ItemLabelPosition position){
    this.positiveItemLabelPositionFallback=position;
    fireChangeEvent();
  }
  /** 
 * Returns the fallback position for negative item labels that don't fit within a bar.
 * @return The fallback position (<code>null</code> possible).
 * @see #setNegativeItemLabelPositionFallback(ItemLabelPosition)
 * @since 1.0.2
 */
  public ItemLabelPosition getNegativeItemLabelPositionFallback(){
    return this.negativeItemLabelPositionFallback;
  }
  /** 
 * Sets the fallback position for negative item labels that don't fit within a bar, and sends a                               {@link RendererChangeEvent} to all registeredlisteners.
 * @param position  the position (<code>null</code> permitted).
 * @see #getNegativeItemLabelPositionFallback()
 * @since 1.0.2
 */
  public void setNegativeItemLabelPositionFallback(  ItemLabelPosition position){
    this.negativeItemLabelPositionFallback=position;
    fireChangeEvent();
  }
  /** 
 * Returns the bar painter.
 * @return The bar painter (never <code>null</code>).
 * @since 1.0.11
 */
  public XYBarPainter getBarPainter(){
    return this.barPainter;
  }
  /** 
 * Sets the bar painter and sends a                               {@link RendererChangeEvent} to allregistered listeners.
 * @param painter  the painter (<code>null</code> not permitted).
 * @since 1.0.11
 */
  public void setBarPainter(  XYBarPainter painter){
    if (painter == null) {
      throw new IllegalArgumentException("Null 'painter' argument.");
    }
    this.barPainter=painter;
    fireChangeEvent();
  }
  /** 
 * Returns the flag that controls whether or not shadows are drawn for the bars.
 * @return A boolean.
 * @since 1.0.11
 */
  public boolean getShadowsVisible(){
    return this.shadowsVisible;
  }
  /** 
 * Sets the flag that controls whether or not the renderer draws shadows for the bars, and sends a                              {@link RendererChangeEvent} to all registered listeners.
 * @param visible  the new flag value.
 * @since 1.0.11
 */
  public void setShadowVisible(  boolean visible){
    this.shadowsVisible=visible;
    fireChangeEvent();
  }
  /** 
 * Returns the shadow x-offset.
 * @return The shadow x-offset.
 * @since 1.0.11
 */
  public double getShadowXOffset(){
    return this.shadowXOffset;
  }
  /** 
 * Sets the x-offset for the bar shadow and sends a                              {@link RendererChangeEvent} to all registered listeners.
 * @param offset  the offset.
 * @since 1.0.11
 */
  public void setShadowXOffset(  double offset){
    this.shadowXOffset=offset;
    fireChangeEvent();
  }
  /** 
 * Returns the shadow y-offset.
 * @return The shadow y-offset.
 * @since 1.0.11
 */
  public double getShadowYOffset(){
    return this.shadowYOffset;
  }
  /** 
 * Sets the y-offset for the bar shadow and sends a                              {@link RendererChangeEvent} to all registered listeners.
 * @param offset  the offset.
 * @since 1.0.11
 */
  public void setShadowYOffset(  double offset){
    this.shadowYOffset=offset;
    fireChangeEvent();
  }
  /** 
 * Returns the bar alignment factor.
 * @return The bar alignment factor.
 * @since 1.0.13
 */
  public double getBarAlignmentFactor(){
    return this.barAlignmentFactor;
  }
  /** 
 * Sets the bar alignment factor and sends a                               {@link RendererChangeEvent}to all registered listeners.  If the alignment factor is outside the range 0.0 to 1.0, no alignment will be performed by the renderer.
 * @param factor  the factor.
 * @since 1.0.13
 */
  public void setBarAlignmentFactor(  double factor){
    this.barAlignmentFactor=factor;
    fireChangeEvent();
  }
  /** 
 * Creates the renderer state.  This is called by the                               {@link #initialise()}method.
 * @param info  the plot rendering info.
 * @return A new state instance.
 * @since 1.2.0
 */
  protected XYItemRendererState createState(  PlotRenderingInfo info){
    return new XYBarRendererState(info);
  }
  /** 
 * Initialises the renderer and returns a state object that should be passed to all subsequent calls to the drawItem() method.  Here we calculate the Java2D y-coordinate for zero, since all the bars have their bases fixed at zero.
 * @param g2  the graphics device.
 * @param dataArea  the area inside the axes.
 * @param plot  the plot.
 * @param dataset  the data.
 * @param info  an optional info collection object to return data back tothe caller.
 * @return A state object.
 */
  public XYItemRendererState initialise(  Graphics2D g2,  Rectangle2D dataArea,  XYPlot plot,  XYDataset dataset,  PlotRenderingInfo info){
    XYBarRendererState state=(XYBarRendererState)super.initialise(g2,dataArea,plot,dataset,info);
    ValueAxis rangeAxis=plot.getRangeAxisForDataset(plot.indexOf(dataset));
    state.setG2Base(rangeAxis.valueToJava2D(this.base,dataArea,plot.getRangeAxisEdge()));
    return state;
  }
  /** 
 * Returns a default legend item for the specified series.  Subclasses should override this method to generate customised items.
 * @param datasetIndex  the dataset index (zero-based).
 * @param series  the series index (zero-based).
 * @return A legend item for the series.
 */
  public LegendItem getLegendItem(  int datasetIndex,  int series){
    LegendItem result=null;
    XYPlot xyplot=getPlot();
    if (xyplot != null) {
      XYDataset dataset=xyplot.getDataset(datasetIndex);
      if (dataset != null) {
        XYSeriesLabelGenerator lg=getLegendItemLabelGenerator();
        String label=lg.generateLabel(dataset,series);
        String description=label;
        String toolTipText=null;
        if (getLegendItemToolTipGenerator() != null) {
          toolTipText=getLegendItemToolTipGenerator().generateLabel(dataset,series);
        }
        String urlText=null;
        if (getLegendItemURLGenerator() != null) {
          urlText=getLegendItemURLGenerator().generateLabel(dataset,series);
        }
        Shape shape=this.legendBar;
        Paint paint=lookupSeriesPaint(series);
        Paint outlinePaint=lookupSeriesOutlinePaint(series);
        Stroke outlineStroke=lookupSeriesOutlineStroke(series);
        if (this.drawBarOutline) {
          result=new LegendItem(label,description,toolTipText,urlText,shape,paint,outlineStroke,outlinePaint);
        }
 else {
          result=new LegendItem(label,description,toolTipText,urlText,shape,paint);
        }
        result.setLabelFont(lookupLegendTextFont(series));
        Paint labelPaint=lookupLegendTextPaint(series);
        if (labelPaint != null) {
          result.setLabelPaint(labelPaint);
        }
        result.setDataset(dataset);
        result.setDatasetIndex(datasetIndex);
        result.setSeriesKey(dataset.getSeriesKey(series));
        result.setSeriesIndex(series);
        if (getGradientPaintTransformer() != null) {
          result.setFillPaintTransformer(getGradientPaintTransformer());
        }
      }
    }
    return result;
  }
  /** 
 * Creates a rectangle representing the bar for a data item
 */
  protected Rectangle2D createBar(  Graphics2D g2,  Rectangle2D dataArea,  XYPlot plot,  ValueAxis domainAxis,  ValueAxis rangeAxis,  XYDataset dataset,  int series,  int item,  boolean selected){
    if (!getItemVisible(series,item)) {
      return null;
    }
    IntervalXYDataset ixyd=(IntervalXYDataset)dataset;
    double value0;
    double value1;
    if (this.useYInterval) {
      value0=ixyd.getStartYValue(series,item);
      value1=ixyd.getEndYValue(series,item);
    }
 else {
      value0=this.base;
      value1=ixyd.getYValue(series,item);
    }
    if (Double.isNaN(value0) || Double.isNaN(value1)) {
      return null;
    }
    if (value0 <= value1) {
      if (!rangeAxis.getRange().intersects(value0,value1)) {
        return null;
      }
    }
 else {
      if (!rangeAxis.getRange().intersects(value1,value0)) {
        return null;
      }
    }
    double translatedValue0=rangeAxis.valueToJava2D(value0,dataArea,plot.getRangeAxisEdge());
    double translatedValue1=rangeAxis.valueToJava2D(value1,dataArea,plot.getRangeAxisEdge());
    double bottom=Math.min(translatedValue0,translatedValue1);
    double top=Math.max(translatedValue0,translatedValue1);
    double startX=ixyd.getStartXValue(series,item);
    if (Double.isNaN(startX)) {
      return null;
    }
    double endX=ixyd.getEndXValue(series,item);
    if (Double.isNaN(endX)) {
      return null;
    }
    if (startX <= endX) {
      if (!domainAxis.getRange().intersects(startX,endX)) {
        return null;
      }
    }
 else {
      if (!domainAxis.getRange().intersects(endX,startX)) {
        return null;
      }
    }
    if (this.barAlignmentFactor >= 0.0 && this.barAlignmentFactor <= 1.0) {
      double x=ixyd.getXValue(series,item);
      double interval=endX - startX;
      startX=x - interval * this.barAlignmentFactor;
      endX=startX + interval;
    }
    RectangleEdge location=plot.getDomainAxisEdge();
    double translatedStartX=domainAxis.valueToJava2D(startX,dataArea,location);
    double translatedEndX=domainAxis.valueToJava2D(endX,dataArea,location);
    double translatedWidth=Math.max(1,Math.abs(translatedEndX - translatedStartX));
    double left=Math.min(translatedStartX,translatedEndX);
    if (getMargin() > 0.0) {
      double cut=translatedWidth * getMargin();
      translatedWidth=translatedWidth - cut;
      left=left + cut / 2;
    }
    Rectangle2D bar=null;
    PlotOrientation orientation=plot.getOrientation();
    if (orientation == PlotOrientation.HORIZONTAL) {
      bottom=Math.max(bottom,dataArea.getMinX());
      top=Math.min(top,dataArea.getMaxX());
      bar=new Rectangle2D.Double(bottom,left,top - bottom,translatedWidth);
    }
 else {
      if (orientation == PlotOrientation.VERTICAL) {
        bottom=Math.max(bottom,dataArea.getMinY());
        top=Math.min(top,dataArea.getMaxY());
        bar=new Rectangle2D.Double(left,bottom,translatedWidth,top - bottom);
      }
    }
    return bar;
  }
  /** 
 * Draws the visual representation of a single data item.
 * @param g2  the graphics device.
 * @param state  the renderer state.
 * @param dataArea  the area within which the plot is being drawn.
 * @param plot  the plot (can be used to obtain standard colorinformation etc).
 * @param domainAxis  the domain axis.
 * @param rangeAxis  the range axis.
 * @param dataset  the dataset.
 * @param series  the series index (zero-based).
 * @param item  the item index (zero-based).
 * @param pass  the pass index.
 */
  public void drawItem(  Graphics2D g2,  XYItemRendererState state,  Rectangle2D dataArea,  XYPlot plot,  ValueAxis domainAxis,  ValueAxis rangeAxis,  XYDataset dataset,  int series,  int item,  boolean selected,  int pass){
    Rectangle2D bar=createBar(g2,dataArea,plot,domainAxis,rangeAxis,dataset,series,item,selected);
    if (bar == null) {
      return;
    }
    boolean positive=true;
    if (this.useYInterval) {
      positive=dataset.getYValue(series,item) >= 0.0;
    }
 else {
      positive=dataset.getYValue(series,item) >= 0.0;
    }
    boolean inverted=rangeAxis.isInverted();
    RectangleEdge barBase;
    if (plot.getOrientation() == PlotOrientation.HORIZONTAL) {
      if (positive && inverted || !positive && !inverted) {
        barBase=RectangleEdge.RIGHT;
      }
 else {
        barBase=RectangleEdge.LEFT;
      }
    }
 else {
      if (positive && !inverted || !positive && inverted) {
        barBase=RectangleEdge.BOTTOM;
      }
 else {
        barBase=RectangleEdge.TOP;
      }
    }
    if (getShadowsVisible()) {
      this.barPainter.paintBarShadow(g2,this,series,item,selected,bar,barBase,!this.useYInterval);
    }
    this.barPainter.paintBar(g2,this,series,item,selected,bar,barBase);
    if (isItemLabelVisible(series,item,selected)) {
      XYItemLabelGenerator generator=getItemLabelGenerator(series,item,selected);
      drawItemLabelForBar(g2,plot,dataset,series,item,selected,generator,bar,!positive);
    }
    double x1=dataset.getXValue(series,item);
    double y1=dataset.getYValue(series,item);
    double transX1=domainAxis.valueToJava2D(x1,dataArea,plot.getDomainAxisEdge());
    double transY1=rangeAxis.valueToJava2D(y1,dataArea,plot.getRangeAxisEdge());
    int domainAxisIndex=plot.getDomainAxisIndex(domainAxis);
    int rangeAxisIndex=plot.getRangeAxisIndex(rangeAxis);
    XYCrosshairState crosshairState=state.getCrosshairState();
    updateCrosshairValues(crosshairState,x1,y1,domainAxisIndex,rangeAxisIndex,transX1,transY1,plot.getOrientation());
    EntityCollection entities=state.getEntityCollection();
    if (entities != null) {
      addEntity(entities,bar,dataset,series,item,selected,0.0,0.0);
    }
  }
  /** 
 * Draws an item label.  This method is provided as an alternative to                              {@link #drawItemLabel(Graphics2D,PlotOrientation,XYDataset,int,int,double,double,boolean)} so that the bar can be used to calculate thelabel anchor point.
 * @param g2  the graphics device.
 * @param dataset  the dataset.
 * @param series  the series index.
 * @param item  the item index.
 * @param selected  is the data item selected?
 * @param plot  the plot.
 * @param generator  the label generator (<code>null</code> permitted, inwhich case the method does nothing, just returns).
 * @param bar  the bar.
 * @param negative  a flag indicating a negative value.
 * @since 1.2.0
 */
  protected void drawItemLabelForBar(  Graphics2D g2,  XYPlot plot,  XYDataset dataset,  int series,  int item,  boolean selected,  XYItemLabelGenerator generator,  Rectangle2D bar,  boolean negative){
    if (generator == null) {
      return;
    }
    String label=generator.generateLabel(dataset,series,item);
    if (label == null) {
      return;
    }
    Font labelFont=getItemLabelFont(series,item,selected);
    g2.setFont(labelFont);
    Paint paint=getItemLabelPaint(series,item,selected);
    g2.setPaint(paint);
    ItemLabelPosition position=null;
    if (!negative) {
      position=getPositiveItemLabelPosition(series,item,selected);
    }
 else {
      position=getNegativeItemLabelPosition(series,item,selected);
    }
    Point2D anchorPoint=calculateLabelAnchorPoint(position.getItemLabelAnchor(),bar,plot.getOrientation());
    if (isInternalAnchor(position.getItemLabelAnchor())) {
      Shape bounds=TextUtilities.calculateRotatedStringBounds(label,g2,(float)anchorPoint.getX(),(float)anchorPoint.getY(),position.getTextAnchor(),position.getAngle(),position.getRotationAnchor());
      if (bounds != null) {
        if (!bar.contains(bounds.getBounds2D())) {
          if (!negative) {
            position=getPositiveItemLabelPositionFallback();
          }
 else {
            position=getNegativeItemLabelPositionFallback();
          }
          if (position != null) {
            anchorPoint=calculateLabelAnchorPoint(position.getItemLabelAnchor(),bar,plot.getOrientation());
          }
        }
      }
    }
    if (position != null) {
      TextUtilities.drawRotatedString(label,g2,(float)anchorPoint.getX(),(float)anchorPoint.getY(),position.getTextAnchor(),position.getAngle(),position.getRotationAnchor());
    }
  }
  /** 
 * Calculates the item label anchor point.
 * @param anchor  the anchor.
 * @param bar  the bar.
 * @param orientation  the plot orientation.
 * @return The anchor point.
 */
  private Point2D calculateLabelAnchorPoint(  ItemLabelAnchor anchor,  Rectangle2D bar,  PlotOrientation orientation){
    Point2D result=null;
    double offset=getItemLabelAnchorOffset();
    double x0=bar.getX() - offset;
    double x1=bar.getX();
    double x2=bar.getX() + offset;
    double x3=bar.getCenterX();
    double x4=bar.getMaxX() - offset;
    double x5=bar.getMaxX();
    double x6=bar.getMaxX() + offset;
    double y0=bar.getMaxY() + offset;
    double y1=bar.getMaxY();
    double y2=bar.getMaxY() - offset;
    double y3=bar.getCenterY();
    double y4=bar.getMinY() + offset;
    double y5=bar.getMinY();
    double y6=bar.getMinY() - offset;
    if (anchor == ItemLabelAnchor.CENTER) {
      result=new Point2D.Double(x3,y3);
    }
 else {
      if (anchor == ItemLabelAnchor.INSIDE1) {
        result=new Point2D.Double(x4,y4);
      }
 else {
        if (anchor == ItemLabelAnchor.INSIDE2) {
          result=new Point2D.Double(x4,y4);
        }
 else {
          if (anchor == ItemLabelAnchor.INSIDE3) {
            result=new Point2D.Double(x4,y3);
          }
 else {
            if (anchor == ItemLabelAnchor.INSIDE4) {
              result=new Point2D.Double(x4,y2);
            }
 else {
              if (anchor == ItemLabelAnchor.INSIDE5) {
                result=new Point2D.Double(x4,y2);
              }
 else {
                if (anchor == ItemLabelAnchor.INSIDE6) {
                  result=new Point2D.Double(x3,y2);
                }
 else {
                  if (anchor == ItemLabelAnchor.INSIDE7) {
                    result=new Point2D.Double(x2,y2);
                  }
 else {
                    if (anchor == ItemLabelAnchor.INSIDE8) {
                      result=new Point2D.Double(x2,y2);
                    }
 else {
                      if (anchor == ItemLabelAnchor.INSIDE9) {
                        result=new Point2D.Double(x2,y3);
                      }
 else {
                        if (anchor == ItemLabelAnchor.INSIDE10) {
                          result=new Point2D.Double(x2,y4);
                        }
 else {
                          if (anchor == ItemLabelAnchor.INSIDE11) {
                            result=new Point2D.Double(x2,y4);
                          }
 else {
                            if (anchor == ItemLabelAnchor.INSIDE12) {
                              result=new Point2D.Double(x3,y4);
                            }
 else {
                              if (anchor == ItemLabelAnchor.OUTSIDE1) {
                                result=new Point2D.Double(x5,y6);
                              }
 else {
                                if (anchor == ItemLabelAnchor.OUTSIDE2) {
                                  result=new Point2D.Double(x6,y5);
                                }
 else {
                                  if (anchor == ItemLabelAnchor.OUTSIDE3) {
                                    result=new Point2D.Double(x6,y3);
                                  }
 else {
                                    if (anchor == ItemLabelAnchor.OUTSIDE4) {
                                      result=new Point2D.Double(x6,y1);
                                    }
 else {
                                      if (anchor == ItemLabelAnchor.OUTSIDE5) {
                                        result=new Point2D.Double(x5,y0);
                                      }
 else {
                                        if (anchor == ItemLabelAnchor.OUTSIDE6) {
                                          result=new Point2D.Double(x3,y0);
                                        }
 else {
                                          if (anchor == ItemLabelAnchor.OUTSIDE7) {
                                            result=new Point2D.Double(x1,y0);
                                          }
 else {
                                            if (anchor == ItemLabelAnchor.OUTSIDE8) {
                                              result=new Point2D.Double(x0,y1);
                                            }
 else {
                                              if (anchor == ItemLabelAnchor.OUTSIDE9) {
                                                result=new Point2D.Double(x0,y3);
                                              }
 else {
                                                if (anchor == ItemLabelAnchor.OUTSIDE10) {
                                                  result=new Point2D.Double(x0,y5);
                                                }
 else {
                                                  if (anchor == ItemLabelAnchor.OUTSIDE11) {
                                                    result=new Point2D.Double(x1,y6);
                                                  }
 else {
                                                    if (anchor == ItemLabelAnchor.OUTSIDE12) {
                                                      result=new Point2D.Double(x3,y6);
                                                    }
                                                  }
                                                }
                                              }
                                            }
                                          }
                                        }
                                      }
                                    }
                                  }
                                }
                              }
                            }
                          }
                        }
                      }
                    }
                  }
                }
              }
            }
          }
        }
      }
    }
    return result;
  }
  /** 
 * Returns <code>true</code> if the specified anchor point is inside a bar.
 * @param anchor  the anchor point.
 * @return A boolean.
 */
  private boolean isInternalAnchor(  ItemLabelAnchor anchor){
    return anchor == ItemLabelAnchor.CENTER || anchor == ItemLabelAnchor.INSIDE1 || anchor == ItemLabelAnchor.INSIDE2 || anchor == ItemLabelAnchor.INSIDE3 || anchor == ItemLabelAnchor.INSIDE4 || anchor == ItemLabelAnchor.INSIDE5 || anchor == ItemLabelAnchor.INSIDE6 || anchor == ItemLabelAnchor.INSIDE7 || anchor == ItemLabelAnchor.INSIDE8 || anchor == ItemLabelAnchor.INSIDE9 || anchor == ItemLabelAnchor.INSIDE10 || anchor == ItemLabelAnchor.INSIDE11 || anchor == ItemLabelAnchor.INSIDE12;
  }
  public Rectangle2D createHotSpotBounds(  Graphics2D g2,  Rectangle2D dataArea,  XYPlot plot,  ValueAxis domainAxis,  ValueAxis rangeAxis,  XYDataset dataset,  int series,  int item,  boolean selected,  Rectangle2D result){
    return createBar(g2,dataArea,plot,domainAxis,rangeAxis,dataset,series,item,selected);
  }
  /** 
 * Returns the lower and upper bounds (range) of the x-values in the specified dataset.  Since this renderer uses the x-interval in the dataset, this is taken into account for the range.
 * @param dataset  the dataset (<code>null</code> permitted).
 * @return The range (<code>null</code> if the dataset is<code>null</code> or empty).
 */
  public Range findDomainBounds(  XYDataset dataset){
    if (dataset != null) {
      return DatasetUtilities.findDomainBounds(dataset,true);
    }
 else {
      return null;
    }
  }
  /** 
 * Returns the lower and upper bounds (range) of the y-values in the specified dataset.  If the renderer is plotting the y-interval from the dataset, this is taken into account for the range.
 * @param dataset  the dataset (<code>null</code> permitted).
 * @return The range (<code>null</code> if the dataset is<code>null</code> or empty).
 */
  public Range findRangeBounds(  XYDataset dataset){
    if (dataset != null) {
      return DatasetUtilities.findRangeBounds(dataset,this.useYInterval);
    }
 else {
      return null;
    }
  }
  /** 
 * Returns a clone of the renderer.
 * @return A clone.
 * @throws CloneNotSupportedException  if the renderer cannot be cloned.
 */
  public Object clone() throws CloneNotSupportedException {
    XYBarRenderer result=(XYBarRenderer)super.clone();
    if (this.gradientPaintTransformer != null) {
      result.gradientPaintTransformer=(GradientPaintTransformer)ObjectUtilities.clone(this.gradientPaintTransformer);
    }
    result.legendBar=ShapeUtilities.clone(this.legendBar);
    return result;
  }
  /** 
 * Tests this renderer for equality with an arbitrary object.
 * @param obj  the object to test against (<code>null</code> permitted).
 * @return A boolean.
 */
  public boolean equals(  Object obj){
    if (obj == this) {
      return true;
    }
    if (!(obj instanceof XYBarRenderer)) {
      return false;
    }
    XYBarRenderer that=(XYBarRenderer)obj;
    if (this.base != that.base) {
      return false;
    }
    if (this.drawBarOutline != that.drawBarOutline) {
      return false;
    }
    if (this.margin != that.margin) {
      return false;
    }
    if (this.useYInterval != that.useYInterval) {
      return false;
    }
    if (!ObjectUtilities.equal(this.gradientPaintTransformer,that.gradientPaintTransformer)) {
      return false;
    }
    if (!ShapeUtilities.equal(this.legendBar,that.legendBar)) {
      return false;
    }
    if (!ObjectUtilities.equal(this.positiveItemLabelPositionFallback,that.positiveItemLabelPositionFallback)) {
      return false;
    }
    if (!ObjectUtilities.equal(this.negativeItemLabelPositionFallback,that.negativeItemLabelPositionFallback)) {
      return false;
    }
    if (!this.barPainter.equals(that.barPainter)) {
      return false;
    }
    if (this.shadowsVisible != that.shadowsVisible) {
      return false;
    }
    if (this.shadowXOffset != that.shadowXOffset) {
      return false;
    }
    if (this.shadowYOffset != that.shadowYOffset) {
      return false;
    }
    if (this.barAlignmentFactor != that.barAlignmentFactor) {
      return false;
    }
    return super.equals(obj);
  }
  /** 
 * Provides serialization support.
 * @param stream  the input stream.
 * @throws IOException  if there is an I/O error.
 * @throws ClassNotFoundException  if there is a classpath problem.
 */
  private void readObject(  ObjectInputStream stream) throws IOException, ClassNotFoundException {
    stream.defaultReadObject();
    this.legendBar=SerialUtilities.readShape(stream);
  }
  /** 
 * Provides serialization support.
 * @param stream  the output stream.
 * @throws IOException  if there is an I/O error.
 */
  private void writeObject(  ObjectOutputStream stream) throws IOException {
    stream.defaultWriteObject();
    SerialUtilities.writeShape(this.legendBar,stream);
  }
}
