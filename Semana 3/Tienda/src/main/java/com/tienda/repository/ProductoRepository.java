package com.tienda.repository;

import com.tienda.domain.Producto;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProductoRepository extends JpaRepository<Producto, Integer> {

    public List<Producto> findByActivoTrue();
    
    //Ejemplo de método utilizando consultas derivadas
    public List<Producto> findByPrecioBetweenOrderByPrecioAsc(double precioInf, double precioSup);

    //Ejemplo de método utilizando consultas JPQL
    @Query(value = "SELECT p FROM Producto p WHERE p.precio BETWEEN :precioInf AND :precioSup ORDER BY p.precio ASC")
    public List<Producto> consultaJPQL(@Param("precioInf") double precioInf, @Param("precioSup") double precioSup);
    
    //Ejemplo de método utilizando consultas SQL nativas
    @Query(nativeQuery = true,value = "SELECT * FROM producto p WHERE p.precio BETWEEN :precioInf AND :precioSup ORDER BY p.precio ASC")
    public List<Producto> consultaSQL(@Param("precioInf") double precioInf, @Param("precioSup") double precioSup);
    
    //PRACTICA 2

    // DERIVADA AVANZADA
    List<Producto> findByActivoTrueAndPrecioBetweenAndExistenciasGreaterThanAndCategoriaActivoTrueAndCategoriaDescripcionContainingIgnoreCaseOrderByPrecioAsc(
            BigDecimal precioMin,
            BigDecimal precioMax,
            Integer existenciasMin,
            String descripcionCategoria
    );


    // JPQL AVANZADA CON JOIN
    @Query("""
        SELECT p
        FROM Producto p
        JOIN p.categoria c
        WHERE p.activo = true
          AND p.precio BETWEEN :precioMin AND :precioMax
          AND p.existencias > :existenciasMin
          AND c.activo = true
          AND LOWER(c.descripcion) LIKE LOWER(CONCAT('%', :descripcionCategoria, '%'))
        ORDER BY p.precio ASC
    """)
    List<Producto> consultaAvanzadaJPQL(
            @Param("precioMin") BigDecimal precioMin,
            @Param("precioMax") BigDecimal precioMax,
            @Param("existenciasMin") Integer existenciasMin,
            @Param("descripcionCategoria") String descripcionCategoria
    );


    // SQL NATIVA AVANZADA CON JOIN
    @Query(value = """
        SELECT p.*
        FROM producto p
        INNER JOIN categoria c ON c.id_categoria = p.id_categoria
        WHERE p.activo = 1
          AND p.precio BETWEEN :precioMin AND :precioMax
          AND p.existencias > :existenciasMin
          AND c.activo = 1
          AND LOWER(c.descripcion) LIKE LOWER(CONCAT('%', :descripcionCategoria, '%'))
        ORDER BY p.precio ASC
    """, nativeQuery = true)
    List<Producto> consultaAvanzadaSQL(
            @Param("precioMin") BigDecimal precioMin,
            @Param("precioMax") BigDecimal precioMax,
            @Param("existenciasMin") Integer existenciasMin,
            @Param("descripcionCategoria") String descripcionCategoria
    );

}