package org.mitocode.infraestructure.adapters.filter;

import io.opentelemetry.api.trace.Span;
import jakarta.annotation.Priority;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.ext.Provider;
import lombok.extern.slf4j.Slf4j;
import org.mitocode.infraestructure.adapters.filter.context.TransactionContext;

import java.io.IOException;

@Slf4j
@Provider
@Priority(Priorities.HEADER_DECORATOR)
public class IncomingRequestFilter implements ContainerRequestFilter, ContainerResponseFilter {

    private static final String TRACEPARENT_HEADER = "traceparent";

    private TransactionContext transactionContext;

    public IncomingRequestFilter(TransactionContext transactionContext) {
        this.transactionContext = transactionContext;
    }


    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        transactionContext.setRequestStartTimeNanos(System.nanoTime());
        String method = requestContext.getMethod();
        String path = requestContext.getUriInfo().getAbsolutePath().toString();

        transactionContext.setPath(path);
        transactionContext.setMethod(method);

        // Omitir la validación para el openapi/swagger
        if (path.startsWith("q/")) {
            return;
        }

        String traceparent = requestContext.getHeaderString(TRACEPARENT_HEADER);

        if (traceparent == null || traceparent.isBlank()){
            log.error("[ValidationError] - Header {} ausente en ruta: {}", TRACEPARENT_HEADER, path);

            throw new WebApplicationException("La cabecera es requerida:"+TRACEPARENT_HEADER);
        }

        Span currentSpan = Span.current();
        String traceId = currentSpan.getSpanContext().getTraceId();
        String spanId = currentSpan.getSpanContext().getSpanId();

        transactionContext.setTraceParent(traceparent);

        log.info("[START] - request init: traceparent:{}, traceId:{}, spanId:{}, method:{}, path:{}",
                traceparent, traceId, spanId, method, path);
    }


    @Override
    public void filter(ContainerRequestContext containerRequestContext, ContainerResponseContext containerResponseContext) throws IOException {

        int statusCode = containerResponseContext.getStatus();

        log.info("[END] - response: path:{}, method:{}, statusCode:{}, duration(ms):{}",
                transactionContext.getPath(), transactionContext.getMethod(), statusCode, durationInMs());
    }

    private long durationInMs() {

        return (System.nanoTime() - transactionContext.getRequestStartTimeNanos()) / 1_000_000;
    }
}
