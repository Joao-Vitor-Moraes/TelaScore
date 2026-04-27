package com.requisitos.avaliacaofilmes.TelaScore.aplicacao.social;

import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;
import org.junit.platform.suite.api.SelectPackages;

import static io.cucumber.junit.platform.engine.Constants.GLUE_PROPERTY_NAME;
import static io.cucumber.junit.platform.engine.Constants.FEATURES_PROPERTY_NAME;

@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("bdd/social")
@ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = "com.requisitos.avaliacaofilmes.TelaScore.aplicacao.social")
public class RunCucumberTest {
}