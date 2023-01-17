package com.sorbonne.library_search_engine.config;

import com.sorbonne.library_search_engine.modele.BookList;
import com.sorbonne.library_search_engine.service.InitLibraryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * @version 1.0
 * @Author Hongyu YAN & Liuyi CHEN
 * @Date 2023/1/16
 */
@Configuration
public class WebConfiguration {
    @Bean
    public RestTemplate restTemplate(){
        return new RestTemplate();
    }

}

