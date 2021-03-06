/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2008 - 2017) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017)
 * Matthieu Wipliez <matthieu.wipliez@insa-rennes.fr> (2008)
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
package org.ietr.preesm.core.types;

// TODO: Auto-generated Javadoc
/**
 * Objects used to tag the DAG edges. The buffer couple definition is transmitted to the code generation. One buffer is created for the source and one for the
 * destination.
 *
 * @author mpelcat
 *
 */
public class BufferProperties {

  /** The data type. */
  private final DataType dataType;

  /** The dest input port ID. */
  private final String destInputPortID;

  /** The size. */
  private final int size;

  /** The source output port ID. */
  private final String sourceOutputPortID;

  /**
   * Instantiates a new buffer properties.
   *
   * @param dataType
   *          the data type
   * @param sourceOutputPortID
   *          the source output port ID
   * @param destInputPortID
   *          the dest input port ID
   * @param size
   *          the size
   */
  public BufferProperties(final DataType dataType, final String sourceOutputPortID, final String destInputPortID, final int size) {
    super();
    this.dataType = dataType;
    this.destInputPortID = destInputPortID;
    this.size = size;
    this.sourceOutputPortID = sourceOutputPortID;
  }

  /**
   * Gets the data type.
   *
   * @return the data type
   */
  public String getDataType() {
    String typeName = "";
    if (this.dataType == null) {
      typeName = "typeNotFound";
    } else {
      typeName = this.dataType.getTypeName();
    }
    return typeName;
  }

  /**
   * Gets the dest input port ID.
   *
   * @return the dest input port ID
   */
  public String getDestInputPortID() {
    return this.destInputPortID;
  }

  /**
   * Gets the size.
   *
   * @return the size
   */
  public int getSize() {
    return this.size;
  }

  /**
   * Gets the source output port ID.
   *
   * @return the source output port ID
   */
  public String getSourceOutputPortID() {
    return this.sourceOutputPortID;
  }

}
