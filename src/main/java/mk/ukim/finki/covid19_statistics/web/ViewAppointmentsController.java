package mk.ukim.finki.covid19_statistics.web;


import mk.ukim.finki.covid19_statistics.model.Doctor;
import mk.ukim.finki.covid19_statistics.model.Patient;
import mk.ukim.finki.covid19_statistics.model.Visit;
import mk.ukim.finki.covid19_statistics.service.DoctorService;
import mk.ukim.finki.covid19_statistics.service.PatientService;
import mk.ukim.finki.covid19_statistics.service.VisitService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Controller
@RequestMapping(value = {"/appointments"})
public class ViewAppointmentsController {

    private final PatientService patientService;
    private final DoctorService doctorService;
    private final VisitService visitService;

    public ViewAppointmentsController(PatientService patientService, DoctorService doctorService, VisitService visitService) {
        this.patientService = patientService;
        this.doctorService = doctorService;
        this.visitService = visitService;
    }

    @GetMapping
    public String getAppointmentPage(@RequestParam(required = false) Long patientSsn,
                                     @RequestParam(required = false) Long doctorSsn,
                                     Model model) {

        List<Doctor> doctorList = this.doctorService.findAll();
        List<Patient> patientList = this.patientService.findAll();
        List<Visit> visitList;

        if (patientSsn == null && doctorSsn == null) {
            visitList = this.visitService.findAll();
        } else {

            visitList = this.visitService.filter(patientSsn, doctorSsn);
        }
        model.addAttribute("doctors", doctorList);
        model.addAttribute("patients", patientList);
        model.addAttribute("visits", visitList);


        model.addAttribute("bodyContent", "appointment");
        return "master-template";
    }

    @GetMapping("/add-appointment-form")
    public String addAppointment(@RequestParam(required = false) String error, Model model) {
        if (error != null && !error.isEmpty()) {
            model.addAttribute("hasError", true);
            model.addAttribute("error", error);
        }
        List<Doctor> doctorList = this.doctorService.findAll();
        List<Patient> patientList = this.patientService.findAll();

        model.addAttribute("doctors", doctorList);
        model.addAttribute("patients", patientList);
        model.addAttribute("bodyContent", "add-appointment");
        return "master-template";
    }

    @DeleteMapping("/{id}/delete")
    public String deleteAppointment(@PathVariable Long id) {
        this.visitService.delete(id);
        return "redirect:/appointments";
    }
}
