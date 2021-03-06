/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2008 - 2017) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017)
 * Clément Guy <clement.guy@insa-rennes.fr> (2014)
 * Matthieu Wipliez <matthieu.wipliez@insa-rennes.fr> (2008)
 * Maxime Pelcat <maxime.pelcat@insa-rennes.fr> (2008 - 2013)
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
package org.ietr.preesm.mapper.tools;

import java.util.logging.Level;
import org.ietr.dftools.workflow.tools.WorkflowLogger;
import org.ietr.preesm.mapper.abc.IAbc;
import org.ietr.preesm.mapper.model.MapperDAG;
import org.ietr.preesm.mapper.model.MapperDAGVertex;

// TODO: Auto-generated Javadoc
/**
 * Iterates an implementation in the rank order.
 *
 * @author mpelcat
 */
public class SchedulingOrderIterator extends ImplementationIterator {

  /** The abc. */
  IAbc abc = null;

  /**
   * Instantiates a new scheduling order iterator.
   *
   * @param implementation
   *          the implementation
   * @param abc
   *          the abc
   * @param directOrder
   *          the direct order
   */
  public SchedulingOrderIterator(final MapperDAG implementation, final IAbc abc, final boolean directOrder) {
    this.abc = abc;
    super.initParams(null, implementation, directOrder);
  }

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.preesm.plugin.mapper.tools.ImplantationIterator#compare(org. ietr.preesm.plugin.mapper.model.MapperDAGVertex,
   * org.ietr.preesm.plugin.mapper.model.MapperDAGVertex)
   */
  @Override
  public int compare(final MapperDAGVertex arg0, final MapperDAGVertex arg1) {
    final int dif = this.abc.getSchedTotalOrder(arg0) - this.abc.getSchedTotalOrder(arg1);

    // Preventing equal scheduling order element discard
    if (dif == 0) {
      WorkflowLogger.getLogger().log(Level.SEVERE, "Found two vertices with the same total order");
    }
    return (dif);
  }

}
