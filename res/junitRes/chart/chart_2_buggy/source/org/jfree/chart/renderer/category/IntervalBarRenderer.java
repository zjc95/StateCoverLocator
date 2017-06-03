package org.jfree.chart.renderer.category;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.entity.EntityCollection;
import org.jfree.chart.labels.CategoryItemLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.util.RectangleEdge;
import org.jfree.data.Range;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.IntervalCategoryDataset;
/** 
 * A renderer that handles the drawing of bars for a bar plot where each bar has a high and low value.  This renderer is for use with the                                                                                                                                                                    {@link CategoryPlot} class.  The example shown here is generated by the<code>IntervalBarChartDemo1.java</code> program included in the JFreeChart Demo Collection: <br><br> <img src="../../../../../images/IntervalBarRendererSample.png" alt="IntervalBarRendererSample.png" />
 */
public class IntervalBarRenderer extends BarRenderer {
  /** 
 * For serialization. 
 */
  private static final long serialVersionUID=-5068857361615528725L;
  /** 
 * Constructs a new renderer.
 */
  public IntervalBarRenderer(){
    super();
  }
  /** 
 * Returns the range of values from the specified dataset.  For this renderer, this is equivalent to calling <code>findRangeBounds(dataset, true)</code>.
 * @param dataset  the dataset (<code>null</code> permitted).
 * @return The range (or <code>null</code> if the dataset is<code>null</code> or empty).
 */
  public Range findRangeBounds(  CategoryDataset dataset){
    return findRangeBounds(dataset,true);
  }
  /** 
 * Draws the bar for a single (series, category) data item.
 * @param g2  the graphics device.
 * @param state  the renderer state.
 * @param dataArea  the data area.
 * @param plot  the plot.
 * @param domainAxis  the domain axis.
 * @param rangeAxis  the range axis.
 * @param dataset  the dataset.
 * @param row  the row index (zero-based).
 * @param column  the column index (zero-based).
 * @param pass  the pass index.
 */
  public void drawItem(  Graphics2D g2,  CategoryItemRendererState state,  Rectangle2D dataArea,  CategoryPlot plot,  CategoryAxis domainAxis,  ValueAxis rangeAxis,  CategoryDataset dataset,  int row,  int column,  boolean selected,  int pass){
    if (dataset instanceof IntervalCategoryDataset) {
      IntervalCategoryDataset d=(IntervalCategoryDataset)dataset;
      drawInterval(g2,state,dataArea,plot,domainAxis,rangeAxis,d,row,column,selected);
    }
 else {
      super.drawItem(g2,state,dataArea,plot,domainAxis,rangeAxis,dataset,row,column,selected,pass);
    }
  }
  /** 
 * Draws a single interval.
 * @param g2  the graphics device.
 * @param state  the renderer state.
 * @param dataArea  the data plot area.
 * @param plot  the plot.
 * @param domainAxis  the domain axis.
 * @param rangeAxis  the range axis.
 * @param dataset  the data.
 * @param row  the row index (zero-based).
 * @param column  the column index (zero-based).
 * @param selected  is the item selected?
 * @since 1.2.0
 */
  protected void drawInterval(  Graphics2D g2,  CategoryItemRendererState state,  Rectangle2D dataArea,  CategoryPlot plot,  CategoryAxis domainAxis,  ValueAxis rangeAxis,  IntervalCategoryDataset dataset,  int row,  int column,  boolean selected){
    int visibleRow=state.getVisibleSeriesIndex(row);
    if (visibleRow < 0) {
      return;
    }
    int seriesCount=state.getVisibleSeriesCount() >= 0 ? state.getVisibleSeriesCount() : getRowCount();
    int categoryCount=getColumnCount();
    PlotOrientation orientation=plot.getOrientation();
    double rectX=0.0;
    double rectY=0.0;
    RectangleEdge domainAxisLocation=plot.getDomainAxisEdge();
    RectangleEdge rangeAxisLocation=plot.getRangeAxisEdge();
    Number value0=dataset.getEndValue(row,column);
    if (value0 == null) {
      return;
    }
    double java2dValue0=rangeAxis.valueToJava2D(value0.doubleValue(),dataArea,rangeAxisLocation);
    Number value1=dataset.getStartValue(row,column);
    if (value1 == null) {
      return;
    }
    double java2dValue1=rangeAxis.valueToJava2D(value1.doubleValue(),dataArea,rangeAxisLocation);
    if (java2dValue1 < java2dValue0) {
      double temp=java2dValue1;
      java2dValue1=java2dValue0;
      java2dValue0=temp;
    }
    double rectWidth=state.getBarWidth();
    double rectHeight=Math.abs(java2dValue1 - java2dValue0);
    RectangleEdge barBase=RectangleEdge.LEFT;
    if (orientation == PlotOrientation.HORIZONTAL) {
      rectY=domainAxis.getCategoryStart(column,getColumnCount(),dataArea,domainAxisLocation);
      if (seriesCount > 1) {
        double seriesGap=dataArea.getHeight() * getItemMargin() / (categoryCount * (seriesCount - 1));
        rectY=rectY + visibleRow * (state.getBarWidth() + seriesGap);
      }
 else {
        rectY=rectY + visibleRow * state.getBarWidth();
      }
      rectX=java2dValue0;
      rectHeight=state.getBarWidth();
      rectWidth=Math.abs(java2dValue1 - java2dValue0);
      barBase=RectangleEdge.LEFT;
    }
 else {
      if (orientation == PlotOrientation.VERTICAL) {
        rectX=domainAxis.getCategoryStart(column,getColumnCount(),dataArea,domainAxisLocation);
        if (seriesCount > 1) {
          double seriesGap=dataArea.getWidth() * getItemMargin() / (categoryCount * (seriesCount - 1));
          rectX=rectX + visibleRow * (state.getBarWidth() + seriesGap);
        }
 else {
          rectX=rectX + visibleRow * state.getBarWidth();
        }
        rectY=java2dValue0;
        barBase=RectangleEdge.BOTTOM;
      }
    }
    Rectangle2D bar=new Rectangle2D.Double(rectX,rectY,rectWidth,rectHeight);
    BarPainter painter=getBarPainter();
    if (getShadowsVisible()) {
      painter.paintBarShadow(g2,this,row,column,selected,bar,barBase,false);
    }
    getBarPainter().paintBar(g2,this,row,column,selected,bar,barBase);
    CategoryItemLabelGenerator generator=getItemLabelGenerator(row,column,selected);
    if (generator != null && isItemLabelVisible(row,column,selected)) {
      drawItemLabelForBar(g2,plot,dataset,row,column,selected,generator,bar,false);
    }
    EntityCollection entities=state.getEntityCollection();
    if (entities != null) {
      addEntity(entities,bar,dataset,row,column,selected);
    }
  }
  /** 
 * Tests this renderer for equality with an arbitrary object.
 * @param obj  the object (<code>null</code> permitted).
 * @return A boolean.
 */
  public boolean equals(  Object obj){
    if (obj == this) {
      return true;
    }
    if (!(obj instanceof IntervalBarRenderer)) {
      return false;
    }
    return super.equals(obj);
  }
}
