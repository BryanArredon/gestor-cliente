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

import com.mx.cbtis.gestor.modelo.Asignaciones;
import com.mx.cbtis.gestor.repository.AsignacionesRepository;

@WebMvcTest(AsignacionesController.class)
class AsignacionesControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private AsignacionesRepository asignacionestable;

	private Asignaciones asignacionBase() {
		return new Asignaciones(1, 1, 1, "2026-03-01");
	}

	@Test
	void buscarAsignacionExistente() throws Exception {
		when(asignacionestable.findById(1)).thenReturn(Optional.of(asignacionBase()));

		mockMvc.perform(get("/buscarAsignacion").param("id", "1"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id_asignacion").value(1))
				.andExpect(jsonPath("$.id_usuario").value(1))
				.andExpect(jsonPath("$.id_tarea").value(1))
				.andExpect(jsonPath("$.fecha_asignacion").value("2026-03-01"));
	}

	@Test
	void buscarAsignacionNoExistenteDevuelveObjetoVacio() throws Exception {
		when(asignacionestable.findById(99)).thenReturn(Optional.empty());

		mockMvc.perform(get("/buscarAsignacion").param("id", "99"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id_asignacion").doesNotExist());
	}

	@Test
	void addAsignacionGuardaYDevuelve1() throws Exception {
		when(asignacionestable.save(any(Asignaciones.class))).thenReturn(asignacionBase());

		mockMvc.perform(post("/addAsignacion")
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"id_usuario\":1,\"id_tarea\":1,\"fecha_asignacion\":\"2026-03-01\"}"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$").value(1));

		verify(asignacionestable, times(1)).save(any(Asignaciones.class));
	}

	@Test
	void updateAsignacionGuardaYDevuelve1() throws Exception {
		when(asignacionestable.save(any(Asignaciones.class))).thenReturn(asignacionBase());

		mockMvc.perform(post("/updateAsignacion")
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"id_asignacion\":1,\"id_usuario\":2}"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$").value(1));

		verify(asignacionestable, times(1)).save(any(Asignaciones.class));
	}

	@Test
	void deleteAsignacionEliminaYDevuelve1() throws Exception {
		doNothing().when(asignacionestable).delete(any(Asignaciones.class));

		mockMvc.perform(delete("/deleteAsignacion")
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"id_asignacion\":1}"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$").value(1));

		verify(asignacionestable, times(1)).delete(any(Asignaciones.class));
	}

	@Test
	void buscarAsignacionesDevuelveLista() throws Exception {
		when(asignacionestable.findAll()).thenReturn(List.of(asignacionBase(),
				new Asignaciones(2, 2, 1, "2026-03-02")));

		mockMvc.perform(post("/buscarAsignaciones"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.length()").value(2))
				.andExpect(jsonPath("$[0].id_asignacion").value(1))
				.andExpect(jsonPath("$[1].id_asignacion").value(2));
	}
}
