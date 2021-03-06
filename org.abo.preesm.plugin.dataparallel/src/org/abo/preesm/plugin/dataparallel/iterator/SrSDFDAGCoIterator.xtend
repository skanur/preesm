/**
 * Copyright or © or Copr. Åbo Akademi University (2017) :
 *
 * Sudeep Kanur <skanur@abo.fi> (2017)
 *
 * This software is a computer program whose purpose is to help prototyping
 * parallel applications using dataflow formalism.
 *
 * This software is governed by the CeCILL  license under French law and
 * abiding by the rules of distribution of free software.  You can  use,
 * modify and/ or redistribute the software under the terms of the CeCILL
 * license as circulated by CEA, CNRS and INRIA at the following URL
 * "http://www.cecill.info".
 *
 * As a counterpart to the access to the source code and  rights to copy,
 * modify and redistribute granted by the license, users are provided only
 * with a limited warranty  and the software's author,  the holder of the
 * economic rights,  and the successive licensors  have only  limited
 * liability.
 *
 * In this respect, the user's attention is drawn to the risks associated
 * with loading,  using,  modifying and/or developing or reproducing the
 * software by the user in light of its specific status of free software,
 * that may mean  that it is complicated to manipulate,  and  that  also
 * therefore means  that it is reserved for developers  and  experienced
 * professionals having in-depth computer knowledge. Users are therefore
 * encouraged to load and test the software's suitability as regards their
 * requirements in conditions enabling the security of their systems and/or
 * data to be ensured and,  more generally, to use and operate it in the
 * same conditions as regards security.
 *
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL license and that you accept its terms.
 */
package org.abo.preesm.plugin.dataparallel.iterator

import java.util.List
import java.util.logging.Logger
import org.abo.preesm.plugin.dataparallel.DAGUtils
import org.abo.preesm.plugin.dataparallel.PureDAGConstructor
import org.ietr.dftools.algorithm.model.sdf.SDFAbstractVertex
import org.ietr.dftools.algorithm.model.sdf.SDFEdge
import org.ietr.dftools.algorithm.model.sdf.SDFGraph
import org.ietr.dftools.algorithm.model.sdf.esdf.SDFForkVertex
import org.ietr.dftools.algorithm.model.sdf.esdf.SDFJoinVertex
import org.ietr.dftools.algorithm.model.sdf.visitors.ToHSDFVisitor
import org.jgrapht.traverse.BreadthFirstIterator

/**
 * The iterator walks one graph w.r.t to nodes present in the another graph such that atleast the 
 * nodes of a given set from the source graph is visited.
 * <p>
 * Eg. Let "source" graph be a DAG and "dest" graph be a SrSDF. 
 * Let visitable nodes be set of nodes of "source" graph that must be seen in "dest" graph
 * Let startInstance be the starting node in "dest" from which walking must be carried out.
 * Then,
 * This iterator walks the "dest" graph from the "startInstance" specified such that all the nodes of
 * "visitable nodes" are seen. Additional nodes of "dest" can be seen in order to reach all the nodes
 * of the "visitable nodes". 
 * <p>
 * Note that "visitable nodes" is defined w.r.t to "source" graph and we need to
 * find explicit equivalent of these in the "dest" graph.
 * While "startInstance" is defined w.r.t to "dest" graph 
 * <p>
 * The class is written explicitly to be used with a Single Rate Graph or HSDF as "dest" graph
 * and the custom DAG (obtained from {@link PureDAGConstructor} implementations).
 * <p>
 * Construct the iterator through the builder {@link SrSDFDAGCoIteratorBuilder}
 * 
 * @author Sudeep Kanur 
 */
class SrSDFDAGCoIterator extends BreadthFirstIterator<SDFAbstractVertex, SDFEdge>  {
	
	/**
	 * Logging instance
	 */
	val Logger logger
	
	/**
	 * List of visitable nodes that are defined in the 
	 * {@link SrSDFDAGCoIteratorBuilder#dag} 
	 */
	val List<SDFAbstractVertex> visitableNodes
	
	/**
	 * A DAG as {@link SDFGraph} instance obtained from implementations
	 * of {@link PureDAGConstructor}
	 */
	val SDFGraph dag
	
	/**
	 * A single rate transform or HSDF as {@link SDFGraph} instance obtained
	 * from {@link ToHSDFVisitor}
	 */
	val SDFGraph srsdf
	
	/**
	 * Constructor
	 * 
	 * @param dag DAG obtained from implementations of {@link PureDAGConstructor}
	 * @param srsdf A single rate SDF graph (SrSDF) obtained from {@link ToHSDFVisitor}
	 * @param visitableNodes Traverse such that only these nodes are seen
	 * @param startInstance The instance to start traversing SrSDF graph
	 * @param logger A logger instance
	 */
	protected new(SDFGraph dag, SDFGraph srsdf, List<SDFAbstractVertex>visitableNodes,
		SDFAbstractVertex startInstance, Logger logger) {
			super(srsdf, startInstance)
			this.visitableNodes = visitableNodes
			this.logger = logger
			this.dag = dag
			this.srsdf = srsdf
		}
	
	/**
	 * Constructor
	 * 
	 * @param dag DAG obtained from implementations of {@link PureDAGConstructor}
	 * @param srsdf A single rate SDF graph (SrSDF) obtained from {@link ToHSDFVisitor}
	 * @param visitableNodes Traverse such that only these nodes are seen
	 * @param startInstance The instance to start traversing SrSDF graph
	 */	
	protected new(SDFGraph source, SDFGraph dest, List<SDFAbstractVertex>visitableNodes,
		SDFAbstractVertex startInstance) {
			this(source, dest, visitableNodes, startInstance, null)
		}
	
	override void encounterVertex(SDFAbstractVertex dagVertex, SDFEdge dagEdge) {
		if(hasEquivalentVertexInDAG(dagVertex)) {
			super.encounterVertex(dagVertex, dagEdge)
		}
	}
	
	private def boolean hasEquivalentVertexInDAG(SDFAbstractVertex srsdfVertex) {
		// Find if there is corresponding vertex in the DAG
		val dagVertex = DAGUtils.findVertex(srsdfVertex, srsdf, dag)
		
		// If its not null and is one of the visitable nodes
		if(dagVertex !== null && visitableNodes.contains(dagVertex)) {
			return true
		}
		
		// If the srsdfVertex is implode node and DAG has no such instance
		// Then check if the associated next node (that is not implode) is
		// in the visitableNodes
		if((srsdfVertex instanceof SDFJoinVertex)) {
			return srsdf.outgoingEdgesOf(srsdfVertex).exists[edge |
				val targetInDAG = DAGUtils.findVertex(edge.target, srsdf, dag)
				(targetInDAG !== null && visitableNodes.contains(targetInDAG))
			]
		}
		
		// If the srsdfVertex is explode node and DAG has no such instance then
		// check if previous associated node was in visitable node
		if((srsdfVertex instanceof SDFForkVertex)) {
			return srsdf.incomingEdgesOf(srsdfVertex).exists[edge |
				val sourceInDAG = DAGUtils.findVertex(edge.source, srsdf, dag)
				(sourceInDAG !== null && visitableNodes.contains(sourceInDAG))
			]
		} 
		
		return false
	}
}