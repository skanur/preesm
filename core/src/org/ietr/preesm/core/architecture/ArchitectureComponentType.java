/**
 * 
 */
package org.ietr.preesm.core.architecture;

/**
 * Representation of an architecture component type
 * 
 * @author mpelcat
 */
public class ArchitectureComponentType {
	
	public static final ArchitectureComponentType operator = new ArchitectureComponentType("operator");
	public static final ArchitectureComponentType medium = new ArchitectureComponentType("medium");
	public static final ArchitectureComponentType communicationNode = new ArchitectureComponentType("communicationNode");
	public static final ArchitectureComponentType communicator = new ArchitectureComponentType("communicator");
	public static final ArchitectureComponentType bus = new ArchitectureComponentType("bus");
	public static final ArchitectureComponentType memory = new ArchitectureComponentType("memory");
	public static final ArchitectureComponentType processor = new ArchitectureComponentType("processor");
	public static final ArchitectureComponentType ipCoprocessor = new ArchitectureComponentType("ipCoprocessor");
	public static final ArchitectureComponentType fifo = new ArchitectureComponentType("fifo");
	
	private String name = null;

	public ArchitectureComponentType(String name) {
		super();
		this.name = name;
	}

	public static ArchitectureComponentType getType(String name) {
		if(name.equalsIgnoreCase("operator")) 				return operator;
		else if(name.equalsIgnoreCase("medium")) 			return medium;
		else if(name.equalsIgnoreCase("communicationNode")) return communicationNode;
		else if(name.equalsIgnoreCase("communicator")) 		return communicator;
		else if(name.equalsIgnoreCase("bus")) 				return bus;
		else if(name.equalsIgnoreCase("memory")) 			return memory;
		else if(name.equalsIgnoreCase("processor"))			return processor;
		else if(name.equalsIgnoreCase("ipCoprocessor"))		return ipCoprocessor;
		else if(name.equalsIgnoreCase("fifo"))	 			return fifo;
		else return null;
	}
}