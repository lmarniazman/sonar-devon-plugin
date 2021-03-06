package com.devonfw.ide.sonarqube.common.impl.check;

import org.sonar.check.Priority;
import org.sonar.check.Rule;

import com.devonfw.ide.sonarqube.common.api.JavaType;
import com.devonfw.ide.sonarqube.common.api.config.Component;

/**
 * {@link DevonArchitectureComponentCheck} verifying that a logic layer does not depend on the dataaccess layer of
 * another {@link Component}.
 */
@Rule(key = "Devon4j:C5", name = "Devon Layer Logic-Dataaccess Component Check", //
    description = "Verify that logic layer may not depend on the dataaccess layer of another component.", //
    priority = Priority.CRITICAL, tags = { "architecture-violation" })
public class DevonArchitectureLayerLogic2Dataaccess4ComponentCheck extends DevonArchitectureComponentCheck {

  @Override
  protected String checkDependency(JavaType source, Component sourceComponent, JavaType target) {

    if (source.isLayerLogic() && target.isLayerDataAccess()) {
      if (target.toString().equals("com.devonfw.module.jpa.dataaccess.api.RevisionMetadata")) {
        return null; // specific exclusion for unclean packaging
      }
      return "Code from logic layer ('" + source.toString()
          + "') shall not depend on dataaccess layer of a different component ('" + target.toString() + "').";
    }
    return null;
  }

}
