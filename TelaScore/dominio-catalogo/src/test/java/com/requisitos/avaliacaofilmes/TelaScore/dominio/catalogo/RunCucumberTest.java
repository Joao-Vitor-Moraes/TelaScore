package com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo;

import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

import static io.cucumber.junit.platform.engine.Constants.GLUE_PROPERTY_NAME;

@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("com/requisitos/avaliacaofilmes/TelaScore/dominio/catalogo/features") 
@ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = "com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo")
public class RunCucumberTest {
}