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

import com.mx.cbtis.gestor.modelo.Comentarios;
import com.mx.cbtis.gestor.repository.ComentariosRepository;

@WebMvcTest(ComentariosController.class)
class ComentariosControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private ComentariosRepository comentariostable;

	private Comentarios comentarioBase() {
		return new Comentarios(1, "1", 1, "Primer avance listo", "2026-03-01 10:00:00");
	}

	@Test
	void getComentarioPorTarea() throws Exception {
		when(comentariostable.findByTarea(1)).thenReturn(List.of(comentarioBase(),
				new Comentarios(2, "2", 1, "Revisado", "2026-03-02 11:00:00")));

		mockMvc.perform(post("/getComentario")
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"id_tarea\":1}"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.length()").value(2))
				.andExpect(jsonPath("$[0].texto_comentario").value("Primer avance listo"))
				.andExpect(jsonPath("$[1].texto_comentario").value("Revisado"));
	}

	@Test
	void buscarComentarioExistente() throws Exception {
		when(comentariostable.findById(1)).thenReturn(Optional.of(comentarioBase()));

		mockMvc.perform(get("/buscarComentario").param("id", "1"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id_comentario").value(1))
				.andExpect(jsonPath("$.texto_comentario").value("Primer avance listo"));
	}

	@Test
	void buscarComentarioNoExistenteDevuelveObjetoVacio() throws Exception {
		when(comentariostable.findById(99)).thenReturn(Optional.empty());

		mockMvc.perform(get("/buscarComentario").param("id", "99"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id_comentario").doesNotExist());
	}

	@Test
	void addComentarioGuardaYDevuelve1() throws Exception {
		when(comentariostable.save(any(Comentarios.class))).thenReturn(comentarioBase());

		mockMvc.perform(post("/addComentario")
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"id_tarea\":1,\"texto_comentario\":\"Primer avance listo\"}"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$").value(1));

		verify(comentariostable, times(1)).save(any(Comentarios.class));
	}

	@Test
	void updateComentarioGuardaYDevuelve1() throws Exception {
		when(comentariostable.save(any(Comentarios.class))).thenReturn(comentarioBase());

		mockMvc.perform(post("/updateComentario")
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"id_comentario\":1,\"texto_comentario\":\"Actualizado\"}"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$").value(1));

		verify(comentariostable, times(1)).save(any(Comentarios.class));
	}

	@Test
	void deleteComentarioEliminaYDevuelve1() throws Exception {
		doNothing().when(comentariostable).delete(any(Comentarios.class));

		mockMvc.perform(delete("/deleteComentario")
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"id_comentario\":1}"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$").value(1));

		verify(comentariostable, times(1)).delete(any(Comentarios.class));
	}

	@Test
	void buscarComentariosDevuelveLista() throws Exception {
		when(comentariostable.findAll()).thenReturn(List.of(comentarioBase()));

		mockMvc.perform(post("/buscarComentarios"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.length()").value(1))
				.andExpect(jsonPath("$[0].texto_comentario").value("Primer avance listo"));
	}
}
