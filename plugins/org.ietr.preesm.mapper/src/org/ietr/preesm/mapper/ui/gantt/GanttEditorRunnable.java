/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2008 - 2017) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017)
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
package org.ietr.preesm.mapper.ui.gantt;

import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.ietr.preesm.mapper.gantt.GanttData;

// TODO: Auto-generated Javadoc
/**
 * Class used by the editor displaying the gantt chart. Useful to run editor in display thread.
 *
 * @author mpelcat
 */
public class GanttEditorRunnable implements Runnable {

  /** The input. */
  private final IEditorInput input;

  /**
   * Instantiates a new gantt editor runnable.
   *
   * @param input
   *          the input
   */
  public GanttEditorRunnable(final IEditorInput input) {
    super();
    this.input = input;
  }

  /**
   * Run a new editor in display thread to display the Gantt chart from given ABC.
   *
   * @param ganttData
   *          the gantt data
   * @param name
   *          the name
   */
  public static void run(final GanttData ganttData, final String name) {

    final IEditorInput input = new GanttEditorInput(ganttData, name);

    PlatformUI.getWorkbench().getDisplay().asyncExec(new GanttEditorRunnable(input));
  }

  /*
   * (non-Javadoc)
   *
   * @see java.lang.Runnable#run()
   */
  @Override
  public void run() {

    final IWorkbenchWindow dwindow = PlatformUI.getWorkbench().getWorkbenchWindows()[0];

    if ((dwindow != null) && (this.input instanceof GanttEditorInput)) {
      final IWorkbenchPage page = dwindow.getActivePage();

      try {
        page.openEditor(this.input, "org.ietr.preesm.plugin.mapper.plot.GanttEditor", false);

      } catch (final PartInitException e) {
        e.printStackTrace();
      }
    }
  }

}
