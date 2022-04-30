package mk.ukim.finki.covid19_statistics.service.impl;

import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfEncodings;
import com.lowagie.text.pdf.PdfWriter;
import mk.ukim.finki.covid19_statistics.model.Doctor;
import mk.ukim.finki.covid19_statistics.model.Patient;
import mk.ukim.finki.covid19_statistics.model.Referral;
import mk.ukim.finki.covid19_statistics.model.exceptions.*;
import mk.ukim.finki.covid19_statistics.repository.DoctorRepository;
import mk.ukim.finki.covid19_statistics.repository.PatientRepository;
import mk.ukim.finki.covid19_statistics.repository.ReferralRepository;
import mk.ukim.finki.covid19_statistics.repository.VisitRepository;
import mk.ukim.finki.covid19_statistics.service.ReferralService;

import org.apache.tomcat.util.buf.Utf8Encoder;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static com.lowagie.text.FontFactory.*;

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

    @Override
    public void export(HttpServletResponse response,Long idReferral) throws IOException {
        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document,response.getOutputStream());

        document.open();


        String patientName = this.referralRepository.findById(idReferral).get().getSsnPatient().getNameAndSurname();
        LocalDateTime term = this.referralRepository.findById(idReferral).get().getTerm();
        Long patientSsn = this.referralRepository.findById(idReferral).get().getSsnPatient().getSsn();
        String doctorForwards = this.referralRepository.findById(idReferral).get().getForwardBy().getNameAndSurname();
        String doctorForwarded = this.referralRepository.findById(idReferral).get().getForwardTo().getNameAndSurname();
        String specialty = this.referralRepository.findById(idReferral).get().getForwardTo().getSpecialty().getName();

        DateTimeFormatter format = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        String termReferral = term.format(format);


        Paragraph paragraph2 = new Paragraph("Информации за упатот: ");
        Paragraph paragraph = new Paragraph("Пациент: " + patientName);
        Paragraph paragraph3 = new Paragraph("Термин: " + termReferral);
        Paragraph paragraph4 = new Paragraph("ЕМБГ: " + patientSsn);
        Paragraph paragraph5 = new Paragraph("Доктор кој препраќа: " + doctorForwards);
        Paragraph paragraph6 = new Paragraph("Доктор кај кој е препратен пациентот: " + doctorForwarded);
        Paragraph paragraph7 = new Paragraph("Специјалност на доктор: " + specialty);

        paragraph.setAlignment(Paragraph.ALIGN_LEFT);
        paragraph2.setAlignment(Paragraph.ALIGN_CENTER);
        paragraph3.setAlignment(Paragraph.ALIGN_LEFT);
        paragraph4.setAlignment(Paragraph.ALIGN_LEFT);
        paragraph5.setAlignment(Paragraph.ALIGN_LEFT);
        paragraph6.setAlignment(Paragraph.ALIGN_LEFT);
        paragraph7.setAlignment(Paragraph.ALIGN_LEFT);


        paragraph2.setSpacingAfter(15);
        document.add(paragraph2);
        document.add(paragraph);
        document.add(paragraph3);
        document.add(paragraph4);
        document.add(paragraph5);
        document.add(paragraph6);
        document.add(paragraph7);
        document.close();
    }


}
