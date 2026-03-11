package com.tienda.service;

import com.tienda.domain.Categoria;
import com.tienda.dto.CategoriaConteo;
import com.tienda.repository.CategoriaRepository;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
public class CategoriaService {
    
    @Autowired
    private CategoriaRepository categoriaRepository;
    
    @Autowired
    private FirebaseStorageService firebaseStorageService;
    
    @Transactional(readOnly = true)
    public List<Categoria> getCategorias(boolean activo) {
        if (activo) {
            return categoriaRepository.findByActivoTrue();
        }
        return categoriaRepository.findAll();
    }
    
    @Transactional(readOnly = true)
    public Optional<Categoria> getCategoria(Integer idCategoria) {
        return categoriaRepository.findById(idCategoria);
    }
    
    @Transactional
    public void save(Categoria categoria, MultipartFile imagenFile) {
        categoria = categoriaRepository.save(categoria);
        if (imagenFile.isEmpty()) {
            return;
        }
        try {
            String rutaImagen = firebaseStorageService.uploadImage(
                    imagenFile,
                    "categoria",
                    categoria.getIdCategoria());
            categoria.setRutaImagen(rutaImagen);
            categoriaRepository.save(categoria);
        } catch (IOException e) {
        }
    }
    
    @Transactional
    public void delete(Integer idCategoria) {
        if (!categoriaRepository.existsById(idCategoria)) {
            throw new IllegalArgumentException("La categoria con ID " + idCategoria + " no existe");
        }
        try {
            categoriaRepository.deleteById(idCategoria);
        } catch (DataIntegrityViolationException e) {
            throw new IllegalStateException("No se puede eliminar la categoria, tiene datos asociados", e);
        }
    }
    
    // DERIVADA
    @Transactional(readOnly = true)
    public List<CategoriaConteo> buscarPorDescripcion(String texto, long minProductos) {
        return categoriaRepository.findByActivoTrueAndDescripcionContainingIgnoreCase(texto)
                .stream()
                .map(c -> new CategoriaConteo() {
                    @Override
                    public Integer getIdCategoria() {
                        return c.getIdCategoria();
                    }

                    @Override
                    public String getDescripcion() {
                        return c.getDescripcion();
                    }

                    @Override
                    public String getRutaImagen() {
                        return c.getRutaImagen();
                    }

                    @Override
                    public Long getCantidad() {
                        if (c.getProductos() == null) {
                            return 0L;
                        }
                        return c.getProductos().stream()
                                .filter(p -> p.isActivo())
                                .count();
                    }
                })
                .filter(c -> c.getCantidad() >= minProductos)
                .sorted((a, b) -> Long.compare(b.getCantidad(), a.getCantidad()))
                .collect(Collectors.toList());
    }

    // JPQL
    @Transactional(readOnly = true)
    public List<CategoriaConteo> conteoProductosJPQL(long minProductos, String textoDescripcion) {
        return categoriaRepository.conteoProductosJPQL(minProductos, textoDescripcion);
    }

    // SQL NATIVA
    @Transactional(readOnly = true)
    public List<CategoriaConteo> conteoProductosSQL(long minProductos, String textoDescripcion) {
        return categoriaRepository.conteoProductosSQL(minProductos, textoDescripcion);
    }
}