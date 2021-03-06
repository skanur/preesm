/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2008 - 2017) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017)
 * Clément Guy <clement.guy@insa-rennes.fr> (2014 - 2015)
 * Jonathan Piat <jpiat@laas.fr> (2008 - 2011)
 * Karol Desnos <karol.desnos@insa-rennes.fr> (2015)
 * Maxime Pelcat <maxime.pelcat@insa-rennes.fr> (2008 - 2016)
 * Pengcheng Mu <pengcheng.mu@insa-rennes.fr> (2008)
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
package org.ietr.preesm.core.scenario.serialize;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.common.util.WrappedException;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.ietr.dftools.algorithm.importer.GMLSDFImporter;
import org.ietr.dftools.algorithm.importer.InvalidModelException;
import org.ietr.dftools.algorithm.model.sdf.SDFAbstractVertex;
import org.ietr.dftools.algorithm.model.sdf.SDFGraph;
import org.ietr.dftools.architecture.slam.Design;
import org.ietr.dftools.architecture.slam.SlamPackage;
import org.ietr.dftools.architecture.slam.serialize.IPXACTResourceFactoryImpl;
import org.ietr.dftools.workflow.tools.WorkflowLogger;
import org.ietr.preesm.core.architecture.util.DesignTools;
import org.ietr.preesm.core.scenario.ConstraintGroup;
import org.ietr.preesm.core.scenario.MemCopySpeed;
import org.ietr.preesm.core.scenario.PreesmScenario;
import org.ietr.preesm.core.scenario.Timing;
import org.ietr.preesm.core.scenario.TimingManager;
import org.ietr.preesm.core.types.DataType;
import org.ietr.preesm.experiment.model.pimm.AbstractActor;
import org.ietr.preesm.experiment.model.pimm.Parameter;
import org.ietr.preesm.experiment.model.pimm.PiGraph;
import org.ietr.preesm.experiment.model.pimm.serialize.PiParser;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

// TODO: Auto-generated Javadoc
/**
 * An xml parser retrieving scenario data.
 *
 * @author mpelcat
 */
public class ScenarioParser {

  /** xml tree. */
  private Document dom = null;

  /** scenario being retrieved. */
  private PreesmScenario scenario = null;

  /** current algorithm. */
  private SDFGraph algoSDF = null;

  /** The algo pi. */
  private PiGraph algoPi = null;

  /**
   * Instantiates a new scenario parser.
   */
  public ScenarioParser() {

    this.scenario = new PreesmScenario();
  }

  /**
   * Gets the dom.
   *
   * @return the dom
   */
  public Document getDom() {
    return this.dom;
  }

  /**
   * Retrieves the DOM document.
   *
   * @param file
   *          the file
   * @return the preesm scenario
   * @throws InvalidModelException
   *           the invalid model exception
   * @throws FileNotFoundException
   *           the file not found exception
   * @throws CoreException
   *           the core exception
   */
  public PreesmScenario parseXmlFile(final IFile file) throws InvalidModelException, FileNotFoundException, CoreException {
    // get the factory
    final DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

    try {
      // Using factory get an instance of document builder
      final DocumentBuilder db = dbf.newDocumentBuilder();

      // parse using builder to get DOM representation of the XML file
      this.dom = db.parse(file.getContents());
    } catch (final ParserConfigurationException pce) {
      pce.printStackTrace();
    } catch (final SAXException e) {
      e.printStackTrace();
    } catch (final IOException e) {
      e.printStackTrace();
    } catch (final CoreException e) {
      e.printStackTrace();
    }

    if (this.dom != null) {
      // get the root elememt
      final Element docElt = this.dom.getDocumentElement();

      Node node = docElt.getFirstChild();

      while (node != null) {

        if (node instanceof Element) {
          final Element elt = (Element) node;
          final String type = elt.getTagName();
          switch (type) {
            case "files":
              parseFileNames(elt);
              break;
            case "constraints":
              parseConstraintGroups(elt);
              break;
            case "relativeconstraints":
              parseRelativeConstraints(elt);
              break;
            case "timings":
              parseTimings(elt);
              break;
            case "simuParams":
              parseSimuParams(elt);
              break;
            case "variables":
              parseVariables(elt);
              break;
            case "parameterValues":
              parseParameterValues(elt);
              break;
            default:
          }
        }

        node = node.getNextSibling();
      }
    }

    this.scenario.setScenarioURL(file.getFullPath().toString());
    return this.scenario;
  }

  /**
   * Retrieves all the parameter values.
   *
   * @param paramValuesElt
   *          the param values elt
   */
  private void parseParameterValues(final Element paramValuesElt) {

    Node node = paramValuesElt.getFirstChild();

    PiGraph graph = null;
    try {
      graph = ScenarioParser.getPiGraph(this.scenario.getAlgorithmURL());
    } catch (InvalidModelException | CoreException e) {
      e.printStackTrace();
    }
    if (this.scenario.isPISDFScenario() && (graph != null)) {
      final Set<Parameter> parameters = new LinkedHashSet<>();
      for (final Parameter p : graph.getAllParameters()) {
        if (!p.isConfigurationInterface()) {
          parameters.add(p);
        }
      }

      while (node != null) {
        if (node instanceof Element) {
          final Element elt = (Element) node;
          final String type = elt.getTagName();
          if (type.equals("parameter")) {
            final Parameter justParsed = parseParameterValue(elt, graph);
            parameters.remove(justParsed);
          }
        }

        node = node.getNextSibling();
      }

      // Create a parameter value foreach parameter not yet in the
      // scenario
      for (final Parameter p : parameters) {
        this.scenario.getParameterValueManager().addParameterValue(p);
      }
    }
  }

  /**
   * Gets the pi graph.
   *
   * @param algorithmURL
   *          URL of the Algorithm.
   * @return the {@link PiGraph} algorithm.
   * @throws InvalidModelException
   *           the invalid model exception
   * @throws CoreException
   *           the core exception
   */
  public static PiGraph getPiGraph(String algorithmURL) throws InvalidModelException, CoreException {
    return PiParser.getPiGraph(algorithmURL);
  }

  /**
   * Retrieve a ParameterValue.
   *
   * @param paramValueElt
   *          the param value elt
   * @param graph
   *          the graph
   * @return the parameter
   */
  private Parameter parseParameterValue(final Element paramValueElt, final PiGraph graph) {
    if (graph == null) {
      throw new IllegalArgumentException();
    }

    Parameter currentParameter = null;

    final String type = paramValueElt.getAttribute("type");
    final String parent = paramValueElt.getAttribute("parent");
    final String name = paramValueElt.getAttribute("name");
    String stringValue = paramValueElt.getAttribute("value");

    currentParameter = graph.getParameterNamedWithParent(name, parent);

    switch (type) {
      case "INDEPENDENT":
      case "STATIC": // Retro-compatibility
        this.scenario.getParameterValueManager().addIndependentParameterValue(currentParameter, stringValue, parent);
        break;
      case "ACTOR_DEPENDENT":
      case "DYNAMIC": // Retro-compatibility
        if ((stringValue.charAt(0) == '[') && (stringValue.charAt(stringValue.length() - 1) == ']')) {
          stringValue = stringValue.substring(1, stringValue.length() - 1);
          final String[] values = stringValue.split(",");

          final Set<Integer> newValues = new LinkedHashSet<>();

          try {
            for (final String val : values) {
              newValues.add(Integer.parseInt(val.trim()));
            }
          } catch (final NumberFormatException e) {
            // TODO: Do smthg?
          }
          this.scenario.getParameterValueManager().addActorDependentParameterValue(currentParameter, newValues, parent);
        }
        break;
      case "PARAMETER_DEPENDENT":
      case "DEPENDENT": // Retro-compatibility
        final Set<String> inputParameters = new LinkedHashSet<>();
        if (graph != null) {

          for (final Parameter input : currentParameter.getInputParameters()) {
            inputParameters.add(input.getName());
          }
        }
        this.scenario.getParameterValueManager().addParameterDependentParameterValue(currentParameter, stringValue, inputParameters, parent);
        break;
      default:
        throw new RuntimeException("Unknown Parameter type: " + type + " for Parameter: " + name);
    }

    return currentParameter;
  }

  /**
   * Retrieves the timings.
   *
   * @param relConsElt
   *          the rel cons elt
   */
  private void parseRelativeConstraints(final Element relConsElt) {

    final String relConsFileUrl = relConsElt.getAttribute("excelUrl");
    this.scenario.getTimingManager().setExcelFileURL(relConsFileUrl);

    Node node = relConsElt.getFirstChild();

    while (node != null) {

      if (node instanceof Element) {
        final Element elt = (Element) node;
        final String type = elt.getTagName();
        if (type.equals("relativeconstraint")) {
          parseRelativeConstraint(elt);
        }
      }

      node = node.getNextSibling();
    }
  }

  /**
   * Retrieves one timing.
   *
   * @param timingElt
   *          the timing elt
   */
  private void parseRelativeConstraint(final Element timingElt) {

    int group = -1;

    if (this.algoSDF != null) {
      final String type = timingElt.getTagName();
      if (type.equals("relativeconstraint")) {
        final String vertexpath = timingElt.getAttribute("vertexname");

        try {
          group = Integer.parseInt(timingElt.getAttribute("group"));
        } catch (final NumberFormatException e) {
          group = -1;
        }

        this.scenario.getRelativeconstraintManager().addConstraint(vertexpath, group);
      }

    }
  }

  /**
   * Retrieves the timings.
   *
   * @param varsElt
   *          the vars elt
   */
  private void parseVariables(final Element varsElt) {

    final String excelFileUrl = varsElt.getAttribute("excelUrl");
    this.scenario.getVariablesManager().setExcelFileURL(excelFileUrl);

    Node node = varsElt.getFirstChild();

    while (node != null) {

      if (node instanceof Element) {
        final Element elt = (Element) node;
        final String type = elt.getTagName();
        if (type.equals("variable")) {
          final String name = elt.getAttribute("name");
          final String value = elt.getAttribute("value");

          this.scenario.getVariablesManager().setVariable(name, value);
        }
      }

      node = node.getNextSibling();
    }
  }

  /**
   * Parses the simulation parameters.
   *
   * @param filesElt
   *          the files elt
   */
  private void parseSimuParams(final Element filesElt) {

    Node node = filesElt.getFirstChild();

    while (node != null) {

      if (node instanceof Element) {
        final Element elt = (Element) node;
        final String type = elt.getTagName();
        final String content = elt.getTextContent();
        switch (type) {
          case "mainCore":
            this.scenario.getSimulationManager().setMainOperatorName(content);
            break;
          case "mainComNode":
            this.scenario.getSimulationManager().setMainComNodeName(content);
            break;
          case "averageDataSize":
            this.scenario.getSimulationManager().setAverageDataSize(Long.valueOf(content));
            break;
          case "dataTypes":
            parseDataTypes(elt);
            break;
          case "specialVertexOperators":
            parseSpecialVertexOperators(elt);
            break;
          case "numberOfTopExecutions":
            this.scenario.getSimulationManager().setNumberOfTopExecutions(Integer.parseInt(content));
            break;
          default:
        }
      }

      node = node.getNextSibling();
    }
  }

  /**
   * Retrieves the data types.
   *
   * @param dataTypeElt
   *          the data type elt
   */
  private void parseDataTypes(final Element dataTypeElt) {

    Node node = dataTypeElt.getFirstChild();

    while (node != null) {

      if (node instanceof Element) {
        final Element elt = (Element) node;
        final String type = elt.getTagName();
        if (type.equals("dataType")) {
          final String name = elt.getAttribute("name");
          final String size = elt.getAttribute("size");

          if (!name.isEmpty() && !size.isEmpty()) {
            final DataType dataType = new DataType(name, Integer.parseInt(size));
            this.scenario.getSimulationManager().putDataType(dataType);
          }
        }
      }

      node = node.getNextSibling();
    }
  }

  /**
   * Retrieves the operators able to execute fork/join/broadcast.
   *
   * @param spvElt
   *          the spv elt
   */
  private void parseSpecialVertexOperators(final Element spvElt) {

    Node node = spvElt.getFirstChild();

    while (node != null) {

      if (node instanceof Element) {
        final Element elt = (Element) node;
        final String type = elt.getTagName();
        if (type.equals("specialVertexOperator")) {
          final String path = elt.getAttribute("path");

          if (path != null) {
            this.scenario.getSimulationManager().addSpecialVertexOperatorId(path);
          }
        }
      }

      node = node.getNextSibling();
    }

    /*
     * It is not possible to remove all operators from special vertex executors: if no operator is selected, all of them are!!
     */
    if (this.scenario.getSimulationManager().getSpecialVertexOperatorIds().isEmpty() && (this.scenario.getOperatorIds() != null)) {
      for (final String opId : this.scenario.getOperatorIds()) {
        this.scenario.getSimulationManager().addSpecialVertexOperatorId(opId);
      }
    }
  }

  /**
   * Parses the archi and algo files and retrieves the file contents.
   *
   * @param filesElt
   *          the files elt
   * @throws InvalidModelException
   *           the invalid model exception
   * @throws FileNotFoundException
   *           the file not found exception
   * @throws CoreException
   *           the core exception
   */
  private void parseFileNames(final Element filesElt) throws InvalidModelException, FileNotFoundException, CoreException {

    Node node = filesElt.getFirstChild();

    while (node != null) {

      if (node instanceof Element) {
        final Element elt = (Element) node;
        final String type = elt.getTagName();
        final String url = elt.getAttribute("url");
        if (url.length() > 0) {
          if (type.equals("algorithm")) {
            this.scenario.setAlgorithmURL(url);
            if (url.endsWith(".graphml")) {
              this.algoSDF = ScenarioParser.getSDFGraph(url);
              this.algoPi = null;
            } else if (url.endsWith(".pi")) {
              this.algoPi = ScenarioParser.getPiGraph(url);
              this.algoSDF = null;
            }
          } else if (type.equals("architecture")) {
            this.scenario.setArchitectureURL(url);
            initializeArchitectureInformation(url);
          } else if (type.equals("codegenDirectory")) {
            this.scenario.getCodegenManager().setCodegenDirectory(url);
          }
        }
      }

      node = node.getNextSibling();
    }
  }

  /**
   * Depending on the architecture model, parses the model and populates the scenario.
   *
   * @param url
   *          the url
   */
  private void initializeArchitectureInformation(final String url) {
    if (url.contains(".design")) {
      WorkflowLogger.getLogger().log(Level.SEVERE, "SLAM architecture 1.0 is no more supported. Use .slam architecture files.");
    } else if (url.contains(".slam")) {

      final Map<String, Object> extToFactoryMap = Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap();
      Object instance = extToFactoryMap.get("slam");
      if (instance == null) {
        instance = new IPXACTResourceFactoryImpl();
        extToFactoryMap.put("slam", instance);
      }

      if (!EPackage.Registry.INSTANCE.containsKey(SlamPackage.eNS_URI)) {
        EPackage.Registry.INSTANCE.put(SlamPackage.eNS_URI, SlamPackage.eINSTANCE);
      }

      // Extract the root object from the resource.
      final Design design = ScenarioParser.parseSlamDesign(url);

      this.scenario.setOperatorIds(DesignTools.getOperatorInstanceIds(design));
      this.scenario.setComNodeIds(DesignTools.getComNodeInstanceIds(design));
      this.scenario.setOperatorDefinitionIds(DesignTools.getOperatorComponentIds(design));
    }
  }

  /**
   * Parses the slam design.
   *
   * @param url
   *          the url
   * @return the design
   */
  public static Design parseSlamDesign(final String url) {
    // Demand load the resource into the resource set.
    final ResourceSet resourceSet = new ResourceSetImpl();

    final Path relativePath = new Path(url);
    final IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(relativePath);
    final String completePath = file.getLocation().toString();

    // resourceSet.
    Resource resource = null;
    Design design = null;

    try {
      resource = resourceSet.getResource(URI.createFileURI(completePath), true);

      // Extract the root object from the resource.
      design = (Design) resource.getContents().get(0);
    } catch (final WrappedException e) {
      WorkflowLogger.getLogger().log(Level.SEVERE, "The architecture file \"" + completePath + "\" specified by the scenario does not exist any more.");
    }

    return design;
  }

  /**
   * Gets the SDF graph.
   *
   * @param path
   *          the path
   * @return the SDF graph
   * @throws InvalidModelException
   *           the invalid model exception
   * @throws FileNotFoundException
   *           the file not found exception
   */
  public static SDFGraph getSDFGraph(final String path) throws InvalidModelException, FileNotFoundException {
    SDFGraph algorithm = null;
    final GMLSDFImporter importer = new GMLSDFImporter();

    final Path relativePath = new Path(path);
    final IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(relativePath);

    try {
      algorithm = importer.parse(new File(file.getLocation().toOSString()));

      ScenarioParser.addVertexPathProperties(algorithm, "");
    } catch (final InvalidModelException e) {
      e.printStackTrace();
    } catch (final FileNotFoundException e) {
      throw e;
    }

    return algorithm;
  }

  /**
   * Adding an information that keeps the path of each vertex relative to the hierarchy.
   *
   * @param algorithm
   *          the algorithm
   * @param currentPath
   *          the current path
   */
  private static void addVertexPathProperties(final SDFGraph algorithm, final String currentPath) {

    for (final SDFAbstractVertex vertex : algorithm.vertexSet()) {
      String newPath = currentPath + vertex.getName();
      vertex.setInfo(newPath);
      newPath += "/";
      if (vertex.getGraphDescription() != null) {
        ScenarioParser.addVertexPathProperties((SDFGraph) vertex.getGraphDescription(), newPath);
      }
    }
  }

  /**
   * Retrieves all the constraint groups.
   *
   * @param cstGroupsElt
   *          the cst groups elt
   */
  private void parseConstraintGroups(final Element cstGroupsElt) {

    final String excelFileUrl = cstGroupsElt.getAttribute("excelUrl");
    this.scenario.getConstraintGroupManager().setExcelFileURL(excelFileUrl);

    Node node = cstGroupsElt.getFirstChild();

    while (node != null) {

      if (node instanceof Element) {
        final Element elt = (Element) node;
        final String type = elt.getTagName();
        if (type.equals("constraintGroup")) {
          final ConstraintGroup cg = getConstraintGroup(elt);
          this.scenario.getConstraintGroupManager().addConstraintGroup(cg);
        }
      }

      node = node.getNextSibling();
    }
  }

  /**
   * Retrieves a constraint group.
   *
   * @param cstGroupElt
   *          the cst group elt
   * @return the constraint group
   */
  private ConstraintGroup getConstraintGroup(final Element cstGroupElt) {

    final ConstraintGroup cg = new ConstraintGroup();

    if ((this.algoSDF != null) || (this.algoPi != null)) {
      Node node = cstGroupElt.getFirstChild();

      while (node != null) {
        if (node instanceof Element) {
          final Element elt = (Element) node;
          final String type = elt.getTagName();
          final String name = elt.getAttribute("name");
          if (type.equals("task")) {
            if (getActorFromPath(name) != null) {
              cg.addActorPath(name);
            }
          } else if (type.equals("operator") && (this.scenario.getOperatorIds() != null)) {
            if (this.scenario.getOperatorIds().contains(name)) {
              cg.addOperatorId(name);
            }
          }
        }
        node = node.getNextSibling();
      }
      return cg;
    }

    return cg;
  }

  /**
   * Retrieves the timings.
   *
   * @param timingsElt
   *          the timings elt
   */
  private void parseTimings(final Element timingsElt) {

    final String timingFileUrl = timingsElt.getAttribute("excelUrl");
    this.scenario.getTimingManager().setExcelFileURL(timingFileUrl);

    Node node = timingsElt.getFirstChild();

    while (node != null) {

      if (node instanceof Element) {
        final Element elt = (Element) node;
        final String type = elt.getTagName();
        if (type.equals("timing")) {
          final Timing timing = getTiming(elt);
          if (timing != null) {
            this.scenario.getTimingManager().addTiming(timing);
          }
        } else if (type.equals("memcpyspeed")) {
          retrieveMemcpySpeed(this.scenario.getTimingManager(), elt);
        }
      }

      node = node.getNextSibling();
    }
  }

  /**
   * Retrieves one timing.
   *
   * @param timingElt
   *          the timing elt
   * @return the timing
   */
  private Timing getTiming(final Element timingElt) {

    Timing timing = null;

    if ((this.algoSDF != null) || (this.algoPi != null)) {

      final String type = timingElt.getTagName();
      if (type.equals("timing")) {
        final String vertexpath = timingElt.getAttribute("vertexname");
        final String opdefname = timingElt.getAttribute("opname");
        long time;
        final String stringValue = timingElt.getAttribute("time");
        boolean isEvaluated = false;
        try {
          time = Long.parseLong(stringValue);
          isEvaluated = true;
        } catch (final NumberFormatException e) {
          time = -1;
        }

        String actorName;
        if (vertexpath.contains("/")) {
          actorName = getActorNameFromPath(vertexpath);
        } else {
          actorName = vertexpath;
        }

        if ((actorName != null) && this.scenario.getOperatorDefinitionIds().contains(opdefname)) {
          if (isEvaluated) {
            timing = new Timing(opdefname, actorName, time);
          } else {
            timing = new Timing(opdefname, actorName, stringValue);
          }
        }
      }
    }

    return timing;
  }

  /**
   * Returns an actor Object (either SDFAbstractVertex from SDFGraph or AbstractActor from PiGraph) from the path in its container graph.
   *
   * @param path
   *          the path to the actor, where its segment is the name of an actor and separators are "/"
   * @return the wanted actor, if existing, null otherwise
   */
  private Object getActorFromPath(final String path) {
    Object result = null;
    if (this.algoSDF != null) {
      result = this.algoSDF.getHierarchicalVertexFromPath(path);
    } else if (this.algoPi != null) {
      result = this.algoPi.getHierarchicalActorFromPath(path);
    }
    return result;
  }

  /**
   * Returns the name of an actor (either SDFAbstractVertex from SDFGraph or AbstractActor from PiGraph) from the path in its container graph.
   *
   * @param path
   *          the path to the actor, where its segment is the name of an actor and separators are "/"
   * @return the name of the wanted actor, if we found it
   */
  private String getActorNameFromPath(final String path) {
    final Object actor = getActorFromPath(path);
    if (actor != null) {
      if (actor instanceof SDFAbstractVertex) {
        return ((SDFAbstractVertex) actor).getName();
      } else if (actor instanceof AbstractActor) {
        return ((AbstractActor) actor).getName();
      }
    }
    return null;
  }

  /**
   * Retrieves one memcopy speed composed of integer setup time and timeperunit.
   *
   * @param timingManager
   *          the timing manager
   * @param timingElt
   *          the timing elt
   */
  private void retrieveMemcpySpeed(final TimingManager timingManager, final Element timingElt) {

    if ((this.algoSDF != null) || (this.algoPi != null)) {

      final String type = timingElt.getTagName();
      if (type.equals("memcpyspeed")) {
        final String opdefname = timingElt.getAttribute("opname");
        final String sSetupTime = timingElt.getAttribute("setuptime");
        final String sTimePerUnit = timingElt.getAttribute("timeperunit");
        int setupTime;
        float timePerUnit;

        try {
          setupTime = Integer.parseInt(sSetupTime);
          timePerUnit = Float.parseFloat(sTimePerUnit);
        } catch (final NumberFormatException e) {
          setupTime = -1;
          timePerUnit = -1;
        }

        if (this.scenario.getOperatorDefinitionIds().contains(opdefname) && (setupTime >= 0) && (timePerUnit >= 0)) {
          final MemCopySpeed speed = new MemCopySpeed(opdefname, setupTime, timePerUnit);
          timingManager.putMemcpySpeed(speed);
        }
      }

    }
  }
}
