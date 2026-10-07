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

import kigali.clinic.rw.domain.Specialization;
import kigali.clinic.rw.service.SpecializationService;

@RestController
@RequestMapping(value = "/api/specialization")
public class SpecializationController {

    @Autowired
    private SpecializationService specializationService;

    @PostMapping(value = {"", "/save"})
    public ResponseEntity<?> saveSpecialization(@RequestBody Specialization specialization) {
        String result = specializationService.saveSpecialization(specialization);
        if (result.equals("Specialization is saved successfully")) {
            return new ResponseEntity<>(result, HttpStatus.CREATED);
        } else if (result.contains("already exists")) {
            return new ResponseEntity<>(result, HttpStatus.CONFLICT);
        } else {
            return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping(value = {"", "/all"})
    public ResponseEntity<List<Specialization>> getAllSpecializations() {
        return new ResponseEntity<>(specializationService.getAllSpecializations(), HttpStatus.OK);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<?> getSpecializationById(@PathVariable UUID id) {
        Optional<Specialization> spec = specializationService.getSpecializationById(id);
        if (spec.isPresent()) {
            return new ResponseEntity<>(spec.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Specialization not found with id: " + id, HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping(value = {"/{id}", "/update/{id}"})
    public ResponseEntity<?> updateSpecialization(@PathVariable UUID id, @RequestBody Specialization specialization) {
        String result = specializationService.updateSpecialization(id, specialization);
        if (result.equals("Specialization is updated successfully")) {
            return new ResponseEntity<>(result, HttpStatus.OK);
        } else if (result.contains("not found")) {
            return new ResponseEntity<>(result, HttpStatus.NOT_FOUND);
        } else if (result.contains("already exists")) {
            return new ResponseEntity<>(result, HttpStatus.CONFLICT);
        } else {
            return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping(value = {"/{id}", "/delete/{id}"})
    public ResponseEntity<?> deleteSpecialization(@PathVariable UUID id) {
        String result = specializationService.deleteSpecialization(id);
        if (result.equals("Specialization is deleted successfully")) {
            return new ResponseEntity<>(result, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(result, HttpStatus.NOT_FOUND);
        }
    }
}
