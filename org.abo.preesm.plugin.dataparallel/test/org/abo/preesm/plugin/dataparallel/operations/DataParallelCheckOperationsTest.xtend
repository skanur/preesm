package org.abo.preesm.plugin.dataparallel.operations

import java.util.Collection
import org.abo.preesm.plugin.dataparallel.test.util.ExampleGraphs
import org.abo.preesm.plugin.dataparallel.test.util.Util
import org.ietr.dftools.algorithm.model.sdf.SDFGraph
import org.ietr.dftools.algorithm.model.visitors.SDF4JException
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

/**
 * Property based test to verify {@link DataParallelCheckOperations} works as expected
 * 
 * @author Sudeep Kanur
 */
@RunWith(Parameterized)
class DataParallelCheckOperationsTest {
	
	protected val SDFGraph sdf
	
	/**
	 * Has the following parameters from {@link Util#provideAllGraphs}:
	 * <ol>
	 * 	<li> {@link SDFGraph} instance
	 * </ol>
	 */
	new(SDFGraph sdf) {
		this.sdf = sdf
	}
	
	/**
	 * Generates following parameters from {@link Util#provideAllGraphs}:
	 * <ol>
	 * 	<li> {@link SDFGraph} instance
	 * </ol>
	 */
	@Parameterized.Parameters
	public static def Collection<Object[]> instancesToTest() {
		val parameters = newArrayList
		
		Util.provideAllGraphs.forEach[sdf |
			parameters.add(#[sdf])
		]		
		
		return parameters
	}
	
	/**
	 * Should throw an exception if a hierarchical SDF is being passed
	 * <p>
	 * <i>Strong Test</i>
	 */
	@Test(expected = SDF4JException)
	public def void exceptionHierGraph() {
		val isolatedSubgraphsVisitor = new DataParallelCheckOperations
		ExampleGraphs.acyclicHierarchicalTwoActors.accept(isolatedSubgraphsVisitor)
	}
	
	/**
	 * Assert that there are no dangling port interfaces for any vertex
	 * of isolated subgraph
	 * <p>
	 * <i>Strong Test</i>
	 */
	@Test
	public def void isolatedSubgraphsAreComplete() {
		val isolatedSubgraphsVisitor = new DataParallelCheckOperations
		sdf.accept(isolatedSubgraphsVisitor)
		
		isolatedSubgraphsVisitor.isolatedStronglyConnectedComponents.forEach[ subgraph |
			// Check that there are no unconnected interface
			subgraph.vertexSet.forEach[vertex |
				subgraph.incomingEdgesOf(vertex).forEach[edge |
					Assert.assertTrue(subgraph.vertexSet.contains(edge.source))
					Assert.assertTrue(subgraph.vertexSet.contains(edge.target))
				]
				
				subgraph.outgoingEdgesOf(vertex).forEach[edge |
					Assert.assertTrue(subgraph.vertexSet.contains(edge.source))
					Assert.assertTrue(subgraph.vertexSet.contains(edge.target))
				]
			]
		]
	}
}