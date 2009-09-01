/*********************************************************
Copyright or � or Copr. IETR/INSA: Matthieu Wipliez, Jonathan Piat,
Maxime Pelcat, Peng Cheng Mu, Jean-Fran�ois Nezan, Micka�l Raulet

[mwipliez,jpiat,mpelcat,pmu,jnezan,mraulet]@insa-rennes.fr

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

package org.ietr.preesm.plugin.abc.impl.latency;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Observer;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.logging.Level;

import org.ietr.preesm.core.architecture.ArchitectureComponent;
import org.ietr.preesm.core.architecture.ArchitectureComponentType;
import org.ietr.preesm.core.architecture.MultiCoreArchitecture;
import org.ietr.preesm.core.architecture.simplemodel.Operator;
import org.ietr.preesm.core.scenario.IScenario;
import org.ietr.preesm.core.tools.PreesmLogger;
import org.ietr.preesm.plugin.abc.AbcType;
import org.ietr.preesm.plugin.abc.AbstractAbc;
import org.ietr.preesm.plugin.abc.edgescheduling.AbstractEdgeSched;
import org.ietr.preesm.plugin.abc.edgescheduling.EdgeSchedType;
import org.ietr.preesm.plugin.abc.edgescheduling.IEdgeSched;
import org.ietr.preesm.plugin.abc.impl.ImplementationCleaner;
import org.ietr.preesm.plugin.abc.route.AbstractCommunicationRouter;
import org.ietr.preesm.plugin.abc.route.CommunicationRouter;
import org.ietr.preesm.plugin.mapper.model.MapperDAG;
import org.ietr.preesm.plugin.mapper.model.MapperDAGEdge;
import org.ietr.preesm.plugin.mapper.model.MapperDAGVertex;
import org.ietr.preesm.plugin.mapper.model.impl.PrecedenceEdgeAdder;
import org.ietr.preesm.plugin.mapper.model.impl.TransferVertex;
import org.ietr.preesm.plugin.mapper.plot.GanttPlotter;
import org.ietr.preesm.plugin.mapper.plot.IImplementationPlotter;
import org.ietr.preesm.plugin.mapper.timekeeper.NewTimeKeeper;
import org.ietr.preesm.plugin.mapper.tools.SchedulingOrderIterator;
import org.sdf4j.model.dag.DAGVertex;

/**
 * Abc that minimizes latency
 * 
 * @author mpelcat
 */
public abstract class LatencyAbc extends AbstractAbc {

	/**
	 * Current time keeper: called exclusively by simulator to update the useful
	 * time tags in DAG
	 */
	// protected GraphTimeKeeper timeKeeper;
	protected NewTimeKeeper nTimeKeeper;

	protected AbstractCommunicationRouter comRouter = null;

	/**
	 * Scheduling the transfer vertices on the media
	 */
	protected IEdgeSched edgeScheduler;

	/**
	 * Constructor of the simulator from a "blank" implementation where every
	 * vertex has not been implanted yet.
	 */
	public LatencyAbc(EdgeSchedType edgeSchedType, MapperDAG dag,
			MultiCoreArchitecture archi, AbcType abcType, IScenario scenario) {
		super(dag, archi, abcType, scenario);

		nTimeKeeper = new NewTimeKeeper(implementation, orderManager);
		nTimeKeeper.resetTimings();

		// this.timeKeeper = new GraphTimeKeeper(implementation, nTimeKeeper);
		// timeKeeper.resetTimings();

		// The media simulator calculates the edges costs
		edgeScheduler = AbstractEdgeSched.getInstance(edgeSchedType,
				orderManager);
		comRouter = new CommunicationRouter(archi, scenario, implementation,
				edgeScheduler, orderManager);
	}

	/**
	 * Sets the DAG as current DAG and retrieves all implementation to calculate
	 * timings
	 */
	@Override
	public void setDAG(MapperDAG dag) {

		this.dag = dag;
		this.implementation = dag.clone();

		orderManager.reconstructTotalOrderFromDAG(implementation);

		nTimeKeeper = new NewTimeKeeper(implementation, orderManager);
		nTimeKeeper.resetTimings();

		// this.timeKeeper = new GraphTimeKeeper(implementation, nTimeKeeper);
		// timeKeeper.resetTimings();

		// Forces the unmapping process before the new mapping process
		HashMap<MapperDAGVertex, Operator> operators = new HashMap<MapperDAGVertex, Operator>();

		for (DAGVertex v : dag.vertexSet()) {
			MapperDAGVertex mdv = (MapperDAGVertex) v;
			operators.put(mdv, mdv.getImplementationVertexProperty()
					.getEffectiveOperator());
			mdv.getImplementationVertexProperty().setEffectiveComponent(
					Operator.NO_COMPONENT);
			implementation.getMapperDAGVertex(mdv.getName())
					.getImplementationVertexProperty().setEffectiveComponent(
							Operator.NO_COMPONENT);
			;
		}

		edgeScheduler = AbstractEdgeSched.getInstance(edgeScheduler
				.getEdgeSchedType(), orderManager);
		comRouter.setManagers(implementation, edgeScheduler, orderManager);

		SchedulingOrderIterator iterator = new SchedulingOrderIterator(
				this.dag, this, true);

		while (iterator.hasNext()) {
			MapperDAGVertex vertex = iterator.next();
			Operator operator = operators.get(vertex);

			implant(vertex, operator, false);
		}
	}

	@Override
	protected void fireNewMappedVertex(MapperDAGVertex vertex,
			boolean updateRank) {

		Operator effectiveOp = vertex.getImplementationVertexProperty()
				.getEffectiveOperator();

		if (effectiveOp == Operator.NO_COMPONENT) {
			PreesmLogger.getLogger().severe(
					"implementation of " + vertex.getName() + " failed");
		} else {

			if (updateRank) {
				taskScheduler.insertVertex(vertex);
			} else {
				orderManager.insertVertexInTotalOrder(vertex);
			}

			long vertextime = vertex.getInitialVertexProperty().getTime(
					effectiveOp);

			// Set costs
			vertex.getTimingVertexProperty().setCost(vertextime);

			setEdgesCosts(vertex.incomingEdges());
			setEdgesCosts(vertex.outgoingEdges());
		}
	}

	@Override
	protected void fireNewUnmappedVertex(MapperDAGVertex vertex) {

		// unimplanting a vertex resets the cost of the current vertex
		// and its edges

		vertex.getTimingVertexProperty().resetCost();

		resetCost(vertex.incomingEdges());
		resetCost(vertex.outgoingEdges());

		ImplementationCleaner cleaner = new ImplementationCleaner(orderManager,
				implementation);
		cleaner.removeAllOverheads(vertex);
		cleaner.removeAllInvolvements(vertex);
		cleaner.removeAllTransfers(vertex);
		cleaner.unscheduleVertex(vertex);
	}

	@Override
	public void implant(MapperDAGVertex dagvertex, Operator operator,
			boolean updateRank) {
		super.implant(dagvertex, operator, updateRank);
		// timeKeeper.setAsDirty(dagvertex);
	}

	@Override
	public void unimplant(MapperDAGVertex dagvertex) {
		super.unimplant(dagvertex);
		// timeKeeper.setAsDirty(dagvertex);
	}

	/**
	 * Asks the time keeper to update timings. Crucial and costly operation.
	 * Depending on the king of timings we want, calls the necessary updates.
	 */
	public void updateTimings() {

		// timeKeeper.updateTLevels();
		nTimeKeeper.updateTLevels();
	}

	/**
	 * Setting edge costs for special types
	 */
	@Override
	protected void setEdgeCost(MapperDAGEdge edge) {

		// Special vertices create edges with prohibitive costs so that they
		// are mapped correctly: fork after the sender and join before the
		// receiver. No more used because the usable rules with prohibitive
		// costs works not well with list scheduling.
		/*
		 * if ((edge.getTarget() != null && SpecialVertexManager.isFork(edge
		 * .getTarget()))) { ImplementationVertexProperty sourceimp =
		 * ((MapperDAGVertex) edge
		 * .getSource()).getImplementationVertexProperty();
		 * ImplementationVertexProperty destimp = ((MapperDAGVertex) edge
		 * .getTarget()).getImplementationVertexProperty();
		 * 
		 * Operator sourceOp = sourceimp.getEffectiveOperator(); Operator destOp
		 * = destimp.getEffectiveOperator();
		 * 
		 * if (sourceOp != Operator.NO_COMPONENT && destOp !=
		 * Operator.NO_COMPONENT) { if (sourceOp.equals(destOp)) {
		 * edge.getTimingEdgeProperty().setCost(0); } else {
		 * edge.getTimingEdgeProperty().setCost(
		 * SpecialVertexManager.dissuasiveCost); } } }
		 */
	}

	public abstract EdgeSchedType getEdgeSchedType();

	/**
	 * *********Timing accesses**********
	 */

	/**
	 * The cost of a vertex is the end time of its execution (latency minimization)
	 */
	@Override
	public final long getFinalCost(MapperDAGVertex vertex) {
		vertex = translateInImplementationVertex(vertex);

		long finalTime = nTimeKeeper.getFinalTime(vertex);

		if (finalTime < 0) {
			PreesmLogger.getLogger().log(Level.SEVERE,
					"negative vertex final time");
		}

		return finalTime;

	}

	/**
	 * The cost of a component is the end time of its last vertex (latency minimization)
	 */
	@Override
	public final long getFinalCost(ArchitectureComponent component) {

		long finalTime = nTimeKeeper.getFinalTime(component);

		return finalTime;
	}

	/**
	 * The cost of an implementation is calculated from its latency and loads
	 */
	@Override
	public final long getFinalCost() {

		long finalTime = getFinalLatency();

		long loadBalancing = evaluateLoadBalancing();

		return finalTime + loadBalancing;
	}

	/**
	 * The cost of an implementation is calculated from its latency and loads
	 */
	public final long getFinalLatency() {

		long finalTime = nTimeKeeper.getFinalTime();

		if (finalTime < 0) {
			PreesmLogger.getLogger().log(Level.SEVERE,
					"negative implementation final latency");
		}

		return finalTime;
	}

	public final long getTLevel(MapperDAGVertex vertex, boolean update) {
		vertex = translateInImplementationVertex(vertex);

		if (update)
			updateTimings();
		return vertex.getTimingVertexProperty().getNewtLevel();
	}

	public final long getBLevel(MapperDAGVertex vertex, boolean update) {
		vertex = translateInImplementationVertex(vertex);

		if (update)
			updateTimings();
		return vertex.getTimingVertexProperty().getNewbLevel();
	}

	/**
	 * Plots the current implementation. If delegatedisplay=false, the gantt is
	 * displayed in a shell. Otherwise, it is displayed in Eclipse.
	 */
	public final IImplementationPlotter plotImplementation(
			boolean delegateDisplay) {

		if (!delegateDisplay) {
			updateTimings();
			GanttPlotter.plot(implementation, this);
			return null;
		} else {
			updateTimings();
			return new GanttPlotter("Solution gantt", implementation, this);
		}
	}

	public AbstractCommunicationRouter getComRouter() {
		return comRouter;
	}

	/**
	 * Gives an index between 0 and 1 evaluating the load balancing
	 */
	public long evaluateLoadBalancing() {

		List<Long> taskSums = new ArrayList<Long>();
		long totalTaskSum = 0l;

		for (ArchitectureComponent o : archi.getComponents()) {
			long load = getLoad(o);
			if (load > 0) {
				taskSums.add(load);
				totalTaskSum += load;
			}
		}

		int cmpNr = taskSums.size();

		Collections.sort(taskSums, new Comparator<Long>() {
			@Override
			public int compare(Long arg0, Long arg1) {
				return (int) (arg0 - arg1);
			}
		});

		long mean = totalTaskSum / taskSums.size();
		long variance = 0;
		// Calculating the load sum of half the components with the lowest loads
		for (long taskDuration : taskSums) {
			variance += ((taskDuration - mean)*(taskDuration - mean)) / taskSums.size();
		}

		return (long)Math.sqrt(variance);
	}

	/**
	 * Reschedule all the transfers generated during mapping
	 */
	public void rescheduleTransfers(List<MapperDAGVertex> cpnDominantList) {

		if (this.orderManager != null) {
			ImplementationCleaner cleaner = new ImplementationCleaner(
					orderManager, implementation);

			for (ArchitectureComponent cmp : archi
					.getComponents(ArchitectureComponentType.contentionNode)) {
				for (MapperDAGVertex v : this.orderManager.getSchedule(cmp)) {
					cleaner.unscheduleVertex(v);
				}
			}

			updateTimings();

			for (ArchitectureComponent cmp : archi
					.getComponents(ArchitectureComponentType.contentionNode)) {
				ConcurrentSkipListSet<MapperDAGVertex> list = new ConcurrentSkipListSet<MapperDAGVertex>(
						new Comparator<MapperDAGVertex>() {
							@Override
							public int compare(MapperDAGVertex arg0,
									MapperDAGVertex arg1) {
								long TLevelDifference = (getTLevel(arg0, false) - getTLevel(
										arg1, false));
								if (TLevelDifference == 0)
									TLevelDifference = (arg0.getName()
											.compareTo(arg1.getName()));
								return (int) TLevelDifference;
							}
						});
				list.addAll(this.orderManager.getSchedule(cmp));

				for (MapperDAGVertex v : list) {
					TransferVertex tv = (TransferVertex) v;
					orderManager.insertVertexBefore(tv, tv.getTarget());
				}
			}
		}

		/*
		 * Schedule totalOrder = this.getTotalOrder(); List<String> orderedNames
		 * = new ArrayList<String>();
		 * 
		 * for (MapperDAGVertex v : totalOrder) { if (v instanceof
		 * TransferVertex) { // addVertexAfterSourceLastTransfer(v,
		 * orderedNames); } else if (v instanceof OverheadVertex) {
		 * addVertexAfterSourceLastOverhead(v, orderedNames); } else {
		 * orderedNames.add(v.getName()); } }
		 * 
		 * for(int index = cpnDominantList.size()-1;index >= 0 ; index--){
		 * MapperDAGVertex v = cpnDominantList.get(index); for (DAGVertex t :
		 * ImplementationCleaner
		 * .getFollowingTransfers(this.translateInImplementationVertex(v))) { if
		 * (!orderedNames.contains(t.getName())) {
		 * addVertexAfterSourceLastTransfer((MapperDAGVertex)t, orderedNames); }
		 * } }
		 */
		/*
		 * for (MapperDAGVertex v : cpnDominantList) { for (DAGVertex t :
		 * ImplementationCleaner
		 * .getPrecedingTransfers(this.translateInImplementationVertex(v))) { if
		 * (!orderedNames.contains(t.getName())) {
		 * addVertexBeforeTarget((MapperDAGVertex)t, orderedNames); } } }
		 */

		/*
		 * MapperDAGVertex v = totalOrder.getLast();
		 * 
		 * while(v!=null){ if (v instanceof TransferVertex) {
		 * addVertexBeforeTargetFirstTransfer(v, orderedNames); } else if (v
		 * instanceof OverheadVertex) { //addVertexAfterSourceLastOverhead(v,
		 * orderedNames); } else { //orderedNames.add(v.getName()); } v =
		 * totalOrder.getPreviousVertex(v); }
		 */
		// reorder(orderedNames);
	}

	@Override
	public void updateFinalCosts() {
		updateTimings();
	}
}
