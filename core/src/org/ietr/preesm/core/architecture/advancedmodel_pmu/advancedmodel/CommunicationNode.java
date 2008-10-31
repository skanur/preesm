/*********************************************************
Copyright or � or Copr. IETR/INSA: Matthieu Wipliez, Jonathan Piat,
Maxime Pelcat, Peng Cheng Mu, Jean-Fran�ois Nezan, Micka�l Raulet

[mwipliez,jpiat,mpelcat,pmu,jnezan,mraulet]@insa-rennes.fr

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

package org.ietr.preesm.core.architecture.advancedmodel;

import java.util.Iterator;

/**
 * A CommunicationNode is a type of node used to connect different links for
 * communication. CommunicationNodes are interconnected by links.
 * 
 * @author pmu
 */
public class CommunicationNode extends AbstractNode {

	/**
	 * ID used to reference the element in a property bean in case of a
	 * communication vertex
	 */
	public static final String propertyBeanName = "communicationNode";

	public CommunicationNode(String name, CommunicationNodeDefinition definition) {
		super(name, definition);
	}

	public CommunicationNode clone() {

		// A new communication node is created  with the same definition.
		CommunicationNode newCommNode = new CommunicationNode(new String(this
				.getName()), this.getDefinition());
		// We iterate in interfaces
		Iterator<SpiritInterface> intfIt = this.availableInterfaces.iterator();
		while (intfIt.hasNext()) {
			SpiritInterface currentIntf = intfIt.next();
			SpiritInterface newIntf = currentIntf.clone();
			newIntf.setOwner(newCommNode);
			newCommNode.addInterface(newIntf);
		}
		return newCommNode;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof CommunicationNode) {
			CommunicationNode commNode = (CommunicationNode) obj;
			return this.getName().compareToIgnoreCase(commNode.getName()) == 0;
		}
		return false;
	}
	
	@Override
	public CommunicationNodeDefinition getDefinition() {
		return (CommunicationNodeDefinition) definition;
	}

}
