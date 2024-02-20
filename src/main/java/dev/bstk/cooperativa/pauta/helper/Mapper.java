package dev.bstk.cooperativa.pauta.helper;

import org.modelmapper.ModelMapper;
import java.util.List;
import java.util.stream.Collectors;

public class Mapper {

    private static final ModelMapper MODEL_MAPPER = new ModelMapper();

    private Mapper() { }

    public static <T> T to(final Object source, final Class<T> clazz) {
        return MODEL_MAPPER.map(source, clazz);
    }

    public static <S, T> List<T> list(final List<S> source, final Class<T> clazz) {
        return source
                .stream()
                .map(element -> MODEL_MAPPER.map(element, clazz))
                .collect(Collectors.toList());
    }
}
