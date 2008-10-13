package org.ietr.preesm.plugin.mapper.commcontenlistsched.scheduler;

import org.ietr.preesm.plugin.mapper.commcontenlistsched.descriptor.AlgorithmDescriptor;
import org.ietr.preesm.plugin.mapper.commcontenlistsched.descriptor.ArchitectureDescriptor;
import org.ietr.preesm.plugin.mapper.commcontenlistsched.descriptor.CommunicationDescriptor;
import org.ietr.preesm.plugin.mapper.commcontenlistsched.descriptor.LinkDescriptor;
import org.ietr.preesm.plugin.mapper.commcontenlistsched.descriptor.OperatorDescriptor;

/**
 * This class gives a classic communication contentious list scheduling method
 * with Communication Delay and nodes sorted by input/output bottom level.
 * 
 * @author pmu
 */
public class CListSchedCdBlinout extends CListSchedCd {

	public CListSchedCdBlinout(AlgorithmDescriptor algorithm,
			ArchitectureDescriptor architecture) {
		super(algorithm, architecture);
		// TODO Auto-generated constructor stub
		this.name = "Classic List Scheduling With Communication Delay And Nodes Sorted By Input/Output Bottom Level";
	}

	public boolean schedule() {
		System.out.println("\n***** schedule *****");
		algorithm.computeTopLevelInOut();
		algorithm.computeBottomLevelInOut();
		schedulingOrder = algorithm.sortComputationsByBottomLevelInOut();
		System.out.println("static scheduling order:");
		for (int i = 0; i < schedulingOrder.size(); i++) {
			System.out.println(" " + i + " -> "
					+ schedulingOrder.get(i).getName() + " (b-level-inout="
					+ schedulingOrder.get(i).getBottomLevelInOut()
					+ "; t-level-inout="
					+ schedulingOrder.get(i).getTopLevelInOut() + ")");
		}
		OperatorDescriptor bestOperator = null;
		for (OperatorDescriptor indexOperator : architecture.getAllOperators()
				.values()) {
			indexOperator.addReceiveCommunication(topCommunication);
			indexOperator.addSendCommunication(topCommunication);
			indexOperator.addOperation(topCommunication);
			indexOperator.addReceiveCommunication(bottomCommunication);
			indexOperator.addSendCommunication(bottomCommunication);
			indexOperator.addOperation(bottomCommunication);
			for (LinkDescriptor indexLink : indexOperator.getInputLinks()) {
				indexLink.addCommunication(topCommunication);
				indexLink.addCommunication(bottomCommunication);
			}
			for (LinkDescriptor indexLink : indexOperator.getOutputLinks()) {
				indexLink.addCommunication(topCommunication);
				indexLink.addCommunication(bottomCommunication);
			}
		}

		for (int i = 0; i < schedulingOrder.size(); i++) {
			System.out.println(i + ": schedule "
					+ schedulingOrder.get(i).getName());
			bestOperator = selectOperator(schedulingOrder.get(i));

			scheduleComputation(schedulingOrder.get(i), bestOperator);
			// schedulingOrder.get(i).setOperator(bestOperator);
			updateTimes();
			System.out.println(" bestOperator" + "->" + bestOperator.getId());
			System.out.println(" startTime" + "="
					+ schedulingOrder.get(i).getStartTime() + "; finishTime"
					+ "=" + schedulingOrder.get(i).getFinishTime());
			for (CommunicationDescriptor indexCommunication : schedulingOrder
					.get(i).getPrecedingCommunications()) {
				System.out.println(" preceding communication:"
						+ indexCommunication.getName() + " startTimeOnLink="
						+ indexCommunication.getStartTimeOnLink()
						+ "; finishTimeOnLink="
						+ indexCommunication.getFinishTimeOnLink() + "; ALAP="
						+ indexCommunication.getALAP());
			}
		}
		for (int i = 0; i < schedulingOrder.size(); i++) {
			scheduleLength = max(scheduleLength, schedulingOrder.get(i)
					.getFinishTime());
		}
		for (OperatorDescriptor indexOperator : architecture.getAllOperators()
				.values()) {
			if (indexOperator.getOperations().size() > 2) {
				usedOperators.add(indexOperator);
				indexOperator.setFinishTime(indexOperator
						.getOccupiedTimeInterval(
								indexOperator
										.getOperation(
												indexOperator.getOperations()
														.size() - 2).getName())
						.getFinishTime());
			}
		}
		return true;
	}
}