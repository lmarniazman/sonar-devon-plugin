package com.devonfw.ide.sonarqube.common.impl;

import org.sonar.api.Plugin;
import org.sonar.api.PropertyType;
import org.sonar.api.config.PropertyDefinition;

/**
 * The {@link Plugin} to integrate devonfw architecture rules into SonarQube.
 */
public class SonarDevon4jPlugin implements Plugin {

  static final String CONFIG_KEY = "sonar.devon.config";

  static final String FORBIDDEN_CONF_KEY = "sonar.devon.forbiddenConf";

  static final String DISABLED = "Disabled";

  static final String ISSUES_SEVERITY_KEY = "sonar.Devon.preview.issuesSeverity";

  // static Context CONTEXT;

  @Override
  public void define(Context context) {

    // setContext(context);
    context.addExtensions(DevonSonarDefinition.class, DevonSonarRegistrar.class, DevonfwJavaProfile.class);
    context.addExtension(PropertyDefinition.builder(CONFIG_KEY).name("Config JSON")
        .description("Configuration of business architecture").category("devonfw").subCategory("")
        .type(PropertyType.TEXT)
        .defaultValue("{\"architecture\":{\"components\":[\n{\"name\":\"component1\",\\\"dependencies\\\":[]}}\n]}}")
        .build());
  }

  // /**
  // *
  // * @return Version of the SonarQube server currently in use
  // */
  // public static Version getSQVersion() {
  //
  // return SonarDevon4jPlugin.CONTEXT.getSonarQubeVersion();
  // }
  //
  // private void setContext(Context context) {
  //
  // SonarDevon4jPlugin.CONTEXT = context;
  // }
}
