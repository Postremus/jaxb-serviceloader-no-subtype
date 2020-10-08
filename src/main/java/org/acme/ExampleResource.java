package org.acme;

import io.smallrye.mutiny.Uni;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.bind.JAXB;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Path("/hello")
public class ExampleResource {

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public Uni<Response> hello()  {
        return Uni.createFrom().completionStage(CompletableFuture.supplyAsync(() -> {
            try {
                MailDataSource dataSource = new MailDataSource();
                dataSource.setName(UUID.randomUUID().toString());

                try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
                    JAXBContext jaxbContext = JAXBContext.newInstance(MailDataSource.class);
                    Marshaller marshaller = jaxbContext.createMarshaller();
                    marshaller.marshal(dataSource, outputStream);

                    byte[] result;
                    result = outputStream.toByteArray();
                    String xml =  new String(result, StandardCharsets.UTF_8);
                    return Response.ok(xml).build();
                }
            } catch (Exception e) {
                e.printStackTrace();
                return Response.status(400).entity(e.getMessage()).build();
            }
        }));
    }
}
