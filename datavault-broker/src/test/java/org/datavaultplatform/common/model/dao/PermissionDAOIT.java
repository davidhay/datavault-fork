package org.datavaultplatform.common.model.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.datavaultplatform.broker.app.DataVaultBrokerApp;
import org.datavaultplatform.broker.config.MockServicesConfig;
import org.datavaultplatform.broker.test.AddTestProperties;
import org.datavaultplatform.broker.test.BaseDatabaseTest;
import org.datavaultplatform.common.event.Event;
import org.datavaultplatform.common.model.Permission;
import org.datavaultplatform.common.model.PermissionModel;
import org.datavaultplatform.common.model.PermissionModel.PermissionType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
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
@Import(MockServicesConfig.class)
public class PermissionDAOIT extends BaseDatabaseTest {

  @Autowired
  PermissionDAO dao;

  @Autowired
  JdbcTemplate template;

  @Test
  void testWriteThenRead() {
    PermissionModel permissionModel1 = getPermissionModel1();

    PermissionModel permissionModel2 = getPermissionModel2();

    dao.save(permissionModel1);
    assertNotNull(permissionModel1.getId());
    assertEquals(1, count());

    dao.save(permissionModel2);
    assertNotNull(permissionModel2.getId());
    assertEquals(2, count());

    PermissionModel foundById1 = dao.findById(permissionModel1.getId()).get();
    assertEquals(permissionModel1.getId(), foundById1.getId());

    PermissionModel foundById2 = dao.findById(permissionModel2.getId()).get();
    assertEquals(permissionModel2.getId(), foundById2.getId());
  }

  @Test
  void testList() {
    PermissionModel permissionModel1 = getPermissionModel1();

    PermissionModel permissionModel2 = getPermissionModel2();

    dao.save(permissionModel1);
    assertNotNull(permissionModel1.getId());
    assertEquals(1, count());

    dao.save(permissionModel2);
    assertNotNull(permissionModel2.getId());
    assertEquals(2, count());

    List<PermissionModel> items = dao.list();
    assertEquals(2, items.size());
    assertEquals(1, items.stream().filter(dr -> dr.getId().equals(permissionModel1.getId())).count());
    assertEquals(1, items.stream().filter(dr -> dr.getId().equals(permissionModel2.getId())).count());
  }


  @Test
  void testUpdate() {
    PermissionModel permissionModel1 = getPermissionModel1();

    dao.save(permissionModel1);

    permissionModel1.setLabel("111-updated");

    dao.update(permissionModel1);

    PermissionModel found = dao.findById(permissionModel1.getId()).get();
    assertEquals(permissionModel1.getLabel(), found.getLabel());
  }

  @Test
  void testFind() {

    PermissionModel permissionModel1 = getPermissionModel1();
    PermissionModel permissionModel2 = getPermissionModel2();

    dao.save(permissionModel1);
    dao.save(permissionModel2);

    assertEquals(2, dao.count());

    PermissionModel found1 = dao.find(Permission.CAN_MANAGE_VAULTS);
    assertEquals(permissionModel1.getId(), found1.getId());

    PermissionModel found2 = dao.find(Permission.CAN_MANAGE_DEPOSITS);
    assertEquals(permissionModel2.getId(), found2.getId());

    PermissionModel found3 = dao.find(Permission.CAN_MANAGE_ARCHIVE_STORES);
    assertNull(found3);
  }

  @Test
  void testFindByType() {

    PermissionModel permissionModel1 = getPermissionModel1();
    PermissionModel permissionModel2 = getPermissionModel2();

    dao.save(permissionModel1);
    dao.save(permissionModel2);

    assertEquals(2, dao.count());

    List<PermissionModel> found1 = dao.findByType(PermissionType.VAULT);
    checkSamePermissionModelIds(found1, permissionModel1);

    List<PermissionModel> found2 = dao.findByType(PermissionType.SCHOOL);
    checkSamePermissionModelIds(found2, permissionModel2);

    List<PermissionModel> found3 = dao.findByType(PermissionType.ADMIN);
    assertTrue(found3.isEmpty());
  }

  void checkSamePermissionModelIds(Collection<PermissionModel> actual, PermissionModel... expected){
    assertEquals(
        Arrays.stream(expected).map(PermissionModel::getId).sorted().collect(Collectors.toList()),
        actual.stream().map(PermissionModel::getId).sorted().collect(Collectors.toList()));
  }

  @BeforeEach
  void setup() {
    assertEquals(0, count());
  }

  @AfterEach
  void cleanup() {
    template.execute("delete from `Permissions`");
    assertEquals(0, count());
  }

  private PermissionModel getPermissionModel1() {
    PermissionModel result = new PermissionModel();
    result.setId(Permission.CAN_MANAGE_VAULTS.name());
    result.setLabel("LABEL-1");
    result.setPermission(Permission.CAN_MANAGE_VAULTS);
    result.setType(PermissionType.VAULT);
    return result;
  }

  private PermissionModel getPermissionModel2() {
    PermissionModel result = new PermissionModel();
    result.setId(Permission.CAN_MANAGE_DEPOSITS.name());
    result.setLabel("LABEL-2");
    result.setPermission(Permission.CAN_MANAGE_DEPOSITS);
    result.setType(PermissionType.SCHOOL);
    return result;
  }

  long count() {
    return dao.count();
  }
}
