package com.cgi.dentistapp.service;

import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cgi.dentistapp.dao.DentistVisitDao;
import com.cgi.dentistapp.dao.entity.DentistVisitEntity;

@Service
@Transactional
public class DentistVisitService {

    @Autowired
    private DentistVisitDao dentistVisitDao;

    public void addVisit(String gpName, String dentistName, Date visitTime) {
        DentistVisitEntity visit = new DentistVisitEntity(gpName, dentistName, visitTime);
        dentistVisitDao.create(visit);
    }

    public void removeVisit(DentistVisitEntity visit) {
        dentistVisitDao.delete(visit);
    }

    public void updateVisit(DentistVisitEntity visit){
        dentistVisitDao.update(visit);
    }

    public List<DentistVisitEntity> listVisits () {
        return dentistVisitDao.getAllVisits();
    }

    //Method to check if a registration already exists
    public boolean doesExist(String dentistName, Date visitTime){
        List<DentistVisitEntity> visits = dentistVisitDao.getAllVisits();
        boolean exists = false;

        for(int i = 0; i!=visits.size();i++) {

            if (dentistName.equals(visits.get(i).getDentistName())
                    && visitTime.equals(visits.get(i).getVisitTime())) {
                exists = true;
            }


        }
        return exists;
    }

}
