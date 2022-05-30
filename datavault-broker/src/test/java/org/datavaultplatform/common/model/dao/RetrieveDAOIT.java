package org.datavaultplatform.common.model.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.datavaultplatform.broker.app.DataVaultBrokerApp;
import org.datavaultplatform.broker.test.AddTestProperties;
import org.datavaultplatform.broker.test.BaseReuseDatabaseTest;
import org.datavaultplatform.common.model.Retrieve;
import org.datavaultplatform.common.model.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest(classes = DataVaultBrokerApp.class)
@AddTestProperties
@Slf4j
@TestPropertySource(properties = {
    "broker.email.enabled=false",
    "broker.controllers.enabled=false",
    "broker.rabbit.enabled=false",
    "broker.scheduled.enabled=false"
})
public class RetrieveDAOIT extends BaseReuseDatabaseTest {

  @Autowired
  RetrieveDAO dao;

  @Autowired
  UserDAO userDAO;

  @Test
  void testWriteThenRead() {
    Retrieve review1 = getRetrieve1();

    Retrieve review2 = getRetrieve2();

    dao.save(review1);
    assertNotNull(review1.getID());
    assertEquals(1, count());

    dao.save(review2);
    assertNotNull(review2.getID());
    assertEquals(2, count());

    Retrieve foundById1 = dao.findById(review1.getID()).get();
    assertEquals(review1.getID(), foundById1.getID());

    Retrieve foundById2 = dao.findById(review2.getID()).get();
    assertEquals(review2.getID(), foundById2.getID());
  }

  @Test
  void testList() {
    Retrieve review1 = getRetrieve1();

    Retrieve review2 = getRetrieve2();

    dao.save(review1);
    assertNotNull(review1.getID());
    assertEquals(1, count());

    dao.save(review2);
    assertNotNull(review2.getID());
    assertEquals(2, count());

    List<Retrieve> items = dao.list();
    assertEquals(2, items.size());
    assertEquals(1, items.stream().filter(dr -> dr.getID().equals(review1.getID())).count());
    assertEquals(1, items.stream().filter(dr -> dr.getID().equals(review2.getID())).count());
  }

  @Test
  void testUpdate() {
    Retrieve arc1 = getRetrieve1();

    dao.save(arc1);

    arc1.setNote("updated-note");

    dao.update(arc1);

    Retrieve found = dao.findById(arc1.getID()).get();
    assertEquals(arc1.getNote(), found.getNote());
  }

  @BeforeEach
  void setup() {
    assertEquals(0, count());
  }

  @AfterEach
  void cleanup() {
    template.execute("delete from `Retrieves`");
    assertEquals(0, count());
  }

  private Retrieve getRetrieve1() {
    Retrieve result = new Retrieve();
    result.setHasExternalRecipients(false);
    result.setNote("note-1");
     return result;
  }

  private User getUser1(){
    User result = new User();
    result.setID("userone");
    result.setFirstname("first1");
    result.setLastname("last1");
    result.setEmail("one.one@test.com");
    return result;
  }

  private User getUser2(){
    User result = new User();
    result.setID("usertwo");
    result.setFirstname("first2");
    result.setLastname("last2");
    result.setEmail("two.two@test.com");
    return result;
  }

  private Retrieve getRetrieve2() {
    Retrieve result = new Retrieve();
    result.setHasExternalRecipients(false);
    result.setNote("note-2");
    return result;
  }

  long count() {
    return dao.count();
  }
}