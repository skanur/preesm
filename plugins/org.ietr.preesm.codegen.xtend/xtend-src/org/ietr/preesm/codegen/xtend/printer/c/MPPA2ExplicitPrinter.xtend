/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2013 - 2017) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017)
 * Julien_Hascoet <jhascoet@kalray.eu> (2016 - 2017)
 * Karol Desnos <karol.desnos@insa-rennes.fr> (2013 - 2015)
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
package org.ietr.preesm.codegen.xtend.printer.c

import java.util.ArrayList
import java.util.Date
import java.util.List
import org.ietr.preesm.codegen.xtend.model.codegen.Buffer
import org.ietr.preesm.codegen.xtend.model.codegen.CallBlock
import org.ietr.preesm.codegen.xtend.model.codegen.Communication
import org.ietr.preesm.codegen.xtend.model.codegen.Constant
import org.ietr.preesm.codegen.xtend.model.codegen.ConstantString
import org.ietr.preesm.codegen.xtend.model.codegen.CoreBlock
import org.ietr.preesm.codegen.xtend.model.codegen.Delimiter
import org.ietr.preesm.codegen.xtend.model.codegen.Direction
import org.ietr.preesm.codegen.xtend.model.codegen.FifoCall
import org.ietr.preesm.codegen.xtend.model.codegen.FifoOperation
import org.ietr.preesm.codegen.xtend.model.codegen.FiniteLoopBlock
import org.ietr.preesm.codegen.xtend.model.codegen.FunctionCall
import org.ietr.preesm.codegen.xtend.model.codegen.LoopBlock
import org.ietr.preesm.codegen.xtend.model.codegen.NullBuffer
import org.ietr.preesm.codegen.xtend.model.codegen.SharedMemoryCommunication
import org.ietr.preesm.codegen.xtend.model.codegen.SpecialCall
import org.ietr.preesm.codegen.xtend.model.codegen.SubBuffer
import org.ietr.preesm.codegen.xtend.model.codegen.Variable
import org.ietr.preesm.codegen.xtend.task.CodegenException

class MPPA2ExplicitPrinter extends CPrinter {

	/**
	 * Temporary global var to ignore the automatic suppression of memcpy
	 * whose target and destination are identical.
	 */
	protected boolean IGNORE_USELESS_MEMCPY = true

	protected String local_buffer = "local_buffer"

	protected boolean IS_HIERARCHICAL = false

	protected String scratch_pad_buffer = ""

	protected int local_buffer_size = 0

	override printCoreBlockHeader(CoreBlock block) '''
		/**
		 * @file «block.name».c
		 * @generated by «this.class.simpleName»
		 * @date «new Date»
		 */

		/* system includes */
		#include <stdlib.h>
		#include <stdio.h>
		#include <stdint.h>
		#include <mOS_vcore_u.h>
		#include <mppa_noc.h>
		#include <mppa_rpc.h>
		#include <mppa_async.h>
		#include <pthread.h>
		#include <semaphore.h>
		#ifndef __nodeos__
		#include <utask.h>
		#endif

		/* user includes */
		#include "preesm.h"

		/* local Core variables */
		#ifdef PROFILE
		#define DUMP_MAX_TIME 128
		static uint64_t timestamp[NB_CORE][DUMP_MAX_TIME]; /* 4KB of data */
		static int current_dump[NB_CORE] = { 0 };
		#define getTimeProfile() if(current_dump[__k1_get_cpu_id()] < DUMP_MAX_TIME) \
								timestamp[__k1_get_cpu_id()][current_dump[__k1_get_cpu_id()]] = __k1_read_dsu_timestamp(); \
								current_dump[__k1_get_cpu_id()]++;
		#endif

		extern long long total_get_cycles[];
		extern long long total_put_cycles[];
		extern mppa_async_segment_t shared_segment;

		/* Scratchpad buffer ptr (will be malloced) */
		char *local_buffer __attribute__((unused)) = NULL;
		/* Scratchpad buffer size */
		int local_buffer_size __attribute__((unused)) = 0;

	'''

	override printBufferDefinition(Buffer buffer) '''
		«IF buffer.name == "Shared"»
		//#define Shared ((char*)0x10000000ULL) 	/* Shared buffer in DDR */
		«ELSE»
		«buffer.type» «buffer.name»[«buffer.size»] __attribute__ ((aligned(64))); // «buffer.comment» size:= «buffer.size»*«buffer.type» aligned on data cache line
		«ENDIF»
	'''

	override printDefinitionsHeader(List<Variable> list) '''
	«IF !list.empty»
		// Core Global Definitions

	«ENDIF»
	'''

	override printSubBufferDefinition(SubBuffer buffer) '''
	«buffer.type» *const «buffer.name» = («buffer.type»*) («var offset = 0»«
	{offset = buffer.offset
	 var b = buffer.container;
	 while(b instanceof SubBuffer){
	 	offset = offset + b.offset
	  	b = b.container
	  }
	 b}.name»+«offset»);  // «buffer.comment» size:= «buffer.size»*«buffer.type»
	'''

	override printFiniteLoopBlockHeader(FiniteLoopBlock block2) '''
	«{
	 	IS_HIERARCHICAL = true
	"\t"}»// Begin the for loop
	{
		«{
		var gets = ""
		var local_offset = 0;
			/* go through eventual out param first because of foot FiniteLoopBlock */
			for(param : block2.outBuffers){
				var b = param.container;
				var offset = param.offset;
				while(b instanceof SubBuffer){
					offset += b.offset;
					b = b.container;
				}
				/* put out buffer here */
				if(b.name == "Shared"){
					gets += "void *" + param.name + " = local_buffer+" + local_offset +";\n";
					local_offset += param.typeSize * param.size;
				}
			}
			for(param : block2.inBuffers){
				var b = param.container;
				var offset = param.offset;
				while(b instanceof SubBuffer){
					offset += b.offset;
					b = b.container;
				}
				//System.out.print("===> " + b.name + "\n");
				if(b.name == "Shared"){
					gets += "void *" + param.name + " = local_buffer+" + local_offset +";\n";
					gets += "{\n"
					gets += "	uint64_t start = __k1_read_dsu_timestamp();\n"
					gets += "	mppa_async_get(local_buffer + " + local_offset + ",\n";
					gets += "	&shared_segment,\n";
					//gets += "	" + b.name + " + " + offset + ",\n";
					gets += "	/* Shared + */ " + offset + ",\n";
					gets += "	" + param.typeSize * param.size + ",\n";
					gets += "	NULL);\n";
					gets += "	__builtin_k1_afdau(&total_get_cycles[__k1_get_cpu_id()], (__k1_read_dsu_timestamp() - start));\n"
					gets += "}\n"
					local_offset += param.typeSize * param.size;
					//System.out.print("==> " + b.name + " " + param.name + " size " + param.size + " port_name "+ port.getName + "\n");
				}
			}

			gets += "int " + block2.iter.name + ";\n"
			gets += "#pragma omp parallel for private(" + block2.iter.name + ")\n"
			gets += "for(" + block2.iter.name + "=0;" + block2.iter.name +"<" + block2.nbIter + ";" + block2.iter.name + "++)\n"
			gets += "	{\n"


			if(local_offset > local_buffer_size)
				local_buffer_size = local_offset
	gets}»
	'''

	override printFiniteLoopBlockFooter(FiniteLoopBlock block2) '''
		}
		«{
				var puts = ""
				var local_offset = 0;
				for(param : block2.outBuffers){
					var b = param.container
					var offset = param.offset
					while(b instanceof SubBuffer){
						offset += b.offset;
						b = b.container;
						//System.out.print("Running through all buffer " + b.name + "\n");
					}
					//System.out.print("===> " + b.name + "\n");
					if(b.name == "Shared"){
						puts += "{\n"
						puts += "	uint64_t start = __k1_read_dsu_timestamp();\n"
						puts += "	mppa_async_put(local_buffer + " + local_offset + ",\n";
						puts += "	&shared_segment,\n";
						puts += "	/* Shared + */" + offset + ",\n";
						puts += "	" + param.typeSize * param.size + ",\n";
						puts += "	NULL);\n";
						puts += "	__builtin_k1_afdau(&total_put_cycles[__k1_get_cpu_id()], __k1_read_dsu_timestamp() - start);\n"
						puts += "	}\n"
						local_offset += param.typeSize * param.size;
						//System.out.print("==> " + b.name + " " + param.name + " size " + param.size + " port_name "+ port.getName + "\n");
					}
				}
				if(local_offset > local_buffer_size)
					local_buffer_size = local_offset
				puts += "}\n"
			puts}»
		«{
			 	IS_HIERARCHICAL = false
			""}»
	'''

	override printFunctionCall(FunctionCall functionCall) '''
	«{
		var gets = ""
		var local_offset = 0;
		if(IS_HIERARCHICAL == false){
			gets += "{\n"
			for(param : functionCall.parameters){

				if(param instanceof SubBuffer){
					var port = functionCall.parameterDirections.get(functionCall.parameters.indexOf(param))
					var b = param.container;
					var offset = param.offset;
					while(b instanceof SubBuffer){
						offset += b.offset;
						b = b.container;
						//System.out.print("Running through all buffer " + b.name + "\n");
					}
					//System.out.print("===> " + b.name + "\n");
					if(b.name == "Shared"){
						gets += "	void *" + param.name + " = local_buffer+" + local_offset +";\n";
						if(port.getName == "INPUT"){ /* we get data from DDR -> cluster only when INPUT */
							gets += "	{\n"
							gets += "		uint64_t start = __k1_read_dsu_timestamp();\n"
							gets += "		mppa_async_get(local_buffer+" + local_offset + ", &shared_segment, /* Shared + */ " + offset + ", " + param.typeSize * param.size + ", NULL);\n";
							gets += "		__builtin_k1_afdau(&total_get_cycles[__k1_get_cpu_id()], __k1_read_dsu_timestamp() - start);\n"
							gets += "	}\n"
						}
						local_offset += param.typeSize * param.size;
						//System.out.print("==> " + b.name + " " + param.name + " size " + param.size + " port_name "+ port.getName + "\n");
					}
				}
			}
			gets += "\t"
		}else{
			gets += " /* gets are normaly generated before */ \n"
		}
		if(local_offset > local_buffer_size)
			local_buffer_size = local_offset
	gets}»
		«functionCall.name»(«FOR param : functionCall.parameters SEPARATOR ', '»«param.doSwitch»«ENDFOR»); // «functionCall.actorName»
	«{
		var puts = ""
		var local_offset = 0;
		if(IS_HIERARCHICAL == false){
			for(param : functionCall.parameters){
				if(param instanceof SubBuffer){
					var port = functionCall.parameterDirections.get(functionCall.parameters.indexOf(param))
					var b = param.container
					var offset = param.offset
					while(b instanceof SubBuffer){
						offset += b.offset;
						b = b.container;
						//System.out.print("Running through all buffer " + b.name + "\n");
					}
					//System.out.print("===> " + b.name + "\n");
					if(b.name == "Shared"){
						if(port.getName == "OUTPUT"){ /* we put data from cluster -> DDR only when OUTPUT */
							puts += "	{\n"
							puts += "		uint64_t start = __k1_read_dsu_timestamp();\n"
							puts += "		mppa_async_put(local_buffer+" + local_offset + ", &shared_segment, /* Shared + */ " + offset + ", " + param.typeSize * param.size + ", NULL);\n";
							puts += "		__builtin_k1_afdau(&total_put_cycles[__k1_get_cpu_id()], __k1_read_dsu_timestamp() - start);\n"
							puts += "	}\n"
						}
						local_offset += param.typeSize * param.size;
						//System.out.print("==> " + b.name + " " + param.name + " size " + param.size + " port_name "+ port.getName + "\n");
					}
				}
			}
			puts += "}\n"
		}else{
			puts += " /* puts are normaly generated before */ \n"
		}
		if(local_offset > local_buffer_size)
			local_buffer_size = local_offset
	puts}»
	'''

	override printDefinitionsFooter(List<Variable> list) '''
	«IF !list.empty»

	«ENDIF»
	'''

	override printDeclarationsHeader(List<Variable> list) '''
	// Core Global Declaration
	extern pthread_barrier_t pthread_barrier;

	'''

	override printBufferDeclaration(Buffer buffer) '''
	extern «printBufferDefinition(buffer)»
	'''

	override printSubBufferDeclaration(SubBuffer buffer) '''
	«buffer.type» *const «buffer.name» = («buffer.type»*) («var offset = 0»«
	{offset = buffer.offset
	 var b = buffer.container;
	 while(b instanceof SubBuffer){
	 	offset = offset + b.offset
	  	b = b.container
	  }
	 b}.name»+«offset»);  // «buffer.comment» size:= «buffer.size»*«buffer.type»
	'''

	override printDeclarationsFooter(List<Variable> list) '''
	«IF !list.empty»

	«ENDIF»
	'''

	override printCoreInitBlockHeader(CallBlock callBlock) '''
	void *computationTask_«(callBlock.eContainer as CoreBlock).name»(void *arg){
	#ifdef VERBOSE
		//printf("Cluster %d runs on task «(callBlock.eContainer as CoreBlock).name»\n", __k1_get_cluster_id());
	#endif
		«IF !callBlock.codeElts.empty»
			// Initialisation(s)

		«ENDIF»
	'''

	override printCoreLoopBlockHeader(LoopBlock block2) '''

		«"\t"»// Begin the execution loop
			int __iii __attribute__((unused));
			for(__iii=0;__iii<GRAPH_ITERATION;__iii++){

				//pthread_barrier_wait(&pthread_barrier);
		#ifdef PROFILE
				getTimeProfile();
		#endif

	'''


	override printCoreLoopBlockFooter(LoopBlock block2) '''
		#ifdef VERBOSE
				mppa_rpc_barrier_all(); /* sync all PE0 of all Clusters */
				if(__k1_get_cpu_id() == 0 && __k1_get_cluster_id() == 0){
					//printf("C0->%d Graph Iteration %d / %d Done !\n", NB_CLUSTER, __iii+1, GRAPH_ITERATION);
				}
				mppa_rpc_barrier_all(); /* sync all PE0 of all Clusters */
		#endif
				/* commit local changes to the global memory */
				//pthread_barrier_wait(&pthread_barrier); /* barrier to make sure all threads have commited data in smem */
				if(__k1_get_cpu_id() == 0){
		#ifdef PROFILE
					int iii;
					for(iii=0;iii<__k1_get_cluster_id();iii++)
						mppa_rpc_barrier_all();
					int ii, jj;
					for(jj=0;jj<NB_CORE;jj++){
						if(current_dump[jj] != 0){
							printf("C%d PE%d : Number of actors %d\n", __k1_get_cluster_id(), jj, current_dump[jj]);
							printf("\t# Profile %d Timestamp %lld\n", 0, (long long)timestamp[jj][0]);
							for(ii=1;ii<current_dump[jj];ii++){
								printf("\t# C%d Profile %d Timestamp %lld Cycle %lld Time %.4f ms\n",
										__k1_get_cluster_id(),
										ii,
										(long long)timestamp[jj][ii],
										(long long)timestamp[jj][ii]-(long long)timestamp[jj][ii-1],
										((float)timestamp[jj][ii]-(float)timestamp[jj][ii-1])/400000.0f /* chip freq */);
							}
						}
					}
					for(iii=__k1_get_cluster_id();iii<NB_CLUSTER;iii++)
						mppa_rpc_barrier_all();
		#endif
				}

			}
			return NULL;
		}
	'''
	override printFifoCall(FifoCall fifoCall) {
		var result = "fifo" + fifoCall.operation.toString.toLowerCase.toFirstUpper + "("

		if (fifoCall.operation != FifoOperation::INIT) {
			var buffer = fifoCall.parameters.head as Buffer
			result = result + '''«buffer.doSwitch», '''
		}

		result = result +
			'''«fifoCall.headBuffer.name», «fifoCall.headBuffer.size»*sizeof(«fifoCall.headBuffer.type»), '''
		result = result + '''«IF fifoCall.bodyBuffer !== null»«fifoCall.bodyBuffer.name», «fifoCall.bodyBuffer.size»*sizeof(«fifoCall.
			bodyBuffer.type»)«ELSE»NULL, 0«ENDIF»);
			'''

		return result
	}

	override printFork(SpecialCall call) '''
	// Fork «call.name»«var input = call.inputBuffers.head»«var index = 0»
	{
		«FOR output : call.outputBuffers»
			«printMemcpy(output,0,input,index,output.size,output.type)»«{index=(output.size+index); ""}»
		«ENDFOR»
	}
	'''

	override printBroadcast(SpecialCall call) '''
	// Broadcast «call.name»«var input = call.inputBuffers.head»«var index = 0»
	{
	«FOR output : call.outputBuffers»«var outputIdx = 0»
		«FOR nbIter : 0..output.size/input.size+1/*Worst case is output.size exec of the loop */»
			«IF outputIdx < output.size /* Execute loop core until all output for current buffer are produced */»
				«val value = Math::min(output.size-outputIdx,input.size-index)»«
				printMemcpy(output,outputIdx,input,index,value,output.type)»«
				{index=(index+value)%input.size;outputIdx=(outputIdx+value); ""}»
			«ENDIF»
		«ENDFOR»
	«ENDFOR»
	}
	'''



	override printRoundBuffer(SpecialCall call) '''
	// RoundBuffer «call.name»«var output = call.outputBuffers.head»«var index = 0»«var inputIdx = 0»
	«/*Compute a list of useful memcpy (the one writing the outputed value) */
	var copiedInBuffers = {var totalSize = call.inputBuffers.fold(0)[res, buf | res+buf.size]
		 var lastInputs = new ArrayList
		 inputIdx = totalSize
		 var i = call.inputBuffers.size	- 1
		 while(totalSize-inputIdx < output.size){
		 	inputIdx = inputIdx - call.inputBuffers.get(i).size
		 	lastInputs.add(0,call.inputBuffers.get(i))
		 	i=i-1
		 }
		 inputIdx = inputIdx %  output.size
		 lastInputs
		 }»
	{
		«FOR input : copiedInBuffers»
			«FOR nbIter : 0..input.size/output.size+1/*Worst number the loop exec */»
				«IF inputIdx < input.size /* Execute loop core until all input for current buffer are produced */»
					«val value = Math::min(input.size-inputIdx,output.size-index)»«
					printMemcpy(output,index,input,inputIdx,value,input.type)»«
					{index=(index+value)%output.size;inputIdx=(inputIdx+value); ""}»
				«ENDIF»
			«ENDFOR»
		«ENDFOR»
	}
	'''

	override printJoin(SpecialCall call) '''
	// Join «call.name»«var output = call.outputBuffers.head»«var index = 0»
	{
		«FOR input : call.inputBuffers»
			«printMemcpy(output,index,input,0,input.size,input.type)»«{index=(input.size+index); ""}»
		«ENDFOR»
	}
	'''

	/**
	 * Print a memcpy call in the generated code. Unless
	 * {@link #IGNORE_USELESS_MEMCPY} is set to <code>true</code>, this method
	 * checks if the destination and the source of the memcpy are superimposed.
	 * In such case, the memcpy is useless and nothing is printed.
	 *
	 * @param output
	 *            the destination {@link Buffer}
	 * @param outOffset
	 *            the offset in the destination {@link Buffer}
	 * @param input
	 *            the source {@link Buffer}
	 * @param inOffset
	 *            the offset in the source {@link Buffer}
	 * @param size
	 *            the amount of memory to copy
	 * @param type
	 *            the type of objects copied
	 * @return a {@link CharSequence} containing the memcpy call (if any)
	 */
	override printMemcpy(Buffer output, int outOffset, Buffer input, int inOffset, int size, String type) {

		// Retrieve the container buffer of the input and output as well
		// as their offset in this buffer
		var totalOffsetOut = outOffset*output.typeSize
		var bOutput = output
		while (bOutput instanceof SubBuffer) {
			totalOffsetOut = totalOffsetOut + bOutput.offset
			bOutput = bOutput.container
		}

		var totalOffsetIn = inOffset*input.typeSize
		var bInput = input
		while (bInput instanceof SubBuffer) {
			totalOffsetIn = totalOffsetIn + bInput.offset
			bInput = bInput.container
		}

		// If the Buffer and offsets are identical, or one buffer is null
		// there is nothing to print
		if((IGNORE_USELESS_MEMCPY && bInput == bOutput && totalOffsetIn == totalOffsetOut) ||
			output instanceof NullBuffer || input instanceof NullBuffer){
			return ""
		} else {
			return '''memcpy(«output.doSwitch»+«outOffset», «input.doSwitch»+«inOffset», «size»*sizeof(«type»));'''
		}
	}

	override printNullBuffer(NullBuffer Buffer) {
		return printBuffer(Buffer)
	}

	override caseCommunication(Communication communication) {

		if(communication.nodes.forall[type == "SHARED_MEM"]) {
			return super.caseCommunication(communication)
		} else {
			throw new CodegenException("Communication "+ communication.name +
				 " has at least one unsupported communication node"+
				 " for the " + this.class.name + " printer")
		}
	}

	override printSharedMemoryCommunication(SharedMemoryCommunication communication) '''
		«communication.direction.toString.toLowerCase»«communication.delimiter.toString.toLowerCase.toFirstUpper»(«IF (communication.
			direction == Direction::SEND && communication.delimiter == Delimiter::START) ||
			(communication.direction == Direction::RECEIVE && communication.delimiter == Delimiter::END)»«{
			var coreName = if (communication.direction == Direction::SEND) {
					communication.receiveStart.coreContainer.name
				} else {
					communication.sendStart.coreContainer.name
				}
			var ret = coreName.substring(4, coreName.length)
			ret
		}»«ENDIF»); // «communication.sendStart.coreContainer.name» > «communication.receiveStart.coreContainer.name»: «communication.
			data.doSwitch»
	'''

	override printConstant(Constant constant) '''«constant.value»«IF !constant.name.nullOrEmpty»/*«constant.name»*/«ENDIF»'''

	override printConstantString(ConstantString constant) '''"«constant.value»"'''

	override printBuffer(Buffer buffer) '''«buffer.name»'''

	override printSubBuffer(SubBuffer buffer) {
		return printBuffer(buffer)
	}

	override postProcessing(CharSequence charSequence){
		var ret = charSequence.toString.replace("int local_buffer_size __attribute__((unused)) = 0;", "int local_buffer_size __attribute__((unused)) = " + local_buffer_size + ";");
		return ret;
	}
}
