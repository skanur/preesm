package org.ietr.preesm.codegen.xtend.printer.c

import java.util.Date
import java.util.List
import org.ietr.preesm.codegen.xtend.model.codegen.Block
import org.ietr.preesm.codegen.xtend.model.codegen.Buffer
import org.ietr.preesm.codegen.xtend.model.codegen.CallBlock
import org.ietr.preesm.codegen.xtend.model.codegen.CoreBlock
import org.ietr.preesm.codegen.xtend.model.codegen.Delimiter
import org.ietr.preesm.codegen.xtend.model.codegen.Direction
import org.ietr.preesm.codegen.xtend.model.codegen.FunctionCall
import org.ietr.preesm.codegen.xtend.model.codegen.LoopBlock
import org.ietr.preesm.codegen.xtend.model.codegen.Semaphore
import org.ietr.preesm.codegen.xtend.model.codegen.SharedMemoryCommunication
import org.ietr.preesm.codegen.xtend.model.codegen.Variable

class C6678CPrinter extends CPrinter {
	
	override printCoreBlockHeader(CoreBlock block) '''
		/** 
		 * @file «block.name».c
		 * @generated by «this.class.simpleName»
		 * @date «new Date»
		 */
		
		#include "cores.h"
		#include "utils.h"
		#include "communication.h"
		#include "fifo.h"
		
		
	'''
	
	override printBufferDefinition(Buffer buffer) '''
	// Won't work if the shared memory is >= 512 MB 
	#pragma DATA_SECTION(«buffer.name», ".mySharedMem")
	«super.printBufferDefinition(buffer)»
	'''
	
	override printBufferDeclaration(Buffer buffer) '''
		extern «super.printBufferDefinition(buffer)»
	'''
	
	override printDeclarationsHeader(List<Variable> list) '''
	// Core Global Declaration
	
	'''
	override printCoreInitBlockHeader(CallBlock callBlock) '''
	void «(callBlock.eContainer as CoreBlock).name.toLowerCase»(void){
		// Initialisation(s)
		communicationInit();
		
	'''
	
	override printCoreLoopBlockHeader(LoopBlock block2) '''
		
		«"\t"»// Begin the execution loop 
			while(1){
				busy_barrier();
				
	'''
	
	override printSharedMemoryCommunication(SharedMemoryCommunication communication) '''
		«/*Since everything is already in shared memory, communications are simple synchronizations here*/
	»«communication.direction.toString.toLowerCase»«communication.delimiter.toString.toLowerCase.toFirstUpper»(«IF (communication.
			direction == Direction::SEND && communication.delimiter == Delimiter::START) ||
			(communication.direction == Direction::RECEIVE && communication.delimiter == Delimiter::END)»«{
			var coreName = if (communication.direction == Direction::SEND) {
					communication.receiveStart.coreContainer.name
				} else {
					communication.sendStart.coreContainer.name
				}
			coreName.charAt(coreName.length - 1)
		}»«ENDIF»); // «communication.sendStart.coreContainer.name» > «communication.receiveStart.coreContainer.name»: «communication.
			data.doSwitch» 
	'''
	
	override printSemaphoreDeclaration(Semaphore semaphore) ''''''
	
	override printSemaphoreDefinition(Semaphore semaphore) ''''''
	
	override printSemaphore(Semaphore semaphore) ''''''
	
	override preProcessing(List<Block> printerBlocks, List<Block> allBlocks) {
		super.preProcessing(printerBlocks, allBlocks)

		/** Remove semaphore init */
		for (block : printerBlocks) {
			(block as CoreBlock).initBlock.codeElts.removeAll(
				((block as CoreBlock).initBlock.codeElts.filter[
					(it instanceof FunctionCall && (it as FunctionCall).name.startsWith("sem_init"))]))

		}
	}
	
}
