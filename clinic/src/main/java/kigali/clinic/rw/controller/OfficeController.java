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

import kigali.clinic.rw.domain.Office;
import kigali.clinic.rw.service.OfficeService;

@RestController 
@RequestMapping (value = {"/api/office", "/api/offices"})
public class OfficeController {

    @Autowired 
    private OfficeService offServe;

    @PostMapping(value = {"", "/save"})  
    public ResponseEntity<?> saveOffice(@RequestBody Office office) {
        String returnedMessage = offServe.saveOffice(office);

        if (returnedMessage.equals("Office is saved successfully")) {
            return new ResponseEntity<>(returnedMessage, HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>(returnedMessage, HttpStatus.CONFLICT);
        }
    }

    @GetMapping(value = {"", "/all"})
    public ResponseEntity<List<Office>> getAllOffices() {
        return new ResponseEntity<>(offServe.getAllOffices(), HttpStatus.OK);
    }

    @GetMapping(value = "/busiest")
    public ResponseEntity<?> getBusiestOffice() {
        Optional<Object[]> office = offServe.getBusiestOffice();
        if (office.isPresent()) {
            return new ResponseEntity<>(office.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>("No appointments yet", HttpStatus.OK);
        }
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<?> getOfficeById(@PathVariable UUID id) {
        Optional<Office> office = offServe.getOfficeById(id);
        if (office.isPresent()) {
            return new ResponseEntity<>(office.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Office not found with id: " + id, HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping(value = {"/{id}", "/update/{id}"})
    public ResponseEntity<?> updateOffice(@PathVariable UUID id, @RequestBody Office office) {
        String result = offServe.updateOffice(id, office);
        if (result.equals("Office is updated successfully")) {
            return new ResponseEntity<>(result, HttpStatus.OK);
        } else if (result.contains("not found")) {
            return new ResponseEntity<>(result, HttpStatus.NOT_FOUND);
        } else {
            return new ResponseEntity<>(result, HttpStatus.CONFLICT);
        }
    }

    @DeleteMapping(value = {"/{id}", "/delete/{id}"})
    public ResponseEntity<?> deleteOffice(@PathVariable UUID id) {
        String result = offServe.deleteOffice(id);
        if (result.equals("Office is deleted successfully")) {
            return new ResponseEntity<>(result, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(result, HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/free")
    public ResponseEntity<List<Office>> getFreeOffices() {
        List<Office> freeOffices = offServe.getFreeOffices();
        return ResponseEntity.ok(freeOffices);
    }
}
