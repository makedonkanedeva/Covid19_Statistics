package mk.ukim.finki.covid19_statistics.web;

import mk.ukim.finki.covid19_statistics.model.Doctor;
import mk.ukim.finki.covid19_statistics.model.Patient;
import mk.ukim.finki.covid19_statistics.model.Referral;
import mk.ukim.finki.covid19_statistics.model.exceptions.*;
import mk.ukim.finki.covid19_statistics.service.DoctorService;
import mk.ukim.finki.covid19_statistics.service.PatientService;
import mk.ukim.finki.covid19_statistics.service.ReferralService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;


@Controller
@RequestMapping("/referrals")
public class ReferralController {

    private final ReferralService referralService;
    private final DoctorService doctorService;
    private final PatientService patientService;

    public ReferralController(ReferralService referralService, DoctorService doctorService, PatientService patientService) {
        this.referralService = referralService;
        this.doctorService = doctorService;
        this.patientService = patientService;
    }


    @GetMapping
    public String getReferralPage(@RequestParam(required = false) Long patientSsn, Model model){

        List<Referral> referrals;
        List<Doctor> doctors = this.doctorService.findAll();
        List<Patient> patients = this.patientService.findAll();

        if(patientSsn == null){
            referrals = this.referralService.findAll();
        }
        else{
            referrals = this.referralService.filter(patientSsn);
        }
        model.addAttribute("referrals", referrals);
        model.addAttribute("bodyContent", "referral");

        return "master-template";
    }
    @GetMapping("/add-referral")
    public String getReferralAddTemplate(Model model, @RequestParam(required = false) String error){
        if(error != null && !error.isEmpty()){
            model.addAttribute("hasError", true);
            model.addAttribute("error", error);
        }
        List<Doctor> doctors = this.doctorService.findAll();

        model.addAttribute("doctors", doctors);
        model.addAttribute("bodyContent", "add-referral");

        return "master-template";
    }
    @PostMapping("/add")
    public String getReferralPage(@RequestParam(required = false) Long id,
                                  @RequestParam(required = false) String localDate,
                                  @RequestParam String patientName,
                                  @RequestParam String patientSurname,
                                  @RequestParam Long patientSsn,
                                  @RequestParam Long doctorForward,
                                  @RequestParam(required = false) Long doctorTo
                                  )
    {
        if(id == null){
            try {
                this.referralService.create(LocalDateTime.parse(localDate), patientName, patientSurname, patientSsn, doctorForward, doctorTo);
            }catch (WrongDataEnteredException | DoctorNotSelectedException| TermIsNotAllowedException|
                    TermIsNotAvailableException | PatientDoesNotExistException exception){
                return "redirect:/referrals/add-referral?error=" +exception.getMessage();
            }
        }
        else{
            try {
                this.referralService.edit(id, LocalDateTime.parse(localDate), patientName, patientSurname, patientSsn, doctorForward, doctorTo);
            }catch (WrongDataEnteredException | DoctorNotSelectedException| TermIsNotAllowedException|
            TermIsNotAvailableException | PatientDoesNotExistException exception){
        return "redirect:/referrals/add-referral?error=" +exception.getMessage();
    }

        }



    return "redirect:/referrals";
    }
    @GetMapping("/{id}/edit-form")
    public String editReferral(@PathVariable Long id, Model model){
        Referral referral = this.referralService.findById(id);
        List<Doctor> doctors = this.doctorService.findAll();
        model.addAttribute("referral", referral);
        model.addAttribute("doctors", doctors);
        model.addAttribute("bodyContent", "add-referral");

        return "master-template";
    }

    @DeleteMapping("/{id}/delete")
    public String deleteReferral(@PathVariable Long id){
        this.referralService.deleteById(id);
        return "redirect:/referrals";
    }
}
