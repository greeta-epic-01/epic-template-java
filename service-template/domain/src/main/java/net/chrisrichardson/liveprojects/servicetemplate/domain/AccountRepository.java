package net.chrisrichardson.liveprojects.servicetemplate.domain;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface AccountRepository extends CrudRepository<Account, Long> {
    List<Account> findByOwner(String owner);
}