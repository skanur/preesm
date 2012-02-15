/*********************************************************
Copyright or � or Copr. IETR/INSA: Matthieu Wipliez, Jonathan Piat,
Maxime Pelcat, Jean-Fran�ois Nezan, Micka�l Raulet

[mwipliez,jpiat,mpelcat,jnezan,mraulet]@insa-rennes.fr

This software is a computer program whose purpose is to prototype
parallel applications.

This software is governed by the CeCILL-C license under French law and
abiding by the rules of distribution of free software.  You can  use, 
modify and/ or redistribute the software under the terms of the CeCILL-C
license as circulated by CEA, CNRS and INRIA at the following URL
"http://www.cecill.info". 

As a counterpart to the access to the source code and  rights to copy,
modify and redistribute granted by the license, users are provided only
with a limited warranty  and the software's author,  the holder of the
economic rights,  and the successive licensors  have only  limited
liability. 

In this respect, the user's attention is drawn to the risks associated
with loading,  using,  modifying and/or developing or reproducing the
software by the user in light of its specific status of free software,
that may mean  that it is complicated to manipulate,  and  that  also
therefore means  that it is reserved for developers  and  experienced
professionals having in-depth computer knowledge. Users are therefore
encouraged to load and test the software's suitability as regards their
requirements in conditions enabling the security of their systems and/or 
data to be ensured and,  more generally, to use and operate it in the 
same conditions as regards security. 

The fact that you are presently reading this means that you have had
knowledge of the CeCILL-C license and that you accept its terms.
 *********************************************************/

package org.ietr.preesm.core.codegen.sdfProperties;

import org.ietr.preesm.core.codegen.types.DataType;

/**
 * Objects used to tag the DAG edges. The buffer couple definition is
 * transmitted to the code generation. One buffer is created for the source and
 * one for the destination.
 * 
 * @author mpelcat
 * 
 */
public class BufferProperties {

	private DataType dataType;

	private String destInputPortID;

	private int size;

	private String sourceOutputPortID;

	public BufferProperties(DataType dataType, String sourceOutputPortID,
			String destInputPortID, int size) {
		super();
		this.dataType = dataType;
		this.destInputPortID = destInputPortID;
		this.size = size;
		this.sourceOutputPortID = sourceOutputPortID;
	}

	public String getDataType() {
		String typeName = "";
		if (dataType == null) {
			typeName = "typeNotFound";
		} else {
			typeName = dataType.getTypeName();
		}
		return typeName;
	}

	public String getDestInputPortID() {
		return destInputPortID;
	}

	public int getSize() {
		return size;
	}

	public String getSourceOutputPortID() {
		return sourceOutputPortID;
	}

}