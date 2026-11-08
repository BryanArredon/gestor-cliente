package com.mx.cbtis.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.RestTemplate;

import com.mx.cbtis.modelo.Comentarios;
import com.mx.cbtis.modelo.Proyectos;
import com.mx.cbtis.modelo.Tareas;
import com.mx.cbtis.modelo.Usuario;

@WebMvcTest(GestorController.class)
class GestorControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private GestorController gestorController;

	@MockitoBean
	private RestTemplate restTemplate;

	private Usuario crearUsuarioEjemplo() {
		return new Usuario(1, "Bryan", "bryan@test.com", "secret", "ADMIN", "avatar.png");
	}

	private Proyectos crearProyectoEjemplo() {
		return new Proyectos(1, "Proyecto CI", "Desc", "2026-01-01", "2026-12-31", "Activo", "verde");
	}

	private Tareas crearTareaEjemplo() {
		return new Tareas(10, "Tarea 1", "Desc", "2026-02-01", "Por hacer", "Pendiente", 1);
	}

	private Comentarios crearComentarioEjemplo() {
		return new Comentarios(1, "1", 10, "Comentario de prueba", "2026-02-01 10:00:00");
	}

	// ==================== GET ENDPOINTS ====================

	@Test
	void homeDevuelveVistaHome() throws Exception {
		mockMvc.perform(get("/"))
				.andExpect(status().isOk())
				.andExpect(view().name("home"));
	}

	@Test
	void tutorialDevuelveVistaTutorial() throws Exception {
		mockMvc.perform(get("/tutorial"))
				.andExpect(status().isOk())
				.andExpect(view().name("tutorial"));
	}

	@Test
	void manualDevuelveVistaManual() throws Exception {
		mockMvc.perform(get("/manual"))
				.andExpect(status().isOk())
				.andExpect(view().name("manual"));
	}

	@Test
	void loginDevuelveVistaLogin() throws Exception {
		mockMvc.perform(get("/login"))
				.andExpect(status().isOk())
				.andExpect(view().name("login"));
	}

	@Test
	void registerDevuelveVistaRegisterConAtributoUsuario() throws Exception {
		mockMvc.perform(get("/register"))
				.andExpect(status().isOk())
				.andExpect(view().name("register"))
				.andExpect(model().attributeExists("usuario"));
	}

	@Test
	void inicioDevuelveVistaInicioConAtributoProyectos() throws Exception {
		mockMvc.perform(get("/inicio"))
				.andExpect(status().isOk())
				.andExpect(view().name("inicio"))
				.andExpect(model().attributeExists("proyectos"));
	}

	@Test
	void tareaDevuelveVistaProyectoConAtributoTareas() throws Exception {
		mockMvc.perform(get("/tarea"))
				.andExpect(status().isOk())
				.andExpect(view().name("proyecto"))
				.andExpect(model().attributeExists("tareas"));
				.andExpect(model().attributeExists("tareas"))
				.andExpect(model().attributeExists("tarea"))
				.andExpect(model().attributeExists("comentarios"));
	}

	@Test
	void proyectoDevuelveVistaProyectoConUsuarios() throws Exception {
		Usuario[] mockUsuarios = new Usuario[] { crearUsuarioEjemplo() };
		when(restTemplate.postForObject(anyString(), any(), eq(Usuario[].class))).thenReturn(mockUsuarios);

		mockMvc.perform(get("/proyecto"))
				.andExpect(status().isOk())
				.andExpect(view().name("proyecto"))
				.andExpect(model().attributeExists("tarea"))
				.andExpect(model().attributeExists("comentarios"))
				.andExpect(model().attributeExists("listaUsuarios"));
	}

	// ==================== POST USUARIOS ====================

	@Test
	void saveUsuarioRedirigeALogin() throws Exception {
		when(restTemplate.postForEntity(anyString(), any(), eq(Integer.class)))
				.thenReturn(new ResponseEntity<>(1, HttpStatus.OK));

		mockMvc.perform(post("/saveUsuario")
						.flashAttr("usuario", crearUsuarioEjemplo()))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/login"));
	}

	@Test
	void verificarLoginDevuelveUsuario() throws Exception {
		Usuario mockUser = crearUsuarioEjemplo();
		when(restTemplate.postForEntity(anyString(), any(), eq(Usuario.class)))
				.thenReturn(new ResponseEntity<>(mockUser, HttpStatus.OK));

		mockMvc.perform(post("/verificarLogin")
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"correo_electronico\":\"bryan@test.com\",\"contrasena\":\"secret\"}"))
				.andExpect(status().isOk());
	}

	@Test
	void consultarUsuariosDevuelveLista() throws Exception {
		Usuario[] usuarios = new Usuario[] { crearUsuarioEjemplo() };
		when(restTemplate.postForObject(anyString(), any(), eq(Usuario[].class))).thenReturn(usuarios);

		mockMvc.perform(post("/consultarUsuarios"))
				.andExpect(status().isOk());
	}

	// ==================== POST PROYECTOS ====================

	@Test
	void saveProyectoRedirigeAInicio() throws Exception {
		when(restTemplate.postForEntity(anyString(), any(), eq(Integer.class)))
				.thenReturn(new ResponseEntity<>(1, HttpStatus.OK));

		mockMvc.perform(post("/saveProyecto")
						.flashAttr("proyecto", crearProyectoEjemplo()))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/inicio"));
	}

	@Test
	void consultarProyectosDevuelveLista() throws Exception {
		Proyectos[] mockProyectos = new Proyectos[] { crearProyectoEjemplo() };
		when(restTemplate.postForObject(anyString(), any(), eq(Proyectos[].class))).thenReturn(mockProyectos);

		mockMvc.perform(post("/consultarProyectos"))
				.andExpect(status().isOk());
	}

	@Test
	void eliminarProyectoRedirigeAInicio() throws Exception {
		when(restTemplate.exchange(anyString(), eq(HttpMethod.DELETE), any(HttpEntity.class), eq(Integer.class)))
				.thenReturn(new ResponseEntity<>(1, HttpStatus.OK));

		mockMvc.perform(post("/eliminarProyecto")
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"id_proyecto\":1}"))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/inicio"));
	}

	@Test
	void actualizarProyectoDevuelveDetalle() throws Exception {
		Proyectos mockP = crearProyectoEjemplo();
		when(restTemplate.getForObject(anyString(), eq(Proyectos.class), any(Map.class))).thenReturn(mockP);

		mockMvc.perform(post("/actualizarProyecto")
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"id_proyecto\":1}"))
				.andExpect(status().isOk());
	}

	// ==================== POST TAREAS ====================

	@Test
	void saveTareaRedirigeAProyecto() throws Exception {
		when(restTemplate.postForEntity(anyString(), any(), eq(Integer.class)))
				.thenReturn(new ResponseEntity<>(1, HttpStatus.OK));

		mockMvc.perform(post("/saveTarea")
						.flashAttr("tareas", crearTareaEjemplo()))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/proyecto?id_proyecto=1"));
	}

	@Test
	void saveComentarioEjecutaYDevuelveVista() {
		when(restTemplate.postForEntity(anyString(), any(), eq(Integer.class)))
				.thenReturn(new ResponseEntity<>(1, HttpStatus.OK));

		String vista = gestorController.setSaveComentario(crearComentarioEjemplo());
		assertEquals("proyecto", vista);
	}

	@Test
	void actualizarTareaDevuelveDetalle() throws Exception {
		Tareas mockT = crearTareaEjemplo();
		when(restTemplate.getForObject(anyString(), eq(Tareas.class), any(Map.class))).thenReturn(mockT);

		mockMvc.perform(post("/actualizarTarea")
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"id_tarea\":10}"))
				.andExpect(status().isOk());
	}

	@Test
	void eliminarTareaRedirigeAProyecto() throws Exception {
		when(restTemplate.exchange(anyString(), eq(HttpMethod.DELETE), any(HttpEntity.class), eq(Integer.class)))
				.thenReturn(new ResponseEntity<>(1, HttpStatus.OK));

		mockMvc.perform(post("/eliminarTarea")
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"id_tarea\":10}"))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/proyecto"));
	}

	@Test
	void verificarTareaDevuelveInteger() throws Exception {
		when(restTemplate.postForEntity(anyString(), any(), eq(Integer.class)))
				.thenReturn(new ResponseEntity<>(1, HttpStatus.OK));

		mockMvc.perform(post("/verificarTarea")
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"id_tarea\":10,\"nombre\":\"Verificada\"}"))
				.andExpect(status().isOk());
	}

	@Test
	void cambiosTareasDevuelveInteger() throws Exception {
		when(restTemplate.postForEntity(anyString(), any(), eq(Integer.class)))
				.thenReturn(new ResponseEntity<>(1, HttpStatus.OK));

		mockMvc.perform(post("/cambiosTareas")
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"id_tarea\":10,\"estado\":\"Terminada\"}"))
				.andExpect(status().isOk());
	}

	@Test
	void consultarTareasDevuelveLista() throws Exception {
		Tareas[] mockTareas = new Tareas[] { crearTareaEjemplo() };
		when(restTemplate.postForObject(anyString(), any(), eq(Tareas[].class))).thenReturn(mockTareas);

		mockMvc.perform(post("/consultarTareas"))
				.andExpect(status().isOk());
	}

	@Test
	void consultarComentariosDevuelveLista() throws Exception {
		Comentarios[] mockComentarios = new Comentarios[] { crearComentarioEjemplo() };
		when(restTemplate.postForObject(anyString(), any(), eq(Comentarios[].class))).thenReturn(mockComentarios);

		mockMvc.perform(post("/consultarComentarios"))
				.andExpect(status().isOk());
	}

	@Test
	void obtenerTareasDevuelveLista() throws Exception {
		Tareas[] mockTareas = new Tareas[] { crearTareaEjemplo() };
		when(restTemplate.postForObject(anyString(), any(), eq(Tareas[].class))).thenReturn(mockTareas);

		mockMvc.perform(post("/obtenerTareas")
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"id_proyecto\":1}"))
				.andExpect(status().isOk());
	}

	@Test
	void obtenerComentariosDevuelveLista() throws Exception {
		Comentarios[] mockComentarios = new Comentarios[] { crearComentarioEjemplo() };
		when(restTemplate.postForObject(anyString(), any(), eq(Comentarios[].class))).thenReturn(mockComentarios);

		mockMvc.perform(post("/obtenerComentarios")
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"id_comentario\":1}"))
				.andExpect(status().isOk());
	}
}
