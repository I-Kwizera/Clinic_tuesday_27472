package kigali.clinic.rw.controller;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import kigali.clinic.rw.domain.Doctor;
import kigali.clinic.rw.service.DoctorService;

@RestController
@RequestMapping(value = "/api/doctor")
public class DoctorController {

    @Autowired
    private DoctorService doctorService;

    @PostMapping(value = {"", "/save"})
    public ResponseEntity<?> saveDoctor(@RequestBody Doctor doctor) {
        String result = doctorService.saveDoctor(doctor);
        if (result.equals("Doctor is saved successfully")) {
            return new ResponseEntity<>(result, HttpStatus.CREATED);
        } else if (result.contains("not found")) {
            return new ResponseEntity<>(result, HttpStatus.NOT_FOUND);
        } else {
            return new ResponseEntity<>(result, HttpStatus.CONFLICT);
        }
    }

    @GetMapping(value = {"", "/all"})
    public ResponseEntity<List<Doctor>> getAllDoctors() {
        return new ResponseEntity<>(doctorService.getAllDoctors(), HttpStatus.OK);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<?> getDoctorById(@PathVariable UUID id) {
        Optional<Doctor> doctor = doctorService.getDoctorById(id);
        if (doctor.isPresent()) {
            return new ResponseEntity<>(doctor.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Doctor not found with id: " + id, HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping(value = {"/{id}", "/update/{id}"})
    public ResponseEntity<?> updateDoctor(@PathVariable UUID id, @RequestBody Doctor doctor) {
        String result = doctorService.updateDoctor(id, doctor);
        if (result.equals("Doctor is updated successfully")) {
            return new ResponseEntity<>(result, HttpStatus.OK);
        } else if (result.contains("not found")) {
            return new ResponseEntity<>(result, HttpStatus.NOT_FOUND);
        } else {
            return new ResponseEntity<>(result, HttpStatus.CONFLICT);
        }
    }

    @DeleteMapping(value = {"/{id}", "/delete/{id}"})
    public ResponseEntity<?> deleteDoctor(@PathVariable UUID id) {
        String result = doctorService.deleteDoctor(id);
        if (result.equals("Doctor is deleted successfully")) {
            return new ResponseEntity<>(result, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(result, HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping(value = "/{doctorId}/assign-office/{officeId}")
    public ResponseEntity<?> assignOffice(@PathVariable UUID doctorId, @PathVariable UUID officeId) {
        String result = doctorService.assignOffice(doctorId, officeId);
        if (result.equals("Office assigned successfully to doctor")) {
            return new ResponseEntity<>(result, HttpStatus.OK);
        } else if (result.contains("not found")) {
            return new ResponseEntity<>(result, HttpStatus.NOT_FOUND);
        } else {
            return new ResponseEntity<>(result, HttpStatus.CONFLICT);
        }
    }

    @PutMapping(value = "/{doctorId}/add-specialization/{specializationId}")
    public ResponseEntity<?> addSpecialization(@PathVariable UUID doctorId, @PathVariable UUID specializationId) {
        String result = doctorService.addSpecialization(doctorId, specializationId);
        if (result.equals("Specialization added successfully to doctor")) {
            return new ResponseEntity<>(result, HttpStatus.OK);
        } else if (result.contains("not found")) {
            return new ResponseEntity<>(result, HttpStatus.NOT_FOUND);
        } else {
            return new ResponseEntity<>(result, HttpStatus.CONFLICT);
        }
    }
}
