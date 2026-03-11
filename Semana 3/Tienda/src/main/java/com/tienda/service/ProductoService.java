package com.tienda.service;

import com.tienda.domain.Producto;
import com.tienda.repository.ProductoRepository;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ProductoService {

    private final ProductoRepository productoRepository;
    private final FirebaseStorageService firebaseStorageService;

    public ProductoService(ProductoRepository productoRepository, FirebaseStorageService firebaseStorageService) {
        this.productoRepository = productoRepository;
        this.firebaseStorageService = firebaseStorageService;
    }

    @Transactional(readOnly = true)
    public List<Producto> getProductos(boolean activo) {
        if (activo) {            
            return productoRepository.findByActivoTrue();
        }
        return productoRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Producto> getProducto(Integer idProducto) {
        return productoRepository.findById(idProducto);
    }

    @Transactional
    public void save(Producto producto, MultipartFile imagenFile) {
        producto = productoRepository.save(producto);
        if (!imagenFile.isEmpty()) {             
            try {
                String rutaImagen = firebaseStorageService.uploadImage(
                        imagenFile, "producto",
                        producto.getIdProducto());
                producto.setRutaImagen(rutaImagen);
                productoRepository.save(producto);
            } catch (IOException e) {

            }
        }
    }

    @Transactional
    public void delete(Integer idProducto) {
        // Verifica si la categoría existe antes de intentar eliminarlo
        if (!productoRepository.existsById(idProducto)) {
            throw new IllegalArgumentException("La categoría con ID " + idProducto + " no existe.");
        }
        try {
            productoRepository.deleteById(idProducto);
        } catch (DataIntegrityViolationException e) {
            throw new IllegalStateException("No se puede eliminar la producto. Tiene datos asociados.", e);
        }
    }

    @Transactional(readOnly = true)
    public List<Producto> consultaDerivada(double precioInf, double precioSup) {
        return productoRepository.findByPrecioBetweenOrderByPrecioAsc(precioInf, precioSup);
    }

    @Transactional(readOnly = true)
    public List<Producto> consultaJPQL(double precioInf, double precioSup) {
        return productoRepository.consultaJPQL(precioInf, precioSup);
    }

    @Transactional(readOnly = true)
    public List<Producto> consultaSQL(double precioInf, double precioSup) {
        return productoRepository.consultaSQL(precioInf, precioSup);
    }

    //CONSULTAS AVANZADAS - PRACTICA 2
 

    @Transactional(readOnly = true)
    public List<Producto> consultaAvanzadaDerivada(BigDecimal precioMin, BigDecimal precioMax,
                Integer existenciasMin, String descripcionCategoria) {
                return productoRepository
                .findByActivoTrueAndPrecioBetweenAndExistenciasGreaterThanAndCategoriaActivoTrueAndCategoriaDescripcionContainingIgnoreCaseOrderByPrecioAsc(
                        precioMin, precioMax, existenciasMin, descripcionCategoria);
    }

    @Transactional(readOnly = true)
    public List<Producto> consultaAvanzadaJPQL(BigDecimal precioMin, BigDecimal precioMax,
                                               Integer existenciasMin, String descripcionCategoria) {
        return productoRepository.consultaAvanzadaJPQL(
                precioMin, precioMax, existenciasMin, descripcionCategoria);
    }

    @Transactional(readOnly = true)
    public List<Producto> consultaAvanzadaSQL(BigDecimal precioMin, BigDecimal precioMax,
                                              Integer existenciasMin, String descripcionCategoria) {
        return productoRepository.consultaAvanzadaSQL(
                precioMin, precioMax, existenciasMin, descripcionCategoria);
    }

}