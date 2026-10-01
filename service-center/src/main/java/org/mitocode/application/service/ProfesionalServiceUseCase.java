package org.mitocode.application.service;

import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.extern.slf4j.Slf4j;
import org.mitocode.application.command.CreateUpdateProfesionalCommand;
import org.mitocode.application.exceptions.BusinessException;
import org.mitocode.application.port.in.CrudProfesionalUseCase;
import org.mitocode.application.port.in.ListAllProfesionalByFechaUseCase;
import org.mitocode.application.port.in.ListAllProfesionalGroupByFechaUseCase;
import org.mitocode.application.port.out.profesional.*;
import org.mitocode.application.port.out.reserva.ListAllByProfesionalPort;
import org.mitocode.application.response.*;
import org.mitocode.domain.enums.ErrorType;
import org.mitocode.domain.model.Profesional;
import org.mitocode.domain.model.Reserva;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@ApplicationScoped
@Slf4j
public class ProfesionalServiceUseCase implements CrudProfesionalUseCase, ListAllProfesionalByFechaUseCase,
        ListAllProfesionalGroupByFechaUseCase {

    private final FindByIdProfesionalPort findByIdProfesionalPort;

    private final ListAllProfesionalPort listAllProfesionalPort;

    private final SaveProfesionalPort saveProfesionalPort;

    private final UpdateProfesionalByIdPort updateProfesionalByIdPort;

    private final ListAllByProfesionalPort listAllByProfesionalPort;

    private final DeleteByIdProfesionalPort deleteByIdProfesionalPort;

    public ProfesionalServiceUseCase(FindByIdProfesionalPort findByIdProfesionalPort, ListAllProfesionalPort listAllProfesionalPort, SaveProfesionalPort saveProfesionalPort, UpdateProfesionalByIdPort updateProfesionalByIdPort, ListAllByProfesionalPort listAllByProfesionalPort, DeleteByIdProfesionalPort deleteByIdProfesionalPort) {
        this.findByIdProfesionalPort = findByIdProfesionalPort;
        this.listAllProfesionalPort = listAllProfesionalPort;
        this.saveProfesionalPort = saveProfesionalPort;
        this.updateProfesionalByIdPort = updateProfesionalByIdPort;
        this.listAllByProfesionalPort = listAllByProfesionalPort;
        this.deleteByIdProfesionalPort = deleteByIdProfesionalPort;
    }

    @Override
    public Uni<ApiResponse<Profesional>> create(CreateUpdateProfesionalCommand command) {

        Profesional profesional = toDomain(command);
        return saveProfesionalPort.save(profesional)
                .map((p) -> {
                    ApiResponse<Profesional> response = new ApiResponse<>();
                    response.setData(new Profesional(p.getId()));
                    return response;
                });
    }

    @Override
    public Uni<ApiResponse<Profesional>> update(CreateUpdateProfesionalCommand command, String id) {
        Profesional profesional = toDomain(command);

        return this.findByIdProfesionalPort.findById(id)
                .onItem().ifNull().failWith(
                        () -> new BusinessException(
                                ErrorType.BUSINESS_NOT_FOUND_ERROR,
                                ErrorType.BUSINESS_NOT_FOUND_ERROR.getDescription())
                ).flatMap(p -> {
                    profesional.setId(p.getId());
                    return this.updateProfesionalByIdPort.update(profesional);
                }).map(p -> {
                    ApiResponse<Profesional> response = new ApiResponse<>();
                    response.setData(p);
                    return response;
                });
    }

    @Override
    public Uni<PageResponseDto<Profesional>> listAll(int page, int limit) {
        return listAllProfesionalPort.listAll(page, limit);
    }

    @Override
    public Uni<ApiResponse<Profesional>> findById(String id) {
        return this.findByIdProfesionalPort.findById(id)
                .onItem().ifNull().failWith(
                        ()-> new BusinessException(
                                ErrorType.BUSINESS_NOT_FOUND_ERROR,
                                ErrorType.BUSINESS_NOT_FOUND_ERROR.getDescription())
                )
                .map(p -> {
                    ApiResponse<Profesional> response = new ApiResponse<>();
                    response.setData(p);
                    return response;
                });
    }

    @Override
    public Uni<Void> deleteById(String id) {
        return this.findByIdProfesionalPort.findById(id)
                .onItem().ifNull().failWith(() ->
                        new BusinessException(
                                ErrorType.BUSINESS_NOT_FOUND_ERROR,
                                ErrorType.BUSINESS_NOT_FOUND_ERROR.getDescription()))
                .flatMap(cliente -> deleteByIdProfesionalPort.deleteById(cliente.getId().toString()));

    }

    @Override
    public Uni<PageResponseDto<ProfesionalReservaResponseDto>> listAllByFecha(
            int page,
            int limit
    ) {

        return listAllProfesionalPort
                .listAll(page, limit)
                .chain(pageProfesionales -> {

                    List<Profesional> profesionales =
                            pageProfesionales.data();

                    List<UUID> profesionalIds =
                            profesionales.stream()
                                    .map(Profesional::getId)
                                    .toList();

                    if (profesionalIds.isEmpty()) {
                        return Uni.createFrom().item(
                                new PageResponseDto<>(
                                        List.of(),
                                        pageProfesionales.currentPage(),
                                        pageProfesionales.limit(),
                                        pageProfesionales.totalElements(),
                                        pageProfesionales.totalPages()
                                )
                        );
                    }

                    return listAllByProfesionalPort
                            .listAllByProfesional(profesionalIds)
                            .map(reservas -> {

                                Map<UUID, List<Reserva>> reservasPorProfesional =
                                        reservas.stream()
                                                .collect(Collectors.groupingBy(
                                                        reserva -> reserva
                                                                .getProfesional()
                                                                .getId()
                                                ));

                                List<ProfesionalReservaResponseDto> resultado =
                                        profesionales.stream()
                                                .map(profesional -> {

                                                    List<ReservaResponseDto> reservasProfesional =
                                                            reservasPorProfesional.getOrDefault(
                                                                    profesional.getId(),
                                                                    List.of()
                                                            ).stream().map(l ->
                                                                    new ReservaResponseDto(l.getId(), l.getFecha(), l.getHoraInicio(), l.getHoraFin(), l.getCliente(), l.getEstado()))
                                                                    .toList();

                                                    return new ProfesionalReservaResponseDto(
                                                            profesional,
                                                            reservasProfesional
                                                    );
                                                })
                                                .sorted(
                                                        Comparator.comparing(
                                                                dto -> dto.reservas().size(),
                                                                Comparator.reverseOrder()
                                                        )
                                                )
                                                .toList();

                                return new PageResponseDto<>(
                                        resultado,
                                        pageProfesionales.currentPage(),
                                        pageProfesionales.limit(),
                                        pageProfesionales.totalElements(),
                                        pageProfesionales.totalPages()
                                );
                            });
                });
    }

    private Profesional toDomain(CreateUpdateProfesionalCommand command) {
        return new Profesional(command.nombres(), command.apellidos(), command.especialidad());
    }

    @Override
    public Uni<PageResponseDto<ProfesionalReservaGroupByFechaResponseDto>> listAllGroupByFecha(
            int page,
            int limit
    ) {

        return listAllProfesionalPort
                .listAll(page, limit)
                .chain(pageProfesionales -> {

                    List<Profesional> profesionales = pageProfesionales.data();

                    List<UUID> profesionalIds = profesionales.stream()
                            .map(Profesional::getId)
                            .toList();

                    if (profesionalIds.isEmpty()) {
                        return Uni.createFrom().item(
                                new PageResponseDto<>(
                                        List.of(),
                                        pageProfesionales.currentPage(),
                                        pageProfesionales.limit(),
                                        pageProfesionales.totalElements(),
                                        pageProfesionales.totalPages()
                                )
                        );
                    }

                    return listAllByProfesionalPort
                            .listAllByProfesional(profesionalIds)
                            .map(reservas -> {

                                Map<UUID, List<Reserva>> reservasPorProfesional =
                                        reservas.stream()
                                                .collect(Collectors.groupingBy(
                                                        reserva -> reserva
                                                                .getProfesional()
                                                                .getId()
                                                ));

                                List<ProfesionalReservaGroupByFechaResponseDto> resultado =
                                        profesionales.stream()
                                                .flatMap(profesional ->
                                                        reservasPorProfesional
                                                                .getOrDefault(
                                                                        profesional.getId(),
                                                                        List.of()
                                                                )
                                                                .stream()
                                                                .collect(Collectors.groupingBy(
                                                                        Reserva::getFecha
                                                                ))
                                                                .entrySet()
                                                                .stream()
                                                                .sorted(
                                                                        Map.Entry.comparingByKey()
                                                                )
                                                                .map(entry ->
                                                                        new ProfesionalReservaGroupByFechaResponseDto(
                                                                                entry.getKey(),
                                                                                entry.getValue()
                                                                                        .stream()
                                                                                        .sorted(
                                                                                                Comparator.comparing(
                                                                                                        Reserva::getHoraInicio
                                                                                                )
                                                                                        ).toList()
                                                                        )
                                                                )
                                                )
                                                .toList();

                                return new PageResponseDto<>(
                                        resultado,
                                        pageProfesionales.currentPage(),
                                        pageProfesionales.limit(),
                                        pageProfesionales.totalElements(),
                                        pageProfesionales.totalPages()
                                );
                            });
                });
    }
}
