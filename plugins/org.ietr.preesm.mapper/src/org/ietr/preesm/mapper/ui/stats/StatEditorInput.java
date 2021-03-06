/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2008 - 2017) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017)
 * Clément Guy <clement.guy@insa-rennes.fr> (2014)
 * Karol Desnos <karol.desnos@insa-rennes.fr> (2015)
 * Maxime Pelcat <maxime.pelcat@insa-rennes.fr> (2008 - 2012)
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
package org.ietr.preesm.mapper.ui.stats;

import java.util.Map;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;
import org.ietr.dftools.workflow.tools.WorkflowLogger;
import org.ietr.preesm.core.scenario.PreesmScenario;
import org.ietr.preesm.mapper.abc.IAbc;
import org.ietr.preesm.mapper.abc.impl.latency.LatencyAbc;
import org.ietr.preesm.mapper.activator.Activator;

// TODO: Auto-generated Javadoc
/**
 * Input of the editor of implementation statistics.
 *
 * @author mpelcat
 */
public class StatEditorInput implements IEditorInput {

  /** The abc. */
  private IAbc abc = null;

  /** The scenario. */
  private PreesmScenario scenario = null;

  /** The params. */
  private Map<String, String> params = null;

  /**
   * Instantiates a new stat editor input.
   *
   * @param abc
   *          the abc
   * @param scenario
   *          the scenario
   * @param params
   *          the params
   */
  public StatEditorInput(final IAbc abc, final PreesmScenario scenario, final Map<String, String> params) {
    super();
    this.abc = abc;
    this.params = params;
    this.scenario = scenario;
  }

  /**
   * Gets the scenario.
   *
   * @return the scenario
   */
  public PreesmScenario getScenario() {
    return this.scenario;
  }

  /**
   * Gets the params.
   *
   * @return the params
   */
  public Map<String, String> getParams() {
    return this.params;
  }

  /**
   * Gets the abc.
   *
   * @return the abc
   */
  public IAbc getAbc() {
    return this.abc;
  }

  /**
   * Sets the abc.
   *
   * @param abc
   *          the new abc
   */
  public void setAbc(final IAbc abc) {
    this.abc = abc;
  }

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.ui.IEditorInput#exists()
   */
  @Override
  public boolean exists() {
    // TODO Auto-generated method stub
    return false;
  }

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.ui.IEditorInput#getImageDescriptor()
   */
  @Override
  public ImageDescriptor getImageDescriptor() {
    final ImageDescriptor img = Activator.getImageDescriptor("icons/preesm1mini.PNG");
    return img;
  }

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.ui.IEditorInput#getName()
   */
  @Override
  public String getName() {
    if (this.abc instanceof LatencyAbc) {
      this.abc.updateFinalCosts();
      return "Latency:" + ((LatencyAbc) this.abc).getFinalLatency() + " Cost:" + this.abc.getFinalCost() + " " + WorkflowLogger.getFormattedTime();
    } else {
      return "Stats " + WorkflowLogger.getFormattedTime();
    }
  }

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.ui.IEditorInput#getPersistable()
   */
  @Override
  public IPersistableElement getPersistable() {
    // TODO Auto-generated method stub
    return null;
  }

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.ui.IEditorInput#getToolTipText()
   */
  @Override
  public String getToolTipText() {
    return "Implementation";
  }

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
   */
  @SuppressWarnings({ "unchecked", "rawtypes" })
  @Override
  public Object getAdapter(final Class adapter) {
    return null;
  }

}
