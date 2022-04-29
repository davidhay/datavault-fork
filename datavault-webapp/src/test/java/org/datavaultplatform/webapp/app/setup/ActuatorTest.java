package org.datavaultplatform.webapp.app.setup;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.datavaultplatform.webapp.test.ProfileStandalone;
import org.datavaultplatform.webapp.test.TestClockConfig;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledOnOs;
import org.junit.jupiter.api.condition.OS;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestClockConfig.class)
@ProfileStandalone
@Slf4j
public class ActuatorTest {

  private static final List<String> PUBLIC_ENDPOINTS = Arrays.asList("info","health","customtime");
  private static final List<String> PRIVATE_ENDPOINTS = Arrays.asList("env","loggers","mappings","beans");

  @Autowired
  ObjectMapper mapper;

  @Autowired
  MockMvc mvc;

  @Value("#{'${management.endpoints.web.exposure.include}'.split(',')}")
  private Set<String> endpoints;

  @Test
  void testInfo() throws Exception {
    mvc.perform(get("/actuator/info"))
        .andExpect(jsonPath("$.app.name").value(Matchers.is("datavault-webapp")))
        .andExpect(jsonPath("$.app.description").value(Matchers.is("webapp for datavault")))
        .andExpect(jsonPath("$.git.commit.time").exists())
        .andExpect(jsonPath("$.git.commit.id").value(Matchers.is("a16f01e")))
        .andExpect(jsonPath("$.build.artifact").value(Matchers.is("datavault-webapp")))
        .andExpect(jsonPath("$.build.time").value(Matchers.is("2022-03-30T12:41:50.383Z")))
        .andExpect(jsonPath("$.java.vendor").exists())
        .andExpect(jsonPath("$.java.runtime.version").exists())
        .andExpect(jsonPath("$.java.jvm.version").exists());
  }


  /* just checking that 'test-classes' come before the other 'classes' directories */
  @Test
  void testClassPath() {
    String classpath = System.getProperty("java.class.path");
    String sep = File.pathSeparator;

    log.info("CLASS PATH [{}]",classpath);
    log.info("sep[{}]", sep);

    List<String> elements = Arrays.stream(classpath.split(sep))
        .filter(elem -> elem.endsWith("classes"))
        .collect(Collectors.toList());

    elements.forEach(el -> log.info("path element [{}]", el));

    assertTrue(elements.size() >= 2);

    assertTrue(elements.get(0).endsWith("datavault-webapp/target/test-classes"));
    assertTrue(elements.get(1).endsWith("datavault-webapp/target/classes"));

  }

  @Test
  @DisabledOnOs(OS.WINDOWS)
  void testCurrentTime() throws Exception {
    MvcResult mvcResult = mvc.perform(
            get("/actuator/customtime"))
        .andExpect(content().contentTypeCompatibleWith("application/vnd.spring-boot.actuator.v3+json"))
        .andExpect(jsonPath("$.current-time").exists())
        .andReturn();

    String json = mvcResult.getResponse().getContentAsString();
    Map<String,String> infoMap = mapper.createParser(json).readValueAs(Map.class);

    assertTrue(infoMap.containsKey("current-time"));
    String ct = infoMap.get("current-time");
    Assertions.assertEquals("Tue Mar 29 14:15:16 BST 2022",ct);
  }

  @Test
  void testAvailableEndpoints() throws Exception {

    assertEquals(Collections.singleton("*"), this.endpoints);

    ResultActions temp = mvc.perform(get("/actuator"))
        .andExpect(status().isOk());

    ArrayList<String> endpoints = new ArrayList<>();
    endpoints.addAll(PUBLIC_ENDPOINTS);
    endpoints.addAll(PRIVATE_ENDPOINTS);

    for(String endpoint : endpoints){
      temp.andExpect(jsonPath("$._links."+endpoint).exists());
    }
  }

  @ParameterizedTest
  @MethodSource("publicEndpoints")
  void testPublicEndpoints(String endpoint) throws Exception {
    mvc.perform(get(String.format("/actuator/%s",endpoint)))
        .andExpect(status().isOk());
  }

  @ParameterizedTest
  @MethodSource("privateEndpoints")
  void testPrivateEndpoints(String endpoint) throws Exception {
    mvc.perform(get(String.format("/actuator/%s",endpoint)))
        .andExpect(status().isFound())
        .andExpect(redirectedUrl("http://localhost/auth/login"));
  }

  public static List<String> publicEndpoints(){
    return PUBLIC_ENDPOINTS;
  }

  public static List<String> privateEndpoints(){
    return PRIVATE_ENDPOINTS;
  }
}
