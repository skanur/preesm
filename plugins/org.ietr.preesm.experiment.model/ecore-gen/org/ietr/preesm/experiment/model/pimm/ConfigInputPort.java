/*******************************************************************************
 * Copyright or © or Copr. 2012 - 2017 IETR/INSA:
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017)
 * Clément Guy <clement.guy@insa-rennes.fr> (2014)
 * Julien Heulot <julien.heulot@insa-rennes.fr> (2013)
 * Karol Desnos <karol.desnos@insa-rennes.fr> (2012)
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
package org.ietr.preesm.experiment.model.pimm;

// TODO: Auto-generated Javadoc
/**
 * <!-- begin-user-doc --> A representation of the model object '<em><b>Config Input Port</b></em>'. <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 * <li>{@link org.ietr.preesm.experiment.model.pimm.ConfigInputPort#getIncomingDependency <em>Incoming Dependency</em>}</li>
 * </ul>
 *
 * @see org.ietr.preesm.experiment.model.pimm.PiMMPackage#getConfigInputPort()
 * @model
 * @generated
 */
public interface ConfigInputPort extends Port {

  /**
   * Returns the value of the '<em><b>Incoming Dependency</b></em>' reference. It is bidirectional and its opposite is
   * '{@link org.ietr.preesm.experiment.model.pimm.Dependency#getGetter <em>Getter</em>}'. <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Incoming Dependency</em>' reference isn't clear, there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   *
   * @return the value of the '<em>Incoming Dependency</em>' reference.
   * @see #setIncomingDependency(Dependency)
   * @see org.ietr.preesm.experiment.model.pimm.PiMMPackage#getConfigInputPort_IncomingDependency()
   * @see org.ietr.preesm.experiment.model.pimm.Dependency#getGetter
   * @model opposite="getter"
   * @generated
   */
  Dependency getIncomingDependency();

  /**
   * Sets the value of the '{@link org.ietr.preesm.experiment.model.pimm.ConfigInputPort#getIncomingDependency <em>Incoming Dependency</em>}' reference. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @param value
   *          the new value of the '<em>Incoming Dependency</em>' reference.
   * @see #getIncomingDependency()
   * @generated
   */
  void setIncomingDependency(Dependency value);
} // ConfigInputPort
