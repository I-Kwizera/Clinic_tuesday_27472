package kigali.clinic.rw.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import kigali.clinic.rw.domain.Doctor;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor, UUID> {

    List<Doctor> findByFirstNameAndLastNameAndDateOfBirth(String firstName, String lastName, java.util.Date dateOfBirth);
    Optional<Doctor> findByOfficeId(UUID officeId);
    boolean existsByOfficeId(UUID officeId);
    boolean existsByFirstNameAndLastNameAndDateOfBirth(String firstName, String lastName, java.util.Date dateOfBirth);
    boolean existsByOfficeOfficeNumber(int officeNumber);

@Query("SELECT d FROM Doctor d JOIN d.specializations s WHERE LOWER(s.name) = LOWER(?1)")
List<Doctor> findDoctorsBySpecializationName(String name);

@Query("SELECT d FROM Doctor d WHERE d.office IS NULL ORDER BY d.lastName")
List<Doctor> findDoctorsWithoutOffice();

@Query("SELECT d FROM Doctor d WHERE LOWER(d.firstName) LIKE LOWER(CONCAT('%', :name, '%')) OR LOWER(d.lastName) LIKE LOWER(CONCAT('%', :name, '%')) ORDER BY d.lastName ASC")
List<Doctor> searchDoctors(@Param("name") String name);

}

