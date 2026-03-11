package com.tienda.controller;

import com.tienda.domain.Categoria;
import com.tienda.service.CategoriaService;
import jakarta.validation.Valid;
import java.util.Locale;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/categoria")
public class CategoriaController {
    
    @Autowired
    private CategoriaService categoriaService;
    
    @Autowired
    private MessageSource messageSource;
    
    @ModelAttribute("categoria")
    public Categoria categoria() {
        return new Categoria();
    }
    
    @GetMapping("/listado")
    public String inicio(Model model) {
        var categorias = categoriaService.getCategorias(false);
        model.addAttribute("categorias", categorias);
        model.addAttribute("totalCategorias", categorias.size());
        return "/categoria/listado";
    }
    
    @PostMapping("/guardar")
    public String guardar (@Valid Categoria categoria, @RequestParam MultipartFile imagenFile, RedirectAttributes redirectAttributes){
        categoriaService.save(categoria, imagenFile);
        redirectAttributes.addFlashAttribute("todoOk", messageSource.getMessage("mensaje.actualizado", null, Locale.getDefault()));
        return "redirect:/categoria/listado";
    }
    
    @PostMapping("/eliminar")
    public String eliminar (@RequestParam Integer idCategoria, RedirectAttributes redirectAttributes) {
        String titulo="todoOk";
        String detalle="mensaje.eliminado";
        try {
            categoriaService.delete(idCategoria);
        } catch (IllegalArgumentException e) {
            titulo="error";
            detalle="cateogira.error01";
        } catch (IllegalStateException e) {
            titulo="error";
            detalle="cateogira.error02";
        } catch (Exception e) {
            titulo="error";
            detalle="cateogira.error03";
        }

        redirectAttributes.addFlashAttribute (titulo, messageSource.getMessage(detalle, null, Locale.getDefault()));
        return "redirect:/categoria/listado";
    }
    
    @GetMapping ("/modificar/{idCategoria}")
    public String modificar (@PathVariable("idCategoria") Integer idCategoria, Model model, RedirectAttributes redirectAttributes) {
        Optional<Categoria> categoriaOpt = categoriaService.getCategoria (idCategoria);
        if (categoriaOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", messageSource.getMessage("categoria.error01", null, Locale.getDefault()));
            return "redirect:/categoria/listado";
        }
        
        model.addAttribute("categoria", categoriaOpt.get());
        return "/categoria/modifica";
    }

    // PRACTICA 2

    @GetMapping("/conteo")
    public String conteo(Model model) {
        long minProductos = 1;
        String textoDescripcion = "";
        var lista = categoriaService.conteoProductosJPQL(minProductos, textoDescripcion);

        model.addAttribute("minProductos", minProductos);
        model.addAttribute("textoDescripcion", textoDescripcion);
        model.addAttribute("conteos", lista);

        return "/categoria/conteo";
    }

    @PostMapping("/conteoDerivada")
    public String conteoDerivada(@RequestParam long minProductos,
                                 @RequestParam String textoDescripcion,
                                 Model model) {

        var lista = categoriaService.buscarPorDescripcion(textoDescripcion, minProductos);

        model.addAttribute("minProductos", minProductos);
        model.addAttribute("textoDescripcion", textoDescripcion);
        model.addAttribute("conteos", lista);

        return "/categoria/conteo";
    }

    @PostMapping("/conteoJPQL")
    public String conteoJPQL(@RequestParam long minProductos,
                             @RequestParam String textoDescripcion,
                             Model model) {

        var lista = categoriaService.conteoProductosJPQL(minProductos, textoDescripcion);

        model.addAttribute("minProductos", minProductos);
        model.addAttribute("textoDescripcion", textoDescripcion);
        model.addAttribute("conteos", lista);

        return "/categoria/conteo";
    }

    @PostMapping("/conteoSQL")
    public String conteoSQL(@RequestParam long minProductos,
                            @RequestParam String textoDescripcion,
                            Model model) {

        var lista = categoriaService.conteoProductosSQL(minProductos, textoDescripcion);

        model.addAttribute("minProductos", minProductos);
        model.addAttribute("textoDescripcion", textoDescripcion);
        model.addAttribute("conteos", lista);

        return "/categoria/conteo";
    }
}