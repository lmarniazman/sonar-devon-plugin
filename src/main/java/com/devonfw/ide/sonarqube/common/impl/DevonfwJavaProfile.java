package com.devonfw.ide.sonarqube.common.impl;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.sonar.api.server.profile.BuiltInQualityProfilesDefinition;
import org.sonar.plugins.java.Java;
import org.sonarsource.api.sonarlint.SonarLintSide;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * This class creates a quality profile containing the rules of this plugin plus additional rules from external repos.
 */
@SonarLintSide
public class DevonfwJavaProfile implements BuiltInQualityProfilesDefinition {

  private static final String DEVON4J_XML = "/com/devonfw/ide/sonarqube/common/rules/devon4j/devon4j.xml";

  private static final Logger logger = Logger.getGlobal();

  // private static final String QUALINSIGHT = "qualinsight-plugins-sonarqube-smell-plugin";
  //
  // private static final String PMD = "sonar-pmd-plugin";
  //
  // private static final String CHECKSTYLE = "checkstyle-sonar-plugin";
  //
  // private static final String FINDBUGS = "sonar-findbugs-plugin";

  private static Set<String> FORBIDDEN_REPO_KEYS = new HashSet<>();

  // private static List<String> FORBIDDEN_JAVA_RULE_KEYS = Arrays.asList("S4143", "S4144", "S3052", "S888", "S2196",
  // "S3042", "S2197", "S3281", "S3282", "S881", "S3047");

  private File pluginDirectory;

  // private static final List<Repository> AVAILABLE_REPOSITORIES = DevonSonarDefinition.getRepositories();

  // private boolean isMinSQVersion;

  // private List<String> pluginList;

  static int one, two = 0;

  // Use this constructor only for testing purposes
  DevonfwJavaProfile(File pluginDirectory/* , Version sqVersion */) {

    this.pluginDirectory = pluginDirectory;
    // this.isMinSQVersion = sqVersion.isGreaterThanOrEqual(Version.create(8, 2));
  }

  /**
   * The constructor
   */
  public DevonfwJavaProfile() {

    this(new File("extensions/plugins")/*
                                        * , Version.create(SonarDevon4jPlugin.getSQVersion().major(),
                                        * SonarDevon4jPlugin.getSQVersion().minor())
                                        */);
  }

  @Override
  public void define(Context context) throws IllegalStateException {

    NewBuiltInQualityProfile devonfwJava = context.createBuiltInQualityProfile("devonfw Java", Java.KEY);
    Document parsedXml = readQualityProfileXml();
    if (parsedXml == null) {
      logger.log(Level.INFO, "The XML file could not be read.");
      return;
    }
    NodeList ruleList = parsedXml.getElementsByTagName("rule");
    NodeList childrenOfRule;

    NewBuiltInActiveRule currentRule;
    String repoKey = null;
    String ruleKey = null;
    String severity = "";

    for (int i = 0; i < ruleList.getLength(); i++) {

      childrenOfRule = ruleList.item(i).getChildNodes();

      for (int j = 0; j < childrenOfRule.getLength(); j++) {
        switch (childrenOfRule.item(j).getNodeName()) {
          case "repositoryKey":
            repoKey = childrenOfRule.item(j).getTextContent();
            break;
          case "key":
            ruleKey = childrenOfRule.item(j).getTextContent();
            break;
          case "priority":
            severity = childrenOfRule.item(j).getTextContent();
            break;
          default:
            break;
        }
      }

      disableRepoKeys(repoKey);
      if (isRuleActivationAllowed(repoKey, ruleKey)) {
        logger.log(Level.SEVERE, "Rule activation was allowed.");
        // try {
        currentRule = devonfwJava.activateRule(repoKey, ruleKey);
        if (!severity.isEmpty()) {
          currentRule.overrideSeverity(severity);
        }
        // } catch (IllegalStateException ex) {
        // logger.log(Level.WARNING, "Rule " + repoKey + ":" + ruleKey + " could not be initialized.");
        // ex.printStackTrace();
        // }
      }
    }

    logger.log(Level.SEVERE, "one: " + one);
    logger.log(Level.SEVERE, "two: " + two);

    // try {
    // devonfwJava.done();
    // } catch (IllegalStateException ex) {
    // logger.log(Level.SEVERE, "IllegalStateException was caught!");
    // logger.log(Level.WARNING, ex.getMessage());
    // return;
    // }
  }

  private boolean isRuleActivationAllowed(String repoKey, String ruleKey) {

    if (!(FORBIDDEN_REPO_KEYS.contains(repoKey) || repoKey == null || ruleKey == null)) {
      try {
        if (repoKey.equals("java")) {
          one += 1;
          return Objects.nonNull(DevonSonarDefinition.getRepoByKey("squid").rule(ruleKey));
        } else {
          two += 1;
          return Objects.nonNull(DevonSonarDefinition.getRepoByKey(repoKey).rule(ruleKey));
        }
      } catch (NullPointerException ex) {
        logger.log(Level.WARNING, "Rule " + repoKey + ":" + ruleKey + " could not be found.");
        return false;
      }
    } else {
      logger.log(Level.WARNING, "The required plugin to instantiate " + repoKey + ":" + ruleKey + " is not installed.");
      return false;
    }
  }

  private Document readQualityProfileXml() {

    try (InputStream inputStream = DevonfwJavaProfile.class.getResourceAsStream(DEVON4J_XML)) {
      DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
      DocumentBuilder builder = dbFactory.newDocumentBuilder();
      return builder.parse(inputStream);
    } catch (ParserConfigurationException pc) {
      logger.log(Level.WARNING, "There was a problem configuring the parser.");
      return null;
    } catch (IOException io) {
      logger.log(Level.WARNING, "There was a problem reading the file.");
      return null;
    } catch (SAXException sax) {
      logger.log(Level.WARNING, "There was a problem parsing the file.");
      return null;
    }
  }

  // private List<String> getPlugins() {
  //
  // if (this.pluginList == null) {
  // File[] fileList = this.pluginDirectory.listFiles(f -> f.getName().endsWith(".jar") && f.isFile());
  // this.pluginList = Arrays.asList(fileList).stream().map(f -> f.getName()).collect(Collectors.toList());
  // }
  //
  // return this.pluginList;
  // }

  // private boolean hasPlugin(String name) {
  //
  // for (String plugin : getPlugins()) {
  // if (plugin.contains(name)) {
  // return true;
  // }
  // }
  //
  // return false;
  // }

  private void disableRepoKeys(String repoKey) {

    /* Special case for the squid/java repository */
    if (repoKey.equals("java") && DevonSonarDefinition.getRepoByKey("squid") != null) {
      return;
    }

    if (DevonSonarDefinition.getRepoByKey(repoKey) == null) {
      FORBIDDEN_REPO_KEYS.add(repoKey);
    }

    // if (!hasPlugin(QUALINSIGHT)) {
    // FORBIDDEN_REPO_KEYS.add("qualinsight-smells");
    // }
    //
    // if (!hasPlugin(PMD)) {
    // FORBIDDEN_REPO_KEYS.add("pmd");
    // FORBIDDEN_REPO_KEYS.add("pmd-unit-tests");
    // }
    //
    // if (!hasPlugin(CHECKSTYLE)) {
    // FORBIDDEN_REPO_KEYS.add("checkstyle");
    // }
    //
    // if (!hasPlugin(FINDBUGS)) {
    // FORBIDDEN_REPO_KEYS.add("findbugs");
    // FORBIDDEN_REPO_KEYS.add("findsecbugs");
    // FORBIDDEN_REPO_KEYS.add("fb-contrib");
    // }
  }

}
