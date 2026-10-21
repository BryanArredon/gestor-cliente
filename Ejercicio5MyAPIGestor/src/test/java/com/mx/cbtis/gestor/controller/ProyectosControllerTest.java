package com.mx.cbtis.gestor.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.mx.cbtis.gestor.modelo.Proyectos;
import com.mx.cbtis.gestor.repository.ProtectosRepository;

@WebMvcTest(ProyectosController.class)
class ProyectosControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private ProtectosRepository proyectostable;

	private Proyectos proyectoBase() {
		return new Proyectos(1, "API Gestor", "Proyecto de prueba", "2026-01-01", "2026-06-30", "En proceso", "blanco");
	}

	@Test
	void buscarProyectoExistente() throws Exception {
		when(proyectostable.findById(1)).thenReturn(Optional.of(proyectoBase()));

		mockMvc.perform(get("/buscarProyecto").param("id_proyecto", "1"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id_proyecto").value(1))
				.andExpect(jsonPath("$.nombre").value("API Gestor"))
				.andExpect(jsonPath("$.estado_proyecto").value("En proceso"));
	}

	@Test
	void buscarProyectoNoExistenteDevuelveObjetoVacio() throws Exception {
		when(proyectostable.findById(99)).thenReturn(Optional.empty());

		mockMvc.perform(get("/buscarProyecto").param("id_proyecto", "99"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id_proyecto").doesNotExist());
	}

	@Test
	void addProyectoGuardaYDevuelve1() throws Exception {
		when(proyectostable.save(any(Proyectos.class))).thenReturn(proyectoBase());

		mockMvc.perform(post("/addProyecto")
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"nombre\":\"API Gestor\",\"descripcion\":\"Proyecto de prueba\"}"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$").value(1));

		verify(proyectostable, times(1)).save(any(Proyectos.class));
	}

	@Test
	void updateProyectoGuardaYDevuelve1() throws Exception {
		when(proyectostable.save(any(Proyectos.class))).thenReturn(proyectoBase());

		mockMvc.perform(post("/updateProyecto")
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"id_proyecto\":1,\"nombre\":\"API Gestor v2\"}"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$").value(1));

		verify(proyectostable, times(1)).save(any(Proyectos.class));
	}

	@Test
	void deleteProyectoEliminaYDevuelve1() throws Exception {
		doNothing().when(proyectostable).delete(any(Proyectos.class));

		mockMvc.perform(delete("/deleteProyecto")
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"id_proyecto\":1}"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$").value(1));

		verify(proyectostable, times(1)).delete(any(Proyectos.class));
	}

	@Test
	void buscarProyectosDevuelveLista() throws Exception {
		List<Proyectos> proyectos = List.of(proyectoBase(),
				new Proyectos(2, "Otro Proyecto", "Segundo", "2026-02-01", "2026-08-30", "Iniciado", "azul"));
		when(proyectostable.findAll()).thenReturn(proyectos);

		mockMvc.perform(post("/buscarProyectos"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.length()").value(2))
				.andExpect(jsonPath("$[0].nombre").value("API Gestor"))
				.andExpect(jsonPath("$[1].nombre").value("Otro Proyecto"));
	}
}
