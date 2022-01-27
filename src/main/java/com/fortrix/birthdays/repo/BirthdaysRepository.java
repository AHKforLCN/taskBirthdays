package com.fortrix.birthdays.repo;

import com.fortrix.birthdays.models.Birthdays;
import org.springframework.data.repository.CrudRepository;

public interface BirthdaysRepository extends CrudRepository<Birthdays, Long> {

}
