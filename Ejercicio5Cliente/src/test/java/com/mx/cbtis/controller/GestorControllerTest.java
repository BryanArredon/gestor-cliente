package com.mx.cbtis.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(GestorController.class)
class GestorControllerTest {

	@Autowired
	private MockMvc mockMvc;

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
	}
}
