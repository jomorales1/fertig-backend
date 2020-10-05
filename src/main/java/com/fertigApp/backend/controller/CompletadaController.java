package com.fertigApp.backend.controller;

import com.fertigApp.backend.repository.CompletadaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CompletadaController {
    @Autowired
    private CompletadaRepository completadaRepository;


}
