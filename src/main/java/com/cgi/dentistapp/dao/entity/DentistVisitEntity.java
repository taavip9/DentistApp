package com.cgi.dentistapp.dao.entity;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "dentist_visit")
public class DentistVisitEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "visit_time")
    private Date visitTime;

    @Column(name = "gp_name")
    private String gpName;

    @Column(name = "dentist_name")
    private String dentistName;

    public DentistVisitEntity() {
    }

    public DentistVisitEntity(String gpName, String dentistName, Date visitTime) {
        this.setVisitTime(visitTime);
        this.setGpName(gpName);
        this.setDentistName(dentistName);
    }

    public String getGpName() { return gpName; }

    public void setGpName(String gpName){ this.gpName = gpName; }

    public String getDentistName() { return dentistName; }

    public void setDentistName(String dentistName){ this.dentistName = dentistName; }

    public Long getId() { return id; }

    public Date getVisitTime() {
        return visitTime;
    }

    public void setVisitTime(Date visitTime) {
        this.visitTime = visitTime;
    }

}
