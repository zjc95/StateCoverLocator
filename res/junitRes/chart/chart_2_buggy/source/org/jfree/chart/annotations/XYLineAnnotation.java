package org.jfree.chart.annotations;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.util.ObjectUtilities;
import org.jfree.chart.util.PaintUtilities;
import org.jfree.chart.util.PublicCloneable;
import org.jfree.chart.util.RectangleEdge;
import org.jfree.chart.util.SerialUtilities;
import org.jfree.chart.util.ShapeUtilities;
/** 
 * A simple line annotation that can be placed on an                                                                                                                                                                     {@link XYPlot}.
 */
public class XYLineAnnotation extends AbstractXYAnnotation implements Cloneable, PublicCloneable, Serializable {
  /** 
 * For serialization. 
 */
  private static final long serialVersionUID=-80535465244091334L;
  /** 
 * The x-coordinate. 
 */
  private double x1;
  /** 
 * The y-coordinate. 
 */
  private double y1;
  /** 
 * The x-coordinate. 
 */
  private double x2;
  /** 
 * The y-coordinate. 
 */
  private double y2;
  /** 
 * The line stroke. 
 */
  private transient Stroke stroke;
  /** 
 * The line color. 
 */
  private transient Paint paint;
  /** 
 * Creates a new annotation that draws a line from (x1, y1) to (x2, y2) where the coordinates are measured in data space (that is, against the plot's axes).
 * @param x1  the x-coordinate for the start of the line.
 * @param y1  the y-coordinate for the start of the line.
 * @param x2  the x-coordinate for the end of the line.
 * @param y2  the y-coordinate for the end of the line.
 */
  public XYLineAnnotation(  double x1,  double y1,  double x2,  double y2){
    this(x1,y1,x2,y2,new BasicStroke(1.0f),Color.black);
  }
  /** 
 * Creates a new annotation that draws a line from (x1, y1) to (x2, y2) where the coordinates are measured in data space (that is, against the plot's axes).
 * @param x1  the x-coordinate for the start of the line.
 * @param y1  the y-coordinate for the start of the line.
 * @param x2  the x-coordinate for the end of the line.
 * @param y2  the y-coordinate for the end of the line.
 * @param stroke  the line stroke (<code>null</code> not permitted).
 * @param paint  the line color (<code>null</code> not permitted).
 */
  public XYLineAnnotation(  double x1,  double y1,  double x2,  double y2,  Stroke stroke,  Paint paint){
    super();
    if (stroke == null) {
      throw new IllegalArgumentException("Null 'stroke' argument.");
    }
    if (paint == null) {
      throw new IllegalArgumentException("Null 'paint' argument.");
    }
    this.x1=x1;
    this.y1=y1;
    this.x2=x2;
    this.y2=y2;
    this.stroke=stroke;
    this.paint=paint;
  }
  /** 
 * Draws the annotation.  This method is called by the                                                                                                                                                                     {@link XYPlot}class, you won't normally need to call it yourself.
 * @param g2  the graphics device.
 * @param plot  the plot.
 * @param dataArea  the data area.
 * @param domainAxis  the domain axis.
 * @param rangeAxis  the range axis.
 * @param rendererIndex  the renderer index.
 * @param info  if supplied, this info object will be populated withentity information.
 */
  public void draw(  Graphics2D g2,  XYPlot plot,  Rectangle2D dataArea,  ValueAxis domainAxis,  ValueAxis rangeAxis,  int rendererIndex,  PlotRenderingInfo info){
    PlotOrientation orientation=plot.getOrientation();
    RectangleEdge domainEdge=Plot.resolveDomainAxisLocation(plot.getDomainAxisLocation(),orientation);
    RectangleEdge rangeEdge=Plot.resolveRangeAxisLocation(plot.getRangeAxisLocation(),orientation);
    float j2DX1=0.0f;
    float j2DX2=0.0f;
    float j2DY1=0.0f;
    float j2DY2=0.0f;
    if (orientation == PlotOrientation.VERTICAL) {
      j2DX1=(float)domainAxis.valueToJava2D(this.x1,dataArea,domainEdge);
      j2DY1=(float)rangeAxis.valueToJava2D(this.y1,dataArea,rangeEdge);
      j2DX2=(float)domainAxis.valueToJava2D(this.x2,dataArea,domainEdge);
      j2DY2=(float)rangeAxis.valueToJava2D(this.y2,dataArea,rangeEdge);
    }
 else {
      if (orientation == PlotOrientation.HORIZONTAL) {
        j2DY1=(float)domainAxis.valueToJava2D(this.x1,dataArea,domainEdge);
        j2DX1=(float)rangeAxis.valueToJava2D(this.y1,dataArea,rangeEdge);
        j2DY2=(float)domainAxis.valueToJava2D(this.x2,dataArea,domainEdge);
        j2DX2=(float)rangeAxis.valueToJava2D(this.y2,dataArea,rangeEdge);
      }
    }
    g2.setPaint(this.paint);
    g2.setStroke(this.stroke);
    Line2D line=new Line2D.Float(j2DX1,j2DY1,j2DX2,j2DY2);
    boolean visible=ShapeUtilities.clipLine(line,dataArea);
    if (visible) {
      g2.draw(line);
    }
    String toolTip=getToolTipText();
    String url=getURL();
    if (toolTip != null || url != null) {
      addEntity(info,ShapeUtilities.createLineRegion(line,1.0f),rendererIndex,toolTip,url);
    }
  }
  /** 
 * Tests this object for equality with an arbitrary object.
 * @param obj  the object to test against (<code>null</code> permitted).
 * @return <code>true</code> or <code>false</code>.
 */
  public boolean equals(  Object obj){
    if (obj == this) {
      return true;
    }
    if (!super.equals(obj)) {
      return false;
    }
    if (!(obj instanceof XYLineAnnotation)) {
      return false;
    }
    XYLineAnnotation that=(XYLineAnnotation)obj;
    if (this.x1 != that.x1) {
      return false;
    }
    if (this.y1 != that.y1) {
      return false;
    }
    if (this.x2 != that.x2) {
      return false;
    }
    if (this.y2 != that.y2) {
      return false;
    }
    if (!PaintUtilities.equal(this.paint,that.paint)) {
      return false;
    }
    if (!ObjectUtilities.equal(this.stroke,that.stroke)) {
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
    long temp;
    temp=Double.doubleToLongBits(this.x1);
    result=(int)(temp ^ (temp >>> 32));
    temp=Double.doubleToLongBits(this.x2);
    result=29 * result + (int)(temp ^ (temp >>> 32));
    temp=Double.doubleToLongBits(this.y1);
    result=29 * result + (int)(temp ^ (temp >>> 32));
    temp=Double.doubleToLongBits(this.y2);
    result=29 * result + (int)(temp ^ (temp >>> 32));
    return result;
  }
  /** 
 * Returns a clone of the annotation.
 * @return A clone.
 * @throws CloneNotSupportedException  if the annotation can't be cloned.
 */
  public Object clone() throws CloneNotSupportedException {
    return super.clone();
  }
  /** 
 * Provides serialization support.
 * @param stream  the output stream.
 * @throws IOException  if there is an I/O error.
 */
  private void writeObject(  ObjectOutputStream stream) throws IOException {
    stream.defaultWriteObject();
    SerialUtilities.writePaint(this.paint,stream);
    SerialUtilities.writeStroke(this.stroke,stream);
  }
  /** 
 * Provides serialization support.
 * @param stream  the input stream.
 * @throws IOException  if there is an I/O error.
 * @throws ClassNotFoundException  if there is a classpath problem.
 */
  private void readObject(  ObjectInputStream stream) throws IOException, ClassNotFoundException {
    stream.defaultReadObject();
    this.paint=SerialUtilities.readPaint(stream);
    this.stroke=SerialUtilities.readStroke(stream);
  }
}
