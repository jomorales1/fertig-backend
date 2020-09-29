package com.fertigApp.backend.controller;

import com.fertigApp.backend.model.Tarea;
import com.fertigApp.backend.repository.TareaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class TareaController {
    @Autowired
    private TareaRepository tareaRepository;

    @GetMapping(path="/tasks")
    public @ResponseBody Iterable<Tarea> getAllTareas() {
        return this.tareaRepository.findAll();
    }

    @GetMapping(path="/tasks/{user}")
    public Iterable<Tarea> getAllTareasByUsuario() {
        return null;
    }

    @GetMapping(path="/tasks/{id}")
    public Tarea getTarea(@PathVariable Integer id) {
        return this.tareaRepository.findById(id).get();
    }

    @PutMapping(path="/tasks/{id}")
    public Tarea replaceTarea(@PathVariable Integer id, @RequestBody Tarea task) {
        return this.tareaRepository.findById(id)
                .map(tarea -> {
                    tarea = task;
                    this.tareaRepository.save(tarea);
                    return tarea;
                })
                .orElseGet(() -> {
                    this.tareaRepository.save(task);
                    return task;
                });
    }

    @PostMapping(path="/tasks")
    public @ResponseBody String addNewTarea(@RequestBody Tarea tarea) {
        this.tareaRepository.save(tarea);
        return "Saved";
    }

    @DeleteMapping(path="/tasks/{id}")
    public void deleteTarea(@RequestParam Integer id) {
        this.tareaRepository.deleteById(id);
    }
}
