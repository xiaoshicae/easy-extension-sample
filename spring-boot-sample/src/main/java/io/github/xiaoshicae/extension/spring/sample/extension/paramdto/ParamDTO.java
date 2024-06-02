package io.github.xiaoshicae.extension.spring.sample.extension.paramdto;


import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ParamDTO {
    private final String category;
    private final String secondCategory;
    private final String extra;
}
