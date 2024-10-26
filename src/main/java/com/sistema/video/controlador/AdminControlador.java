package com.sistema.video.controlador;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.sistema.video.modelo.Genero;
import com.sistema.video.modelo.Video;
import com.sistema.video.repositorio.GeneroRepositorio;
import com.sistema.video.repositorio.VideoRepositorio;
import com.sistema.video.servicio.AlmacenServicioImpl;

@Controller
@RequestMapping("/admin")
public class AdminControlador {

    @Autowired
    private VideoRepositorio videoRepositorio;

    @Autowired
    private GeneroRepositorio generoRepositorio;

    @Autowired
    private AlmacenServicioImpl servicio;

    @GetMapping("")
    public ModelAndView verPaginaDeInicio() {
        List<Video> videos = videoRepositorio.findAll();
        return new ModelAndView("admin/index").addObject("videos", videos);
    }

    @GetMapping("/videos/nuevo")
    public ModelAndView mostrarFormularioDeNuevoVideo() {
        List<Genero> generos = generoRepositorio.findAll(Sort.by("titulo"));
        return new ModelAndView("admin/nuevo-video")
                .addObject("video", new Video())
                .addObject("generos", generos);
    }

    @PostMapping("/videos/nuevo")
    public ModelAndView registrarVideo(@Validated Video video, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors() || video.getPortada().isEmpty()) {
            if (video.getPortada().isEmpty()) {
                bindingResult.rejectValue("portada", "MultipartNotEmpty", "La portada no puede estar vacía.");
            }
            List<Genero> generos = generoRepositorio.findAll(Sort.by("titulo"));
            return new ModelAndView("admin/nuevo-video")
                    .addObject("video", video)
                    .addObject("generos", generos);
        }

        if (video.getSinopsis() != null && video.getSinopsis().length() > 1000) {
            bindingResult.rejectValue("sinopsis", "Size.video.sinopsis", "La sinopsis no puede exceder los 1000 caracteres.");
            List<Genero> generos = generoRepositorio.findAll(Sort.by("titulo"));
            return new ModelAndView("admin/nuevo-video")
                    .addObject("video", video)
                    .addObject("generos", generos);
        }

        String rutaPortada = servicio.almacenarArchivo(video.getPortada());
        video.setRutaPortada(rutaPortada);

        videoRepositorio.save(video);
        redirectAttributes.addFlashAttribute("mensaje", "Video registrado exitosamente.");
        return new ModelAndView("redirect:/admin");
        
    }
    
    //----------------------------------------------
    @GetMapping("/videos/{id}/editar")
	public ModelAndView mostrarFormilarioDeEditarVideo(@PathVariable Integer id) {
		Video video = videoRepositorio.getOne(id);
		List<Genero> generos = generoRepositorio.findAll(Sort.by("titulo"));
		
		return new ModelAndView("admin/editar-video")
				.addObject("video", video)
                .addObject("generos", generos);
	}
    
    @PostMapping("/videos/{id}/editar")
	public ModelAndView actualizarVideo(@PathVariable Integer id,@Validated Video video,BindingResult bindingResult) {
		if(bindingResult.hasErrors()) {
			List<Genero> generos = generoRepositorio.findAll(Sort.by("titulo"));
			return new ModelAndView("admin/editar-video")
					.addObject("video", video)
					.addObject("generos",generos);
		}
		
		Video videoDB = videoRepositorio.getOne(id);
		videoDB.setTitulo(video.getTitulo());
		videoDB.setSinopsis(video.getSinopsis());
		videoDB.setFechaEstreno(video.getFechaEstreno());
		videoDB.setYoutubeTrailerId(video.getYoutubeTrailerId());
		videoDB.setGeneros(video.getGeneros());
		
		if(!video.getPortada().isEmpty()) {
			servicio.eliminarArchivo(videoDB.getRutaPortada());
			String rutaPortada = servicio.almacenarArchivo(video.getPortada());
			videoDB.setRutaPortada(rutaPortada);
		}
		
		videoRepositorio.save(videoDB);
		return new ModelAndView("redirect:/admin");
	}
    
    //----------------------------------------------
    @PostMapping("/videos/{id}/eliminar")
	public String eliminarVideo(@PathVariable Integer id) {
		Video video = videoRepositorio.getOne(id);
		videoRepositorio.delete(video);
		servicio.eliminarArchivo(video.getRutaPortada());
		
		return "redirect:/admin";
	}
}

