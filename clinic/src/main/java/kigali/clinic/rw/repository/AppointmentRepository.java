package kigali.clinic.rw.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import kigali.clinic.rw.domain.Appointment;
import kigali.clinic.rw.domain.AppointmentStatus;
import java.sql.Date;
import java.time.LocalDate;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, UUID> {

    List<Appointment> findByPatientId(UUID patientId);
    List<Appointment> findByDoctorId(UUID doctorId);
    List<Appointment> findByStatus(AppointmentStatus status);
    List<Appointment> findByStatusOrderByAppointmentDateAsc(AppointmentStatus status);
    List<Appointment> findByAppointmentDateBetweenOrderByAppointmentDateAsc(Date start, Date end);
    boolean existsByDoctorIdAndAppointmentDateAndStatusNot(UUID doctorId, Date appointmentDate, AppointmentStatus status);
    List<Appointment> findByDoctorIdAndDateAndStatusNot(UUID doctorId, LocalDate date, AppointmentStatus status);

@Query("SELECT a.status, COUNT(a) FROM Appointment a GROUP BY a.status")
    
    List<Object[]> countAppointmentsByStatus();

@Modifying
@Query("UPDATE Appointment a SET a.status = kigali.clinic.rw.domain.AppointmentStatus.CANCELLED WHERE a.doctor.id = ?1 AND a.appointmentDate = ?2 AND a.status <> kigali.clinic.rw.domain.AppointmentStatus.COMPLETED")
    int cancelDay(UUID doctorId, Date date);    

@Modifying
@Query("DELETE FROM Appointment a WHERE a.status = kigali.clinic.rw.domain.AppointmentStatus.CANCELLED AND a.appointmentDate < ?1")
int deleteCancelledBefore(Date date);
    
}
