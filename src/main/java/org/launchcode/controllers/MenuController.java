package org.launchcode.controllers;

import org.launchcode.models.Menu;
import org.launchcode.models.data.CheeseDao;
import org.launchcode.models.data.MenuDao;
import org.launchcode.models.forms.AddMenuItemForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.validation.Valid;

/**
 * Created by Deb on 4/10/2017.
 */

@Controller
@RequestMapping(value = "menu")
public class MenuController {

    @Autowired
    private MenuDao menuDao;

    @Autowired
    private CheeseDao cheeseDao;

    @RequestMapping(value = "")
    public String index(Model model) {

        model.addAttribute("menus", menuDao.findAll());
        model.addAttribute("title", "Menus");

        return "menu/index";
    }

    @RequestMapping(value = "add", method = RequestMethod.GET)
    public String add(Model model) {

        model.addAttribute("title", "Add Menu");
        model.addAttribute(new Menu());

        return "menu/add";
    }

    @RequestMapping(value = "add", method = RequestMethod.POST)
    public String add(Model model, @ModelAttribute @Valid Menu menu,
                      Errors errors) {

        if (errors.hasErrors()) {
            model.addAttribute("title", "Add Menu");
            return "menu/add";
        } else {
            menuDao.save(menu);
            return "redirect:view/" + menu.getId();
        }
    }

    @RequestMapping(value = "view/{id}", method = RequestMethod.GET)
    public String viewMenu(Model model, @PathVariable("id") int id) {

        Menu menu = menuDao.findOne(id);
        model.addAttribute("menu", menu);
        return "menu/view";
    }

    @RequestMapping(value = "add-item/{id}", method = RequestMethod.GET)
    public String addItem(Model model, @PathVariable("id") int id) {

        Menu menu = menuDao.findOne(id);
        AddMenuItemForm menuItemForm = new AddMenuItemForm(menu, cheeseDao.findAll());
        model.addAttribute("form", menuItemForm);
        model.addAttribute("title", "Add item to menu: " + menu.getName());
        model.addAttribute("menu", menu);
        return "menu/add-item";
    }

    @RequestMapping(value = "add-item/{id}", method = RequestMethod.POST)
    public String addItem(Model model,
                          @ModelAttribute @Valid AddMenuItemForm addMenuItemForm,
                          Errors errors, @PathVariable("id") int id) {
        if (errors.hasErrors()) {
            model.addAttribute("title", "Add Menu");
            return "menu/add-item" + id;
        } else {
            Menu theMenu = menuDao.findOne(addMenuItemForm.getMenuId());
            theMenu.addItem(cheeseDao.findOne(addMenuItemForm.getCheeseId()));
            menuDao.save(theMenu);
            return "redirect:../view/" + addMenuItemForm.getMenuId();
        }
    }

}
