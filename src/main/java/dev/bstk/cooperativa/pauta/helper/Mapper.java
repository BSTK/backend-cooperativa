package dev.bstk.cooperativa.pauta.helper;

import org.modelmapper.ModelMapper;

public class Mapper {

    private static final ModelMapper MODEL_MAPPER = new ModelMapper();

    private Mapper() { }

    public static <T> T to(final Object source, final Class<T> clazz) {
        return MODEL_MAPPER.map(source, clazz);
    }
}
