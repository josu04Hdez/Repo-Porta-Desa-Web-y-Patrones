package com.tienda.controller;

import com.tienda.service.ProductoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.math.BigDecimal;

@Controller
@RequestMapping("/consultas")
public class ConsultasController {

    private final ProductoService productoService;

    public ConsultasController(ProductoService productoService) {
        this.productoService = productoService;
    }

    @GetMapping("/listado")
    public String listado(Model model) {
        var lista = productoService.getProductos(false);
        model.addAttribute("productos", lista);
        return "/consultas/listado";
    }

    // ===============================
    // CONSULTAS ANTERIORES (las de precio)
    // ===============================

    @PostMapping("/consultaDerivada")
    public String consultaDerivada(@RequestParam double precioInf,
                                   @RequestParam double precioSup,
                                   Model model) {
        var lista = productoService.consultaDerivada(precioInf, precioSup);
        model.addAttribute("productos", lista);
        return "/consultas/listado";
    }

    @PostMapping("/consultaJPQL")
    public String consultaJPQL(@RequestParam double precioInf,
                               @RequestParam double precioSup,
                               Model model) {
        var lista = productoService.consultaJPQL(precioInf, precioSup);
        model.addAttribute("productos", lista);
        return "/consultas/listado";
    }

    @PostMapping("/consultaSQL")
    public String consultaSQL(@RequestParam double precioInf,
                              @RequestParam double precioSup,
                              Model model) {
        var lista = productoService.consultaSQL(precioInf, precioSup);
        model.addAttribute("productos", lista);
        return "/consultas/listado";
    }

    // PRACTICA 2 - CONSULTA AVANZADA PRODUCTO

    @PostMapping("/consultaAvanzadaDerivada")
    public String consultaAvanzadaDerivada(@RequestParam BigDecimal precioMin,
                                           @RequestParam BigDecimal precioMax,
                                           @RequestParam Integer existenciasMin,
                                           @RequestParam String descripcionCategoria,
                                           Model model) {
        var lista = productoService.consultaAvanzadaDerivada(
                precioMin, precioMax, existenciasMin, descripcionCategoria);
        model.addAttribute("productos", lista);
        return "/consultas/listado";
    }

    @PostMapping("/consultaAvanzadaJPQL")
    public String consultaAvanzadaJPQL(@RequestParam BigDecimal precioMin,
                                       @RequestParam BigDecimal precioMax,
                                       @RequestParam Integer existenciasMin,
                                       @RequestParam String descripcionCategoria,
                                       Model model) {
        var lista = productoService.consultaAvanzadaJPQL(
                precioMin, precioMax, existenciasMin, descripcionCategoria);
        model.addAttribute("productos", lista);
        return "/consultas/listado";
    }

    @PostMapping("/consultaAvanzadaSQL")
    public String consultaAvanzadaSQL(@RequestParam BigDecimal precioMin,
                                      @RequestParam BigDecimal precioMax,
                                      @RequestParam Integer existenciasMin,
                                      @RequestParam String descripcionCategoria,
                                      Model model) {
        var lista = productoService.consultaAvanzadaSQL(
                precioMin, precioMax, existenciasMin, descripcionCategoria);
        model.addAttribute("productos", lista);
        return "/consultas/listado";
    }
}