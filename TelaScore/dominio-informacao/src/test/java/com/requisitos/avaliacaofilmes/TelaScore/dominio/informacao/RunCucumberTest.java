package com.requisitos.avaliacaofilmes.TelaScore.dominio.informacao;

import org.junit.platform.suite.api.*;

import static io.cucumber.junit.platform.engine.Constants.GLUE_PROPERTY_NAME;

@Suite
@IncludeEngines("cucumber")
@SelectPackages("com/requisitos/avaliacaofilmes/TelaScore/dominio/informacao/features")
@ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = "com.requisitos.avaliacaofilmes.TelaScore.dominio.informacao")
public class RunCucumberTest {
}
