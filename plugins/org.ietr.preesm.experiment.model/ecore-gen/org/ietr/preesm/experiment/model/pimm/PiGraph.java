/*******************************************************************************
 * Copyright or © or Copr. 2013 - 2017 IETR/INSA:
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017)
 * Clément Guy <clement.guy@insa-rennes.fr> (2014)
 * Julien Heulot <julien.heulot@insa-rennes.fr> (2013 - 2014)
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

import org.eclipse.emf.common.util.EList;

// TODO: Auto-generated Javadoc
/**
 * <!-- begin-user-doc --> A representation of the model object ' <em><b>Graph</b></em>'. <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 * <li>{@link org.ietr.preesm.experiment.model.pimm.PiGraph#getVertices <em>Vertices</em>}</li>
 * <li>{@link org.ietr.preesm.experiment.model.pimm.PiGraph#getFifos <em>Fifos</em>}</li>
 * <li>{@link org.ietr.preesm.experiment.model.pimm.PiGraph#getParameters <em>Parameters</em>}</li>
 * <li>{@link org.ietr.preesm.experiment.model.pimm.PiGraph#getDependencies <em>Dependencies</em>}</li>
 * </ul>
 *
 * @see org.ietr.preesm.experiment.model.pimm.PiMMPackage#getPiGraph()
 * @model
 * @generated
 */
public interface PiGraph extends AbstractActor {
  /**
   * Returns the value of the '<em><b>Vertices</b></em>' containment reference list. The list contents are of type
   * {@link org.ietr.preesm.experiment.model.pimm.AbstractActor}. <!-- begin-user-doc -->
   * <p>
   * <b>Do not use this method when adding an Interface to the graph</b> (i.e. a {@link InterfaceActor} or an interface {@link Parameter}) <br>
   * Use {@link PiGraph#addInterface(AbstractVertex) and Graph#removeInterfaceActor(InterfaceActor)} instead.
   * </p>
   * <p>
   * If the meaning of the '<em>Vertices</em>' containment reference list isn't clear, there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   *
   * @return the value of the '<em>Vertices</em>' containment reference list.
   * @see org.ietr.preesm.experiment.model.pimm.PiMMPackage#getPiGraph_Vertices()
   * @model containment="true"
   * @generated
   */
  EList<AbstractActor> getVertices();

  /**
   * Returns the value of the '<em><b>Fifos</b></em>' containment reference list. The list contents are of type
   * {@link org.ietr.preesm.experiment.model.pimm.Fifo}. <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Fifos</em>' containment reference list isn't clear, there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   *
   * @return the value of the '<em>Fifos</em>' containment reference list.
   * @see org.ietr.preesm.experiment.model.pimm.PiMMPackage#getPiGraph_Fifos()
   * @model containment="true"
   * @generated
   */
  EList<Fifo> getFifos();

  /**
   * Returns the value of the '<em><b>Parameters</b></em>' containment reference list. The list contents are of type
   * {@link org.ietr.preesm.experiment.model.pimm.Parameter}. <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Parameters</em>' containment reference list isn't clear, there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   *
   * @return the value of the '<em>Parameters</em>' containment reference list.
   * @see org.ietr.preesm.experiment.model.pimm.PiMMPackage#getPiGraph_Parameters()
   * @model containment="true"
   * @generated
   */
  EList<Parameter> getParameters();

  /**
   * Returns the value of the '<em><b>Dependencies</b></em>' containment reference list. The list contents are of type
   * {@link org.ietr.preesm.experiment.model.pimm.Dependency}. <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Dependencies</em>' containment reference list isn't clear, there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   *
   * @return the value of the '<em>Dependencies</em>' containment reference list.
   * @see org.ietr.preesm.experiment.model.pimm.PiMMPackage#getPiGraph_Dependencies()
   * @model containment="true"
   * @generated
   */
  EList<Dependency> getDependencies();

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @model kind="operation" annotation="http://www.eclipse.org/emf/2002/GenModel body='return
   *        ECollections.newBasicEList(getVertices().stream().map(AbstractActor::getName).collect(Collectors.toList()));'"
   * @generated
   */
  EList<String> getVerticesNames();

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @model kind="operation" annotation="http://www.eclipse.org/emf/2002/GenModel body='return
   *        ECollections.newBasicEList(getParameters().stream().map(Parameter::getName).collect(Collectors.toList()));'"
   * @generated
   */
  EList<String> getParametersNames();

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @model kind="operation" annotation="http://www.eclipse.org/emf/2002/GenModel body='return
   *        ECollections.newBasicEList(getVertices().stream().filter(Actor.class::isInstance).map(Actor.class::cast).collect(Collectors.toList()));'"
   * @generated
   */
  EList<Actor> getActors();

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @model kind="operation" annotation="http://www.eclipse.org/emf/2002/GenModel body='final EList&lt;Parameter&gt; result = ECollections.newBasicEList();\nfor
   *        (final AbstractActor aa : getVertices()) {\n if (aa instanceof PiGraph) {\n result.addAll(((PiGraph) aa).getAllParameters());\n } else if (aa
   *        instanceof Actor) {\n final Refinement refinement = ((Actor) aa).getRefinement();\n if (refinement != null) {\n final AbstractActor subGraph =
   *        refinement.getAbstractActor();\n if ((subGraph != null) &amp;&amp; (subGraph instanceof PiGraph)) {\n result.addAll(((PiGraph)
   *        subGraph).getAllParameters());\n }\n }\n }\n}\nresult.addAll(getParameters());\nreturn result;'"
   * @generated
   */
  EList<Parameter> getAllParameters();

  /**
   * Return the {@link AbstractVertex} ( {@link AbstractActor} or {@link Parameter}) whose name is given as a parameter.
   *
   * @param name
   *          the desired name
   * @return the desired vertex, or <code>null</code> if no vertex has the requested name.
   */
  public AbstractVertex getVertexNamed(String name);

  /**
   * Return the {@link Fifo} whose ID is given as a parameter.
   *
   * @param id
   *          the desired id
   * @return the desired {@link Fifo}, or <code>null</code> if no vertex has the requested name.
   */
  public Fifo getFifoIded(String id);

  /**
   * Gets the hierarchical actor from path.
   *
   * @param path
   *          the path
   * @return the hierarchical actor from path
   */
  public AbstractActor getHierarchicalActorFromPath(String path);

  /**
   * Gets the parameter named with parent.
   *
   * @param name
   *          the name of the Parameter we are looking for
   * @param parent
   *          the parent
   * @return the first Parameter with the given name found in the graph hierarchy, null if none is found
   */
  public Parameter getParameterNamedWithParent(String name, String parent);

  /**
   * Gets the all vertices.
   *
   * @return the set of all the actors contained by the graph and its subgraphs
   */
  public EList<AbstractActor> getAllVertices();

} // Graph
