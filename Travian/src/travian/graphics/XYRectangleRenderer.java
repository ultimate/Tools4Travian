package travian.graphics;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.geom.Rectangle2D;

import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.entity.EntityCollection;
import org.jfree.chart.plot.CrosshairState;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYBubbleRenderer;
import org.jfree.chart.renderer.xy.XYItemRendererState;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYZDataset;
import org.jfree.ui.RectangleEdge;

public class XYRectangleRenderer extends XYBubbleRenderer
{
	private static final long serialVersionUID = 1L;

	/**
     * Draws the visual representation of a single data item.
     *
     * @param g2  the graphics device.
     * @param state  the renderer state.
     * @param dataArea  the area within which the data is being drawn.
     * @param info  collects information about the drawing.
     * @param plot  the plot (can be used to obtain standard color 
     *              information etc).
     * @param domainAxis  the domain (horizontal) axis.
     * @param rangeAxis  the range (vertical) axis.
     * @param dataset  the dataset (an {@link XYZDataset} is expected).
     * @param series  the series index (zero-based).
     * @param item  the item index (zero-based).
     * @param crosshairState  crosshair information for the plot 
     *                        (<code>null</code> permitted).
     * @param pass  the pass index.
     */
    public void drawItem(Graphics2D g2, XYItemRendererState state,
            Rectangle2D dataArea, PlotRenderingInfo info, XYPlot plot,
            ValueAxis domainAxis, ValueAxis rangeAxis, XYDataset dataset, 
            int series, int item, CrosshairState crosshairState, int pass) {

        // return straight away if the item is not visible
        if (!getItemVisible(series, item)) {
            return;   
        }
        
        PlotOrientation orientation = plot.getOrientation();
        
        // get the data point...
        double x = dataset.getXValue(series, item);
        double y = dataset.getYValue(series, item);
        double z = Double.NaN;
        if (dataset instanceof XYZDataset) {
            XYZDataset xyzData = (XYZDataset) dataset;
            z = xyzData.getZValue(series, item);
        }
        if (!Double.isNaN(z)) {
            RectangleEdge domainAxisLocation = plot.getDomainAxisEdge();
            RectangleEdge rangeAxisLocation = plot.getRangeAxisEdge();
            double transX = domainAxis.valueToJava2D(x, dataArea, 
                    domainAxisLocation);
            double transY = rangeAxis.valueToJava2D(y, dataArea, 
                    rangeAxisLocation);

            double transDomain = 0.0;
            double transRange = 0.0;
            double zero;

            switch(getScaleType()) {
                case SCALE_ON_DOMAIN_AXIS:
                    zero = domainAxis.valueToJava2D(0.0, dataArea, 
                            domainAxisLocation);
                    transDomain = domainAxis.valueToJava2D(z, dataArea, 
                            domainAxisLocation) - zero;
                    transRange = transDomain;
                    break;
                case SCALE_ON_RANGE_AXIS:
                    zero = rangeAxis.valueToJava2D(0.0, dataArea, 
                            rangeAxisLocation);
                    transRange = zero - rangeAxis.valueToJava2D(z, dataArea, 
                            rangeAxisLocation);
                    transDomain = transRange;
                    break;
                default:
                    double zero1 = domainAxis.valueToJava2D(0.0, dataArea, 
                            domainAxisLocation);
                    double zero2 = rangeAxis.valueToJava2D(0.0, dataArea, 
                            rangeAxisLocation);
                    transDomain = domainAxis.valueToJava2D(z, dataArea, 
                            domainAxisLocation) - zero1;
                    transRange = zero2 - rangeAxis.valueToJava2D(z, dataArea, 
                            rangeAxisLocation);
            }
            transDomain = Math.abs(transDomain);
            transRange = Math.abs(transRange);
            Rectangle2D rect = null;
            if (orientation == PlotOrientation.VERTICAL) {
            	rect = new Rectangle2D.Double(transX - transDomain / 2.0, 
                        transY - transRange / 2.0, transDomain, transRange);
            }
            else if (orientation == PlotOrientation.HORIZONTAL) {
            	rect = new Rectangle2D.Double(transY - transRange / 2.0, 
                        transX - transDomain / 2.0, transRange, transDomain);
            }
            g2.setPaint(getItemPaint(series, item));
            g2.fill(rect);
            
            Paint outlinePaint = getItemOutlinePaint(series, item);
            if(!outlinePaint.equals(new Color(1,1,1)))
            {
            	g2.setStroke(getItemOutlineStroke(series, item));
            	g2.setPaint(outlinePaint);
            	int w = (int)rect.getWidth();
            	if(w < 2)
            		w = 0;
            	else
            		w = w/2;
            	int h = (int)rect.getHeight();
            	if(h < 2)
            		h = 0;
            	else
            		h = h/2;
            	
            	int[] xPoints = {(int)rect.getX()+1, (int)rect.getX()+1+w, (int)rect.getX()+1  };
            	int[] yPoints = {(int)rect.getY()+1, (int)rect.getY()+1  , (int)rect.getY()+1+h};
            	g2.fillPolygon(xPoints, yPoints, 3);
            }

            if (isItemLabelVisible(series, item)) {
                if (orientation == PlotOrientation.VERTICAL) {
                    drawItemLabel(g2, orientation, dataset, series, item, 
                            transX, transY, false);
                }
                else if (orientation == PlotOrientation.HORIZONTAL) {
                    drawItemLabel(g2, orientation, dataset, series, item, 
                            transY, transX, false);                
                }
            }
            
            // add an entity if this info is being collected
            EntityCollection entities = null;
            if (info != null) {
                entities = info.getOwner().getEntityCollection();
                if (entities != null && rect.intersects(dataArea)) {
                    addEntity(entities, rect, dataset, series, item, 
                    		rect.getCenterX(), rect.getCenterY());
                }
            }

            int domainAxisIndex = plot.getDomainAxisIndex(domainAxis);
            int rangeAxisIndex = plot.getRangeAxisIndex(rangeAxis);
            updateCrosshairValues(crosshairState, x, y, domainAxisIndex, 
                    rangeAxisIndex, transX, transY, orientation);
        }

    }

}
