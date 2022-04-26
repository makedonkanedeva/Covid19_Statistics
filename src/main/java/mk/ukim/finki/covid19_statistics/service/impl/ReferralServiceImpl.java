package mk.ukim.finki.covid19_statistics.service.impl;

import mk.ukim.finki.covid19_statistics.model.Doctor;
import mk.ukim.finki.covid19_statistics.model.Patient;
import mk.ukim.finki.covid19_statistics.model.Referral;
import mk.ukim.finki.covid19_statistics.model.exceptions.*;
import mk.ukim.finki.covid19_statistics.repository.DoctorRepository;
import mk.ukim.finki.covid19_statistics.repository.PatientRepository;
import mk.ukim.finki.covid19_statistics.repository.ReferralRepository;
import mk.ukim.finki.covid19_statistics.repository.VisitRepository;
import mk.ukim.finki.covid19_statistics.service.ReferralService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ReferralServiceImpl implements ReferralService {

    private final ReferralRepository referralRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final VisitRepository visitRepository;

    public ReferralServiceImpl(ReferralRepository referralRepository, DoctorRepository doctorRepository, PatientRepository patientRepository, VisitRepository visitRepository) {
        this.referralRepository = referralRepository;
        this.doctorRepository = doctorRepository;
        this.patientRepository = patientRepository;
        this.visitRepository = visitRepository;
    }

    @Override
    public Referral create(LocalDateTime term, String patientName, String patientSurname, Long patientSsn,
                           Long forwardBySsn, Long forwardToSsn) {
        if (term == null || patientName == null || patientSurname == null ||  patientSsn == null) {
            throw new WrongDataEnteredException();
        }
        if(forwardBySsn == null || forwardToSsn == null)
        {
            throw new DoctorNotSelectedException();
        }
        if(term.isBefore(LocalDateTime.now())){
            throw new TermIsNotAllowedException();
        }
        if(visitRepository.findByTerm(term) != null){
            throw new TermIsNotAvailableException(term);
        }
        if(patientRepository.findBySsnAndNameAndSurname(patientSsn,patientName,patientSurname) == null)
        {
            throw new PatientDoesNotExistException();
        }




        Patient p = patientRepository.findBySsn(patientSsn);
        Doctor d1 = doctorRepository.findBySsn(forwardBySsn);
        Doctor d2 = doctorRepository.findBySsn(forwardToSsn);

        Referral r = new Referral(term, p, d1, d2);
        referralRepository.save(r);
        return r;
    }

    @Override
    public Referral edit(Long id, LocalDateTime term, String patientName, String patientSurname, Long patientSsn, Long forwardBySsn, Long forwardToSsn) {
        if (term == null || patientName == null || patientSurname == null ||  patientSsn == null) {
            throw new WrongDataEnteredException();
        }




        Referral referral = this.referralRepository.findById(id).orElseThrow(() ->new ReferralNotFoundException(id));
        Patient patient = this.patientRepository.findBySsnAndNameAndSurname(patientSsn,patientName,patientSurname);
        if(patientRepository.findBySsnAndNameAndSurname(patientSsn,patientName,patientSurname) == null)
        {
            throw new PatientDoesNotExistException();
        }
        if(forwardBySsn == null || forwardToSsn == null)
        {
            throw new DoctorNotSelectedException();
        }
        Doctor doctor = this.doctorRepository.findBySsn(forwardBySsn);
        Doctor doctor2 = this.doctorRepository.findBySsn(forwardToSsn);
        if(visitRepository.findByTerm(term) != null){
            throw new TermIsNotAvailableException(term);
        }
        if(term.isBefore(LocalDateTime.now())){
            throw new TermIsNotAllowedException();
        }
        referral.setTerm(term);
        referral.setSsnPatient(patient);
        referral.setForwardBy(doctor);
        referral.setForwardTo(doctor2);
        this.referralRepository.save(referral);
        return referral;
    }

    @Override
    public Referral deleteById(Long id) {
        Referral referral = this.referralRepository.findById(id).orElseThrow(()-> new ReferralNotFoundException(id));
        this.referralRepository.delete(referral);
        return referral;
    }

    @Override
    public List<Referral> findAll() {
        return this.referralRepository.findAll();
    }

    @Override
    public List<Referral> filter(Long patientSsn) {
        if(patientSsn != null){
            Patient patient = this.patientRepository.findBySsn(patientSsn);
            return this.referralRepository.findBySsnPatient(patient);
        }
        else
        return this.referralRepository.findAll();
    }

    @Override
    public Referral findById(Long id) {
        return this.referralRepository.findById(id).orElseThrow(() -> new ReferralNotFoundException(id));
    }


}
