package com.tienda.repository;

import com.tienda.domain.Categoria;
import com.tienda.dto.CategoriaConteo;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CategoriaRepository extends JpaRepository<Categoria, Integer> {

    public List<Categoria> findByActivoTrue();

    // 1) DERIVADA
    public List<Categoria> findByActivoTrueOrderByDescripcionAsc();
    List<Categoria> findByActivoTrueAndDescripcionContainingIgnoreCase(String texto);

    // 2) JPQL (GROUP BY + HAVING + textoDescripcion + productos activos)
    @Query("""
        SELECT c.idCategoria as idCategoria,
               c.descripcion as descripcion,
               c.rutaImagen as rutaImagen,
               COUNT(p) as cantidad
        FROM Categoria c
        JOIN c.productos p
        WHERE c.activo = true
          AND p.activo = true
          AND LOWER(c.descripcion) LIKE LOWER(CONCAT('%', :textoDescripcion, '%'))
        GROUP BY c.idCategoria, c.descripcion, c.rutaImagen
        HAVING COUNT(p) >= :minProductos
        ORDER BY COUNT(p) DESC
    """)
    public List<CategoriaConteo> conteoProductosJPQL(
            @Param("minProductos") long minProductos,
            @Param("textoDescripcion") String textoDescripcion
    );

    // 3) SQL NATIVA (GROUP BY + HAVING + textoDescripcion + productos activos)
    @Query(value = """
        SELECT c.id_categoria as idCategoria,
               c.descripcion as descripcion,
               c.ruta_imagen as rutaImagen,
               COUNT(p.id_producto) as cantidad
        FROM categoria c
        INNER JOIN producto p ON p.id_categoria = c.id_categoria
        WHERE c.activo = 1
          AND p.activo = 1
          AND LOWER(c.descripcion) LIKE LOWER(CONCAT('%', :textoDescripcion, '%'))
        GROUP BY c.id_categoria, c.descripcion, c.ruta_imagen
        HAVING COUNT(p.id_producto) >= :minProductos
        ORDER BY COUNT(p.id_producto) DESC
    """, nativeQuery = true)
    public List<CategoriaConteo> conteoProductosSQL(
            @Param("minProductos") long minProductos,
            @Param("textoDescripcion") String textoDescripcion
    );
}