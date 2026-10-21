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

import com.mx.cbtis.gestor.modelo.Usuario;
import com.mx.cbtis.gestor.repository.UsuarioRepository;

@WebMvcTest(UsuarioController.class)
class UsuarioControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private UsuarioRepository usuariotable;

	@Test
	void buscarUsuarioExistente() throws Exception {
		Usuario usuario = new Usuario(1, "Juan", "juan@correo.com", "1234", "admin", "img.png");
		when(usuariotable.findById(1)).thenReturn(Optional.of(usuario));

		mockMvc.perform(get("/buscarUsuario").param("id", "1"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id_usuario").value(1))
				.andExpect(jsonPath("$.nombre").value("Juan"))
				.andExpect(jsonPath("$.correo_electronico").value("juan@correo.com"))
				.andExpect(jsonPath("$.rol").value("admin"));
	}

	@Test
	void buscarUsuarioNoExistenteDevuelveObjetoVacio() throws Exception {
		when(usuariotable.findById(99)).thenReturn(Optional.empty());

		mockMvc.perform(get("/buscarUsuario").param("id", "99"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id_usuario").doesNotExist())
				.andExpect(jsonPath("$.nombre").doesNotExist());
	}

	@Test
	void loginCorrectoDevuelveUsuario() throws Exception {
		Usuario usuario = new Usuario(1, "Juan", "juan@correo.com", "1234", "admin", "img.png");
		when(usuariotable.findByCorreoPass("juan@correo.com", "1234")).thenReturn(Optional.of(usuario));

		mockMvc.perform(post("/getUsuarioLogin")
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"correo_electronico\":\"juan@correo.com\",\"contrasena\":\"1234\"}"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id_usuario").value(1))
				.andExpect(jsonPath("$.nombre").value("Juan"));
	}

	@Test
	void loginIncorrectoDevuelveMarca000() throws Exception {
		when(usuariotable.findByCorreoPass("juan@correo.com", "mala")).thenReturn(Optional.empty());

		mockMvc.perform(post("/getUsuarioLogin")
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"correo_electronico\":\"juan@correo.com\",\"contrasena\":\"mala\"}"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.nombre").value("000"));
	}

	@Test
	void addUsuarioGuardaYDevuelve1() throws Exception {
		when(usuariotable.save(any(Usuario.class)))
				.thenReturn(new Usuario(1, "Juan", "juan@correo.com", "1234", "admin", "img.png"));

		mockMvc.perform(post("/addUsuario")
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"nombre\":\"Juan\",\"correo_electronico\":\"juan@correo.com\","
								+ "\"contrasena\":\"1234\",\"rol\":\"admin\",\"imagen_usuario\":\"img.png\"}"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$").value(1));

		verify(usuariotable, times(1)).save(any(Usuario.class));
	}

	@Test
	void updateUsuarioGuardaYDevuelve1() throws Exception {
		when(usuariotable.save(any(Usuario.class)))
				.thenReturn(new Usuario(1, "Juan Editado", "juan@correo.com", "1234", "user", "img.png"));

		mockMvc.perform(post("/updateUsuario")
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"id_usuario\":1,\"nombre\":\"Juan Editado\"}"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$").value(1));

		verify(usuariotable, times(1)).save(any(Usuario.class));
	}

	@Test
	void deleteUsuarioEliminaYDevuelve1() throws Exception {
		doNothing().when(usuariotable).delete(any(Usuario.class));

		mockMvc.perform(delete("/deleteUsuario")
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"id_usuario\":1}"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$").value(1));

		verify(usuariotable, times(1)).delete(any(Usuario.class));
	}

	@Test
	void buscarUsuariosDevuelveLista() throws Exception {
		List<Usuario> usuarios = List.of(
				new Usuario(1, "Juan", "juan@correo.com", "1234", "admin", "img.png"),
				new Usuario(2, "Maria", "maria@correo.com", "5678", "user", "img2.png"));
		when(usuariotable.findAll()).thenReturn(usuarios);

		mockMvc.perform(post("/buscarUsuarios"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.length()").value(2))
				.andExpect(jsonPath("$[0].nombre").value("Juan"))
				.andExpect(jsonPath("$[1].nombre").value("Maria"));
	}
}
