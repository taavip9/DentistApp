package com.cgi.dentistapp.controller;

import com.cgi.dentistapp.dao.DentistVisitDao;
import com.cgi.dentistapp.dao.entity.DentistVisitEntity;
import com.cgi.dentistapp.dto.DentistVisitDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import com.cgi.dentistapp.service.DentistVisitService;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.util.HtmlUtils;

import javax.validation.Valid;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

@Controller
@EnableAutoConfiguration
public class DentistAppController extends WebMvcConfigurerAdapter {

    @Autowired
    private DentistVisitService dentistVisitService;

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/findreg").setViewName("findreg");
    }

    @GetMapping("/")
    public String showRegisterForm(DentistVisitDTO dentistVisitDTO) {
        return "form";
    }

    @PostMapping("/")
    public String postRegisterForm(Model model, @Valid DentistVisitDTO dentistVisitDTO, BindingResult bindingResult) {

        //checks with .doesExist() method if a registration already exists for a specific date and time to a specific dentist
        boolean exists = dentistVisitService.doesExist(dentistVisitDTO.getDentistName(), dentistVisitDTO.getVisitTime());
        if(exists){
            model.addAttribute("exists", dentistVisitDTO.getVisitTime());
            return "form";
        }

        //if to check with .areWorkingHours() method whether the user input hours are from mon to fri, 10:00 - 16:00
        if (bindingResult.hasErrors() || !areWorkingHours(dentistVisitDTO.getVisitTime().getHours(),
                dentistVisitDTO.getVisitTime().getDay())){

            if (dentistVisitDTO.getVisitTime() == null || !areWorkingHours(dentistVisitDTO.getVisitTime().getHours(),
                    dentistVisitDTO.getVisitTime().getDay())){

                model.addAttribute("error", dentistVisitDTO.getVisitTime());
            }

            return "form";
        }

        dentistVisitService.addVisit(dentistVisitDTO.getGpName(), dentistVisitDTO.getDentistName(), dentistVisitDTO.getVisitTime());
        System.out.println(dentistVisitDTO.getVisitTime());
        return "redirect:/findreg";
    }

    @GetMapping("/findreg")
    public String searchRegistrations (Model model){

        model.addAttribute("regs", dentistVisitService.listVisits());

        return "findreg";
    }

    @PostMapping("/findreg")
    public String regResults (@RequestParam("dentistName") String searchWord, Model model){

        model.addAttribute("regs", dentistVisitService.listVisits());

        List<DentistVisitEntity> visits = dentistVisitService.listVisits();
        ArrayList<DentistVisitEntity> results = new ArrayList<DentistVisitEntity>();

        //For cycle to get and display all the registrations
        for(int i = 0; i != visits.size();i++){
            if(visits.get(i).getDentistName().toLowerCase().contains(searchWord.toLowerCase())){
                results.add(visits.get(i));
            }
        }

        model.addAttribute("results", results);

        return "findreg";
    }

    @GetMapping("/regdetail")

    public String showRegistDetail(@RequestParam("id") Long id,
                                   Model model,
                                   DentistVisitDTO dentistVisitDTO) {

        List<DentistVisitEntity> visits = dentistVisitService.listVisits();

        //For cycle to get a DentistVisitEntity with a matching ID and converting the values to a DTO
        for(int i = 0; i != visits.size();i++){
            if(visits.get(i).getId().equals(id)){
                DentistVisitEntity dve = visits.get(i);
                dentistVisitDTO.setGpName(dve.getGpName());
                dentistVisitDTO.setDentistName(dve.getDentistName());
                dentistVisitDTO.setVisitTime(dve.getVisitTime());
                model.addAttribute("result", dve);

            }
        }

        return "regdetail";
    }

    @PostMapping("/regdetail")
    public String postRegisterForm(@RequestParam("id") Long id,
                                   @RequestParam("action") String action,
                                   @Valid DentistVisitDTO dentistVisitDTO,
                                   BindingResult bindingResult,
                                   Model model) {

        List<DentistVisitEntity> visits = dentistVisitService.listVisits();

        //Get the DentistVisitEntity with a matching ID
         for (int i = 0; i != visits.size(); i++) {
             if (visits.get(i).getId().equals(id)) {

                 //Check if user wanted to delete or update a registration
                 if(action.equals("delete")) {
                     dentistVisitService.removeVisit(visits.get(i));
                 }

                 if(action.equals("update")){

                     //If user wished to update a registration, commence validation of new inputs
                     DentistVisitEntity dve = visits.get(i);
                     model.addAttribute("result", dve);

                     if(bindingResult.hasErrors()){
                        return "regdetail";
                     }

                     boolean exists = dentistVisitService.doesExist(dentistVisitDTO.getDentistName(), dentistVisitDTO.getVisitTime());

                     //Validate date time and day
                     if (bindingResult.hasErrors() || !areWorkingHours(dentistVisitDTO.getVisitTime().getHours(),
                             dentistVisitDTO.getVisitTime().getDay())){

                         model.addAttribute("dateerr", dentistVisitDTO.getVisitTime());

                         return "regdetail";
                     }

                     //Check if that that time to that dentist has already been booked
                     if(!exists){
                         visits.get(i).setVisitTime(dentistVisitDTO.getVisitTime());
                         visits.get(i).setDentistName(dentistVisitDTO.getDentistName());
                         visits.get(i).setGpName(dentistVisitDTO.getGpName());
                         dentistVisitService.updateVisit(visits.get(i));
                         model.addAttribute("success", dentistVisitDTO.getVisitTime());
                         return "regdetail";
                     } else{
                         model.addAttribute("exists", dentistVisitDTO.getVisitTime());
                         return "regdetail";
                     }
                 }
             }
         }

        return "redirect:/findreg";
    }

    //Method to validate if user input hours are from mon to fri and 10-16:00

    private boolean areWorkingHours(int hour, int day){
        if (hour<10 || hour>16 || day == 0 || day == 6){
            return false;
        } else{
            return true;
        }
    }

}
