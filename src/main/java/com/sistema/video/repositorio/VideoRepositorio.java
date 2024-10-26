package com.sistema.video.repositorio;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sistema.video.modelo.Video;

public interface VideoRepositorio extends JpaRepository<Video, Integer>{

}
