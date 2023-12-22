package com.project.request;

import com.project.domain.Center;
import lombok.*;

import jakarta.persistence.*;
import java.io.File;


@Getter
@NoArgsConstructor
public class FileDto {

    private Long id;
    private String originFileName;
    private String newFileName;
    private String filePath;
    private Center center;

    @Builder
    public FileDto(Long id, String originFileName, String newFileName, String filePath,Center center ) {
        this.id = id;
        this.originFileName = originFileName;
        this.newFileName = newFileName;
        this.filePath = filePath;
        this.center = center;
    }


}

