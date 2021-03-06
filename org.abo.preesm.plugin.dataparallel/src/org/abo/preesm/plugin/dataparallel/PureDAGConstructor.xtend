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
package org.abo.preesm.plugin.dataparallel

import java.util.List
import java.util.Map
import org.abo.preesm.plugin.dataparallel.operations.DAGOperations
import org.ietr.dftools.algorithm.model.sdf.SDFAbstractVertex
import org.ietr.dftools.algorithm.model.sdf.SDFEdge
import org.ietr.dftools.algorithm.model.sdf.SDFGraph
import org.jgrapht.graph.AbstractGraph

/**
 * Interface for construction of Pure DAGs only. 
 * In additions to the methods from DAGConstructor, classes implementing
 * this interface can also return the constructed DAG
 * 
 * @author Sudeep Kanur
 */
interface PureDAGConstructor extends DAGConstructor {
	/**
	 * Return the DAG that is constructed
	 * The DAG is the loop schedule
	 * 
	 * @return DAG constructed
	 */
	public def SDFGraph getOutputGraph()
	
	/**
	 * Return the input SDFG
	 * 
	 * @return input SDFG
	 */
	public def AbstractGraph<SDFAbstractVertex, SDFEdge> getInputGraph()
	
	/**
	 * Return the map of actor from original SDFG to all its immediate
	 * predecessor
	 * 
	 * @return Map of actor to list of immediate predecessor in original SDFG
	 */
	public def Map<SDFAbstractVertex, List<SDFAbstractVertex>> getActorPredecessor()
	
	/**
	 * Return a list of all the actors of the original SDFG that form a part of
	 * a cycle
	 * 
	 * @return List of all actors of all the cycles in the SDFG
	 */
	public def List<SDFAbstractVertex> getCycleActors()
	
	/**
	 * Method for operation visitor
	 */
	public def void accept(DAGOperations visitor)
}