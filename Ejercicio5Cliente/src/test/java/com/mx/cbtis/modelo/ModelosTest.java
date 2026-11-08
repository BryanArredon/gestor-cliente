package com.mx.cbtis.modelo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

class ModelosTest {

	@Test
	void testAsignaciones() {
		Asignaciones a = new Asignaciones();
		a.setId_asignacion(1);
		a.setId_usuario(2);
		a.setId_tarea(3);
		a.setFecha_asignacion("2026-03-01");

		assertEquals(1, a.getId_asignacion());
		assertEquals(2, a.getId_usuario());
		assertEquals(3, a.getId_tarea());
		assertEquals("2026-03-01", a.getFecha_asignacion());

		Asignaciones a2 = new Asignaciones(1, 2, 3, "2026-03-01");
		assertEquals("2026-03-01", a2.getFecha_asignacion());
	}

	@Test
	void testUsuario() {
		Usuario u = new Usuario();
		u.setId_usuario(1);
		u.setNombre("Bryan");
		u.setCorreo_electronico("bryan@test.com");
		u.setContrasena("secret");
		u.setRol("ADMIN");
		u.setImagen_usuario("img.png");

		assertEquals(1, u.getId_usuario());
		assertEquals("Bryan", u.getNombre());
		assertEquals("bryan@test.com", u.getCorreo_electronico());
		assertEquals("secret", u.getContrasena());
		assertEquals("ADMIN", u.getRol());
		assertEquals("img.png", u.getImagen_usuario());

		Usuario u2 = new Usuario(1, "Bryan", "bryan@test.com", "secret", "ADMIN", "img.png");
		assertEquals("Bryan", u2.getNombre());
	}

	@Test
	void testProyectos() {
		Proyectos p = new Proyectos();
		p.setId_proyecto(1);
		p.setNombre("P1");
		p.setDescripcion("Desc");
		p.setFecha_inicio("2026-01-01");
		p.setFecha_finalizacion("2026-12-31");
		p.setEstado_proyecto("Activo");
		p.setFondo("azul");

		assertEquals(1, p.getId_proyecto());
		assertEquals("P1", p.getNombre());
		assertEquals("Desc", p.getDescripcion());
		assertEquals("2026-01-01", p.getFecha_inicio());
		assertEquals("2026-12-31", p.getFecha_finalizacion());
		assertEquals("Activo", p.getEstado_proyecto());
		assertEquals("azul", p.getFondo());

		Proyectos p2 = new Proyectos(1, "P1", "Desc", "2026-01-01", "2026-12-31", "Activo", "azul");
		assertEquals("P1", p2.getNombre());
	}

	@Test
	void testTareas() {
		Tareas t = new Tareas();
		t.setId_tarea(1);
		t.setNombre("T1");
		t.setDescripcion("Desc");
		t.setFecha_vencimiento("2026-02-01");
		t.setLista("Pendiente");
		t.setEstado("En proceso");
		t.setId_proyecto(10);

		assertEquals(1, t.getId_tarea());
		assertEquals("T1", t.getNombre());
		assertEquals("Desc", t.getDescripcion());
		assertEquals("2026-02-01", t.getFecha_vencimiento());
		assertEquals("Pendiente", t.getLista());
		assertEquals("En proceso", t.getEstado());
		assertEquals(10, t.getId_proyecto());

		Tareas t2 = new Tareas(1, "T1", "Desc", "2026-02-01", "Pendiente", "En proceso", 10);
		assertEquals("T1", t2.getNombre());
	}

	@Test
	void testComentarios() {
		Comentarios c = new Comentarios();
		c.setId_comentario(1);
		c.setId_usuario("1");
		c.setId_tarea(10);
		c.setTexto_comentario("Texto");
		c.setFecha_hora("2026-01-01 12:00:00");

		assertEquals(1, c.getId_comentario());
		assertEquals("1", c.getId_usuario());
		assertEquals(10, c.getId_tarea());
		assertEquals("Texto", c.getTexto_comentario());
		assertEquals("2026-01-01 12:00:00", c.getFecha_hora());

		Comentarios c2 = new Comentarios(1, "1", 10, "Texto", "2026-01-01 12:00:00");
		assertEquals("Texto", c2.getTexto_comentario());
	}
}