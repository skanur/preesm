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
package org.abo.preesm.plugin.dataparallel.operations

import org.abo.preesm.plugin.dataparallel.DAG2DAG
import org.abo.preesm.plugin.dataparallel.SDF2DAG
import org.abo.preesm.plugin.dataparallel.PureDAGConstructor

/**
 * Visitor interface for operations carried out only on
 * {@link PureDAGConstructor} instances
 * 
 * @author Sudeep Kanur
 */
interface DAGOperations {
	
	/**
	 * Visitor method for DAGs constructed from SDFs
	 * 
	 * @param dag A {@link SDF2DAG} instance
	 */
	def void visit(SDF2DAG dagGen)
	
	/**
	 * Visitor method for DAGs constructed from other DAGs
	 * 
	 * @param dag A {@link DAG2DAG} instance
	 */
	def void visit(DAG2DAG dagGen)
}