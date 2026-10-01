package org.mitocode.application.response;

import java.util.List;

public record PageResponseDto<R>(
        List<R> data,
        int currentPage,
        int limit,
        long totalElements,
        int totalPages
){}