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

import com.mx.cbtis.gestor.modelo.Tareas;
import com.mx.cbtis.gestor.repository.TareasRepository;

@WebMvcTest(TareasController.class)
class TareasControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private TareasRepository tareastable;

	private Tareas tareaBase() {
		return new Tareas(1, "Diseñar API", "Diseño del endpoints", "2026-03-01", "Pendientes", "En proceso", 1);
	}

	@Test
	void getTareaPorProyecto() throws Exception {
		List<Tareas> tareas = List.of(tareaBase(),
				new Tareas(2, "Implementar", "Codigo", "2026-04-01", "Pendientes", "Iniciado", 1));
		when(tareastable.findByIdProyecto(1)).thenReturn(tareas);

		mockMvc.perform(post("/getTarea")
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"id_proyecto\":1}"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.length()").value(2))
				.andExpect(jsonPath("$[0].nombre").value("Diseñar API"))
				.andExpect(jsonPath("$[1].nombre").value("Implementar"));
	}

	@Test
	void buscarTareaExistente() throws Exception {
		when(tareastable.findById(1)).thenReturn(Optional.of(tareaBase()));

		mockMvc.perform(get("/buscarTarea").param("id_tarea", "1"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id_tarea").value(1))
				.andExpect(jsonPath("$.nombre").value("Diseñar API"))
				.andExpect(jsonPath("$.estado").value("En proceso"));
	}

	@Test
	void buscarTareaNoExistenteDevuelveObjetoVacio() throws Exception {
		when(tareastable.findById(99)).thenReturn(Optional.empty());

		mockMvc.perform(get("/buscarTarea").param("id_tarea", "99"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id_tarea").doesNotExist());
	}

	@Test
	void addTareasGuardaYDevuelve1() throws Exception {
		when(tareastable.save(any(Tareas.class))).thenReturn(tareaBase());

		mockMvc.perform(post("/addTareas")
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"nombre\":\"Diseñar API\",\"id_proyecto\":1}"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$").value(1));

		verify(tareastable, times(1)).save(any(Tareas.class));
	}

	@Test
	void updateTareasGuardaYDevuelve1() throws Exception {
		when(tareastable.save(any(Tareas.class))).thenReturn(tareaBase());

		mockMvc.perform(post("/updateTareas")
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"id_tarea\":1,\"estado\":\"Terminado\"}"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$").value(1));

		verify(tareastable, times(1)).save(any(Tareas.class));
	}

	@Test
	void deleteTareasEliminaYDevuelve1() throws Exception {
		doNothing().when(tareastable).delete(any(Tareas.class));

		mockMvc.perform(delete("/deleteTareas")
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"id_tarea\":1}"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$").value(1));

		verify(tareastable, times(1)).delete(any(Tareas.class));
	}

	@Test
	void buscarTareasDevuelveLista() throws Exception {
		when(tareastable.findAll()).thenReturn(List.of(tareaBase()));

		mockMvc.perform(post("/buscarTareas"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.length()").value(1))
				.andExpect(jsonPath("$[0].nombre").value("Diseñar API"));
	}
}
