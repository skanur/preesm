/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2014 - 2017) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017)
 * Clément Guy <clement.guy@insa-rennes.fr> (2014 - 2015)
 * Hugo Miomandre <hugo.miomandre@insa-rennes.fr> (2017)
 * Julien Hascoet <jhascoet@kalray.eu> (2017)
 * Julien Heulot <julien.heulot@insa-rennes.fr> (2015 - 2017)
 * Karol Desnos <karol.desnos@insa-rennes.fr> (2017)
 * Maxime Pelcat <maxime.pelcat@insa-rennes.fr> (2015)
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
package org.ietr.preesm.pimm.algorithm.spider.codegen.visitor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import org.ietr.dftools.workflow.tools.WorkflowLogger;
import org.ietr.preesm.core.types.DataType;
import org.ietr.preesm.experiment.model.pimm.AbstractActor;
import org.ietr.preesm.experiment.model.pimm.AbstractVertex;
import org.ietr.preesm.experiment.model.pimm.Actor;
import org.ietr.preesm.experiment.model.pimm.BroadcastActor;
import org.ietr.preesm.experiment.model.pimm.ConfigInputInterface;
import org.ietr.preesm.experiment.model.pimm.ConfigInputPort;
import org.ietr.preesm.experiment.model.pimm.ConfigOutputInterface;
import org.ietr.preesm.experiment.model.pimm.ConfigOutputPort;
import org.ietr.preesm.experiment.model.pimm.DataInputInterface;
import org.ietr.preesm.experiment.model.pimm.DataInputPort;
import org.ietr.preesm.experiment.model.pimm.DataOutputInterface;
import org.ietr.preesm.experiment.model.pimm.DataOutputPort;
import org.ietr.preesm.experiment.model.pimm.DataPort;
import org.ietr.preesm.experiment.model.pimm.Delay;
import org.ietr.preesm.experiment.model.pimm.Dependency;
import org.ietr.preesm.experiment.model.pimm.ExecutableActor;
import org.ietr.preesm.experiment.model.pimm.Expression;
import org.ietr.preesm.experiment.model.pimm.Fifo;
import org.ietr.preesm.experiment.model.pimm.ForkActor;
import org.ietr.preesm.experiment.model.pimm.FunctionParameter;
import org.ietr.preesm.experiment.model.pimm.FunctionPrototype;
import org.ietr.preesm.experiment.model.pimm.HRefinement;
import org.ietr.preesm.experiment.model.pimm.ISetter;
import org.ietr.preesm.experiment.model.pimm.InterfaceActor;
import org.ietr.preesm.experiment.model.pimm.JoinActor;
import org.ietr.preesm.experiment.model.pimm.Parameter;
import org.ietr.preesm.experiment.model.pimm.Parameterizable;
import org.ietr.preesm.experiment.model.pimm.PiGraph;
import org.ietr.preesm.experiment.model.pimm.Port;
import org.ietr.preesm.experiment.model.pimm.Refinement;
import org.ietr.preesm.experiment.model.pimm.RoundBufferActor;
import org.ietr.preesm.experiment.model.pimm.util.PiMMDefaultVisitor;
import org.ietr.preesm.pimm.algorithm.spider.codegen.utils.SpiderNameGenerator;
import org.ietr.preesm.pimm.algorithm.spider.codegen.utils.SpiderTypeConverter;
import org.ietr.preesm.pimm.algorithm.spider.codegen.utils.SpiderTypeConverter.PiSDFSubType;

// TODO: Find a cleaner way to setParentEdge in Interfaces
/*
 * Ugly workaround for setParentEdge in Interfaces. Must suppose that fifos are always obtained in the same order => Modify the C++ headers?
 * A better way would be a possibility to get edges from one building method to the other (since the parentEdge is in the outer graph),
 * maybe a map from edgeNames to edges with a method getOutputEdgeByName in BaseVertex
 */

/**
 * PiMM models visitor generating C++ code for COMPA Runtime currentGraph: The most outer graph of the PiMM model currentMethod: The StringBuilder used to write
 * the C++ code
 */
public class SpiderCodegenVisitor extends PiMMDefaultVisitor {
  private final SpiderPreProcessVisitor preprocessor;

  private final SpiderCodegen callerSpiderCodegen;

  // Ordered set for methods prototypes
  private final LinkedHashSet<String> prototypes = new LinkedHashSet<>();

  // Maps to handle hierarchical graphs
  private final Map<PiGraph, StringBuilder> graph2method    = new LinkedHashMap<>();
  private final Map<PiGraph, List<PiGraph>> graph2subgraphs = new LinkedHashMap<>();

  private final Map<String, DataType> dataTypes;

  private StringBuilder currentMethod;

  private PiGraph       currentGraph;
  private List<PiGraph> currentSubGraphs;

  // Variables containing the type of the currently visited AbstractActor for
  // AbstractActor generation
  // private String currentAbstractActorType;
  // private String currentAbstractActorClass;

  // Map linking data ports to their corresponding description
  private final Map<Port, Integer> portMap;

  private final Map<AbstractActor, Integer> functionMap;

  private final Map<AbstractActor, Map<String, String>> timings;

  private final Map<AbstractActor, Set<String>> constraints;

  public LinkedHashSet<String> getPrototypes() {
    return this.prototypes;
  }

  public Collection<StringBuilder> getMethods() {
    return this.graph2method.values();
  }

  // Shortcut for currentMethod.append()
  private void append(final Object a) {
    this.currentMethod.append(a);
  }

  /**
   */
  public SpiderCodegenVisitor(final SpiderCodegen callerSpiderCodegen, final StringBuilder topMethod, final SpiderPreProcessVisitor prepocessor,
      final Map<AbstractActor, Map<String, String>> timings, final Map<AbstractActor, Set<String>> constraints, final Map<String, DataType> dataTypes) {
    this.callerSpiderCodegen = callerSpiderCodegen;
    this.currentMethod = topMethod;
    this.preprocessor = prepocessor;
    this.portMap = this.preprocessor.getPortMap();
    this.functionMap = this.preprocessor.getFunctionMap();
    this.timings = timings;
    this.constraints = constraints;
    this.dataTypes = dataTypes;
  }

  /**
   * When visiting a PiGraph (either the most outer graph or an hierarchical actor), we should generate a new C++ method
   */
  @Override
  public void visitPiGraph(final PiGraph pg) {
    // We should first generate the C++ code as for any Actor in the outer
    // graph

    visitAbstractActor(pg);

    // We add pg as a subgraph of the current graph
    if (this.currentSubGraphs == null) {
      this.currentSubGraphs = new ArrayList<>();
    }
    this.currentSubGraphs.add(pg);

    // We stock the informations about the current graph for later use
    final PiGraph currentOuterGraph = this.currentGraph;
    if (currentOuterGraph != null) {
      this.graph2method.put(currentOuterGraph, this.currentMethod);
      this.graph2subgraphs.put(currentOuterGraph, this.currentSubGraphs);
    }
    // We initialize variables which will stock informations about pg during
    // its method generation
    // The new current graph is pg
    this.currentGraph = pg;
    // We start a new StringBuilder to generate its method
    this.currentMethod = new StringBuilder();
    // Currently we know no subgraphs to pg
    this.currentSubGraphs = new ArrayList<>();

    // And then visit pg as a PiGraph, generating the method to build its
    // C++ corresponding PiSDFGraph

    append("\n// Method building PiSDFGraph");
    append(pg.getName() + "\n");

    // Generating the method prototype
    generateMethodPrototype(pg);
    // Generating the method body
    generateMethodBody(pg);

    // If pg has no subgraphs, its method has not been added in graph2method
    // map
    if (!this.graph2method.containsKey(this.currentGraph)) {
      this.graph2method.put(this.currentGraph, this.currentMethod);
    }

    // We get back the informations about the outer graph to continue
    // visiting it
    if (currentOuterGraph != null) {
      this.currentMethod = this.graph2method.get(currentOuterGraph);
      this.currentSubGraphs = this.graph2subgraphs.get(currentOuterGraph);
    }
    this.currentGraph = currentOuterGraph;
  }

  /**
   * Class that sort parameters with dependencies
   */
  private class ParameterSorting {
    private final Map<Parameter, Integer> ParameterLevels = new LinkedHashMap<>();

    private Integer getLevelParameter(final Parameter p) {
      if (this.ParameterLevels.containsKey(p)) {
        return this.ParameterLevels.get(p);
      }

      int level = 0;
      for (final ConfigInputPort port : p.getConfigInputPorts()) {
        if (port.getIncomingDependency().getSetter() instanceof Parameter) {
          final Parameter incomingParameter = (Parameter) port.getIncomingDependency().getSetter();
          if (!this.ParameterLevels.containsKey(incomingParameter)) {
            getLevelParameter(incomingParameter);
          }
          level = Math.max(level, this.ParameterLevels.get(incomingParameter) + 1);
        }
      }
      this.ParameterLevels.put(p, level);
      return level;
    }

    public List<Parameter> sortParameters(final List<Parameter> params) {
      for (final Parameter p : params) {
        getLevelParameter(p);
      }
      params.sort((p1, p2) -> this.ParameterLevels.get(p1) - this.ParameterLevels.get(p2));
      return params;
    }

  }

  /**
   * Concatenate the signature of the method corresponding to a PiGraph to the currentMethod StringBuilder
   */
  private void generateMethodPrototype(final PiGraph pg) {
    final StringBuilder prototype = new StringBuilder();
    final StringBuilder parameters_proto = new StringBuilder();
    final StringBuilder parameters_def = new StringBuilder();
    final StringBuilder definition = new StringBuilder();

    prototype.append("PiSDFGraph* ");
    prototype.append(SpiderNameGenerator.getMethodName(pg));
    prototype.append("(");

    definition.append(prototype.toString());

    final List<Parameter> l = new LinkedList<>();
    l.addAll(pg.getAllParameters());
    Collections.sort(l, (p1, p2) -> p1.getName().compareTo(p2.getName()));

    for (final Parameter p : l) {
      if (p.isLocallyStatic() && !p.isDependent() && !p.isConfigurationInterface()) {
        if (parameters_proto.length() > 0) {
          parameters_proto.append(", ");
          parameters_def.append(", ");
        }
        parameters_proto.append("Param " + p.getName() + " = " + ((int) Double.parseDouble(p.getExpression().evaluate())));
        parameters_def.append("Param " + p.getName());
      }
    }

    prototype.append(parameters_proto);
    definition.append(parameters_def);

    prototype.append(");\n");
    definition.append(")");
    this.prototypes.add(prototype.toString());
    append(definition);
  }

  /**
   * Concatenate the body of the method corresponding to a PiGraph to the currentMethod StringBuilder
   */
  private void generateMethodBody(final PiGraph pg) {
    append("{\n");

    int nInIf = 0;
    int nOutif = 0;
    int nConfig = 0;
    int nBody = 0;

    for (final AbstractActor v : pg.getVertices()) {
      switch (SpiderTypeConverter.getType(v)) {
        case PISDF_TYPE_IF:
          if (SpiderTypeConverter.getSubType(v) == PiSDFSubType.PISDF_SUBTYPE_INPUT_IF) {
            nInIf++;
          } else {
            nOutif++;
          }
          break;
        case PISDF_TYPE_CONFIG:
          nConfig++;
          break;
        case PISDF_TYPE_BODY:
          nBody++;
          break;
        default:
          break;
      }
    }

    // Create a graph and a top vertex
    append("\tPiSDFGraph* graph = Spider::createGraph(\n" + "\t\t/*Edges*/    " + pg.getFifos().size() + ",\n" + "\t\t/*Params*/   " + pg.getParameters().size()
        + ",\n" + "\t\t/*InputIf*/  " + nInIf + ",\n" + "\t\t/*OutputIf*/ " + nOutif + ",\n" + "\t\t/*Config*/   " + nConfig + ",\n" + "\t\t/*Body*/     "
        + nBody + ");\n");

    // Generating parameters
    append("\n\t/* Parameters */\n");

    final List<Parameter> params = new ArrayList<>(pg.getParameters());
    final ParameterSorting ps = new ParameterSorting();
    final List<Parameter> sortedParams = ps.sortParameters(params);

    for (final Parameter p : sortedParams) {
      p.accept(this);
    }

    // Generating vertices
    append("\n\t/* Vertices */\n");
    for (final AbstractActor v : pg.getVertices()) {
      v.accept(this);
    }
    // Generating edges
    append("\n\t/* Edges */\n");
    for (final Fifo f : pg.getFifos()) {
      f.accept(this);
    }

    append("\treturn graph;");
    append("\n}\n");
  }

  private String generateConfigVertex(final AbstractActor aa) {
    final String vertexName = SpiderNameGenerator.getVertexName(aa);

    String fctIx;
    if (this.functionMap.containsKey(aa)) {
      fctIx = SpiderNameGenerator.getFunctionName(aa).toUpperCase() + "_FCT";
    } else {
      fctIx = "-1";
    }

    // Call the addVertex method on the current graph
    append("\tPiSDFVertex* " + vertexName);
    append(" = Spider::addConfigVertex(\n");
    append("\t\t/*Graph*/   graph,\n");
    append("\t\t/*Name*/    \"" + aa.getName() + "\",\n");
    append("\t\t/*FctId*/   " + fctIx + ",\n");
    append("\t\t/*SubType*/ " + "PISDF_SUBTYPE_NORMAL" + ",\n");
    append("\t\t/*InData*/  " + aa.getDataInputPorts().size() + ",\n");
    append("\t\t/*OutData*/ " + aa.getDataOutputPorts().size() + ",\n");
    append("\t\t/*InParam*/ " + aa.getConfigInputPorts().size() + ",\n");
    append("\t\t/*OutParam*/" + aa.getConfigOutputPorts().size() + ");\n");

    return vertexName;
  }

  private String generateBodyVertex(final AbstractActor aa) {
    final String vertexName = SpiderNameGenerator.getVertexName(aa);

    String fctIx;
    if (this.functionMap.containsKey(aa)) {
      fctIx = SpiderNameGenerator.getFunctionName(aa).toUpperCase() + "_FCT";
    } else {
      fctIx = "-1";
    }

    // Call the addVertex method on the current graph
    append("\tPiSDFVertex* " + vertexName);
    append(" = Spider::addBodyVertex(\n");
    append("\t\t/*Graph*/   graph,\n");
    append("\t\t/*Name*/    \"" + aa.getName() + "\",\n");
    append("\t\t/*FctId*/   " + fctIx + ",\n");
    append("\t\t/*InData*/  " + aa.getDataInputPorts().size() + ",\n");
    append("\t\t/*OutData*/ " + aa.getDataOutputPorts().size() + ",\n");
    append("\t\t/*InParam*/ " + aa.getConfigInputPorts().size() + ");\n");

    return vertexName;
  }

  private String generateHierarchicalVertex(final AbstractActor aa) {
    final String vertexName = SpiderNameGenerator.getVertexName(aa);
    final PiGraph subGraph = ((PiGraph) aa);

    // Call the addVertex method on the current graph
    append("\tPiSDFVertex* " + vertexName);
    append(" = Spider::addHierVertex(\n");
    append("\t\t/*Graph*/   graph,\n");
    append("\t\t/*Name*/    \"" + aa.getName() + "\",\n");
    append("\t\t/*Graph*/   " + SpiderNameGenerator.getMethodName(subGraph) + "(");

    final List<Parameter> params = new LinkedList<>();
    params.addAll(subGraph.getAllParameters());
    Collections.sort(params, (p1, p2) -> p1.getName().compareTo(p2.getName()));
    final List<String> paramStrings = new LinkedList<>();
    for (final Parameter p : params) {
      if (p.isLocallyStatic() && !p.isDependent() && !p.isConfigurationInterface()) {
        paramStrings.add(p.getName());
      }
    }
    append(String.join(", ", paramStrings));

    append("),\n");
    append("\t\t/*InData*/  " + aa.getDataInputPorts().size() + ",\n");
    append("\t\t/*OutData*/ " + aa.getDataOutputPorts().size() + ",\n");
    append("\t\t/*InParam*/ " + aa.getConfigInputPorts().size() + ");\n");

    return vertexName;
  }

  /**
   * Generic visit method for all AbstractActors (Actors, PiGraph)
   */
  @Override
  public void visitAbstractActor(final AbstractActor aa) {
    String vertexName;

    if ((aa instanceof Actor) && ((Actor) aa).isConfigurationActor()) {
      vertexName = generateConfigVertex(aa);
    } else if (aa instanceof PiGraph) {
      vertexName = generateHierarchicalVertex(aa);
    } else if (aa.getName() == "end") {
      visitEndActor(aa);
      return;
    } else {
      vertexName = generateBodyVertex(aa);
    }

    // Add connections to parameters if necessary
    for (final ConfigOutputPort cop : aa.getConfigOutputPorts()) {
      for (final Dependency d : cop.getOutgoingDependencies()) {
        append("\tSpider::addOutParam(");
        append(vertexName + ", ");
        append(this.portMap.get(cop) + ", ");
        append(SpiderNameGenerator.getParameterName((Parameter) d.getGetter().eContainer()));
        append(");\n");
      }
    }

    // Add connections from parameters if necessary
    for (final ConfigInputPort cip : aa.getConfigInputPorts()) {
      append("\tSpider::addInParam(");
      append(vertexName + ", ");
      append(this.portMap.get(cip) + ", ");
      append(SpiderNameGenerator.getParameterName((Parameter) cip.getIncomingDependency().getSetter()));
      append(");\n");
    }

    if ((aa instanceof Actor) && !(aa instanceof PiGraph)) {
      if (this.constraints.get(aa) != null) {
        // Check if the actor is enabled on all PEs.
        final Set<String> peNames = this.constraints.get(aa);
        if (peNames.containsAll(this.callerSpiderCodegen.getCoreIds().keySet())) {
          append("\tSpider::isExecutableOnAllPE(");
          append(vertexName + ");\n");
        } else {
          // Not all the PEs are enabled for the actor
          for (final String core : this.constraints.get(aa)) {
            append("\tSpider::isExecutableOnPE(");
            append(vertexName + ", ");
            append(SpiderNameGenerator.getCoreName(core) + ");\n");
          }
        }
      } else {
        WorkflowLogger.getLogger().log(Level.WARNING, "Actor " + aa.getName() + " does not have a valid operator to execute on");
      }
    }

    final Map<String, String> aaTimings = this.timings.get(aa);
    if (aaTimings != null) {
      for (final String coreType : aaTimings.keySet()) {
        append("\tSpider::setTimingOnType(");
        append(vertexName + ", ");
        append(SpiderNameGenerator.getCoreTypeName(coreType) + ", \"");
        append(aaTimings.get(coreType));
        append("\");\n");
      }
    }

    append("\n");
  }

  @Override
  public void visitActor(final Actor a) {
    visitAbstractActor(a);
  }

  @Override
  public void visitDataInputInterface(final DataInputInterface dii) {
    final String vertexName = SpiderNameGenerator.getVertexName(dii);

    append("\tPiSDFVertex* " + vertexName);
    append(" = Spider::addInputIf(\n");
    append("\t\t/*Graph*/   graph,\n");
    append("\t\t/*Name*/    \"" + vertexName + "\",\n");
    append("\t\t/*InParam*/ " + dii.getConfigInputPorts().size() + ");\n");

    // Add connections from parameters if necessary
    for (final ConfigInputPort cip : dii.getConfigInputPorts()) {
      append("\tSpider::addInParam(");
      append(vertexName + ", ");
      append(this.portMap.get(cip) + ", ");
      append(SpiderNameGenerator.getParameterName((Parameter) cip.getIncomingDependency().getSetter()));
      append(");\n");
    }
    append("\n");
  }

  @Override
  public void visitDataOutputInterface(final DataOutputInterface doi) {
    final String vertexName = SpiderNameGenerator.getVertexName(doi);

    append("\tPiSDFVertex* " + vertexName);
    append(" = Spider::addOutputIf(\n");
    append("\t\t/*Graph*/   graph,\n");
    append("\t\t/*Name*/    \"" + vertexName + "\",\n");
    append("\t\t/*InParam*/ " + doi.getConfigInputPorts().size() + ");\n");

    // Add connections from parameters if necessary
    for (final ConfigInputPort cip : doi.getConfigInputPorts()) {
      append("\tSpider::addInParam(");
      append(vertexName + ", ");
      append(this.portMap.get(cip) + ", ");
      append(SpiderNameGenerator.getParameterName((Parameter) cip.getIncomingDependency().getSetter()));
      append(");\n");
    }
    append("\n");
  }

  /**
   * When visiting a FIFO we should add an edge to the current graph
   */
  @Override
  public void visitFifo(final Fifo f) {
    // Call the addEdge method on the current graph
    append("\tSpider::connect(\n");
    append("\t\t/*Graph*/   graph,\n");

    final DataOutputPort srcPort = f.getSourcePort();
    final DataInputPort snkPort = f.getTargetPort();

    int typeSize;
    if (this.dataTypes.containsKey(f.getType())) {
      typeSize = this.dataTypes.get(f.getType()).getSize();
    } else {
      WorkflowLogger.getLogger().warning("Type " + f.getType() + " is not defined in scenario (considered size = 1).");
      typeSize = 1;
    }

    final AbstractVertex srcActor = (AbstractVertex) srcPort.eContainer();
    final AbstractVertex snkActor = (AbstractVertex) snkPort.eContainer();

    String srcProd = srcPort.getExpression().getString();
    String snkProd = snkPort.getExpression().getString();

    /* Change port name in prod/cons/delay */
    for (final ConfigInputPort cfgPort : srcActor.getConfigInputPorts()) {
      final String paramName = ((Parameter) cfgPort.getIncomingDependency().getSetter()).getName();
      srcProd = srcProd.replaceAll("\\b" + cfgPort.getName() + "\\b", paramName);
    }

    for (final ConfigInputPort cfgPort : snkActor.getConfigInputPorts()) {
      final String paramName = ((Parameter) cfgPort.getIncomingDependency().getSetter()).getName();
      snkProd = snkProd.replaceAll("\\b" + cfgPort.getName() + "\\b", paramName);
    }

    String delay = "0";
    if (f.getDelay() != null) {
      delay = f.getDelay().getExpression().getString();

      for (final ConfigInputPort cfgPort : f.getDelay().getConfigInputPorts()) {
        final String paramName = ((Parameter) cfgPort.getIncomingDependency().getSetter()).getName();
        delay = delay.replaceAll("\\b" + cfgPort.getName() + "\\b", paramName);
      }
    }

    append("\t\t/*Src*/ " + SpiderNameGenerator.getVertexName(srcActor) + ", /*SrcPrt*/ " + this.portMap.get(srcPort) + ", /*Prod*/ \"(" + srcProd + ")*"
        + typeSize + "\",\n");

    append("\t\t/*Snk*/ " + SpiderNameGenerator.getVertexName(snkActor) + ", /*SnkPrt*/ " + this.portMap.get(snkPort) + ", /*Cons*/ \"(" + snkProd + ")*"
        + typeSize + "\",\n");

    if (f.getDelay() != null) {
      // append("\t\t/*Delay*/ \"(" + delay + ")*sizeof(" + f.getType() + ")\",0);\n\n");
      append("\t\t/*Delay*/ \"(" + delay + ")*" + typeSize + "\",0);\n\n");
    } else {
      append("\t\t/*Delay*/ \"0\",0);\n\n");
    }
  }

  /**
   * When visiting a parameter, we should add a parameter to the current graph
   */
  @Override
  public void visitParameter(final Parameter p) {
    final String paramName = SpiderNameGenerator.getParameterName(p);

    if (!p.isLocallyStatic()) {
      if ((p.getConfigInputPorts().size() == 1) && !(p.getConfigInputPorts().get(0).getIncomingDependency().getSetter() instanceof Parameter)) {
        /* DYNAMIC */
        append("\tPiSDFParam *" + paramName + " = Spider::addDynamicParam(graph, " + "\"" + p.getName() + "\"" + ");\n");
      } else {
        /* DYNAMIC DEPENDANT */
        append("\tPiSDFParam *" + paramName + " = Spider::addDynamicDependentParam(graph, " + "\"" + p.getName() + "\", \"" + p.getExpression().getString()
            + "\");\n");
      }
    } else if (p.getGraphPort() instanceof ConfigInputPort) {
      /* HERITED */
      append("\tPiSDFParam *" + paramName + " = Spider::addHeritedParam(graph, " + "\"" + p.getName() + "\", " + this.portMap.get(p.getGraphPort()) + ");\n");
    } else if (p.getConfigInputPorts().isEmpty()) {
      /* STATIC */
      append("\tPiSDFParam *" + paramName + " = Spider::addStaticParam(graph, " + "\"" + p.getName() + "\", " + p.getName() + ");\n");
    } else {
      /* STATIC DEPENDANT */
      append("\tPiSDFParam *" + paramName + " = Spider::addStaticDependentParam(graph, " + "\"" + p.getName() + "\", \"" + p.getExpression().getString()
          + "\");\n");
    }
  }

  @Override
  public void visitBroadcastActor(final BroadcastActor ba) {
    append("\tPiSDFVertex* " + SpiderNameGenerator.getVertexName(ba));
    append(" = Spider::addSpecialVertex(\n");
    append("\t\t/*Graph*/   graph,\n");
    append("\t\t/*Type*/    " + "PISDF_SUBTYPE_BROADCAST" + ",\n");
    append("\t\t/*InData*/  " + ba.getDataInputPorts().size() + ",\n");
    append("\t\t/*OutData*/ " + ba.getDataOutputPorts().size() + ",\n");
    append("\t\t/*InParam*/ " + ba.getConfigInputPorts().size() + ");\n");

    // Add connections from parameters if necessary
    for (final ConfigInputPort cip : ba.getConfigInputPorts()) {
      append("\tSpider::addInParam(");
      append(SpiderNameGenerator.getVertexName(ba) + ", ");
      append(this.portMap.get(cip) + ", ");
      append(SpiderNameGenerator.getParameterName((Parameter) cip.getIncomingDependency().getSetter()));
      append(");\n");
    }
    append("\n");
  }

  /**
   *
   */
  public void visitEndActor(final AbstractActor aa) {
    append("\tPiSDFVertex* " + SpiderNameGenerator.getVertexName(aa));
    append(" = Spider::addSpecialVertex(\n");
    append("\t\t/*Graph*/   graph,\n");
    append("\t\t/*Type*/    " + "PISDF_SUBTYPE_END" + ",\n");
    append("\t\t/*InData*/  " + aa.getDataInputPorts().size() + ",\n");
    append("\t\t/*OutData*/ " + aa.getDataOutputPorts().size() + ",\n");
    append("\t\t/*InParam*/ " + aa.getConfigInputPorts().size() + ");\n");

    // Add connections from parameters if necessary
    for (final ConfigInputPort cip : aa.getConfigInputPorts()) {
      append("\tSpider::addInParam(");
      append(SpiderNameGenerator.getVertexName(aa) + ", ");
      append(this.portMap.get(cip) + ", ");
      append(SpiderNameGenerator.getParameterName((Parameter) cip.getIncomingDependency().getSetter()));
      append(");\n");
    }
    append("\n");
  }

  @Override
  public void visitJoinActor(final JoinActor ja) {
    append("\tPiSDFVertex* " + SpiderNameGenerator.getVertexName(ja));
    append(" = Spider::addSpecialVertex(\n");
    append("\t\t/*Graph*/   graph,\n");
    append("\t\t/*Type*/    " + "PISDF_SUBTYPE_JOIN" + ",\n");
    append("\t\t/*InData*/  " + ja.getDataInputPorts().size() + ",\n");
    append("\t\t/*OutData*/ " + ja.getDataOutputPorts().size() + ",\n");
    append("\t\t/*InParam*/ " + ja.getConfigInputPorts().size() + ");\n");

    // Add connections from parameters if necessary
    for (final ConfigInputPort cip : ja.getConfigInputPorts()) {
      append("\tSpider::addInParam(");
      append(SpiderNameGenerator.getVertexName(ja) + ", ");
      append(this.portMap.get(cip) + ", ");
      append(SpiderNameGenerator.getParameterName((Parameter) cip.getIncomingDependency().getSetter()));
      append(");\n");
    }
    append("\n");
  }

  @Override
  public void visitForkActor(final ForkActor fa) {
    append("\tPiSDFVertex* " + SpiderNameGenerator.getVertexName(fa));
    append(" = Spider::addSpecialVertex(\n");
    append("\t\t/*Graph*/   graph,\n");
    append("\t\t/*Type*/    " + "PISDF_SUBTYPE_FORK" + ",\n");
    append("\t\t/*InData*/  " + fa.getDataInputPorts().size() + ",\n");
    append("\t\t/*OutData*/ " + fa.getDataOutputPorts().size() + ",\n");
    append("\t\t/*InParam*/ " + fa.getConfigInputPorts().size() + ");\n");

    // Add connections from parameters if necessary
    for (final ConfigInputPort cip : fa.getConfigInputPorts()) {
      append("\tSpider::addInParam(");
      append(SpiderNameGenerator.getVertexName(fa) + ", ");
      append(this.portMap.get(cip) + ", ");
      append(SpiderNameGenerator.getParameterName((Parameter) cip.getIncomingDependency().getSetter()));
      append(");\n");
    }
    append("\n");
  }

  @Override
  public void visitRoundBufferActor(final RoundBufferActor rba) {
    append("\tPiSDFVertex* " + SpiderNameGenerator.getVertexName(rba));
    append(" = Spider::addSpecialVertex(\n");
    append("\t\t/*Graph*/   graph,\n");
    append("\t\t/*Type*/    " + "PISDF_SUBTYPE_ROUNDBUFFER" + ",\n");
    append("\t\t/*InData*/  " + rba.getDataInputPorts().size() + ",\n");
    append("\t\t/*OutData*/ " + rba.getDataOutputPorts().size() + ",\n");
    append("\t\t/*InParam*/ " + rba.getConfigInputPorts().size() + ");\n");

    // Add connections from parameters if necessary
    for (final ConfigInputPort cip : rba.getConfigInputPorts()) {
      append("\tSpider::addInParam(");
      append(SpiderNameGenerator.getVertexName(rba) + ", ");
      append(this.portMap.get(cip) + ", ");
      append(SpiderNameGenerator.getParameterName((Parameter) cip.getIncomingDependency().getSetter()));
      append(");\n");
    }
    append("\n");
  }

  @Override
  public void visitConfigOutputInterface(final ConfigOutputInterface coi) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void visitDataInputPort(final DataInputPort dip) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void visitAbstractVertex(final AbstractVertex av) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void visitConfigInputInterface(final ConfigInputInterface cii) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void visitConfigInputPort(final ConfigInputPort cip) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void visitConfigOutputPort(final ConfigOutputPort cop) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void visitDataOutputPort(final DataOutputPort dop) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void visitDelay(final Delay d) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void visitDependency(final Dependency d) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void visitExpression(final Expression e) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void visitInterfaceActor(final InterfaceActor ia) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void visitISetter(final ISetter is) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void visitParameterizable(final Parameterizable p) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void visitPort(final Port p) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void visitDataPort(final DataPort p) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void visitRefinement(final Refinement r) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void visitFunctionParameter(final FunctionParameter functionParameter) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void visitFunctionPrototype(final FunctionPrototype functionPrototype) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void visitHRefinement(final HRefinement hRefinement) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void visitExecutableActor(final ExecutableActor ea) {
    throw new UnsupportedOperationException();
  }

  /**
   * Class allowing to stock necessary information about graphs when moving through the graph hierarchy
   */
  // private class GraphDescription {
  // List<PiGraph> subGraphs;
  // StringBuilder method;
  //
  // public GraphDescription(List<PiGraph> subGraphs, StringBuilder method) {
  // this.subGraphs = subGraphs;
  // this.method = method;
  // }
  //
  // }
}
