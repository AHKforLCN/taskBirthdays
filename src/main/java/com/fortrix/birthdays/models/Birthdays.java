package com.fortrix.birthdays.models;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Birthdays {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  private String fullName, dateBirthday;

  public Long getId() { return id; }
  public void setId(Long id) { this.id = id; }

  public String getFullName() { return fullName; }
  public void setFullName(String fullName) { this.fullName = fullName; }

  public String getDateBirthday() { return dateBirthday; }
  public void setDateBirthday(String dateBirthday) { this.dateBirthday = dateBirthday; }

  public Birthdays() { fullName = ""; dateBirthday = "-1"; }

  public Birthdays(String fullName, String dateBirthday) {
    this.fullName = fullName;
    this.dateBirthday = dateBirthday;
  }
}
