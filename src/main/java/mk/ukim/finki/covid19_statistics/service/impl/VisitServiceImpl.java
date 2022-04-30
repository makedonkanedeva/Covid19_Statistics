package mk.ukim.finki.covid19_statistics.service.impl;

import mk.ukim.finki.covid19_statistics.model.Doctor;
import mk.ukim.finki.covid19_statistics.model.Patient;
import mk.ukim.finki.covid19_statistics.model.Referral;
import mk.ukim.finki.covid19_statistics.model.Visit;
import mk.ukim.finki.covid19_statistics.model.exceptions.*;
import mk.ukim.finki.covid19_statistics.repository.DoctorRepository;
import mk.ukim.finki.covid19_statistics.repository.PatientRepository;
import mk.ukim.finki.covid19_statistics.repository.ReferralRepository;
import mk.ukim.finki.covid19_statistics.repository.VisitRepository;
import mk.ukim.finki.covid19_statistics.service.VisitService;
import org.springframework.stereotype.Service;
import java.util.*;

import java.time.LocalDateTime;

@Service
public class VisitServiceImpl implements VisitService {

    private final VisitRepository visitRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final ReferralRepository referralRepository;

    public VisitServiceImpl(VisitRepository visitRepository, DoctorRepository doctorRepository, PatientRepository patientRepository, ReferralRepository referralRepository) {
        this.visitRepository = visitRepository;
        this.doctorRepository = doctorRepository;
        this.patientRepository = patientRepository;
        this.referralRepository = referralRepository;
    }

    @Override
    public List<Visit> findByPatientSsn(Long patientSsn) {
        return visitRepository.findByPatientSsn(patientSsn);
    }

    @Override
    public List<Visit> findByPatientUhid(Long patientUhid) {
        return visitRepository.findByPatientUhid(patientUhid);
    }

    @Override
    public List<Visit> findByDoctorSsn(Long doctorSsn) {
        return visitRepository.findByDoctorSsn(doctorSsn);
    }

    @Override
    public Visit findByTerm(LocalDateTime term) {
        return visitRepository.findByTerm(term);
    }

    @Override
    public List<Visit> findByPatientSsnAndDoctorSsn(Long patientSsn, Long doctorSsn) {
        return visitRepository.findByPatientSsnAndDoctorSsn(patientSsn, doctorSsn);
    }

    @Override
    public List<Visit> findAll() {
        return this.visitRepository.findAll();
    }

    @Override
    public List<Visit> filterTwo(Long patientSsn) {
        if(patientSsn == null){
            return this.visitRepository.findAll();
        }
        else{
            List<Visit> visits = this.visitRepository.findByPatientSsn(patientSsn);
            return visits;
        }
    }

    @Override
    public List<Visit> filter(Long patientSsn, Long doctorSsn) {
        if(patientSsn != null && doctorSsn != null){
            List<Visit> visits = this.visitRepository.findByPatientSsnAndDoctorSsn(patientSsn,doctorSsn);
            return visits;
        }
        else if(patientSsn != null && doctorSsn == null){
            List<Visit> visits = this.visitRepository.findByPatientSsn(patientSsn);
            return visits;
        }
        else if(patientSsn == null && doctorSsn != null){
            List<Visit> visits = this.visitRepository.findByDoctorSsn(doctorSsn);
            return visits;
        }
        else
        return this.visitRepository.findAll();
    }

    @Override
    public Visit create(LocalDateTime term, String patientName, String patientSurname, Long patientSsn,
                        Long doctorSsn) {

        if (term == null || patientSsn == null  || patientName == null || patientName.isEmpty() ||
            patientSurname == null || patientSurname.isEmpty() || doctorSsn == null || doctorSsn == 0) {
            throw new InvalidArgumentException();
        }
        //TODO: To handle wrong name and surname exception

        if (visitRepository.findByTerm(term) != null) {
            throw new TermIsNotAvailableException(term);
        }
        if(term.isBefore(LocalDateTime.now())){
            throw new TermIsNotAllowedException();
        }
        if(patientRepository.findBySsnAndNameAndSurname(patientSsn,patientName,patientSurname) == null){
            throw new PatientDoesNotExistException();
        }




        Patient p = patientRepository.findBySsn(patientSsn);
        Doctor d = this.doctorRepository.findBySsn(doctorSsn);
        Visit v = new Visit(term, d, p);
        visitRepository.save(v);
        return v;
    }

    @Override
    public Visit delete(Long id) {
        Visit visit = this.visitRepository.findById(id).orElseThrow(VisitNotFoundException::new);
        LocalDateTime visitTerm = visit.getTerm();
        if(this.referralRepository.findAll().stream()
                .filter(i -> i.getTerm().isEqual(visitTerm)).findFirst().isPresent()){
            throw new CannotDeleteVisitException();
        }
        else
        this.visitRepository.delete(visit);
        return visit;
    }

    @Override
    public Visit findById(Long id) {
        return this.visitRepository.findById(id).orElseThrow(()-> new VisitNotFoundException());
    }

    @Override
    public Visit edit(Long id, LocalDateTime term, String patientName, String patientSurname, Long patientSsn, Long doctorSsn) {
        Visit visit = this.visitRepository.findById(id).orElseThrow(() -> new VisitNotFoundException());
        visit.setTerm(term);
        Patient patient = this.patientRepository.findBySsn(patientSsn);
        visit.setPatient(patient);
        Doctor doctor = this.doctorRepository.findBySsn(doctorSsn);
        visit.setDoctor(doctor);
        this.visitRepository.save(visit);
        return visit;
    }
}
