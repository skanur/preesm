/*******************************************************************************
 * Copyright or © or Copr. 2015 - 2017 IETR/INSA:
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017)
 * blaunay <bapt.launay@gmail.com> (2015)
 *
 * This software is a computer program whose purpose is to prototype
 * parallel applications.
 *
 * This software is governed by the CeCILL-C license under French law and
 * abiding by the rules of distribution of free software.  You can  use
 * modify and/ or redistribute the software under the terms of the CeCILL-C
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
 * knowledge of the CeCILL-C license and that you accept its terms.
 *******************************************************************************/
package org.ietr.preesm.evaluator.test;

import java.io.IOException;
import org.ietr.dftools.algorithm.model.parameters.InvalidExpressionException;
import org.ietr.dftools.algorithm.model.visitors.SDF4JException;
import org.ietr.preesm.evaluator.IBSDFGenerator;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Generator of hierarchical graphs (IBSDF) from alive SDF graphs generated with <a href="https://github.com/bbodin/turbine/">Turbine</a>, the only parameter to
 * give is the number of actors. Used independently from the throughput evaluator to create instances for test
 *
 * @author blaunay
 *
 */
public class IBSDFGeneratorTest {

  /**
   * Main method for tests and generate IBSDF graphs
   */
  @Test
  /*
   * The tested class requires system specific configuration (turbine installed and path configured ...) TODO: ask Hamza Deroui about it
   */
  @Ignore
  public void testGenerator() throws IOException, InterruptedException, SDF4JException, InvalidExpressionException {
    final IBSDFGenerator x = new IBSDFGenerator(50);
    x.graphSet_gen();
    x.hierarchize();

  }
}
