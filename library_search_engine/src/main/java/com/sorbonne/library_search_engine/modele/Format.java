package com.sorbonne.library_search_engine.modele;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @version 1.0
 * @Author Hongyu YAN & Liuyi CHEN
 * @Date 2023/1
 */
@Data
public class Format implements Serializable {
    @JsonProperty(value = "text/html")
    private String textHTML;
    @JsonProperty(value = "image/jpeg")
    private String coverImage;
    @JsonProperty(value = "text/plain; charset=us-ascii")
    private String textASCII;
    @JsonProperty(value = "text/plain; charset=utf-8")
    private String textUTF8;
    @JsonProperty(value = "text")
    private String text;
}
