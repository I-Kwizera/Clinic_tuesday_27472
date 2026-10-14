package kigali.clinic.rw.repository;

import java.util.List;
import java.util.UUID;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;

import kigali.clinic.rw.domain.Patient;

@Repository
public interface PatientRepository extends JpaRepository<Patient, UUID> {

    List<Patient> findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(String firstName, String lastName);
    List<Patient> findByLastNameIgnoreCaseOrderByFirstNameAsc(String lastName);
   
    Optional<Patient> findBySsNumber(String ssNumber);

@Query("SELECT DISTINCT p FROM Patient p JOIN p.appointments a WHERE a.doctor.id = ?1")

    List<Patient> findPatientsOfDoctor(UUID doctorId);

@Query("SELECT p FROM Patient p JOIN p.appointments a GROUP BY p HAVING COUNT(a) >= ?1 ORDER BY COUNT(a) DESC")
    
    List<Patient> findFrequentPatients(long min);

}
