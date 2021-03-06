/*******************************************************************************
 * Copyright or © or Copr. 2013 - 2017 IETR/INSA:
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017)
 * Clément Guy <clement.guy@insa-rennes.fr> (2015)
 * Karol Desnos <karol.desnos@insa-rennes.fr> (2013)
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
package org.ietr.preesm.codegen.xtend.model.codegen.impl;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.ietr.preesm.codegen.xtend.model.codegen.ActorBlock;
import org.ietr.preesm.codegen.xtend.model.codegen.CallBlock;
import org.ietr.preesm.codegen.xtend.model.codegen.CodeElt;
import org.ietr.preesm.codegen.xtend.model.codegen.CodegenFactory;
import org.ietr.preesm.codegen.xtend.model.codegen.CodegenPackage;
import org.ietr.preesm.codegen.xtend.model.codegen.LoopBlock;

// TODO: Auto-generated Javadoc
/**
 * <!-- begin-user-doc --> An implementation of the model object ' <em><b>Actor Block</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 * <li>{@link org.ietr.preesm.codegen.xtend.model.codegen.impl.ActorBlockImpl#getLoopBlock <em>Loop Block</em>}</li>
 * <li>{@link org.ietr.preesm.codegen.xtend.model.codegen.impl.ActorBlockImpl#getInitBlock <em>Init Block</em>}</li>
 * </ul>
 *
 * @generated
 */
public class ActorBlockImpl extends BlockImpl implements ActorBlock {
  /**
   * The cached value of the '{@link #getLoopBlock() <em>Loop Block</em>}' reference. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @see #getLoopBlock()
   * @generated
   * @ordered
   */
  protected LoopBlock loopBlock;
  /**
   * The cached value of the '{@link #getInitBlock() <em>Init Block</em>}' reference. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @see #getInitBlock()
   * @generated
   * @ordered
   */
  protected CallBlock initBlock;

  /**
   * <!-- begin-user-doc -->Default Constructor also create the init and loop blocks and add them to the {@link CodeElt} list<!-- end-user-doc -->.
   */
  protected ActorBlockImpl() {
    super();
    this.initBlock = CodegenFactory.eINSTANCE.createCallBlock();
    getCodeElts().add(this.initBlock);
    this.loopBlock = CodegenFactory.eINSTANCE.createLoopBlock();
    getCodeElts().add(this.loopBlock);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @return the e class
   * @generated
   */
  @Override
  protected EClass eStaticClass() {
    return CodegenPackage.Literals.ACTOR_BLOCK;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @return the loop block
   * @generated
   */
  @Override
  public LoopBlock getLoopBlock() {
    if ((this.loopBlock != null) && this.loopBlock.eIsProxy()) {
      final InternalEObject oldLoopBlock = (InternalEObject) this.loopBlock;
      this.loopBlock = (LoopBlock) eResolveProxy(oldLoopBlock);
      if (this.loopBlock != oldLoopBlock) {
        if (eNotificationRequired()) {
          eNotify(new ENotificationImpl(this, Notification.RESOLVE, CodegenPackage.ACTOR_BLOCK__LOOP_BLOCK, oldLoopBlock, this.loopBlock));
        }
      }
    }
    return this.loopBlock;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @return the loop block
   * @generated
   */
  public LoopBlock basicGetLoopBlock() {
    return this.loopBlock;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @param newLoopBlock
   *          the new loop block
   * @generated
   */
  @Override
  public void setLoopBlock(final LoopBlock newLoopBlock) {
    final LoopBlock oldLoopBlock = this.loopBlock;
    this.loopBlock = newLoopBlock;
    if (eNotificationRequired()) {
      eNotify(new ENotificationImpl(this, Notification.SET, CodegenPackage.ACTOR_BLOCK__LOOP_BLOCK, oldLoopBlock, this.loopBlock));
    }
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @return the inits the block
   * @generated
   */
  @Override
  public CallBlock getInitBlock() {
    if ((this.initBlock != null) && this.initBlock.eIsProxy()) {
      final InternalEObject oldInitBlock = (InternalEObject) this.initBlock;
      this.initBlock = (CallBlock) eResolveProxy(oldInitBlock);
      if (this.initBlock != oldInitBlock) {
        if (eNotificationRequired()) {
          eNotify(new ENotificationImpl(this, Notification.RESOLVE, CodegenPackage.ACTOR_BLOCK__INIT_BLOCK, oldInitBlock, this.initBlock));
        }
      }
    }
    return this.initBlock;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @return the call block
   * @generated
   */
  public CallBlock basicGetInitBlock() {
    return this.initBlock;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @param newInitBlock
   *          the new inits the block
   * @generated
   */
  @Override
  public void setInitBlock(final CallBlock newInitBlock) {
    final CallBlock oldInitBlock = this.initBlock;
    this.initBlock = newInitBlock;
    if (eNotificationRequired()) {
      eNotify(new ENotificationImpl(this, Notification.SET, CodegenPackage.ACTOR_BLOCK__INIT_BLOCK, oldInitBlock, this.initBlock));
    }
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @param featureID
   *          the feature ID
   * @param resolve
   *          the resolve
   * @param coreType
   *          the core type
   * @return the object
   * @generated
   */
  @Override
  public Object eGet(final int featureID, final boolean resolve, final boolean coreType) {
    switch (featureID) {
      case CodegenPackage.ACTOR_BLOCK__LOOP_BLOCK:
        if (resolve) {
          return getLoopBlock();
        }
        return basicGetLoopBlock();
      case CodegenPackage.ACTOR_BLOCK__INIT_BLOCK:
        if (resolve) {
          return getInitBlock();
        }
        return basicGetInitBlock();
    }
    return super.eGet(featureID, resolve, coreType);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @param featureID
   *          the feature ID
   * @param newValue
   *          the new value
   * @generated
   */
  @Override
  public void eSet(final int featureID, final Object newValue) {
    switch (featureID) {
      case CodegenPackage.ACTOR_BLOCK__LOOP_BLOCK:
        setLoopBlock((LoopBlock) newValue);
        return;
      case CodegenPackage.ACTOR_BLOCK__INIT_BLOCK:
        setInitBlock((CallBlock) newValue);
        return;
    }
    super.eSet(featureID, newValue);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @param featureID
   *          the feature ID
   * @generated
   */
  @Override
  public void eUnset(final int featureID) {
    switch (featureID) {
      case CodegenPackage.ACTOR_BLOCK__LOOP_BLOCK:
        setLoopBlock((LoopBlock) null);
        return;
      case CodegenPackage.ACTOR_BLOCK__INIT_BLOCK:
        setInitBlock((CallBlock) null);
        return;
    }
    super.eUnset(featureID);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @param featureID
   *          the feature ID
   * @return true, if successful
   * @generated
   */
  @Override
  public boolean eIsSet(final int featureID) {
    switch (featureID) {
      case CodegenPackage.ACTOR_BLOCK__LOOP_BLOCK:
        return this.loopBlock != null;
      case CodegenPackage.ACTOR_BLOCK__INIT_BLOCK:
        return this.initBlock != null;
    }
    return super.eIsSet(featureID);
  }

} // ActorBlockImpl
