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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import kigali.clinic.rw.domain.Patient;
import kigali.clinic.rw.service.PatientService;

@RestController
@RequestMapping(value = {"/api/patient", "/api/patients"})
public class PatientController {

    @Autowired
    private PatientService patientService;

    @PostMapping(value = {"", "/save"})
    public ResponseEntity<?> savePatient(@RequestBody Patient patient) {
        String result = patientService.savePatient(patient);
        if (result.equals("Patient is saved successfully")) {
            return new ResponseEntity<>(result, HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping(value = {"", "/all"})
    public ResponseEntity<List<Patient>> getAllPatients() {
        return new ResponseEntity<>(patientService.getAllPatients(), HttpStatus.OK);
    }
    
    @GetMapping(value = "/by-last-name")
    public ResponseEntity<List<Patient>> getPatientsByLastName(@RequestParam String lastName) {
        return new ResponseEntity<>(patientService.getPatientsByLastName(lastName), HttpStatus.OK);
}

    @GetMapping(value = "/{id}")
    public ResponseEntity<?> getPatientById(@PathVariable UUID id) {
        Optional<Patient> patient = patientService.getPatientById(id);
        if (patient.isPresent()) {
            return new ResponseEntity<>(patient.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Patient not found with id: " + id, HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping(value = {"/{id}", "/update/{id}"})
    public ResponseEntity<?> updatePatient(@PathVariable UUID id, @RequestBody Patient patient) {
        String result = patientService.updatePatient(id, patient);
        if (result.equals("Patient is updated successfully")) {
            return new ResponseEntity<>(result, HttpStatus.OK);
        } else if (result.contains("not found")) {
            return new ResponseEntity<>(result, HttpStatus.NOT_FOUND);
        } else {
            return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping(value = {"/{id}", "/delete/{id}"})
    public ResponseEntity<?> deletePatient(@PathVariable UUID id) {
        String result = patientService.deletePatient(id);
        if (result.equals("Patient is deleted successfully")) {
            return new ResponseEntity<>(result, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(result, HttpStatus.NOT_FOUND);
        }
    }
}
