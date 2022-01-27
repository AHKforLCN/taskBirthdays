package com.fortrix.birthdays.сontrollers;

import com.fortrix.birthdays.models.Birthdays;
import com.fortrix.birthdays.repo.BirthdaysRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;

@Controller
public class BirthdaysController {

  @Autowired
  private BirthdaysRepository birthdaysRepository;

  @GetMapping("/")
  public String main(Model model) {
    Calendar date = Calendar.getInstance();
    int day = date.get(Calendar.DAY_OF_MONTH);
    int month = date.get(Calendar.MONTH) + 1;
    Iterable<Birthdays> birthdays = birthdaysRepository.findAll();
    Iterator<Birthdays> birthdaysIter = birthdays.iterator();
    ArrayList<Birthdays> res = new ArrayList<>();

    while(birthdaysIter.hasNext()) { // выполняется 1 раз
      Birthdays item = birthdaysIter.next();
      String str = item.getDateBirthday();
      String[] resStr = str.split("\\.");
      int resMonth = Integer.parseInt(resStr[1]);
      int resDay = Integer.parseInt(resStr[0]);
      System.out.println(day + "." + month);
      if (resMonth == month & (resDay - day <= 2 & resDay - day >= 0)) {
        res.add(item);
        birthdaysIter.next();
      }

    }

    System.out.println(res);
    model.addAttribute("birthdays", res);
    return "main";
  }

  @GetMapping("/add")
  public String add(Model model) {
    return "add";
  }

  @PostMapping("/add")
  public String addPost(@RequestParam String fullName, @RequestParam String dateBirthday, Model model) {
    String[] subStr = dateBirthday.split("\\.");
    if (subStr[0].isEmpty() || subStr[1].isEmpty() || subStr[2].isEmpty()) return "error";
    if (fullName.isEmpty() || dateBirthday.isEmpty()) return "error";

    Birthdays birthdays = new Birthdays(fullName, dateBirthday);
    birthdaysRepository.save(birthdays);
    return "redirect:/";
  }

  @GetMapping("/all")
  public String all(Model model) {
    Iterable<Birthdays> birthdays = birthdaysRepository.findAll();
    model.addAttribute("birthdays", birthdays);
    return "add";
  }
}
