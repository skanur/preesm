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

import java.util.HashSet;
import java.util.Set;

import org.ietr.preesm.core.architecture.ArchitectureComponent;
import org.ietr.preesm.core.architecture.ArchitectureComponentType;

/**
 * a Memory (exemple: a Shared RAM)
 * 
 * @author pmu
 */
public class Memory extends ArchitectureComponent implements ITerminal {

	/**
	 * ID used to reference the element in a property bean in case of a
	 * computation vertex
	 */
	public static final String propertyBeanName = "memory";

	/**
	 * Communication performers are communicators and processors that can access
	 * this processor.
	 */
	private Set<String> commPerformerNames;

	public Memory(String name, MemoryDefinition type) {
		super(name, type);
		commPerformerNames = new HashSet<String>();
	}

	public void addCommunicationPerformerName(String commPerformerName) {
		commPerformerNames.add(commPerformerName);
	}

	public Set<String> getCommunicationPerformerNames() {
		return commPerformerNames;
	}

	public ArchitectureComponentType getType() {
		return ArchitectureComponentType.memory;
	}

	@Override
	public ArchitectureComponent clone() {
		return new Memory(getName(), null);
	}

	public boolean isNode() {
		return true;
	}

}
