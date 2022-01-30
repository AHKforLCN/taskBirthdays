package com.fortrix.birthdays.сontrollers;

import com.fortrix.birthdays.models.Birthdays;
import com.fortrix.birthdays.repo.BirthdaysRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

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

    while(birthdaysIter.hasNext()) {
      Birthdays item = birthdaysIter.next();
      String str = item.getDateBirthday();
      String[] resStr = str.split("\\.");
      int resMonth = Integer.parseInt(resStr[1]);
      int resDay = Integer.parseInt(resStr[0]);
      if ((resMonth == month & (resDay - day <= 2 & resDay - day >= 0)) || (resMonth - month == 1 & (resDay - day >= 27)))
        res.add(item);
    }

    if (!res.isEmpty())
      model.addAttribute("birthdays", res);
    return "main";
  }

  @GetMapping("/add")
  public String add(Model model) {
    return "add";
  }

  @PostMapping("/add")
  public String addPost(
          @RequestParam String fullName,
          @RequestParam String dateBirthday,
          @RequestParam (value = "file") String avatar,
          Model model) {

    String[] subStr = dateBirthday.split("\\.");
    if (subStr[0].isEmpty() || subStr[1].isEmpty() || subStr[2].isEmpty()) return "error";
    if (fullName.isEmpty() || dateBirthday.isEmpty()) return "error";

    Birthdays birthdays = new Birthdays(fullName, dateBirthday);
    System.out.println();

    File file = new File(avatar);
    File output = new File("C:/Users/FORTRIX/Desktop/birthdays/src/main/resources/static/avatar/" + birthdays.getId() + ".jpg");
    try {
      BufferedImage bufferedImage = ImageIO.read(file);
      ImageIO.write(bufferedImage, "jpg", output);
      birthdaysRepository.save(birthdays);
    } catch (IOException e) { return "redirect:/error"; }

    return "redirect:/";

  }

  @GetMapping("/all")
  public String all(Model model) {
    Iterable<Birthdays> birthdays = birthdaysRepository.findAll();
    model.addAttribute("birthdays", birthdays);
    return "all";
  }

  @GetMapping("/view")
  public String view(Model model) {
    Iterable<Birthdays> birthdays = birthdaysRepository.findAll();
    model.addAttribute("birthdays", birthdays);
    return "view";
  }

  @PostMapping("/view")
  public String postView(@RequestParam(value = "id", required = false) Long intID, Model model) {
    Birthdays bd = birthdaysRepository.findById(intID).orElseThrow();
    return "redirect:/view/id" + bd.getId();
  }

  @GetMapping("/view/id{id}")
  public String viewID(@PathVariable(value = "id") Long id, Model model) {
    Optional<Birthdays> bd = birthdaysRepository.findById(id);
    ArrayList<Birthdays> res = bd.stream().collect(Collectors.toCollection(ArrayList::new));

    model.addAttribute("birthdays", res);
    model.addAttribute("dayLeft", dayLeft(res.get(0).getDateBirthday()));
    model.addAttribute("fullYears", fullYears(res.get(0).getDateBirthday()));
    return "userInfo";
  }

  @GetMapping("/delete")
  public String delete(Model model) {
    Iterable<Birthdays> birthdays = birthdaysRepository.findAll();
    model.addAttribute("birthdays", birthdays);
    return "delete";
  }

  @PostMapping("/delete")
  public String postDelete(@RequestParam(value = "id", required = false) Long intID, Model model) {
    Birthdays bd = birthdaysRepository.findById(intID).orElseThrow();
    birthdaysRepository.delete(bd);
    File del = new File("C:/Users/FORTRIX/Desktop/birthdays/src/main/resources/static/avatar/" + intID + ".jpg");
    if (del.exists())
      del.delete();

    return "redirect:/";
  }

  @GetMapping("/delete/id{id}")
  public String deleteById(@PathVariable(value = "id") Long ID, Model model) {
    if (birthdaysRepository.findById(ID).isEmpty()) return "redirect:/";

    Birthdays birthdays = birthdaysRepository.findById(ID).orElseThrow();
    birthdaysRepository.delete(birthdays);
    File del = new File("C:/Users/FORTRIX/Desktop/birthdays/src/main/resources/static/avatar/" + ID + ".jpg");
    if (del.exists())
      del.delete();

    return "redirect:/";
  }

  @GetMapping("/id{id}/edit")
  public String edit(@PathVariable(value = "id") Long ID, Model model) {
    Optional<Birthdays> bd = birthdaysRepository.findById(ID);
    ArrayList<Birthdays> res = bd.stream().collect(Collectors.toCollection(ArrayList::new));
    model.addAttribute("birthdays", res);
    return "edit";
  }

  @PostMapping("/id{id}/edit")
  public String postEdit(
          @PathVariable (value = "id") Long ID,
          @RequestParam String fullName,
          @RequestParam String dateBirthday,
          @RequestParam (value = "file") String avatar,
          Model model) {

    Birthdays birthday = birthdaysRepository.findById(ID).orElseThrow();
    birthday.setFullName(fullName);
    birthday.setDateBirthday(dateBirthday);

    birthdaysRepository.save(birthday);

    File file = new File(avatar);
    File output = new File("C:/Users/FORTRIX/Desktop/birthdays/src/main/resources/static/avatar/" + birthday.getId() + ".jpg");
    try {
      BufferedImage bufferedImage = ImageIO.read(file);
      ImageIO.write(bufferedImage, "jpg", output);
    } catch (IOException e) { return "redirect:/view/id" + ID; }

    return "redirect:/view/id" + ID;
  }






  public int dayLeft(String dateBirthday) {
    Calendar date = Calendar.getInstance();
    int day = date.get(Calendar.DAY_OF_MONTH);
    int month = date.get(Calendar.MONTH) + 1;
    int year = date.get(Calendar.YEAR);
    boolean isLeapYear = ((year % 4 == 0) && (year % 100 != 0) || (year % 400 == 0));
    int daysInYear = isLeapYear ? 366 : 365;

    String[] str = dateBirthday.split("\\.");
    int userDay = Integer.parseInt(str[0]);
    int userMonth = Integer.parseInt(str[1]);
    int userYear = Integer.parseInt(str[2]);

    int daysInMonths[] = new int[12];
    for (int i = 0; i < 12; i++) {
      YearMonth yearMonth = YearMonth.of(year, i + 1);
      daysInMonths[i] = yearMonth.lengthOfMonth();
    }

    int summOfMonths = 0;
    if (userMonth == month && (day - userDay < 0)) return userDay - day;
    else if (userMonth == month && (day - userDay > 0)) return (daysInYear - (day - userDay));
    if (userMonth - month > 0) {
      for (int j = month; j < userMonth - month; j++)
        summOfMonths += daysInMonths[j];
    }
    if (userMonth - month < 0) {
      if (month == 12) summOfMonths += daysInMonths[11] - userDay;
      for (int i = month; i <= 12; i++) {
        summOfMonths += daysInMonths[i];
      }

      for (int i = 0; i < userMonth; i++) {
        summOfMonths += daysInMonths[i];
      }
    }

    int res = (daysInMonths[month - 1] - day) + summOfMonths + userDay;

    return res;
  }

  public int fullYears(String dateBirthday) {
    Calendar date = Calendar.getInstance();
    int day = date.get(Calendar.DAY_OF_MONTH);
    int month = date.get(Calendar.MONTH) + 1;
    int year = date.get(Calendar.YEAR);

    String[] str = dateBirthday.split("\\.");
    int userDay = Integer.parseInt(str[0]);
    int userMonth = Integer.parseInt(str[1]);
    int userYear = Integer.parseInt(str[2]);

    if (month - userMonth < 0) return year - userYear - 1;
    else if (month - userMonth > 0) return  year - userYear;
    else if (day - userDay < 0) return year - userYear - 1;
    else return year - userYear;
  }
}
