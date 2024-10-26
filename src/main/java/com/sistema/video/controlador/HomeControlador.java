package com.sistema.video.controlador;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import com.sistema.video.modelo.Video;
import com.sistema.video.repositorio.VideoRepositorio;

@Controller
@RequestMapping("")
public class HomeControlador {
	
	@Autowired
	private VideoRepositorio videoRepositorio;

	@GetMapping("")
	public ModelAndView verPaginaDeInicio() {
		List<Video> ultimosVideos = videoRepositorio.findAll(PageRequest.of(0, 4, Sort.by("fechaEstreno").descending())).toList();
		return new ModelAndView("index").addObject("ultimosVideos", ultimosVideos);
	}

	@GetMapping("/videos")
	public ModelAndView listarVideos(@PageableDefault(sort = "fechaEstreno", direction = Sort.Direction.DESC) Pageable pageable) {
		Page<Video> videos = videoRepositorio.findAll(pageable);
		return new ModelAndView("videos").addObject("videos", videos);
	}
	
	@GetMapping("/videos/{id}")
	public ModelAndView mostrarDetallesDeVideo(@PathVariable Integer id) {
		Video video = videoRepositorio.getOne(id);
		return new ModelAndView("video").addObject("video", video);
	}
}
