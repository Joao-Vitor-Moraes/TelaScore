package com.requisitos.avaliacaofilmes.TelaScore.dominio.social;

import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectPackages;
import org.junit.platform.suite.api.Suite;

import static io.cucumber.junit.platform.engine.Constants.GLUE_PROPERTY_NAME;
import static io.cucumber.junit.platform.engine.Constants.PLUGIN_PROPERTY_NAME;

@Suite
@IncludeEngines("cucumber")
// Selecionamos o pacote 'social' para garantir que este Runner só execute o que for deste módulo
@SelectPackages("com.requisitos.avaliacaofilmes.TelaScore.dominio.social")
@ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = "com.requisitos.avaliacaofilmes.TelaScore.dominio.social")
// Mantemos o plugin 'pretty' para você continuar vendo os logs detalhados no console
@ConfigurationParameter(key = PLUGIN_PROPERTY_NAME, value = "pretty")
public class RunCucumberTest {
}