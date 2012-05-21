/*********************************************************
Copyright or � or Copr. IETR/INSA: Matthieu Wipliez, Jonathan Piat,
Maxime Pelcat, Jean-Fran�ois Nezan, Micka�l Raulet

[mwipliez,jpiat,mpelcat,jnezan,mraulet]@insa-rennes.fr

This software is a computer program whose purpose is to prototype
parallel applications.

This software is governed by the CeCILL-C license under French law and
abiding by the rules of distribution of free software.  You can  use, 
modify and/ or redistribute the software under the terms of the CeCILL-C
license as circulated by CEA, CNRS and INRIA at the following URL
"http://www.cecill.info". 

As a counterpart to the access to the source code and  rights to copy,
modify and redistribute granted by the license, users are provided only
with a limited warranty  and the software's author,  the holder of the
economic rights,  and the successive licensors  have only  limited
liability. 

In this respect, the user's attention is drawn to the risks associated
with loading,  using,  modifying and/or developing or reproducing the
software by the user in light of its specific status of free software,
that may mean  that it is complicated to manipulate,  and  that  also
therefore means  that it is reserved for developers  and  experienced
professionals having in-depth computer knowledge. Users are therefore
encouraged to load and test the software's suitability as regards their
requirements in conditions enabling the security of their systems and/or 
data to be ensured and,  more generally, to use and operate it in the 
same conditions as regards security. 

The fact that you are presently reading this means that you have had
knowledge of the CeCILL-C license and that you accept its terms.
 *********************************************************/

package org.ietr.preesm.mapper.ui;

import java.awt.Color;
import java.awt.Frame;
import java.awt.LinearGradientPaint;
import java.awt.Paint;
import java.awt.event.WindowEvent;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.dftools.architecture.slam.ComponentInstance;
import net.sf.dftools.architecture.slam.Design;

import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.ietr.preesm.core.architecture.util.DesignTools;
import org.ietr.preesm.mapper.model.MapperDAG;
import org.ietr.preesm.mapper.model.MapperDAGVertex;
import org.ietr.preesm.mapper.tools.TopologicalDAGIterator;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.labels.IntervalCategoryItemLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.DefaultDrawingSupplier;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.IntervalCategoryDataset;
import org.jfree.data.gantt.Task;
import org.jfree.data.gantt.TaskSeries;
import org.jfree.data.gantt.TaskSeriesCollection;
import org.jfree.data.time.SimpleTimePeriod;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RectangleInsets;
import org.jfree.ui.RefineryUtilities;

/**
 * Gantt plotter of a mapperdagvertex using JFreeChart
 * 
 * @author mpelcat
 */
public class GanttPlotter extends ApplicationFrame implements
		IImplementationPlotter {

	private static final long serialVersionUID = 1L;

	JFreeChart chart = null;
	ChartPanel chartPanel = null;

	/**
	 * Creates a chart.
	 * 
	 * @param dataset
	 *            a dataset.
	 * 
	 * @return A chart.
	 */
	private JFreeChart createChart(IntervalCategoryDataset dataset) {

		JFreeChart chart = ChartFactory.createGanttChart("Solution Gantt", // title
				"Operators", // x-axis label
				"Time", // y-axis label
				null, // data
				true, // create legend?
				true, // generate tooltips?
				false // generate URLs?
				);

		CategoryPlot plot = (CategoryPlot) chart.getPlot();

		Paint p = getBackgroundColorGradient();
		chart.setBackgroundPaint(p);

		plot.setBackgroundPaint(Color.white);
		plot.setDomainGridlinePaint(Color.white);
		plot.setRangeGridlinePaint(Color.black);
		plot.setAxisOffset(new RectangleInsets(5.0, 5.0, 5.0, 5.0));
		plot.setOrientation(PlotOrientation.HORIZONTAL);

		DateAxis xaxis = (DateAxis) plot.getRangeAxis();
		xaxis.setDateFormatOverride(new VertexDateFormat());
		xaxis.setPositiveArrowVisible(true);

		DefaultDrawingSupplier d = new DefaultDrawingSupplier();

		plot.setDrawingSupplier(d);
		MyGanttRenderer ren = new MyGanttRenderer();
		// ren.setRepaintedListener(new RefreshRepaintedListener(this));

		ren.setSeriesItemLabelsVisible(0, false);
		ren.setSeriesVisibleInLegend(0, false);
		ren.setSeriesItemLabelGenerator(0,
				new IntervalCategoryItemLabelGenerator());
		ren.setSeriesToolTipGenerator(0, new MapperGanttToolTipGenerator());

		ren.setAutoPopulateSeriesShape(false);

		plot.setRenderer(ren);

		plot.setDataset(dataset);
		return chart;

	}

	/**
	 * Creates a dataset from a MapperDAGVertex.
	 * 
	 * @return The dataset.
	 */
	private static IntervalCategoryDataset createDataset(MapperDAG dag,
			Design archi) {

		TaskSeries series = new TaskSeries("Scheduled");
		Task currenttask;
		Map<String, Long> finalCosts = new HashMap<String, Long>();

		// Creating the Operator lines
		List<ComponentInstance> cmps = new ArrayList<ComponentInstance>();
		cmps.addAll(DesignTools.getComponentInstances(archi));
		Collections.sort(cmps, new DesignTools.ComponentInstanceComparator());

		for (ComponentInstance cmp : cmps) {
			currenttask = new Task(cmp.getInstanceName(), new SimpleTimePeriod(
					0, 1));
			series.add(currenttask);
			finalCosts.put(cmp.getInstanceName(), 0l);
		}

		// Populating the Operator lines
		TopologicalDAGIterator viterator = new TopologicalDAGIterator(dag);

		while (viterator.hasNext()) {
			MapperDAGVertex currentVertex = (MapperDAGVertex) viterator.next();
			ComponentInstance cmp = currentVertex
					.getImplementationVertexProperty().getEffectiveComponent();

			if (cmp != DesignTools.NO_COMPONENT_INSTANCE) {
				long start = currentVertex.getTimingVertexProperty()
						.getNewtLevel();
				long end = start
						+ currentVertex.getTimingVertexProperty().getCost();
				String taskName = currentVertex.getName()
						+ " (x"
						+ currentVertex.getInitialVertexProperty()
								.getNbRepeat() + ")";
				Task t = new Task(taskName, new SimpleTimePeriod(start, end));
				series.get(cmp.getInstanceName()).addSubtask(t);

				// Updating the component final cost
				long finalCost = finalCosts.get(cmp.getInstanceName());
				if (finalCost < end) {
					finalCosts.put(cmp.getInstanceName(), end);
				}
			}
		}

		// Setting the series length to the maximum end time of a task
		for (ComponentInstance cmp : cmps) {
			long finalCost = finalCosts.get(cmp.getInstanceName());
			if (finalCost > 0) {
				series.get(cmp.getInstanceName()).setDuration(
						new SimpleTimePeriod(0, finalCost));
			} else {
				series.remove(series.get(cmp.getInstanceName()));
			}
		}

		TaskSeriesCollection collection = new TaskSeriesCollection();
		collection.add(series);

		return collection;

	}

	public static void plotDeployment(MapperDAG dag, Design archi,
			Composite delegateDisplay) {

		GanttPlotter plotter = new GanttPlotter("Solution gantt", dag, archi);

		if (delegateDisplay == null) {
			plotter.plot(dag, archi);
		} else {
			plotter.plotInComposite(dag, archi, delegateDisplay);
		}
	}

	/**
	 * Starting point for the demonstration application.
	 * 
	 * @param args
	 *            ignored.
	 */
	public void plot(MapperDAG dag, Design archi) {

		pack();
		RefineryUtilities.centerFrameOnScreen(this);
		setVisible(true);

	}

	/**
	 * Gantt chart plotting function in a given composite
	 */
	public void plotInComposite(MapperDAG dag, Design archi, Composite parent) {

		Composite composite = new Composite(parent, SWT.EMBEDDED | SWT.FILL);
		parent.setLayout(new FillLayout());
		Frame frame = SWT_AWT.new_Frame(composite);
		frame.add(getContentPane());

		MouseClickedListener listener = new MouseClickedListener(frame);
		chartPanel.addChartMouseListener(listener);
		chartPanel.addMouseMotionListener(listener);
		chartPanel.addMouseListener(listener);
	}

	/**
	 * Plotting a Gantt chart
	 */
	public GanttPlotter(String title, MapperDAG dag, Design archi) {
		super(title);

		chart = createChart(createDataset(dag, archi));
		chartPanel = new ChartPanel(chart);
		chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));
		chartPanel.setMouseZoomable(true, true);
		setContentPane(chartPanel);

	}

	public void windowClosing(WindowEvent event) {
		if (event.equals(WindowEvent.WINDOW_CLOSING)) {

		}
	}

	public static LinearGradientPaint getBackgroundColorGradient() {
		Point2D start = new Point2D.Float(0, 0);
		Point2D end = new Point2D.Float(500, 500);
		float[] dist = { 0.0f, 0.8f };
		Color[] colors = { new Color(170, 160, 190), Color.WHITE };
		LinearGradientPaint p = new LinearGradientPaint(start, end, dist,
				colors);
		return p;
	}
}