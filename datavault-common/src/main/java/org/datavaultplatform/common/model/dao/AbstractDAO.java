package org.datavaultplatform.common.model.dao;


import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AbstractDAO<T,ID> extends JpaRepository<T,ID> {

  default List<T> list(){
    return findAll();
  }

  default T update(T item) {
    return this.save(item);
  }
}
