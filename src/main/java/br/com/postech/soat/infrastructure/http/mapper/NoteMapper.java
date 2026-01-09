package br.com.postech.soat.infrastructure.http.mapper;

import br.com.postech.soat.domain.valueobject.Observation;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface NoteMapper {
    NoteMapper INSTANCE = Mappers.getMapper(NoteMapper.class);

    default List<Observation> mapFrom(List<String> note) {
        return note.stream()
                .map(Observation::new)
                .toList();
    }
}
