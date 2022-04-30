package mk.ukim.finki.covid19_statistics.model;

import lombok.Data;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
public class Patient {

    @Id
    private Long ssn;

    private String name;

    private String surname;

    private Long uhid;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Doctor doctor;

    @OneToMany
    private List<Referral> referrals = new ArrayList<>();

    @OneToMany
    private List<Visit> visits = new ArrayList<>();

    public Patient() {
    }
    public String getNameAndSurname() {
        return name+ " "+surname;
    }
    public Patient(Long ssn, Long uhid, Doctor doctor, List<Referral> referrals, List<Visit> visits) {
        this.ssn = ssn;
        this.uhid = uhid;
        this.doctor = doctor;
        this.referrals = referrals;
        this.visits = visits;
    }
}
