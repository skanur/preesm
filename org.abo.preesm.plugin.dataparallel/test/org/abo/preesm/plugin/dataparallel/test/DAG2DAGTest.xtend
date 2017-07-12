package org.abo.preesm.plugin.dataparallel.test

import java.util.Collection
import java.util.HashMap
import org.abo.preesm.plugin.dataparallel.DAG2DAG
import org.abo.preesm.plugin.dataparallel.SDF2DAG
import org.ietr.dftools.algorithm.model.sdf.SDFGraph
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.abo.preesm.plugin.dataparallel.operations.visitor.LevelsOperations
import org.abo.preesm.plugin.dataparallel.operations.visitor.OperationsUtils

/**
 * Property based test to check construction of DAG from another DAG
 * 
 * @author Sudeep Kanur
 */
@RunWith(Parameterized)
class DAG2DAGTest {
	
	protected val SDF2DAG dagGen
	
	protected val SDFGraph sdf
	
	new(SDF2DAG dagGen, SDFGraph sdf) {
		this.dagGen = dagGen
		this.sdf = sdf
	}
	
	@Parameterized.Parameters
	public static def Collection<Object[]> instancesToTest() {
		val parameters = newArrayList
		
		Util.provideAllGraphs.forEach[sdf |
			val dagGen = new SDF2DAG(sdf)
			parameters.add(#[dagGen, sdf])
		]
		
		return parameters
	}
	
	/**
	 * The test checks that the actor obtained from instance2Actor
	 * for a {@link DAG2DAG} instance gives same actor from instance2Actor
	 * map of a {@link SDF2DAG} instance
	 */
	@Test
	public def void bothInstancesLeadToSameActor() {
		val newDagGen = new DAG2DAG(dagGen)
		newDagGen.instance2Actor.forEach[instance, actor |
			val newActor = newDagGen.instance2Actor.get(instance)
			// Check that the new map does return an actor and not null
			Assert.assertTrue(newActor !== null)
			
			// Now check that its same as old actor
			Assert.assertEquals(newActor, actor)
		]
	}
	
	/**
	 * Check that changing levels of one SDF does not modify the levels of
	 * another SDF. 
	 */
	@Test
	public def void graphsAreOperationInvariant() {
		val newDagGen = new DAG2DAG(dagGen)
		var levelOp = new LevelsOperations
		dagGen.accept(levelOp)
		var oldLevels = new HashMap(levelOp.levels)
		val maxLevel = OperationsUtils.getMaxLevel(oldLevels)
		
		// Now modify one level
		val indexInstance = oldLevels.keySet.get(0)
		oldLevels.put(indexInstance, maxLevel)
		
		// Now construct the new DAG and check if the levels are modified
		levelOp = new LevelsOperations
		newDagGen.accept(levelOp)
		val newLevels = new HashMap(levelOp.levels)
		Assert.assertEquals(oldLevels.get(indexInstance), maxLevel)
		Assert.assertTrue(newLevels.get(indexInstance) != maxLevel)
	}
}