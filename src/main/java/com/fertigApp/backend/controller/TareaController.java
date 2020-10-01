package com.fertigApp.backend.controller;

import com.fertigApp.backend.model.Tarea;
import com.fertigApp.backend.repository.TareaRepository;
import com.fertigApp.backend.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class TareaController {
    @Autowired
    private TareaRepository tareaRepository;
    @Autowired
    private UsuarioRepository usuarioRepository;

    @GetMapping(path="/tasks")
    public @ResponseBody Iterable<Tarea> getAllTareas() {
        return this.tareaRepository.findAll();
    }

    @GetMapping(path="/tasks/{user}/getTasks")
    public Iterable<Tarea> getAllTareasByUsuario(@RequestParam String usuario) {
        try {
            return usuarioRepository.findById(usuario).get().getTareas();
        } catch(java.util.NoSuchElementException ex){
            return null;
        }
    }

    @GetMapping(path="/tasks/{user}/{id}")
    public Tarea getTarea(@PathVariable Integer id) {
        return this.tareaRepository.findById(id).get();
    }

    @PutMapping(path="/tasks/{user}/updateTask/{id}")
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

    @PostMapping(path="/tasks/addTask")
    public @ResponseBody String addNewTarea(@RequestBody Tarea tarea) {
        this.tareaRepository.save(tarea);
        return "Saved";
    }

    @DeleteMapping(path="/tasks/{user}/deleteTask/{id}")
    public void deleteTarea(@RequestParam Integer id) {
        this.tareaRepository.deleteById(id);
    }
}
