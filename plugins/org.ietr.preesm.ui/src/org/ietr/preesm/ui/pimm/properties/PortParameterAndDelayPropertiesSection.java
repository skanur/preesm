/*******************************************************************************
 * Copyright or © or Copr. 2015 - 2017 IETR/INSA:
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017)
 * Clément Guy <clement.guy@insa-rennes.fr> (2015)
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
package org.ietr.preesm.ui.pimm.properties;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.graphiti.features.ILayoutFeature;
import org.eclipse.graphiti.features.context.impl.CustomContext;
import org.eclipse.graphiti.features.context.impl.LayoutContext;
import org.eclipse.graphiti.features.custom.ICustomFeature;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.views.properties.tabbed.ITabbedPropertyConstants;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetWidgetFactory;
import org.ietr.preesm.experiment.model.pimm.DataInputInterface;
import org.ietr.preesm.experiment.model.pimm.DataOutputInterface;
import org.ietr.preesm.experiment.model.pimm.DataPort;
import org.ietr.preesm.experiment.model.pimm.Delay;
import org.ietr.preesm.experiment.model.pimm.Expression;
import org.ietr.preesm.experiment.model.pimm.Fifo;
import org.ietr.preesm.experiment.model.pimm.InterfaceActor;
import org.ietr.preesm.experiment.model.pimm.Parameter;
import org.ietr.preesm.experiment.model.pimm.PortMemoryAnnotation;
import org.ietr.preesm.ui.pimm.features.SetPortMemoryAnnotationFeature;

// TODO: Auto-generated Javadoc
/**
 * The Class PortParameterAndDelayPropertiesSection.
 *
 * @author Romina Racca
 * @author jheulot
 */
public class PortParameterAndDelayPropertiesSection extends DataPortPropertiesUpdater implements ITabbedPropertyConstants {

  /** The lbl name. */
  private CLabel lblName;

  /** The lbl name obj. */
  private CLabel lblNameObj;

  /** The lbl expression. */
  private CLabel lblExpression;

  /** The lbl value. */
  private CLabel lblValue;

  /** The lbl value obj. */
  private CLabel lblValueObj;

  /** The lbl annotation. */
  private CLabel lblAnnotation;

  /** The combo annotation. */
  private CCombo comboAnnotation;

  /** The first column width. */
  private final int FIRST_COLUMN_WIDTH = 200;

  /** A text expression can be as an expression: value numbers, trigonometric functions, expression of condition "if (cond, true value, false value)". */
  private Text txtExpression;

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.ui.views.properties.tabbed.AbstractPropertySection#createControls(org.eclipse.swt.widgets.Composite,
   * org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage)
   */
  @Override
  public void createControls(final Composite parent, final TabbedPropertySheetPage tabbedPropertySheetPage) {

    super.createControls(parent, tabbedPropertySheetPage);

    final TabbedPropertySheetWidgetFactory factory = getWidgetFactory();
    final Composite composite = factory.createFlatFormComposite(parent);
    FormData data;

    /**** NAME ****/
    this.lblNameObj = factory.createCLabel(composite, " ");
    data = new FormData();
    data.left = new FormAttachment(0, this.FIRST_COLUMN_WIDTH);
    data.right = new FormAttachment(100, 0);
    this.lblNameObj.setLayoutData(data);

    this.lblName = factory.createCLabel(composite, "Name:");
    data = new FormData();
    data.left = new FormAttachment(0, 0);
    data.right = new FormAttachment(this.lblNameObj, -ITabbedPropertyConstants.HSPACE);
    this.lblName.setLayoutData(data);

    /**** EXPRESION ****/
    this.txtExpression = factory.createText(composite, "");
    data = new FormData();
    data.left = new FormAttachment(0, this.FIRST_COLUMN_WIDTH);
    data.right = new FormAttachment(100, 0);
    data.top = new FormAttachment(this.lblNameObj);
    this.txtExpression.setLayoutData(data);
    this.txtExpression.setEnabled(true);

    this.lblExpression = factory.createCLabel(composite, "Expression:");
    data = new FormData();
    data.left = new FormAttachment(0, 0);
    data.right = new FormAttachment(this.txtExpression, -ITabbedPropertyConstants.HSPACE);
    data.top = new FormAttachment(this.lblName);
    this.lblExpression.setLayoutData(data);

    /**** VALUE ****/
    this.lblValueObj = factory.createCLabel(composite, "");
    data = new FormData();
    data.left = new FormAttachment(0, this.FIRST_COLUMN_WIDTH);
    data.right = new FormAttachment(100, 0);
    data.top = new FormAttachment(this.txtExpression);
    this.lblValueObj.setLayoutData(data);

    this.lblValue = factory.createCLabel(composite, "Default Value:");
    data = new FormData();
    data.left = new FormAttachment(0, 0);
    data.right = new FormAttachment(this.lblValueObj, -ITabbedPropertyConstants.HSPACE);
    data.top = new FormAttachment(this.lblExpression);
    this.lblValue.setLayoutData(data);

    /**** MEMORY ANNOTATION ****/
    this.comboAnnotation = factory.createCCombo(composite);
    for (final PortMemoryAnnotation pma : PortMemoryAnnotation.values()) {
      this.comboAnnotation.add(pma.toString(), pma.getValue());
    }

    data = new FormData();
    data.left = new FormAttachment(0, this.FIRST_COLUMN_WIDTH);
    data.right = new FormAttachment(25, 0);
    data.top = new FormAttachment(this.lblValueObj);
    this.comboAnnotation.setLayoutData(data);

    this.lblAnnotation = factory.createCLabel(composite, "Memory Annotation:");
    data = new FormData();
    data.left = new FormAttachment(0, 0);
    data.right = new FormAttachment(this.comboAnnotation, -ITabbedPropertyConstants.HSPACE);
    data.top = new FormAttachment(this.lblValue);
    this.lblAnnotation.setLayoutData(data);

    this.txtExpression.addModifyListener(e -> updateProperties());

    this.comboAnnotation.addSelectionListener(new SelectionListener() {

      @Override
      public void widgetSelected(final SelectionEvent e) {
        final PictogramElement[] pes = new PictogramElement[1];
        pes[0] = getSelectedPictogramElement();

        final CustomContext context = new CustomContext(pes);
        final ICustomFeature[] setPotMemoryAnnotationFeature = getDiagramTypeProvider().getFeatureProvider().getCustomFeatures(context);

        for (final ICustomFeature feature : setPotMemoryAnnotationFeature) {
          if (feature instanceof SetPortMemoryAnnotationFeature) {
            PortMemoryAnnotation pma = null;
            switch (((CCombo) e.getSource()).getSelectionIndex()) {
              case PortMemoryAnnotation.READ_ONLY_VALUE:
                pma = PortMemoryAnnotation.READ_ONLY;
                break;
              case PortMemoryAnnotation.WRITE_ONLY_VALUE:
                pma = PortMemoryAnnotation.WRITE_ONLY;
                break;
              case PortMemoryAnnotation.UNUSED_VALUE:
                pma = PortMemoryAnnotation.UNUSED;
                break;
              default:
                break;

            }
            ((SetPortMemoryAnnotationFeature) feature).setCurrentPMA(pma);

            getDiagramTypeProvider().getDiagramBehavior().executeFeature(feature, context);
            final LayoutContext contextLayout = new LayoutContext(getSelectedPictogramElement());
            final ILayoutFeature layoutFeature = getDiagramTypeProvider().getFeatureProvider().getLayoutFeature(contextLayout);
            getDiagramTypeProvider().getDiagramBehavior().executeFeature(layoutFeature, contextLayout);
          }
        }

        refresh();
      }

      @Override
      public void widgetDefaultSelected(final SelectionEvent e) {
      }
    });

  }

  /**
   * Update the {@link Port}/{@link Delay}/{@link Parameter} {@link Expression} with the value stored in the txtEpression.
   */
  private void updateProperties() {
    final PictogramElement pe = getSelectedPictogramElement();
    if (pe != null) {
      final EObject bo = Graphiti.getLinkService().getBusinessObjectForLinkedPictogramElement(pe);
      if (bo == null) {
        return;
      }

      if (bo instanceof Parameter) {
        final Parameter param = (Parameter) bo;
        if (param.getExpression().getString().compareTo(this.txtExpression.getText()) != 0) {
          setNewExpression(param.getExpression(), this.txtExpression.getText());
          getDiagramTypeProvider().getDiagramBehavior().refreshRenderingDecorators(pe);
        }
      } // end Parameter

      if (bo instanceof DataPort) {
        final DataPort port = (DataPort) bo;
        updateDataPortProperties(port, this.txtExpression);

        getDiagramTypeProvider().getDiagramBehavior().refreshRenderingDecorators((PictogramElement) (pe.eContainer()));
      } // end DataPort

      if (bo instanceof DataPort) {
        this.comboAnnotation.setEnabled(false);
        this.comboAnnotation.select(((DataPort) bo).getAnnotation().getValue());
        this.comboAnnotation.setVisible(true);
        this.comboAnnotation.setEnabled(true);
        this.lblAnnotation.setEnabled(false);
        this.lblAnnotation.setVisible(true);
        this.lblAnnotation.setEnabled(true);
      } else {
        this.comboAnnotation.setEnabled(false);
        this.comboAnnotation.setVisible(false);
        this.comboAnnotation.setEnabled(true);
        this.lblAnnotation.setEnabled(false);
        this.lblAnnotation.setVisible(false);
        this.lblAnnotation.setEnabled(true);
      }

      if (bo instanceof Delay) {
        final Delay delay = (Delay) bo;
        if (delay.getExpression().getString().compareTo(this.txtExpression.getText()) != 0) {
          setNewExpression(delay.getExpression(), this.txtExpression.getText());
          getDiagramTypeProvider().getDiagramBehavior().refreshRenderingDecorators(pe);
        }
      } // end Delay
    }
    refresh();
  }

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.ui.views.properties.tabbed.AbstractPropertySection#refresh()
   */
  @Override
  public void refresh() {
    final PictogramElement pe = getSelectedPictogramElement();
    String name = null;
    Expression e = null;
    final boolean expressionFocus = this.txtExpression.isFocusControl();
    final Point sel = this.txtExpression.getSelection();
    this.txtExpression.setEnabled(false);

    if (pe != null) {
      final Object bo = Graphiti.getLinkService().getBusinessObjectForLinkedPictogramElement(pe);
      if (bo == null) {
        return;
      }

      if (bo instanceof Parameter) {
        name = ((Parameter) bo).getName();
        e = ((Parameter) bo).getExpression();
      } // end Parameter

      // if (bo instanceof DataOutputPort) {
      // DataOutputPort oPort = ((DataOutputPort) bo);
      //
      // if (oPort.eContainer() instanceof DataInputInterface) {
      // name = ((DataInputInterface) oPort.eContainer()).getName();
      // } else {
      // name = oPort.getName();
      // }
      //
      // e = oPort.getExpression();
      // }// end OutputPort

      if (bo instanceof DataPort) {
        final DataPort iPort = ((DataPort) bo);

        this.comboAnnotation.select(((DataPort) bo).getAnnotation().getValue());

        if (iPort.eContainer() instanceof DataOutputInterface) {
          name = ((DataOutputInterface) iPort.eContainer()).getName();
        } else {
          name = iPort.getName();
        }

        e = iPort.getExpression();

      } // end InputPort

      if (bo instanceof InterfaceActor) {
        final InterfaceActor iface = ((InterfaceActor) bo);
        name = iface.getName();

        if (iface instanceof DataInputInterface) {
          e = iface.getDataOutputPorts().get(0).getExpression();
        } else if (iface instanceof DataOutputInterface) {
          e = iface.getDataInputPorts().get(0).getExpression();
        } else {
          e = null;
        }

      } // end InterfaceActor

      if (bo instanceof Delay) {
        if (((Delay) bo).eContainer() instanceof Fifo) {
          final Fifo fifo = (Fifo) ((Delay) bo).eContainer();
          name = fifo.getId();
          e = fifo.getDelay().getExpression();
        }
      } // end Delay

      this.lblNameObj.setText(name == null ? " " : name);
      if (e != null) {
        if (!(bo instanceof InterfaceActor)) {
          this.txtExpression.setEnabled(true);
        }

        if (this.txtExpression.getText().compareTo(e.getString()) != 0) {
          this.txtExpression.setText(e.getString());
        }

        this.lblValueObj.setText(e.evaluate());

        if (expressionFocus) {
          this.txtExpression.setFocus();
          this.txtExpression.setSelection(sel);
        }
      }
    }
  }
}
